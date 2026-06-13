# Global Index Database Configuration

Database location: `~/.hermes/etf_market_db.json`
Total: **77,728 records** across 13 indices + 4 ETFs (as of 2026-05-28)

## Full Historical Data Coverage

### 🇺🇸 US Indices (FRED — daily close only)

FRED CSV endpoint (no API key needed, use `curl` not `pd.read_csv(url)`):

```bash
curl -sL "https://fred.stlouisfed.org/graph/fredgraph.csv?id=SP500&cosd=2016-01-01&coed=2026-05-27"
```

| Index | FRED ID | Records | Earliest | Notes |
|-------|---------|---------|----------|-------|
| S&P 500 | SP500 | 2,515 | 2016-05-23 | Daily data only from ~2016 |
| Nasdaq 100 | NASDAQ100 | 10,179 | 1986-01-02 | 40 years of data |
| 道琼斯 | DJIA | 2,512 | 2016-05-27 | Daily data only from ~2016 |
| VIX波动率 | VIXCLS | 9,194 | 1990-01-02 | 36 years |

**Pitfall**: FRED caps each request at ~2,600 records. For full history, use chunked fetching:
```python
def fetch_fred_all(series_id):
    all_rec = []
    for decade in range(8):
        start = f'{1900 + decade*20}-01-01'
        url = f'https://fred.stlouisfed.org/graph/fredgraph.csv?id={series_id}&cosd={start}&coed={current_end}'
        # parse CSV, deduplicate, merge, update current_end to earliest date
    return all_rec
```

SP500 and DJIA only return ~10 years of daily data from FRED. The historical daily frequency may not be available further back. NASDAQ100 and VIXCLS return 30-40 years.

### 🇨🇳 A-Share Indices (Tencent Finance — daily K-line with OHLCV)

Maximum records per API call: **2,000** (limit parameter in URL). Exceeding this returns `"param error"`.

Use **chunked fetching** with backward iteration to get full history:

```python
all_records = []
current_end = '2026-05-27'
for chunk in range(10):
    check_start = f'{1900 + chunk * 20}-01-01'
    url = f'http://ifzq.gtimg.cn/appstock/app/fqkline/get?param=sh000001,day,{check_start},{current_end},2000,qfq'
    # fetch, deduplicate, prepend to all_records
    # update current_end = first date of new chunk
    # stop when API returns less than 2000 records (means end of available data)
```

**Limitation**: Some indices started after 1990 (e.g., 科创50 in 2019, 创业板指 in 2010). The chunk loop will naturally stop when the API returns fewer records than the limit.

| Index | Code | Records | Earliest | Market |
|-------|------|---------|----------|--------|
| 上证指数 | sh000001 | 8,475 | 2004-01-02 | CN |
| 沪深300 | sh000300 | 5,133 | 2012-01-04 | CN |
| 中证500 | sh000905 | 4,704 | 2012-01-04 | CN |
| 深证成指 | sz399001 | 8,116 | 2009-12-04 | CN |
| 创业板指 | sz399006 | 3,879 | 2017-01-03 | CN |
| 科创50 | sh000688 | 1,549 | 2019-12-31 | CN |

### 🇭🇰 HK Indices (Tencent Finance)

Same API and chunked mechanism as A-share.

| Index | Code | Records | Earliest |
|-------|------|---------|----------|
| 恒生指数 | hkHSI | 9,256 | 2010-03-01 |
| 恒生科技 | hkHSTECH | 1,434 | 2020-07-27 |
| 恒生国企 | hkHSCEI | 8,102 | 2010-02-24 |

### 📊 Chinese QDII ETFs (Tencent Finance)

| Code | Name | Records | Earliest |
|------|------|---------|----------|
| 513650 | 南方标普500ETF | 760 | 2023-04-04 |
| 513100 | 国泰纳斯达克ETF | 640 | 2023-09-27 |
| 513500 | 博时标普500ETF | 640 | 2023-09-27 |
| 159941 | 广发纳斯达克ETF | 640 | 2023-09-27 |

**ETF limitation**: These QDII ETFs were listed relatively recently (2023). No historical data before listing date.

## Chunked Fetching Implementation

### Tencent K-Line (ifzq.gtimg.cn)

Full-history fetch via backward chunked pagination. Each call returns max 2,000 records. The loop pushes `current_end` backward until we exhaust available data or hit the max chunk limit.

**Pitfall**: The `check_start` date must always be EARLIER than `current_end`. Each chunk moves `current_end` further back in time. Using strictly increasing `check_start` years (1900, 1920, 1940) works because the API only returns data within the [start, end] window.

**Pitfall**: Index codes already contain the exchange prefix (e.g., `sh000001`). The fetch function must detect this and NOT double-prefix. Check `param.startswith(('sh','sz','hk'))` before prepending.

### FRED (fred.stlouisfed.org)

Decade-based chunked requests. Each call returns the most recent ~2,600 records within the [start, end] window. Move `end` backward each iteration.

**Pitfall**: Some series (SP500, DJIA) have daily data only from ~2016. Earlier data may exist at monthly frequency but won't be returned by the daily endpoint.

## Real-time Price Fetching

For A-share and HK indices, use the same Tencent real-time API as ETFs:

```bash
curl -s "http://qt.gtimg.cn/q=sh000001"           # 上证指数
curl -s "http://qt.gtimg.cn/q=sh000300"           # 沪深300
curl -s "http://qt.gtimg.cn/q=hkHSI"               # 恒生指数
curl -s "http://qt.gtimg.cn/q=sh513650,sz159941"   # Multiple ETFs
```

## Full Index Configuration

```python
FRED_INDICES = {
    'sp500':     {'name': 'S&P 500',     'fred_id': 'SP500',     'market': 'US'},
    'nasdaq100': {'name': 'Nasdaq 100',  'fred_id': 'NASDAQ100', 'market': 'US'},
    'djia':      {'name': '道琼斯',      'fred_id': 'DJIA',      'market': 'US'},
    'vix':       {'name': 'VIX波动率',   'fred_id': 'VIXCLS',    'market': 'US'},
}

TENCENT_INDICES = {
    'sh000001': {'name': '上证指数',   'exchange': 'sh', 'market': 'CN'},
    'sh000300': {'name': '沪深300',    'exchange': 'sh', 'market': 'CN'},
    'sh000905': {'name': '中证500',    'exchange': 'sh', 'market': 'CN'},
    'sz399001': {'name': '深证成指',   'exchange': 'sz', 'market': 'CN'},
    'sz399006': {'name': '创业板指',   'exchange': 'sz', 'market': 'CN'},
    'sh000688': {'name': '科创50',     'exchange': 'sh', 'market': 'CN'},
    'hkHSI':    {'name': '恒生指数',   'exchange': 'hk', 'market': 'HK'},
    'hkHSTECH': {'name': '恒生科技',   'exchange': 'hk', 'market': 'HK'},
    'hkHSCEI':  {'name': '恒生国企',   'exchange': 'hk', 'market': 'HK'},
}

CN_ETFS = {
    '513650': {'name': '南方标普500ETF',   'exchange': 'sh'},
    '513100': {'name': '国泰纳斯达克ETF',  'exchange': 'sh'},
    '513500': {'name': '博时标普500ETF',   'exchange': 'sh'},
    '159941': {'name': '广发纳斯达克ETF',  'exchange': 'sz'},
}
```

## Scripts

All in `~/.hermes/scripts/`:

| Script | Purpose |
|--------|---------|
| `etf_db.py` | Database manager (init/update/rec/report/status) |
| `daily_etf_report.py` | Cron wrapper: update DB then print report |

Commands:
```bash
python3.12 ~/.hermes/scripts/etf_db.py init      # Download all history (one-time, about 3min)
python3.12 ~/.hermes/scripts/etf_db.py update     # Daily refresh
python3.12 ~/.hermes/scripts/etf_db.py rec        # Smart DCA recommendation
python3.12 ~/.hermes/scripts/etf_db.py report     # Full global market report
python3.12 ~/.hermes/scripts/etf_db.py status     # Health check
```
