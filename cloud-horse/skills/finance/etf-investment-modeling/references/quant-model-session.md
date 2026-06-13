# Quant Model Backtest Session — 2026-05-27/28

## Context

User (魏开昊) holds 513650 (南方标普500ETF), DCA 500 shares monthly on 20th. Requested a quantitative investment model for S&P 500 + Nasdaq 100 ETFs to evaluate and optimize strategy.

## Data Source Journey

1. **First attempt**: `akshare` + East Money API → `Connection aborted` from Chinese VPS
2. **Second attempt**: Direct HTTP to `push2his.eastmoney.com` → Also blocked
3. **Third attempt**: Yahoo Finance → `Too Many Requests` rate limited
4. **Winner**: **Tencent Finance API** (`ifzq.gtimg.cn`) → Works reliably

### Tencent Historical Endpoint

```
http://ifzq.gtimg.cn/appstock/app/fqkline/get?param={exchange}{code},day,{start},{end},{limit},qfq
```

### Critical Discovery

Different ETFs return data under different JSON keys:
- `day` key: Used by 513650 (标普500)
- `qfqday` key: Used by 513100 (纳斯达克100)

**Always fallback**: `data.get('day') or data.get('qfqday') or []`

## Key Results

### User's Strategy (513650, 500 shares/mo, 20th)
- Period: 2023-04-20 ~ 2026-05-20 (38 trades)
- Total invested: ¥27,818
- Current value: ¥36,252
- Total return: **+30.32%** (annualized: 8.91%)
- Max drawdown: -21.15%

### Comparison: Nasdaq vs S&P 500 (same 500 shares/mo)

| Metric | S&P 500 | Nasdaq 100 |
|--------|---------|------------|
| Return | 30.32% | **44.27%** |
| Annualized | 8.91% | **14.75%** |
| Max DD | 21.15% | 24.01% |

### Investment Day Impact

Standard deviation across days 1-28: ±0.76% → **Don't optimize the day**

### Recommended Split

- 60% S&P 500 (513650): 300 shares/mo
- 40% Nasdaq 100 (513100 or 159941): 200 shares/mo

## Chart Rendering Bug

Initial matplotlib chart had garbled Chinese text. Fix:
- System has `WenQuanYi Zen Hei` at `/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc`
- Must call `fm.fontManager.addfont(path)` BEFORE setting rcParams
- Then set `plt.rcParams['font.sans-serif'] = ['WenQuanYi Zen Hei', 'DejaVu Sans']`
