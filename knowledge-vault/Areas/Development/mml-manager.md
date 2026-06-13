---
created: 2026-05-10
tags: [development, mml, converter]
---

# MML 转换工具链

## 概述

`~/workspace/mml-manager` — MML（一种配置管理语言）格式的转换工具集。

## 工具架构

```
converter/
├── mml2xls.py    # MML → Excel/CSV 正向转换
├── mml2sql.py    # MML → SQL 正向转换
├── xls2mml.py    # Excel/CSV → MML 反向转换
├── csv2mml.py    # CSV → MML 反向转换
├── sql2mml.py    # SQL → MML 反向转换
└── utils/
    ├── parse.py  # MML 解析
    ├── sort.py   # 排序
    └── mml.py    # MML 实用函数
```

## MML 格式

```
SET/ADD 表名:KEY1=value1,KEY2="value with spaces";
```

## 开发状态

- ✅ mml2xls — 完成
- ✅ mml2sql — 完成
- ✅ xls2mml — 完成
- ✅ csv2mml — 完成
- ✅ sql2mml — 完成
- 🔄 重构（模块化封装）— 进行中

## 仓库

- GitHub: `github.com/wzklhk/mml-manager.git`
- 远程 origin: `wzklhk/mml-manager`
