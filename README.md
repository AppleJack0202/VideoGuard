# VideoGuard

VideoGuard is a course project for an AI-assisted short video content moderation management system.

The system supports video upload, video preprocessing, AI-assisted risk review, manual review, audit logs, and dashboard statistics.

## Architecture

```text
frontend-vue
  Vue 3 + Vite + Element Plus + ECharts

backend-springboot
  Spring Boot 3 + Maven + MySQL

ai-service-fastapi
  FastAPI + FFmpeg/OpenCV-based video analysis

database
  MySQL 8
```

Request flow:

```text
Vue -> SpringBoot -> FastAPI
```

The frontend must call SpringBoot only. SpringBoot owns business data, permissions, audit logs, and calls FastAPI for AI analysis.

## Directory Layout

```text
VideoGuard/
  backend-springboot/     Spring Boot business service
  ai-service-fastapi/     FastAPI AI analysis service
  frontend-vue/           Vue management frontend
  docs/                   API, database, dev log, demo script
  sql/                    MySQL schema and seed data
  samples/                Sample data notes
  uploads/                Local runtime uploads, ignored by Git
```

## Local Ports

```text
Vue:        http://localhost:5173
SpringBoot: http://localhost:8080
FastAPI:    http://localhost:8000
MySQL:      localhost:3306
```

## Quick Start

### 1. MySQL

Create database:

```sql
CREATE DATABASE video_guard DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Import:

```bash
mysql -u root -p video_guard < sql/schema.sql
mysql --default-character-set=utf8mb4 -u root -p video_guard < sql/seed.sql
```

SpringBoot reads database credentials from environment variables when present:

```text
DB_USERNAME=root
DB_PASSWORD=1234
```

If your local MySQL password is different, set `DB_PASSWORD` before starting the backend or update `backend-springboot/src/main/resources/application.yml` for local development.

### 2. FastAPI

```bash
cd ai-service-fastapi
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Health check:

```bash
curl http://localhost:8000/ai/health
```

### 3. SpringBoot

```bash
cd backend-springboot
mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/api/health
```

### 4. Vue

```bash
cd frontend-vue
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

## MVP Priorities

1. Video upload and playback
2. Video metadata extraction
3. Key frame extraction
4. Sensitive-word detection
5. Risk scoring and AI review result persistence
6. Manual review workflow
7. Statistics dashboard
8. Demo script and course report materials
