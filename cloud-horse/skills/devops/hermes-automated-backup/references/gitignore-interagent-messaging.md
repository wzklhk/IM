# .gitignore & Inter-Agent Messaging

## The Problem

The backup script (`backup-hermes.sh`) writes a `.gitignore` that blocks all non-`.md` files:

```
*.*
!*.md
!.gitignore
```

When the same repo is used for inter-agent messaging (`shared/horse-comm/`), JSON message files are silently ignored by `git add -A` and never reach the remote. This means even if local-horse commits JSON messages to `shared/horse-comm/to-cloud/`, the daily backup's `git pull --rebase` won't see them, because they were never tracked.

## Symptoms

- `shared/horse-comm/to-cloud/` directory **does not exist** in the cloned repo
- GitHub API returns 404 for `shared/horse-comm/` path
- API-level check confirms the path was never created/committed to the repo
- No messages from other agents ever appear, despite the setup being correct

## Root Cause

The `.gitignore` pattern `*.*` matches all files with a dot in their name (which is effectively all files). The `!*.md` exception only allows `.md` files. `.json` files (and most other formats) are excluded.

## Fix

### Option A: Add gitignore exception (recommended)

Update the backup script's `.gitignore` block:

```bash
cat > .gitignore << 'GITIGNORE'
*.*
!*.md
!.gitignore
!shared/horse-comm/**
GITIGNORE
```

This allows any file type within `shared/horse-comm/` to be tracked, while keeping the MD-only restriction everywhere else.

### Option B: Use `.md` extension for messages

Rather than changing the `.gitignore`, agents can write messages as `.md` files. The standard message format already supports markdown in the `body` field. Example:

```markdown
# 🐎 Status Update from <agent>

- **id**: msg_20260518_030000_abcd
- **from**: cloud-horse
- **to**: local-horse
- **type**: status
- **timestamp**: 2026-05-18T03:00:00+08:00

Everything running smoothly on VPS. 24h uptime.
```

This is simpler but less structured — `body` parsing requires heuristic extraction rather than direct JSON field access.

### Option C: Combine both

Add the `.gitignore` exception AND encourage `.md` messages. This is the most flexible path — JSON for structured messages, `.md` for human-readable updates.

## Detection Checklist

Use this to determine if the `.gitignore` is blocking messages:

1. `gh api repos/owner/repo/contents/shared/horse-comm/to-cloud` → 404? ⚠️ Path doesn't exist
2. `gh api repos/owner/repo/contents/.gitignore --jq '.content' | base64 -d` → contains `*.*` and no `!shared/horse-comm/**`? ⚠️ Blocked
3. Check `~/.hermes/backup-hermes.sh` for the `.gitignore` write block — verify pattern
4. Check if local-horse has committed to `shared/` in the git history: `gh api repos/owner/repo/commits?path=shared/` → empty? ⚠️ No agent has written to shared/

## Related

- `hermes-automated-backup/SKILL.md` → "Inter-Agent Message Bus" section
- `agent-mesh/SKILL.md` → "Backup Script's .gitignore Blocks Non-.md Messages" pitfall
