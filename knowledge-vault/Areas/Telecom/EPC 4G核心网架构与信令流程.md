# EPC 4G 核心网架构与信令流程

## 概述

EPC（Evolved Packet Core）是 4G LTE 网络的核心网架构，采用**全 IP** 设计，实现了控制平面与用户平面的分离。相比 3G 的电路交换+分组交换双核心，EPC 仅保留分组交换，通过 IMS 提供语音服务（VoLTE）。

---

## 一、EPC 核心网架构

### 1.1 整体架构图

```
                        ┌─────────────────────────────────────┐
                        │              PCRF                     │
                        │   (策略与计费规则功能)                  │
                        └──────────┬──────────────────────────┘
                                   │ Gx
┌──────────┐    S1-MME    ┌───────┴───────┐    S11    ┌───────────┐
│   eNB    │──────────────│     MME       │───────────│   SGW     │
│(基站)    │              │(移动性管理实体)  │           │(服务网关)  │
└──────────┘              └───────────────┘           └─────┬─────┘
       │                                                     │ S5/S8
       │ S1-U                                                │
       │─────────────────────────────────────────────────┐   │
                                                         │   │
                                                  ┌──────┴───┴──────┐
                                                  │      PGW        │
                                                  │ (PDN网关/公网出口) │
                                                  └──────┬──────────┘
                                                         │ SGi
                                                  ┌──────┴──────┐
                                                  │  外部PDN网络   │
                                                  │ (Internet/IMS)│
                                                  └─────────────┘
```

### 1.2 核心网元详解

#### MME（Mobility Management Entity）— 移动性管理实体
- **职责**：控制面核心，处理所有信令
- **功能**：
  - 用户认证（与 HSS 交互）
  - 位置管理（跟踪区更新）
  - 移动性管理（切换、附着/去附着）
  - 空闲态 UE 可达性管理（寻呼）
  - 承载管理（建立/修改/释放）
  - NAS 信令的加密与完整性保护
  - SGW 选择
- **接口**：S1-MME（eNB↔MME）、S11（MME↔SGW）、S6a（MME↔HSS）

#### SGW（Serving Gateway）— 服务网关
- **职责**：用户面数据转发锚点
- **功能**：
  - eNB 间切换时的用户面锚点
  - 空闲态下行数据缓冲与触发寻呼
  - 计费数据采集
  - 合法监听
  - 跨 SGW 切换时的数据转发
- **接口**：S1-U（eNB↔SGW）、S11（MME↔SGW）、S5/S8（SGW↔PGW）

#### PGW（PDN Gateway）— PDN 网关
- **职责**：外部 PDN 网络的出入口
- **功能**：
  - UE IP 地址分配
  - 策略执行（与 PCRF 交互，Gx 接口）
  - 计费与 QoS 执行
  - 包过滤与深度包检测（DPI）
  - 到外部网络的流量路由
- **接口**：S5/S8（SGW↔PGW）、SGi（PGW↔外部PDN）、Gx（PGW↔PCRF）

#### HSS（Home Subscriber Server）— 归属签约用户服务器
- **职责**：用户数据库
- **功能**：
  - 用户签约数据存储（APN、QoS 配置）
  - 认证向量生成（AuC 功能）
  - 用户位置信息记录
  - 鉴权与密钥协商
- **接口**：S6a（MME↔HSS，基于 Diameter）

#### PCRF（Policy and Charging Rules Function）— 策略与计费规则功能
- **职责**：QoS 策略与计费规则决策
- **功能**：
  - 策略控制决策
  - 基于流的计费规则
  - 承载绑定与事件报告
- **接口**：Gx（PGW↔PCRF）、Rx（PCRF↔AF/IMS）

### 1.3 参考点/接口总表

| 接口 | 连接两端 | 协议 | 用途 |
|------|---------|------|------|
| S1-MME | eNB ↔ MME | S1-AP (over SCTP) | 控制面信令 |
| S1-U | eNB ↔ SGW | GTP-U/UDP | 用户面数据 |
| S5/S8 | SGW ↔ PGW | GTP-C/UDP（控制面）<br>GTP-U/UDP（用户面） | 承载管理（S5：归属地<br>S8：漫游） |
| S11 | MME ↔ SGW | GTP-C/UDP | 承载控制 |
| S6a | MME ↔ HSS | Diameter | 认证与签约数据 |
| Gx | PGW ↔ PCRF | Diameter | 策略与计费规则下发 |
| SGi | PGW ↔ 外部PDN | IP | 外部网络接入 |
| S10 | MME ↔ MME | GTP-C | MME 间移动性 |
| S13 | MME ↔ EIR | Diameter | 设备身份检查 |

### 1.4 NAS 与 AS 协议栈

```
UE                  eNB                 MME
┌──────┐          ┌──────┐           ┌──────┐
│ NAS  │─────────│ S1-AP │──────────│ NAS  │ ← 非接入层
│ RRC  │───AS───│ RRC   │           │      │
│ PDCP │          │ PDCP │           │      │
│ RLC  │          │ RLC  │           │      │
│ MAC  │          │ MAC  │           │      │
│ PHY  │          │ PHY  │           │      │
└──────┘          └──────┘           └──────┘
     ← Uu 接口 →     ← S1-MME → 
```

- **NAS（Non-Access Stratum）**：UE ↔ MME 之间的控制面协议，承载 EPS 移动性管理（EMM）和 EPS 会话管理（ESM）
- **AS（Access Stratum）**：UE ↔ eNB 之间的接入层协议

### 1.5 EPS 承载架构

```
UE ← Radio Bearer → eNB ← S1 Bearer → SGW ← S5/S8 Bearer → PGW
└────────────────── EPS Bearer ──────────────────────────────┘
```

- **EPS Bearer**（默认承载 + 专用承载）：
  - **默认承载**：UE 附着时建立，始终保持，提供非保证比特率（Non-GBR）
  - **专用承载**：按需建立（如 VoLTE），提供 GBR 保证
- **QoS 参数**：QCI（1-9）、ARP、GBR/MBR、APN-AMBR

---

## 二、信令流程总览

以下所有流程按编号列出，贯穿一张流程图覆盖全生命周期。

### 信令流程全景图

```
┌─────────────────────────────────────────────────────────┐
│                   EPC 信令流程全景                         │
│                                                         │
│  1. 附着流程 (Attach) ──────────────────────────→ 初始连接  │
│  2. 去附着流程 (Detach) ←──────────────────────── 断开连接  │
│  3. TAU 流程 (Tracking Area Update) ────────────→ 位置更新  │
│  4. 业务请求流程 (Service Request) ──────────────→ 激活连接  │
│  5. S1 切换 (S1 Handover)                           │
│  6. X2 切换 (X2 Handover) ────────────────────────→ 移动切换  │
│  7. 专用承载建立流程 (Dedicated Bearer Setup) ──────→ QoS建立  │
│  8. 寻呼流程 (Paging) ────────────────────────────→ 寻呼UE  │
└─────────────────────────────────────────────────────────┘
```

---

## 三、详细信令流程

### 流程 1：附着流程（Attach Procedure）⭐ 最重要

**场景**：UE 开机首次接入网络 / 从其他网络回到 LTE

```
UE                  eNB                 MME               SGW/PGW            HSS
│                    │                   │                   │                │
│←───1. RRC Setup───→│                   │                   │                │
│                    │                   │                   │                │
│───2. Attach Req───→│───3. Initial UE  │                   │                │
│   + PDN Conn Req   │   Message ──────→│                   │                │
│                    │   (Attach Req)   │                   │                │
│                    │                   │──4. 身份请求──────→│                │
│                    │                   │←──身份响应────────│                │
│                    │                   │                   │                │
│                    │                   │──5. 认证流程──────→│                │
│                    │                   │   (Auth Info Req  │────→ Auth Info │
│                    │                   │    /Resp)         │←──── Req/Resp │
│                    │                   │                   │                │
│←──Auth Req────────│←──NAS: Auth Req───│                   │                │
│───Auth Resp───────→│───NAS: Auth Resp─→│                   │                │
│                    │                   │                   │                │
│                    │                   │──6. Update Loc Req→│────→ ULR      │
│                    │                   │                   │←──── ULA      │
│                    │                   │←──Update Loc Ack──│                │
│                    │                   │                   │                │
│                    │                   │──7. Create Session Req ──→│        │
│                    │                   │                   │←──Create Sess  │
│                    │                   │                   │    Resp        │
│                    │                   │                   │                │
│←──RRC Reconfig────│───8. Initial Ctxt │                   │                │
│   (Attach Accept)  │   Setup Req ─────│                   │                │
│                    │   (Attach Accept) │                   │                │
│───RRC Reconfig Cmp─→│                   │                   │                │
│                    │───Initial Ctxt    │                   │                │
│                    │   Setup Resp ────→│                   │                │
│                    │                   │                   │                │
│───9. Direct Transfer│                   │                   │                │
│   (Attach Complete)│───Uplink NAS ─────→│                   │                │
│   + PDN Conn Cmp   │   (Attach Cmp)    │                   │                │
│                    │                   │───Modify Bearer──→│                │
│                    │                   │   Req             │                │
│                    │                   │←──Modify Bearer───│                │
│                    │                   │   Resp            │                │
│                    │                   │                   │                │
│◄═══════ EPS Bearer Established ════════════════════════════►│ Data Flow     │
```

**关键步骤说明：**

| 步骤 | 消息 | 说明 |
|------|------|------|
| 1 | RRC Setup | UE 与 eNB 建立 RRC 连接 |
| 2 | Attach Request + PDN Connectivity Request | UE 在 RRC 中携带 NAS 消息发送；包含 IMSI/GUTI、UE 能力、PDN 类型（IPv4/IPv6） |
| 3 | Initial UE Message | eNB 用 S1-AP 封装 NAS 消息发送给 MME |
| 4 | 身份请求/响应 | 如果 MME 没有有效的 UE 上下文（如首次附着），请求 IMSI；若有 GUTI，可跳过 |
| 5 | 认证与 NAS 安全 | MME ↔ HSS 取认证向量（EAP-AKA'）；UE 与 MME 之间进行双向认证；之后启动 NAS 安全（加密+完整性保护） |
| 6 | Update Location | MME 向 HSS 注册位置；HSS 取消旧 MME；HSS 返回签约数据（APN、QoS profile） |
| 7 | Create Session | MME → SGW → PGW 创建默认承载；PGW 分配 UE IP 地址；PCRF（Gx 接口）下发策略 |
| 8 | Initial Context Setup | MME → eNB：附着接受 + 安全模式 + RRC 重配 |
| 9 | Attach Complete | UE → MME：附着完成 + PDN 连接完成；MME → SGW：修改承载 |

---

### 流程 2：去附着流程（Detach Procedure）

#### 场景 A：UE 发起关机去附着

```
UE                  MME                SGW/PGW            HSS
│                     │                   │                │
│───Detach Request───→│                   │                │
│  (Power Off)       │                   │                │
│                     │───Delete Session Req──→│           │
│                     │                   │←──Delete Sess  │
│                     │                   │    Resp        │
│←──Detach Accept────│                   │                │
│                     │                   │                │
│                     │───4. Purge UE───→│───Purge UE─────→│
│                     │   (可选)          │                │
│                     │                   │                │
│ RRC 释放             │                   │                │
```

#### 场景 B：MME 发起的去附着（如网络侧注销）

```
UE                  MME                SGW/PGW            HSS
│                     │                   │                │
│                     │（网络侧决策去附着）                    │
│                     │───Delete Session Req──→│           │
│                     │                   │←──Delete Sess  │
│                     │                   │    Resp        │
│                     │                   │                │
│←──Detach Request────│                   │                │
│───Detach Accept────→│                   │                │
│                     │                   │                │
│ RRC 释放             │                   │                │
```

#### 场景 C：HSS 发起的去附着

```
UE                  MME (旧)             HSS
│                     │                   │
│                     │←──Cancel Location──│
│                     │   (CLR)           │
│                     │                   │
│←──Detach Request────│                   │
│───Detach Accept────→│                   │
│                     │                   │
│                     │───Cancel Location ─→│
│                     │   Ack (CLA)       │
│                     │                   │
│ MME → SGW → PGW：删除承载 │               │
```

---

### 流程 3：跟踪区更新（TAU）流程

**触发条件**：
- UE 进入一个不属于其注册 TA 列表的新 TA
- TAU 定时器（T3412）超时 → 周期性 TAU
- UE 能力变更
- 网络侧 TA 列表变更

#### 正常 TAU（MME 不变）

```
UE                  eNB                 MME (原)           HSS
│                     │                   │                │
│───RRC Setup────────→│                   │                │
│                     │                   │                │
│───TAU Request──────→│───Initial UE Msg──→│                │
│  (GUTI, Last TAI)   │   (TAU Req)      │                │
│                     │                   │                │
│                      （可选：安全模式）                     │
│                     │                   │                │
│←──TAU Accept────────│←──DL NAS Transport│                │
│  (GUTI, TA List)    │   (TAU Accept)   │                │
│                     │                   │                │
│───TAU Complete─────→│───UL NAS Transport→│                │
│                     │   (TAU Complete)  │                │
```

#### TAU 带 MME 变更（跨 MME）

```
UE                  eNB             目标MME        源MME           HSS
│                     │                │              │              │
│───TAU Request──────→│───Initial UE───→│              │              │
│  (旧 GUTI)          │   Msg          │              │              │
│                     │                │──Context Req──→│              │
│                     │                │←──Context Resp│              │
│                     │                │              │              │
│                     │                │(可选认证)     │              │
│                     │                │              │              │
│                     │                │──Update Loc Req──────────────→│
│                     │                │←──Update Loc Ack──────────────│
│                     │                │              │              │
│                     │                │──Context Ack──→│              │
│                     │                │              │              │
│                     │                │──Modify Bearer Req────→SGW   │
│                     │                │←──Modify Bearer Resp──←SGW   │
│                     │                │              │              │
│←──TAU Accept────────│←──TAU Accept───│              │              │
│───TAU Complete─────→│───TAU Cmp─────→│              │              │
```

---

### 流程 4：业务请求流程（Service Request）

**场景**：UE 在空闲态（ECM-IDLE）有上下行数据需要发送

#### UE 发起的业务请求（上行数据）

```
UE                  eNB                 MME               SGW
│                     │                   │                │
│←──空闲态 (ECM-IDLE)                                             │
│                     │                   │                │
│───RRC Setup────────→│                   │                │
│                     │                   │                │
│───Service Request───→│───Initial UE      │                │
│  NAS: SR (原因码)    │   Message ──────→│                │
│  (已加密/完整性保护)  │   (Service Req)  │                │
│                     │                   │                │
│                      （MME验证SR有效性）                     │
│                     │                   │                │
│                     │                   │                │
│                     │←──S1-AP: Initial  │                │
│                     │   Context Setup   │                │
│                     │   Req            │                │
│                     │   (E-RAB列表)     │                │
│                     │                   │                │
│←──RRC Reconfig─────│                   │                │
│───RRC Reconfig Cmp ─→│                   │                │
│                     │                   │                │
│                     │───S1-AP: Initial─→│                │
│                     │   Context Setup   │                │
│                     │   Resp           │                │
│                     │                   │                │
│                     │                   │───Modify Bearer──→│
│                     │                   │   Req            │
│                     │                   │←──Modify Bearer──│
│                     │                   │    Resp          │
│                     │                   │                │
│◄═══════ S1-U 承载重建 ═══════════════════════════════► Data Flow │
```

#### 网络发起的业务请求（下行数据→寻呼→业务请求）

```
SGW                       MME                 eNB                UE
│                          │                   │                  │
│── 下行数据到达 ──────────→│                   │                  │
│  (缓存数据)              │                   │                  │
│                          │                   │                  │
│                          │───寻呼(Paging)────→│                  │
│                          │   S1-AP Paging    │                  │
│                          │   (UE Identity +  │                  │
│                          │    TAI list)      │───Paging─────────→│
│                          │                   │   (PCCH逻辑信道)   │
│                          │                   │                  │
│                          │                   │ (UE 发起 Service │
│                          │                   │  Request)        │
│                          │                   │                  │
│                          │                   │  ← 后续同UE发起流程 │
│                          │                   │                  │
│◄══════ UE 恢复连接，下行数据开始传输 ════════════════════════════►│
```

---

### 流程 5：X2 切换（基于 X2 接口的 eNB 间切换）⭐

**场景**：UE 在连接态下从一个 eNB 移动到另一个 eNB，且无需更换 MME/SGW

```
UE           源 eNB             目标 eNB         MME            SGW
│                │                   │             │              │
│←───测量控制─────│                   │             │              │
│───测量报告─────→│                   │             │              │
│                │                   │             │              │
│                │──1. Handover Req──→│             │              │
│                │                   │             │              │
│                │←──Handover Req Ack──│             │              │
│                │   (包含切换命令)    │             │              │
│                │                   │             │              │
│←─RRC: HO Cmd───│                   │             │              │
│  (包含目标eNB   │                   │             │              │
│   分配的C-RNTI) │                   │             │              │
│                │                   │             │              │
│═══════离开源 eNB，同步到目标 eNB ════════════════════════════════│
│                │                   │             │              │
│───────────────────→2. RRC HO Cmp───│             │              │
│                │   (RA完成)        │             │              │
│                │                   │             │              │
│                │                   │──3. Path───→│              │
│                │                   │   Switch    │              │
│                │                   │   Req       │              │
│                │                   │             │              │
│                │                   │             │───Modify────→│
│                │                   │             │   Bearer Req │
│                │                   │             │←──Modify──── │
│                │                   │             │   Bearer Resp│
│                │                   │             │              │
│                │                   │←──Path──────│              │
│                │                   │   Switch    │              │
│                │                   │   Ack       │              │
│                │                   │             │              │
│                │──4. UE Context───→│             │              │
│                │    Release        │             │              │
│                │                   │             │              │
│◄═══════ S1-U 路径切换到目标 eNB ════════════════════════════════►│
```

**流程要点：**
- eNB 间通过 X2 接口直接通信，不经过 MME
- MME 参与路径切换（Path Switch），更新 SGW 的用户面路径
- 零中断切换（make-before-break），先建后断

---

### 流程 6：S1 切换（基于 S1 接口的 eNB 间切换）

**场景**：UE 在连接态下跨 MME/SGW 移动，或 X2 接口不可用

```
UE           源 eNB           源 MME          目标 MME       目标 eNB       SGW
│                │                │               │              │          │
│──MR(report)───→│                │               │              │          │
│                │──HO Required──→│               │              │          │
│                │   (目标eNB ID) │               │              │          │
│                │                │──Forward Reloc─→│           │          │
│                │                │   Req          │              │          │
│                │                │               │──HO Req──────→│         │
│                │                │               │←──HO Req Ack──│         │
│                │                │               │              │          │
│                │                │←──Forward------│           │          │
│                │                │   Reloc Resp  │              │          │
│                │                │               │              │          │
│                │←──HO Command───│               │              │          │
│←──HO Cmd───────│                │               │              │          │
│                │                │               │              │          │
│═══════ UE 切换 ════════════════════════════════════════════════════════│
│                │                │               │              │          │
│──────────────────────────────────────→RRC HO Cmp──│         │          │
│                │                │               │              │          │
│                │                │               │──HO Notify──→│          │
│                │                │               │              │          │
│                │                │               │──Modify───────→│          │
│                │                │               │   Bearer Req  │          │
│                │                │               │←──Modify───────│          │
│                │                │               │   Bearer Resp │          │
│                │                │               │              │          │
│                │                │←────Forward───│           │          │
│                │                │     Reloc Cmp │              │          │
│                │                │               │              │          │
│                │──UE Context───→│               │              │          │
│                │    Release     │               │              │          │
│                │                │               │              │          │
```

---

### 流程 7：专用承载建立流程（Dedicated Bearer Establishment）

**场景**：需要额外的 EPS 承载来提供特定 QoS（如 VoLTE 呼叫建立）

#### 网络侧发起的专用承载建立（PCRF 触发）

```
PCRF              PGW               SGW               MME            eNB+UE
 │                 │                 │                 │               │
 │──Gx: CCR/CCA──→│                 │                 │               │
 │  (策略决策)     │                 │                 │               │
 │                 │                 │                 │               │
 │                 │──1. Create───→│                 │               │
 │                 │   Bearer Req   │                 │               │
 │                 │   (QCI, TFT,   │                 │               │
 │                 │    GBR, TAD)   │                 │               │
 │                 │                 │──2. Create───→│               │
 │                 │                 │   Bearer Req   │               │
 │                 │                 │   (QCI, TFT,   │               │
 │                 │                 │   Bearer ID)   │               │
 │                 │                 │                 │               │
 │                 │                 │                 │──3. Bearer───→│
 │                 │                 │                 │   Setup Req   │
 │                 │                 │                 │   (S1-AP)     │
 │                 │                 │                 │               │
 │                 │                 │                 │←──RRC Reconf──│
 │                 │                 │                 │   完成         │
 │                 │                 │                 │               │
 │                 │                 │                 │──4. Bearer────│
 │                 │                 │                 │    Setup Resp  │
 │                 │                 │                 │   (S1-AP)     │
 │                 │                 │                 │               │
 │                 │                 │←──Create Bearer│               │
 │                 │                 │    Resp        │               │
 │                 │←──Create Bearer─│                 │               │
 │                 │    Resp        │                 │               │
 │                 │                 │                 │               │
 │  [[专用承载建立完成，GBR数据流开始传输]]                          │
```

**关键点**：
- **QCI（QoS Class Identifier）**：1-9，QCI=1 用于 VoLTE（GBR）
- **TFT（Traffic Flow Template）**：定义哪些 IP 流映射到该承载
- **TAD（Traffic Aggregate Description）**：描述流量特征
- 专用承载默认与默认承载共享同一个 PDN 连接

---

### 流程 8：寻呼流程（Paging）

**场景**：UE 在空闲态（ECM-IDLE），SGW 收到下行数据后触发

```
SGW                   MME                    eNB                   UE
 │                      │                      │                    │
 │── 下行数据到达──────→│                      │                    │
 │  (无S1-U连接)        │                      │                    │
 │                      │                      │                    │
 │                      │  查询 UE 注册的 TA 列表                 │
 │                      │  及该 TA 下列的所有 eNB                 │
 │                      │                      │                    │
 │                      │──S1-AP Paging (eNB1)─→│                    │
 │                      │──S1-AP Paging (eNB2)─→│                    │
 │                      │──S1-AP Paging (eNB3)─→│                    │
 │                      │                      │                    │
 │                      │                      │──Paging (PCCH)─────→│
 │                      │                      │   (UE Identity:    │
 │                      │                      │    S-TMSI 或 IMSI）│
 │                      │                      │                    │
 │                      │                      │                    │
 │                      │  (UE 在 TA 内任一 eNB 响应)               │
 │                      │                      │  ← UE 发起         │
 │                      │                      │    Service Request  │
 │                      │                      │                    │
 │                      │  ← 后续 Service Request 流程，恢复 S1-U  │
 │                      │                      │                    │
 │◄═══════ SGW 将缓存的下行数据发送给 UE ═══════════════════════════┤
```

---

## 四、EPS 移动性管理状态机（EMM & ECM）

### EMM 状态（EPS Mobility Management）

```
          ┌──────────────────────────────────────┐
          │                                      │
          ▼                                      │
┌─────────────────┐   Attach Success    ┌──────────────────┐
│  EMM-DEREGISTERED│──────────────────→│   EMM-REGISTERED  │
│  (未注册/去附着)  │                    │   (已注册/附着)    │
│                  │←──────────────────│                   │
└─────────────────┘   Detach           └──────────────────┘
```

### ECM 状态（EPS Connection Management）

```
     ┌──────────────┐
     │  ECM-IDLE     │  ← 无 NAS 信令连接，无 S1-U 承载
     │  (空闲态)      │     UE 在 TA 寻呼区
     └──────┬───────┘
            │ Service Request / Paging Response
            │  or Attach / TAU
            ▼
     ┌──────────────┐
     │  ECM-CONNECTED│  ← 有 RRC 连接 + S1 连接
     │  (连接态)      │     用户面承载存在
     └──────────────┘
```

### 状态转换与关键信令关联

| 起点 | 终点 | 触发事件 | 涉及流程 |
|------|------|---------|---------|
| EMM-DEREG + ECM-IDLE | EMM-REG + ECM-CONNECTED | 开机/网络接入 | 附着流程 |
| EMM-REG + ECM-IDLE | EMM-REG + ECM-CONNECTED | 数据到达/TAU | 业务请求/TAU |
| EMM-REG + ECM-CONNECTED | EMM-REG + ECM-IDLE | 无数据 + 定时器超时 | RRC 释放 |
| EMM-REG + ECM-CONNECTED | EMM-DEREG + ECM-IDLE | 关机/网络注销 | 去附着流程 |

---

## 五、关键总结

### 核心设计原则

| 原则 | 说明 |
|------|------|
| **控制与用户面分离** | MME 只处理信令，SGW/PGW 处理数据转发 |
| **扁平化架构** | 相比 3G RNC+BSC 的层级结构，LTE 直接 eNB→SGW |
| **全 IP 网络** | 无电路域，所有业务通过 IP 承载 |
| **QoS 基于承载** | EPS Bearer 是 QoS 粒度单元，QCI 定义转发行为 |
| **移动性锚点** | SGW 是 eNB 间切换的锚点，PGW 是跨 SGW 切换的锚点 |

### 关键接口协议总结

| 协议 | 用途 | 传输层 |
|------|------|--------|
| NAS (EMM+ESM) | UE↔MME 信令 | S1-AP 承载 |
| S1-AP | eNB↔MME 信令 | SCTP |
| GTP-C | 核心网控制面（MME↔SGW↔PGW） | UDP |
| GTP-U | 核心网用户面（eNB↔SGW↔PGW） | UDP |
| Diameter | MME↔HSS (S6a), PGW↔PCRF (Gx) | SCTP/TCP |
| RRC | UE↔eNB 无线资源控制 | PDCP/RLC/MAC/PHY |

### 重要定时器

| 定时器 | 默认值 | 用途 |
|--------|--------|------|
| T3412 | 54 min | 周期性 TAU 定时器 |
| T3402 | 12 min | TAU 失败后重试定时器 |
| T3411 | 5 s | TAU 失败后立即重试 |
| T3450 | 6 s | 承载建立等待响应 |
| T3485 | 8 s | 去附着等待响应 |
| 移动可达定时器 | 4 min | MME 等待 UE 寻呼响应 |

---

> **参考标准**：3GPP TS 23.401（GPRS Enhancements for E-UTRAN Access）、TS 24.301（NAS Protocol）、TS 36.300（E-UTRAN Overall Description）
