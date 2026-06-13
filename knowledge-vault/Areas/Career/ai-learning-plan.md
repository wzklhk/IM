# 🚀 AI转型学习计划（完整版）

> 从 2026.06 → 归国入职，5个阶段
> 目标：广州AI平台/MLOps工程师，年薪30-50万

---

## 总体策略

```
你的独特优势 = K8s运维经验 + Java架构能力 + Agent工作流
你的学习方式 = 郝明瑞出架构 → 郝明智写代码 → 你审+调
你的节奏     = 工作日1-2h，周日4h项目实操，不熬夜
你的验收     = 每个阶段有可展示的GitHub项目
```

---

## ⚡ Phase 0：出发前冲刺（2026.06 → 07 出发，~4周）

> **背景：** 你在达卡，可能忙于尼泊尔前准备。这阶段保持轻量。
> **目标：** Python捡起来 + 环境就绪 + 跑通第一个LLM调用

### 每周计划

| 周 | 主题 | 具体任务 | 交付物 |
|:---:|------|---------|-------|
| 1 | Python速通 | ① 把 `QuantBase/` 里所有Python代码读一遍，理解每行 ② 用Python重写一个Java常见操作（如文件处理、HTTP请求、JSON解析） | 写一份《Java→Python对照表》笔记 |
| 2 | LLM API入门 | ① 注册DeepSeek API（或硅基流动） ② 用Python调API做一次对话 ③ 理解 `messages`、`temperature`、`max_tokens`、`stream` | 一个 `hello_llm.py` 能跑通的脚本 |
| 3 | Prompt Engineering | ① 读 [Prompt Engineering Guide](https://www.promptingguide.ai/zh) 前3章 ② 用同一个问题，对比不同prompt的输出差异 ③ 理解 system/user/assistant 角色 | 一个prompt对比实验笔记 |
| 4 | Agent初体验 | ① 选一个国产Agent框架跑通demo（推荐CrewAI或Dify） ② 理解 Agent = LLM + 工具 + 记忆 + 规划 | 跑通一个"天气查询Agent"demo |

### Phase 0 验收标准
```bash
□ 能用Python独立写一个调LLM API的程序
□ 能解释 prompt 怎么影响输出质量
□ 设备上已装好 Python 3.12 + Ollama（或确认VPS上可用）
□ 知道AI Agent的基本概念（工具调用、多步推理）
```

---

## 🏔️ Phase 1：加德满都安顿期（7月抵达 → 8月底，~8周）

> **目标：** 适应环境 + RAG系统从零到一

### 学习模块

#### 模块 A：Python 工程化（前2周）
| 主题 | 学什么 | 练习 |
|------|--------|-----|
| 类型与Pydantic | `pydantic.BaseModel`、类型注解 | 把之前的API调用脚本改成类型安全版 |
| 异步编程 | `async/await`、`aiohttp` | 并发调3个LLM API比较结果 |
| 日志与配置 | `loguru`、`yaml`配置管理 | 重构一个项目加上日志系统 |
| 测试 | `pytest` 基础、mock | 给QuantBase的模块写2个测试 |

#### 模块 B：向量与检索（第3-4周）
| 主题 | 学什么 | 郝明智配合 |
|------|--------|-----------|
| Embedding原理 | 文本→向量的数学直觉（不需要推导） | 写一个embedding可视化脚本 |
| 向量数据库 | ChromaDB（轻量，本地跑） | 搭好ChromaDB + 写入你的知识库文档 |
| 语义检索 | 相似度搜索、top_k调参 | 你搜"标普500投资"，看召回质量 |

#### 模块 C：RAG Pipeline（第5-8周）
| 主题 | 学什么 | 郝明智配合 |
|------|--------|-----------|
| 文档处理 | `langchain` 或 `llama-index` 加载PDF/Markdown | 写文档加载器 |
| 分块策略 | chunk_size、overlap、语义分块 | 写不同策略的对比实验 |
| 检索增强 | 基础RAG→带上下文的提示词模板 | 写完整的RAG Pipeline |
| 效果评估 | 准备20道测试题，人工评估回答质量 | 写评估脚本 |

### Phase 1 交付物

```
GitHub Repo: wzklhk/rag-knowledge-qa
  ├── data/              — 示例文档（用你的知识库文档脱敏后）
  ├── embeddings/        — ChromaDB持久化
  ├── src/
  │   ├── loader.py      — 文档加载
  │   ├── chunker.py     — 分块策略
  │   ├── retriever.py   — 检索逻辑
  │   └── pipeline.py    — 完整RAG链路
  ├── app.py             — FastAPI / Spring Boot包装
  ├── eval_questions.json — 20道测试题+标准答案
  ├── README.md          — 含架构图
  └── docker-compose.yml — 一键启动
```

### Phase 1 验收标准
```bash
□ 上传一篇技术文档，能问出文档里80%的内容
□ 能解释RAG每个环节在做什么、为什么这样设计
□ 系统在本地/云VPS上跑通
□ README里有架构图 + 使用说明
```

---

## 🤖 Phase 2：AI Agent 实战（9月 → 10月底，~8周）

> **目标：** 做一个带多工具调用的AI Agent，跑在K8s上

### 学习模块

#### 模块 A：Function Calling 深挖（第1-2周）
| 主题 | 学什么 | 练习 |
|------|--------|-----|
| Tool Definition | JSON Schema定义工具、OpenAI function calling格式 | 定义3个工具（查天气、算数学、搜索文件） |
| Tool Router | LLM自动选择工具 → 执行 → 回传结果 | 写一个能自动路由的多工具Agent |
| 错误处理 | 工具调用失败时的fallback策略 | 故意给错误参数，看Agent如何handle |

#### 模块 B：Agent框架（第3-4周）
| 主题 | 学什么 | 郝明智配合 |
|------|--------|-----------|
| LangGraph | State Graph、节点流转、条件分支 | 写一个多Agent协作的Graph（如：分析员→研究员→写手） |
| Agent记忆 | 短期记忆（对话历史）+ 长期记忆（向量库） | 实现一个有记忆的Agent |
| 工具生态 | MCP协议、工具注册与发现 | 注册3个MCP工具，Agent自动发现并调用 |

#### 模块 C：实战项目 — 运维诊断Agent（第5-8周）
```
场景：你给Agent一个VPS地址，Agent自动：
  1. SSH连接 → 检查CPU/内存/磁盘
  2. 读取应用日志 → 分析异常
  3. 检查Docker容器状态
  4. 输出诊断报告 + 修复建议
```

| 周 | 任务 | 交付 |
|:---:|------|------|
| 5 | 定义工具集（SSH、kubectl、docker ps、tail log） | 工具函数全部写完 |
| 6 | LangGraph编排诊断流程 | 跑通一个诊断场景 |
| 7 | 加前端（简单的Chat UI，或WeChat Bot） | 能通过微信触发Agent |
| 8 | Docker打包 → K8s部署 → 写架构文档 | 完整的GitHub仓库 |

### Phase 2 交付物

```
GitHub Repo: wzklhk/ops-agent
  ├── tools/
  │   ├── ssh_tool.py     — SSH远程执行
  │   ├── docker_tool.py  — Docker状态检查
  │   ├── log_tool.py     — 日志分析
  │   └── k8s_tool.py     — K8s集群检查
  ├── agent/
  │   ├── graph.py        — LangGraph诊断流程
  │   └── memory.py       — 诊断历史记忆
  ├── deploy/
  │   ├── Dockerfile
  │   └── k8s-manifest.yaml
  └── README.md
```

### Phase 2 验收标准
```bash
□ Agent能接收"检查这个VPS的健康状态"指令 → 自动执行 → 出报告
□ 包括至少4个工具，Agent能正确选择
□ K8s部署成功，有Helm chart
□ 架构文档能讲给面试官听
```

---

## 🏗️ Phase 3：MLOps 基础设施（11月 → 12月底，~8周）

> **目标：** 搭建一个AI模型推理平台，这是你区别于普通AI工程师的核心项目

### 学习模块

#### 模块 A：推理引擎（第1-3周）
| 主题 | 学什么 | 练习 |
|------|--------|-----|
| vLLM | 安装、启动、OpenAI兼容API、吞吐量对比 | 在VPS上部署一个7B模型，压测QPS |
| 量化 | GGUF格式、llama.cpp、INT4/INT8 | 对比量化前后的速度和质量 |
| 模型管理 | HuggingFace Hub、模型下载、版本管理 | 用 `huggingface_hub` 写模型下载脚本 |

#### 模块 B：推理平台化（第4-6周）
| 主题 | 学什么 | 郝明智配合 |
|------|--------|-----------|
| 模型注册 | 一个简单注册中心（SQLite/dict-based） | 写注册+发现逻辑 |
| 负载均衡 | 多实例部署、Nginx/Traefik反向代理 | 写部署+配置 |
| 自动扩缩 | K8s HPA 基于GPU/QPS的弹性伸缩 | 写HPA配置+压测验证 |

#### 模块 C：MLflow 实验管理（第7-8周）
| 主题 | 学什么 | 练习 |
|------|--------|-----|
| MLflow Tracking | 记录实验参数、指标、模型 | 跑3组prompt实验，在MLflow里对比 |
| MLflow Registry | 模型版本管理、staging→production | 模拟模型上线流程 |

### Phase 3 交付物

```
GitHub Repo: wzklhk/inference-platform
  ├── engine/
  │   └── vllm_deploy.sh       — 一键部署脚本
  ├── registry/
  │   └── model_registry.py    — 模型注册中心
  ├── gateway/
  │   └── nginx.conf           — 推理流量路由
  ├── k8s/
  │   ├── deployment.yaml
  │   └── hpa.yaml
  ├── experiments/              — MLflow实验记录截图
  └── README.md
```

### Phase 3 验收标准
```bash
□ 能从零部署一个LLM推理服务（vLLM），通过HTTP调用
□ 能展示3个模型实例的负载均衡
□ MLflow里至少有3组实验记录
□ 整套流程写在README里，面试官能看懂你在做什么
```

---

## 🎯 Phase 4：面试冲刺（2027.01 → 归国，~8-12周）

> **目标：** 3个精品GitHub项目 + LeetCode150 + 系统设计准备

### 项目打磨（前2周）
- 3个项目README全部重写：问题 → 方案 → 架构 → 效果
- 每个项目录一个5分钟演示视频（或GIF截图序列）
- GitHub Profile 装修：Pinned repos、个人简介、技能标签

### 算法刷题（全程并行，周末集中）
```
优先级：
  Tier 1: 数组、哈希表、字符串、双指针
  Tier 2: 树、图（BFS/DFS）、堆
  Tier 3: 动态规划（只做经典题）
  Tier 4: 系统设计

目标：LeetCode 中等150题
节奏：每周末集中刷5道，周中复习
```

### 系统设计准备
| 主题 | 练什么 |
|------|--------|
| 设计一个RAG系统 | 架构图、技术选型、扩容方案 |
| 设计一个Agent平台 | 多租户、工具管理、推理调度 |
| 设计一个推理服务网关 | 路由、限流、模型热更新 |

### 面试投递
```
投递优先级：
  T0: 广研院校友内推（最高成功率）
  T1: 广州AI公司（微信AI、网易、树根互联、云从、暗物智能）
  T2: 深圳AI公司（腾讯混元、商汤、思谋）
  T3: 猎头渠道（脉脉、BOSS直聘）

每月目标：
  第1个月 → 投20家，拿5个面试
  第2个月 → 从面试反馈中迭代简历
  第3个月 → 拿2-3个offer比选
```

### Phase 4 验收标准
```bash
□ GitHub Profile 有3个精品AI项目（RAG + Agent + Inference）
□ LeetCode 中等150题完成率 > 80%
□ 能脱稿讲"设计一个RAG系统"15分钟
□ 拿至少2个offer
```

---

## 📊 技能矩阵（实时更新）

| 技能 | 当前 | Phase 0 | Phase 1 | Phase 2 | Phase 3 | 目标 |
|:---|:---:|:---:|:---:|:---:|:---:|:---:|
| Python工程化 | 🟡 | 🟡 | 🟢 | 🟢 | 🟢 | 🟢 |
| LLM API调用 | 🔴 | 🟢 | 🟢 | 🟢 | 🟢 | 🟢 |
| Prompt Engineering | 🔴 | 🟡 | 🟢 | 🟢 | 🟢 | 🟢 |
| RAG系统 | 🔴 | 🔴 | 🟢 | 🟢 | 🟢 | 🟢 |
| Agent框架 | 🔴 | 🔴 | 🔴 | 🟢 | 🟢 | 🟢 |
| 推理部署(vLLM) | 🔴 | 🔴 | 🔴 | 🔴 | 🟢 | 🟢 |
| K8s运维 | 🟢 | 🟢 | 🟢 | 🟢 | 🟢 | 🟢 |
| LeetCode | 🔴 | 🔴 | 🟡 | 🟡 | 🟡 | 🟢 |
| 系统设计 | 🔴 | 🔴 | 🔴 | 🟡 | 🟡 | 🟢 |

> 🔴 = 未接触  🟡 = 入门  🟢 = 熟练

---

## 🔄 协作分工（不变）

```
你（主人）      → 决策 + 架构审查 + 效果验收
郝明瑞（云马）  → 出方案 + 出架构设计 + 答疑 + 进度追踪
郝明智（本地马）→ 写Python代码 + 搭环境 + 写部署配置
```

每次有进展在微信上跟我说，我帮你：
- 审架构设计
- 卡住时给方向
- 验证你的理解是否正确
- 更新这个计划和进度矩阵

---

## 📅 立即行动

**今晚就做这3件事：**

1. ☐ 把 `QuantBase/` 里的 `.py` 文件全部浏览一遍，标记看不懂的地方
2. ☐ 在 https://cloud.siliconflow.cn 注册账号，拿到API Key
3. ☐ 用下面的代码跑通第一个LLM调用：
```python
# hello_llm.py — 今晚就写这个
from openai import OpenAI
client = OpenAI(
    api_key="你的key",
    base_url="https://api.siliconflow.cn/v1"
)
r = client.chat.completions.create(
    model="deepseek-ai/DeepSeek-V3",
    messages=[{"role": "user", "content": "用三句话介绍RAG是什么"}]
)
print(r.choices[0].message.content)
```
