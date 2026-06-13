# Vault Incremental Sync — MD5-Change-Detected Push to GitHub

A pattern for automatically syncing an Obsidian knowledge vault (or any markdown directory) to a GitHub repo **incrementally**, pushing only when content actually changes.

## Architecture

```
┌──────────────────┐     every hour (cron)      ┌──────────────────────┐
│  knowledge-vault  │ ──→ compute MD5 snapshot ──→ compare with last    │
│  ~/workspace/     │     of all .md files        snapshot              │
└──────────────────┘                             └──────────┬───────────┘
                                                            │
                                                   no change? → silent exit
                                                            │
                                                      has change?
                                                            │
                                                            ▼
                                              ┌──────────────────────────┐
                                              │ Copy .md files to        │
                                              │ my-hermes-backup/        │
                                              │ knowledge-vault/         │
                                              │ → commit → push          │
                                              └──────────────────────────┘
                                                        │
                                                        ▼
                                              Notify user: "知识库已同步"
```

## Why Not Full Clone Every Time

The daily backup script does full snapshot (clone + replace all + push). For hourly sync, a full clone each time wastes bandwidth and time. The incremental approach:

- Maintains a persistent clone at `/tmp/hermes-vault-sync/`
- Pulls only new commits (shallow fetch)
- Only copies files when the MD5 snapshot differs
- Silent exit when nothing changed — no user notification, no network traffic

## Script Pattern

The key technique: **compute MD5 checksums over all vault files**, compare to previous run.

```bash
# Compute snapshot
find . -name '*.md' -not -path './.obsidian/*' -exec md5sum {} \; > /tmp/snapshot-$$.txt

# Compare
HASH_FILE="$HOME/.hermes/horse-comm/vault-last-hash.txt"
if [ -f "$HASH_FILE" ] && cmp -s /tmp/snapshot-$$.txt "$HASH_FILE"; then
  rm -f /tmp/snapshot-$$.txt
  exit 0  # silent — nothing to do
fi

# ... copy .md files, commit, push ...

# Save snapshot for next comparison
mv /tmp/snapshot-$$.txt "$HASH_FILE"
```

## Cron Job Configuration

Use `no_agent=True` to save LLM costs:

```yaml
name: vault-sync
schedule: "0 * * * *"           # every hour at :00
no_agent: true
script: sync-vault.sh           # lives in ~/.hermes/scripts/
deliver: origin                 # non-empty stdout → user notification
```

## Token Extraction

Same approach as the full backup — self-contained grep in script:

```bash
if [ -z "$GITHUB_TOKEN" ] || [ "$GITHUB_TOKEN" = "***" ]; then
  TOKEN_VALUE=$(grep '^GITHUB_TOKEN=' "$HOME/.hermes/.env" | tail -1 | cut -d= -f2-)
  if [ -n "$TOKEN_VALUE" ] && [ "$TOKEN_VALUE" != "***" ]; then
    export GITHUB_TOKEN="$TOKEN_VALUE"
  fi
fi
```

## Persistent Clone Management

```bash
SYNC_DIR="/tmp/hermes-vault-sync"
# First run: clone
if [ ! -d "$SYNC_DIR/.git" ]; then
  git clone --depth 1 "$REPO" "$SYNC_DIR"
fi
# Subsequent runs: pull
cd "$SYNC_DIR"
git pull --rebase --depth 1 origin main
```

The clone persists across cron runs. On VPS reboot, it disappears and gets recloned automatically.
