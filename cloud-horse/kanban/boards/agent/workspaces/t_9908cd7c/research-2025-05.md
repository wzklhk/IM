# 研究学习总结 — 2025年AI Agent & LLM基础设施调研

> 生成时间: 2025-05-10
> 类型: 新技术调研

---

## 一、AI Agent 框架动态

### 1. MCP (Model Context Protocol) — 标准化协议层
- Anthropic 推出的开放协议，标准化 LLM 连接外部数据源和工具的方式
- 2025年已成为事实上的工具集成标准，500+ 社区服务器在 GitHub
- 被 GitHub, Replit, Codeium 等主流平台采纳
- **关键特点**: 安全、双向通信、与厂商无关

### 2. Google A2A (Agent-to-Agent) — 跨Agent通信协议
- 2025年4月发布，解决不同框架/厂商的Agent间通信问题
- 支持任务委派、能力发现、结构化 Agent-to-Agent 卡片
- 尚处于早期阶段，有 Salesforce, Atlassian 等合作伙伴

### 3. LangGraph (LangChain) — 生产级图形化Agent
- ⭐ 30k+ GitHub stars，Elastic/Uber/LinkedIn 等企业生产使用
- 基于图的编排，支持循环、分支、并行和人机协同
- LangSmith 可观测性集成是核心优势

### 4. AutoGen (Microsoft) — 对话式多Agent
- ⭐ 30k+ GitHub stars，2025年初发布 AutoGen Core + Magentic-One
- 支持 Agent 组、动态对话模式、代码执行
- 深度集成 Azure/OpenAI

### 5. CrewAI — 角色化多Agent
- ⭐ 20k+ GitHub stars，Python原生，定义有特定角色的Agent团队
- 适合自动化多步骤任务流程

### 6. OpenAI Agents SDK (2025年3月)
- 新发布，简洁的 SDK，内置 handoff 机制
- ⭐ 10k+ stars，原生集成 OpenAI 模型

### 其他值得关注的
- **Smolagents** (Hugging Face): 极简主义，代码即动作
- **Agno** (原 Phidata): 轻量多模态
- **Dapr Agents** (Microsoft, 2025.08): 微服务架构的分布式Agent，生产级

### 2025年趋势总结
| 趋势 | 说明 |
|------|------|
| 协议标准化 | MCP (模型↔工具) + A2A (Agent↔Agent) 两层协议 |
| 多Agent生产化 | 不再只是研究Demo，进入企业部署阶段 |
| 可观测性 | LangSmith, LangFuse 成为Agent调试必备 |
| 小模型+Agent | Smolagents等框架配合本地小模型降低成本 |
| 企业级投入 | Google/Microsoft/AWS 全面布局 |

---

## 二、LLM 基础设施动态

### 1. vLLM — 推理引擎
- **Prefix Caching (APC)**: 对共享前缀（system prompt等）大幅降低延迟，已成默认功能
- **Speculative Decoding**: 支持draft model和Medusa多头推测解码，吞吐量提升1.5-3x
- **Pipeline Parallelism**: 支持跨节点100B+模型
- **Multi-LoRA Serving**: 一个基础模型服务多个微调adapter
- **FP8 KV Cache**: H100/B100原生支持，内存减半

### 2. llama.cpp / GGUF 生态
- GGUF 成为本地推理事实标准，HuggingFace原生支持GGUF上传
- **K-quant演进**: IQ (Importance-aware Quantization) 系列 (IQ2_XXS, IQ3_M, IQ4_NL) 优于传统Q4/Q5
- **MoE支持**: 完整支持Mixtral等混合专家模型
- **Flash Attention**: 128k+上下文在消费级硬件可行
- **多后端**: Metal/Vulkan/SYCL 性能显著提升

### 3. 量化趋势
- **FP8推理/训练**: NVIDIA H100/B100原生支持，内存需求相比FP16减半
- **AWQ**: 成为GPU服务的首选W4A16方法，精度损失极小
- **Ternary/Binary**: BitNet 1.58-bit等研究原型但未生产就绪

### 4. 微调框架
- **Unsloth**: 最热门的LoRA/QLoRA加速工具，2x速度 + 更低显存
- **Axolotl**: 支持多GPU FSDP/QLoRA/DeepSpeed
- **LLaMA-Factory**: 快速成长的全面微调工具包（SFT/RLHF/DPO/ORPO）
- **DPO/ORPO**: 逐渐替代传统RLHF，更简单、更低成本

---

## 三、核心启示

1. **Agent方向**: 如果想跟进最前沿，关注 MCP + A2A 协议演进。生产使用选 LangGraph 或 AutoGen。
2. **基础设施**: vLLM 仍然是LLM serving的首选，GGUF/IQ量化处理本地部署。
3. **微调**: Unsloth 入门最快，Axolotl 适合生产级fine-tuning。DPO/ORPO 替代 RLHF。
4. **小模型时代**: 3B-8B参数模型（Phi-3, Gemma 2, Qwen2.5, Llama 3.2 3B）在Agent场景中已经非常实用。
