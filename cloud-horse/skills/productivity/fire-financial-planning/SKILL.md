---
name: fire-financial-planning
description: Financial Independence Retire Early (FIRE) planning — assess user's situation, compute FIRE number, recommend investment strategy for Chinese users. Covers 4% rule, asset allocation, tax optimization, and milestone tracking.
---

# FIRE Financial Planning

## Core Framework

### The 4% Rule (Trinity Study)
> **FIRE Number = Annual Expenses × 25**

| Monthly Spend | Annual Spend | FIRE Number |
|--------------|-------------|------------|
| ¥5,000 | ¥60,000 | ¥1,500,000 |
| ¥10,000 | ¥120,000 | ¥3,000,000 |
| ¥15,000 | ¥180,000 | ¥4,500,000 |
| ¥20,000 | ¥240,000 | ¥6,000,000 |
| ¥30,000 | ¥360,000 | ¥9,000,000 |

**Lean FIRE**: ¥3,000-8,000/mo → target ¥900K-2.4M
**Coast FIRE**: ¥8,000-15,000/mo → target ¥2.4M-4.5M (passive income covers part of expenses; keep working but on your own terms)
**Fat FIRE**: ¥15,000-30,000+/mo → target ¥4.5M-9M+

### Coast FIRE / Barista FIRE — The Career Transition Variant

This is the most common FIRE variant for mid-career professionals in China, but the least documented. The idea:

> **Not retiring early — buying the freedom to do work you actually like.**

| Traditional FIRE | Coast/Barista FIRE |
|-----------------|-------------------|
| Stop working entirely at 40 | Keep working, but on your own terms |
| Need 100% of expenses covered by portfolio | Portfolio covers a meaningful fraction (~30-50%) |
| Target: ¥3-9M+ | Target: ¥500K-2M (far more achievable) |
| Requires maxing savings rate for 10-15 years | Achievable in 3-5 years of aggressive saving |

**Key insight for career changers:** Even a small portfolio that covers ¥3K-5K/month dramatically changes the calculus. Salary drops from 50万→30万 are no longer scary when ¥3K/month of passive income fills the gap.

**Common profile (real session data):**
- Overseas telecom engineer, 27, ~80万 saved
- Wants to switch from overseas delivery to AI engineering in Guangzhou
- Can't afford to just quit, but 100万 plus a mid-range AI job = comfortable transition
- "咖啡师FIRE" — works an AI job he enjoys, portfolio covers the gap if salary is lower, and the low-stress job provides purpose + income to keep building wealth

**How to frame it:**
> "你不是在放弃高薪去赌转型，而是在用存款买一张'做自己喜欢的事'的门票。"

**Decision framework for career-transition FIRE:**

```
Current savings: ~80万 (example)
Monthly expenses (target city, couple): ~7K
4% withdrawal: ~2.7K/month
Gap to cover by new-job salary: ~4.3K/month

If new AI job pays 25万/year (21K/mo take-home):
  After covering gap: 21K - 4.3K = 16.7K left
  Still saving ~12K/month!
  
⇨ The portfolio isn't "retirement" — it's a safety net that lets you take
  a lower-paying job without stress, while still building wealth.
```

### Safety adjustments for China
- Chinese inflation rate tends higher than US → consider **3.5% rule** (×28.6) instead of 4%
- Medical costs rising fast → add ¥300-500K buffer for health emergencies
- No 401(k)/IRA equivalent → use index funds + commercial pension insurance
- Housing is the biggest variable → if rent-free, reduce FIRE number by 30-40%

## User Intake Questions

Before planning, **always** ask. Never assume their savings capacity — "月存1万" is not realistic for most:

1. **Age?** → determines time horizon, risk tolerance, compound runway
2. **Current monthly/yearly income?** Be specific: base salary vs bonus vs overseas/per-diem income
3. **Current savings & assets?** (deposits, stocks, funds, real estate, crypto, etc.) Also note the *allocation breakdown* (e.g. "% in bonds vs equities")
4. **Current monthly expenses?** (the most important number — get real, not aspirational. Break down by category.)
5. **Target retirement monthly spend?** (what lifestyle do they want in retirement)
6. **Investment experience & current holdings?** (stocks? index funds? 场内ETF? QDII? real estate? never invested?)
7. **City tier?** (一线/二线/三线 → huge cost-of-living difference)
8. **Debts?** (mortgage, car loan, consumer loan — these delay FIRE significantly)
9. **Any overseas income or per-diem?** (出差补贴、海外津贴 — these can dramatically accelerate savings because expenses drop to near-zero during travel)
10. **What funds/ETFs do they already hold?** (不假设从零开始 — many users already have a portfolio; diagnose allocation, not just lack of it)

### User Preference: Agent-Trusted Decision Making

When a user says "还是用你这样的agent做决策好" or similar expressions of trust in agent-driven decisions:
- They want the agent to **own** the financial plan, not just give advice
- Capture ALL decisions and context in memory (income, expenses, holdings, FIRE target, timeline)
- Offer to build persistent artifacts: Obsidian vault entries, tracking sheets, milestone checkpoints
- **Always** offer to store the plan in their knowledge base (Obsidian vault) after completing analysis
- When they return in a future session, memory should contain enough context to resume mid-conversation without re-asking everything

### CRITICAL: Never Assume Savings Capacity

**DO NOT default to "月存1万" or any other savings amount.** The savings-rate/user-finances section exists precisely because this number varies wildly by income.

When the user hasn't provided their full income/expense picture:
- Ask about base salary AND overseas/per-diem income separately — many Chinese engineers in telecom have both
- Calculate take-home pay (social insurance, tax) based on their city
- Calculate savings = take-home minus realistic expenses
- Always ask "你的月收入税前多少" before assuming a savings number
- If they corrected you once on this, the correction goes in the SKILL.md body, not just in memory

**Example of what NOT to do:**
```
月投1万有点夸张吧，我的月收入税前才17
```

The agent proposed 月存1万 without knowing the user's actual income. Always ask first.

### "Overseas Income Accelerator" Detection

When the user mentions per-diem, overseas assignments, or "出差补贴":
- Immediately calculate both scenarios (base-only and with-overseas)
- Explain the dramatic difference — savings rate can jump from 50% → 80%+
- Advise: during travel, spending drops to near-zero; full per-diem = pure savings
- If possible, suggest opening an overseas brokerage account during travel
## Investment Strategy (by Risk Tolerance)

### Conservative (低风险)
- **70%** bond funds / money market / bank deposits (2-3% yield)
- **20%** index ETF (沪深300: 510300, 标普500: 513500)
- **10%** gold ETF (518880)
- Expected return: 3-5%/yr
- Best for: age 50+, FIRE within 3-5 years

### Balanced (中等风险 — recommended for most)
- **50%** index ETF (沪深300 30% + 标普500 20%)
- **20%** bond funds
- **15%** industry ETF (消费/科技/医药)
- **10%** gold or REIT ETF
- **5%** cash / money market
- Expected return: 6-9%/yr
- Best for: age 30-50, FIRE within 5-15 years

### Aggressive (高风险)
- **70%** equity ETFs (A股 40% + 美股 30%)
- **15%** crypto / venture capital (high risk, high reward)
- **10%** individual stocks
- **5%** cash
- Expected return: 8-12%/yr (with -30% drawdown risk)
- Best for: age <30, long runway, high risk tolerance

## Compound Growth Tables

Use to show the power of time:

### ¥5,000/mo invested (¥60K/yr) at various returns

| Years | 5% | 8% | 10% |
|-------|----|----|-----|
| 5 | ¥340K | ¥367K | ¥386K |
| 10 | ¥775K | ¥900K | ¥1.0M |
| 15 | ¥1.34M | ¥1.73M | ¥2.1M |
| 20 | ¥2.07M | ¥2.96M | ¥3.8M |
| 25 | ¥3.0M | ¥4.7M | ¥6.5M |

### ¥10,000/mo invested (¥120K/yr) at various returns

| Years | 5% | 8% | 10% |
|-------|----|----|-----|
| 5 | ¥681K | ¥735K | ¥773K |
| 10 | ¥1.55M | ¥1.8M | ¥2.0M |
| 15 | ¥2.68M | ¥3.47M | ¥4.2M |
| 20 | ¥4.14M | ¥5.93M | ¥7.6M |
| 25 | ¥6.0M | ¥9.4M | ¥13.0M |

### Share-Based DCA (Fixed Shares, Not Fixed Amount)

Many Chinese retail investors buy in **fixed share quantities** (每月500股) rather than fixed monetary amounts (每月1,000元). This is a valid behavioral pattern:

```
Fixed shares (500股/月):  
  Market up → spends more CNY each month (buys at higher prices) ❌ 
  Market down → spends less CNY (buys at lower prices) ✅
  → Semi-automatically does the "buy low, buy less high" thing
  
Fixed amount (1,000元/月):
  Market up → buys fewer shares
  Market down → buys more shares
  → Pure value averaging, academically "optimal"
```

For users who prefer share-based DCA, offer a **rules-based enhancement** that corrects the natural downside:

| Condition | Shares to Buy |
|-----------|--------------|
| Index dropped >10% this month | 2× normal shares |
| Index dropped 5-10% | 1.5× normal shares |
| Index ±5% | 1× normal (base) |
| Index rose >5% | 0.6× normal |
| 3+ consecutive drops | Add 0.4× on top |
| 3+ consecutive rises | Reduce by 0.4× |

This transforms "buying fixed shares" from a passive habit into a quantified strategy with lower average cost.

### 场外QDII Purchase Limits — Confirmed Real Numbers

Many QDII ETFs (especially US-market ones like 标普500/纳斯达克100) have **severe daily purchase limits** on 场外 (over-the-counter) platforms:
- Common limit: **¥10-100/day per fund** — effectively unusable for serious accumulation
- This is a real constraint, not a theoretical one. Many users give up on US market exposure because of this.
- Multiple users have confirmed **as low as ¥10/day** for popular QDII funds (南方标普500, 广发纳指100 etc.), making even ¥3,000/month impossible without opening a brokerage account.
- **Always verify the actual daily limit before recommending a DCA plan** — don't assume the fund can be bought in bulk.

**Workaround: 场内ETF (Exchange-Traded)**
- Same fund, traded on exchange like stocks — **no purchase limits**
- Just need a 证券账户 (brokerage account) to buy
- Key advantages: real-time pricing, no limit, lower fees, faster settlement
- Examples: 513500 (标普500), 513100 (纳斯达克100) traded on exchange
- **Always recommend 场内ETF when a user mentions QDII purchase limits**

### QDII Purchase Limits — Critical Real-World Constraint

Many QDII ETFs (especially US-market ones like 标普500/纳斯达克100) have **severe daily purchase limits** on 场外 (over-the-counter) platforms:
- Common limit: **¥10-100/day per fund** — effectively unusable for serious accumulation
- This is a real constraint, not a theoretical one. Many users give up on US market exposure because of this.

**Workaround: 场内ETF (Exchange-Traded)**
- Same fund, traded on exchange like stocks — **no purchase limits**
- Just need a 证券账户 (brokerage account) to buy
- Key advantages: real-time pricing, no limit, lower fees, faster settlement
- Examples: 513500 (标普500), 513100 (纳斯达克100) traded on exchange
- **Always recommend 场内ETF when a user mentions QDII purchase limits**

### Overseas Income / 出差补贴 as FIRE Accelerator

When a user has overseas per-diem or 出差补贴:
- During travel: company covers food/accommodation → user spending drops to near-zero
- Full per-diem becomes pure savings → savings rate can jump from 50% → 80%+
- Example: 17K monthly base → ~7K/mo savings; 40K annualized (with overseas) → ~27K/mo savings
- If possible: open an overseas brokerage account during travel to buy VOO/QQQ directly (no QDII markup, no limit)

### Assumption Validation Protocol — Prevent the "Cascade Correction"

**When the user corrects one assumption, others are likely wrong too.** Don't just fix that one number and continue — re-validate the full scenario.

**Before any financial analysis, state your working assumptions explicitly and ask for confirmation:**

```
My assumptions for this analysis:
1. You're single, no dependents
2. Annual Xi'an stay: 1 month
3. Marginal tax rate: 10%
4. Current rent: 850/mo
5. Timeframe: 3-5 years before settling
→ Can you confirm all of these, or are any wrong?
```

**Failure mode (what happened this session):**
- Agent assumed 20% tax rate → corrected to 10%
- Agent assumed 850 rent → corrected to 1000 → back to 850
- Agent assumed "single, annual visitor" → "settling someday" → "有对象准备结婚了"
- Each correction invalidated the entire previous analysis, wasting 3+ rounds of calculation

**🛑 Life Change = Full Reboot:** When the user reveals a major life event mid-conversation (结婚/分手/换工作/搬家/生娃/父母生病), the current analysis is **stale**. Say: "这个信息改变了前提，之前的分析作废，我重新算。" Then restart from intake.

### Life-Stage Housing Decision Tree (for Chinese Tier-2 Cities)

This decision tree covers the common scenario of a Chinese professional considering when/where to buy in a tier-2 city like Xi'an.

#### Stage 0: Quick Situation Mapper

Before recommending anything, determine:

```
Current residence: 常驻地 vs 老家 vs 外派
Partner status: 单身 / 有对象(未婚) / 已婚/ 已婚已有娃
Return timeline: 1年内 / 1-3年 / 3-5年 / 不确定
Current housing: 租房 / 有房 / 住公司
Budget for purchase: 30万 / 50万 / 80万 / 100万+
```

#### Key 西安-Specific Facts

- **30万可以买到什么样的住宅？** 30-40㎡老破小一室一厅（70年产权），主要在碑林/莲湖/雁塔的老小区，80-90年代的国企家属院。远郊可到40-50㎡
- **落户条件：** 西安已取消面积限制（2019年起），买住宅≥一套即可落户。**必须确认是70年产权住宅，不是40年公寓**
- **买进成本：** 契税1% + 中介费1-2% ≈ 6,000-9,000（30万标的）
- **持有成本（空置）：** 物业暖气≈3,000/年 + 机会成本≈15,000/年（30万×5%）≈ **18,000/年**
- **卖出成本：** 中介1-2% + 个税1%（满五唯一免）+ 增值税5.6%（满二免）≈ 6,000-15,000

#### 迷你仓 (Self-Storage) as Alternative

For users who are away most of the year (外派/出差):

| 面积 | 适合 | 月租（西安） |
|:---|:---|:---:|
| 1㎡ | 行李箱+纸箱 | 80-150元 |
| 3㎡ | 一室一厅家具 | 200-400元 |
| 5㎡ | 两室家具 | 400-700元 |

平台：迷你考拉仓、大众迷你仓、万物仓（西安主城区有覆盖）

#### Decision Matrix: 租房 vs 迷你仓 vs 酒店 vs 买房

For a user who spends ~1 month/year in the target city:

| 方案 | 年净成本 | 灵活度 | 有落脚点 | 适合阶段 |
|:---|:---:|:---:|:---:|:---:|
| 续租 | ~8,400-10,200 | ❌ | ✅ | 愿意付空置费 |
| 迷你仓+住酒店 | ~9,600 | ✅ | ⚠️临时 | 过渡期最优 |
| 纯住酒店 | ~4,500-7,500 | ✅✅ | ❌ | 纯省钱模式 |
| 买30w老破小 | ~18,000 | ❌❌ | ✅ | 确定定居+落户需求 |

#### 🚨 "有对象准备结婚了" — Full Reboot Signal

If the user reveals they are getting married (or in a serious relationship) during a housing conversation:
1. **Immediately discard previous solo analysis** — no partial re-use
2. Re-map household income (two people), household timeline, household space needs
3. 30㎡ is a temporary fit for a couple but impractical for future children
4. Recommend: rent first, then buy a proper 2BR (60-80㎡) together after settling
5. Buying a 30w 老破小 solo now, then selling/upgrading in 3 years incurs 2-3万 in transaction costs — usually not worth it

#### Tax Deduction Clarification

**Common mistake (user correction):** 住房租金专项附加扣除 1,500元/月 (西安标准) is **免税额 (taxable income deduction)**, not a **直接减税 (tax credit)**. 

- At 10% marginal rate: actual saving = 1,500 × 10% × 12 = **1,800元/年**
- At 20% marginal rate: actual saving = 1,500 × 20% × 12 = **3,600元/年**
- **Always state: "这1500是抵扣应纳税所得额，不是直接减1500的税"**

### Savings Rate Principle

For FIRE planning in China (lower wages vs global markets), **savings rate dominates investment returns** in the first 5-7 years:
- At low asset base (~100万), adding ¥5K/mo more impact than chasing 2% extra yield
- Always optimize savings rate before optimizing allocation
- Rule of thumb: savings rate % ≈ years-to-FIRE compression factor
  - 50% savings → ~17 years to FIRE
  - 70% savings → ~9 years
  - 80% savings → ~5-7 years

### Tax & Investment Vehicles
- **A-share stocks**: 0.1% stamp duty (sell side), 20% capital gains tax if held <1yr
- **Index ETF**: no stamp duty, 20% cap gains if held <1yr → prefer for long-term
- **Hong Kong Stock Connect**: no stamp duty for mainland investors (yet)
- **QDII funds**: only way to invest in US market from China (e.g., 513500 标普500ETF)
- **Commercial pension insurance**: tax-deferred up to ¥12,000/yr (个人养老金账户)

### Key ETF codes for Chinese investors
- 510300 — 沪深300 ETF (largest A-share index)
- 510050 — 上证50 ETF (blue chips)
- 159915 — 创业板 ETF (growth tech)
- **513500 / 159612** — 标普500 ETF (US market — use 场内 to bypass QDII limit)
- **513100 / 159632** — 纳指100 ETF (NASDAQ — use 场内 to bypass QDII limit)
- 518880 — 黄金ETF (gold)
- 512100 — 中证1000 ETF (small cap)
- **512890 / 563020** — 红利低波 ETF (dividend + low volatility, good for defensive allocation)
- 510500 — 中证500 ETF (mid-cap growth)

### Provident Fund (公积金)
- Can be withdrawn for: buying house, renovation, rent, retirement
- FIRE planners should consider early withdrawal strategies
- Typically 5-12% of salary matched by employer → significant forced savings

## Milestone Tracking

| Phase | Target | Action |
|-------|--------|--------|
| 🏁 Start | 0-10% of FIRE number | Build emergency fund (6mo expenses), clear high-interest debt |
| 🚀 Build | 10-50% | DCA into ETFs monthly, increase savings rate to 40-60% |
| 🛡️ Coast | 50-80% | De-risk allocation, add bond/cash, consider part-time work |
| 🎯 Home | 80-100% | Shift to conservative, plan withdrawal strategy |
| 🏖️ FIRE | 100%+ | 4% withdrawals, review quarterly, adjust for inflation |

## Knowledge Base Integration (Obsidian Vault)

After delivering the FIRE plan, **always** offer to build persistent tracking artifacts in the user's Obsidian vault (if they have one). Do not skip this step — it turns a one-time conversation into a living plan the user can maintain.

### Suggested Vault Structure

```
Areas/
├── FIRE/
│   ├── MOC.md                     ← Main navigation + current task list
│   ├── net-worth-tracking.md      ← Monthly net worth table + progress bar
│   ├── fire-plan.md               ← Phase roadmap with milestones
│   ├── portfolio-strategy.md      ← Asset allocation + ETF DCA plan
│   └── lessons-learned.md         ← Investment journal
```

### Key Content Per Module

- **MOC.md**: Current net worth, target, progress %, links to all sub-pages
- **net-worth-tracking.md**: Table with monthly rows (date, total, equity%, bond%, saved this month, notes). FIRE progress bar (e.g. `██████░░░ 60%`). Milestone checkboxes.
- **fire-plan.md**: 3-phase plan (accumulation → transition → FIRE), core assumptions table (rate, inflation, target), investment principles
- **portfolio-strategy.md**: ETF list with codes, monthly DCA amounts, rebalancing rules, forbidden actions list
- **lessons-learned.md**: Empty template for the user to fill as they go

### Vault Update Checklist

- [ ] Add FIRE to Home.md as a featured section
- [ ] Add FIRE to Areas/MOC.md
- [ ] Create the FIRE directory with all files
- [ ] Create Templates/Daily Note.md (optional — for daily expense tracking)

## Withdrawal Strategy (Post-FIRE)

- **Bucket strategy**: 1yr cash + 3yr bonds + rest in equities
- **Variable withdrawal**: tighten belt in down years (-15%+), spend more in up years
- **Part-time / side income**: even ¥2-3K/mo reduces sequence-of-returns risk dramatically
- **Healthcare**: maintain basic social insurance (医保) — don't drop it
- **Re-balance**: annually, back to target allocation

## Reference Files

- `references/compound-calculations.md` — Compound interest tables for common scenarios
- `references/portfolio-examples.md` — Sample portfolios by risk level with Chinese ETFs
- `references/real-income-scenarios.md` — Full session data: 西安/17K base/40K overseas/100万 start/5-fund portfolio/DCA plan. Includes both base-salary-only and overseas-income scenarios with FIRE timeline comparisons. Reference this anytime a user has overseas per-diem income or a similar savings profile.
- `references/career-transition-coast-fire.md` — Career switch + coast FIRE worked example: overseas telecom engineer (80万, 27, 广州), 广积粮 strategy, 3-agent workflow, Nepal-to-Guangzhou timeline.
- `references/sp500-quantified-dca.md` — Rules-based quantified DCA system for single-ETF investors who buy fixed shares instead of fixed CNY. Validated with real user (513500 标普500, 500股 base, 每月10号).

## Pitfalls

- **Underestimating inflation**: Chinese CPI is reported at 2-3%, but real inflation (housing, education, medical) is 5-8%. Always add a buffer.
- **Sequence of returns risk**: if the market crashes right after you FIRE and you keep withdrawing, you run out of money sooner. Mitigate with 2-3 years of cash buffer.
- **Ignoring social insurance**: voluntary contribution to 医保 and 养老保险 is cheap insurance even after FIRE.
- **Treating crypto as a retirement plan**: crypto is entertainment, not a FIRE strategy. Never allocate more than 10%.
- **One-child pressure**: Chinese FIRE planners may need to support aging parents — budget for this.
- **Assuming "100万" is a FIRE number**: Many Chinese professionals target ¥1M as "enough to FIRE" because it's a round psychological milestone. In reality, ¥1M generates only ¥30-40K/year (¥2.5-3.3K/month) at 3-4% — not enough for any Chinese city. Correct framing: **¥1M is a safety-net number, not a retirement number.** It buys you 1-2 years of no-income runway or the confidence to take a lower-paying job you actually enjoy (Coast FIRE). Don't let the user think they can stop working at ¥1M unless they live in a very low-cost area with no dependents.
- **Confusing "存到100万就fire" with literal full retirement**: When a user says this, probe: do they mean full retirement or career-transition safety cushion? The latter is much more achievable and common for Chinese professionals. If it's the latter, frame it as "广积粮" (accumulate grain) — one more overseas contract to hit ¥1M, then transition to a job you like without financial pressure. See `references/career-transition-coast-fire.md` for a worked example.
