# ETF Tracker Development Session (2026-05-28)

## Context
User wanted daily tracking of all Chinese-listed US index QDII ETFs (S&P 500 + Nasdaq 100) and a quantitative DCA investment model. They currently DCA 500 shares of 513650 (南方标普500ETF) on the 20th of each month.

## Key Decisions

### Data Source
East Money API (akshare) was blocked from the VPS (`Connection aborted`). Switched to Tencent Finance API (`qt.gtimg.cn`) which has <500ms response time from the same VPS.

### Exchange Prefix Discovery
- Shanghai ETFs (51xx codes) need `sh` prefix
- Shenzhen ETFs (15xx/16xx codes) need `sz` prefix  
- Initial bug: all codes were sent as `sz`, so 51xx ETFs returned no data

### Cron Schedule
Market close is 15:00 CST. Script runs at 15:15 CST (Mon-Fri) to ensure all data is settled.

### ETF Selection
11 total funds tracked:
- S&P 500: 513650 (user's holding), 513500 (largest)
- Nasdaq 100: 9 funds - 159941 (广发, most liquid at ¥21B/day) recommended as primary

## Files Created
- `~/.hermes/scripts/etf_tracker.py` — Daily tracker
- `~/.hermes/etf_tracker_history.json` — Price history accumulator
- Cron: `etf-market-tracker-daily` — 15:15 Mon-Fri, deliver to WeChat
