# QDII ETF Premium Tracking

## Premium Ranges by Fund (observed as of 2026-06)

QDII ETF premiums reflect QDII quota pressure — when more money chases limited foreign exchange quotas, premiums inflate.

### S&P 500 ETFs

| ETF | Normal | Warning | Crisis | 2026-06-08 |
|-----|:------:|:-------:|:------:|:----------:|
| **513650** 南方标普500 | <1% | 1-3% | >3% | **3.32%** ⚠️ |
| **513500** 博时标普500 | <1% | 1-2% | >3% | ~1% |
| **159655** 华夏标普500 | <1% | 1-3% | >3% | ~0.7% |
| **159612** 国泰标普500 | <1% | 1-3% | >3% | ~0.7% |

### Nasdaq 100 ETFs

| ETF | Normal | Warning | Crisis | 2026-06-08 |
|-----|:------:|:-------:|:------:|:----------:|
| **513300** 华夏纳指 | <2% | 2-5% | >5% | **9.61%** 🚨 |
| **159941** 广发纳指 | <2% | 2-5% | >5% | **10.68%** 🚨 |
| **513110** 华安纳指 | <2% | 2-5% | >5% | ~7% |
| **513100** 国泰纳指 | <2% | 2-5% | >5% | ~8% |

## Interpretation

### Normal Premium (<1% for S&P 500, <2% for Nasdaq)
- Normal market conditions, adequate QDII quota
- This is the best time to buy — minimal friction cost
- ETF price closely tracks NAV

### Warning Premium (1-3% S&P 500, 2-5% Nasdaq)
- QDII quota tightening — more demand than supply
- Favorable for existing holders (ETF trades above NAV)
- For new buyers: consider waiting for pullback or split purchase into smaller tranches
- Monitor 外汇额度 announcements (quarter-end may see quota replenishment)

### Crisis Premium (>3% S&P 500, >5% Nasdaq)
- Severe QDII quota shortage
- **Do not enter new positions** at these levels
- Existing holders should NOT panic sell — the premium is temporary, NAV is what matters long-term
- 场外联接基金 are almost certainly 限购 at this level
- The user's daily ¥10-20/日定投 may be the only working purchase channel
- Quarter-end (March, June, September, December) often brings new quota allocation

## How to Compute Premium

```python
import urllib.request, json

def get_premium(etf_code, exchange='sh'):
    """Compute ETF premium %."""
    # Step 1: Get market price from Tencent API
    url = f'http://qt.gtimg.cn/q={exchange}{etf_code}'
    req = urllib.request.Request(url)
    resp = urllib.request.urlopen(req, timeout=10).read().decode('gbk')
    parts = resp.split('~')
    price = float(parts[3])
    
    # Step 2: Get NAV from fundgz
    url2 = f'https://fundgz.1234567.com.cn/js/{etf_code}.js'
    req2 = urllib.request.Request(url2, headers={'User-Agent': 'Mozilla/5.0'})
    resp2 = urllib.request.urlopen(req2, timeout=10).read().decode('utf-8')
    json_str = resp2[resp2.index('{'):resp2.rindex('}')+1]
    nav_data = json.loads(json_str)
    gsz = float(nav_data.get('gsz', 0))  # estimated NAV
    
    if gsz == 0:
        # Fallback to official NAV
        gsz = float(nav_data.get('dwjz', 0))
    
    premium = (price / gsz - 1) * 100
    return {
        'code': etf_code,
        'name': nav_data.get('name', ''),
        'price': price,
        'nav': gsz,
        'premium_pct': round(premium, 2),
        'nav_date': nav_data.get('jzrq', ''),
        'gsz_time': nav_data.get('gztime', ''),
        'gszzl': nav_data.get('gszzl', '')
    }
```

## Pitfalls

**Pitfall — Stale gsz**: The `gsz` (estimated NAV) may be from the previous trading day for QDII ETFs. The gztime shows when it was computed (e.g., "2026-06-06 04:00" means Saturday 4am = Friday US close). When A-share market opens Monday, this NAV is already ~3 days old. Premiums may appear inflated because US markets moved since the last NAV fix.

**Pitfall — Pre-market vs intraday**: Before A-share market open (09:30 CST), ETF prices show Friday's close. After market close (15:00 CST), prices are stale until next day. Premium data is most reliable during A-share trading hours (9:30-15:00 CST).

**Pitfall — Batch fetching NAV**: The fundgz API is slower than Tencent API. Batch-fetching all 10+ ETFs in one script may timeout. Use 0.3s delay between requests or fetch via separate curl calls.

**Pitfall — Fund code for fundgz**: The fundgz API uses the raw fund code without exchange prefix (e.g., `513650` not `sh513650`). The Tencent API uses exchange prefix (`sh513650`).

## When Premium Analysis Matters Most

1. **Monthly 20th (user's 513650 purchase day)** — Check premium before each buy
2. **Before entering Nasdaq 100** — The user is waiting for a favorable entry point. Premiums >3% are a clear "wait" signal
3. **Quarter-end (March/June/September/December)** — QDII quota may be replenished, causing premiums to normalize
