---
name: kanban-personal-task-board
description: Set up and manage a personal Kanban task board using the built-in Hermes Kanban CLI. Covers board initialization, lifecycle (create/claim/complete/block/archive), board switching, and common cleanup patterns. NOT for multi-agent routing — see kanban-orchestrator for that.
platforms: [linux, macos, windows]
---

# Kanban Personal Task Board

Use this skill when the user wants to set up a Kanban board for **personal task management** — a single user tracking their own work, not routing tasks to specialist agents.

## When to use

Create a personal board when the user says things like:
- "I need a kanban/看板/task board"
- "帮我建一个看板/任务管理"
- "我想跟踪我的工作"
- "帮我管理任务"

Do NOT use this for multi-agent routing (that's `kanban-orchestrator`).

## Quick Start

```bash
# 1. Initialize the kanban database (idempotent)
hermes kanban init

# 2. Create a named board
hermes kanban boards create <slug> --name "显示名称"

# 3. Switch to it
hermes kanban boards switch <slug>

# 4. Create tasks
hermes kanban create "任务标题" --body "任务描述" --assignee default
```

## CLI Lifecycle

| Action | Command | Effect |
|--------|---------|--------|
| Create | `hermes kanban create "title" --body "..." --assignee default` | Creates task in `ready` state |
| List | `hermes kanban list` | Shows all tasks with status icons |
| Show | `hermes kanban show <id>` | Full detail with comments + events |
| Claim | `hermes kanban claim <id>` | Moves to `running`, creates workspace dir |
| Complete | `hermes kanban complete <id>` | Marks `done` |
| Block | `hermes kanban block <id>` | Marks `blocked` (needs reason in comment) |
| Unblock | `hermes kanban unblock <id>` | Returns to `ready` |
| Archive | `hermes kanban archive <id>` | Hides from list but preserves history |
| Comment | `hermes kanban comment <id> "text"` | Appends to task thread |
| Stats | `hermes kanban stats` | Per-status counts |

## Board Management

```bash
# List all boards
hermes kanban boards list

# Create a new board
hermes kanban boards create agent --name "Hermes日常工作"

# Switch active board
hermes kanban boards switch agent

# See active board (shown in 'list' output header)
hermes kanban list
```

Current board is tracked by `HERMES_KANBAN_BOARD` env var or `hermes kanban boards switch`.

## Cleanup Patterns

### Remove duplicate tasks

When tasks were accidentally created multiple times (e.g., due to shell quoting issues), archive the duplicates:

```bash
hermes kanban archive t_id1 t_id2 t_id3
```

Archived tasks don't appear in `list` output but remain in SQLite for history.

### Recreate a board from scratch

```bash
# Create a fresh board instead of trying to clean the old one
hermes kanban boards create new-slug --name "新看板名"
hermes kanban boards switch new-slug
```

The old board's data persists but is out of sight. No data loss.

## Status Icons

| Icon | Status | Meaning |
|------|--------|---------|
| ◻ | todo | Created but not ready |
| ▶ | ready | Ready for work |
| ● | running | Currently being worked |
| ⊘ | blocked | Blocked, waiting |
| ✓ | done | Completed |
| — | archived | Historical |

## Pitfalls

**Shell quoting with kanban create.** The `hermes kanban create` command uses argparse. Chinese characters, emoji, and special shell characters (`&`, `|`, `;`, `$`) in titles or bodies will break command-line parsing. Three approaches:

1. **Simple titles only** — remove all special chars, keep it ASCII-safe
2. **Use Python** — call through `execute_code` or a tool that avoids shell interpretation:

```python
from hermes_tools import terminal
tasks = [
    ("💻 代码开发", "编写、修改、调试代码"),
    ("📂 文件管理", "文件操作、备份"),
]
for title, body in tasks:
    terminal(f'hermes kanban create "{title}" --body "{body}" --assignee default')
```

3. **Avoid `&&` chaining** with kanban create — each command must succeed independently, and shell `&` backgrounding will break subcommand parsing.

**Running status on create.** Newly created tasks should appear as `▶ ready`. If they show as `● running`, the kanban gateway dispatcher may have claimed them. Verify with `hermes kanban list` and use `reclaim` to reset if needed.

**No auto-tick without gateway.** The kanban dispatcher lives in the gateway. Without `hermes gateway start`, ready tasks won't auto-assign — you must claim them manually with `hermes kanban claim <id>`.
