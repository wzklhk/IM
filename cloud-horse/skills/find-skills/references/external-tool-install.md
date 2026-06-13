# Installing External AI Agent Tools (gstack, gbrain, etc.)

When users share "skills" from social media that turn out to be standalone tool systems.

## gstack — AI Engineering Workflow

```bash
# 1. Install runtime
curl -fsSL https://bun.sh/install | bash
export PATH="$HOME/.bun/bin:$PATH"

# 2. Clone and install
git clone https://github.com/garrytan/gstack ~/gstack
cd ~/gstack && bun install

# 3. Link for gbrain discovery
ln -sf ~/gstack ~/.claude/skills/gstack
```

**What you get:** 40+ AI agent skills — plan reviews (`/plan-ceo-review`, `/plan-eng-review`), code review (`/review`), QA (`/qa`), deploy (`/ship`), browser automation (`/browse`), security audit (`/cso`).

**Key commands in repo:** `bun test`, `bun run gen:skill-docs`, `bun run skill:check`

## gbrain — Personal Knowledge Brain

```bash
# 1. Install runtime (skip if bun already installed)
curl -fsSL https://bun.sh/install | bash
export PATH="$HOME/.bun/bin:$PATH"

# 2. Clone and install
git clone https://github.com/garrytan/gbrain ~/gbrain
cd ~/gbrain && bun install

# 3. Initialize brain (applies 50+ schema migrations)
bun run src/cli.ts init
# Output: Brain ready at ~/.gbrain/brain.pglite

# 4. (Optional) Install bundled skills
gbrain skillpack install --all
```

**Brain location:** `~/.gbrain/brain.pglite` (PGLite, zero-config)
**Engine:** PGLite (local Postgres-compatible)
**42 skills loaded**, 38 in skillpack manifest

**Useful commands:**
- `gbrain import <dir>` — import markdown/Obsidian/Notion content
- `gbrain doctor --fix` — health check and auto-repair
- `gbrain soul-audit` — customize agent identity
- `gbrain migrate --to supabase` — upgrade to Supabase when outgrowing PGLite

## Common Failure Patterns (Don't Do These)

| Command | Why it fails | What to do instead |
|---------|-------------|-------------------|
| `npx skills add owner/repo@skill` | Repo has no standard SKILL.md | Clone directly, use native installer |
| `hermes skills install owner/repo@skill` | Same — not a Hermes skill format | Same as above |
| Retrying after first failure | Repo format won't change | Inspect structure, switch approach |

## Quick Diagnosis Script

```bash
# After cloning to /tmp/name:
find /tmp/name -maxdepth 2 -type f | head -20
# Look for: package.json (→ bun/npm tool), skills/manifest.json (→ skillpack), SKILL.md (→ standard skill)
```
