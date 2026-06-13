# Docker Build Example: Flask + Vue Multi-Stage Build (Chinese VPS)

Complete Dockerfile with Chinese VPS optimizations for all three package managers (npm/pip/apt).

## Dockerfile

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
    sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list 2>/dev/null; \
    sed -i 's|security.debian.org|mirrors.tuna.tsinghua.edu.cn/debian-security|g' /etc/apt/sources.list 2>/dev/null; \
    apt-get update && apt-get install -y --no-install-recommends \
    libxml2-dev libxslt1-dev && \
    rm -rf /var/lib/apt/lists/*

# Python deps with mirror
COPY converter/requirements.txt .
RUN pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple && \
    pip install --no-cache-dir gunicorn && \
    pip install --no-cache-dir -r requirements.txt

COPY converter/ .
COPY --from=builder /app/converter/static ./static

EXPOSE 80
CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:80", "app:app"]
```

## docker-compose.yml

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

## Key design notes

- Flask's `app.py` serves both `/api/*` routes AND the built frontend from `./static/` via `send_from_directory(STATIC_DIR, path)` and SPA fallback
- `--no-cache-dir` on pip keeps slim image layers small
- Both `deb822` (`debian.sources`) and legacy (`sources.list`) formats are handled for Debian Trixie compatibility
