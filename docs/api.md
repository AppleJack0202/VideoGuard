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
  "fileUrl": "/uploads/videos/uuid.mp4",
  "status": "UPLOADED"
}
```

Example:

```bash
curl -X POST http://localhost:8080/api/videos/upload \
  -F "file=@D:/demo/normal.mp4" \
  -F "title=正常视频" \
  -F "description=课程演示视频" \
  -F "uploaderId=3"
```

### GET /api/videos

Query parameters:

```text
status: optional
aiRiskLevel: optional
```

Response:

```json
[
  {
    "id": 1,
    "title": "正常视频",
    "uploaderId": 3,
    "createdAt": "2026-06-08T20:00:00",
    "status": "UPLOADED",
    "aiRiskLevel": null,
    "aiRiskScore": 0.0,
    "fileSize": 123456,
    "duration": null
  }
]
```

### GET /api/videos/{id}

Response:

```json
{
  "id": 1,
  "uploaderId": 3,
  "title": "正常视频",
  "description": "课程演示视频",
  "originalFilename": "normal.mp4",
  "storedFilename": "uuid.mp4",
  "filePath": "uploads/videos/uuid.mp4",
  "fileUrl": "/uploads/videos/uuid.mp4",
  "fileSize": 123456,
  "duration": null,
  "width": null,
  "height": null,
  "fps": null,
  "status": "UPLOADED",
  "aiRiskLevel": null,
  "aiRiskScore": 0.0,
  "finalResult": null,
  "finalComment": null,
  "frames": [],
  "sensitiveHits": [],
  "reviewLogs": []
}
```

### GET /api/videos/{id}/play

Redirects to the uploaded static file URL, for example:

```text
/uploads/videos/uuid.mp4
```

### POST /api/videos/{id}/analyze

Calls FastAPI `/ai/analyze`, persists the AI evidence, and updates the video status.

Processing rules:

```text
PASS       -> AI_PASSED
SUSPICIOUS -> AI_SUSPICIOUS
VIOLATION  -> AI_VIOLATION
```

Response: same shape as `GET /api/videos/{id}`, with populated `aiResult`, `frames`, and `sensitiveHits`.

Example:

```bash
curl -X POST http://localhost:8080/api/videos/1/analyze
```

Response excerpt:

```json
{
  "id": 1,
  "status": "AI_SUSPICIOUS",
  "aiRiskLevel": "SUSPICIOUS",
  "aiRiskScore": 40.0,
  "aiResult": {
    "textScore": 40.0,
    "imageScore": 5.0,
    "asrScore": 0.0,
    "finalScore": 40.0,
    "riskLevel": "SUSPICIOUS",
    "asrText": ""
  },
  "frames": [
    {
      "framePath": "uploads/frames/1/frame_0000.jpg",
      "frameUrl": "/uploads/frames/1/frame_0000.jpg",
      "timestampSec": 0.0,
      "label": "normal",
      "confidence": 0.9,
      "riskScore": 5.0
    }
  ],
  "sensitiveHits": [
    {
      "sourceType": "TITLE",
      "word": "测试违规",
      "category": "violence",
      "weight": 20,
      "contextText": "测试违规演示视频"
    }
  ]
}
```

## Review

Implemented:

```text
GET  /api/review/tasks
GET  /api/review/tasks/{videoId}
POST /api/review/tasks/{videoId}/submit
GET  /api/review/logs/{videoId}
```

### GET /api/review/tasks

Returns videos that need manual review. By default, this endpoint returns videos with status `AI_SUSPICIOUS` or `AI_VIOLATION` and no final manual result.

Query parameters:

```text
status: optional, for example AI_SUSPICIOUS
aiRiskLevel: optional, for example SUSPICIOUS
```

### GET /api/review/tasks/{videoId}

Returns the same shape as `GET /api/videos/{id}`, including AI evidence and existing review logs.

### POST /api/review/tasks/{videoId}/submit

Submit request:

```json
{
  "reviewerId": 2,
  "finalResult": "REJECT",
  "comment": "Reviewed manually."
}
```

Rules:

```text
finalResult = PASS   -> video.status = MANUAL_PASSED
finalResult = REJECT -> video.status = MANUAL_REJECTED
```

The endpoint updates `video.final_result`, updates `video.final_comment`, and appends one row to `review_log`.

### GET /api/review/logs/{videoId}

Returns manual review logs ordered by newest first.

Response:

```json
[
  {
    "id": 1,
    "videoId": 8,
    "reviewerId": 2,
    "beforeStatus": "AI_PASSED",
    "afterStatus": "MANUAL_REJECTED",
    "beforeResult": null,
    "afterResult": "REJECT",
    "comment": "Reviewed manually.",
    "createdAt": "2026-06-08T23:08:00"
  }
]
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

Implemented:

```text
GET /api/statistics/overview
GET /api/statistics/risk-distribution
GET /api/statistics/daily-upload
GET /api/statistics/status-distribution
GET /api/statistics/category-distribution
```

### GET /api/statistics/overview

Response:

```json
{
  "totalVideos": 5,
  "todayUploads": 5,
  "pendingReviews": 1,
  "manualReviewed": 1,
  "aiPassed": 3,
  "aiPassRate": 60.0
}
```

### GET /api/statistics/risk-distribution

Response:

```json
[
  { "name": "PASS", "count": 4 },
  { "name": "SUSPICIOUS", "count": 1 }
]
```

### GET /api/statistics/status-distribution

Response:

```json
[
  { "name": "AI_PASSED", "count": 3 },
  { "name": "AI_SUSPICIOUS", "count": 1 },
  { "name": "MANUAL_REJECTED", "count": 1 }
]
```

### GET /api/statistics/daily-upload

Query parameters:

```text
days: optional, default 7, max 30
```

Response:

```json
[
  { "name": "2026-06-08", "count": 5 }
]
```

### GET /api/statistics/category-distribution

Response:

```json
[
  { "name": "violence", "count": 1 }
]
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
