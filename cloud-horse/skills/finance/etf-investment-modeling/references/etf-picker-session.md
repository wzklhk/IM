# ETF Buy Decision Tool — Implementation Session Notes

Date: 2026-05-28
Script: `~/workspace/QuantBase/scripts/etf_picker.py`
Repo: `github.com/wzklhk/QuantBase`

## What Was Built

A Python script that compares all S&P 500 (or Nasdaq 100) QDII ETFs in real-time and recommends the best one to buy. Integrated into the daily report cron job.

## API Reliability Findings (from China VPS)

### ❌ East Money push2 API (push2.eastmoney.com)
- Fails intermittently from China VPS — returns empty responses outside trading hours
- Even during trading hours, may return `Connection aborted` for some providers
- Not reliable enough for a cron script that needs to work silently

### ✅ Tencent Finance API (qt.gtimg.cn)
- **Always works**, both during and after hours
- Returns GBK-encoded pipe-delimited text
- Even outside trading hours, returns the last close data
- Key fields: position 3 (current price), 4 (prev close), 6 (volume 手), 31 (time), 32 (change%), 33 (high), 34 (low), 37 (amount 万)

### ✅ 天天基金 NAV API (fundgz.1234567.com.cn)
- Always works for fund NAV data
- Returns `jsonpgz({"fundcode":"513650","name":"...","dwjz":"1.8495","gsz":"1.8606","gszzl":"0.60",...})` 
- `dwjz` = latest official NAV (date in `jzrq`), `gsz` = estimated NAV (实时估算)
- `gszzl` = estimated change % based on US futures

## Premium Calculation

```python
# During trading hours (preferred):
premium = (market_price / gs_nav - 1) * 100  # uses estimated NAV

# After hours (fallback):
premium = (market_price / dwjz - 1) * 100  # uses last official NAV
```

**Pitfall**: After-hours premium appears inflated because the NAV is stale (last US close) while the market price may reflect overnight US futures movement. Example: at 03:25 CST with S&P 500 futures up ~0.6%, all 4 ETFs showed +2.3% to +4.0% premium. During trading hours the same premium would be ~0.3-0.8%.

## Scoring Model

```python
score = -premium * 5 + min(volume_yi * 2, 10) - (fee - 0.6) * 10
```

This proved effective in ranking:
1. Low-premium ETFs first (premium heavily penalized)
2. Liquid ETFs preferred but capped at 10 bonus points
3. Fee difference is a small factor (0.15% diff = -1.5 points)

## S&P 500 ETF Sizes (2026-05)

| Code | Scale (亿) | Fee | Daily Vol (亿) | Status |
|------|:---------:|:---:|:-------------:|:------:|
| 513650 南方 | 45.75 | 0.75% | 2.04 | ✅ Normal |
| 513500 博时 | 209.41 | 0.80% | 3.03 | ⚠️ 暂停申购 |
| 159612 国泰 | 7.45 | 0.75% | 0.38 | ⚠️ 暂停申购 |
| 159655 华夏 | 33.86 | 0.75% | TBD | ✅ Normal |

## Instruction for Future Use

When the user asks "which ETF should I buy now?" or "帮我看看现在买哪个划算":

1. Run the script: `cd ~/workspace/QuantBase && python3 scripts/etf_picker.py`
2. If during A-share hours (9:30-15:00 CST), the premium data is reliable
3. If after hours, explain that the premium is inflated and suggest checking again during market hours
4. Show the comparison table and recommend the lowest-premium ETF
5. If the user's current ETF (513650) is not the best choice, recommend switching for this month's purchase
