---
name: china-vps-docker-build
description: Docker build optimization for Chinese VPS — pip/npm/apt mirror configuration in Dockerfiles to avoid timeouts from Great Firewall throttled package registries.
triggers:
  - "docker build 超时"
  - "pip install timeout"
  - "npm install timeout"
  - "国内 VPS Docker"
  - "docker build slow China"
  - "镜像源"
  - "npmmirror"
  - "pypi tuna"
category: devops
---

# China VPS Docker Build Optimization

When building Docker images from a Chinese VPS (Tencent Cloud, Alibaba Cloud, etc.), default package registries (npmjs.org, PyPI, deb.debian.org) are often throttled or unreachable. Builds time out on `pip install` or `npm install`.

## Quick Reference

| Package Manager | Default Source | China Mirror | Speedup |
|----------------|---------------|--------------|---------|
| **npm** | registry.npmjs.org | `https://registry.npmmirror.com` | 29s → 10s |
| **pip** | pypi.org | `https://pypi.tuna.tsinghua.edu.cn/simple` | timeout → 10s |
| **apt** | deb.debian.org | use system default (slower, unavoidable for base image pull) | N/A |

## Dockerfile Patterns

### npm (Node.js builds)

Configure mirror **before** `npm install`, not after:

```dockerfile
FROM node:22-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm config set registry https://registry.npmmirror.com && \
    npm install
COPY . .
RUN npm run build
```

Key details:
- `npm config set registry` writes to `~/.npmrc` so it persists for subsequent `npm install` calls in the same RUN layer.
- Set it *before* install, not after `COPY` — cache the install step separately.
- Works for both `npm install` and `npm ci`.

### pip (Python builds)

Use `pip config set global.index-url` to write to `pip.conf`:

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple && \
    pip install --no-cache-dir gunicorn && \
    pip install --no-cache-dir -r requirements.txt
COPY . .
```

Key details:
- `--no-cache-dir` is important on slim images to keep layer size small.
- Tsinghua (tuna) mirror is the most reliable for PyPI on Chinese VPS.
- Aliyun mirror (`https://mirrors.aliyun.com/pypi/simple/`) is an alternative.

### apt (Debian/Ubuntu system packages)

No special mirror needed for base images (they're pulled from Docker Hub, not directly from Debian). But **`apt-get update` inside the container is extremely slow from China** — Debian Trixie can take 12+ minutes without a mirror.

**Solution: replace sources at the start of the RUN layer:**

```dockerfile
# Debian Trixie (deb822 format in /etc/apt/sources.list.d/debian.sources)
RUN sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list.d/debian.sources 2>/dev/null; \
    sed -i 's|security.debian.org|mirrors.tuna.tsinghua.edu.cn/debian-security|g' /etc/apt/sources.list.d/debian.sources 2>/dev/null; \
    sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list 2>/dev/null; \
    sed -i 's|security.debian.org|mirrors.tuna.tsinghua.edu.cn/debian-security|g' /etc/apt/sources.list 2>/dev/null; \
    apt-get update && apt-get install -y --no-install-recommends \
    PACKAGES && \
    rm -rf /var/lib/apt/lists/*
```

Both `sources.list` (old format) and `debian.sources` (deb822 format) are tried with `2>/dev/null` to avoid errors.

**Speedup (实测):** Debian Trixie `apt-get update + install libxml2-dev libxslt1-dev`:
- Without mirror: **12+ minutes** (network timeout risk)
- With tsinghua mirror: **~6 seconds** (300x+ faster)

**Pitfall — deb822 format in Debian Trixie:**
Debian Trixie (python:3.11-slim as of 2025+) uses `/etc/apt/sources.list.d/debian.sources` (deb822 format) instead of `/etc/apt/sources.list`. Both files may coexist. Always sed-replace in both locations.

## Full Example: Flask + Vue Single-Container Build (Static Files)

When the Flask backend also serves the built frontend as static files, use a single Dockerfile with multi-stage build:

```dockerfile
# Stage 1: Build frontend (Vue)
FROM node:22-alpine AS builder
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm config set registry https://registry.npmmirror.com && \
    npm install
COPY frontend/ .
# vue.config.js should output to ../converter/static/ (Flask's static dir)
RUN npm run build

# Stage 2: Flask runtime
FROM python:3.11-slim
WORKDIR /app

# System deps with mirror
RUN sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list.d/debian.sources 2>/dev/null; \
    sed -i 's|security.debian.org|mirrors.tuna.tsinghua.edu.cn/debian-security|g' /etc/apt/sources.list.d/debian.sources 2>/dev/null; \
    apt-get update && apt-get install -y --no-install-recommends \
    libxml2-dev libxslt1-dev && \
    rm -rf /var/lib/apt/lists/*

# Python deps with mirror
COPY converter/requirements.txt .
RUN pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple && \
    pip install --no-cache-dir gunicorn && \
    pip install --no-cache-dir -r requirements.txt

# Copy backend code
COPY converter/ .

# Copy built frontend into Flask's static directory
COPY --from=builder /app/converter/static ./static

EXPOSE 80
CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:80", "app:app"]
```

docker-compose.yml:
```yaml
services:
  app:
    build: .
    container_name: mml-manager
    ports:
      - "80:80"
    volumes:
      - mml_data:/app/data
    restart: unless-stopped
volumes:
  mml_data:
```

Key design: Flask's `app.py` serves both `/api/*` routes AND the built frontend from `./static/` via `send_from_directory(STATIC_DIR, path)` and SPA fallback.

## Pitfalls

### ⚠️ Mirror config must come BEFORE the install command

```dockerfile
# ✅ Correct
RUN npm config set registry https://registry.npmmirror.com && npm install

# ❌ Wrong — npm install uses default registry
RUN npm install
RUN npm config set registry https://registry.npmmirror.com
```

The `pip config set` and `npm config set` commands write config files that are only read by subsequent commands in the same or later RUN layers.

### ⚠️ Don't use mirrors for apt inside Docker

Unlike pip/npm, Debian apt mirrors require host-specific configuration and have complex mirror selection logic. For Docker builds, the default Debian repos work (slowly) from China — 4 min for `apt-get update` is acceptable. If truly blocked, switch to a Debian-based image that uses `sources.list` from a CDN.

### ⚠️ Docker Hub image pulls are slow too

Pulling `node:22-alpine` and `python:3.11-slim` from Docker Hub from China can take 2-5 minutes each. This is unavoidable without Docker Hub mirror configuration on the daemon level (`/etc/docker/daemon.json` with `registry-mirrors`). Dockerfile-level mirror config only affects package installs inside the build, not the base image pull.
