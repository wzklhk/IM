---
created: 2026-05-10
tags: [devops, automation, backup]
related: [Areas/Development/hermes-agent]
---

# 自动化与定时任务

## Hermes 自动备份（hot）

### 概述
每日 3:00 AM 自动备份 `~/.hermes` 到 GitHub 私有仓库 `wzklhk/my-hermes-backup`。

### 备份脚本
- 文件: `~/.hermes/backup-hermes.sh`
- cron job 名称: `hermes-auto-backup`
- cron job ID: `60ebda1c7bf8`

### 备份内容
- 所有配置文件 (`config.yaml`, `.env`)
- 所有技能文件 (`~/.hermes/skills/`)
- 排除项: cache, logs, sessions, venv, state db

### 工作原理
```bash
# 1. 加载 GitHub token
source ~/.hermes/scripts/load-github-token.sh

# 2. rsync 快照到临时目录
rsync -a --delete ~/.hermes/ /tmp/hermes-snapshot/
  --exclude cache --exclude logs --exclude sessions --exclude venv

# 3. git commit & push 到私库
cd /tmp/hermes-snapshot && git push origin main
```

### 故障排查
- token 过期 → 更新 `~/.hermes/.env` 中的 `GITHUB_TOKEN`
- git identity → `My Hermes Agent <hermes@bot.local>`
