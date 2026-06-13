# VLESS+REALITY Proxy Config Reference

Use this when the user wants encrypted proxy on a Chinese VPS but has no domain. REALITY mimics TLS handshake of a real website — no certificate, no domain needed, higher stealth than WS+TLS.

## ⚠️ Critical: use Xray, not V2Fly

VLESS+REALITY requires **Xray**. V2Fly (v2fly/v2fly-core) does NOT properly support REALITY — you'll get `invalid "privateKey"` errors even with a valid x25519 key.

- **Use `teddysun/xray`** (Xray 26.5.9+) Docker image
- Config path: `/etc/xray/config.json`
- Entrypoint: `/usr/bin/xray -config /etc/xray/config.json`
- V2Fly key format (base64 with padding) is incompatible → **always generate keys with Xray's `xray x25519`**

### Xray-specific key generation

Xray uses **URL-safe base64 (no padding)** for keys, different from V2Fly/Python cryptography:

```bash
docker run --rm --entrypoint sh teddysun/xray -c "timeout 5 /usr/bin/xray x25519"
```

Output:
```
PrivateKey: <URL-safe-base64-no-padding>
Password (PublicKey): <URL-safe-base64-no-padding>
Hash32: ...
```

- **`PrivateKey`** → server config `realitySettings.privateKey`
- **`Password (PublicKey)`** → client config `realitySettings.publicKey`

> Xray-generated public keys are pure alphanumeric (no `+`, `/`, or `=`), so they work in QR/share links without URL-encoding issues.

## Server config.json

> ⚠️ **Server vs client `network`**: The server (v2fly/v2fly-core) MUST use `"network": "tcp"` for REALITY. Setting `"raw"` crashes with `unknown transport protocol: raw`. The Xray client uses `"network": "raw"`. This is a fundamental divergence between V2Fly and Xray — do not mirror the client's `raw` setting on the server.
> 
> ⚠️ **`dest` choice matters**: Not all domains work as REALITY destinations. `www.baidu.com` causes HTTP 000 (handshake fails). `www.microsoft.com` is tested and reliable. When choosing a `dest`, verify it works by testing with a local client container before deploying.

```json
{
  "log": { "loglevel": "warning" },
  "inbounds": [{
    "port": <PORT>,
    "protocol": "vless",
    "settings": {
      "clients": [{
        "id": "<UUID>"
      }],
      "decryption": "none"
    },
    "streamSettings": {
      "network": "tcp",
      "security": "reality",
      "realitySettings": {
        "dest": "www.microsoft.com:443",
        "serverNames": ["www.microsoft.com"],
        "privateKey": "<GENERATED_PRIVATE_KEY>",
        "shortIds": ["<HEX_ID>"]
      }
    }
  }],
  "outbounds": [
    { "protocol": "freedom", "tag": "direct" },
    { "protocol": "blackhole", "tag": "block" }
  ]
}
```

## Key generation

> ⚠️ **Always use Xray's `xray x25519` command.** Xray uses URL-safe base64 (no `+`, `/`, or `=`), different from standard base64. Python cryptography generates standard base64 keys that Xray rejects with `invalid "privateKey"`.

Generate the REALITY key pair using the Xray Docker image:

```bash
docker run --rm --entrypoint sh teddysun/xray -c "timeout 5 /usr/bin/xray x25519"
```

Output:
```
PrivateKey: <URL-safe-base64-no-padding>
Password (PublicKey): <URL-safe-base64-no-padding>
Hash32: ...
```

- **`PrivateKey`** → server config `realitySettings.privateKey`
- **`Password (PublicKey)`** → client config `realitySettings.publicKey`

> Xray-generated public keys are pure alphanumeric, so they work in QR/share links without URL-encoding issues.

### V2Fly fallback (not recommended)

If you MUST use V2Fly (v2fly/v2fly-core), generate via Python cryptography:

```bash
python3 -c "
from cryptography.hazmat.primitives.asymmetric.x25519 import X25519PrivateKey
import base64
key = X25519PrivateKey.generate()
print('PrivateKey:', base64.b64encode(key.private_bytes_raw()).decode())
print('PublicKey:', base64.b64encode(key.public_key().public_bytes_raw()).decode())
"
```

These keys use standard base64 with `=` padding and may contain `+`/`/` — incompatible with Xray.

### Short IDs

Generate via:
```bash
openssl rand -hex 4
```

Produces an 8-char hex string (e.g. `6ba85179e30d4fc2`). Use as `shortId` (or leave as empty string `""` for no shortId).

## Docker run

Choose the image matching your key generation method:

### With V2Fly (keys generated via Python cryptography)

```bash
docker run -d --name v2ray-reality --restart unless-stopped \
  -p <PORT>:<PORT> \
  -v ~/v2ray-reality/config.json:/etc/v2ray/config.json:ro \
  v2fly/v2fly-core run -c /etc/v2ray/config.json
```

### With Xray (keys generated via `xray x25519`)

```bash
docker run -d --name v2ray-reality --restart unless-stopped \
  -p <PORT>:<PORT> \
  -v ~/v2ray-reality/config.json:/etc/xray/config.json:ro \
  teddysun/xray
```

## Share link format for QR / clipboard import

> ✅ **Xray-generated keys don't need URL encoding.** Since Xray's `xray x25519` outputs pure alphanumeric base64 (no `+`, `/`, or `=`), you can use the public key raw in the share link without `urllib.parse.quote()`.
>
> ⚠️ The instructions below (with URL-encoding) only apply if you're stuck on V2Fly-generated keys that contain `+` and `=`.

When generating a VLESS share link, the `pbk` (publicKey) parameter MUST be URL-encoded if using V2Fly keys — they contain `+` and `=` which are special characters in URL query strings.

**❌ Wrong (raw copy from terminal):**
```
pbk=+udOdFA0zq5FpWmg0aLOsCM509d9PMJE03MmQZ8JLQM=
```
The `+` at the start is decoded as a space → client sees wrong publicKey → `infra/conf: invalid "password"` error.

**✅ Correct (URL-encoded):**
```bash
python3 -c "
import urllib.parse
link = f'vless://...?...&pbk={urllib.parse.quote(pubkey, safe=\"\")}&...'
print(link)
"
```
Result: `pbk=%2BudOdFA0zq5FpWmg0aLOsCM509d9PMJE03MmQZ8JLQM%3D`

Parameters needing encoding: `pbk` (contains `+`, `=`), `remark` (contains Chinese/Unicode), `sid` (hex, safe). Always URL-encode `pbk` and the fragment `#remark` with `urllib.parse.quote()`.

### Full link generation script

```python
import urllib.parse

uuid = '<UUID>'
server = '<SERVER_IP>'
port = '<PORT>'
pubkey = '<PUBLIC_KEY>'  # e.g. "+udOdFA0..."
shortid = '<HEX_ID>'     # e.g. "6ba85179e30d4fc2"
sni = 'www.microsoft.com'
remark = '广州回国代理'  # or any display name

link = (
    f'vless://{uuid}@{server}:{port}'
    f'?encryption=none&security=reality'
    f'&sni={sni}&fp=chrome'
    f'&pbk={urllib.parse.quote(pubkey, safe="")}'
    f'&sid={shortid}&type=tcp&headerType=none'
    f'#{urllib.parse.quote(remark)}'
)
print(link)
```

### QR code generation (import fallback)

If clipboard import fails (encoding issues, app limitations), generate a QR code:

```bash
# Install qrencode if needed
sudo apt-get install -y qrencode

# Generate QR from the share link
qrencode -s 10 -o /tmp/v2ray_qr.png '<full_share_link>'
```

Send the image to the user. v2rayNG: **+** → **Scan QR code**.

### Verification: decode a QR to check content

```bash
sudo apt-get install -y zbar-tools
zbarimg /tmp/v2ray_qr.png
# Should output: QR-Code:vless://<correct_link>
```

## Client config (Clash Meta format)

```yaml
proxies:
  - name: "广州回国"
    type: vless
    server: <SERVER_IP>
    port: <PORT>
    uuid: <UUID>
    cipher: none
    network: tcp
    tls: true
    servername: www.microsoft.com
    reality-opts:
      public-key: <PUBLIC_KEY>
      short-id: <HEX_ID>
    udp: true
```

## v2rayNG client setup (Android)

Manual configuration steps in v2rayNG:

1. Tap **+** → **[VLESS]**
2. Fill in:
   - **地址/Address**: `<SERVER_IP>`
   - **端口/Port**: `<PORT>`
   - **用户ID/ID**: `<UUID>`
   - **加密方式/Encryption**: `none`
   - **传输方式/Network**: `tcp`
   - **伪装类型/Header type**: `none`
3. Scroll down to **外层传输安全/Security**: select **Reality**
4. Fill Reality options:
   - **ServerName**: `www.microsoft.com`
   - **PublicKey**: `<PUBLIC_KEY>`
   - **ShortId**: `<HEX_ID>`
5. Save and connect

> ⚠️ ShortId is optional but recommended for better compatibility. If missing, some clients may fail to connect.

## Firewall (UFW) requirements

UFW must allow the proxy port. Check and add rule:

```bash
sudo ufw status | grep <PORT> || sudo ufw allow <PORT>/tcp comment 'V2Ray proxy'
```

REALITY traffic looks like normal HTTPS to the dest host — UFW doesn't need special config beyond opening the port.

### ⚠️ Cloud security group (critical!)

Chinese VPS providers (Tencent Cloud, Aliyun, Huawei Cloud) maintain a **separate firewall at the hypervisor level** — the security group. UFW rules inside the VM are NOT sufficient.

**Symptom:** UFW shows the port open, `ss -tlnp | grep <PORT>` shows listening, but external/self-connect tests time out (HTTP 000). The connection never reaches Xray.

**Fix:** Log into the cloud console and add an inbound allow rule for the port in the security group. Common default-open ports: 22, 80, 443, 3389. Any other port (10086, 30086, 31086) needs explicit approval in both UFW AND the security group.

**Check from outside:**
```bash
# If this times out, the security group is blocking it
curl -s --max-time 5 http://<PUBLIC_IP>:<PORT>/
```

> Some ports like 31086 may be pre-opened by the provider for specific users. Always verify which ports are actually open at the cloud level before deploying on a non-standard port.

## Docker networking pitfalls

### Xray IPv6-only binding

Xray binds to `::` (IPv6 any) by default. Docker container-to-container communication on custom/bridge networks uses **IPv4**. This causes connection failures even though the server appears to be listening.

**Symptom:** Local test client container can't reach the server (HTTP 000), server logs show no incoming connections.

**Fix:** Add `"listen": "0.0.0.0"` to the inbound config:

```json
"inbounds": [{
  "listen": "0.0.0.0",
  "port": <PORT>,
  ...
}]
```

After making this change, verify the server listens on both IPv4 and IPv6:
```bash
docker exec <NAME> ss -tlnp | grep <PORT>
# Should show both 0.0.0.0:<PORT> and :::<PORT>
```

### Container recreate loses custom networks

When you `docker rm` + `docker run` a container, any custom Docker networks it was attached to (e.g. via `docker network connect`) are **permanently lost**. The new container has the same name but is a different object.

**Fix:** Always reconnect after recreate:
```bash
docker network connect <NETWORK> <CONTAINER_NAME>
```

**Alternative:** Use `docker compose` or specify the network at creation time:
```bash
docker run -d --name <NAME> --network <NETWORK> ...
```

### Local test container setup

When debugging client connectivity issues, run a local Xray client on the same VPS to isolate the problem:

```bash
# 1. Create a Docker network
docker network create v2ray-net
docker network connect v2ray-net <SERVER_CONTAINER_NAME>

# 2. Write client config
mkdir -p ~/v2ray-client-test
cat > ~/v2ray-client-test/config.json << 'EOF'
{
  "log": {"loglevel": "warning"},
  "inbounds": [{"port": 1080, "protocol": "socks", "settings": {"auth": "noauth", "udp": true}}],
  "outbounds": [{
    "protocol": "vless",
    "settings": {
      "vnext": [{
        "address": "<CONTAINER_NAME>", "port": <PORT>,
        "users": [{"id": "<UUID>", "encryption": "none"}]
      }]
    },
    "streamSettings": {
      "network": "tcp", "security": "reality",
      "realitySettings": {
        "serverName": "www.microsoft.com", "fingerprint": "chrome",
        "publicKey": "<PUBKEY>", "shortId": "<SHORTID>"
      }
    }
  }]
}
EOF

# 3. Run on the same network
docker run -d --name v2ray-test --rm \
  --network v2ray-net \
  -p 11080:1080 \
  -v ~/v2ray-client-test/config.json:/etc/xray/config.json:ro \
  teddysun/xray

# 4. Test
curl -s --max-time 10 --socks5-hostname 127.0.0.1:11080 https://www.baidu.com
curl -s --max-time 10 --socks5-hostname 127.0.0.1:11080 https://ifconfig.me/ip

# 5. Cleanup
docker stop v2ray-test 2>/dev/null
```

A passing test (HTTP 200 + VPS public IP) confirms the server works — the problem is on the remote client side.

### Alternative: host bridge network test

If custom Docker networks are unavailable, test via the Docker bridge gateway:

```bash
# Find the gateway IP
GATEWAY=$(ip route | grep docker0 | awk '{print $1}' | cut -d/ -f1)
# Usually 172.17.0.1

# Use GATEWAY as the server address in client config


## Client-side pitfalls: VMess/sing-box legacy fields

A common cause of `infra/conf: invalid "password"` errors is **leftover fields from a previous VMess or sing-box config**, not a real password/UUID mismatch.

### Fields that MUST be removed from client config

| Field | Origin | Why it breaks |
|-------|--------|---------------|
| `"security": "auto"` | VMess | Xray tries to parse VLESS user with VMess logic → falls through to password validation → bogus `invalid password` error |
| `"mldsa65Verify": ""` | experimental | Post-quantum field, not stable across versions |

### Final client streamSettings

```json
"streamSettings": {
  "network": "raw",
  "security": "reality",
  "realitySettings": {
    "serverName": "www.microsoft.com",
    "fingerprint": "chrome",
    "publicKey": "<PUBLIC_KEY>",
    "shortId": "<HEX_ID>"
  }
}
```

### Final client users

```json
"users": [
  {
    "id": "<UUID>",
    "encryption": "none"
  }
]
```

> ⚠️ Do NOT add `flow: "xtls-rprx-vision"` unless the server also requires it. If the server requires flow but the client (e.g. v2rayNG) doesn't send it, Xray rejects with `client flow is empty`. For maximum v2rayNG compatibility, omit flow entirely.

> ⚠️ If your client was previously configured for VMess or sing-box, **start fresh** — don't carry over old fields. The `invalid password` error with a seemingly correct publicKey is almost always caused by `security: auto` polluting the VLESS REALITY parser.

## Key differences from VMess+WS

| Aspect | VMess+WS | VLESS+REALITY |
|--------|----------|---------------|
| Protocol | vmess | vless |
| Need domain? | ✅ (for TLS) | ❌ |
| Need certificate? | ✅ | ❌ |
| Encryption | none (WS) / TLS | built-in REALITY |
| Handshake mimic | ❌ | ✅ mimics Microsoft/Cloudflare |
| detectable by GFW | ⭐ (WS plaintext) | ⭐⭐⭐⭐⭐ (almost impossible) |
| Client support | All clients | Most modern clients (v2rayNG, Clash Meta, Sing-box) |
