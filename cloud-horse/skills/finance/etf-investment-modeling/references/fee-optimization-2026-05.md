# 20-Fund Complete Fee Analysis (2026-05-29)

## Results Summary

Total portfolio: ¥226K, 20 funds across QDII/A股/固收/中概

### Fee Rankings (管理费+托管费+销售服务费, lowest to highest)

| Tier | Funds | Fee Range |
|------|-------|:---------:|
| ⭐ Best | 110020, 007028, 009051 | **0.20%** |
| ⭐ Great | 002758 (0.35%), 016644/003547 (0.40%), 016066 (0.45%) | 0.35-0.45% |
| 🟢 Good | 161130 (0.60%), 013075 (0.25%) | 0.25-0.60% |
| 🟡 OK | 008592/012951/006327/040046/050025 | 0.65-0.80% |
| 🟠 High | 270042, 161125 | 1.00% |
| 🔴 Avoid | 006020 (1.20%), 270023/164906/013853 | **1.20-1.40%** |

### Optimization Targets Identified

| Fund | Fee | Issue | 10yr Cost | Action |
|------|:---:|-------|:---------:|--------|
| 164906 交银海外中国 | 1.40% | -21% loss + no DCA | ¥489 | Stop-loss/ignore (¥2K) |
| 270023 广发全球精选 | 1.40% | Active mgmt | ¥4,273 | Stop DCA, hold |
| 013853 大成匠心卓越 | 1.40% | 3yr lock | ¥10,425 | Stop DCA, hold till unlock |
| 006020 广发增强 | 1.20% | 6× fee of 110020 | ¥38 | Sell → 110020 |
| 006327 中概互联 | 0.80% | Loss position | ¥1,221 | Stop DCA, hold |

### Best-in-Class Replacements

| Current | → Replace with | Fee Delta | 10yr Save |
|---------|---------------|:---------:|:---------:|
| 270023 (1.40%) | 161130 (0.60%) | -0.80% | ¥2,560 |
| 006020 (1.20%) | 110020 (0.20%) | -1.00% | ¥35 |
| 013853 (1.40%) | 110020 (0.20%) | -1.20% | ¥5,500 |

## Scraping Script

The complete scraping script is at `scripts/scrape_fund_fees.py`. It:
1. Loads fund codes from East Money fundcode_search.js
2. Batch-fetches fee pages from `fundf10.eastmoney.com/jjfl_{CODE}.html`
3. Parses: 申购费/赎回费/管理费/托管费/销售服务费
4. Outputs JSON to `/tmp/fund_full_fees.json`

Key regex patterns:
- 管理费: `管理费率</td><td[^>]*>([^<]+)</td>`
- 托管费: `托管费率</td><td[^>]*>([^<]+)</td>`
- 销售服务费: `销售服务费率</td><td[^>]*>([^<]+)</td>`
- 赎回费 tiers: `<td[^>]*>([^<]*\d+[天月年][^<]*)</td>\s*<td[^>]*>([\d.]+%?)</td>`
- 申购费折扣: `(\d+[万]?\s*.+\d+[万]?).*?</td>\s*<td[^>]*>([\d.]+%?).*?</td>\s*<td[^>]*>([\d.]+%?).*?</td>`

### 10-Year Cost Model

Formula for DCA funds:
```python
for year in range(1, years+1):
    annual_contrib = freq_amount * periods_per_year
    total_contrib += annual_contrib
    avg_balance = total_contrib * (1 + r) ** ((year - 0.5) / years)
    total_op_cost += avg_balance * op_fee_rate

end_value = holdings * (1+r)^years + Σ annual_contrib * (1+r)^(years-yr)
```

Assumptions: QDII 10%/yr, A股 8%/yr, 固收 3%/yr, 货币 2%/yr

## Final State (After Optimization)

6 active DCA plans (all low-fee ≤1.0%):
- Daily: 270042 + 040046 (¥20 total, Nasdaq)
-周三: 110020 + 007028 + 009051 + 016066

5 stopped DCA (holdings kept):
- 270023, 013853, 006327, 164906, 006020

Monthly DCA: ~¥5,600-7,600
10yr projected end value: ~¥1,673,000
10yr total fee: ~¥36,000 (2.2% of end value)
Optimization saves: ~¥10,000 over 10 years

Portfolio report: `~/workspace/QuantBase/portfolio/report-2026-05-optimized.md`
Weekly reconciliation cron: `686d74023f4c` (Mondays 09:00 CST)
