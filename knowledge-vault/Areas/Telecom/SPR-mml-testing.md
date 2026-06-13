---
created: 2026-05-12
tags: [spr, telecom, mml, testing, ocs]
---

# SPR 网元 · 营帐 MML 指令 · 套餐测试

## 概述

SPR（SPR）网元的营帐 MML 指令下发套餐测试。涉及指令：`Set Pak`、`QRY PAK`、`QRY DynUsage`。

## 基本原则：OPERATYPE=1 使用规则

### 核心规则（三条）

> **① 首次订购不携带 OPERATYPE=1**
> **② 重复订购必须携带 OPERATYPE=1**
> **③ 仅一次性套餐允许重复订购，周期性套餐禁止**

< 详细解释见 [[Areas/Telecom/SPR-rules-QP_Traffic_Classification]] >

### 正反案例对照

#### 案例一：号码 8801326713137（无OPERATYPE=1）— 首次订购 ✅

```mml
> QRY PAK: ISDN=8801326713137,PAKNAME=QP_Traffic_Classification;
< RETN=131570, DESC=PAK info does not exist     -- 套餐不存在，正常

> Set Pak: ISDN=8801326713137,ADDFLAG=1,PAKNAME=QP_Traffic_Classification;
< RETN=000000, DESC=success                       -- ✅ 首次订购成功（无OPERATYPE=1）

> Set Pak: ISDN=8801326713137,ADDFLAG=1,PAKNAME=QP_Traffic_Classification;
< RETN=131560, DESC=PAKPUB already Exist          -- ❌ 重复订购失败（缺少OPERATYPE=1）
```

#### 案例二：号码 8801326713139（有OPERATYPE=1）— 重复订购 ✅

```mml
> Set Pak: ISDN=8801326713139,ADDFLAG=1,OPERATYPE=1,PAKNAME=QP_Traffic_Classification,BEGINDATE=20260510000000;
< RETN=000000, DESC=success                       -- ⚠️ 首次订购成功但违规（不应带OPERATYPE=1）

> Set Pak: ISDN=8801326713139,ADDFLAG=1,OPERATYPE=1,PAKNAME=QP_Traffic_Classification,BEGINDATE=20260511000000;
< RETN=000000, DESC=success                       -- ✅ 重复订购成功

> Set Pak: ISDN=8801326713139,ADDFLAG=1,OPERATYPE=1,PAKNAME=QP_Traffic_Classification;
< RETN=000000, DESC=success                       -- ✅ 第三次叠加成功
```

### 错误码参考

| RETN | 含义 |
|------|------|
| 000000 | 成功 |
| 131560 | PAKPUB already Exist（套餐已存在，重复订购需携带 OPERATYPE=1） |
| 131570 | PAK info does not exist（套餐不存在，首次订购正常） |

## QP_Traffic_Classification 套餐特征

- 一次性套餐（或需确认是否为周期性）
- 两个用量桶：`Q_S_QP_Traffic_Classification`（慢速通道）和 `Q_QP_Traffic_Classification`（通用通道）
- 每次订购配额增加 30GB x 2
- 有效期叠加（每次 +1天）
- 新需求，与当前割接无关，需等割接上线后实施
