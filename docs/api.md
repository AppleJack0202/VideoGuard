# API Design

Base URLs:

```text
SpringBoot: http://localhost:8080
FastAPI:    http://localhost:8000
```

Frontend calls SpringBoot only. SpringBoot calls FastAPI.

## Health

### GET /api/health

Response:

```json
{
  "status": "ok"
}
```

### GET /ai/health

Response:

```json
{
  "status": "ok"
}
```

## Auth

Planned:

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

Roles:

```text
USER
REVIEWER
ADMIN
```

## Videos

Planned:

```text
POST   /api/videos/upload
GET    /api/videos
GET    /api/videos/{id}
DELETE /api/videos/{id}
POST   /api/videos/{id}/analyze
```

### POST /api/videos/upload

Request: `multipart/form-data`

```text
file: video file, mp4/mov/avi
title: string
description: string
uploaderId: number
```

Response:

```json
{
  "videoId": 1,
  "title": "demo",
  "filePath": "uploads/videos/uuid.mp4",
  "status": "UPLOADED"
}
```

## Review

Planned:

```text
GET  /api/review/tasks
GET  /api/review/tasks/{videoId}
POST /api/review/tasks/{videoId}/submit
GET  /api/review/logs/{videoId}
```

Submit request:

```json
{
  "reviewerId": 2,
  "finalResult": "PASS",
  "comment": "Reviewed manually."
}
```

## Sensitive Words

Planned:

```text
GET    /api/sensitive-words
POST   /api/sensitive-words
PUT    /api/sensitive-words/{id}
DELETE /api/sensitive-words/{id}
```

## Statistics

Planned:

```text
GET /api/statistics/overview
GET /api/statistics/risk-distribution
GET /api/statistics/daily-upload
GET /api/statistics/category-distribution
```

## FastAPI AI Service

### POST /ai/analyze

Request:

```json
{
  "video_id": 1,
  "video_path": "D:/projects/video-guard/uploads/videos/demo.mp4",
  "title": "demo title",
  "description": "demo description",
  "frame_interval_sec": 5,
  "sensitive_words": [
    {
      "word": "测试违规",
      "category": "violence",
      "weight": 20
    }
  ]
}
```

Response:

```json
{
  "video_id": 1,
  "metadata": {
    "duration": 32.5,
    "width": 1280,
    "height": 720,
    "fps": 30.0,
    "file_size": 10485760
  },
  "frames": [],
  "asr_text": "",
  "text_hits": [],
  "scores": {
    "text_score": 0,
    "image_score": 5,
    "asr_score": 0,
    "final_score": 5
  },
  "risk_level": "PASS"
}
```

