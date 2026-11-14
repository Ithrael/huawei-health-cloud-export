import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'scripts'))
import health as h
import keepalive as k


class KeepaliveTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.state = Path(self.tmp.name) / 'private/state.json'
        self.session = self.state.parent / 'session.json'
        self.now = [1800000000.0]
        self.clock = patch('keepalive.time.time', side_effect=lambda: self.now[0])
        self.clock.start()
        self.network = patch('socket.socket', side_effect=AssertionError('Offline only'))
        self.network.start()

    def tearDown(self):
        self.network.stop()
        self.clock.stop()
        self.tmp.cleanup()

    def fake_client(self, failure=None, lifetime=3600):
        session = {'accessToken': 'PRIVATE_ACCESS', 'refreshToken': 'PRIVATE_REFRESH',
                   'accessTokenExpireTime': int((self.now[0] + 3600) * 1000)}
        calls = []
        clock = self.now

        class Client:
            def __init__(self, path):
                self.session = session.copy()

            def ready(self):
                calls.append(('ready', clock[0]))

            def refresh(self):
                calls.append(('refresh', clock[0]))
                if failure:
                    raise h.HealthError(failure)
                session['accessTokenExpireTime'] = int((clock[0] + lifetime) * 1000)
                self.session = session.copy()
        return Client, calls

    def stop_event(self):
        clock = self.now

        class Event:
            def is_set(self):
                return False

            def wait(self, seconds):
                clock[0] += seconds
        return Event()

    def test_refreshes_near_expiry_without_hot_loop_and_finishes(self):
        client, calls = self.fake_client()
        with patch('keepalive.h.Client', client), patch('builtins.print'):
            self.assertEqual(k.run(self.session, self.state, hours=2, stop=self.stop_event()), 0)
        refreshes = [stamp for kind, stamp in calls if kind == 'refresh']
        self.assertEqual(len(refreshes), 2)
        self.assertEqual(refreshes[1] - refreshes[0], 3480)
        state = json.loads(self.state.read_text())
        self.assertEqual(state['phase'], 'completed')
        self.assertEqual(state['refreshCount'], 2)
        self.assertFalse(state['browserCookiesMaintained'])
        self.assertNotIn('PRIVATE_', self.state.read_text())
        self.assertEqual(self.state.stat().st_mode & 0o777, 0o600)
        self.assertEqual(self.state.parent.stat().st_mode & 0o777, 0o700)

    def test_http_429_stops_immediately_without_relogin_or_retry(self):
        client, calls = self.fake_client(failure='HTTP 429 at /commonAbility/userAccessToken/refresh')
        with patch('keepalive.h.Client', client), patch('builtins.print'):
            self.assertEqual(k.run(self.session, self.state, hours=24, stop=self.stop_event()), 1)
        self.assertEqual(len([c for c in calls if c[0] == 'refresh']), 1)
        state = json.loads(self.state.read_text())
        self.assertEqual(state['phase'], 'failed')
        self.assertTrue(state['stoppedWithoutRelogin'])

    def test_short_expiry_cannot_create_a_refresh_storm(self):
        client, calls = self.fake_client(lifetime=30)
        with patch('keepalive.h.Client', client), patch('builtins.print'):
            self.assertEqual(k.run(self.session, self.state, hours=24, stop=self.stop_event()), 1)
        self.assertEqual(len([c for c in calls if c[0] == 'refresh']), 1)
        self.assertEqual(json.loads(self.state.read_text())['phase'], 'failed')

    def test_only_one_observer_can_use_the_selected_session(self):
        self.state.parent.mkdir()
        lock = self.session.with_name(self.session.name + '.keepalive.lock')
        with lock.open('a') as stream:
            k.fcntl.flock(stream, k.fcntl.LOCK_EX | k.fcntl.LOCK_NB)
            with self.assertRaises(h.HealthError):
                k.run(self.session, self.state, hours=1, stop=self.stop_event())


if __name__ == '__main__':
    unittest.main()
