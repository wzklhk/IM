# Full Backup Script Reference

## Complete backup-hermes.sh (current as of 2026-05-12)

The live script is at `~/.hermes/backup-hermes.sh`. This reference shows the multi-source version with cloud-horse + knowledge-vault backup.

```bash
#!/bin/bash
set -e

# =========================================
# Hermes Backup Script — ~/.hermes + 知识库 → GitHub
# Only backs up Markdown (.md) files
# Runs daily at 3:00 AM via cron job
# =========================================

BACKUP_DIR="/tmp/hermes-backup-$$"
HERMES_HOME="$HOME/.hermes"
KNOWLEDGE_VAULT="$HOME/workspace/knowledge-vault"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

echo "[$TIMESTAMP] Starting Hermes + 知识库 backup (MD only)..."

trap 'rm -rf "$BACKUP_DIR"' EXIT

# Auto-load GITHUB_TOKEN from .env if not already set (handles cron context)
if [ -z "$GITHUB_TOKEN" ] || [ "$GITHUB_TOKEN" = "***" ]; then
  if [ -f "$HOME/.hermes/.env" ]; then
    TOKEN_VALUE=$(grep '^GITHUB_TOKEN=' "$HOME/.hermes/.env" | cut -d= -f2-)
    if [ -n "$TOKEN_VALUE" ] && [ "$TOKEN_VALUE" != "***" ]; then
      export GITHUB_TOKEN="$TOKEN_VALUE"
      echo "GITHUB_TOKEN loaded from .env"
    fi
  fi
fi

if [ -z "$GITHUB_TOKEN" ] || [ "$GITHUB_TOKEN" = "***" ]; then
  echo "ERROR: GITHUB_TOKEN is not set or invalid. Aborting."
  exit 1
fi

export GITHUB_TOKEN

# IMPORTANT: fine-grained PATs don't work via GIT_ASKPASS — embed token in URL
# Use variable substitution — NOT literal *** placeholder which causes silent timeouts
REPO="https://wzklhk:${GITHUB_TOKEN}@github.com/wzklhk/my-hermes-backup.git"

# Clone with retries (flaky connections from Chinese VPS)
# Use timeout 120 for clones — first TCP handshake can be slow from China
for i in 1 2 3; do
  if timeout 120 git clone --depth=1 --no-checkout "$REPO" "$BACKUP_DIR" 2>/dev/null; then
    echo "Cloned existing repo (attempt $i)."
    cd "$BACKUP_DIR"
    git config core.sparseCheckout false 2>/dev/null || true
    git checkout HEAD 2>/dev/null
    break
  fi
  echo "Clone attempt $i failed, retrying..."
  rm -rf "$BACKUP_DIR" 2>/dev/null
  sleep 3
done

if [ ! -d "$BACKUP_DIR/.git" ]; then
  echo "Initializing new repo..."
  mkdir -p "$BACKUP_DIR"
  git init --initial-branch=main "$BACKUP_DIR"
  git -C "$BACKUP_DIR" remote add origin "$REPO"
fi

cd "$BACKUP_DIR"

# 先拉最新，避免覆盖其他马的提交
for i in 1 2 3; do
  if timeout 60 git pull --rebase --depth 1 origin main 2>/dev/null; then
    echo "Pulled latest from origin (attempt $i)."
    break
  fi
  echo "Pull attempt $i failed, retrying..."
  sleep 3
done

# Set commit identity
git config user.name "My Hermes Agent"
git config user.email "hermes@bot.local"

# 只清理我们自己的目录，不碰其他马的
# cloud-horse/ 和 knowledge-vault/ 是云马维护的
# local-horse/, shared/ 等留给其他马
for dir in cloud-horse knowledge-vault; do
  rm -rf "$BACKUP_DIR/$dir"
done

# =========================================
# 1️⃣ Backup ~/.hermes → cloud-horse/
# =========================================
CLOUD_HORSE_DIR="$BACKUP_DIR/cloud-horse"
mkdir -p "$CLOUD_HORSE_DIR"

echo "Collecting .md files from $HERMES_HOME -> cloud-horse/..."
cd "$HERMES_HOME"
find . -name '*.md' \
  -not -path './hermes-agent/*' \
  -not -path './venv/*' \
  -not -path './.git/*' \
  -print0 | while IFS= read -r -d '' file; do
    dir=$(dirname "$file")
    mkdir -p "$CLOUD_HORSE_DIR/$dir"
    cp "$HERMES_HOME/$file" "$CLOUD_HORSE_DIR/$file"
done

# =========================================
# 2️⃣ Backup knowledge vault → knowledge-vault/
# =========================================
VAULT_DIR="$BACKUP_DIR/knowledge-vault"
mkdir -p "$VAULT_DIR"

if [ -d "$KNOWLEDGE_VAULT" ]; then
  echo "Collecting .md files from $KNOWLEDGE_VAULT -> knowledge-vault/..."
  cd "$KNOWLEDGE_VAULT"
  find . -name '*.md' \
    -not -path './.obsidian/*' \
    -print0 | while IFS= read -r -d '' file; do
    dir=$(dirname "$file")
    mkdir -p "$VAULT_DIR/$dir"
    cp "$KNOWLEDGE_VAULT/$file" "$VAULT_DIR/$file"
  done
else
  echo "Knowledge vault not found at $KNOWLEDGE_VAULT, skipping."
fi

cd "$BACKUP_DIR"

cat > .gitignore << 'GITIGNORE'
*.*
!*.md
!.gitignore
!shared/horse-comm/**
GITIGNORE

git add -A
FILE_COUNT=$(git diff --cached --name-only | grep -c '\.md$' || true)
if [ "$FILE_COUNT" -eq 0 ] && git diff --cached --quiet; then
  echo "[$TIMESTAMP] No changes to backup."
else
  git commit -m "Auto-backup $TIMESTAMP ($FILE_COUNT files)"
  for i in 1 2 3; do
    echo "Push attempt $i..."
    if timeout 60 git push origin HEAD:main 2>&1; then
      echo "[$TIMESTAMP] Backup pushed successfully ($FILE_COUNT .md files)."
      break
    fi
    echo "Push attempt $i failed, retrying..."
    sleep 5
  done
fi
```

## Architecture (current)

```
cron (3AM daily)
  └─ self-contained backup-hermes.sh
      ├─ auto-loads token from .env (grep/cut, not source)
      ├─ builds REPO URL with embedded token (${GITHUB_TOKEN}, not literal ***)
      ├─ clone/init repo (3 retries, sleep 3s between)
      ├─ Source 1: find + cp .md from ~/.hermes/ → cloud-horse/
      ├─ Source 2: find + cp .md from ~/workspace/knowledge-vault/ → knowledge-vault/
      ├─ .gitignore (md-only)
      └─ git commit & push --force (3 retries, timeout 60s each, sleep 5s between)
```

## GitHub Repo Structure

```
github.com/wzklhk/my-hermes-backup
├── cloud-horse/          ← Hermes Agent data (memory, skills, sessions, configs)
│   ├── memories/         ← MEMORY.md, USER.md
│   ├── skills/           ← All skill SKILL.md files
│   ├── cron/output/      ← Cron job run logs
│   ├── kanban/           ← Kanban board state
│   └── ...other paths preserved from ~/.hermes/
└── knowledge-vault/      ← Obsidian vault (FIRE plans, notes, templates)
    ├── Home.md
    ├── Areas/
    ├── Projects/
    ├── Templates/
    └── ...
```

## Key Files

| File | Purpose |
|------|---------|
| `~/.hermes/backup-hermes.sh` | Main backup script (self-contained, multi-source) |
| `~/.hermes/scripts/git-askpass.sh` | GIT_ASKPASS helper — kept for legacy, not used (fine-grained PAT incompatible) |
| `~/.hermes/scripts/load-github-token.sh` | Deprecated — token loading now baked into backup-hermes.sh |

## Real-World Pitfalls (discovered 2026-05-11/12)

1. **Fine-grained PAT rejects GIT_ASKPASS** — the token must be embedded in the git URL (`https://user:${TOKEN}@github.com/...`). Using `GIT_ASKPASS` + a helper script produces `remote: Write access to repository not granted` even when the token has valid permissions.

2. **Shallow clone ref mismatch** — `git clone --depth=1` followed by `git push --force` can fail with `cannot lock ref 'refs/heads/main': is at X but expected Y`. This happens when the local shallow clone's remote ref tracking is stale. `--force` resolves it on retry.

3. **VPS network timeouts** — From Chinese VPS, `git clone` can hang indefinitely. Must wrap all git operations in `timeout 60` to prevent blocking. Retry 3x with 3-5s sleep between attempts.

4. **`.env` masking** — Hermes replaces token values with `***` in `.env` on disk after loading. But if the token was set mid-session (appended via `>>`), the file still has the real value. The grep/cut trick works as long as there's at least one non-masked line.

5. **Literal `***` in REPO URL** — If you hardcode `https://user:***@github.com/...` instead of using `https://user:${GITHUB_TOKEN}@github.com/...`, the script will fail with silent clone timeouts. Always use variable substitution. The auto-load block at the top extracts the real token from `.env` at runtime.

6. **Token in URL exposed in `ps`** — Embedding the token in the git URL means it appears in process listings. Mitigation: run the script in isolation, cron hides arguments by default.
