# Frontend Review UI Changes

## 2026-06-10

- `/review` is now a review task list instead of a combined workbench.
- `/review/:id` is the dedicated manual review page.
- `/videos/:id/asr` displays full ASR text for reviewers and admins.
- Video detail pages group basic video information and audit evidence separately.
- Review submission continues to rely on JWT for reviewer identity. The frontend must not submit `reviewerId`.
- The local default MySQL password remains `1234`; teammates with a different password should set `DB_PASSWORD` locally.
