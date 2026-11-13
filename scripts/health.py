#!/usr/bin/env python3
"""Private Huawei Health China session transport and export entry point."""
import argparse
import datetime as dt
import getpass
import json
import os
from pathlib import Path
import sys
import tempfile
import time
import urllib.error
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
AUTH = 'https://healthsession.things.dbankcloud.cn'
DATA = 'https://healthdata.dbankcloud.cn'
APP = '10414141'
VERSION = 'and_health_17.0.10.300'
ZONE = dt.timezone(dt.timedelta(hours=8))
READ_PATHS = {entry['path'] for entry in json.loads((ROOT/'references/api-catalog.json').read_text())['interfaces']
              if entry['runtimeStatus'] in {'returned_data', 'success_empty', 'success_metadata'}}


class HealthError(Exception):
    pass


def private_json(path, value):
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    path.parent.chmod(0o700)
    temporary = None
    try:
        with tempfile.NamedTemporaryFile(mode='w', dir=path.parent, delete=False) as stream:
            temporary = Path(stream.name)
            json.dump(value, stream, ensure_ascii=False, indent=2)
            stream.write('\n')
        os.replace(temporary, path)
    finally:
        if temporary and temporary.exists():
            temporary.unlink()


def import_session(path, value):
    required = ('uid', 'accessToken', 'refreshToken', 'accessTokenExpireTime')
    if not isinstance(value, dict) or not all(value.get(k) for k in required):
        raise HealthError('Missing successful-session fields')
    int(value['accessTokenExpireTime'])
    if path.exists() and str(json.loads(path.read_text()).get('uid')) != str(value['uid']):
        raise HealthError('Cache belongs to another account; use a new session path')
    allowed = (*required, 'timeStamp', 'siteId', 'nationalCode', 'srvNationalCode')
    saved = {k:value[k] for k in allowed if value.get(k) is not None}
    saved.update(appId=APP, appVersion=VERSION)
    private_json(path, saved)


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


class Client:
    def __init__(self, path):
        self.path = Path(path)
        self.session = json.loads(self.path.read_text())

    def post(self, host, path, body):
        if host == AUTH:
            allowed = path in {'/commonAbility/userAccessToken/refresh', '/commonAbility/userAccessToken/query'}
        else:
            allowed = host == DATA and path in READ_PATHS
        if not allowed:
            raise HealthError('Not a verified read/session endpoint')
        headers = {'Content-Type':'application/json', 'x-huid':str(self.session['uid']),
                   'x-version':VERSION, 'x-ts':str(int(time.time()*1000))}
        request = urllib.request.Request(host+path, data=json.dumps(body).encode(), headers=headers, method='POST')
        try:
            with urllib.request.build_opener(NoRedirect).open(request, timeout=25) as response:
                value = json.load(response)
        except urllib.error.HTTPError as error:
            raise HealthError(f'HTTP {error.code} at {path}') from None
        except (urllib.error.URLError, TimeoutError, json.JSONDecodeError):
            raise HealthError(f'Network or JSON failure at {path}') from None
        if not isinstance(value, dict) or value.get('resultCode') != 0:
            code = value.get('resultCode', 'missing') if isinstance(value, dict) else 'missing'
            if not isinstance(code, (int,float)):
                code = 'unrecognized'
            raise HealthError(f'Huawei resultCode={code} at {path}')
        return value

    def refresh(self):
        value = self.post(AUTH, '/commonAbility/userAccessToken/refresh',
                          {'refreshToken':self.session['refreshToken'], 'appId':APP})
        if not value.get('accessToken') or not value.get('accessTokenExpireTime'):
            raise HealthError('Refresh returned an incomplete session; cache unchanged')
        if value.get('uid') and str(value['uid']) != str(self.session['uid']):
            raise HealthError('Refreshed account does not match the selected cache')
        int(value['accessTokenExpireTime'])
        for key in ('accessToken','refreshToken','accessTokenExpireTime','timeStamp','siteId'):
            if value.get(key) is not None:
                self.session[key] = value[key]
        self.session['appVersion'] = VERSION
        private_json(self.path, self.session)

    def ready(self):
        if int(self.session['accessTokenExpireTime']) <= int(time.time()*1000)+120000:
            self.refresh()
        context = self.post(AUTH, '/commonAbility/userAccessToken/query',
                            {'accessToken':self.session['accessToken'], 'appId':APP})
        if str(context.get('uid')) != str(self.session['uid']):
            raise HealthError('Account context does not match the selected cache')
        if context.get('siteId') != 1 or context.get('nationalCode') != 'CN':
            raise HealthError('Only the source-verified China-region workflow is supported')
        for key in ('siteId','nationalCode','srvNationalCode'):
            if context.get(key) is not None:
                self.session[key] = context[key]
        private_json(self.path, self.session)

    def query(self, path, params):
        if path not in READ_PATHS:
            raise HealthError('Not a verified data-read endpoint')
        if int(self.session['accessTokenExpireTime']) <= int(time.time()*1000)+120000:
            self.refresh()
        body = {'deviceId':'clientnull','deviceType':'-1','language':'zh','isManually':1,
                'siteId':str(self.session['siteId']), **params}
        body.update(token=self.session['accessToken'], tokenType=2, ts=int(time.time()*1000),
                    source=1, appId='com.huawei.health')
        return self.post(DATA, path, body)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('command', choices=['import-session','status','refresh','export'])
    parser.add_argument('--session', type=Path, default=Path('.secrets/huawei-health/session.json'))
    parser.add_argument('--days', type=int, default=30)
    parser.add_argument('--types', help='Comma-separated cloud/local dictionary type IDs; default: all supported types')
    parser.add_argument('--max-pages', type=int, default=100)
    parser.add_argument('--output', type=Path, default=Path('exports')/dt.datetime.now().strftime('%Y%m%d-%H%M%S'))
    args = parser.parse_args()
    if args.days<1 or args.max_pages<1:
        parser.error('--days and --max-pages must be positive')
    if args.command=='import-session':
        raw = getpass.getpass('SESSION_JSON> ') if sys.stdin.isatty() else sys.stdin.read()
        import_session(args.session, json.loads(raw))
        print(json.dumps({'sessionSaved':True,'passwordStored':False}))
        return 0
    client = Client(args.session)
    if args.command=='status':
        print(json.dumps({'hasAccessToken':bool(client.session.get('accessToken')),
                          'hasRefreshToken':bool(client.session.get('refreshToken')),
                          'accessRemainingMinutes':round((int(client.session['accessTokenExpireTime'])-time.time()*1000)/60000)}))
        return 0
    if args.command=='refresh':
        client.refresh();print(json.dumps({'sessionUpdated':True}));return 0
    from export import export
    selected = {int(v.strip()) for v in args.types.split(',')} if args.types else None
    result = export(client, args.output, args.days, selected, args.max_pages)
    print(json.dumps({'status':result['status'],'endpointCounts':result['endpointCounts'],
                      'archive':str(args.output/'huawei-health-data.zip')}, ensure_ascii=False))
    return 0 if result['status']=='complete' else 2


if __name__=='__main__':
    try:
        raise SystemExit(main())
    except (HealthError,OSError,ValueError,KeyError,TypeError) as error:
        # OS/JSON exceptions may include sensitive input; keep stdout/stderr generic.
        message=str(error) if isinstance(error,HealthError) else 'Invalid session, input or response; secret details omitted'
        print(json.dumps({'error':message}),file=sys.stderr)
        raise SystemExit(1)
