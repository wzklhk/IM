---
created: 2026-05-12
updated: 2026-05-12
tags: [telecom, openshift, ocp, kubernetes, delivery]
---

# Red Hat OpenShift（OCP）工程交付

## 项目概述

B 国 G 运营商核心网云化平台 Red Hat OpenShift（OCP）开局部署项目。

## 集群架构

站点部署 **三类 OCP 集群**，各司其职：

| 集群类型 | 用途 |
|---------|------|
| **Hub OCP** | 中心管理集群，负责全网集群的统一管理和运维 |
| **Service OCP** | 业务集群，承载核心网网元（EPC / IMS 等）|
| **STF OCP** | 智能流量处理集群，承载数据面/用户面网元 |

## Support 节点

每个站点域部署 **2 台 Support 节点**：

- 操作系统：**Red Hat Enterprise Linux（RHEL）**
- 通过 **virsh（KVM 虚拟化）** 创建集群部署所需的关键虚机：

| 虚机 | 数量 | 用途 |
|------|------|------|
| **Bastion** | 1 | 跳板机，集群安装与运维入口 |
| **IDM** | 2 | 身份管理（DNS / DHCP / 证书服务），HA 部署 |
| **LB** | 2 | 负载均衡器（HAProxy / Keepalived），HA 部署 |
| **Bootstrap** | 1 | OCP 集群安装时的临时引导节点，安装完成后可销毁 |

## 集群节点规格

每个 OCP 集群的节点组成：

| 节点角色 | 数量 | 说明 |
|---------|------|------|
| **Master** | 3 | 控制面节点，运行 etcd + API Server + Controller Manager + Scheduler |
| **Infra** | 3 | 基础设施节点，运行 Ingress Controller + Registry + 监控组件 |
| **Worker** | 数十台 | 计算节点，承载核心网网元容器化业务负载 |

## 交付全流程

### 1️⃣ LLD 详细设计

- 机柜上架规划（U 位分配、PDU 功率计算）
- 服务器设备选型与配置清单
- 交换机布线设计（TOR / EOR 拓扑）
- IP 地址分配表

### 2️⃣ 网络基建 — Underlay

- 交换机配置调试：VLAN 划分、端口配置、链路聚合（LACP）
- 管理平面、业务平面、存储平面、SDN 平面的 VLAN 隔离
- 防火墙策略配置与安全域规划

### 3️⃣ 网络基建 — Overlay

- VXLAN / SDN 网络平面规划与实施
- 跨机柜 / 跨机房的网络互通

### 4️⃣ Support 节点部署

- RHEL 操作系统安装与配置
- KVM / virsh 虚拟化环境搭建
- 通过 virsh 创建 Bastion / IDM / LB / Bootstrap 虚机
- IDM 双机 HA + LB 双机 HA 配置

### 5️⃣ OCP 集群安装

- 通过 Bastion 节点发起 OCP 集群安装
- Bootstrap 引导 → Master 初始化 → Infra 加入 → Worker 加入
- CNI 插件（OVN-Kubernetes / Calico）集成调测
- CSI 存储插件集成
- 网络配置：Ingress / Egress / Service Network

### 6️⃣ 业务迁移

- Hub / Service / STF 三类集群各司其职
- 核心网网元容器化上云适配与联调
- 网络性能调优与资源隔离
- 用户平面数据面性能优化

## 交付物

- 平台部署规范文档
- 运维手册
- 移交客户 O&M 团队

## 关联

- [[Areas/General/resume|简历]] — 项目经历摘要
- [[Areas/CloudNative/MOC|Cloud Native 主页]]
- [[Areas/Telecom/MOC|Telecom 主页]]
