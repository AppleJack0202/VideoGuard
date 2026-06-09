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
已上传
预审中
复审中
待申诉
通过
驳回
```

Risk levels:

```text
正常
可疑
违规
```

User roles:

```text
一般用户
审核员
管理员
```
