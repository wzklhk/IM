# VMess+WebSocket Share Link & QR Guide

Use this reference when the user needs a VMess proxy share link for v2rayNG or similar clients.

## VMess share link format

VMess uses a **base64-encoded JSON** in the format `vmess://<base64>` — unlike VLESS which uses a URI format.

### JSON structure

```json
{
  "v": "2",
  "ps": "广州回国代理",
  "add": "<SERVER_IP>",
  "port": "<PORT>",
  "id": "<UUID>",
  "aid": "0",
  "scy": "auto",
  "net": "ws",
  "type": "none",
  "host": "",
  "path": "/ray",
  "tls": ""
}
```

### Generation (Python)

```python
import base64, json

config = {
    "v": "2",
    "ps": "广州回国代理",         # display name
    "add": "101.33.254.34",       # server IP
    "port": "10086",              # port (string!)
    "id": "<UUID>",               # user UUID
    "aid": "0",                   # alterId (0 for AEAD)
    "scy": "auto",                # encryption
    "net": "ws",                  # network: ws / tcp / kcp / etc.
    "type": "none",               # header type
    "host": "",                   # HTTP host (optional)
    "path": "/ray",              # WebSocket path
    "tls": ""                     # TLS: "tls" or ""
}

json_str = json.dumps(config, separators=(',', ':'))
encoded = base64.b64encode(json_str.encode()).decode()
link = f'vmess://{encoded}'
print(link)
```

### Key details

- All values in the JSON must be **strings** (even port, aid, v)
- Use `separators=(',', ':')` for compact JSON (no spaces)
- Standard base64, not URL-safe base64
- The entire link including `vmess://` prefix fits in a QR code

### QR code generation

```bash
sudo apt-get install -y qrencode
qrencode -s 10 -o /tmp/vmess_qr.png 'vmess://<full_base64_link>'
```

### Verification

```bash
sudo apt-get install -y zbar-tools
zbarimg -q /tmp/vmess_qr.png
# Expected: QR-Code:vmess://<correct_link>
```

## Common pitfalls

### Port must be a string
In the JSON, `"port": "10086"` not `10086` (number). Some parsers silently fail on integer ports.

### aid/alterId must be 0
Modern V2Ray (5.x+) uses AEAD encryption. alterId must be 0. Non-zero values cause connection failures.

### scy: "auto" vs "aes-128-gcm"
"auto" lets the server negotiate. Safe for all modern clients. Old clients (v2rayNG < 2.x) may need explicit "aes-128-gcm".

### No security in VMess+WS
WebSocket without TLS is **plaintext**. Any observer on the network can see the full traffic. Fine for 回国代理 (getting a Chinese IP), not for sensitive data.

## Debugging

If the user says "invalid password" or connection fails:

1. **Check the UUID** matches server config
2. **Try manual entry** instead of QR/scan (field by field in v2rayNG)
3. **V2rayNG version** — the latest is always best
4. **Server logs**: `docker logs v2ray --tail 20`, look for "rejected" entries
