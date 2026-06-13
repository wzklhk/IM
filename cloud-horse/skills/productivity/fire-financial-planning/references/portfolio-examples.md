# Portfolio Construction Reference

## A High-Quality 5-Fund Chinese FIRE Portfolio

This session's user had an excellent fund selection that works well for a 27-year-old targeting FIRE:

| ETF | Code | Allocation | Role |
|-----|------|-----------|------|
| 沪深300 | 510300 | 25% | A-share core (blue chips) |
| 中证500 | 510500 | 15% | A-share mid-cap growth |
| 标普500 | 513500/159612 | 20% | US large-cap core |
| 纳斯达克100 | 513100/159632 | 20% | US tech/growth |
| 红利低波 | 512890/563020 | 20% | Defensive/dividend anchor |

### Why This Works
- **A-share diversification**: 沪深300 (large) + 中证500 (mid) covers 800 largest A-shares
- **US global exposure**: 标普500 (broad US) + 纳指100 (tech growth) = 40% international
- **Defense**: 红利低波 provides dividend income and lower volatility in bear markets
- **Simple to maintain**: 5 funds, monthly DCA, rebalance annually

### Real DCA Schedule (from session 2026-05-12)

For a user with ~15K/mo available for ETF DCA (after expenses, 27-year-old in Xi'an):

| ETF | Code | Monthly | % |
|-----|------|---------|---|
| 沪深300 | 510300 | 3,500 | 23% |
| 中证500 | 510500 | 2,000 | 13% |
| 标普500 | 513500/159612 | 3,000 | 20% |
| 纳斯达克100 | 513100/159632 | 3,000 | 20% |
| 红利低波 | 512890/563020 | 2,000 | 13% |
| Flexible reserve | — | 1,500 | 11% |
| **Total** | | **15,000** | **100%** |

This allocation provides: A-share coverage (沪深300 + 中证500 = 36%), US market exposure (标普500 + 纳指100 = 40%), and a defensive anchor (红利低波 = 13%). Keep 11% flexible for buying dips.

## Common User Pitfalls

1. **"I already have a portfolio"** — Many users already hold funds/ETFs. Diagnose their allocation before suggesting changes. The most common mistake is being too conservative (90% bonds at age 27).

2. **"QDII限购怎么办"** — Always suggest 场内ETF as the bypass. 场外QDII daily limits (¥10-100) make them useless for accumulation. Provide specific ticker codes for the 场内 version.

3. **"月存1万太夸张"** — Don't suggest aspirational savings rates. Calculate from their real after-tax income and expense breakdown. 50-60% savings rate is already excellent.

4. **"我出差的时候"** — When users mention overseas work: during travel their expenses drop to near-zero and income spikes. This is the most powerful FIRE accelerator for Chinese professionals. Quantify both scenarios (normal month vs travel month) in the projection.

5. **"基金的選擇我不需要改"** — When users already have a well-chosen portfolio (this user had 沪深300 + 中证500 + 标普500 + 纳指100 + 红利低波), **don't suggest changing the fund selection**. The issue is almost always *allocation percentage* (e.g. only 10% in equities), not the fund choices themselves. Compliment their selection before diagnosing the real problem.

6. **"按我的实际收入算"** — Always compute from actual after-tax income and real expense breakdown. Don't use round numbers like "月存1万" unless the user confirms it. Use `execute_code` to produce real projections with their actual numbers.
