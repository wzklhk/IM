---
title: 本地 LLM 部署方案对比研究报告
created: 2026-05-10
tags: [research, llm, inference, deployment]
status: draft
---

# 本地 LLM 部署方案对比

## 概述

对主流本地大模型部署方案进行技术选型对比，评估吞吐、延迟、显存占用等关键指标。

## 候选方案

| 方案 | 后端 | 特点 | 适用场景 |
|------|------|------|----------|
| **vLLM** | CUDA | 高吞吐，PagedAttention，连续批处理 | 生产级推理服务 |
| **llama.cpp** | CPU/GPU | 跨平台，GGUF 量化，单文件部署 | 资源受限环境 |
| **SGLang** | CUDA | 结构化输出，RadixAttention，低延迟 | 需要结构化 LLM 输出的场景 |
| **Ollama** | llama.cpp | 一键安装，模型管理，REST API | 个人开发 / 快速原型 |

## 性能对比

| 指标 | vLLM | llama.cpp | SGLang | Ollama |
|------|------|-----------|--------|--------|
| 吞吐 (tok/s) | 高 | 中 | 中-高 | 中 |
| 首 token 延迟 | 中 | 低 | 低 | 低 |
| 显存效率 | 优 (PagedAttention) | 一般 | 优 (RadixAttention) | 一般 |
| 结构化输出 | ❌ (需 Guidance) | ❌ | ✅ 原生 | ❌ |
| 多 GPU 支持 | ✅ 成熟 | ⚠️ 有限 | ✅ | ⚠️ 有限 |
| 易用性 | 中 | 中 | 中 | **高** |

## 结论

对开发和快速原型 → **Ollama**
对生产级推理服务 → **vLLM**
对需要结构化/约束输出的场景 → **SGLang**
对跨平台/边缘部署 → **llama.cpp**

## 相关资源

- [[Areas/Research/LLM 评估]]
- Ollama: https://ollama.com
- vLLM: https://github.com/vllm-project/vllm
- SGLang: https://github.com/sgl-project/sglang
- llama.cpp: https://github.com/ggerganov/llama.cpp
