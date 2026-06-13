# Changing SSH Port — Socket Activation Edition

## Context

Modern Ubuntu (20.04+) and Debian (11+) use **systemd socket activation** for SSH.
The listening port is NOT controlled by `Port` in `/etc/ssh/sshd_config` — it's
controlled by `ssh.socket` unit.

## Detection

```bash
systemctl status ssh.socket --no-pager | grep Listen
```

If you see `Listen: 0.0.0.0:22 (Stream)`, socket activation is in play.

## Full Procedure (port 22 → 22022)

### 1. Firewall — allow new port FIRST (safety net)

```bash
sudo ufw allow 22022/tcp comment 'SSH new port'
```

### 2. Update sshd_config

```bash
# Add Port line for clarity (sshd -t validation)
sudo sed -i '/^AddressFamily/a Port 22022' /etc/ssh/sshd_config

# Disable root login (common hardening step)
sudo sed -i 's/^PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config
```

### 3. Override ssh.socket via systemd drop-in

```bash
sudo mkdir -p /etc/systemd/system/ssh.socket.d/
sudo tee /etc/systemd/system/ssh.socket.d/override.conf >/dev/null <<'DEOF'
[Socket]
ListenStream=
ListenStream=0.0.0.0:22022
ListenStream=[::]:22022
DEOF
```

> **Key detail:** `ListenStream=` (empty value) clears the default `ListenStream=22`
> from the unit. Without it, both 22 and 22022 would listen.

### 4. Apply

```bash
sudo systemctl daemon-reload
sudo systemctl restart ssh.socket ssh.service
```

### 5. Verify

```bash
ss -tlnp | grep ':22022'   # should show LISTEN
timeout 3 bash -c 'echo > /dev/tcp/localhost/22022' 2>&1 && echo "OPEN" || echo "CLOSED"
```

### 6. Remove old port (only after confirming new one works)

```bash
# Local smoke-test
ssh -o StrictHostKeyChecking=no -p 22022 localhost 'echo OK'

# Only then remove old port
sudo ufw delete allow 22/tcp
```

## Real-World Example

This procedure was validated on a Tencent Cloud Ubuntu VPS (2026-05-19).
The gotcha: `systemctl restart sshd` alone did nothing because `ssh.socket`
recreated the original port binding. Both socket and service must be restarted
together.

## Rollback

If something breaks:

```bash
# Restore default socket
sudo rm /etc/systemd/system/ssh.socket.d/override.conf
sudo systemctl daemon-reload
sudo systemctl restart ssh.socket ssh.service

# Restore sshd_config
sudo cp /etc/ssh/sshd_config.bak.* /etc/ssh/sshd_config
sudo systemctl restart sshd

# Fix firewall
sudo ufw allow 22/tcp
sudo ufw delete allow 22022/tcp
```
