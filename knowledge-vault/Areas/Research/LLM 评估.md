---
created: 2026-05-10
tags: [research, llm, evaluation]
---

# LLM 评估指南

## 评估框架

### 标准 Benchmarks
- **MMLU** — 多任务语言理解
- **GSM8K** — 数学推理
- **HumanEval** — 代码生成
- **MT-Bench** — 多轮对话

### 评估工具
- **lm-eval-harness** — 主流评估框架，支持 200+ benchmark
  ```bash
  lm_eval --model hf --model_args pretrained=<model> \
    --tasks mmlu,gsm8k --num_fewshot 5
  ```

## 评估流程
1. 选择 benchmark (覆盖推理/知识/代码/安全)
2. 配置推理后端 (vLLM / llama.cpp)
3. 运行评估
4. 分析结果 (准确率/延迟/显存)
5. 对比基线模型

## 参考
- [[本地 LLM 部署方案对比]]
- [lm-eval-harness 仓库](https://github.com/EleutherAI/lm-evaluation-harness)
