---
name: agent-mesh
description: Async agent-to-agent communication via shared GitHub repo — JSON messaging, cron-based polling, processed-ID tracking, task delegation between Hermes agents.
---

# Agent Mesh: Inter-Agent Communication

When multiple Hermes Agent instances need to communicate, delegate tasks, or sync knowledge — and they share only a user/gateway (not direct network access) — there are two complementary modes:

| Mode | Transport | Latency | Best For |
|------|-----------|---------|----------|
| **Async (GitHub Repo)** | git push/pull via cron | 30min polling | Knowledge sync, offline archive, "email" |
| **Real-Time (HorseLink IM)** | WebSocket (RFC 6455) | <100ms | Chat, task delegation, heartbeat, live coordination |

Both modes coexist: use HorseLink IM for live communication, fall back to GitHub for durable archiving and bootstrapping new agents. The same JSON message format and type system spans both — messages are interchangeable.

---

# Mode 1: Async — Shared GitHub Repo

Use a **shared GitHub repo as an async message bus** when real-time connectivity isn't possible or for long-term durable storage.

## Architecture

```
┌──────────────────────┐     ┌──────────────────────┐
│  云马 (VPS/Cloud)    │     │ 郝明智 (Local)      │
│  ~/.hermes/          │     │ ~/.hermes/           │
│  box=cloud-horse     │     │ box=local-horse      │
└────────┬─────────────┘     └──────────┬───────────┘
         │                              │
         │   git push/pull              │   git push/pull
         ▼                              ▼
    ┌────────────────────────────────────────┐
    │  GitHub: wzklhk/my-hermes-backup       │
    │  shared/horse-comm/                    │
    │  ├── to-cloud/    (→ cloud horse)      │
    │  ├── to-local/    (→ local horse)      │
    └────────────────────────────────────────┘
```

## Directory Convention

The repo has a `shared/horse-comm/` directory with per-agent inboxes:

```
shared/horse-comm/
├── to-cloud/        ← Messages FROM other agents TO the cloud/VPS agent
├── to-local/        ← Messages FROM other agents TO the local machine agent
└── ...              ← One inbox per agent box name
```

**Naming rule**: `to-<box-name>/` where `box-name` matches the agent's identity.

## Message Format (JSON)

```json
{
  "id": "msg_<yyyymmdd>_<hhmmss>_<random>.json",
  "from": "cloud-horse",
  "to": "local-horse",
  "type": "greeting|status|task|knowledge-sync|ack",
  "subject": "🐎 Short human-readable subject",
  "body": "Free-form message body — markdown supported",
  "timestamp": "2026-05-15T20:55:00+08:00",
  "status": "sent"
}
```

### Message Types

| Type | Purpose | Expected Response |
|------|---------|-------------------|
| `greeting` | Introduction/hello | Reply greeting |
| `status` | Heartbeat / health check | None (log only) |
| `task` | Task delegation | Process → reply `ack` with results |
| `knowledge-sync` | Share knowledge/notes | Acknowledge + update local KB |
| `ack` | Confirmation of receipt | None (terminates exchange) |

## Polling Setup (Cron Job)

Each agent that wants to receive messages sets up a cron job to poll its inbox.

### Polling Cadence

**Default:** every 30 minutes for responsive task delegation.

**Daily cadence** (when HorseLink IM is available for real-time chat): merge polling into the daily backup cron job at 3AM. The backup prompt also handles inbox checking — this turns inter-agent communication into a daily "morning digest" alongside the knowledge backup.

```yaml
# Combined backup + inbox check (daily at 3AM)
name: hermes-auto-backup
schedule: "0 3 * * *"
prompt: "First check shared/horse-comm/to-<my-box>/ for new messages, then run the backup script"
```

### Cron Job Prompt

```yaml
name: check-<to-box-name>
schedule: "every 30m"
deliver: origin  # forward results to shared gateway
```

Prompt template:

```
I am <agent-name> (<box-name>) running on <host>. My task is to check
GitHub repo <repo-url> in <shared/horse-comm/to-<my-box>/ for NEW messages
from other agents.

Protocol:
- to-<my-box>/      = other agents write, I read
- to-<other-box>/    = I write for other agents to read
- Message format: JSON {id, from, to, type, subject, body, timestamp, status}
- Types: greeting/status/task/knowledge-sync/ack

Execution steps:
1. Read shared/horse-comm/to-<my-box>/ for .json files (see auth section below)
2. Check message.id against processed.txt (~/.hermes/horse-comm/processed.txt)
3. For each NEW message:
   a. Record id in processed.txt
   b. Process based on type (greeting=reply, status=log, task=handle+reply, etc.)
   c. If reply needed, write to to-<other-box>/ and push back to GitHub
4. Report interesting findings to the user via delivery channel

GITHUB_TOKEN extraction: grep '^GITHUB_TOKEN=' ~/.hermes/.env | cut -d= -f2-
```

### Processed-ID Tracking

Maintain a simple text file at `~/.hermes/horse-comm/processed.txt`:

```text
msg_greeting_001
msg_20260515_185217_c8b505
msg_reply_greeting_001
msg_20260515_210000_abc123
```

One ID per line. The cron job reads this to skip already-processed messages.

## Authentication & Reading Strategy

### ⚠️ PITFALL: Backup Script's `.gitignore` Blocks Non-`.md` Messages

If the same GitHub repo is used for **both backup and inter-agent messaging** (as the `my-hermes-backup` repo does), the backup script's `.gitignore` will block JSON message files in `shared/horse-comm/` from being tracked.

**Root cause**: The backup script (`~/.hermes/backup-hermes.sh`) generates a `.gitignore` that only allows `.md` files:

```bash
cat > .gitignore << 'GITIGNORE'
*.*
!*.md
!.gitignore
GITIGNORE
```

This means `git add -A` silently skips JSON message files — they never get pushed to the remote, even if local-horse committed them previously.

**Fix (applied 2026-05-19)**: The backup script's `.gitignore` now includes `!shared/horse-comm/**`:

```bash
# In ~/.hermes/backup-hermes.sh:
cat > .gitignore << 'GITIGNORE'
# Only .md files are backed up (except shared/horse-comm/ for inter-agent messaging)
*.*
!*.md
!.gitignore
!shared/horse-comm/**
GITIGNORE
```

This allows both `.md` and `.json` message files in the horse-comm directory to be tracked. Either format now works reliably.

**Historical note**: Before the fix, `.json` messages were silently ignored by `git add -A`. The alternative workaround (using `.md` extension for messages) is still valid but no longer necessary.

**What about backup cleanup destroying `shared/`?** — ✅ **Not an issue.** The current backup script uses **targeted directory removal**, removing only `cloud-horse` and `knowledge-vault`:

```bash
for dir in cloud-horse knowledge-vault; do
  rm -rf "$BACKUP_DIR/$dir"
done
```

The `shared/` directory is never touched. The broad `find ... -exec rm -rf {} +` cleanup pattern was removed from the script in an earlier update. No fix needed here.

 ### Sequencing Requirements for Cron Jobs

When a cron job handles both backup and communication (combined daily cron at 3AM), the current backup script uses **targeted directory removal** (only `cloud-horse`/`knowledge-vault`) and preserves `shared/` intact. So the backup → check-messages ordering is safe for the daily cadence.

The `.gitignore` has been fixed (2026-05-19) to allow `shared/horse-comm/**` through. Message files of any format (`.md` or `.json`) in the horse-comm directory are now tracked by git. The order doesn't matter for the daily cadence — the backup script preserves `shared/` and `git add -A` includes any tracked messages in the commit.

### Option A: git clone (general purpose)

Works everywhere with stable internet. See `hermes-automated-backup` skill for full details on PAT handling and retry logic:

```bash
TOKEN=$(grep '^GITHUB_TOKEN=' ~/.hermes/.env | tail -1 | cut -d= -f2-)
git clone --depth 1 "https://user:${TOKEN}@github.com/owner/repo.git" /tmp/repo
# ... process files in shared/horse-comm/ ...
```

### Option B: gh api (reliable from China VPS and flaky networks)

When `git clone` times out from a Chinese VPS or other restricted network, use the **GitHub REST API** via `gh` CLI instead. `gh` is often pre-authenticated (`gh auth status` to verify) and API requests are more reliable than SSH/HTTPS git connections:

**Read files (list directory contents + fetch JSON):**

```bash
# List all files in the inbox directory
gh api "repos/owner/repo-name/contents/shared/horse-comm/to-cloud" --jq '.[].name'

# Read a specific JSON file (content is base64-encoded)
gh api "repos/owner/repo-name/contents/shared/horse-comm/to-cloud/msg_xxx.json" --jq '.content' | base64 -d
```

**Write a new message file:**

```bash
# Encode the JSON content as base64
B64=$(echo '{"id":"msg_...","from":"...","to":"...","type":"status",...}' | base64 -w0)

# Create or update the file via the GitHub Contents API
gh api "repos/owner/repo-name/contents/shared/horse-comm/to-<target>/<filename>.json" \
  -X PUT \
  -f message="📬 <subject>" \
  -f content="$B64"
```

**Why this works better from China:**
- API requests (HTTPS) have fewer dropped connections than git protocol
- No need to clone a repo (saves bandwidth and avoids full-repo timeout)
- `gh` CLI's built-in auth avoids embedded token issues
- Simple curl-level requests recover faster from transient failures

### Choosing between options

| Condition | Recommended |
|-----------|-------------|
| General internet, git clone works | Option A (clone) |
| Chinese VPS, flaky connections | Option B (gh api) |
| Git clone keeps timing out | Option B (gh api) |
| Need to push changes back | Both work; Option B avoids clone/push |
| Prefer simplicity | Option A if network stable |

## Writing a Message

Two formats — both work since the `.gitignore` fix (2026-05-19) allows `shared/horse-comm/**` through:

### Approach A: `.md` messages (human-readable, zero-structure)

```bash
cd /tmp/repo-clone

# Write the message as a .md file
cat > "shared/horse-comm/to-<target-box>/msg_$(date +%Y%m%d_%H%M%S)_$(openssl rand -hex 4).md" << 'MDEOF'
# 🐎 Message from <from-agent>

- **id**: msg_20260518_030000_abcd
- **from**: cloud-horse
- **to**: local-horse
- **type**: status
- **timestamp**: 2026-05-18T03:00:00+08:00

Free-form message body — full markdown supported.
MDEOF

# Commit and push
git add shared/horse-comm/to-<target-box>/
git commit -m "📬 <subject> → <target-box>"
git push origin HEAD:main
```

### Approach B: `.json` messages (structured, programmatic)

```bash
cd /tmp/repo-clone

# Write the JSON file
cat > "shared/horse-comm/to-<target-box>/msg_$(date +%Y%m%d_%H%M%S)_$(openssl rand -hex 4).json" << 'JSONEOF'
{
  "id": "msg_...",
  "from": "cloud-horse",
  "to": "local-horse",
  "type": "status",
  "subject": "...",
  "body": "...",
  "timestamp": "2026-05-18T03:00:00+08:00",
  "status": "sent"
}
JSONEOF

# Commit and push
git add shared/horse-comm/to-<target-box>/
git commit -m "📬 <subject> → <target-box>"
git push origin HEAD:main
```

### Git Operations

Authentication uses the same embedded-token approach as the backup script:

```bash
GITHUB_TOKEN=$(grep '^GITHUB_TOKEN=' ~/.hermes/.env | cut -d= -f2-)
REPO="https://user:${GITHUB_TOKEN}@github.com/owner/repo.git"
```

⚠️ **Pitfall**: Do NOT hardcode `***` in the URL — extract from `.env` at runtime. Hardcoded `***` causes `fatal: Authentication failed` because git sends the literal `***` as the password and never falls back to `GIT_ASKPASS`. Use `${GITHUB_TOKEN}` in the URL instead. See `hermes-automated-backup` → `references/url-star-star-star-fix.md` for the full debugging story.

## Gateway Consideration

When all agents share the same user-facing gateway (e.g. same WeChat account), agent-to-agent communication MUST go through a separate channel, NOT through the gateway. The user's messages to one agent are always user→agent, never agent→agent. Keep the two channels cleanly separated:

- **GitHub repo / HorseLink IM** → inter-agent messages
- **Gateway (WeChat etc.)** → user→agent messages only

**Two-channel rule**: If the user says something in the gateway, it's their directive. If another agent says something in the repo/IM channel, it's inter-agent coordination. Never relay gateway messages to another agent, and never inject inter-agent traffic into the gateway.

## Reference Files

- `references/session-example-cloud-local.md` — Concrete example from Session 1: 云马 (VPS) ↔ 郝明智 (Local) setup, with actual message JSON and cron job config.
- `references/horselink-deployment-session.md` — HorseLink IM deployment details from Session 2 (2026-05-15): repo info, verified behaviors, token security, and Chinese VPS git workarounds.
- `references/horselink-v0.2-security-and-china-vps.md` — HorseLink v0.2 security features (WSS, IP whitelist, rate limiting), Chinese VPS GitHub connectivity diagnostics, and SSH fallback for git operations.

## Use Cases

- **Task delegation**: "Cloud horse, run this CPU-intensive analysis → reply with results"
- **Knowledge sync**: "I found X in the user's conversation — save to Area/CrossAgent/Insights/"
- **Status reporting**: Periodic heartbeat to confirm the agent is still running
- **Coordinated backup**: One agent does the backup, the other reads the status from the repo

### Task Forwarding Protocol

When the user gives a task to one agent and wants it simultaneously forwarded to another agent (e.g., "传递任务给郝明智让他同步"):

**Protocol steps (using GitHub async mode as fallback when HorseLink IM is not deployed):**

1. **Acknowledge the task** to the user — confirm you received it
2. **Forward to the other agent** by writing to their inbox:
   ```bash
   MSG_ID="msg_$(date +%Y%m%d_%H%M%S)_cloud-horse-task-fwd"
   cat > /tmp/${MSG_ID}.md << 'MDF'
   # 🐎 Task Forward from 云马
   - **from**: cloud-horse
   - **to**: local-horse  
   - **type**: task
   - **subject**: User task: [brief description]

   ## Task Summary
   [What the user asked — enough context for the other agent]

   ## My Plan
   [What cloud horse is doing about it]
   MDF
   B64=$(base64 -w0 /tmp/${MSG_ID}.md)
   gh api "repos/wzklhk/my-hermes-backup/contents/shared/horse-comm/to-local/${MSG_ID}.md" \
     -X PUT -f message="📬 Task fwd -> local-horse" -f content="$B64"
   ```
3. **Record the forward** in `~/.hermes/horse-comm/processed.txt`
4. **Proceed with the task** — the forwarded message lets the other agent stay in sync

**When to forward:**
- User explicitly says "同时传给郝明智" or "让明智同步"
- Task has implications the other agent should know about (new config, workflow change, system update)
- User refers to the other agent in the task description

**When NOT to forward:**
- Quick yes/no questions (waste of bandwidth)
- Personal conversation between user and this agent (Two-channel rule)
- The info would be stale before the other agent reads it

**Pitfall:** Forwarding adds latency — the GitHub write + push takes a few seconds from a China VPS. For sub-second coordination, deploy HorseLink IM.

**Pitfall:** The `gh api` approach (Option B in Authentication) is preferred from China VPS since git clone/push often times out. Ensure `gh auth status` shows logged-in first.

---

# Mode 2: Real-Time — HorseLink WebSocket IM

For sub-second agent-to-agent communication, deploy **HorseLink** — a lightweight P2P IM system over WebSocket. Source: https://github.com/wzklhk/IM (branch: `p2pim4agent`).

## Architecture

Relay-based P2P — one agent with a public IP runs as Relay (WebSocket server), others connect as Clients:

```
                    ┌─────────────────┐
                    │   Relay Peer     │  ← VPS (public IP)
                    │  cloud-horse     │     routes messages between peers
                    └──────┬──────────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
       ┌──────▼────┐ ┌────▼───┐ ┌─────▼─────┐
       │local-horse │ │ 龙虾   │ │ 其他 Agent │
       └───────────┘ └────────┘ └───────────┘
```

### Peer Roles

| Role | Description | Example |
|------|-------------|---------|
| **Relay** | Has public IP, accepts inbound connections, routes messages | cloud-horse (VPS) |
| **Client** | Connects to Relay, sends/receives messages | local-horse, other agents |
| **Hybrid** (future) | Acts as both Relay + connects to other Relays (Mesh) | Multi-VPS setup |

### Why WebSocket

- **Full-duplex** — both sides push messages, no polling
- **HTTP upgrade** — works through most firewalls (port 80/443 friendly)
- **Mature libraries** — `websockets` for Python, `ws` for Node.js, built-in for browsers
- **Lightweight** — frame header is 2-14 bytes
- **No broker needed** — unlike MQTT, no separate infrastructure

## Transport Protocol

| Layer | Protocol |
|-------|----------|
| Transport | WebSocket (RFC 6455) |
| Heartbeat | Ping/pong every 30s, timeout at 60s |
| Reconnect | Exponential backoff: 5s → 10s → 20s → max 60s |
| Auth | Shared secret, sent as first message, 30s timeout |

## Message Protocol

Reuses the same JSON format as the GitHub async mode, with additional fields:

```json
{
  "type": "message",
  "version": "1.0",
  "msg_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "msg_type": "chat",
  "from": "cloud-horse",
  "to": "local-horse",
  "payload": {
    "subject": "Hello!",
    "body": "明智，收到请回答 🐎"
  },
  "ack_required": false,
  "timestamp": "2026-05-15T21:00:00+00:00"
}
```

### Message Types

Same types as async mode, plus:

| Type | Purpose | Notes |
|------|---------|-------|
| `auth` | Identity + secret verification | Sent on connect |
| `ping/pong` | Heartbeat | Automatic |
| `peer_list` | List of connected peers | Pushed on join |
| `chat` | Human-readable chat | Same as greeting |
| `status` | Status + join/leave events | Relay broadcasts peer lifecycle |
| `task` | Task delegation | Same as async |
| `knowledge_sync` | Knowledge sharing | Same as async |
| `ack` | Confirmation | Same as async |

### Routing Rules

| `to` field | Behavior |
|------------|----------|
| Specific peer name (e.g. `local-horse`) | Direct delivery via Relay |
| `*` or `all` | Broadcast to all other connected peers |
| Relay's own name | Consumed locally |

## Deployment

### Relay Node (public IP agent)

```bash
pip install websockets

# LAN/dev: plain WebSocket (no encryption)
python horselink.py --mode relay --name cloud-horse --secret YOUR_KEY --port 8765

# Production: WSS + IP whitelist (strongly recommended for public deployment)
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem \
  -days 365 -nodes -subj '/CN=YOUR_VPS_IP'
python horselink.py --mode relay --name cloud-horse --secret YOUR_KEY \
  --cert cert.pem --key key.pem --port 8765 \
  --allow-ip KNOWN_CLIENT_IP --allow-ip ANOTHER_IP \
  --max-auth-fail 5
```

| Security Flag | Purpose |
|:---|---|
| `--cert/--key` | Enables WSS (TLS encryption) |
| `--allow-ip` | IP whitelist — only listed IPs can connect |
| `--max-auth-fail` | Rate limit — block IP after N failed auth attempts |

⚠️ **Pitfall**: `--cert` and `--key` must be provided together. Without them, the relay uses plain WS (no encryption) — only safe for LAN/dev.

### Client Node (NAT'd agent)

```bash
pip install websockets
python horselink.py --mode client --name local-horse --secret YOUR_KEY \
  --connect ws://RELAY_IP:8765
```

### Python API Integration

For agent-to-agent automation (no terminal interaction):

```python
import asyncio
from horselink import Peer

async def main():
    peer = Peer(name="cloud-horse", secret="s3cret", mode="relay",
                host="0.0.0.0", port=8765, log_level=logging.INFO)

    def on_message(msg):
        if msg.get("msg_type") == "task":
            # process task...
            asyncio.create_task(peer.send(
                msg.get("from"), "ack",
                {"ack_for": msg["msg_id"], "result": "done"}
            ))

    peer.on_message(on_message)
    await peer.start()

asyncio.run(main())
```

## GitHub Token for HorseLink Repo

The HorseLink source lives at `github.com/wzklhk/IM` (branch `p2pim4agent`). Access requires a fine-grained PAT scoped to that repo. Store in `.env` as `IM_GITHUB_TOKEN`:

```bash
# ~/.hermes/.env
IM_GITHUB_TOKEN=github_pat_xxxxxxxxxxxxxxxxxxxx
```

Extract at runtime (never show plaintext in output — use filler `***`):

```bash
IM_TOKEN=$(grep '^IM_GITHUB_TOKEN=' ~/.hermes/.env | cut -d= -f2-)
git clone --depth 1 --branch p2pim4agent \
  "https://wzklhk:${IM_TOKEN}@github.com/wzklhk/IM.git" /tmp/horselink
```

### Git Clone Flakiness from Chinese VPS

Use `GIT_HTTP_LOW_SPEED_LIMIT` and `GIT_HTTP_LOW_SPEED_TIME` env vars to prevent `git clone` from hanging indefinitely:

```bash
GIT_HTTP_LOW_SPEED_LIMIT=1000 GIT_HTTP_LOW_SPEED_TIME=20 \
  git clone --depth 1 --branch p2pim4agent \
  "https://user:${TOKEN}@github.com/wzklhk/IM.git" /tmp/horselink
```

## Mode Selection: When to Use Which

```
                    ┌──────────┐
                    │ Need to   │
                    │ talk to   │
                    │ another   │
                    │ agent?    │
                    └─────┬────┘
                          │
              ┌───────────┴───────────┐
              │                       │
         Sub-second?            Minutes OK?
              │                       │
              ▼                       ▼
       ┌────────────┐         ┌──────────────┐
       │ HorseLink  │         │ GitHub Repo  │
       │ WebSocket  │         │ (async mail) │
       │ (real-time)│         │              │
       └────────────┘         └──────────────┘
              │                       │
              ▼                       ▼
       Chat / task /             Knowledge sync /
       heartbeat /               durable archive /
       live status               bootstrapping /
                                 offline messages
