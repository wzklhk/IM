# Telecom → AI Engineering — Worked Example (2026-05-19)

## Full Session Profile

This is the complete session data that informed the `career-transition-planning`
skill. Referenced when a user profile matches this pattern.

### Persona

| Attribute | Value |
|-----------|-------|
| Gender/Age | Male, 27 (98年生) |
| Education | 西安电子科技大学广州研究院 (CS Master) + 西安理工大学 (EE Bachelor) |
| Current company | 中兴通讯 (ZTE) |
| Current role | Core network overseas delivery (EPC/IMS, NFVI, OpenShift) |
| Years in role | ~3 |
| Annual savings | ~30万 (overseas per-diem heavy) |
| Total assets | ~80万 (≈65万 liquid + 15万 公积金/社保) |
| City preference | 广州 (第二故乡, 精神广东人) → also accepts 深圳/杭州/上海 |
| Partner | In Yangon, can relocate together |
| Technical stack | Java/Spring (expert), K8s/OCP (expert), WebRTC/RTMP, core network |
| Target role | AI platform engineer / MLOps / AI backend |
| Career goal | Coffee FIRE: do AI R&D he enjoys with financial safety net |
| Workflow | Architect + tech lead: evaluates frameworks, designs system, delegates coding to 郝明智 (local AI agent) |

### The Core Tensions Identified

1. "海外交付赚钱多但讨厌运维扯皮" → wants pure R&D, not operations
2. "存够100万就fire" → realized 100万 is not FIRE in China, but is a safety net for career transition
3. "多年没写代码了只记得CRUD" → discovered he's actually operating at architect/tech-lead level, not coder level
4. "回国AI岗薪资可能降" → coast FIRE math shows the portfolio covers the gap
5. "怕转型失败" → 广积粮 strategy: one more overseas project to hit 100万, then transition

### Career Path Decision Tree

```
Current: ZTE overseas delivery (达卡, ~80万 saved)
  │
  ├─→ Option A: Go to Nepal (7月, 6-12 months)
  │     ├─ Continue saving: +20-30万 → hit 100万 ✅
  │     ├─ Learn AI at 3-4 nights/week
  │     └─ Return to Guangzhou with 100万 + AI project portfolio
  │
  └─→ Option B: Return to Guangzhou now (7月)
        ├─ Less financial cushion (60-65万 liquid)
        ├─ Full-time AI learning (faster progress)
        └─ Earlier start to AI career
```

**Chosen path:** Option A (广积粮) — one more project in Nepal to build the safety net.

### Nepal Year Plan Summary

| Month | Focus | Deliverables |
|-------|-------|-------------|
| 1-2 | Python + LLM API basics | Ollama running, DeepSeek API called |
| 3-4 | RAG knowledge base system | Spring Boot + Python: PDF → Q&A pipeline |
| 5-6 | AI Agent | Network diagnostic agent with tool calling |
| 7-8 | Project polish + GitHub | Two repos with README, architecture docs |
| 9-10 | Job applications | Targeted 广州/深圳 AI platform roles |
| 11-12 | Offer → return to Guangzhou | Start AI job, settle with partner |

### Financial Transition Math

```
Scenario: Nepal (1 year) + Guangzhou AI job (following year)

End of Nepal (2027.06):
  Assets: ~103-115万 (80 current + 20-30 saved + 3-5 investment growth)
  
Year 1 in Guangzhou (2027.07-2028.06):
  AI job take-home: ~18-21K/mo (assumes ¥25-30w annual)
  Couple living expenses: ~8-10K/mo (Guangzhou)
  Monthly savings: ~8-12K/mo
  Investment growth: ~5-8% on 100万 portfolio
  Year-end assets: ~120-135万

By age 35 (2033):
  If saving 10K/mo + 5% return: ~250万
  Passive income at 4%: ~10,000/mo → coffee FIRE comfort zone
```

### Three-Agent Workflow (this session's pattern)

This session established a three-role collaboration pattern:

| Role | Who | Responsibility |
|------|-----|---------------|
| 主人 | User | Decision maker, architect, tester, needs |
| 云马 | Cloud agent (Hermes) | Analyst, strategist, design doc writer, memory |
| 本地马 | Local agent (郝明智) | Vibe coding implementer |

**Cloud agent protocol for career transition sessions:**
1. Intake phase: build complete profile, state assumptions before analysis
2. Assessment phase: translate existing skills to target domain language
3. Strategy phase: financial cushion plan + timeline
4. Portfolio phase: design docs → local agent implements → user reviews
5. Push all artifacts to knowledge vault + GitHub backup
6. Set up cron reminders for monthly tracking
