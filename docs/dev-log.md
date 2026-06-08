# VideoGuard 项目开发日志

> 用途：记录每日项目进展、验证结果、问题处理和下一步计划，可作为课程实训日报、周报和最终报告素材。

## 2026-06-08

### 今日目标

- 根据课程设计要求，搭建 VideoGuard 短视频内容审核管理系统的项目基础结构。
- 完成三端服务的最小可运行版本。
- 完成 AI 分析服务 MVP。
- 完成后端视频上传、视频查询和 MySQL 入库的第一版。
- 配置 GitHub 仓库，便于与队友协作开发。

### 完成工作

1. 项目初始化
   - 在 `D:\zaproject\Real_Projects\VideoGuard` 创建正式项目目录。
   - 建立 monorepo 结构：
     - `backend-springboot`
     - `ai-service-fastapi`
     - `frontend-vue`
     - `docs`
     - `sql`
     - `samples`
   - 编写 `README.md`，记录项目介绍、技术栈、目录结构和启动方式。
   - 编写 `.gitignore`，忽略 `uploads`、模型文件、依赖目录、构建产物和日志。
   - 编写 `AGENTS.md`，记录协作规则和模块边界。

2. Git 与 GitHub 协作配置
   - 初始化本地 Git 仓库。
   - 创建 `main` 和 `dev` 分支。
   - 配置 GitHub 远程仓库：
     - `git@github.com:wade13265071018-hue/VideoGuard.git`
   - 生成并配置 GitHub SSH key。
   - 成功推送 `main` 和 `dev` 分支。

3. 三端最小服务
   - SpringBoot 后端：
     - 新增 `/api/health`
     - 本地运行端口：`8080`
   - FastAPI AI 服务：
     - 新增 `/ai/health`
     - 本地运行端口：`8000`
   - Vue 前端：
     - 创建 Vue 3 + Vite + Element Plus 骨架。
     - 创建基础页面和路由：
       - `/dashboard`
       - `/upload`
       - `/videos`
       - `/review`
       - `/login`
     - 本地运行端口：`5173`

4. 数据库设计与初始化
   - 编写 `sql/schema.sql`，创建 7 张核心表：
     - `user`
     - `video`
     - `video_frame`
     - `sensitive_word`
     - `sensitive_hit`
     - `ai_review_result`
     - `review_log`
   - 编写 `sql/seed.sql`，插入测试用户和敏感词。
   - 确认本地 MySQL 配置：
     - 用户名：`root`
     - 密码：`1234`
   - 创建数据库 `video_guard`。
   - 成功导入表结构和测试数据。

5. FastAPI AI 分析 MVP
   - 实现 `/ai/metadata`：提取视频时长、分辨率、fps、文件大小。
   - 实现 `/ai/extract-frames`：按固定间隔抽取关键帧。
   - 实现 `/ai/text-detect`：标题、描述、ASR 文本敏感词检测。
   - 实现 `/ai/image-detect`：图像风险识别 MVP，支持规则模拟。
   - 实现 `/ai/analyze`：组合元数据、抽帧、文本检测、图像检测和风险评分。
   - 风险等级规则：
     - `PASS`：风险分 `< 30`
     - `SUSPICIOUS`：`30 <= 风险分 < 70`
     - `VIOLATION`：风险分 `>= 70`

6. SpringBoot 视频基础接口
   - 接入 Spring Data JPA 和 MySQL。
   - 新增 `Video` 实体和 `VideoRepository`。
   - 实现 `POST /api/videos/upload`：
     - 支持 `mp4`、`mov`、`avi`
     - 保存文件到项目级 `uploads/videos`
     - 使用 UUID 文件名避免重名
     - 写入 `video` 表
   - 实现 `GET /api/videos`：
     - 支持按 `status` 和 `aiRiskLevel` 筛选
   - 实现 `GET /api/videos/{id}`：
     - 返回视频详情
     - 预留 `frames`、`sensitiveHits`、`reviewLogs` 空数组
   - 实现 `/uploads/**` 静态资源映射，支持视频播放访问。
   - 实现 `GET /api/videos/{id}/play` 播放跳转接口。

7. 文档维护
   - 更新 `docs/api.md`，补充 SpringBoot 和 FastAPI 接口说明。
   - 更新 `docs/database.md`，说明核心表职责。
   - 创建 `docs/demo-script.md`，记录后续 5 分钟演示流程草稿。
   - 将 `docs/dev-log.md` 调整为每日汇报可用格式。

8. 开发环境整理
   - 打开 IDEA 项目。
   - 使用可见 PowerShell 窗口运行三端服务，便于观察日志。
   - 清理不需要的历史 PowerShell 窗口，仅保留必要服务窗口：
     - `VideoGuard SpringBoot :8080`
     - `VideoGuard FastAPI :8000`
     - `VideoGuard Vue :5173`

### 验证结果

- `mvn -DskipTests package`：通过。
- Vue 构建 `npm run build`：通过。
- FastAPI 依赖安装与导入检查：通过。
- MySQL 数据库：
  - `video_guard` 创建成功。
  - 7 张表创建成功。
  - 测试用户导入成功。
  - 敏感词导入成功。
- 服务健康检查：
  - `http://localhost:8080/api/health` 返回 `{"status":"ok"}`
  - `http://localhost:8000/ai/health` 返回 `{"status":"ok"}`
  - `http://localhost:5173` 返回页面内容。
- 视频上传验证：
  - 上传测试 MP4 成功。
  - `video` 表产生记录。
  - `/api/videos` 可查询列表。
  - `/api/videos/{id}` 可查询详情。
  - `/uploads/videos/{filename}.mp4` 静态访问返回 `200`。

### 遇到的问题与处理

1. 端口占用
   - 问题：`8080` 被 `ApplicationWebServer.exe` 占用。
   - 处理：经确认允许后释放 `8080`，将 SpringBoot 恢复到标准端口 `8080`。

2. GitHub HTTPS 无法连接
   - 问题：本机到 GitHub `443` 端口连接失败。
   - 处理：改用 SSH 推送，生成 SSH key 并添加到 GitHub，最终推送成功。

3. MySQL 密码不确定
   - 问题：初始尝试 `root/root` 和空密码失败。
   - 处理：确认本机 MySQL 密码为 `1234`。

4. seed 中文导入乱码
   - 问题：MySQL 客户端导入中文敏感词时字符集不正确。
   - 处理：在 `seed.sql` 中加入 `SET NAMES utf8mb4;`，并在 README 中记录导入时使用 `--default-character-set=utf8mb4`。

5. multipart 中文字段显示乱码
   - 问题：Windows 命令行测试 multipart 中文字段时，控制台显示乱码。
   - 处理：后端增加 multipart 文本编码兜底处理；同时通过数据库 HEX 验证，最终确认数据库实际存储为正确 UTF-8。

### Git 提交记录

- `de572b8 chore: initialize VideoGuard project`
- `0cc39f3 chore: lock frontend dependencies`
- `33d0abe feat: implement FastAPI analysis MVP`
- `40517b3 chore: merge remote initial history`
- `1b69e4d feat: add video upload APIs`
- `95033b9 fix: support local MySQL and multipart text encoding`

### 当前项目状态

- `main`：已推送到 GitHub，作为稳定基础版本。
- `dev`：已推送到 GitHub，包含最新开发进展。
- 当前主要功能完成度：
  - 项目骨架：完成
  - GitHub 协作：完成
  - FastAPI AI MVP：完成
  - SpringBoot 视频上传/列表/详情：完成
  - MySQL 接入：完成
  - 前端真实接口接入：未开始
  - SpringBoot 调用 FastAPI 并入库：未开始
  - 人工复审：未开始
  - 统计看板真实数据：未开始

### 下一步计划

1. 实现 `POST /api/videos/{id}/analyze`
   - SpringBoot 查询视频记录。
   - 查询启用的敏感词。
   - 调用 FastAPI `/ai/analyze`。
   - 保存元数据到 `video` 表。
   - 保存关键帧到 `video_frame` 表。
   - 保存敏感词命中到 `sensitive_hit` 表。
   - 保存 AI 分析结果到 `ai_review_result` 表。
   - 根据风险等级更新视频状态。

2. 前端上传页接入真实接口
   - 调用 `POST /api/videos/upload`
   - 上传成功后展示 `videoId`
   - 调用 `POST /api/videos/{id}/analyze`
   - 分析完成后跳转详情页

3. 视频详情页展示真实数据
   - 视频播放器
   - AI 风险等级和风险分
   - 关键帧
   - 敏感词命中
   - 审核日志预留区域

## 2026-06-08 追加进展

### 追加目标

- 打通 SpringBoot 调用 FastAPI 的 AI 分析链路。
- 将 AI 分析证据入库，形成“上传 -> AI 初审 -> 详情查看”的闭环。

### 计划工作

1. 新增 AI 结果相关实体和仓储：
   - `SensitiveWord`
   - `VideoFrame`
   - `SensitiveHit`
   - `AiReviewResult`
2. 实现 `POST /api/videos/{id}/analyze`：
   - 查询视频记录。
   - 查询启用的敏感词。
   - 调用 FastAPI `/ai/analyze`。
   - 保存关键帧、敏感词命中和 AI 审核结果。
   - 更新视频元数据、风险等级、风险分和状态。
3. 更新视频详情接口，让详情页可以拿到：
   - AI 评分结果
   - 关键帧证据
   - 敏感词命中证据

### 追加完成工作

- 新增 `SensitiveWord`、`VideoFrame`、`SensitiveHit`、`AiReviewResult` 实体。
- 新增对应 Repository，用于保存 AI 分析证据。
- 实现 `POST /api/videos/{id}/analyze` 后端接口。
- SpringBoot 能调用 FastAPI `/ai/analyze`。
- 分析完成后可写入：
  - `video_frame`
  - `sensitive_hit`
  - `ai_review_result`
- 分析完成后可更新 `video` 表的：
  - `duration`
  - `width`
  - `height`
  - `fps`
  - `ai_risk_level`
  - `ai_risk_score`
  - `status`
- 更新 `GET /api/videos/{id}`，返回 AI 结果、关键帧和敏感词命中。

### 追加验证结果

- `mvn -DskipTests package`：通过。
- SpringBoot 新版服务重启成功：`http://localhost:8080/api/health` 返回正常。
- FastAPI 服务可用：`http://localhost:8000/ai/health` 返回正常。
- 对测试视频 ID `5` 调用 `POST /api/videos/5/analyze` 成功。
- 分析后 `video` 表更新结果：
  - `status = AI_SUSPICIOUS`
  - `ai_risk_level = SUSPICIOUS`
  - `ai_risk_score = 40`
  - `duration = 2`
  - `width = 320`
  - `height = 180`
  - `fps = 10`
- `video_frame` 表写入 1 条关键帧记录。
- `sensitive_hit` 表写入 1 条敏感词命中记录。
- `ai_review_result` 表写入 1 条 AI 审核结果记录。
- 关键帧静态访问验证通过：
  - `/uploads/frames/5/frame_0000.jpg` 返回 `200`。
- GitHub 推送完成：
  - `55e6c28 feat: persist AI analysis results`

### 追加问题与处理

1. FastAPI 返回字段为 `snake_case`，Java DTO 初始未映射 `text_hits`
   - 现象：AI 风险分已计算，但视频详情中的敏感词命中为空。
   - 处理：为 `AiAnalyzeResponse` 补充 `@JsonProperty("text_hits")` 等字段映射。

2. 测试敏感词权重过低
   - 现象：命中 `测试违规` 后最终分数为 20，仍属于 `PASS`。
   - 处理：将 `测试违规` 权重调整为 40，便于课程演示中稳定进入 `SUSPICIOUS`。

### 当前新增能力

截至本次追加，项目已经具备：

```text
上传视频
-> 视频记录入库
-> 调用 AI 服务分析
-> 视频元数据入库
-> 关键帧证据入库
-> 敏感词命中入库
-> AI 审核结果入库
-> 视频状态更新为 AI_PASSED / AI_SUSPICIOUS / AI_VIOLATION
-> 详情接口返回完整 AI 初审证据
```

### 下一步计划调整

下一步优先进入前端真实接口接入：

1. `/upload` 页面接入 `POST /api/videos/upload`。
2. 上传成功后调用 `POST /api/videos/{id}/analyze`。
3. `/videos` 页面接入 `GET /api/videos`。
4. 视频详情页展示：
   - 视频播放器
   - AI 风险分
   - 关键帧
   - 敏感词命中
