# 知识管理工作流

> 如何维护和更新此知识库

## 日常维护

### 每日

1. **快速笔记**: 随时记录想法到 `01-Obsidian-Notes/`，加 `#quick-note` 标签
2. **会议纪要**: 会议结束后立即使用模板记录
3. **日志**: 可选写每日日志

### 每周

1. **整理**: Review 所有 `#quick-note`，将精华内容原子化
2. **技能同步**: 检查 Hermes Agent 是否新增/更新了技能，更新 [[Skills Catalog]]
3. **备份**: 确保知识库有备份 (参考 [[Backup Workflow]])

### 每月

1. **Review**: 回顾本月产生的所有笔记
2. **提炼**: 识别可以提炼为技能 (Skill) 的知识
3. **归档**: 归档不再活跃的笔记
4. **图谱检查**: 在 Obsidian Graph View 中检查笔记连接性

## 从笔记到技能

当某个知识点被重复使用或需要自动化时：

1. 在 `01-Obsidian-Notes/` 中创建知识卡片
2. 在 `02-Skill-Docs/` 中编写技能文档
3. 使用 `skill_manage(action='create')` 注册为 Hermes Agent 技能
4. 更新 [[Skills Catalog]]

## 从研究到报告

完成一项研究后：

1. 使用 `05-Templates/Research Report Template.md` 撰写报告
2. 保存到 `03-Research-Reports/` 目录
3. 在 [[Research MOC]] 中添加链接
4. 添加标签和交叉引用

## Obsidian 集成

### 方式 A: 直接作为 Obsidian Vault 打开

```bash
# 在工作台目录中直接打开 Obsidian
open -a Obsidian /home/agentuser/.hermes/kanban/boards/daily/workspaces/t_46ec6063
```

### 方式 B: 同步到主 Obsidian Vault

```bash
# 将知识库内容同步到主 Vault
rsync -a /home/agentuser/.hermes/kanban/boards/daily/workspaces/t_46ec6063/ ~/Documents/Obsidian\ Vault/
```

## 推荐的 Obsidian 插件

- **Dataview**: 使用 SQL-like 查询管理笔记
- **Templater**: 高级模板功能
- **Graph Analysis**: 增强图谱分析
- **Kanban**: 看板式任务管理
- **Calendar**: 日历视图
- **Excalidraw**: 手绘风格图表

## 知识管理原则

1. **最低有效量**: 只记录有价值的信息，避免信息过载
2. **连接胜于堆砌**: 笔记的价值在于连接，而非数量
3. **渐进式归纳**: 从片段到结构化，逐步提炼
4. **可操作**: 每条笔记都应该能带来行动或决策
