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

### POST /ai/metadata

Request:

```json
{
  "video_id": 1,
  "video_path": "D:/projects/video-guard/uploads/videos/demo.mp4"
}
```

Response:

```json
{
  "video_id": 1,
  "duration": 12.3,
  "width": 1280,
  "height": 720,
  "fps": 30.0,
  "file_size": 12345678
}
```

### POST /ai/extract-frames

Request:

```json
{
  "video_id": 1,
  "video_path": "D:/projects/video-guard/uploads/videos/demo.mp4",
  "frame_interval_sec": 5
}
```

Response:

```json
{
  "video_id": 1,
  "frames": [
    {
      "frame_path": "uploads/frames/1/frame_0000.jpg",
      "timestamp_sec": 0
    }
  ]
}
```

### POST /ai/text-detect

Request:

```json
{
  "video_id": 1,
  "title": "测试标题",
  "description": "测试描述",
  "asr_text": "",
  "sensitive_words": [
    {
      "word": "测试违规",
      "category": "violence",
      "weight": 20
    }
  ]
}
```

### POST /ai/image-detect

Request:

```json
{
  "video_id": 1,
  "frames": [
    {
      "frame_path": "uploads/frames/1/frame_0000.jpg",
      "timestamp_sec": 0
    }
  ]
}
```

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

Windows PowerShell note: when testing Chinese JSON manually, send UTF-8 bytes or use a Python client. Otherwise PowerShell may display response text as mojibake even when the API logic is correct.

Python test example:

```bash
python -c "import json, urllib.request; body={'video_id':1,'video_path':'D:/zaproject/Real_Projects/VideoGuard/uploads/videos/text_risk.mp4','title':'这个标题包含测试违规词','description':'课程演示视频','frame_interval_sec':1,'sensitive_words':[{'word':'测试违规','category':'violence','weight':40}]}; data=json.dumps(body, ensure_ascii=False).encode('utf-8'); req=urllib.request.Request('http://localhost:8000/ai/analyze', data=data, headers={'Content-Type':'application/json; charset=utf-8'}); print(urllib.request.urlopen(req).read().decode('utf-8'))"
```
