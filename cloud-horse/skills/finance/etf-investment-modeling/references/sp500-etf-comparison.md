# S&P 500 ETF Comparison (Chinese QDII)

Data as of 2026-05-28. Sources: 天天基金网, 东方财富API.

## At-a-Glance

| Code | Name | Manager | 管理费 | 托管费 | 总费率 | 规模(亿) | 日均成交额(亿) | 最新价 | 申购状态 |
|:----:|:----|:-------:|:----:|:----:|:----:|:------:|:-----------:|:-----:|:--------:|
| **513650** | 南方标普500ETF | 南方基金 | 0.60% | 0.15% | **0.75%** | **45.75** | **~2.04** | 1.908 | ✅ 正常 |
| **513500** | 博时标普500ETF | 博时基金 | 0.60% | 0.20% | **0.80%** ❌ | **209.41** 🥇 | **~3.03** | 2.541 | ⚠️ 暂停申购 |
| **159612** | 国泰标普500ETF | 国泰基金 | 0.60% | 0.15% | **0.75%** | **7.45** ⚠️ | **~0.38** | 1.962 | ⚠️ 暂停申购 |
| **159655** | 华夏标普500ETF | 华夏基金 | 0.60% | 0.15% | **0.75%** | **33.86** | 待确认 | 1.858(NAV) | ✅ 正常 |

## Tracking Error

All three major S&P 500 ETFs show **~0.57% annualized tracking error** (from 天天基金 tsdata pages). Essentially identical:

- 513650 南方: 0.57%
- 513500 博时: 0.57%  
- 159612 国泰: 0.57%

## Fee Breakdown

### 管理费 (Management Fee)
- All 4 ETFs: **0.60%** per year

### 托管费 (Custodian Fee)
- 513650 (南方): **0.15%**
- 513500 (博时): **0.20%** — highest
- 159612 (国泰): **0.15%**
- 159655 (华夏): **0.15%**

### Total Annual Fee
| Ratio | ETFs |
|:----:|:----:|
| 0.75% | 513650, 159612, 159655 |
| 0.80% | 513500 |

## Liquidity

| ETF | Daily Volume (shares) | Daily Amount | Adequate for |
|:---|:-------------------:|:-----------:|:------------|
| 513650 南方 | 1,069,137手 | ¥2.04亿 | Up to ~¥500万/笔 |
| 513500 博时 | 1,190,641手 | ¥3.03亿 | Up to ~¥750万/笔 |
| 159612 国泰 | 193,554手 | ¥0.38亿 | Up to ~¥95万/笔 ⚠️ |
| 159655 华夏 | TBD | TBD | TBD |

## Notable Differences

### 513650 (南方) — Recommended Default
- ✅ All-around balanced: good size, good liquidity, normal申购 status
- ✅ Lowest possible total fee tier (0.75%)
- ✅ Single share price ~¥1.9 — easy math for monthly DCA
- ⚠️ Smaller than 博时 but 45亿 is still very adequate for retail DCA

### 513500 (博时) — Largest but Slightly More Expensive
- 🥇 Largest AUM (209亿) → tightest premium/discount
- 🥇 Best liquidity (3亿/day)
- ❌ 0.80% fee — 0.05% more than peers
- ⚠️ 暂停场外申购 (but secondary market trading unaffected)
- ⚠️ 暂停申购 signals tight QDII quota — may cause higher premium

### 159612 (国泰) — Small, Not Recommended for DCA
- ❌ Only 7.45亿 — bid/ask spreads will be wide
- ❌ Only 0.38亿 daily turnover — slippage on 500-share orders
- ❌ 暂停申购 — same QDII concern
- Not suitable for regular buying/selling

### 159655 (华夏) — Potential Alternative
- 华夏 brand, known for ample QDII quota
- 33.86亿 — decent size
- Same fee as 南方 (0.75%)
- ⚠️ Newer fund (2022-10-12) — less history to evaluate tracking
- ⚠️ Liquidity data unavailable from API — may have thin trading

## Recommendation

**For the user's use case** (500 shares/month DCA at ~¥954/mo):

1. **Keep using 513650 (南方标普500ETF)** — No compelling reason to switch
2. The 0.05% fee advantage over 博时 translates to ~¥5.70/year savings (negligible)
3. The 博时 scale advantage (209亿 vs 46亿) only matters for >¥1M orders
4. 华夏 is a potential second choice but lacks proven liquidity
5. Switching costs (buy/sell spread ~0.1-0.2%) outweigh any theoretical benefit

**Decision**: 513650 is the optimal S&P 500 ETF for this user's DCA strategy.
