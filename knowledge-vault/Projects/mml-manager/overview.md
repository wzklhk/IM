---
created: 2026-05-10
tags: [project, mml]
status: active
---

# MML Manager 项目

## 目标
构建一套完整的 MML ↔ Excel/CSV/SQL 双向转换工具链。

## 当前里程碑

### ✅ 已完成
1. 基础正向转换 (MML → Excel/CSV/SQL)
2. 反向转换 (XLS/CSV/SQL → MML)
3. 端到端测试通过

### 🔄 进行中
4. **模块化重构** — 提取公共逻辑到工具模块
   - 消除 7 个脚本间的重复代码
   - 统一 I/O 接口
   - 标准化 CLI 参数

## 相关笔记
- [[Areas/Development/mml-manager]]
