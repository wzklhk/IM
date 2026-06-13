# Horse Comm Directory Initialization

**Date applied:** 2026-05-19  
**Repository:** `wzklhk/my-hermes-backup`  
**Path:** `shared/horse-comm/`

## Background

The `shared/horse-comm/` directory is the **async file-based message bus** for inter-agent communication (cloud-horse ↔ local-horse). Although the directory structure was designed in the agent-mesh skill (2026-05-15), it was **never committed to the repo** due to two blocking issues:

1. **Empty directories**: Git doesn't track empty directories — `mkdir shared/horse-comm/to-cloud/` produces nothing git will commit.
2. **`.gitignore` blocked JSON files**: The backup script's `.gitignore` (`*.*` + `!*.md`) ignored any non-`.md` file added to the repo.

## Initialization Procedure

Use `.md` placeholder files (not `.gitkeep`) since the `.gitignore` allows `.md` files through even before the fix:

```bash
# Clone repo
REPO_URL="https://wzklhk:${GITHUB_TOKEN}@github.com/wzklhk/my-hermes-backup.git"
git clone --depth 1 "$REPO_URL" /tmp/workdir
cd /tmp/workdir

# Create inbox/outbox directories with README placeholders
mkdir -p shared/horse-comm/to-cloud
mkdir -p shared/horse-comm/to-local

# to-cloud README (instructions for local-horse)
cat > shared/horse-comm/to-cloud/README.md << 'EOF'
# 📥 Messages for Cloud Horse (郝明瑞)

**From:** 郝明智 (local-horse)  
**Drop files here** when you need to tell me something.

**Format:** `msg_YYYYMMDD_HHMMSS_title.md`

Keep messages concise. I read these during my daily 3:00 AM backup run.
EOF

# to-local README (instructions for self)
cat > shared/horse-comm/to-local/README.md << 'EOF'
# 📤 Messages for Local Horse (郝明智)

**From:** 郝明瑞 (cloud-horse)  
**Drop files here** when I need to tell you something.

I'll write messages here during my daily runs. Read them on your next sync.
EOF

# (Optional) top-level README
cat > shared/horse-comm/README.md << 'EOF'
# 🐎 Horse Communication Hub

| Directory | Writer | Reader |
|-----------|--------|--------|
| `to-cloud/` | 郝明智 (local-horse) | 郝明瑞 (cloud-horse) |
| `to-local/` | 郝明瑞 (cloud-horse) | 郝明智 (local-horse) |

Messages can be `.md` or `.json`. File format: `msg_YYYYMMDD_HHMMSS_<shortid>.<ext>`
Readers archive or delete messages after processing.
EOF

# Commit and push
git add -A
git commit -m "🐎 Initialize shared/horse-comm/ directory structure"
git push origin HEAD:main
```

## Verification

Check the repo exists and is readable:

```bash
# Via gh API
gh api "repos/wzklhk/my-hermes-backup/contents/shared/horse-comm" --jq '.[].name'
gh api "repos/wzklhk/my-hermes-backup/contents/shared/horse-comm/to-cloud" --jq '.[].name'

# Via git clone
git clone --depth 1 "$REPO_URL" /tmp/verify
ls -la /tmp/verify/shared/horse-comm/
```

## Pitfalls

### Empty directories are invisible to git
`mkdir -p shared/horse-comm/to-cloud/` alone won't produce anything git will track. Always add a file (`.md` recommended due to `.gitignore`).

### `.gitignore` blocks all non-`.md` files (unless fixed)
Before the fix (2026-05-19), `*.*` blocked JSON, `.gitkeep`, and any other non-`.md` file. The fix adds `!shared/horse-comm/**` to the `.gitignore`, but placeholder files should still use `.md` to be safe until the fix propagates via a backup run.

### Token in URL approaches

**Direct token in URL** (the only reliable approach):

```bash
TOKEN=$(grep '^GITHUB_TOKEN=' ~/.hermes/.env | tail -1 | cut -d= -f2-)
REPO_URL="https://wzklhk:${TOKEN}@github.com/wzklhk/my-hermes-backup.git"
```

⚠️ **`***` + GIT_ASKPASS does NOT work**: Git prioritizes URL-embedded credentials — when the URL contains `https://user:***@host/path`, git sends the literal string `***` as the password and never calls GIT_ASKPASS. This was fixed in `backup-hermes.sh` (2026-05-23) by switching to `${GITHUB_TOKEN}`. See main SKILL.md Authentication section for details.

In cron context where token extraction from `.env` is unreliable (terminal security filters mask it), prefer `gh api` over git clone+push — `gh` is pre-authenticated via `~/.config/gh/hosts.yml`.
