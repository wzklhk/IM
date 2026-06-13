# Tencent Finance API (qt.gtimg.cn)

## Overview

Tencent's stock quote API at `qt.gtimg.cn` provides real-time and end-of-day data for Chinese-listed securities (stocks, ETFs, funds). It is more reliable from China VPS than East Money APIs.

## Endpoint

```
GET http://qt.gtimg.cn/q=<prefix><code>[,<prefix><code>...]
```

No authentication required.

## Prefix Rules

| Exchange | Code Prefix | Example |
|----------|-------------|---------|
| Shanghai A-shares | `sh` | `sh513650` |
| Shenzhen A-shares | `sz` | `sz159941` |
| Beijing | `bj` | (rarely needed) |

**Rule**: Codes starting with `51`, `11`, `60` → Shanghai (`sh`). Codes starting with `15`, `16`, `00`, `30` → Shenzhen (`sz`). 

## Response Format

**Encoding**: GBK (GB2312/GB18030)

**Structure**: Pipe-delimited (`~` separated) text block per symbol:

```
v_shCODE="51~name~field3~field4~...~fieldN";
```

## Field Mapping (0-indexed)

| Index | Field | Type | Notes |
|-------|-------|------|-------|
| 0 | Market code | int | 51=ETF, varies for stocks |
| 1 | Name | string | GBK encoded Chinese name |
| 2 | Code | string | e.g. `513650` |
| 3 | Current price | float | Latest traded price |
| 4 | Previous close | float | Yesterday's close |
| 5 | Open price | float | Today's open |
| 6 | Volume | int | In 手 (1手=100 shares) |
| 7-32 | Bid/ask levels | various | 5-level order book data |
| 33 | High | float | Day high |
| 34 | Low | float | Day low |
| 35 | Price change | float | Absolute price change |
| 36 | Change % | float | Percentage change from previous close |
| 37 | Amount | float | In 万 (元). Multiply by 10000 for total yuan |

## Python Parsing (Reliable Pattern)

```python
import subprocess, re

def fetch_etfs(codes: list) -> dict:
    """codes: list of ETF codes like ['513650','159941']"""
    qcodes = ','.join(
        f'sh{c}' if c.startswith(('51','11','60')) else f'sz{c}' 
        for c in codes
    )
    result = subprocess.run(
        ['timeout', '10', 'curl', '-s', f'http://qt.gtimg.cn/q={qcodes}'],
        capture_output=True, timeout=15
    )
    raw = result.stdout  # bytes, NOT decoded string
    decoded = raw.decode('gbk')  # Decode GBK bytes directly
    
    results = {}
    for code in codes:
        m = re.search(rf'v_(?:sh|sz){code}="([^"]+)"', decoded)
        if not m:
            continue
        fields = m.group(1).split('~')
        try:
            results[code] = {
                'name': fields[1],
                'current': float(fields[3]),
                'prev_close': float(fields[4]),
                'open': float(fields[5]),
                'volume': int(fields[6]),
                'high': float(fields[33]),
                'low': float(fields[34]),
                'amount': float(fields[37]) * 10000,
                'change_pct': (float(fields[3]) - float(fields[4])) / float(fields[4]) * 100,
            }
        except (IndexError, ValueError):
            continue
    return results
```

## Pitfalls

### 1. GBK Encoding Trap
The API **always** returns GBK bytes. If you call from Python's `requests` library and the response.text has already been decoded as UTF-8, the Chinese characters will be corrupted and you CANNOT re-encode them back to latin1. **Always use subprocess + raw bytes or `requests.get(..., stream=True).content`**.

### 2. Trading Hours Only
The API returns fresh data only during:
- Morning: 09:30-11:30 CST
- Afternoon: 13:00-15:00 CST
Outside these hours, `current` == `prev_close` and `change_pct` == 0.

### 3. Name Encoding
Even after GBK decode, some fund names may show garbled characters (e.g. `��ָETF` instead of `纳指ETF`). This is a display issue — the raw bytes are correct, but the terminal/UI encoding doesn't match. Save parsed data to JSON instead of printing raw names.

### 4. Volume Unit
Volume is in **手** (lots), where 1手 = 100 shares. To get share count: `volume * 100`.

### 5. Amount Unit
Amount is in **万** (10,000). Multiply by 10000 to get yuan. The field value itself is already divided by 10000.

## Historical K-Line API (ifzq.gtimg.cn)

In addition to real-time quotes, Tencent provides a historical K-line endpoint:

```
GET http://ifzq.gtimg.cn/appstock/app/fqkline/get?param=<code>,day,<start>,<end>,<limit>,qfq
```

### Parameters

| Parameter | Description | Example |
|-----------|-------------|---------|
| `code` | Full code with exchange prefix | `sh513650`, `sh000001`, `hkHSI` |
| `day` | K-line frequency | `day` (daily), `week`, `month` |
| `start` | Start date | `2016-01-01` |
| `end` | End date | `2026-05-27` |
| `limit` | Max records per response | **2,000 max** — exceeding returns `param error` |
| `qfq` | Price adjustment | `qfq` (forward-adjusted) |

### Response Format

```json
{
  "code": 0,
  "msg": "",
  "data": {
    "sh513650": {
      "day": [
        ["2023-04-04", "0.991", "0.994", "0.994", "0.989", "24874503.000"],
        ...
      ]
    }
  }
}
```

**Record format**: `[date, open, close, high, low, volume]`

**Pitfall**: Some ETFs/indices use `qfqday` instead of `day` as the key. Always check both:
```python
days = data['data'][key].get('day') or data['data'][key].get('qfqday') or []
```

### Limit Constraint: Max 2,000 Records

The API caps each response at 2,000 records. To get full history:

```python
def fetch_tencent_full(param):
    all_records = []
    current_end = '2026-05-27'
    for chunk in range(10):
        check_start = f'{1900 + chunk * 20}-01-01'
        url = f'http://ifzq.gtimg.cn/appstock/app/fqkline/get?param={param},day,{check_start},{current_end},2000,qfq'
        # fetch, parse, deduplicate, prepend
        # update current_end = first record date
        # break if less than 2000 records returned
    return all_records
```

### Available Index Codes for K-Line

| Code | Index | Exchange | Data Start |
|------|-------|----------|------------|
| sh000001 | 上证指数 | Shanghai | 2004 |
| sh000300 | 沪深300 | Shanghai | 2012 |
| sh000905 | 中证500 | Shanghai | 2012 |
| sz399001 | 深证成指 | Shenzhen | 2009 |
| sz399006 | 创业板指 | Shenzhen | 2017 |
| sh000688 | 科创50 | Shanghai | 2019 |
| hkHSI | 恒生指数 | Hong Kong | 2010 |
| hkHSTECH | 恒生科技 | Hong Kong | 2020 |
| hkHSCEI | 恒生国企 | Hong Kong | 2010 |

**Pitfall**: Index codes ALREADY contain the exchange prefix (`sh`, `sz`, `hk`). Do NOT prepend again. The K-line URL uses the full code directly in the `param` value.

### Exchange Prefix Logic

When fetching both indices and ETFs:

```python
if any(code.startswith(p) for p in ['sh','sz','hk']):
    full_param = code          # index like sh000001
else:
    full_param = f'{exch}{code}'  # ETF like 513650 → sh513650
```

### Index vs ETF Response

Indices return K-line data going back decades (上证 to 2004). ETFs return data only from their listing date (most QDII ETFs listed ~2023). The chunked fetching loop handles this gracefully — it stops when requests return fewer than 2,000 records.

| Aspect | Tencent (qt.gtimg.cn) | East Money (akshare) |
|--------|----------------------|---------------------|
| Authentication | None | None |
| China VPS reliability | ✅ Excellent | ❌ May be blocked |
| Historical data | ❌ Real-time only | ✅ Full history |
| Response time | <500ms | 1-5s |
| GBK encoding | Yes (annoying) | UTF-8 (clean) |
| Rate limits | None observed | 5 req/min |

Use Tencent for daily monitoring, East Money for historical backtesting (if accessible).
