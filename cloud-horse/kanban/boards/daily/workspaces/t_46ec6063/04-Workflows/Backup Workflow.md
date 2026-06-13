# 备份工作流

> 知识库与 Hermes Agent 配置的自动备份

## 自动备份 (已配置)

系统通过 cron job 自动执行备份，每天凌晨 3:00 运行。

- **目标仓库**: `github.com/wzklhk/my-hermes-backup.git`
- **备份范围**: `~/.hermes/` 目录中的 `.md` 文件
- **提交身份**: "My Hermes Agent" <hermes@bot.local>
- **脚本**: `~/.hermes/backup-hermes.sh`

## 手动备份

```bash
~/.hermes/backup-hermes.sh
```

## 恢复

```bash
# 从远程仓库恢复
git clone git@github.com:wzklhk/my-hermes-backup.git ~/restore/backup

# 或从本地备份目录恢复
rsync -a ~/.hermes-backup/ ~/.hermes/
```

## 本知识库的备份

本知识库位于工作台目录下，是临时性的。要持久化保存：

```bash
# 将本知识库文件同步到 Obsidian Vault
rsync -a /home/agentuser/.hermes/kanban/boards/daily/workspaces/t_46ec6063/ ~/Documents/Obsidian\ Vault/
```

## 相关技能

- [[hermes-automated-backup]]
