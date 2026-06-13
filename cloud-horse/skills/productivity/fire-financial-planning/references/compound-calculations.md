# FIRE Compound Calculation Reference

Use `execute_code` with Python to compute FIRE projections. This file documents the approach.

## Core Python Template

```python
capital = 1_000_000          # current assets
annual_save = 84_000         # annual additional savings (e.g. 7K/mo)
annual_return = 0.07         # expected real return (e.g. 7%)
inflation = 0.025            # inflation rate
fire_number = 2_300_000      # target = annual_expense × 25 × inflation_buffer
age_now = 27

v = capital
for y in range(1, 16):
    v = v * (1 + annual_return) + annual_save
    monthly_withdrawal = (v * 0.04) / 12
    # print milestone status
```

## Scenario Comparison Template

When the user has a suboptimal current allocation (e.g. 90% bonds):

```python
# Scenario A: Keep current allocation (e.g. 4% return)
v4 = capital
# Scenario B: Optimized allocation (e.g. 7% return)
v7 = capital

for y in range(1, 30):
    v4 = v4 * 1.04 + annual_save
    v7 = v7 * 1.07 + annual_save
    # Compare when each hits FIRE number
```

## Key Insight: Savings Rate > Returns (early)

At low asset bases (~100万), savings rate dominates:
- ¥5K/mo extra savings ≈ 3% higher effective return
- Always calculate from the user's REAL after-tax income, not aspirational savings
- Use this table for quick visual:

| Monthly Save | Annual Save | Years to ¥230万 @4% | Years to ¥230万 @7% |
|-------------|------------|-------------------|-------------------|
| ¥3,000 | ¥36,000 | 15 | 12 |
| ¥7,000 | ¥84,000 | 10 | 7 |
| ¥15,000 | ¥180,000 | 4 | 4 |

## Expense Breakdown Template (Xi'an example)

```
房租：2000
饮食：1500
交通/通讯：700
社交/娱乐：1000
医疗/保险：500
旅行/备用：500
合计：6200/月
```

Adjust for other cities — down 20% for 三线, up 30% for 一线.
