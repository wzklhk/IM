# Complete Portfolio Schema

## File Location

`~/workspace/QuantBase/data/processed/complete-portfolio-2026-05.json`

## JSON Schema

```json
{
  "meta": {
    "snapshot_date": "2026-05-29",
    "source": "蚂蚁基金-我的持有+投资计划截图",
    "total_holdings": 226030,
    "total_profit": 10600,
    "funds_count": 20,
    "active_plans": 12,
    "paused_plans": 6
  },
  "funds": [
    {
      "code": "270042",
      "name": "广发纳斯达克100ETF联接A",
      "category": "QDII-纳指",
      "holdings": 13002.14,
      "cost": 9350,
      "profit": 3652.14,
      "pct": 39.19,
      "daily_invest": 10,
      "invest_mode": "固定-每日",
      "status_deduct": true,
      "status": "✅进行中",
      "note": ""
    }
  ],
  "summary": {
    "total_holdings": 226030,
    "by_category": {
      "美股QDII": {"holdings": 44044},
      "A股指数": {"holdings": 9179},
      "固收类": {"holdings": 165411},
      "中概+主动": {"holdings": 7396}
    },
    "active_invest_daily": {"daily_total": 20, "monthly_approx": 600},
    "active_invest_weekly": {
      "fixed_weekly": 1150,
      "total_weekly_low": 1410,
      "total_weekly_high": 1960
    }
  }
}
```

## Status Codes

| Status | Meaning |
|--------|---------|
| ✅进行中 | Active — money is being deducted successfully |
| 🚫限购暂停 | Plan exists in app but QDII quota exhausted — no money deducted |
| 持仓闲置 | Held but no active定投 plan |

## Critical Field: `status_deduct`

The `status` field alone can be misleading. A fund can show "进行中" in the app but have `status_deduct: false` because QDII限购 prevents actual execution. This boolean is the source of truth.

## Update Procedure

1. Load `complete-portfolio-2026-05.json`
2. For each fund, fetch latest NAV via `curl -s "https://fundgz.1234567.com.cn/js/CODE.js"`
3. Calculate: `holdings_new = holdings / old_nav * new_nav`; `profit = holdings - cost`
4. If user provides updated cost basis (new定投 added), update `cost` and `daily_invest`
5. Write updated JSON + regenerate Markdown
6. Commit to QuantBase Git repo

## Weekly Reconciliation Cron

- **Job ID**: `686d74023f4c`
- **Schedule**: Every Monday 09:00 CST
- **Deliver**: WeChat/微信
- **Skills**: etf-investment-modeling
- **Action**: Update all 20 fund NAVs, recalculate holdings, generate weekly report
