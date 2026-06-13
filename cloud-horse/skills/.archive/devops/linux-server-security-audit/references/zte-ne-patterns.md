# ZTE Network Element SFTP/SSH Patterns

## Device identification
When you see these process names in `ps aux` or `ss -tlnp`, you're on a ZTE telecom network element,
not a general-purpose Linux server:

| Process | Role | Typical ports |
|---------|------|---------------|
| `oamroutersrv` | OAM routing service | 7681, 8777, 8778, 8779, 7778 |
| `oamndfsrv` | NDF (Node Data Function) service | 7700, 7800 |
| `commroutersrv` | Communication router service | 7722, 7788 |
| `web_info.log` | Web info/logging service | 2332, 2333 |

## Standard sshd_config on ZTE NEs

Typical configuration observed on production ZTE OAM/NDF devices:

```
Subsystem sftp internal-sftp
ssh_softwareversion ""
StartByNonroot yes
PermitTTY no
ForceCommand internal-sftp

Match User ems
    ChrootDirectory "/home/ftp/inner"
Match User ndf
    ChrootDirectory "/home/ftp/ndf"
Match User cn-5g-inner-oper
    ChrootDirectory "/home/ftp/inner"
Match User inner-ndf
    ChrootDirectory "/home/ftp/ndf"
Match User rule_import
    ChrootDirectory "/home/ftp/inner"
Match User inner-mem
    ChrootDirectory "/sftp/mem"
Match User mem
    ChrootDirectory "/sftp/mem"
```

### Key observations
- **`ForceCommand internal-sftp`** is global → all SSH connections are SFTP-only, no shell access
- **`PermitTTY no`** → terminal emulation blocked
- **`ssh_softwareversion ""`** → version string hidden (security by obscurity)
- **Per-user ChrootDirectory** via `Match User` blocks → each SFTP account is jailed to its own directory
- **No `Port` directive** in this config → likely running on a non-standard port (check `commroutersrv` on 7722)
- **`StartByNonroot yes`** → sshd started by a non-root process (proprietary launcher)

### Audit checklist for ZTE NEs
1. `grep -i '^Subsystem.*sftp' /etc/ssh/sshd_config` — confirm SFTP is `internal-sftp`
2. `grep -i 'ForceCommand' /etc/ssh/sshd_config` — should show `internal-sftp`
3. `grep -i 'PermitTTY' /etc/ssh/sshd_config` — should be `no`
4. List all `Match User` blocks: `grep -A2 '^Match User' /etc/ssh/sshd_config`
5. Verify each ChrootDirectory path exists and has correct permissions
6. Confirm no unexpected users in the Match list (should match operational needs)
7. Check that port 22 (or whichever port SSH runs on) is ACL-restricted to management IPs

### Why port 22 may be missing from netstat
ZTE NEs often embed the SFTP subsystem inside proprietary processes (`commroutersrv` typically
handles it on port 7722). The system `sshd` service may not be running at all. The `sshd_config`
file is still used as configuration input by the proprietary SSH handler.
