---
name: linux-server-security-audit
description: >
  Audit Linux servers for security posture — exposed ports, running services,
  SSH/SFTP/FTP/Telnet configuration, hidden PIDs, and socket-activation-aware
  SSH port changes. Covers both general-purpose Linux and telecom network
  elements (ZTE OAM/NDF).
---

# Linux Server Security Audit

## Quick one-liners

### Port inventory (what's listening, with PIDs)
```bash
# Preferred: modern, fast, human-readable
ss -tlnp | sort -t: -k2 -n

# Fallback (requires net-tools)
netstat -tlnp
```

### Check for dangerous protocols specifically
```bash
ss -tlnp | awk 'NR==1 || /:(21|22|23|69|115|989|990) /'
```
Reference: 21=FTP, 22=SSH/SFTP, 23=Telnet, 69=TFTP, 115=SFTP(standalone), 989/990=FTP-TLS

### When PID shows `-` (no root)
The process belongs to another user and `netstat`/`ss` can't read it. Fix: `sudo ss -tlnp`.

Fallback without sudo — scan `/proc` by port (hex):
```bash
for port in 29029 10310; do
    hex=$(printf '%04X' $port)
    echo "=== Port $port (0x$hex) ==="
    grep ":$hex " /proc/*/net/tcp 2>/dev/null | while read line; do
        pid=$(echo $line | cut -d/ -f3)
        echo -n "PID: $pid  CMD: "
        cat /proc/$pid/cmdline 2>/dev/null | tr '\0' ' '
    done
done
```

Also try `fuser -v <PORT>/tcp` — sometimes works without root.

## SSH/SFTP configuration audit

### Is SFTP enabled?
```bash
grep -i '^Subsystem.*sftp' /etc/ssh/sshd_config
```
- **Present and uncommented** → SFTP enabled
- **Commented out (`#`)** → disabled
- **Line missing entirely** → usually defaults to enabled (sshd ships with built-in sftp-server)

### Full active (non-comment) config
```bash
grep -E '^[^#]' /etc/ssh/sshd_config | grep -v '^$'
```

### What to look for (security checklist)
| Config line | Meaning | Recommendation |
|-------------|---------|----------------|
| `Subsystem sftp …` | SFTP active | Disable if not needed |
| `Port 22` | SSH port | Confirm standard vs obfuscated |
| `PermitRootLogin yes` | Root can SSH | Set to `no` |
| `PasswordAuthentication yes` | Password login allowed | Set to `no`, key-only |
| `ForceCommand internal-sftp` | All SSH forced to SFTP only | Common on ZTE NEs |
| `PermitTTY no` | No shell access | Security hardening |
| `ChrootDirectory /path` | User jailed to directory | Verify paths are correct |
| `AllowUsers user1 user2` | Whitelist | Should be tight |
| `Match User xxx` | Per-user overrides | Review each block |

### Check for active SFTP sessions
```bash
ps aux | grep sftp-server | grep -v grep
pstree -p | grep sftp
```

### Verify SSH/SFTP is actually running
```bash
systemctl status sshd 2>/dev/null || service sshd status 2>/dev/null
ps aux | grep sshd | grep -v grep
```

### Detect socket activation (Ubuntu/systemd)

Modern Ubuntu ships with **socket-activated** SSH (`ssh.socket`). The listening
port is controlled by systemd, **not** by `Port` in `sshd_config`. Always check:

```bash
# Is ssh.socket active?
systemctl status ssh.socket --no-pager | grep -E 'Listen|Active'

# Show the raw socket unit
systemctl cat ssh.socket | grep ListenStream
```

If `ssh.socket` is active (`Active: active (running)`), the port listed there
takes priority over `sshd_config`. See `references/changing-ssh-port.md` for
the full socket-activation-aware procedure.

### Changing SSH port on socket-activated systems

See `references/changing-ssh-port.md` for the complete step-by-step
procedure. Critical rules:

1. **UFW allow new port FIRST** (safety net — never close both old and new)
2. Create systemd drop-in at `/etc/systemd/system/ssh.socket.d/override.conf`
   with `ListenStream=` to clear defaults before setting the new port
3. Restart **both** `ssh.socket` and `ssh.service` — `sshd` alone is not enough
4. Test locally (`ssh localhost -p NEWPORT`) before removing old firewall rule

## Telnet-specific check
```bash
# Service check
systemctl status telnetd telnet.socket 2>/dev/null

# Process check
ps aux | grep -E 'telnetd|in\.telnetd' | grep -v grep

# Port check
ss -tlnp | grep ':23 '
```

## FTP-specific check
```bash
# Service check (common daemons)
systemctl status vsftpd proftpd pure-ftpd 2>/dev/null

# Port check
ss -tlnp | grep ':21 '
```

## ZTE network element specifics
Telecom NEs (OAM routers, NDF nodes) often run custom SSH stacks where:
- `sshd` may not appear as a Linux service — the SFTP subsystem is embedded
  inside proprietary processes (`oamroutersrv`, `commroutersrv`)
- Standard port 22 may not be used; look for port `7722` or application ports
- `sshd_config` uses `internal-sftp` + `ForceCommand` + `ChrootDirectory` +
  per-user `Match` blocks for strict isolation
- Process names to recognize: `oamroutersrv`, `oamndfsrv`, `commroutersrv`

See `references/zte-ne-patterns.md` for detailed examples and known configurations.

## Composite audit command
```bash
echo "=== Listening ports ===" && sudo ss -tlnp && \
echo "=== SSH config (active) ===" && grep -E '^[^#]' /etc/ssh/sshd_config | grep -v '^$' && \
echo "=== Socket activation check ===" && systemctl status ssh.socket --no-pager | grep Listen && \
echo "=== SFTP process check ===" && ps aux | grep sftp-server | grep -v grep && \
echo "=== Telnet check ===" && ss -tlnp | grep ':23 ' && \
echo "=== FTP check ===" && ss -tlnp | grep ':21 '
```
(expect empty output for Telnet, FTP, and sftp-server on a clean system)

## Pitfalls

- `Change Port in sshd_config alone does NOTHING on socket-activated systems` —
  you MUST create a systemd drop-in for `ssh.socket`
- Forgetting `ListenStream=` (empty) to clear the default port before setting
  the new one causes both old and new ports to remain open
- `systemctl restart sshd` (without socket) leaves the old socket still
  listening — always restart both: `ssh.socket` AND `ssh.service`
- UFW must allow the new port before restarting the socket, or you'll lose SSH
  access if the service restarts
- `netstat -tlnp` with PID `-`: always try `sudo` first; the `/proc` fallback
  is fragile and OS-dependent
- ZTE NEs: `sshd_config` may be present but `sshd` service may not be the one
  actually handling SFTP — the config is read by a proprietary process
- Port 22 missing from `ss` doesn't mean SFTP is disabled on ZTE NEs — check
  non-standard ports like 7722 or look at the application-layer process
