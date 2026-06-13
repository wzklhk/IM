# 孟加拉项目履历（Red Hat OpenShift + BlueCat DNS 开局）

> 聚焦 G 运营商两大基础设施平台的交付，可拆分用作简历项目经历或面试回答

---

## 项目一：Red Hat OpenShift 容器平台开局

- **时间**：2025.05 – 至今
- **角色**：云原生基础设施工程师 / 项目部署负责人
- **客户**：孟加拉 G 运营商

### 项目背景

运营商启动核心网云化战略，需搭建 Red Hat OpenShift（OCP）容器平台，承载 EPC / IMS 等核心网网元。项目覆盖从裸金属到业务上线的全链条交付。

### 集群架构设计

部署三类 OCP 集群各司其职：

| 集群 | 用途 |
|------|------|
| Hub OCP | 中心管理集群，统一纳管全网集群 |
| Service OCP | 业务集群，承载核心网控制面网元 |
| STF OCP | 智能流量处理集群，承载用户面/数据面网元 |

### 交付全流程

1. **LLD 详细设计** — 机柜上架规划、服务器选型、交换机布线设计、全站 IP 地址分配
2. **Underlay 网络基建** — 交换机配置调试（VLAN 划分 / LACP 链路聚合 / 端口配置），管理面、业务面、存储面、SDN 面四平面隔离，防火墙策略与安全域规划
3. **Overlay 网络** — VXLAN / SDN 网络平面规划实施，跨机柜/跨机房互通
4. **Support 节点部署** — 基于 RHEL + KVM（virsh）创建 Bastion 跳板机、IDM 身份管理（双机 HA）、LB 负载均衡（HAProxy + Keepalived 双机 HA）、Bootstrap 引导节点
5. **OCP 集群安装** — Bastion 发起安装 → Bootstrap 引导 → 3×Master（控制面 + etcd）→ 3×Infra（Ingress + 监控 + 镜像仓库）→ 数十台 Worker（计算节点），集成 OVN-Kubernetes CNI + CSI 存储
6. **业务迁移** — 核心网网元容器化适配联调，网络性能调优与资源隔离

### 核心交付物

- 平台部署规范文档、运维手册
- 移交客户 O&M 团队

---

## 项目二：BlueCat DNS 系统开局（GI DNS）

- **时间**：2025.04 – 2025.12
- **角色**：基础设施工程师
- **客户**：孟加拉 G 运营商

### 项目背景

运营商需搭建运营商级 DNS 系统，作为 GI DNS（Gi Interface DNS）为核心网 Gi 接口提供域名解析服务——即移动用户数据业务出公网的第一级 DNS。

### 系统架构

| 组件 | 说明 |
|------|------|
| BAM（Broadband Address Manager） | 配置管理网关，集中配置与下发 DNS 策略 |
| BDDS（BlueCat DNS Data Server） | 业务虚机，承载 DNS 解析服务，每站点多台 HA 部署 |
| 负载均衡 | 交换机侧 LB 分发 DNS 请求到多台 BDDS |

### 交付全流程

1. **LLD 详细设计** — DNS / DHCP / IPAM 服务器与网络设备部署规划，全站 IP 子网划分
2. **网络基建** — 交换机/防火墙配置，管理平面 / 业务平面 / 同步平面三平面隔离
3. **OpenStack 虚拟化部署** — 创建 Project/Tenant，创建 VM（规格/镜像/网络），Neutron 虚拟网络平面搭建
4. **BlueCat 系统部署** — BAM + BDDS 高可用架构安装配置，DNS 策略下发，与现网核心网元链路互通
5. **验收交付** — HA 双机热备验证，灾备切换演练，域名解析功能测试

---

## 技能关键词

`Red Hat OpenShift` `Kubernetes` `OCP` `RHEL` `KVM/virsh` `HAProxy` `Keepalived` `OVN-Kubernetes` `VXLAN`
`BlueCat DNS` `BAM` `BDDS` `GI DNS` `OpenStack` `Neutron` `LLD` `Underlay/Overlay 网络`
`交换机调试` `VLAN` `LACP` `防火墙策略` `核心网` `EPC` `IMS` `海外交付`

---

## 面试要点

以下是可以展开讲的关键技术话头：

1. **OCP 三类集群架构为什么这么设计** — Hub 做统一管理入口，Service 跑控制面（低延迟要求），STF 跑用户面（高吞吐+DPDK/SR-IOV），资源隔离 + 故障域隔离
2. **IDM + LB 的 HA 设计** — IDM 双机做 DNS/DHCP/证书服务的 HA，LB 双机做 HAProxy + Keepalived VIP 漂移，任意单点故障不影响集群运行
3. **Underlay vs Overlay 网络** — Underlay 解决物理可达性（VLAN/路由），Overlay 解决多租户隔离（VXLAN/SDN），两者配合实现网元的灵活部署和迁移
4. **GI DNS 在移动网络中的位置** — 用户手机发起数据请求 → 基站 → 核心网 PGW → Gi 接口 → BlueCat GI DNS → 公网。GI DNS 是运营商网络和互联网的边界解析点，性能和可靠性要求极高
5. **海外项目经验** — 全程英文技术交流，对接客户、厂商（Red Hat / BlueCat）、本地团队三方协作，适应跨文化工作节奏
