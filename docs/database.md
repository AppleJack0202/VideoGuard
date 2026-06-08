# Database Notes

Database name:

```text
video_guard
```

Core tables:

- `user`: users and roles.
- `video`: uploaded videos and review status.
- `video_frame`: extracted key frames and image risk results.
- `sensitive_word`: configurable sensitive-word dictionary.
- `sensitive_hit`: sensitive-word hit evidence.
- `ai_review_result`: AI scoring result snapshot.
- `review_log`: append-only manual review log.

Status values:

```text
UPLOADED
PROCESSING
AI_PASSED
AI_SUSPICIOUS
AI_VIOLATION
REVIEWED
FAILED
```

Risk levels:

```text
PASS
SUSPICIOUS
VIOLATION
```

