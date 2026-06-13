---
name: realtime-vault-logging
description: 每次回复时自动写对话 markdown 到知识库 Daily 笔记，并立即推 GitHub。三层保障：即时推 → 6h补漏 → 每天3AM全量兜底。
---

# 实时知识库写入流水线

## 每次回复必做

每次在 WeChat 或其他渠道回复用户后，按以下步骤执行：

### 1️⃣ 写 Daily 笔记

文件路径：`~/workspace/knowledge-vault/Daily/YYYY-MM-DD.md`

格式：
```markdown
# YYYY-MM-DD 周X

## 💬 对话记录

### HH:MM — 对话主题概括
**用户**: 用户说的话...
**郝明瑞**: 我回复的内容摘要...
```

- 如果当天文件已存在，用 `patch` 在文件末尾追加新记录
- 如果当天文件不存在，用 `write_file` 创建
- 每条记录标注时间戳和简短主题
- 保留对话要点，不必逐字记录

### 2️⃣ 立即推 GitHub

调用脚本：
```bash
bash ~/.hermes/scripts/push-vault-now.sh
```

该脚本：
- 维护持久化克隆在 `/tmp/hermes-vault-now/`
- 将 vault .md 文件同步到 `knowledge-vault/` 子目录
- git add + commit + push（3次重试，静默成功）
- 无变更时静默退出，不打扰用户

### 3️⃣ LLM Wiki 同步

关联的 LLM Wiki (`~/workspace/knowledge-vault-llm/`) 会在 push-vault-now.sh 执行成功后自动后台同步。不需要手动调用。

同步脚本：`~/.hermes/scripts/sync-llmwiki.sh`
- 把 Daily/*.md 复制到 `raw/daily/`
- 重新构建 `llm-wiki.json` 索引
- 更新 `index.md` 和 `log.md`

兜底：cron `llm-wiki-gap-fill` 每6小时检查一次。

## 三层保障架构

```
每次回复 → 写 Daily/YYYY-MM-DD.md → push-vault-now.sh（即时推）
                                            │
vault-gap-fill cron（0 */6 * * *）──── 补漏层：sync-vault.sh MD5检测推送
                                            │
hermes-auto-backup cron（0 3 * * *）── 兜底层：backup-hermes.sh全量备份
```

| 层级 | 机制 | 触发方式 | 输出 |
|------|------|----------|------|
| 即时 | push-vault-now.sh | 每次回复后手动调用 | 静默成功 |
| 补漏 | sync-vault.sh (MD5检测) | cron 每6小时, no_agent=true | 有变更才通知 |
| 兜底 | backup-hermes.sh (全量) | cron 每天3AM | 输出备份结果 |

## 脚本位置

- **即时推**: `~/.hermes/scripts/push-vault-now.sh`
- **补漏同步**: `~/.hermes/scripts/sync-vault.sh`
- **全量备份**: `~/.hermes/backup-hermes.sh`

## 多马共用同一 GitHub Repo 安全机制

这个 repo 不只是云马在用 —— **郝明智（local-horse）** 也在往同一 repo 推。因此：

### 所有脚本 push 前必须先 pull

| 脚本 | 机制 | 说明 |
|:--|:--|:--|
| `push-vault-now.sh` | `git pull --rebase` + `push` | 持久化克隆，先 rebase 再推 |
| `sync-vault.sh` | `git pull --rebase` + `push` | ✅ 已安全 |
| `backup-hermes.sh` | `git pull --rebase` + `push`（**已移除 `--force`**） | 之前用 force push 会覆盖其他马，已修 |

### 只清理自己的目录，不碰别人的

- 云马只管理 `cloud-horse/` 和 `knowledge-vault/`
- `backup-hermes.sh` 不再 `rm -rf` 所有文件，只清自己的两个目录
- `local-horse/`、`shared/` 等目录由其他马维护

## 注意事项

- 不要让 vault 文件太大 — 每个 Daily 笔记最多几十 KB
- 重要技术决策、项目讨论应额外写独立笔记到 `Areas/` 或 `Projects/`（不只在 Daily 中）
- 推送静默成功，无需告知用户"已推送"
- 如果 push-vault-now.sh 失败（网络问题），6h gap-fill 会补上，无需恐慌
