# 技能编写指南

> 如何编写和维护 Hermes Agent 技能 (Skills)

## 什么是技能

Skills 是 Hermes Agent 的"程序性记忆" — 针对特定任务类型的可复用解决方案。它们存储在 `~/.hermes/skills/` 目录中。

## 技能目录结构

```
~/.hermes/skills/
  ├── <category>/
  │   └── <skill-name>/
  │       ├── SKILL.md          (主文档, 必需)
  │       ├── references/       (参考文档)
  │       ├── templates/        (模板文件)
  │       └── scripts/          (辅助脚本)
  └── <standalone-skill>/
      └── SKILL.md
```

## SKILL.md 格式

使用 YAML 前置元数据 + Markdown 正文：

```yaml
---
name: skill-name
description: One-line description
platforms: [linux, macos]
---
```

## 何时创建新技能

- 完成复杂任务 (5+ 次工具调用) 后
- 发现可复用的工作流
- 用户纠正了你的方法
- 解决了棘手的报错
- 用户要求你"记住这个方法"

## 何时更新技能

- 步骤过时了
- 遇到了新平台特有的坑
- 缺少必要的验证步骤
- 命令或 API 发生了变化

## 优秀技能的特征

1. **明确的触发条件** — 什么情况下加载此技能
2. **带编号的步骤** — 精确的命令和操作
3. **验证步骤** — 如何确认操作成功
4. **常见问题** — 已知的坑和解决方案
5. **注意事项** — ⚠️ 标记危险操作

## 工具

- `skill_manage(action='create', name=..., content=...)` — 创建新技能
- `skill_manage(action='patch', name=..., ...)` — 更新技能 (推荐)
- `skill_view(name=...)` — 查看技能内容
- `skill_manage(action='edit', name=..., content=...)` — 完全重写

## 相关技能

- [[hermes-agent-skill-authoring]]
- [[writing-plans]]

## 参考

- [[Skills Catalog|技能总目录]]
- [[Skill Template|技能模板]]
