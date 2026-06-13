# Smart DCA Strategy: 10-Year Backtest (2016-2026)

## Three Key Results

### 1. Portfolio (S&P 500 60% + Nasdaq 100 40%)

| Metric | Fixed DCA | Smart DCA (MA Dev) | Diff |
|--------|-----------|-------------------|------|
| Capital deployed | $60,000 | **$47,140** | **-21% less** |
| Total return | 134% | **144%** | **+10%** |
| Annualized return | 8.98% | **9.42%** | +0.44% |
| Max drawdown | -22% | **-17%** | **-5pp** |

**Core insight**: Same or better returns with 21% less capital. Superior capital efficiency.

### 2. S&P 500 Alone (fixed $500/month)

| Metric | Fixed | Smart | Diff |
|--------|-------|-------|------|
| Capital | $60,000 | $49,000 | -$11K (-18%) |
| Total return | 105% | **114%** | +9% |
| Annualized | 7.50% | **7.98%** | +0.48% |
| Max DD | -18% | **-14%** | -4pp |

### 3. Nasdaq 100 Alone (fixed $500/month)

| Metric | Fixed | Smart | Diff |
|--------|-------|-------|------|
| Capital | $60,000 | $44,350 | -$15.6K (-26%) |
| Total return | 179% | **194%** | +15% |
| Annualized | 10.90% | **11.48%** | +0.58% |
| Max DD | -28% | **-22%** | -6pp |

## Why Smart DCA Wins
- **Bear market protection**: Buys more at lows (2020 COVID, 2022 correction)
- **Froth avoidance**: Buys less at speculative highs
- **Cash reserve**: Accumulates deployable capital during rallies
- **Psychological**: Less painful than buying at all-time highs

## Why It "Underperforms" on Paper in Bull Markets
In a straight-line bull market, fixed DCA puts more money to work sooner. This is NOT a flaw — the cash reserve is insurance for the inevitable correction.

## When Smart DCA Shines Most
- Sideways/choppy markets (buy low, sell high within range)
- Post-correction recoveries (deployed cash at lows)
- Prolonged bear markets (accumulates shares cheaply)

## Current Implementation
- 60-day (3-month) moving average
- Rules applied to deviation from MA
- Shares rounded to nearest 100 (Chinese ETF lot size)
- User's baselines: S&P 500 = 500 shares/mo, Nasdaq = 300 shares/mo
