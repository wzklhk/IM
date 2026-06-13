---
name: github
description: "Interact with GitHub using the `gh` CLI. Use `gh issue`, `gh pr`, `gh run`, and `gh api` for issues, PRs, CI runs, and advanced queries."
---

# GitHub Skill

Use the `gh` CLI to interact with GitHub. Always specify `--repo owner/repo` when not in a git directory, or use URLs directly.

## Pull Requests

Check CI status on a PR:
```bash
gh pr checks 55 --repo owner/repo
```

List recent workflow runs:
```bash
gh run list --repo owner/repo --limit 10
```

View a run and see which steps failed:
```bash
gh run view <run-id> --repo owner/repo
```

View logs for failed steps only:
```bash
gh run view <run-id> --repo owner/repo --log-failed
```

## API for Advanced Queries

The `gh api` command is useful for accessing data not available through other subcommands.

Get PR with specific fields:
```bash
gh api repos/owner/repo/pulls/55 --jq '.title, .state, .user.login'
```

## Repository Content (Files & Directories)

The GitHub Contents API lets you read, create, update, and list files directly — no `git clone` needed. This is especially useful in cron or automation contexts where `git` operations may be blocked by security scanners or network restrictions.

### List directory contents

```bash
# List files and subdirectories
gh api repos/owner/repo/contents/path/to/dir \
  --jq '.[] | "[\(.type)] \(.name) (\(.size // "-") bytes)"'

# Count message files matching a pattern
gh api repos/owner/repo/contents/path/to/dir \
  --jq '[.[] | select(.name | startswith("msg_"))] | length'
```

### Read a file

```bash
# Get file content (base64-decoded)
gh api repos/owner/repo/contents/path/to/file.md \
  --jq '.content | @base64d'

# Get metadata only (download URL from CN-friendly endpoint)
gh api repos/owner/repo/contents/path/to/file.md \
  --jq '{name: .name, size: .size, url: .download_url}'
```

### Create or update a file

Requires a JSON payload with the file content base64-encoded:

```bash
# Prepare the payload
CONTENT_B64=$(echo -n "# Hello\nThis is new content." | base64 -w0)
cat > /tmp/payload.json << 'EOF'
{
  "message": "Add new file via API",
  "content": "'$CONTENT_B64'",
  "branch": "main"
}
EOF

# Create the file
gh api repos/owner/repo/contents/path/to/new-file.md \
  --method PUT --input /tmp/payload.json
```

To **update** an existing file, include its SHA (get it from the first read):

```bash
# Get the SHA of the existing file
SHA=$(gh api repos/owner/repo/contents/path/to/file.md --jq '.sha')

# Update with the SHA in payload
CONTENT_B64=$(echo -n "# Updated content" | base64 -w0)
cat > /tmp/payload.json << 'EOF'
{
  "message": "Update file",
  "content": "'$CONTENT_B64'",
  "sha": "'$SHA'",
  "branch": "main"
}
EOF

gh api repos/owner/repo/contents/path/to/file.md \
  --method PUT --input /tmp/payload.json
```

### Delete a file

```bash
SHA=$(gh api repos/owner/repo/contents/path/to/file.md --jq '.sha')
cat > /tmp/payload.json << 'EOF'
{
  "message": "Delete file",
  "sha": "'$SHA'",
  "branch": "main"
}
EOF

gh api repos/owner/repo/contents/path/to/file.md \
  --method DELETE --input /tmp/payload.json
```

### Important caveats

- **File size limit**: The Contents API handles files up to 1 MB. For larger files, use the Git Data API (blobs + trees + commits).
- **`gh` must be authenticated**: Run `gh auth status` to verify. Credentials are stored in `~/.config/gh/hosts.yml`, not in `.env`.
- **No `git` needed**: All operations above work without cloning or pulling — ideal for cron jobs, serverless functions, or environments where `git` is slow or blocked.
- **Rate limits**: Unauthenticated: 60 req/hr. Authenticated via `gh`: 5,000 req/hr. The Contents API counts toward this limit.

## JSON Output

Most commands support `--json` for structured output.  You can use `--jq` to filter:

```bash
gh issue list --repo owner/repo --json number,title --jq '.[] | "\(.number): \(.title)"'
```
