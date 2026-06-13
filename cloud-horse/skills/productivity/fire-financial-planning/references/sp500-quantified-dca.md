# S&P 500 Quantified DCA System — Worked Example (2026-05-19)

## User Profile

| Attribute | Value |
|-----------|-------|
| Investor type | Long-term (10yr+), single ETF (513500 标普500) |
| Investment channel | 券商场内ETF (not 支付宝场外) |
| Buying unit | **Shares (股)** — not CNY amount |
| Base allocation | 500 shares/month fixed |
| Monthly budget modest | Can't increase much; optimizes through timing, not volume |

## The Core Insight

Most DCA advice assumes **fixed CNY amount** (每月投1,000元). This user buys **fixed shares** (每月500股). These are different behaviors:

```
Fixed CNY (standard DCA):
  Price drops → buy more shares (good)
  Price rises → buy fewer shares (good)
  → Auto-adjusts toward lower avg cost ✅

Fixed Shares (user's pattern):
  Price drops → spend less (misses opportunity ❌)
  Price rises → spend more (buys at peak ❌)
  → Actually counterproductive, amplifies buy-high-sell-low ⚠️
```

## The Fix: Rules-Based Share DCA

Transform fixed-share buying into a quantified strategy with a simple lookup table:

| Condition (month-over-month) | Shares | Logic |
|------------------------------|--------|-------|
| Drop > 10% | **1,000** (2×) | Fear buy — best opportunity |
| Drop 5-10% | **750** (1.5×) | Discount buy |
| ±5% | **500** (1×) | Normal — stay the course |
| Rose > 5% | **300** (0.6×) | Avoid chasing |
| Dropped 3+ consecutive months | +200 more | Persistently low, add |
| Rose 3+ consecutive months | -200 less | Persistently high, slow |
| Single crash > 15% | **1,500** (3×) | Once-a-year-or-rarer event |
| ATH drawdown > 20% | Full remaining cash | Once-a-decade event |
| 10+ consecutive monthly gains | Skip 1 month | Wait for pullback |

## Why This Works

```
Standard fixed-shares DCA (500股/月):
  Cost basis ≈ average price over period (no advantage)

Quantified share DCA:
  Buy 2× when down → lower cost basis than average
  Buy 0.6× when up → avoid buying at peak
  → Cost basis below period average = the entire point of DCA
```

## Implementation

1. Set monthly calendar alarm (10th of month)
2. Check S&P 500 price vs 1 month ago (or check index value from any finance app)
3. Read table → determine share count
4. Execute order in brokerage app
5. Total time: ~5 minutes/month

## Pitfalls

- **Don't overthink**: the simple 5-condition table captures 90%+ of the benefit. Adding more rules creates analysis paralysis.
- **Don't skip months**: the base 500 shares keeps you in the market. The adjustment is on top, not instead of.
- **Log performance annually**: compare cost basis vs simple DCA. If after 3 years the strategy isn't winning, revert to simple DCA.
- **This only works with 场内ETF**: 场外QDII funds are limited to 10元/day (as of 2026), making this impossible at any meaningful share count.
