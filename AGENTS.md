# AGENTS.md

## Project

VideoGuard is a course project for an AI-assisted short video content moderation management system.

## Architecture

- `frontend-vue`: Vue 3 + Element Plus
- `backend-springboot`: Spring Boot 3 + MySQL
- `ai-service-fastapi`: FastAPI + FFmpeg/OpenCV-based video analysis

Vue must call SpringBoot only. SpringBoot calls FastAPI. Do not let the frontend call FastAPI directly.

## Collaboration Rules

1. Keep `main` as the stable demo branch.
2. Use `dev` for daily integration.
3. Create one `feature/*` branch for each focused task.
4. Do not commit large media files, model files, `.env`, `uploads/`, or credentials.
5. If adding or changing an API, update `docs/api.md`.
6. If changing the database schema, update `sql/schema.sql`.
7. Keep changes scoped to the relevant module.
8. Prefer simple, readable code over over-engineered abstractions.

## Suggested Ownership

- AI service and AI-related backend integration: `ai-service-fastapi/`, backend analyze APIs.
- Frontend, review workflow pages, dashboard, and business CRUD: `frontend-vue/`, related backend APIs.

## Current MVP Priorities

1. Video upload
2. Video metadata extraction
3. Frame extraction
4. Text sensitive-word detection
5. Risk scoring
6. Review workflow
7. Dashboard

