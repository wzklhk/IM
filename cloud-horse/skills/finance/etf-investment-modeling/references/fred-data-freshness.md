# FRED Data Freshness: Update Lag & Workarounds

## The Problem

As of 2026-06-08, the S&P 500 and Nasdaq 100 data in the DB is stale:
- S&P 500: last record 2026-06-01 (7,599.96)
- Nasdaq 100: last record 2026-05-29 (30,333.18)

The DB update function (`etf_db.update_db()`) runs every trading day at 09:00 CST and successfully fetches A-share data through the current date, but US index data lags.

## Root Cause

FRED (Federal Reserve Economic Data) updates with a **1-3 business day lag**:

| Series | Update Pattern | Typical Lag |
|--------|---------------|:-----------:|
| `SP500` | Daily close values, published next morning ET | 1 business day |
| `NASDAQ100` | Daily close values, published next morning ET | 1 business day |
| `DJIA` | Same as SP500 | 1 business day |
| `DEXCHUS` (CNY FX) | Daily, but may skip weekends | 2-3 days |

If today is Monday June 8 and the VPS is in CST (UTC+8):
- Friday June 5 US close = Saturday June 6 04:00 CST
- FRED may publish Friday's data by Monday morning ET = Monday evening CST
- The 09:00 CST cron runs BEFORE FRED has updated with Friday's data
- So the newest data available is Thursday June 4 (or even Wednesday)

## Workarounds

### 1. Accept the Lag

For the daily report the lag is acceptable — the report primarily shows A-share ETF prices (which are current) and US index context (which is fine with 1-2 day lag). The portfolio NAV data comes from `fundgz` which is the authoritative source.

### 2. Verify with Tencent K-line API

When in doubt about whether the DB is stale vs FRED is just slow:

```python
import urllib.request, json

# Fetch 513650 K-line for MA60 computation
url = 'http://ifzq.gtimg.cn/appstock/app/fqkline/get?param=sh513650,day,2026-01-01,2026-06-08,200,qfq'
req = urllib.request.Request(url)
resp = urllib.request.urlopen(req, timeout=15).read().decode('utf-8')
data = json.loads(resp)
d = data.get('data', {}).get('sh513650', {})
days = d.get('day') or d.get('qfqday') or []
print(f'Last date: {days[-1][0]}')  # This shows the most recent trading day
closes = [float(day[2]) for day in days]
ma_60 = sum(closes[-60:]) / 60
print(f'MA60: {ma_60:.4f}')
```

### 3. Use fundgz NAV for Portfolio Valuation

The `fundgz.1234567.com.cn` API provides estimated NAV for QDII funds. This is more current than FRED for portfolio valuation purposes. See `references/qdii-premium-tracking.md`.

## Detection: Is the DB Stale or FRED Just Slow?

| Condition | Likely Diagnosis |
|-----------|-----------------|
| Same S&P 500 value for 1-2 trading days | Normal FRED lag |
| Same S&P 500 value for 3+ trading days | Stale DB or FRED fetcher issue |
| A-share data current but US data stale | FRED lag (expected, see above) |
| NEITHER A-share nor US updating | DB update function broken |

## Future Improvement

Consider adding a fallback US data source that updates faster:
- Tencent Finance does NOT provide US index data
- Consider a lightweight API call to a US-based endpoint for daily close
- Or simply accept the lag and note it in reports: "US index data as of [date], fresh A-share data"
