# AI专项 — 工作产出汇总

## 项目背景
任务 "AI专项" 涵盖：模型部署、微调、Prompt 优化、推理调优。
环境：CPU only, 2GB RAM, DeepSeek API 接入。

## 产出文件

| 文件 | 内容 |
|------|------|
| `01-prompt-optimization.md` | Prompt 优化指南（中文版，含系统/用户 prompt 结构、Chain-of-Thought 引导、少样本示例、常见陷阱） |
| `02-inference-tuning.md` | 推理参数调优指南（temperature/top_p/frequency_penalty/presence_penalty 参数组合速查表、调试工作流、常见问题排查、成本优化） |
| `03-deployment-and-finetuning-assessment.md` | 环境评估报告（模型部署可行性分析、微调可行性分析、零 GPU 替代方案、推荐路线图、已安装 ML 技能清单） |
| `prompt_test.py` | Prompt 性能对比测试脚本（支持多参数组合对比、自动生成 JSON 报告） |

## 关键发现

### 当前环境限制
- 无可用的 GPU，RAM 仅 2GB → 本地部署/微调效果有限
- 仅 0.5B 级别 GGUF 小模型可本地运行，实际价值有限

### 推荐路径（零 GPU）
1. **短期：** Prompt 结构化优化 + 推理参数调优（本指南已覆盖）
2. **中期：** DSPy 自动化 Prompt 优化 + 云端 GPU（Modal/Colab）微调评估
3. **长期：** 在 Modal GPU Cloud 或 AutoDL 上进行正式模型微调和部署

### 可用工具
- `llama-cpp` — 本地小模型推理（0.5B 级）
- `huggingface-hub` — 模型搜索下载
- `dspy` — 零 GPU 自动 Prompt 优化
- `unsloth` + `modal-serverless-gpu` — 有 GPU 时的微调方案
- DeepSeek API — 当前主力推理后端
