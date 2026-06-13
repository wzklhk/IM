# 环境评估：模型部署与微调可行性分析

> 评估时间：2026-05-10

## 环境概况

| 项目 | 值 |
|------|-----|
| CPU | AMD EPYC 7K62 48-Core Processor（2 核分配） |
| RAM | 1.9 GiB（可用约 634 MiB） |
| GPU | 无 |
| 磁盘 | 有（在 Docker/SSH 中可能受限） |
| Python | 3.11.15 |
| API 提供商 | DeepSeek Chat（deepseek-chat 模型） |

## 一、模型部署可行性

### ❌ 本地大模型部署（不可行）

| 模型大小 | 最低 RAM 需求 | 判断 |
|---------|--------------|------|
| 7B Q4_K_M (~4.5 GB) | ~6 GB | ❌ 不足 |
| 3B Q4_K_M (~2 GB) | ~3 GB | ❌ 不足 |
| 1.5B Q4_K_M (~0.9 GB) | ~1.5 GB | ⚠️ 勉强，但只剩约 634 MB 可用 |
| 0.5B Q4_K_M (~0.3 GB) | ~0.8 GB | ✅ 理论上可行 |

**结论：** 仅 0.5B 级别的小模型（如 TinyLlama、Qwen2.5-0.5B-GGUF）可以在本地运行，但实际价值有限。建议使用 API。

### ✅ 推荐方案：API 部署
- **当前使用：** DeepSeek Chat 通过 Hermes Agent 直接调用
- **替代方案：** 
  - OpenRouter（多模型路由，支持 Claude/GPT/开源模型）
  - 本地通过 llama.ccp 运行 GGUF 小模型（0.5B-1B 级别）
  - Modal GPU Cloud（需注册，按需付费，提供 GPU 实例）

## 二、模型微调可行性

### ❌ 本地微调（不可行）

| 方法 | GPU/VRAM 需求 | CPU RAM 需求 | 判断 |
|------|-------------|-------------|------|
| 全量微调 7B | 60+ GB VRAM | - | ❌ |
| LoRA 7B | 16+ GB VRAM | 32+ GB | ❌ |
| QLoRA 7B | 8+ GB VRAM | 20+ GB | ❌ |
| LoRA 1.5B | 4+ GB VRAM | 8+ GB | ❌ |

### 🟡 可行方案

#### 方案 A：云端微调
1. **Modal GPU Cloud** — 按秒计费，无需长期保留 GPU
   - 推荐：A100-80G:1 实例，约 $3/hr
   - 适用于：7B-70B 模型的 LoRA/QLoRA 微调
   - 参考技能：`modal-serverless-gpu`

2. **Unsloth + Colab** — 免费 T4（16GB VRAM）
   - 适用于：1.5B-7B 模型的 LoRA 微调
   - 参考技能：`unsloth`

3. **AutoDL / 矩池云** — 国内 GPU 租赁
   - ¥2-15/小时，4090/A100 可选

#### 方案 B：API 层面 "微调"（无 GPU 方案）
1. **Prompt 工程化** — 无需 GPU，优化效果显著
   - 结构化 prompt + 少样本示例
   - 参考 `01-prompt-optimization.md`

2. **DSPy** — 自动优化 prompt 和 few-shot
   - 纯 API 调用，无需本地 GPU
   - 参考技能：`dspy`

3. **开源模型的 API 微调** — 部分平台支持
   - OpenRouter 部分模型支持微调（需付费）
   - Together AI、Fireworks 等平台提供无 GPU 微调 API

## 三、推荐路线

### 当前环境最佳实践

```
短期（零成本）         中期（少量投入）        长期
    │                     │                     │
    v                     v                     v
┌─────────────┐    ┌──────────────┐    ┌──────────────┐
│ Prompt 优化  │ →  │ DSPy 自动化   │ →  │ Modal/Unsloth│
│ 推理参数调优  │    │ API 微调评估  │    │ 正式微调     │
│ 结构化模板    │    │ 小模型本地测试 │    │ 模型部署服务 │
└─────────────┘    └──────────────┘    └──────────────┘
```

## 四、已安装的 MLOps 工具

以下技能在 Hermes Agent 中可用：

| 技能 | 用途 | 适用场景 |
|------|------|---------|
| `llama-cpp` | 本地 GGUF 推理 | 小模型 CPU 推理 |
| `huggingface-hub` | 模型下载/搜索 | 模型发现 |
| `evaluating-llms-harness` | 模型评测 | 对比测试 |
| `unsloth` | 高效微调 | 有 GPU 时 |
| `peft-fine-tuning` | PEFT 微调 | 有 GPU 时 |
| `modal-serverless-gpu` | 云端 GPU | 按需部署/微调 |
| `dspy` | 自动 prompt 优化 | 零 GPU 方案 |
