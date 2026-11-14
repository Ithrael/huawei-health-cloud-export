# Huawei Health Cloud Export Skill

[简体中文](README.md) | English

A standalone Codex skill and Python project for exporting your own Huawei Health data through read-only requests. Sign in with your own Huawei account, password, and an SMS verification code you provide to export activity, sleep, HRV, heart rate, stress, and emotion data. The project also includes fitness capability endpoints whose request parameters have been verified.

API source: **Huawei Health for Android, `com.huawei.health` 17.0.10.300 / versionCode 1700010300**, analyzed and tested on November 13, 2026. APK SHA-256: `27e6c6dbaa96e09e1314a1b40d00db4e87e73e8c8c8cbb54bcd495cf386c3119`. Download details, certificate fingerprints, and verification procedures are documented in [APK provenance](references/apk-provenance.json); code evidence is available in [source evidence](references/source-evidence). This repository does not include the APK, personal health data, or login credentials.

The catalog contains **26 native API endpoints and 127 types from the newer data dictionary**, plus 11 ArkUI-X candidates. Of the 21 native endpoints tested, 20 succeeded with at least one set of parameters: 9 returned business data, 10 succeeded with empty results, and 1 provided synchronization metadata. The tested parameters for `getSampleSequenceLatest` failed. Other endpoints remain unverified; the exporter does not guess authentication schemes or trigger calculations.

## Use the skill (★ Recommended)

Requires Python 3.10+ and browser tooling that can retain a Playwright `page` object. The Python scripts require no third-party packages. The root [SKILL.md](SKILL.md) contains the complete skill workflow.

Link or copy this project directory to `~/.codex/skills/huawei-health-cloud-export`, then invoke the skill. If a skill with the same name already exists, preserve the previous version before replacing it. You can also ask the assistant to use this project's `SKILL.md` directly.

```text
$huawei-health-cloud-export --username "your-huawei-account" --password "your-password"
$huawei-health-cloud-export "your-huawei-account" "your-password" --days 30
```

These are **skill arguments passed to the assistant**, not terminal commands. The password is used only in memory during browser interaction; you can also enter it yourself on the official login page. After requesting one verification code, the assistant waits for you to provide it. It does not retrieve SMS messages automatically.

Exports default to the most recent 30 calendar days in UTC+8. New credentials start a new isolated login. A cached session may be reused only when the account has been confirmed and you explicitly request reuse. Access tokens are refreshed normally as they approach expiration; the long-term validity of refresh tokens has not been confirmed.

## Export with Python

The skill handles the browser login and SMS workflow; the Python CLI does not accept a password argument. Follow the [browser login instructions](references/browser-login.md) to import tokens through hidden standard input into a new private session cache, then run:

```sh
python3 scripts/health.py export \
  --session .secrets/huawei-health/session.json \
  --output exports/latest --days 30

# Restrict version-based reads by type; regular statistics endpoints are still queried.
python3 scripts/health.py export \
  --session .secrets/huawei-health/session.json \
  --output exports/selected --types 500044,7,9,700013

python3 scripts/health.py status --session .secrets/huawei-health/session.json
python3 -m unittest discover -s tests
```

An export may take several minutes, depending on the selected types, data volume, and pagination. Use a fresh output directory for each run; do not mix accounts. Network exports use normal API requests to read your own data. Creating the skill or running offline tests does not log in or send SMS messages.

## Observe session renewal

When you explicitly request ongoing maintenance of the current session, run the renewal observer:

```sh
python3 scripts/keepalive.py --session /path/to/your/workspace/.secrets/huawei-health/session.json \
  --state /path/to/your/workspace/.local/keepalive/status.json --hours 24
```

By default, the observer calls the normal refresh endpoint 120 seconds before the access token expires, then verifies the account context. It does not read health data, log in, or send SMS messages. Only one observer may use a session at a time. Set `--hours 0` to run until manually stopped; stop it with SIGTERM or Ctrl+C. The status file and standard output report timing, counters, and token rotation flags without exposing tokens or account details. Any refresh or verification failure stops the observer without automatically logging in again.

This process renews the health API access token; it does not renew cookies for the official website login. The refresh token's long-term validity, revocation policy, and ability to support indefinite renewal remain unconfirmed. Short-term success does not establish permanent validity. The observer depends on a running local process, network connectivity, and the computer remaining awake. It does not install a startup task by default.

## Output

| Path | Contents |
| --- | --- |
| `interfaces/` | One JSON file per endpoint, including business parameters, status, and raw responses |
| `datasets/` | Data normalized by type, with timestamps, sources, and pagination coverage |
| `activity/` | Separate outputs for steps, distance, floors climbed, and active calories |
| `mapping.json` | Field names, meanings, units, verified enumerations, and unknowns |
| `type-mapping.json` | The 127 newer dictionary types, legacy core types, and cloud mappings |
| `provenance.json` | APK version, hash, source, and verification details |
| `manifest.json` | Endpoint results, reasons for skipped calls, and coverage limitations |
| `huawei-health-data.zip` | An archive of the files above, excluding session credentials |

`recordCount` counts records in responses or datasets, rather than days of measurement. Daily statistics from different sources are kept separate. Synchronized records may contain updates to events from earlier dates; normalized results are filtered by event time. Records without a confirmed event time are retained in `undatedRecords`. Binary `detailData` is preserved, and HRV RMSSD is handled separately from raw RR intervals.

Coverage of the entire app has not been verified, so exports default to `manifest.status=partial` and CLI exit code **2**, while successfully retrieved results are still delivered. Exit code 1 indicates a session, input, or execution error. A partial export should not trigger another login, another SMS request, or substitution of missing data with zeroes.

## Offline health dashboard

★ Recommended: try the simulated data first. No account or third-party packages are required:

```sh
python3 scripts/dashboard.py --demo --output exports/demo/dashboard.html
open exports/demo/dashboard.html

# Generate a dashboard from an existing export without making network requests.
python3 scripts/dashboard.py --input exports/latest --output exports/latest/dashboard.html
```

The `open` command above is for macOS. On other platforms, open the generated HTML file in your browser.

The dashboard uses a Huawei watch-inspired design with an AMOLED black background, rounded cards, colorful charts, and large numbers. It displays custom activity rings, heart rate, HRV, activity trends, sleep stages, and stress records. Controls support dates, 7- and 30-day ranges, trend metrics, per-metric data sources, daily goals, previous/next-day navigation, jumps to card details, fullscreen, and pausing animations. The layout adapts to mobile screens and respects the system's reduced-motion preference. Data, styles, and scripts are embedded in a single HTML file that works offline. You can also ask the skill to “generate a health dashboard from this export directory” or “show the dashboard using simulated data.”

Use the top navigation on desktop or the bottom pill navigation on mobile to switch between **Health overview** (健康总览), **All metrics** (全部指标), and **Data archive** (数据档案). All metrics adds topics such as resting heart rate, emotion, altitude, floors climbed, active-hour markers, basal metabolism, activity segments, extended sleep fields, and achievements. Each topic supports field and source selection. Data archive includes every dataset, endpoint result, activity statistic, and field/coverage reference from the export. It supports search, status and date filters, pagination, expansion of all unknown fields, and downloads of complete JSON display copies. Undated records, deletion markers, and undecoded sequences are also accessible.

Archive files are embedded individually with gzip compression and decompressed on demand in the browser, which must support `DecompressionStream`. Dense charts explicitly label the number of sampled points; the complete records are not truncated. Display copies hide authentication fields and convert large integers to strings to preserve precision. The original export files on disk are not modified. Unknown units, enumerations, and binary payloads are not interpreted as health measurements.

Simulated pages always identify their data as fictional. Pages generated from real exports retain coverage limitations, leave missing values empty, and keep data from different devices separate. Only recognized fields are rendered as measurements: undecoded sleep details and summaries with unconfirmed units are not used to infer durations. Goals apply only to the current page and reset to their defaults on reload. Generated HTML contains health data and is stored in the Git-ignored `exports/` directory alongside the JSON files. It is not uploaded automatically or added automatically to the existing data ZIP.

For request parameter differences, failure cases, authentication schemes, and code locations, see the [interface notes](references/interfaces.md), [API catalog](references/api-catalog.json), and [type mappings](references/type-mapping.json). Regions outside mainland China have not been verified. When updating the supported app version, update provenance and verification records as well as request headers.

The dashboard's three rings represent custom goals for steps, active calories, and distance. Huawei's official Activity Rings represent active calories, exercise duration, and active hours. Fields whose mappings are unconfirmed are not forced into the official ring definitions.
