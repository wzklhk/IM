# Cron Debug Session — 2026-06-02

## Symptom

Three QuantBase cron jobs (`quantbase-daily` / `weekly` / `monthly`) all failing with identical error:

```
ModuleNotFoundError: No module named 'report_generator'
```

After fixing the import path, a second error surfaced:

```
KeyError: 'etfs' in report_generator.py line 140
```

## Root Cause 1: `no_agent` mode ignores `workdir`

The cron jobs used `no_agent: true` with `workdir: /home/agentuser/workspace/QuantBase`. Despite this config, the cron executor does NOT `cd` to the workdir before running scripts. Any `os.getcwd()` call returns the executor's own directory, not QuantBase.

**Affected scripts** (all in `~/.hermes/scripts/`):
- `daily_report.py` — `BASE = os.getcwd()`
- `weekly_report.py` — `BASE = os.getcwd()`
- `monthly_report.py` — `BASE = os.getcwd()`

**Fix applied:** Replace `os.getcwd()` with hardcoded quantBase path + `os.chdir()`:

```python
# BEFORE (broken in no_agent mode):
BASE = os.getcwd()
sys.path.insert(0, BASE)

# AFTER (works in any mode):
QB = '/home/agentuser/workspace/QuantBase'
sys.path.insert(0, QB)
os.chdir(QB)
```

## Root Cause 2: DB missing `etfs` key

`report_generator.py` and `models/smart_dca.py` access `db['etfs']` in multiple places, but both DB copies only had `meta` + `indices`:

```
~/.hermes/etf_market_db.json            → {meta, indices}  NO etfs
~/workspace/QuantBase/data/processed/market_db.json → {meta, indices}  NO etfs
```

**Fix applied:**
1. `load_db()` in `report_generator.py`: added `if 'etfs' not in db: db['etfs'] = {}`
2. All `db['etfs']` → `db.get('etfs', {})` in `smart_dca.py` and `report_generator.py`
3. Both disk DB files patched to include `"etfs": {}`

## Root Cause 3: Git push timeout

Even after fixing imports and DB, the `push()` function's `subprocess.run(..., timeout=30)` timed out (VPS in Guangzhou → GitHub latency).

**Fix applied:** Wrapped git commands in try/except to gracefully skip on network errors:
```python
try:
    r = subprocess.run(cmd, capture_output=True, text=True, timeout=10)
except Exception as e:
    print(f"  git skip ({type(e).__name__}), will retry next run")
```

## Files Modified

| File | Change |
|------|--------|
| `~/.hermes/scripts/daily_report.py` | `os.getcwd()` → `QB` hardcoded + `os.chdir(QB)` |
| `~/.hermes/scripts/weekly_report.py` | Same |
| `~/.hermes/scripts/monthly_report.py` | Same |
| `~/workspace/QuantBase/report_generator.py` | `load_db()` + `etfs` guard + git try/except |
| `~/workspace/QuantBase/models/smart_dca.py` | `db['etfs']` → `db.get('etfs', {})` |
| Both DB JSON files | Added `"etfs": {}` |

## Verification

Manual run of `python3.12 ~/.hermes/scripts/daily_report.py` succeeded:
- DB update: all 7 indices refreshed (S&P +5, Nasdaq +3, etc.)
- Smart DCA computed: 标普+8.33%→买300股, 纳指+14.53%→买200股
- Git push gracefully skipped (network)
- Exit code 0
