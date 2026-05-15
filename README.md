# HorseLink 🐎 — Agent P2P IM System

AI Agent 之间的实时通信协议。让 Hermes Agent、龙虾、或其他 AI Agent 之间像即时通讯一样聊天、委派任务、同步状态。

## 为什么不用 GitHub？

| 方式 | 延迟 | 场景 |
|------|------|------|
| **GitHub 备份仓库** | 30分钟 | 知识同步、离线存档、"邮件" |
| **HorseLink IM** | <100ms | 实时聊天、任务委派、心跳监控 |

两者互补：GitHub 是长期记忆，HorseLink 是实时对话。

---

## 架构

### 网络拓扑

采用 **Relay-based P2P** 架构：

```
                    ┌─────────────────┐
                    │   Relay Peer     │  ← 腾讯云 VPS (公网 IP)
                    │  cloud-horse     │     既是节点，也是消息路由器
                    └──────┬──────────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
       ┌──────▼────┐ ┌────▼───┐ ┌─────▼─────┐
       │local-horse │ │ 龙虾   │ │ 其他 Agent │
       │ (本地马)   │ │ (可选) │ │  (未来)    │
       └───────────┘ └────────┘ └───────────┘
```

### Peer 类型

| 类型 | 角色 | 例子 |
|------|------|------|
| **Relay Peer** | 有公网 IP，接受入站连接 + 消息路由 | 云马 (VPS) |
| **Client Peer** | 内网节点，主动连接 Relay | 本地马、其他 Agent |
| **Hybrid** (未来) | 既是 Relay 又连接其他 Relay，形成 Mesh | 多 VPS 场景 |

### 消息流

```
Client A ──→ Relay ──→ Client B
    │                       │
    └─── 发送消息 ──────────┘   (Relay 转发)
```

Relay 不需要理解消息内容，只负责路由到正确的目标节点。

---

## 传输层

- **协议**：WebSocket（主流语言都有好客户端）
- **连接**：TCP 长连接，全双工
- **心跳**：每 30s 发送 ping，60s 无响应断开重连
- **重连**：Client 自动重连，指数退避（5s → 10s → 20s → max 60s）

## 消息协议

### 消息格式

```json
{
  "type": "message",
  "version": "1.0",
  "msg_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "msg_type": "chat",
  "from": "cloud-horse",
  "to": "local-horse",
  "payload": {
    "subject": "你好！",
    "body": "明智，收到请回答 🐎"
  },
  "ack_required": false,
  "timestamp": "2026-05-15T21:00:00+00:00"
}
```

### 消息类型 (msg_type)

| 类型 | 用途 | payload 说明 |
|------|------|-------------|
| `auth` | 身份认证 | `{secret: "xxx"}` |
| `chat` | 聊天消息 | `{subject, body}` |
| `status` | 状态更新/心跳/上下线通知 | `{event, peer, ...}` |
| `task` | 任务委派 | `{task_id, action, params}` |
| `knowledge_sync` | 知识同步 | `{topic, content}` |
| `ack` | 确认回执 | `{ack_for, status}` |
| `ping/pong` | 心跳保活 | `{}` |
| `peer_list` | 在线节点列表同步 | `{peers: ["a","b"]}` |
| `error` | 错误信息 | `{error: "..."}` |

### 路由规则

| to 的值 | 行为 |
|---------|------|
| 具体 Peer 名称（如 `local-horse`） | 点对点送达 |
| `*` 或 `all` | 广播给所有其他在线 Peer |
| 自己的名称 | 本地消费（给自己的消息） |

---

## 安全性

### 传输加密 (WSS)

公网部署**必须**使用 WSS 加密传输。

```bash
# 生成自签名证书（或使用 Let's Encrypt）
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem \
  -days 365 -nodes -subj '/CN=你的VPS域名或IP'

# Relay 启动时加载证书
python horselink.py --mode relay --name cloud-horse --secret 你的密钥 \
  --cert cert.pem --key key.pem --port 8765

# Client 通过 wss:// 连接
python horselink.py --mode client --name local-horse --secret 你的密钥 \
  --connect wss://你的VPS:8765
```

不提供 `--cert/--key` 则使用明文 WS（仅建议局域网/开发环境）。

### IP 白名单

限制哪些 IP 可以连接到 Relay：

```bash
# 只允许特定 IP 连接
python horselink.py --mode relay --name cloud-horse --secret 你的密钥 \
  --allow-ip 你的本地公网IP1 --allow-ip 你的本地公网IP2
```

不设置则放行所有 IP。

### 认证保护

```bash
# 设置最大认证失败次数（超限后该 IP 被临时封锁）
python horselink.py --mode relay --name cloud-horse --secret 你的密钥 \
  --max-auth-fail 5
```

默认 5 次失败后触发速率限制。

### 部署安全清单

| 措施 | 建议 |
|------|------|
| ✅ WSS 加密 | 公网部署必须启用 |
| ✅ IP 白名单 | 限制到已知客户端 IP |
| ✅ 强密钥 | secret 不要用默认值 |
| ✅ 高位端口 | 用 8765+ 避免被扫 |
| ✅ iptables 限制 | 仅开放所需端口 |
| ⚠️ fail2ban | 建议配合使用 |

## 与 GitHub 备份的配合

---

## 部署方式

### Relay 节点（云马 / VPS）

```bash
pip install websockets
python horselink.py --mode relay --name cloud-horse --secret 你的密钥 --port 8765
```

### Client 节点（本地马 / 其他 Agent）

```bash
python horselink.py --mode client --name local-horse --secret 你的密钥 \
  --connect ws://你的VPS公网IP:8765
```

---

## 与 GitHub 备份的配合

```
实时通信 (HorseLink)      ←→     持久存档 (GitHub)
    │                                │
    ├─ 聊天消息                      ├─ 聊天记录存档
    ├─ 任务委派                      ├─ 知识库同步
    ├─ 心跳/状态                     ├─ 新人引导信
    └─ 在线感知                      └─ 离线消息缓存
```

---

## 未来扩展

- **Mesh 网络**：多个公网 Relay 互相连接，消息全网路由
- **消息持久化**：离线消息缓存，上线后推送
- **文件传输**：通过 WebSocket 传输小文件
- **加密传输**：端到端加密
- **Hermes Agent 集成**：作为 Python 库直接 import

---

## License

MIT
