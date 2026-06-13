# QuantBase Project — Full Implementation

GitHub: `github.com/wzklhk/QuantBase`
Token: `QUANT_BASE_GITHUB_TOKEN` in `~/.hermes/.env`

## Project Layout

```
~/workspace/QuantBase/
├── data/processed/market_db.json   # 77,728 records (committed to Git)
├── models/
│   └── smart_dca.py                # Strategy engine (imported by daily_report.py)
├── reports/daily/
│   ├── YYYY-MM-DD_report.md        # Daily Markdown report
│   └── YYYY-MM-DD_rec.json         # Daily recommendation JSON
├── scripts/
│   ├── etf_db.py                   # Database manager
│   └── daily_etf_report.py         # Legacy wrapper (keep for reference)
├── daily_report.py                 # Cron entry point (copied to ~/.hermes/scripts/)
├── config.yaml                     # DCA rules, baseline shares
├── README.md
└── .gitignore
```

## Key Files

### daily_report.py (cron entry point)

Imports and workflow:
1. Load `QUANT_BASE_GITHUB_TOKEN` from `~/.hermes/.env`
2. Import `yaml`, `smart_dca`, `etf_db`
3. Load DB → update DB → save DB → generate report → save report → git push

### config.yaml (strategy parameters)

```yaml
smart_dca:
  sp500_base_shares: 500
  nasdaq_base_shares: 300
  ma_period: 60
  rules:
    - {min_dev: 15, mult: 0.4, label: "极度高估"}
    - {min_dev: 10, mult: 0.5, label: "显著偏高"}
    - {min_dev: 5, mult: 0.7, label: "偏高"}
    - {min_dev: 2, mult: 0.85, label: "略高"}
    - {min_dev: -2, mult: 1.0, label: "正常"}
    - {min_dev: -5, mult: 1.15, label: "略低"}
    - {min_dev: -10, mult: 1.3, label: "偏低"}
    - {min_dev: -999, mult: 1.5, label: "显著低估"}
```

## Cron Job

```yaml
name: quantbase-daily-report
schedule: "0 9 * * 1-5"      # 09:00 VPS (UTC+8) = 07:00 user (UTC+6)
script: daily_report.py
deliver: weixin
no_agent: true
workdir: /home/agentuser/workspace/QuantBase
```

The `daily_report.py` in `~/.hermes/scripts/` must stay in sync with the QuantBase repo version.

## Token Setup

```bash
# ~/.hermes/.env
QUANT_BASE_GITHUB_TOKEN=github_pat_xxx
```

## Full Data Coverage

| Market | Index/ETF | Records | Earliest |
|--------|-----------|---------|----------|
| US | S&P 500 | 2,515 | 2016-05-23 |
| US | Nasdaq 100 | 10,179 | 1986-01-02 |
| US | 道琼斯 | 2,512 | 2016-05-27 |
| US | VIX波动率 | 9,194 | 1990-01-02 |
| CN | 上证指数 | 8,475 | 2004-01-02 |
| CN | 深证成指 | 8,116 | 2009-12-04 |
| CN | 沪深300 | 5,133 | 2012-01-04 |
| CN | 中证500 | 4,704 | 2012-01-04 |
| CN | 创业板指 | 3,879 | 2017-01-03 |
| CN | 科创50 | 1,549 | 2019-12-31 |
| HK | 恒生指数 | 9,256 | 2010-03-01 |
| HK | 恒生科技 | 1,434 | 2020-07-27 |
| HK | 恒生国企 | 8,102 | 2010-02-24 |
