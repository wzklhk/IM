---
created: 2026-05-10
tags: [project, backup]
status: completed
completed_at: 2026-05-10
---

# Hermes 自动备份

## 概述
将 `~/.hermes` 配置和技能文件每日自动备份到 GitHub 私有仓库。

## 架构决策

| 决策 | 选择 | 原因 |
|------|------|------|
| 备份时机 | 每日 3:00 AM | 低负载时段，不影响工作 |
| 存储仓库 | GitHub 私库 `my-hermes-backup` | 与 GitHub 生态整合 |
| 认证方式 | `GITHUB_TOKEN` + `GIT_ASKPASS` | 解决 `.env` 保护机制 |
| 排除项 | cache, logs, sessions, venv | 可重新生成，节省空间 |
| 传输方式 | `rsync` + `git push --force` | 干净快照，不保留历史冲突 |

## 关键文件
- `~/.hermes/backup-hermes.sh` — 主备份脚本
- `~/.hermes/scripts/load-github-token.sh` — token 加载助手

## 监控
- 通过 cron dashboard: `hermes cron list`
- 手动触发: `hermes cron run 60ebda1c7bf8`
