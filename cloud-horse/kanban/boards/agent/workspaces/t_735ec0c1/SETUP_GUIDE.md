# GitHub DevOps Setup for wzklhk

## What's in this workspace

CI/CD workflow files ready to push to your GitHub repos. The current GITHUB_TOKEN is read-only so workflows couldn't be created directly via API. Once you add write-access credentials, you can push these.

## Repos covered

### 1. wzklhk/mml-manager (main project — Flask + Vue)
Workflow files in `mml-manager/.github/workflows/`:
- **backend-ci.yml** — Python 3.10: pip install, flake8 lint, pytest (triggered on converter/ changes)
- **frontend-ci.yml** — Node.js 18: npm ci, lint, npm build (triggered on frontend/ changes)
- **health-check.yml** — Weekly Monday 6AM UTC: starts Flask, hits /api/health, reports status. Also manually dispatchable.

### 2. wzklhk/dns_resolution_ping_check
Workflow file in `dns_resolution_ping_check/.github/workflows/`:
- **ci.yml** — Python 3.10: pip install, flake8 lint, pytest

### 3. wzklhk/file-renamer
Workflow file in `file-renamer/.github/workflows/`:
- **ci.yml** — Python 3.10: pip install, flake8 lint, pytest

### 4. wzklhk/md-doc-mgr
Workflow file in `md-doc-mgr/.github/workflows/`:
- **ci.yml** — Node.js 18: npm ci, lint

## How to deploy

### Option A: GitHub Web UI (easiest, no CLI needed)

For each repo, go to:
  https://github.com/wzklhk/<repo-name>
  Create the directory `.github/workflows/` (if it doesn't exist)
  Upload the .yml file from this workspace
  Commit directly to main

That's it — GitHub Actions auto-detects the workflows and runs them on next push/PR.

### Option B: Git clone + push (requires write-access token)

1. Generate a PAT with `repo` and `workflow` scopes:
   https://github.com/settings/tokens/new

2. Clone each repo:
   ```
   git clone https://<token>@github.com/wzklhk/<repo-name>.git
   ```

3. Copy the .github/workflows/ files from this workspace into the cloned repo

4. Commit and push:
   ```
   git add .github/
   git commit -m "ci: add GitHub Actions workflows"
   git push
   ```

### Option C: Automate the whole thing

Run the `deploy-ci.sh` script in this workspace — it needs a write-capable GITHUB_TOKEN:
```
bash deploy-ci.sh
```

## Branch protection setup

After CI workflows are in place and passing:

### For each repo you want to protect (recommended: mml-manager):

1. Go to Settings > Branches > Add branch protection rule
2. Branch name pattern: `main`
3. Check:
   - [x] Require pull request reviews (1 approval)
   - [x] Dismiss stale reviews
   - [x] Require status checks (select your CI workflows)
   - [x] Require branches to be up to date
   - [x] Require linear history
4. Save

### Via gh CLI (if you have write-access gh):
```
gh api repos/wzklhk/mml-manager/branches/main/protection \
  --method PUT \
  --field required_status_checks='{"strict":true,"contexts":["Backend CI","Frontend CI"]}' \
  --field enforce_admins=false \
  --field required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true}' \
  --field required_linear_history=true
```
