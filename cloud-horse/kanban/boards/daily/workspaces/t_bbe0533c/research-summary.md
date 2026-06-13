# 研究学习总结 — 2026-05-10

Today's research covered **three recent papers** from arXiv (May 2026), all at the intersection of LLM architecture and reasoning.

---

## 1. EMO: Pretraining Mixture of Experts for Emergent Modularity

| Field | Detail |
|---|---|
| **Authors** | Ryan Wang, Akshita Bhagia, Sewon Min |
| **arXiv** | 2605.06663 |
| **Categories** | cs.CL |

**Core idea:** Standard MoEs can't be sliced — restricting inference to a subset of experts for a given domain causes severe degradation. EMO fixes this by using *document boundaries* as a training signal: tokens within the same document share an expert pool. This causes coherent domain-level expert groupings to emerge naturally during pretraining without human priors.

**Key results:**
- Pretrained a 1B-active / 14B-total MoE on 1T tokens
- As a full model, matches standard MoE performance
- *Retaining only 25% of experts → only 1% accuracy drop* (vs standard MoE which breaks)
- Experts specialize at *semantic* levels (math, code) rather than *syntactic* levels
- Enables composable, memory-efficient deployment of large sparse models

**Takeaway:** If you need to deploy a large MoE on a single GPU or edge device, EMO's approach lets you keep only the experts relevant to your domain without retraining.

---

## 2. UniPool: A Globally Shared Expert Pool for Mixture-of-Experts

| Field | Detail |
|---|---|
| **Authors** | Minbin Huang, Han Shi, Chuanyang Zheng, Yimeng Wu, Guoxuan Chen, Xintong Yu, Yichun Yin, Hong Cheng |
| **arXiv** | 2605.06665 |
| **Categories** | cs.LG, cs.AI |

**Core idea:** Current MoE assigns each layer its own set of experts, meaning expert parameters grow linearly with depth. But the authors probe this assumption and find that replacing deep-layer routers with random routing only drops 1.0-1.6 accuracy points. So they propose **UniPool**: a single shared expert pool accessed by all layers' routers.

**Key results:**
- Trained LLaMA models at 5 scales (182M–978M) on 30B tokens from The Pile
- Consistently improves validation loss (up to -0.0386 vs vanilla MoE)
- Reduced-pool variants using only 41.6%–66.7% of the expert-parameter budget *still match or beat* vanilla MoE
- Composes with finer-grained expert decomposition

**Takeaway:** Expert parameters need not grow linearly with depth. A shared pool approach is both more parameter-efficient and more effective. Good for scaling MoEs while keeping memory manageable.

---

## 3. VHG: Verifier-Backed Hard Problem Generation for Mathematical Reasoning

| Field | Detail |
|---|---|
| **Authors** | Yuhang Lai, Jiazhan Feng, Yee Whye Teh, Ning Miao |
| **arXiv** | 2605.06660 |
| **Categories** | cs.LG, cs.AI, cs.CL |

**Core idea:** LLMs can solve math problems but struggle to *generate* valid, challenging, novel ones. Naive self-play approaches suffer from reward hacking (setter and solver collude). VHG introduces a **three-party self-play** system: setter → solver → verifier. The setter's reward depends on both validity (by verifier) and difficulty (by solver), preventing gaming.

**Key results:**
- Two verifier variants: Hard (symbolic) and Soft (LLM-based)
- Evaluated on indefinite integration and general math reasoning tasks
- Substantially outperforms all baselines

**Takeaway:** For anyone working on synthetic data generation for reasoning models (GRPO, process reward models, etc.), this three-party verification approach is directly applicable. It solves the reward-hacking problem that plagues self-play generation.

---

## Connections & Synthesis

1. **Both MoE papers** challenge the standard per-layer expert allocation. EMO does it via document-level routing constraints for modularity; UniPool does it via a global shared pool for parameter efficiency. They're complementary and could potentially be combined.

2. **VHG** addresses the synthetic data bottleneck — generating diverse, hard training problems without human annotation. This feeds directly into the training pipeline that models like EMO/UniPool would use.

3. **Practical workflow:** VHG → generate hard math problems → train a reasoning MoE (EMO/UniPool architecture) → deploy with domain-specific expert subsets.
