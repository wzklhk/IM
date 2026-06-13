---
name: vps-service-deployment
description: Chinese VPS server operations — Docker service deployment & build optimization (mirrors), UFW firewall management, proxy setup (VMess+WS, VLESS+REALITY), server security audit, SSH/SFTP config, telecom NE patterns.
triggers:
  - "部署服务"
  - "docker run 服务"
  - "开放端口"
  - "UFW 防火墙"
  - "V2Ray 部署"
  - "回国代理"
  - "反代"
  - "代理服务"
  - "reality"
  - "vless"
  - "vmess"
  - "docker 端口映射"
  - "服务器防火墙"
  - "docker build 超时"
  - "pip install timeout"
  - "npm install timeout"
  - "国内 VPS Docker"
  - "docker build slow China"
  - "镜像源"
  - "npmmirror"
  - "pypi tuna"
  - "安全审计"
  - "SSH 端口"
  - "SFTP"
  - "端口扫描"
  - "socket activation"
  - "sshd"
category: devops
---

# VPS Service Deployment

## Quick reference

| Step | Command |
|------|---------|
| Check if port is free | `ss -tlnp \| grep <PORT>` |
| Current firewall rules | `sudo ufw status numbered` |
| Add firewall rule | `sudo ufw allow <PORT>/tcp comment 'description'` |
| Check container logs | `docker logs <NAME> --tail 50` |
| Verify container running | `docker ps --filter name=<NAME> --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"` |
| Get public IP | `curl -s ifconfig.me` |

## Service deployment checklist

Always follow this sequence:

1. **Check available space** — `df -h ~`
2. **Pull image** — `docker pull <image>:<tag>`
3. **Create config directory** — `mkdir -p ~/<service-name>`
4. **Check port availability** — `ss -tlnp | grep <PORT>` before binding
5. **Run container** with:
   - `--restart unless-stopped` (survives VPS reboot)
   - `-v <host-config-path>:<container-config-path>:ro` (config read-only)
   - `-p <HOST_PORT>:<CONTAINER_PORT>` (port mapping)
6. **Check firewall** — `sudo ufw status` → add rule if port not listed
7. **Verify** — check container logs + test connectivity from outside

## UFW firewall patterns (Chinese VPS)

UFW is usually active on fresh VPS. Only ports 80, 443, and SSH (custom) are open by default.

### Common commands

```bash
# Check status and rules
sudo ufw status numbered

# Allow a port
sudo ufw allow 10086/tcp comment 'description'

# Allow with specific source IP range
sudo ufw allow from <CIDR> to any port <PORT> proto tcp

# Delete a rule (find number first with `status numbered`)
sudo ufw delete <NUMBER>

# Reload after changes
sudo ufw reload
```

### Pitfall: UFW default deny + Docker

Docker containers are reachable even without UFW rules if Docker's iptables bypasses UFW. **Always add explicit UFW rules** — relying on Docker's NAT alone is fragile. Verify from an external VPS or client.

## Docker service patterns

### Port mapping

- Always use explicit `-p HOST:CONTAINER` (not `--network host`) for clarity
- Use memorable but non-obvious ports (e.g., 10086 not 1080/8080) to reduce scanner noise
- If port is taken, choose an adjacent port and communicate the change clearly

### Config management

- Mount config files read-only (`:ro`) after the volume path
- V2Ray: config goes in `/etc/v2ray/config.json` (v2fly/v2fly-core image)
- Keep config in `~/<service>/config.json` for easy edits: `docker restart <NAME>` after changes

### Lifecycle

```bash
# Start
docker run -d --name <NAME> --restart unless-stopped -p ... -v ... <IMAGE> <CMD>

# Restart (after config change)
docker restart <NAME>

# Stop + remove
docker stop <NAME> && docker rm <NAME>

# Logs
docker logs <NAME> --tail 50 -f
```

## V2Ray proxy deployment specifics

### Option A: VMess+WebSocket (no domain, no encryption)

Simplest option for 回国代理. No TLS, no certificate needed. Config:

```json
{
  "inbounds": [{
    "port": <PORT>,
    "protocol": "vmess",
    "settings": {
      "clients": [{
        "id": "<UUID>",
        "alterId": 0
      }]
    },
    "streamSettings": {
      "network": "ws",
      "wsSettings": { "path": "/ray" }
    }
  }],
  "outbounds": [
    { "protocol": "freedom", "tag": "direct" },
    { "protocol": "blackhole", "tag": "block" }
  ]
}
```

Generate UUID: `uuidgen`

Docker run:
```bash
docker run -d --name v2ray --restart unless-stopped \
  -p <PORT>:<PORT> \
  -v ~/v2ray/config.json:/etc/v2ray/config.json:ro \
  v2fly/v2fly-core run -c /etc/v2ray/config.json
```

### Option B: VLESS+REALITY (no domain needed, encrypted)

**Recommended when no domain is available.** Encrypts traffic by mimicking TLS handshake of a real website (microsoft.com, etc.). Higher stealth than WS+TLS — GFW sees only regular HTTPS to the target domain.

Key config differences from VMess+WS:
- Protocol: `vless` instead of `vmess` (no protocol-layer encryption — REALITY handles it)
- No `alterId` — VLESS doesn't use it
- Requires a REALITY key pair (see reference for generation)
- `decryption: "none"` is mandatory for VLESS
- ⚠️ `flow: "xtls-rprx-vision"` is OPTIONAL. If the server requires it but the client (e.g. v2rayNG) doesn't send it, Xray rejects with `client flow is empty`. For maximum v2rayNG compatibility, omit `flow` on both sides. Only add if you control the client config and can guarantee flow is sent.
- ⚠️ **Server vs client `network` differs**: Server (v2fly/v2fly-core) MUST use `"tcp"` — setting `"raw"` crashes with `unknown transport protocol: raw`. Xray clients use `"raw"`. Never set `"raw"` on the server side.
- ❌ Never carry over VMess field `"security": "auto"` into VLESS config — it causes a misleading `invalid password` error even when the UUID/publicKey are correct.

#### Server image choice: V2Fly vs Xray

| Aspect | v2fly/v2fly-core | teddysun/xray |
|--------|-----------------|---------------|
| REALITY support | ✅ Basic | ✅ Native (Xray originated REALITY) |
| Key format | Standard base64 with `=` padding | **URL-safe base64, NO padding** ⚠️ |
| Key generation | Python cryptography | `xray x25519` (inside container) |
| Config path | `/etc/v2ray/config.json` | `/etc/xray/config.json` |
| Docker Cmd | `run -c /etc/v2ray/config.json` | Default cmd is `-config /etc/xray/config.json` |
| `network: raw` support | ❌ Server crashes | ✅ |

**🔑 CRITICAL: Xray private key format is NOT compatible with V2Fly-generated keys.**

V2Fly (or Python cryptography) generates keys like `KGmC3FC7TMYr0Niqk26pGJfnMwLBq8BExRHUuXpCKX4=` (standard base64, `=` padding).

Xray generates keys like `cD-3f5cS2e7lcKV1V5f5b5YtncHf8ZhyeDo012x7vmI` (URL-safe base64, **no padding**, uses `-` and `_` instead of `+` and `/`).

**A V2Fly-generated privateKey in an Xray config will fail with `invalid "privateKey"`.** Always use Xray's own `xray x25519` to generate keys when deploying with Xray.

**Xray deployment (recommended for VLESS+REALITY):**

```bash
# 1. Generate key pair (inside Xray container — the only reliable way)
docker run --rm --entrypoint sh teddysun/xray -c "timeout 5 /usr/bin/xray x25519"

# Output:
# PrivateKey: <URL-safe-base64-no-padding>
# Password (PublicKey): <URL-safe-base64-no-padding>
# Hash32: ...

# 2. Write config.json with the Xray-generated PrivateKey
# Config path inside container: /etc/xray/config.json

# 3. Run
docker run -d --name v2ray-reality --restart unless-stopped \
  -p 443:443 \
  -v ~/v2ray-reality/config.json:/etc/xray/config.json:ro \
  teddysun/xray
```

> ⚠️ `xray x25519` sometimes hangs. Wrap with `timeout 5` as shown.
> ⚠️ `--entrypoint sh` is needed because the default Cmd runs `xray -config /etc/xray/config.json`, not bare `xray`.

**V2Fly deployment (when Xray is not desired):**

```bash
docker run -d --name v2ray-reality --restart unless-stopped \
  -p 443:443 \
  -v ~/v2ray-reality/config.json:/etc/v2ray/config.json:ro \
  v2fly/v2fly-core run -c /etc/v2ray/config.json
```

### Troubleshooting: local client test container

When a remote client reports `invalid password` but the server config looks correct, create a local Xray client container on the same VPS to isolate the issue. This proves whether the server works (HTTP 200 + VPS IP return) — if it does, the problem is on the remote client side.

**Best approach: use a Docker network** (more reliable than `host.docker.internal` on Linux):

```bash
mkdir -p ~/v2ray-client-test
cat > ~/v2ray-client-test/config.json << 'CONFEOF'
{
  "log": {"loglevel": "warning"},
  "inbounds": [{"port": 1080, "protocol": "socks", "settings": {"auth": "noauth", "udp": true}}],
  "outbounds": [{
    "protocol": "vless",
    "settings": {
      "vnext": [{
        "address": "v2ray-reality", "port": 443,
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
CONFEOF

docker network create v2ray-net 2>/dev/null
docker network connect v2ray-net v2ray-reality

docker run -d --name v2ray-test --rm \
  --network v2ray-net \
  -p 11080:1080 \
  -v ~/v2ray-client-test/config.json:/etc/xray/config.json:ro \
  teddysun/xray

curl -s --max-time 10 --socks5-hostname 127.0.0.1:11080 https://www.baidu.com
curl -s --max-time 10 --socks5-hostname 127.0.0.1:11080 https://ifconfig.me/ip

docker stop v2ray-test; rm -rf ~/v2ray-client-test
```

> ⚠️ Do NOT set `flow: xtls-rprx-vision` in the test client config unless the server also requires it. For v2rayNG compatibility, omit flow entirely.

**Alternative: `host.docker.internal`** (may fail on some Linux setups):

```bash
docker run -d --name v2ray-test --rm \
  --add-host host.docker.internal:host-gateway \
  -p 11080:1080 \
  -v ~/v2ray-client-test/config.json:/etc/xray/config.json:ro \
  teddysun/xray
  
# Then use address "host.docker.internal" instead of container name in the client config
```

A passing local test (HTTP 200 + VPS IP return) confirms the server works — the problem is on the remote client side.

> 💡 Recommend VLESS+REALITY over VMess+WS when the user has no domain.

### Client sharing format

**VMess:** Use base64-encoded `vmess://...` format.

**VLESS:** Use URI format `vless://uuid@server:port?...`.
⚠️ **Critical: URL-encode `pbk` parameter!** The publicKey contains `+` and `=` which get misinterpreted as spaces/separators in URL query strings. See `references/vless-reality-proxy.md` → "Share link format for QR / clipboard import" for the full generation script + QR code fallback.

**QR code fallback:** If clipboard import fails, generate QR with `qrencode -s 10 -o /tmp/qr.png '<link>'` and send as image. VMess QR codes use base64-encoded JSON; VLESS QR codes use URI format. Verify with `zbarimg -q /tmp/qr.png`.

**When REALITY fails on the client:** If the client cannot parse the VLESS+REALITY share link (common on some v2rayNG versions, even the latest), **revert to VMess+WS**. No URL-encoding pitfalls, works on every client, acceptable for 回国代理. Steps: (1) change server config to vmess+ws, (2) `docker restart v2ray`, (3) generate new VMess share link and QR.

### ⚠️ Xray: `listen: \"0.0.0.0\"` required for Docker networking

Xray binds to `:::PORT` (IPv6-only) by default. On Docker's internal bridge network (IPv4), this means the container is unreachable by container name — `container-name:PORT` connections hang or timeout.

Fix: always add `"listen": "0.0.0.0"` to the inbound config when running in Docker:

```json
{
  \"inbounds\": [{
    \"listen\": \"0.0.0.0\",
    \"port\": 443,
    ...
  }]
}
```

Without this, the container passes `docker ps` and appears healthy, but local client containers on the same Docker network cannot connect. The host port mapping (`-p HOST:CONTAINER`) still works in this case because Docker's proxy handles it, but internal network communication fails.

> ⚠️ This only affects Xray (teddysun/xray). V2Fly (v2fly/v2fly-core) binds to both IPv4 and IPv6 by default — it doesn't need `listen: 0.0.0.0`.

### ⚠️ REALITY `dest` domain must be tested before use

Not all domains work as REALITY `dest`. Some CDNs or origin servers reject the REALITY TLS handshake pattern, causing `HTTP 000` or connection drops:

| Domain | Works? | Latency from China |
|--------|--------|-------------------|
| `www.microsoft.com` | ✅ Reliable | ~0.27s |
| `www.bing.com` | ✅ | ~0.21s |
| `www.baidu.com` | ❌ Handshake rejected | 0.04s but fails |
| `cloudflare.com` | ❌ Timeout from China | >5s |

Always verify that the chosen `dest` responds to a real TLS handshake from the VPS before deploying:

```bash
curl -s --max-time 5 -o /dev/null -w \"%{http_code} %{time_total}s\\n\" https://www.microsoft.com
```

If it returns `000` or times out, pick a different domain.

### ⚠️ Chinese VPS: cloud security group blocks non-standard ports

Tencent Cloud, Alibaba Cloud, and Huawei Cloud all have a **hypervisor-level firewall** (security group) separate from UFW. By default, only ports 22, 80, 443, and sometimes 3389 are allowed inbound.

If a client reports connection timeout but the server is running and UFW allows the port, check the cloud provider's security group console. Quick test from the VPS itself:

```bash
# Self-connect via public IP to verify
curl -s --max-time 5 http://<PUBLIC_IP>:<PORT>/
# If this times out on port X but works on 443/80, security group is blocking it
```

**Workaround:** Use port 443 (always open) or add the port to the cloud security group via the provider's console.

## Docker build optimization (Chinese VPS mirrors)

When building Docker images from a Chinese VPS (Tencent Cloud, Alibaba Cloud, etc.), default package registries (npmjs.org, PyPI, deb.debian.org) are often throttled or unreachable. Builds time out on `pip install` or `npm install`.

### Quick mirror reference

| Package Manager | Default Source | China Mirror | Speedup |
|----------------|---------------|--------------|---------|
| **npm** | registry.npmjs.org | `https://registry.npmmirror.com` | 29s → 10s |
| **pip** | pypi.org | `https://pypi.tuna.tsinghua.edu.cn/simple` | timeout → 10s |
| **apt** | deb.debian.org | `mirrors.tuna.tsinghua.edu.cn` | 12min → 6s |

### Dockerfile patterns

**npm (Node.js):** Configure mirror *before* `npm install`:

```dockerfile
RUN npm config set registry https://registry.npmmirror.com && npm install
```

**pip (Python):** Use `pip config set global.index-url`:

```dockerfile
RUN pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple && \
    pip install --no-cache-dir -r requirements.txt
```

**apt (Debian):** Replace sources in the RUN layer:

```dockerfile
RUN sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list.d/debian.sources 2>/dev/null; \
    sed -i 's|security.debian.org|mirrors.tuna.tsinghua.edu.cn/debian-security|g' /etc/apt/sources.list 2>/dev/null; \
    sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list 2>/dev/null; \
    apt-get update && apt-get install -y --no-install-recommends PACKAGES && \
    rm -rf /var/lib/apt/lists/*
```

> ⚠️ Debian Trixie uses `/etc/apt/sources.list.d/debian.sources` (deb822 format) in addition to the old `sources.list`. Always sed-replace in both locations.

### Pitfalls

### ⚠️ Docker Hub base image pulls are slow from China
Dockerfile-level mirror config only affects package installs *inside* the build, not the base image pull. For daemon-level Docker Hub mirror, configure `registry-mirrors` in `/etc/docker/daemon.json`.

### ⚠️ Mirror config must come BEFORE install
`npm config set` / `pip config set` write config files that are only read by subsequent commands in the same or later RUN layers.

---

### Full example: Flask + Vue multi-stage build

See `references/docker-build-china-example.md` for a complete multi-stage Dockerfile with all three mirror types (npm/pip/apt) and a docker-compose.yml.

---

## Server security audit

Quick checklist for assessing the security posture of a Linux server (general-purpose or telecom NE).

### Port inventory

```bash
# What's listening, with PIDs
ss -tlnp | sort -t: -k2 -n

# Check specific dangerous protocols
ss -tlnp | awk 'NR==1 || /:(21|22|23|69|115|989|990) /'
# 21=FTP, 22=SSH/SFTP, 23=Telnet, 69=TFTP, 115=SFTP(standalone), 989/990=FTP-TLS
```

### When PID shows `-` (no root access)

```bash
# Try sudo first
sudo ss -tlnp

# Fallback: scan /proc by port (hex)
for port in 29029 10310; do
    hex=$(printf '%04X' $port)
    echo "=== Port $port (0x$hex) ==="
    grep ":$hex " /proc/*/net/tcp 2>/dev/null | while read line; do
        pid=$(echo $line | cut -d/ -f3)
        echo -n "PID: $pid  CMD: "
        cat /proc/$pid/cmdline 2>/dev/null | tr '\0' ' '
    done
done

# Also try fuser
fuser -v <PORT>/tcp
```

### SSH/SFTP configuration audit

```bash
# Is SFTP enabled?
grep -i '^Subsystem.*sftp' /etc/ssh/sshd_config

# Full active config (non-comment, non-empty)
grep -E '^[^#]' /etc/ssh/sshd_config | grep -v '^$'

# Check for active SFTP sessions
ps aux | grep sftp-server | grep -v grep
```

| Config line | Security risk if wrong | Recommendation |
|-------------|----------------------|----------------|
| `PermitRootLogin yes` | Root SSH access | Set to `no` |
| `PasswordAuthentication yes` | Password-based login | Set to `no`, key-only |
| `Subsystem sftp …` | SFTP active when not needed | Disable if unused |
| `ForceCommand internal-sftp` | All SSH forced to SFTP | Common on ZTE NEs |
| `PermitTTY no` | No shell access | Security hardening |
| `ChrootDirectory /path` | User jail | Verify paths correct |
| `AllowUsers user1 user2` | Access whitelist | Should be tight |

### Socket activation detection (modern Ubuntu/Debian)

Modern Ubuntu ships with socket-activated SSH (`ssh.socket`). The listening port is controlled by systemd, **not** by `Port` in `sshd_config`.

```bash
systemctl status ssh.socket --no-pager | grep -E 'Listen|Active'
systemctl cat ssh.socket | grep ListenStream
```

If `ssh.socket` is active, changing `Port` in `sshd_config` alone does **nothing**. See `references/changing-ssh-port.md` for the full socket-aware procedure.

### Telnet, FTP check

```bash
systemctl status telnetd telnet.socket vsftpd proftpd pure-ftpd 2>/dev/null
ss -tlnp | grep -E ':21 |:23 '
ps aux | grep -E 'telnetd|in\.telnetd|vsftpd|proftpd' | grep -v grep
```

### ZTE network element specifics

Telecom NEs (OAM routers, NDF nodes) embed the SFTP subsystem inside proprietary processes:

| Process | Role | Typical ports |
|---------|------|---------------|
| `oamroutersrv` | OAM routing service | 7681, 8777, 8778 |
| `oamndfsrv` | NDF service | 7700, 7800 |
| `commroutersrv` | Communication router | 7722, 7788 |

Key differences from general Linux servers:
- `sshd` may not appear as a Linux service — SSH is handled by proprietary processes reading `sshd_config`
- Standard port 22 may not be used; check process-specific ports (7722 common)
- `ForceCommand internal-sftp` + `PermitTTY no` + per-user `ChrootDirectory` is the norm

See `references/zte-ne-patterns.md` for detailed config examples and audit checklists.

### Composite audit one-liner

```bash
echo "=== Listening ports ===" && sudo ss -tlnp && \
echo "=== SSH config (active) ===" && grep -E '^[^#]' /etc/ssh/sshd_config | grep -v '^$' && \
echo "=== Socket activation ===" && systemctl status ssh.socket --no-pager | grep Listen && \
echo "=== SFTP processes ===" && ps aux | grep sftp-server | grep -v grep && \
echo "=== Telnet ===" && ss -tlnp | grep ':23 ' && \
echo "=== FTP ===" && ss -tlnp | grep ':21 '
```

## Pitfalls

### ⚠️ DNS of ifconfig.me may be slow from China
Use `curl -s --max-time 5 ifconfig.me` with a timeout. Alternative: `curl -s ip.sb`.

### ⚠️ Don't expose config with raw tokens in chat
When sharing connection info, keep the UUID/token visible only to the user. Never paste config into a public context.

### ⚠️ VLESS+REALITY: "invalid password" error = public key corruption

The most common VLESS+REALITY client error is `infra/conf: invalid "password": <PUBKEY>` where the pubkey looks mangled (missing leading chars, spaces in the middle). **This is NOT a real password/UUID issue** — it means the `pbk` (publicKey) value in the share link got corrupted during URL parsing.

**Root causes (in order of likelihood):**
1. **Public key starts with `+`** → decoded as space in URL query string → `+udOd...` → ` udOd...` or dropped entirely
2. **Public key contains `/`** → interpreted as path separator or encoded incorrectly
3. **V2rayNG parses URL-encoded `%2B`/`%2F`/`%3D` incorrectly** — some client versions don't decode properly

**Solution 1 (recommended): Use Xray-generated keys.** Xray's `xray x25519` outputs URL-safe base64 (pure alphanumeric, no `+`, `/`, or `=`). This completely avoids the issue.

**Solution 2 (V2Fly only):** If stuck on V2Fly, regenerate Python-generated keys until you get a clean public key (no `+` or `/` in body, only alphanumeric chars + trailing `=`).
Anyone who sees the WebSocket traffic can read it. Fine for 回国代理 (getting a Chinese IP, not hiding content), but don't use for sensitive data without TLS or REALITY.

### ⚠️ Container restart policy
Always use `--restart unless-stopped` — `always` will restart even after `docker stop`, which is annoying.

### ⚠️ IPv6 UFW rules
UFW by default adds rules for both IPv4 and IPv6 (`v6` suffix). Check with `sudo ufw status`.
