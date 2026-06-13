---
created: 2026-05-12
tags: [telecom, dns, bluecat, delivery]
---

# Bluecat DNS 工程交付（GI DNS）

## 项目概述

B 国 G 运营商 Bluecat DNS 系统开局部署，作为 GI DNS 为核心网 Gi 接口提供域名解析服务。

## 交付全流程

### 1️⃣ LLD 详细设计

- DNS / DHCP / IPAM 服务器与网络设备的部署规划
- IP 地址分配、子网划分

### 2️⃣ 网络基建

- 交换机与防火墙的网络配置
- VLAN 划分、端口配置、防火墙策略
- **三平面隔离**：管理平面 / 业务平面 / 同步平面

### 3️⃣ OpenStack 虚拟化部署

- 基于 OpenStack 虚拟化平台创建部署环境
- 创建工程（Project）、租户（Tenant）
- 创建虚拟机（规格、镜像、网络）
- 创建虚拟网络平面（Neutron）

### 4️⃣ Bluecat 系统架构

#### BAM（Broadband Address Management）

- 配置管理网关
- 集中配置与下发 DNS 策略
- 管理面组件

#### BDDS（Bluecat DNS Data Server）

- 业务虚机，承载实际的 DNS 解析服务
- 每个站点部署多台 BDDS 虚机
- **交换机侧负载均衡**分发流量到多台 BDDS

### 5️⃣ 业务定位 — GI DNS

- 该系统作为 **GI DNS（Gi Interface DNS）** 使用
- 负责解析核心网 **Gi 接口** 的 DNS 请求
- 为移动用户的数据业务提供域名解析服务
- 相当于移动网络出公网的第一级 DNS

## 交付物

- 各模块（DNS / DHCP / IPAM）安装配置完成
- DNS 设备与现网核心网元链路互通
- 域名解析策略配置
- 高可用部署（HA 双机热备）
- 灾备切换演练
