---
name: career-transition-planning
description: >-
  Help a mid-career Chinese IT/telecom professional plan a pivot into a new
  technical domain (especially AI engineering). Covers skill assessment,
  gap analysis, financial cushion strategy (广积粮/coast FIRE), project
  portfolio building with multi-agent workflows, city/company targeting,
  and timeline planning.
---

# Career Transition Planning

## Architecture

This skill targets a specific recurring pattern:

> A mid-career (27-35) Chinese engineer has deep domain expertise in one
> field (telecom, networking, embedded, etc.) and wants to pivot into AI
> engineering. They have financial savings, a partner, and are tired of
> their current lifestyle (overseas travel, 996, client-facing ops).

## Core Framework

### Phase 0: Intake — Build Complete Profile

Gather these before any planning. The cascade-correction trap (user corrects
one assumption, invalidating the whole plan) is the #1 failure mode.

**Required fields — always state your assumptions for confirmation after
collecting:**

```
Profile:
  ├─ Current role + years in role + what exactly they do daily
  ├─ Education (school matters in China — 西电/北邮/成电 have strong networks)
  ├─ Age (→ transition window)
  ├─ Target role (not just "AI" — probe: MLOps? AI platform? AI backend? algo?)
  ├─ Target city (tier-1, tier-2, or flexible?)
  ├─ Partner situation (tied to a city, or can relocate?)
  └─ Living situation (西安老家 vs rent vs own)

Financial cushion:
  ├─ Total assets (probe: liquid vs 公积金/社保)
  ├─ Annual savings rate (ask about overseas per-diem separately)
  ├─ Target safety net number (often "100万" is psychological, not FIRE)
  └─ Risk tolerance (fix asset allocation for their age)

Technical foundation:
  ├─ Strongest skills (what's production-grade)
  ├─ What they ENJOY (coding? architecture? ops? management?)
  ├─ Current workflow (do they use AI agents? vibe coding? pair programming?)
  └─ Gap to target role
```

### Phase 1: Assessment — Map Existing Skills to Target Domain

The single most valuable thing you can do: translate their production experience
into the language of the target domain.

**Example (telecom → AI):**

```
Production K8s/OCP experience  →  MLOps / AI inference serving
Java/Spring microservices      →  AI backend platform engineering
Production network ops         →  AI system reliability / observability
Overseas delivery              →  Cross-cultural communication, high-pressure ops
```

**Common gaps for telecom → AI:**
- Python (they know Java; it's a 2-week translation, not a 6-month learning)
- LLM API patterns (prompt engineering, function calling, RAG pipeline)
- Agent frameworks (LangChain, LangGraph)
- MLOps tooling (vLLM, MLflow, Triton)

### Phase 2: Strategy — Financial Cushion + Timeline

**The "广积粮" Strategy (accumulate grain):**

For engineers with overseas/high-savings income, the optimal path is often
one more contract to build a safety net, then pivot:

```
Current savings  →  One more assignment (6-12mo)  →  Safety net hit
   80万                       存20-30万                   100万+
```

Then frame the transition timeline around the project end date.

**Coast FIRE framing (critical):**

When the user says "存到X万就fire", DO NOT take it literally. Probe:
- Do they mean full retirement? (Unlikely at 100万 in China)
- Or "I want the confidence to switch to a lower-paying job I actually like"?

> 100万 at 4% = ¥3,330/mo. In a tier-1 city with a partner, that covers rent
> but not much else. The real value: it lets you take a ¥10-15万 salary cut
> without stress, and STILL save money because you're doing work you enjoy.

### Phase 3: Project Portfolio — The Agent-Assisted Workflow

Many mid-career engineers already use AI agents in their workflow. The key
insight: **their existing workflow is already an AI engineering workflow**,
just applied to non-AI projects.

```
User workflow:                     Applied to AI project:
  ├─ Evaluate tech stack           →  Choose LangChain vs LlamaIndex
  ├─ Design system architecture    →  Design RAG pipeline architecture
  ├─ Delegate coding to agent      →  Agent writes Python AI code
  └─ Test and review               →  Test LLM output quality
```

**Build 2-3 projects minimum:**
1. RAG system (document → QA pipeline)
2. AI Agent (tool-calling, multi-step reasoning)
3. Infrastructure deployment (K8s + vLLM serving)

These should be structured in the user's knowledge vault with design docs,
architecture diagrams, and deployable code.

### Phase 4: City + Company Targeting

Rank target companies by:
1. **Role fit** — does the role match the user's transformed skill set?
2. **Alumni network** — Chinese tech companies hire heavily from specific schools
3. **City lifestyle** — cost of living, partner's job market, cultural fit
4. **Career ceiling** — can they grow in the new role?

### Phase 5: Resume Transformation

Rewrite job descriptions to highlight **target-domain-relevant** skills, not
source-domain terminology.

```
❌ "负责海外电信运营商核心网工程的网络规划与开局部署"
✅ "负责 Red Hat OpenShift 容器平台的生产级部署与运维,
    涵盖 Underlay/Overlay 网络平面设计与基础设施规划"
```

Change title if legitimately warranted:
```
❌ "核心网工程师"
✅ "云原生基础设施工程师"
```

## Multi-Agent Workflow Pattern

This skill assumes a specific three-way collaboration:

```
主人 (User) ── Decision maker, architect, tester
  │
  ├─ 云马 (Cloud Agent / You) ── Strategist, analyst, design docs
  │    • Probes for complete profile (cascade-correction prevention)
  │    • Translates existing skills to target domain
  │    • Writes architecture design docs
  │    • Builds tracking artifacts in knowledge vault
  │    • Sets up cron reminders for milestones
  │
  └─ 本地马 (Local Agent / 郝明智) ── Implementation via vibe coding
       • Writes Python AI code
       • Builds project frameworks
       • Sets up deployment configs
       • Works from design docs produced by 云马
```

All three roles are essential. Do NOT try to do everything yourself — delegate
implementation to the local agent through whatever coordination mechanism
exists (horse-comm, shared repo, etc.).

## Knowledge Vault Artifacts

Always create (or update) these files in the user's Obsidian vault:

| File | Content | Update Frequency |
|------|---------|-----------------|
| `Areas/Career/nepal-year-plan.md` | Monthly execution plan | Per session |
| `Areas/Career/resume-progress.md` | Skill matrix + timeline | Per milestone |
| `Areas/FIRE/MOC.md` | Net worth + FIRE progress | Monthly |
| `Areas/FIRE/net-worth-tracking.md` | Monthly tracking table | Monthly |
| `Areas/FIRE/portfolio-strategy.md` | Asset allocation + ETF DCA plan | Quarterly |
| `Areas/FIRE/fire-plan.md` | Three-phase roadmap | Annual |

## Common Pitfalls

- **Cascade correction**: user corrects one number (e.g., "不是100万是80万")
  and the entire plan needs recalculation. State ALL assumptions before
  presenting any analysis.
- **Assuming "100万" means full retirement**: ALWAYS probe for coast FIRE
  vs full retirement. Frame the difference explicitly.
- **Ignoring the partner**: the user's city choice is often decided by the
  partner's situation. Ask this early.
- **Over-prescribing learning**: don't tell an experienced engineer to
  "learn Python from scratch" — they need a 2-week migration guide from
  their existing language, not a 6-month course.
- **Forgetting to save to knowledge vault**: the artifacts outlive the
  conversation. Always push to vault + GitHub.
- **Empty plans**: every plan needs a concrete "do this tonight" action.
  Generic advice gets ignored.
