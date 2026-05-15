#!/usr/bin/env python3
"""
HorseLink 🐎 — Agent P2P IM System
====================================
AI Agent 之间的实时通信系统。
支持 Relay/Client 两种模式，WebSocket 传输。

用法:
  # Relay 模式 (有公网 IP 的节点)
  python horselink.py --mode relay --name cloud-horse --secret mykey --port 8765

  # Client 模式 (内网节点)
  python horselink.py --mode client --name local-horse --secret mykey \\
    --connect ws://VPS_IP:8765

  # 交互模式: 连接后可以在终端输入消息发送
  # 输入格式: 目标Peer名称|消息内容 (例如: local-horse|你好!)
"""

import asyncio
import json
import uuid
import argparse
import logging
import sys
import os
import signal
from datetime import datetime, timezone

# ── 依赖检查 ──
try:
    import websockets
except ImportError:
    print("❌ 需要安装 websockets: pip install websockets")
    sys.exit(1)

# ═══════════════════════════════════════════════
# 消息协议定义
# ═══════════════════════════════════════════════

MSG_AUTH = "auth"
MSG_CHAT = "chat"
MSG_STATUS = "status"
MSG_TASK = "task"
MSG_KNOWLEDGE_SYNC = "knowledge_sync"
MSG_ACK = "ack"
MSG_PING = "ping"
MSG_PONG = "pong"
MSG_PEER_LIST = "peer_list"
MSG_ERROR = "error"

ALL_MSG_TYPES = {
    MSG_AUTH, MSG_CHAT, MSG_STATUS, MSG_TASK,
    MSG_KNOWLEDGE_SYNC, MSG_ACK, MSG_PING, MSG_PONG,
    MSG_PEER_LIST, MSG_ERROR
}


def make_msg(msg_type: str, from_name: str, to: str,
             payload: dict, ack: bool = False) -> dict:
    """创建符合协议的消息字典"""
    return {
        "type": "message",
        "version": "1.0",
        "msg_id": str(uuid.uuid4()),
        "msg_type": msg_type,
        "from": from_name,
        "to": to,
        "payload": payload,
        "ack_required": ack,
        "timestamp": datetime.now(timezone.utc).isoformat()
    }


def validate_msg(msg: dict) -> str | None:
    """校验消息格式，返回错误信息或 None"""
    if not isinstance(msg, dict):
        return "Message must be a JSON object"
    if msg.get("type") != "message":
        return "Missing or invalid 'type' (expected 'message')"
    if msg.get("msg_type") not in ALL_MSG_TYPES:
        return f"Unknown msg_type: {msg.get('msg_type')}"
    if not msg.get("from"):
        return "Missing 'from' field"
    if not msg.get("to"):
        return "Missing 'to' field"
    if "payload" not in msg:
        return "Missing 'payload' field"
    return None


def fmt(msg: dict) -> str:
    """格式化消息为可读字符串"""
    t = msg.get("msg_type", "?")
    f = msg.get("from", "?")
    to = msg.get("to", "?")
    pl = msg.get("payload", {})
    body = pl.get("body", pl.get("subject", pl.get("error", "")))
    time_str = msg.get("timestamp", "")[11:19]  # HH:MM:SS
    return f"[{time_str}] [{t}] {f} → {to}: {body}"


# ═══════════════════════════════════════════════
# Peer 节点
# ═══════════════════════════════════════════════

class Peer:
    """
    HorseLink 对等节点。

    mode="relay": 运行 WebSocket 服务器，接受客户端连接，路由消息
    mode="client": 连接 Relay 服务器，收发消息
    """

    def __init__(
        self,
        name: str,
        secret: str,
        mode: str = "client",
        host: str = "0.0.0.0",
        port: int = 8765,
        connect_uri: str | None = None,
        log_level: int = logging.INFO,
    ):
        self.name = name
        self.secret = secret
        self.mode = mode
        self.host = host
        self.port = port
        self.connect_uri = connect_uri

        # 运行时状态
        self.running = False
        self._relay_peers: dict[str, object] = {}    # mode=relay: name → websocket
        self._relay_ws: object | None = None         # mode=client: 连到的 relay
        self._callbacks: list = []                    # 消息回调
        self._on_peer_join = None
        self._on_peer_leave = None
        self._server = None

        # 日志
        self.log = logging.getLogger(f"HL-{name}")
        self.log.setLevel(log_level)
        if not self.log.handlers:
            h = logging.StreamHandler()
            h.setFormatter(logging.Formatter(
                "[%(asctime)s] [%(name)s] %(levelname)s: %(message)s",
                datefmt="%H:%M:%S"
            ))
            self.log.addHandler(h)

    # ── 公开 API ──

    def on_message(self, callback):
        """注册消息回调: callback(msg_dict)"""
        self._callbacks.append(callback)

    def on_peer_join(self, callback):
        """注册 Peer 上线回调: callback(peer_name)"""
        self._on_peer_join = callback

    def on_peer_leave(self, callback):
        """注册 Peer 下线回调: callback(peer_name)"""
        self._on_peer_leave = callback

    async def start(self):
        """启动节点（阻塞直到停止）"""
        self.running = True

        if self.mode == "relay":
            await self._run_relay()
        elif self.mode == "client":
            await self._run_client()
        else:
            self.log.error(f"未知模式: {self.mode}")

    def stop(self):
        """停止节点"""
        self.running = False
        if self._server:
            self._server.close()

    async def send(self, to: str, msg_type: str = MSG_CHAT,
                   payload: dict | None = None, ack: bool = False) -> str | None:
        """发送消息。返回 msg_id 或 None"""
        if payload is None:
            payload = {}
        msg = make_msg(msg_type, self.name, to, payload, ack)

        if self.mode == "relay":
            ws = self._relay_peers.get(to)
            if ws:
                try:
                    await ws.send(json.dumps(msg, ensure_ascii=False))
                except Exception as e:
                    self.log.warning(f"发送到 {to} 失败: {e}")
                    return None
                return msg["msg_id"]
            else:
                self.log.warning(f"目标不在线: {to}")
                return None

        elif self.mode == "client" and self._relay_ws:
            try:
                await self._relay_ws.send(json.dumps(msg, ensure_ascii=False))
            except Exception as e:
                self.log.warning(f"发送失败: {e}")
                return None
            return msg["msg_id"]

        return None

    # ── Relay 模式 ──

    async def _run_relay(self):
        self.log.info(f"🚀 Relay 启动 | ws://{self.host}:{self.port}")

        async def handler(ws):
            peer_name = None
            try:
                # 等待认证 (30 秒超时)
                raw = await asyncio.wait_for(ws.recv(), timeout=30)
                msg = json.loads(raw)

                if msg.get("msg_type") != MSG_AUTH:
                    await ws.send(json.dumps(make_msg(
                        MSG_ERROR, self.name, "?",
                        {"error": "首条消息必须是 auth"}
                    ), ensure_ascii=False))
                    return

                peer_name = msg.get("from")
                secret = msg.get("payload", {}).get("secret")

                if secret != self.secret:
                    await ws.send(json.dumps(make_msg(
                        MSG_ERROR, self.name, peer_name or "?",
                        {"error": "认证失败: secret 不匹配"}
                    ), ensure_ascii=False))
                    return

                self._relay_peers[peer_name] = ws
                self.log.info(f"✅ Peer 上线: {peer_name}")

                # 发确认
                await ws.send(json.dumps(make_msg(
                    MSG_ACK, self.name, peer_name,
                    {"ack_for": msg["msg_id"], "status": "authenticated"}
                ), ensure_ascii=False))

                # 广播新 peer 加入
                join_msg = make_msg(
                    MSG_STATUS, self.name, "*",
                    {"event": "peer_joined", "peer": peer_name}
                )
                await self._broadcast(join_msg, exclude=peer_name)

                # 通知回调
                if self._on_peer_join:
                    self._on_peer_join(peer_name)

                # 发在线列表
                await ws.send(json.dumps(make_msg(
                    MSG_PEER_LIST, self.name, peer_name,
                    {"peers": [n for n in self._relay_peers if n != peer_name]}
                ), ensure_ascii=False))

                # 消息循环
                async for raw in ws:
                    try:
                        msg_in = json.loads(raw)
                        err = validate_msg(msg_in)
                        if err:
                            self.log.warning(f"消息校验失败: {err}")
                            await ws.send(json.dumps(make_msg(
                                MSG_ERROR, self.name, peer_name,
                                {"error": err}
                            ), ensure_ascii=False))
                            continue
                        await self._route(msg_in, ws)
                    except json.JSONDecodeError:
                        self.log.warning(f"收到非法 JSON: {raw[:100]}")
                        await ws.send(json.dumps(make_msg(
                            MSG_ERROR, self.name, peer_name,
                            {"error": "非法 JSON"}
                        ), ensure_ascii=False))

            except asyncio.TimeoutError:
                self.log.warning(f"⏱ 认证超时: {ws.remote_address}")
            except websockets.exceptions.ConnectionClosed:
                pass
            except Exception as e:
                self.log.error(f"Handler 异常: {e}")
            finally:
                if peer_name and peer_name in self._relay_peers:
                    del self._relay_peers[peer_name]
                    self.log.info(f"❌ Peer 下线: {peer_name}")
                    leave_msg = make_msg(
                        MSG_STATUS, self.name, "*",
                        {"event": "peer_left", "peer": peer_name}
                    )
                    await self._broadcast(leave_msg, exclude=peer_name)
                    if self._on_peer_leave:
                        self._on_peer_leave(peer_name)

        self._server = await websockets.serve(
            handler, self.host, self.port,
            ping_interval=30, ping_timeout=10,
        )
        await self._server.wait_closed()

    # ── Client 模式 ──

    async def _run_client(self):
        if not self.connect_uri:
            self.log.error("Client 模式需要 --connect 参数")
            return

        self.log.info(f"🚀 Client 启动 | 连接 → {self.connect_uri}")

        retry_delay = 5

        while self.running:
            try:
                async with websockets.connect(
                    self.connect_uri,
                    ping_interval=30,
                    ping_timeout=10,
                ) as ws:
                    self._relay_ws = ws
                    self.log.info(f"✅ 已连接到 Relay")
                    retry_delay = 5  # 重置重连间隔

                    # 发送认证
                    await ws.send(json.dumps(make_msg(
                        MSG_AUTH, self.name, "relay",
                        {"secret": self.secret}
                    ), ensure_ascii=False))

                    # 消息循环
                    async for raw in ws:
                        try:
                            msg = json.loads(raw)
                            self._dispatch(msg)
                        except json.JSONDecodeError:
                            pass

            except (websockets.exceptions.ConnectionClosed,
                    ConnectionRefusedError,
                    OSError) as e:
                if self.running:
                    self.log.warning(f"🔄 断开 ({e}), {retry_delay}s 后重连...")
                    await asyncio.sleep(retry_delay)
                    retry_delay = min(retry_delay * 2, 60)
                else:
                    break
            except Exception as e:
                self.log.error(f"Client 异常: {e}")
                if self.running:
                    await asyncio.sleep(10)

    # ── 消息路由 (Relay 内部) ──

    async def _route(self, msg: dict, sender_ws):
        msg_type = msg.get("msg_type", "")
        to = msg.get("to", "")
        frm = msg.get("from", "")

        # 心跳
        if msg_type == MSG_PING:
            await sender_ws.send(json.dumps(
                make_msg(MSG_PONG, self.name, frm, {}),
                ensure_ascii=False
            ))
            return

        # 分发
        targets = []

        if to == "*" or to == "all":
            # 广播给所有其他 peer
            targets = [(n, ws) for n, ws in self._relay_peers.items() if n != frm]
        elif to in self._relay_peers:
            # 点对点
            targets = [(to, self._relay_peers[to])]
        elif to == self.name:
            # 给自己（Relay 自己消费）
            self._dispatch(msg)
            return
        else:
            self.log.warning(f"未知目标: {to}")
            await sender_ws.send(json.dumps(make_msg(
                MSG_ERROR, self.name, frm,
                {"error": f"目标不在线: {to}"}
            ), ensure_ascii=False))
            return

        for name, ws in targets:
            try:
                await ws.send(json.dumps(msg, ensure_ascii=False))
            except Exception as e:
                self.log.warning(f"转发到 {name} 失败: {e}")

    async def _broadcast(self, msg: dict, exclude: str | None = None):
        """广播给所有连接的 peers"""
        for name, ws in self._relay_peers.items():
            if exclude and name == exclude:
                continue
            try:
                await ws.send(json.dumps(msg, ensure_ascii=False))
            except Exception:
                pass

    # ── 消息分发 ──

    def _dispatch(self, msg: dict):
        """分发消息到所有回调"""
        for cb in self._callbacks:
            try:
                cb(msg)
            except Exception as e:
                self.log.error(f"回调异常: {e}")


# ═══════════════════════════════════════════════
# CLI 交互模式
# ═══════════════════════════════════════════════

def start_interactive(peer: Peer):
    """启动终端交互线程（从 stdin 读取消息）"""
    import threading

    def reader():
        print()
        print("=" * 50)
        print(f"🐎 HorseLink 交互模式")
        print(f"   你: {peer.name} ({peer.mode})")
        print(f"   输入: '目标名称|消息内容' 发送")
        print(f"   输入: 'help' 查看帮助")
        print(f"   输入: 'peers' 查看在线列表")
        print(f"   输入: 'quit' 退出")
        print("=" * 50)
        print()

        while peer.running:
            try:
                line = input("> ").strip()
                if not line:
                    continue

                if line.lower() == "quit" or line.lower() == "exit":
                    print("👋 再见!")
                    peer.stop()
                    break

                if line.lower() == "help":
                    print("格式: 目标|消息")
                    print("例如: local-horse|你好!")
                    print("     all|大家注意")
                    print("命令: peers, help, quit")
                    continue

                if line.lower() == "peers":
                    if peer.mode == "relay":
                        peers = list(peer._relay_peers.keys())
                        print(f"在线: {peers}")
                    else:
                        print("在线列表由 Relay 推送")
                    continue

                if "|" in line:
                    target, _, body = line.partition("|")
                    target = target.strip()
                    body = body.strip()
                    if target and body:
                        msg_id = asyncio.run_coroutine_threadsafe(
                            peer.send(target, MSG_CHAT,
                                      {"subject": f"来自 {peer.name}", "body": body}),
                            _event_loop
                        ).result()
                        if msg_id:
                            print(f"✓ 已发送 → {target}")
                        else:
                            print(f"✗ 发送失败: {target} 可能不在线")
                    else:
                        print("格式: 目标|消息")
                else:
                    # 默认发送给所有
                    target = "*"
                    body = line
                    msg_id = asyncio.run_coroutine_threadsafe(
                        peer.send(target, MSG_CHAT,
                                  {"subject": f"来自 {peer.name}", "body": body}),
                        _event_loop
                    ).result()
                    if msg_id:
                        print(f"✓ 已广播")

            except (EOFError, KeyboardInterrupt):
                print()
                peer.stop()
                break

    t = threading.Thread(target=reader, daemon=True)
    t.start()


def make_message_printer(peer_name: str):
    """创建打印消息的回调"""
    def printer(msg):
        frm = msg.get("from", "?")
        if frm == peer_name:
            return  # 不打印自己发的
        to = msg.get("to", "?")
        mt = msg.get("msg_type", "?")
        pl = msg.get("payload", {})

        # 静默处理心跳和确认
        if mt in ("ping", "pong", "ack"):
            return

        # 状态事件
        if mt == MSG_STATUS:
            event = pl.get("event", "")
            peer = pl.get("peer", "")
            if event == "peer_joined":
                print(f"\n🟢 新 peer 上线: {peer}")
            elif event == "peer_left":
                print(f"\n🔴 Peer 下线: {peer}")
            print("> ", end="", flush=True)
            return

        # 在线列表
        if mt == MSG_PEER_LIST:
            peers = pl.get("peers", [])
            if peers:
                print(f"\n📋 在线 peers: {', '.join(peers)}")
            else:
                print(f"\n📋 没有其他在线 peer")
            print("> ", end="", flush=True)
            return

        # 聊天 / 任务 / 其他
        body = pl.get("body") or pl.get("subject") or json.dumps(pl, ensure_ascii=False)
        print(f"\n💬 [{mt}] {frm}: {body}")
        print("> ", end="", flush=True)

    return printer


# ═══════════════════════════════════════════════
# 入口
# ═══════════════════════════════════════════════

def main():
    parser = argparse.ArgumentParser(
        description="🐎 HorseLink — Agent P2P IM System",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 云马 (VPS, Relay 模式)
  python horselink.py --mode relay --name cloud-horse --secret s3cret --port 8765

  # 本地马 (Client 模式)
  python horselink.py --mode client --name local-horse --secret s3cret \\
    --connect ws://1.2.3.4:8765
        """
    )
    parser.add_argument("--mode", choices=["relay", "client"], default="client",
                        help="运行模式 (默认: client)")
    parser.add_argument("--name", required=True,
                        help="Peer 名称 (如 cloud-horse, local-horse)")
    parser.add_argument("--secret", required=True,
                        help="共享密钥，用于身份认证")
    parser.add_argument("--host", default="0.0.0.0",
                        help="Relay 监听地址 (默认: 0.0.0.0)")
    parser.add_argument("--port", type=int, default=8765,
                        help="Relay 监听端口 (默认: 8765)")
    parser.add_argument("--connect",
                        help="Client 连接 URI (如 ws://1.2.3.4:8765)")
    parser.add_argument("--quiet", action="store_true",
                        help="静默模式，只打印消息不打印日志")

    args = parser.parse_args()

    # 参数校验
    if args.mode == "relay" and args.port < 1024 and os.geteuid() != 0:
        print("⚠️  使用小于 1024 的端口需要 root 权限")
        sys.exit(1)

    if args.mode == "client" and not args.connect:
        print("❌ Client 模式需要 --connect 参数")
        sys.exit(1)

    # 日志
    log_level = logging.WARNING if args.quiet else logging.INFO

    # 创建 peer
    peer = Peer(
        name=args.name,
        secret=args.secret,
        mode=args.mode,
        host=args.host,
        port=args.port,
        connect_uri=args.connect,
        log_level=log_level,
    )

    # 注册消息回调（打印到终端）
    peer.on_message(make_message_printer(args.name))

    # 启动交互
    start_interactive(peer)

    # 运行事件循环
    global _event_loop
    _event_loop = asyncio.new_event_loop()
    asyncio.set_event_loop(_event_loop)

    # 处理 Ctrl+C
    try:
        _event_loop.run_until_complete(peer.start())
    except KeyboardInterrupt:
        print("\n👋 HorseLink 已停止")
        peer.stop()


_event_loop = None

if __name__ == "__main__":
    main()
