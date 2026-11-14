#!/usr/bin/env python3
"""Observe normal health API token renewal; does not maintain browser cookies."""
import argparse
import datetime as dt
import fcntl
import json
from pathlib import Path
import signal
import threading
import time

import health as h


def iso(seconds):
    return dt.datetime.fromtimestamp(seconds, h.ZONE).isoformat()


def run(session, state_path, hours=24, margin=120, stop=None):
    stop = stop or threading.Event()
    state_path = Path(state_path)
    state_path.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    state_path.parent.chmod(0o700)
    lock_path = Path(session).with_name(Path(session).name + '.keepalive.lock')
    with lock_path.open('a') as lock:
        lock_path.chmod(0o600)
        try:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            raise h.HealthError('A keepalive observer already owns this state path') from None
        start = time.time()
        deadline = start + hours * 3600 if hours else None
        state = dict(credentialKind='health-api-tokens', browserCookiesMaintained=False,
                     startedAt=iso(start), plannedStopAt=iso(deadline) if deadline else None,
                     refreshCount=0, refreshTokenExpiryConfirmed=False)

        def publish(phase, **details):
            state.update(phase=phase, checkedAt=iso(time.time()), **details)
            h.private_json(state_path, state)
            print(json.dumps(state, ensure_ascii=False), flush=True)

        try:
            client = h.Client(session)
            # Normal account/region verification, no health data reads or login.
            before = client.session.copy()
            client.ready()
            if client.session['accessTokenExpireTime'] != before['accessTokenExpireTime']:
                expires = int(client.session['accessTokenExpireTime']) / 1000
                if expires <= time.time() + margin:
                    raise h.HealthError('Initial refresh returned an expiry inside the renewal margin; stopped')
                state['refreshCount'] += 1
                publish('refreshed', accessExpiresAt=iso(expires), nextRefreshAt=iso(expires - margin),
                        accessTokenRotated=client.session['accessToken'] != before['accessToken'],
                        refreshTokenRotated=client.session['refreshToken'] != before['refreshToken'])
            last_expiration = None
            while not stop.is_set():
                now = time.time()
                if deadline is not None and now >= deadline:
                    publish('completed', reason='observation_duration_reached')
                    return 0
                # Reload the selected cache so other authorized exports can renew it.
                client = h.Client(session)
                expires = int(client.session['accessTokenExpireTime']) / 1000
                due = expires - margin
                if now >= due:
                    before = client.session.copy()
                    client.refresh()
                    new_expires = int(client.session['accessTokenExpireTime']) / 1000
                    if new_expires <= time.time() + margin or new_expires <= expires:
                        raise h.HealthError('Refresh did not extend expiry beyond the renewal margin; stopped')
                    client.ready()
                    state['refreshCount'] += 1
                    publish('refreshed', accessExpiresAt=iso(new_expires), nextRefreshAt=iso(new_expires - margin),
                            accessTokenRotated=client.session['accessToken'] != before['accessToken'],
                            refreshTokenRotated=client.session['refreshToken'] != before['refreshToken'],
                            accessRemainingMinutes=round((new_expires - time.time()) / 60, 2))
                    last_expiration = new_expires
                    continue
                if expires != last_expiration:
                    publish('waiting', accessExpiresAt=iso(expires), nextRefreshAt=iso(due),
                            accessRemainingMinutes=round((expires - now) / 60, 2))
                    last_expiration = expires
                wait = min(60, due - now, deadline - now if deadline is not None else 60)
                stop.wait(max(.1, wait))
            publish('stopped', reason='user_stopped')
            return 0
        except (h.HealthError, OSError, ValueError, KeyError, TypeError) as error:
            message = str(error) if isinstance(error, h.HealthError) else 'Invalid cache, input or network response; details omitted'
            publish('failed', error=message, stoppedWithoutRelogin=True)
            return 1


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--session', type=Path, required=True)
    parser.add_argument('--state', type=Path, required=True, help='Private JSON containing expiry and outcome only')
    parser.add_argument('--hours', type=float, default=24, help='Observation duration; 0 means until explicitly stopped')
    parser.add_argument('--margin', type=int, default=120, help='Refresh this many seconds before expiry')
    args = parser.parse_args()
    if not 0 <= args.hours < float('inf') or args.margin < 30:
        parser.error('--hours must be finite and nonnegative; --margin must be at least 30 seconds')
    stop = threading.Event()
    for event in (signal.SIGINT, signal.SIGTERM):
        signal.signal(event, lambda *_: stop.set())
    return run(args.session, args.state, args.hours, args.margin, stop)


if __name__ == '__main__':
    try:
        raise SystemExit(main())
    except (h.HealthError, OSError, ValueError):
        print(json.dumps({'phase':'failed','error':'Unable to start private keepalive observer'}))
        raise SystemExit(1)
