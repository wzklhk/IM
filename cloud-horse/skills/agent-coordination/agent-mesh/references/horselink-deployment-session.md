# HorseLink Deployment Session Notes (2026-05-16)

Session context: Setting up HorseLink IM on `p2pim4agent` branch of `github.com/wzklhk/IM` repo.

## Deployment Decisions

| Decision | Choice | Why |
|----------|--------|-----|
| Transport | WebSocket (RFC 6455) | Full-duplex, mature libs, HTTP-friendly |
| Node | Python with `websockets` | Both agents are Hermes (Python), single dependency |
| Token | `IM_GITHUB_TOKEN` in `.env` | Separate from `GITHUB_TOKEN`; scoped to IM repo only |
| Branch | `p2pim4agent` | Keeps IM code separate from other IM experiments |

## Token Security

Store in `.env` as a named variable distinct from `GITHUB_TOKEN`:

```bash
# ~/.hermes/.env
IM_GITHUB_TOKEN=github_pat_xxxxxxxxxxxxx
```

**Rule**: Never display the raw token in tool output. Use `***` filler when referring to it in conversation. Extract at runtime:

```bash
IM_TOKEN=$(grep '^IM_GITHUB_TOKEN=' ~/.hermes/.env | cut -d= -f2-)
```

## Clearing an Existing Branch

When replacing a branch's content completely:

```bash
# Clone the branch
git clone --depth 1 --branch p2pim4agent <repo_url> /tmp/repo

# Remove everything except .git
cd /tmp/repo
find . -maxdepth 1 -not -name '.git' -not -name '.' -exec rm -rf {} +

# Copy new files
cp -r ~/workspace/horselink/* .

# Force push (no history conflicts since we're replacing)
git add -A
git commit -m "🎉 Replace with HorseLink v0.2"
git push <repo_url> HEAD:p2pim4agent --force
```

## Security Verification Checklist

Before deploying to public VPS, verify:

- [ ] `--cert` and `--key` provided → WSS enabled
- [ ] `--allow-ip` set → IP whitelist active
- [ ] `--max-auth-fail` set → rate limiting active
- [ ] Secret is NOT a default/test value
- [ ] Port is > 1024 (avoids root requirement)

## Git Push Failure Recovery

From Chinese VPS, HTTPS git push frequently times out. Recovery sequence:

1. Check `github.com` vs `api.github.com` reachability separately
2. If both work → retry with longer timeout + retry loop
3. If only API works → use `gh api` to create/update files
4. If SSH works → convert remote to SSH and push that way

## Test Results

| Test | Result |
|------|--------|
| Relay + Client connect | ✅ |
| Message routing A→B via Relay | ✅ |
| Message routing B→A via Relay | ✅ |
| Peer join/leave notification | ✅ |
| Rate limiter blocking | ✅ |
| IP whitelist filtering | ✅ |
