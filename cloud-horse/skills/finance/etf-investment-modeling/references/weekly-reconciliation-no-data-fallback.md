# Weekly Reconciliation: No User Data Fallback

## Context

Normal workflow: user sends portfolio screenshot on Sunday → cron runs Monday 09:00 → agent generates weekly report.

When the user doesn't send data (or the data doesn't arrive in time), the cron should still produce a useful report.

## Fallback Procedure

### Step 1: Load Last Known Snapshot

Use the most recent portfolio report from `~/workspace/QuantBase/portfolio/`:

```python
import os, re
portfolio_dir = '/home/agentuser/workspace/QuantBase/portfolio'
reports = sorted([f for f in os.listdir(portfolio_dir) if f.startswith('report-')])
# Use the latest
```

Key data needed from the report:
- Per-fund holding amounts (for active, QDII-locked, and stopped funds)
- Baseline portfolio total (e.g., ¥226,225 as of 2026-06-01)

### Step 2: Fetch Current NAV Estimates

For every fund in the portfolio, fetch estimated NAV from fundgz:

```python
import urllib.request, json, time

fund_codes = {
    '270042': '广发纳指100联接A',
    '040046': '华安纳指100联接A',
    # ... all 13+ funds
}

for code, name in fund_codes.items():
    url = f'https://fundgz.1234567.com.cn/js/{code}.js'
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    resp = urllib.request.urlopen(req, timeout=10).read().decode('utf-8')
    json_str = resp[resp.index('{'):resp.rindex('}')+1]
    nav_data = json.loads(json_str)
    gsz = float(nav_data.get('gsz', 0))
    dwjz = float(nav_data.get('dwjz', 0))
    gszzl = float(nav_data.get('gszzl', 0))
    jzrq = nav_data.get('jzrq', '')
    print(f'{code} {name}: dwjz={dwjz} gsz={gsz} gszzl={gszzl}% jzrq={jzrq}')
    time.sleep(0.3)  # avoid rate limiting
```

**Pitfall**: fundgz returns `jsonpgz({...})` wrapped. Strip the wrapper: `resp[resp.index('{'):resp.rindex('}')+1]`.

**Pitfall**: Some funds return error (e.g., 006327 中概互联, 164906 交银海外) — skip those and note "NAV data unavailable".

### Step 3: Estimate Portfolio Value

For each fund, estimate current value:
```
new_value = last_reported_amount × (1 + gszzl / 100)
```

Where `gszzl` is the estimated change % from the last official NAV.

**Pitfall**: `gszzl` is relative to the last official NAV (`dwjz`), not relative to the last REPORTED value. The estimate is approximate — ±1% error is acceptable.

### Step 4: Identify Market Context

Fetch key index data from the DB:

```python
import json
with open('/home/agentuser/.hermes/etf_market_db.json') as f:
    db = json.load(f)
# Check last 5 days of sp500, nasdaq100, sh000300, sh000905
```

Also fetch current ETF premiums (Tencent API + fundgz):

```python
import urllib.request
# Price
req = urllib.request.Request('http://qt.gtimg.cn/q=sh513650')
resp = urllib.request.urlopen(req, timeout=10).read().decode('gbk')
parts = resp.split('~')
price = float(parts[3])

# NAV
req2 = urllib.request.Request('https://fundgz.1234567.com.cn/js/513650.js', 
                              headers={'User-Agent': 'Mozilla/5.0'})
resp2 = urllib.request.urlopen(req2, timeout=10).read().decode('utf-8')
gsz = float(json.loads(resp2[resp2.index('{'):resp2.rindex('}')+1]).get('gsz', 0))
premium = (price / gsz - 1) * 100
```

### Step 5: Generate Report (500字 WeChat style)

Structure:
1. ⚠️ Header: "未收到周日数据，以下为估算值"
2. Market summary table (1-2 lines)
3. Portfolio estimate vs last known (show biggest gainers/losers)
4. Equity quadrant progress bar
5. Premium alerts (if any fund >3%)
6. Action items for the coming week

## FRED Data Freshness Issue (discovered 2026-06-08)

As of 2026-06-08, FRED `SP500` data stopped at 2026-06-01 and `NASDAQ100` at 2026-05-29. This means:
- The DB update function ran but got no new records
- The daily report shows "S&P 500: 7,600" for multiple consecutive days
- This is NOT necessarily a bug — FRED updates with 1-2 business day lag
- However, if the same value persists for >3 trading days, the FRED fetcher may need investigation

**Workaround**: Use Tencent K-line API for ETF prices and fundgz NAV data for portfolio valuation. US index data from FRED is only needed for long-term backtesting.

## Daily Report MA60 Bug (discovered 2026-06-08)

The daily report (`daily_report.py` or `etf_db.py`) shows MA60 values like "60日均¥7015.782" for S&P 500 ETF 513650. This is WRONG — the ETF trades at ~¥1.9.

**Root cause**: The `get_recommendation()` function computes MA60 on the underlying index FRED data (S&P 500 index level ~7,600) instead of the ETF's own K-line close prices (~¥1.9).

**Fix**: The recommendation function must use the ETF's historical close prices from the Tencent K-line API, not the FRED index data. Only the ETF price MA60 is relevant for buying decisions.
