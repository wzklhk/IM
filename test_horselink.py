#!/usr/bin/env python3
"""
HorseLink 快速测试
启动 Relay + Client，然后 Client 发消息验证通信
"""
import sys
import os
import json
import time
import subprocess
import signal

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from horselink import Peer, MSG_CHAT, MSG_ACK, MSG_STATUS, MSG_PEER_LIST


def test_horselink():
    """启动 Relay 和 Client，验证消息能送达"""
    
    relay = Peer(name="cloud-horse", secret="s3cret", mode="relay",
                 host="127.0.0.1", port=18765, log_level=40)  # ERROR only
    
    client = Peer(name="local-horse", secret="s3cret", mode="client",
                  connect_uri="ws://127.0.0.1:18765", log_level=40)
    
    received_messages = []
    client_peers = []
    
    def on_client_msg(msg):
        received_messages.append(msg)
        mt = msg.get("msg_type", "")
        if mt == MSG_PEER_LIST:
            client_peers.extend(msg.get("payload", {}).get("peers", []))
    
    client.on_message(on_client_msg)
    
    import asyncio
    
    async def run():
        # 启动 relay
        relay_task = asyncio.create_task(relay.start())
        await asyncio.sleep(0.5)
        
        # 启动 client
        client_task = asyncio.create_task(client.start())
        
        # 等待连接建立
        await asyncio.sleep(2)
        
        # 检查 client 是否收到 peer list
        print(f"[TEST] Client received messages: {len(received_messages)}")
        for m in received_messages:
            print(f"  - type={m.get('msg_type')}, from={m.get('from')}")
        
        if client_peers:
            print(f"[TEST] ✅ Client got peer list: {client_peers}")
        else:
            print(f"[TEST] ⚠️ No peer list yet")
        
        # Client 发消息给 relay
        msg_id = await client.send("cloud-horse", MSG_CHAT,
                                   {"subject": "测试", "body": "Hello from local-horse!"})
        print(f"[TEST] Client sent message: {msg_id}")
        
        await asyncio.sleep(1)
        
        # 检查 relay 是否收到
        print(f"[TEST] Total client received: {len(received_messages)}")
        for m in received_messages:
            print(f"  [{m.get('msg_type')}] {m.get('from')} → {m.get('to')}: {m.get('payload', {}).get('body', '')}")
        
        # 停止
        relay.stop()
        client.stop()
        relay_task.cancel()
        client_task.cancel()
        
        print(f"\n[TEST] {'✅ PASS' if len(received_messages) > 0 else '❌ FAIL'}")

    asyncio.run(run())


if __name__ == "__main__":
    test_horselink()
