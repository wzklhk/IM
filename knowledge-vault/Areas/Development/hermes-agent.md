---
created: 2026-05-10
tags: [development, hermes-agent]
related: [Areas/DevOps/automation]
---

# Hermes Agent 使用指南

## 配置文件

- 主配置: `~/.hermes/config.yaml`
- 环境变量: `~/.hermes/.env`

## 常用命令

### 配置管理
```bash
hermes config get <key>
hermes config set <key> <value>
```

### 技能管理
```bash
hermes skill list
hermes skill show <name>
hermes skill install <path>
```

### 工具管理
```bash
hermes tools
```

### 提供商/模型
```bash
hermes models
hermes setup
```

## 技能开发

技能存放于 `~/.hermes/skills/<category>/<name>/SKILL.md`。

### 格式要求
1. YAML frontmatter (name, description, platforms, metadata)
2. Markdown body with numbered steps
3. Pitfalls section recommended
4. Verification steps for setup

## 注意点

- `.env` 文件有保护机制，`***` 替代实际值 — 通过 `grep/cut` 提取
- Git identity 常用: `My Hermes Agent <hermes@bot.local>`
- `GITHUB_TOKEN` 通过 `~/.hermes/scripts/load-github-token.sh` 加载
