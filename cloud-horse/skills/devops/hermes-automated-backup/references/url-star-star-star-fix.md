# `***` URL Bug: Discovery and Fix (2026-05-23)

## Symptoms

The backup script at `~/.hermes/backup-hermes.sh` produced this output during cron run:

```
Cloned existing repo (attempt 1).
Already up to date.
...
Push attempt 1...
remote: Invalid username or token. Password authentication is not supported for Git operations.
fatal: Authentication failed for 'https://github.com/wzklhk/my-hermes-backup.git/'
Push attempt 1 failed, retrying...
```

Despite:
- `GITHUB_TOKEN loaded from .env` being printed (token extraction from `.env` worked)
- `GIT_ASKPASS` being set to the git-askpass script
- The same token working via `curl -H "Authorization: token $TOKEN"` against the GitHub API

## Root Cause

Line 44 of the backup script had a **hardcoded literal `***`** in the repo URL:

```bash
REPO="https://wzklhk:***@github.com/wzklhk/my-hermes-backup.git"
```

**Git's credential precedence**: When a URL contains embedded credentials (`user:password@host`), git uses them directly for the HTTP request and **never calls `GIT_ASKPASS`** — it sends the literal string `***` as the password. The server rejects it immediately with `fatal: Authentication failed`.

## The Fix

Changed the `***` to `${GITHUB_TOKEN}` so the token is substituted at runtime:

```bash
REPO="https://wzklhk:${GITHUB_TOKEN}@github.com/wzklhk/my-hermes-backup.git"
```

## Verification

After the fix, the script ran successfully:

```
Cloned existing repo (attempt 1).
...
Pulled latest from origin (attempt 1).
...
Auto-backup 2026-05-23 03:01:42 (13 files)
Push attempt 1...
To https://github.com/wzklhk/my-hermes-backup.git
   b5388a1..d4ac5b4  HEAD -> main
Backup pushed successfully (13 .md files).
```

## Why the Old Approach Failed

The **intended design** was:
1. URL has `***` as a placeholder/dummy password
2. Git tries the URL credentials → fails (expected)
3. Git falls back to `GIT_ASKPASS` → returns real token
4. Auth succeeds

**Why it doesn't work**: Git does NOT fall through to `GIT_ASKPASS` when URL-embedded credentials are present. It uses them as-is, even if they're obviously invalid. The `GIT_ASKPASS` variable is only consulted when the URL itself has **no credentials at all** (no `user:password@` segment).

From `gitcredentials(7)`:
> If no username or password is found in the URL, Git queries the credential helper or GIT_ASKPASS.

Only when credentials are absent from the URL does git use helpers/askpass.

## Implications

1. **Always embed the real token** — `${GITHUB_TOKEN}`, never `***`
2. `GIT_ASKPASS` is a dead path for URLs with embedded credentials — remove or ignore it
3. The `.env` extraction block at the top of the script is essential — it provides the real token value before the URL is constructed
4. Fine-grained PATs are fine with URL-embedded credentials — no special handling needed

## Token Handling in Cron Context

The script uses this pattern at the top to load the token:

```bash
if [ -z "$GITHUB_TOKEN" ] || [ "$GITHUB_TOKEN" = "***" ]; then
  if [ -f "$HOME/.hermes/.env" ]; then
    TOKEN_VALUE=$(grep '^GITHUB_TOKEN=' "$HOME/.hermes/.env" | cut -d= -f2-)
    if [ -n "$TOKEN_VALUE" ] && [ "$TOKEN_VALUE" != "***" ]; then
      export GITHUB_TOKEN="$TOKEN_VALUE"
    fi
  fi
fi
```

This works because `.env` may have multiple `GITHUB_TOKEN=` lines — Hermes can mask one with `***` on disk while an earlier line still has the real value (or a comment describing the token format). The `grep` picks up the last non-masked line.

## Diagnostic Steps Used

1. **Checked the actual script content** — `sed -n '44p' ~/.hermes/backup-hermes.sh` showed `***` in the URL
2. **Checked `.env` line 401** — confirmed token exists (prefix `github...ciqN`)
3. **Checked git-askpass script** — `echo "$GITHUB_TOKEN"` — correct but never called
4. **Tried clone via terminal** with `${GITHUB_TOKEN}` URL → failed because terminal subprocess had the same `***` extraction issue
5. **Tried GitHub API** with token directly → worked (`curl -H "Authorization: token $TOKEN"`)
6. **Fixed the script** to use `${GITHUB_TOKEN}` → immediate success
