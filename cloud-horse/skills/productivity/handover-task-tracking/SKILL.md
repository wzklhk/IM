---
name: handover-task-tracking
description: Structured handover/移交 checklist management in PARA knowledge vault. Build and maintain multi-item handover trackers with status, blocking reasons, responsible person chains, update logs, and daily themed cron reminders. For telecom/project delivery engineers tracking work through handover period.
---

# Handover Task Tracking

Use when the user needs to build or maintain a structured handover/移交 checklist — tracking multiple work items with status changes, blocking dependencies, and automated daily reminders.

## When to use

- User says "离场交接", "handover", "移交", "任务追踪"
- User wants to track blockers/dependencies across multiple teams or people
- User asks for daily/weekly reminders about work progress
- User provides updates about specific task statuses

## Structure in PARA Vault

All handover artifacts live under `knowledge-vault/Work/`:

```
knowledge-vault/Work/
├── README.md              # Overview + dashboard of active tasks
├── tasks/
│   └── index.md           # Task details, one per project
├── memos/
│   ├── index.md           # Memo log index
│   └── yyyy-mm-dd-topic.md  # Per-entry notes
└── handover/
    └── project-handover-checklist.md  # The canonical checklist
```

### README.md — Dashboard

High-level status table showing all tasks:

```
## 📊 当前活跃任务

| 编号 | 任务 | 状态 | 截止 |
|:---:|:----|:---:|:----:|
| 1 | GiDNS SAV 局点 | ❌ 阻塞 | 离场前 |
| 2 | USPP SPR 割接 | 🔄 推进中 | 离场前 |
```

Pin the latest memo at the bottom.

### Handover Checklist — Detailed Item Format

Each item follows this structure:

```markdown
## N. Item Title

- **状态：** ✅ completed | 🔄 in-progress | ❌ blocked | ⏳ pending-review
- **阻塞原因：** ⛔ specific blocker description — who/when/why
- **行动项：**
  - [ ] Next action #1 — who does what
  - [ ] Next action #2
- **负责人：** Person A → Person B → Person C (dependency chain)
- **截止：** deadline if known
```

#### Status Icon Convention

| Icon | Meaning | When |
|:----:|:--------|:-----|
| ✅ | Completed | DONE |
| 🔄 | In progress | Being worked |
| ❌ | Blocked/unfinished | Has open blocker |
| ⏳ | Pending | Waiting for review/approval |

#### Blocking Reason Convention

```
⛔ specific blocker description — who kickstarted action, outcome
```

Example:
```
⛔ pending 在承载防火墙上 — Moshiur 已转发邮件给客户确认
```

#### Responsible Person Chain

Show the dependency flow with arrows:

```markdown
- **负责人：** Arnab（DFD已批）→ Faisal（催防火墙）→ Alamgir（上电）→ 你（后续扩容）
```

This lets you see at a glance who needs to act next on any given item.

#### Site/Sublocation Status Table

When items span multiple sites/locations:

```markdown
| 站点 | 上电状态 |
|:----|:--------:|
| Sylhet (SYL) | ❌ No |
| Jashore (JES) | ❌ No |
| Cumilla (CML) | ✅ Yes |
| Bogura (BOG) | ⚠️ 仅 ZTE 服务器已上电 |
```

#### Update Log

Keep a chronological log at the bottom:

```markdown
## 更新日志

| 日期 | 变更 |
|------|------|
| 2026-06-03 | 更新 #1 阻塞原因为承载防火墙；新增 #4 SAV OCP 开局 |
| 2026-06-03 (2) | 更新 #3 DFD 已批，新增站点电源状况 + Faisal 催防火墙 |
```

Use `(2)`, `(3)` for same-day multiple updates.

## Daily Cron Reminder — Theme-Organized

When the user wants a daily reminder, create a cron job that:

1. Reads the handover checklist
2. Groups items by **theme** (not flat list)
3. Produces a compact daily reminder

### Theme Grouping Convention

Group related items under a shared heading, but keep them as distinct entries. Do NOT merge same-blocker items into one — even if two items have the same blocker, show them separately with identical blocking notes.

Example themes for a telecom project:
- **🏗️ SAV 开局** — Site deployment items
- **🔄 USPP 割接** — Cutover items
- **🖥️ OCP 平台运维** — Platform ops items

### Cron Format

```yaml
name: project-daily-reminder
schedule: "0 9 * * 1-5"   # 09:00 CST weekdays (user's 07:00)
deliver: origin
prompt: |
  Read ~/workspace/knowledge-vault/Work/handover/project-checklist.md, generate themed daily reminder.

  ### 🏗️ Theme 1
  **① Item A**
  - 状态/阻塞：...
  - 今日行动：...

  **② Item B**  
  - 状态/阻塞：...
  - 今日行动：...

  ### 🖥️ Theme 2
  ...
```

### Cron Prompt Details

The prompt must explicitly describe the desired format — don't rely on the model to guess:

- State the theme groups and which items belong to each
- List each item with `①`, `②`, `③` numbering
- Specify fields: `状态/阻塞`, `今日行动`
- End with a brief summary line
- Note: weekends not running

### Cron Timezone Handling

VPS runs at UTC+8 (CST). User may be in UTC+6 (BDT) or elsewhere.
- User's 07:00 local = VPS 09:00 (for UTC+6)
- If user changes timezone, update BOTH memory AND all cron schedules

## Adding New Items Mid-Session

When the user provides an update during conversation:

1. **Update the handover checklist** — patch the relevant item or add a new one with sequential numbering
2. **Update tasks/index.md** — mirror the status table
3. **Create a memo** — `Work/memos/yyyy-mm-dd-topic.md` with the conversation notes
4. **Save key facts to memory** — person names (e.g., "Alamgir 是站点工程师"), responsibility chains, blocker details

### New Item Format

```markdown
## N. New Item Title

- **状态：** ❌ description
- **待办：**
  - [ ] First action — who does what
- **负责人：** Person → Person
- **截止：** deadline
```

## Pitfalls

- **Same blocker ≠ same item.** Two items may share the same blocking reason (e.g., both pending on firewall). Display them separately with identical blocking notes. DO NOT merge.
- **Responsible person chain changes.** When a dependency step completes, update the chain to show what's next. E.g., "DFD已批" changes the chain from "等待审批" to "催实施".
- **Don't over-update the README.** The README dashboard should stay high-level. Detailed status goes in the checklist and memos.
- **Update log for traceability.** Always append to the update log so the user can see what changed and when.
- **Cron prompt drift.** If the checklist structure changes (new items, new themes), update the cron prompt accordingly. The cron prompt explicitly describes the format, so it won't auto-adapt.
- **Multiple cron jobs at same time.** The daily handover reminder runs at 09:00 CST. If another cron (e.g., ETF report) also runs at the same time, they deliver independently — no conflict.
