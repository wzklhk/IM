# Kanban 工作流

> Hermes Agent 看板任务管理系统使用指南

## 概述

Kanban 看板是 Hermes Agent 的任务分配与协调系统。本知识库的任务管理遵循以下流程。

## 角色

- **Orchestrator** (编排者): 分解复杂任务，创建子任务
- **Worker** (执行者): 执行具体任务
- **User** (用户): 创建任务、提供反馈、阻塞决策

## 看板状态

| 状态 | 说明 |
|------|------|
| triage | 待定/需要细化 |
| todo | 待执行 |
| ready | 已就绪 (所有前置依赖完成) |
| running | 执行中 |
| done | 已完成 |
| blocked | 阻塞 |
| archived | 已归档 |

## Worker 工作流

1. **Orient**: `kanban_show()` 查看任务上下文
2. **Work**: 在 workspace 中工作
3. **Heartbeat**: 长时间操作发送心跳 `kanban_heartbeat()`
4. **Block**: 需要人类输入时 `kanban_block()`
5. **Complete**: 完成后 `kanban_complete()`

## 相关技能

- [[kanban-orchestrator]]
- [[kanban-worker]]

## 相关笔记

- [[Knowledge Management Workflow]]
