# HorseLink v0.2 Security & Chinese VPS Git Workarounds

## Session 2026-05-15: Security Reinforcement

### What Changed (v0.1 → v0.2)

| Feature | v0.1 | v0.2 |
|---------|------|------|
| Transport | WS (plaintext) | WS + WSS (TLS) |
| IP control | None | `--allow-ip` whitelist |
| Auth protection | 1 attempt then disconnect | Rate limiter (`--max-auth-fail`, default 5) |
| Client IP logging | None | Logged in relay handler |

### Production Deployment Checklist

- [ ] Generate TLS certificate: `openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes`
- [ ] Use `--cert cert.pem --key key.pem` to enable WSS
- [ ] Set `--allow-ip` for each known client's public IP
- [ ] Choose a strong `--secret` (16+ chars, not a dictionary word)
- [ ] Use high port (8765+) to avoid port-scan noise on standard ports
- [ ] (Optional) Configure iptables to restrict port 8765 to known IPs as second layer

### Chinese VPS → GitHub Connectivity Test (2026-05-15)

**VPS:** Tencent Cloud Guangzhou
**Results:**

| Target | Protocol | Status | Latency |
|--------|----------|--------|---------|
| `github.com:443` | HTTPS (git/web) | ❌ Timeout | 135s+ |
| `api.github.com:443` | REST API | ✅ Works | ~275ms |
| `github.com:22` | SSH | ✅ Works | Connected instantly |

**Implication:** From some Chinese VPS providers, `github.com` TCP/443 is throttled but SSH port 22 and `api.github.com` work fine.

### SSH Fallback for Git Operations

When HTTPS to `github.com` times out:

1. Generate SSH key: `ssh-keygen -t ed25519 -f ~/.ssh/github_deploy -N ""`
2. Add to GitHub (Settings → SSH Keys)
3. Update remote: `git remote set-url origin git@github.com:owner/repo.git`
4. Push via SSH: `git push origin main`

Add to `~/.ssh/config` for automatic key selection:
```
Host github.com
  HostName github.com
  IdentityFile ~/.ssh/github_deploy
  User git
```

### Horse-Comm Cadence Change

The horse-comm GitHub polling was reduced from every 30 minutes to daily at 3:00 AM (merged with the backup cron job). This works because HorseLink IM now handles real-time communication; the GitHub channel became a daily "morning digest" for durable knowledge sync.

Cron jobs after change:

| Name | Schedule | Purpose |
|------|----------|---------|
| `hermes-auto-backup` | `0 3 * * *` | Backup + inbox check (merged) |

The old `check-horse-comm-to-cloud` (every 30m) was deleted.
