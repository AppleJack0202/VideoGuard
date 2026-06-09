# VideoGuard 项目开发日志

> 用途：记录每日项目进展、验证结果、问题处理和下一步计划，可作为课程实训日报、周报和最终报告素材。

## 2026-06-08

### 今日目标

- 根据课程设计要求，搭建 VideoGuard 短视频内容审核管理系统的项目基础结构。
- 完成三端服务的最小可运行版本。
- 完成 AI 分析服务 MVP。
- 完成后端视频上传、视频查询和 MySQL 入库的第一版。
- 配置 GitHub 仓库，方便与队友协作开发。
- 打通 SpringBoot 调用 FastAPI 的 AI 分析链路。
- 完成前端真实接口接入，让上传、列表、详情、AI 分析流程可交互验证。

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
   - 编写 `README.md`，记录项目介绍、技术栈、目录结构、启动方式和文档索引。
   - 编写 `.gitignore`，忽略 `uploads`、模型文件、依赖目录、构建产物和日志。
   - 编写 `AGENTS.md`，记录协作规则和模块边界。

2. Git 与 GitHub 协作配置
   - 初始化本地 Git 仓库。
   - 创建 `main` 和 `dev` 分支。
   - 配置 GitHub 远程仓库：`git@github.com:wade13265071018-hue/VideoGuard.git`。
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
     - 返回 AI 结果、关键帧、敏感词命中、审核日志预留字段
   - 实现 `/uploads/**` 静态资源映射，支持视频播放和关键帧访问。
   - 实现 `GET /api/videos/{id}/play` 播放跳转接口。

7. SpringBoot 调用 FastAPI 并持久化 AI 结果
   - 新增实体：
     - `SensitiveWord`
     - `VideoFrame`
     - `SensitiveHit`
     - `AiReviewResult`
   - 新增对应 Repository。
   - 实现 `POST /api/videos/{id}/analyze`：
     - 查询视频记录
     - 查询启用的敏感词
     - 调用 FastAPI `/ai/analyze`
     - 保存视频元数据到 `video`
     - 保存关键帧到 `video_frame`
     - 保存敏感词命中到 `sensitive_hit`
     - 保存 AI 分析结果到 `ai_review_result`
     - 根据风险等级更新视频状态
   - 更新详情接口，使其返回完整 AI 初审证据。

8. 前端真实接口接入
   - 后端 `WebConfig` 增加 `/api/**` 跨域配置，允许 `http://localhost:5173` 调用 SpringBoot 接口。
   - 新增 `frontend-vue/src/api/client.js`，统一封装：
     - `uploadVideo`
     - `analyzeVideo`
     - `fetchVideos`
     - `fetchVideoDetail`
     - `toAssetUrl`
   - 更新前端路由，新增 `/videos/:id` 视频详情页。
   - 更新上传页面：
     - 支持选择视频文件
     - 支持填写标题、描述、上传人 ID
     - 上传成功后可直接触发 AI 分析
     - 分析完成后跳转视频详情页
   - 更新视频管理页面：
     - 接入真实视频列表接口
     - 支持按处理状态和风险等级筛选
     - 支持进入详情和重新分析
   - 新增视频详情页面：
     - 展示视频播放器
     - 展示处理状态、风险等级、风险分、视频时长
     - 展示 AI 分析结果
     - 展示敏感词命中
     - 展示视频抽帧

9. 文档维护
   - 更新 `docs/api.md`，补充 SpringBoot 和 FastAPI 接口说明。
   - 更新 `docs/database.md`，说明核心表职责。
   - 创建 `docs/demo-script.md`，记录后续 5 分钟演示流程草稿。
   - 创建 `docs/collaboration-guide.md`，说明队友如何使用 Git/GitHub 一起编辑项目。
   - 创建 `docs/project-progress.md`，说明当前项目进展、里程碑和待办任务。
   - 创建 `docs/acceptance-test-guide.md`，说明如何启动项目并进行页面/接口验收。
   - 重写 `docs/dev-log.md`，修复历史乱码，使其可直接作为汇报材料。

10. 开发环境整理
   - 打开 IDEA 项目。
   - 使用可见 PowerShell 窗口运行三端服务，便于观察日志。
   - 按用户要求清理不必要的 PowerShell 窗口，仅保留必要服务窗口：
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
  - `http://localhost:5173/videos/5` 返回 `200`
- 视频上传验证：
  - 上传测试 MP4 成功。
  - `video` 表产生记录。
  - `/api/videos` 可查询列表。
  - `/api/videos/{id}` 可查询详情。
  - `/uploads/videos/{filename}.mp4` 静态访问返回 `200`。
- AI 分析验证：
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
  - 关键帧静态访问验证通过：`/uploads/frames/5/frame_0000.jpg` 返回 `200`。
- 前端联调验证：
  - `GET /api/videos?aiRiskLevel=SUSPICIOUS` 可返回测试视频。
  - `GET /api/videos/5` 可返回 AI 分析结果、关键帧和敏感词命中。
  - CORS 检查通过：`Origin=http://localhost:5173` 时，后端返回 `Access-Control-Allow-Origin=http://localhost:5173`。
  - 冒烟测试上传视频 ID `7`：
    - 上传状态：`UPLOADED`
    - 分析后状态：`AI_PASSED`
    - 风险等级：`PASS`
    - 风险分：`5`
    - 关键帧数量：`1`
    - 敏感词命中数量：`0`

### 遇到的问题与处理

1. 端口占用
   - 问题：`8080` 被其他本地应用占用。
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

5. Windows PowerShell 中文显示乱码
   - 问题：PowerShell 测试 multipart 中文字段、JSON 响应时，控制台显示可能为乱码。
   - 处理：使用数据库 HEX、浏览器页面和 UTF-8 读取方式确认实际数据正确；在验收指南中提示优先用浏览器、Postman/Apifox 或 Python 测试中文。

6. 前端上传返回字段不一致
   - 问题：上传接口返回字段为 `videoId`，前端初始写法误用 `id`。
   - 处理：修正上传页逻辑，使用 `videoId` 触发 AI 分析和跳转详情页。

7. 前端筛选枚举与后端状态不一致
   - 问题：前端初始使用 `AI_PASS`、`AI_REJECTED`，后端实际状态为 `AI_PASSED`、`AI_VIOLATION`。
   - 处理：修正视频管理页筛选值。

### Git 提交记录

已完成并推送的历史提交：

- `de572b8 chore: initialize VideoGuard project`
- `0cc39f3 chore: lock frontend dependencies`
- `33d0abe feat: implement FastAPI analysis MVP`
- `40517b3 chore: merge remote initial history`
- `1b69e4d feat: add video upload APIs`
- `95033b9 fix: support local MySQL and multipart text encoding`
- `55e6c28 feat: persist AI analysis results`
- `3a4bc81 docs: record AI analysis progress`

本次前端联调和文档拆分待提交。

### 当前项目状态

- `main`：已推送到 GitHub，作为稳定基础版本。
- `dev`：已推送到 GitHub，包含最新开发进展；本次前端联调和文档拆分即将提交。
- 当前主要功能完成度：
  - 项目骨架：完成
  - GitHub 协作：完成
  - FastAPI AI MVP：完成
  - SpringBoot 视频上传/列表/详情：完成
  - SpringBoot 调用 FastAPI 并入库：完成
  - MySQL 接入：完成
  - 前端真实接口接入：完成
  - 人工复审：未开始
  - 统计看板真实数据：未开始
  - 登录与角色：未开始

### 下一步计划

1. 实现人工复审模块
   - 查询待复审视频。
   - 审核员提交最终结论。
   - 写入 `review_log`。
   - 更新视频最终审核状态。

2. 实现统计看板真实数据
   - 总视频数。
   - 待复审数量。
   - 风险等级分布。
   - 每日上传趋势。
   - 违规类别分布。

3. 实现登录与角色
   - 简化登录。
   - 区分普通用户、审核员、管理员。

4. 整理演示材料
   - 准备正常视频和风险视频。
   - 完善 5 分钟演示脚本。
   - 整理课程报告中的系统架构、数据库设计和核心代码说明。

## 2026-06-08 人工复审模块第一版完成

### 本次目标

- 实现人工复审后端接口。
- 实现人工复审前端工作台。
- 将人工复审结果写入 `video` 表和 `review_log` 表。
- 更新项目进展、API 文档和交互验收指南。

### 完成工作

- 新增 `ReviewLog` 实体，对应数据库 `review_log` 表。
- 新增 `ReviewLogRepository`。
- 新增复审 DTO：
  - `ReviewSubmitRequest`
  - `ReviewLogResponse`
- 新增 `ReviewService`：
  - 查询待复审任务。
  - 查询复审详情。
  - 提交人工复审结论。
  - 查询复审日志。
- 新增 `ReviewController`：
  - `GET /api/review/tasks`
  - `GET /api/review/tasks/{videoId}`
  - `POST /api/review/tasks/{videoId}/submit`
  - `GET /api/review/logs/{videoId}`
- 更新 `VideoService.detail`，让视频详情返回复审日志。
- 更新 `VideoDetailResponse`，将 `reviewLogs` 调整为结构化复审日志列表。
- 更新前端 API 客户端，新增复审相关方法：
  - `fetchReviewTasks`
  - `fetchReviewTask`
  - `submitReview`
  - `fetchReviewLogs`
- 重写 `ReviewView.vue`：
  - 左侧展示待复审任务列表。
  - 右侧展示视频播放器、AI 风险信息、敏感词命中。
  - 支持填写审核员 ID、复审结论和审核意见。
  - 支持提交复审并刷新复审日志。
- 更新 `VideoDetailView.vue`，展示复审日志。
- 更新 `docs/api.md`、`docs/project-progress.md`、`docs/acceptance-test-guide.md`。

### 验证结果

- `mvn -DskipTests package`：通过。
- `npm run build`：通过。
- SpringBoot 重启成功，`http://localhost:8080/api/health` 返回正常。
- Vue 页面 `http://localhost:5173/review` 返回 `200`。
- `GET /api/review/tasks` 可返回待复审任务，当前保留视频 ID `5` 作为可疑样例。
- 复审冒烟测试视频 ID `8`：
  - 提交人工复审成功。
  - 提交后状态：`MANUAL_REJECTED`
  - 最终结论：`REJECT`
  - `review_log` 可查询到复审记录。

### 当前项目状态

- 人工复审第一版已完成。
- 现在系统已具备课程演示中的核心闭环：

```text
视频上传 -> AI 分析 -> 证据入库 -> 人工复审 -> 复审日志入库 -> 视频最终结论更新
```

### 下一步计划

- 开始实现统计看板真实数据：
  - 总视频数。
  - 待复审数量。
  - 风险等级分布。
  - 每日上传趋势。
  - 敏感词类别分布。

## 2026-06-08 统计看板真实数据第一版完成

### 本次目标

- 实现统计看板后端接口。
- 将 Vue 统计看板从静态占位改为真实数据库数据。
- 使用图表展示视频审核进展和风险分布。

### 完成工作

- 新增统计 DTO：
  - `CountItemResponse`
  - `StatisticsOverviewResponse`
- 新增 `StatisticsService`。
- 新增 `StatisticsController`。
- 在 `VideoRepository` 中新增聚合查询：
  - 今日上传数量。
  - 待复审数量。
  - 已人工复审数量。
  - AI 通过数量。
  - 风险等级分布。
  - 处理状态分布。
  - 近 7 日上传趋势。
- 在 `SensitiveHitRepository` 中新增敏感类别分布统计。
- 前端 API 客户端新增统计接口方法。
- 重写 `DashboardView.vue`：
  - 顶部指标展示总视频数、今日上传、AI 通过率、待复审、已人工复审。
  - 使用 ECharts 展示风险等级分布。
  - 使用 ECharts 展示近 7 日上传趋势。
  - 使用 ECharts 展示处理状态分布。
  - 使用 ECharts 展示敏感类别分布。
- 更新 `docs/api.md`、`docs/project-progress.md`、`docs/acceptance-test-guide.md` 和本开发日志。

### 验证结果

- `mvn -DskipTests package`：通过。
- `npm run build`：通过。
- 修复一次 JPQL 日期聚合查询问题：`date_format` 返回类型需显式 `cast(... as string)`，否则 SpringBoot 启动时 Repository 查询校验失败。
- SpringBoot 重启成功，`http://localhost:8080/api/health` 返回正常。
- Vue 页面 `http://localhost:5173/dashboard` 返回 `200`。
- 统计接口验证结果：
  - `GET /api/statistics/overview` 返回：
    - `totalVideos = 5`
    - `todayUploads = 5`
    - `pendingReviews = 1`
    - `manualReviewed = 1`
    - `aiPassRate = 60.0`
  - `GET /api/statistics/risk-distribution` 返回 `PASS`、`SUSPICIOUS`。
  - `GET /api/statistics/status-distribution` 返回 `AI_PASSED`、`AI_SUSPICIOUS`、`MANUAL_REJECTED`。
  - `GET /api/statistics/daily-upload?days=7` 返回 `2026-06-08` 当日上传数量。
  - `GET /api/statistics/category-distribution` 返回 `violence` 类别命中数量。

### 当前项目状态

- 统计看板真实数据第一版已完成。
- 系统已具备课程演示需要的主要业务闭环：

```text
视频上传 -> AI 分析 -> 证据入库 -> 人工复审 -> 复审日志入库 -> 统计看板汇总展示
```

### 下一步计划

- 实现简化登录与角色展示。
- 实现敏感词管理第一版。
- 整理最终演示脚本和课程报告材料。

## 2026-06-08 敏感词管理第一版完成

### 本次目标

- 实现敏感词管理后端接口。
- 实现前端“敏感词管理”页面。
- 让 AI 分析使用的敏感词库可以通过页面维护。

### 完成工作

- 新增敏感词 DTO：
  - `SensitiveWordRequest`
  - `SensitiveWordResponse`
- 新增 `SensitiveWordService`。
- 新增 `SensitiveWordController`。
- 扩展 `SensitiveWordRepository`，支持动态筛选。
- 后端新增接口：
  - `GET /api/sensitive-words`
  - `POST /api/sensitive-words`
  - `PUT /api/sensitive-words/{id}`
  - `DELETE /api/sensitive-words/{id}`
- 前端 API 客户端新增敏感词管理方法。
- 新增 `SensitiveWordsView.vue`：
  - 支持按类别和启用状态筛选。
  - 支持新增敏感词。
  - 支持编辑敏感词。
  - 支持启用/停用敏感词。
  - 支持删除敏感词。
- 更新侧边栏和路由，新增“敏感词管理”入口。
- 更新 `docs/api.md`、`docs/project-progress.md`、`docs/acceptance-test-guide.md` 和本开发日志。

### 验证结果

- `mvn -DskipTests package`：通过。
- `npm run build`：通过。
- SpringBoot 重启成功，`http://localhost:8080/api/health` 返回正常。
- Vue 页面 `http://localhost:5173/sensitive-words` 返回 `200`。
- 敏感词接口冒烟测试通过：
  - 初始敏感词数量：`6`
  - 新增测试词成功，生成 ID `7`
  - 编辑测试词成功，权重改为 `44`
  - 停用测试词成功，`enabled = 0`
  - 按 `category=custom&enabled=0` 筛选可查到测试词
  - 删除测试词成功，删除后筛选结果为 `0`

### 当前项目状态

- 敏感词管理第一版已完成。
- 现在系统具备：

```text
敏感词配置 -> 视频上传 -> AI 分析 -> 证据入库 -> 人工复审 -> 统计看板
```

### 下一步计划

- 实现简化登录与角色展示。
- 整理最终演示脚本和课程报告材料。

## 2026-06-09 简化登录与角色展示第一版完成

### 本次目标

- 完成课程演示所需的简化登录功能。
- 在前端显示当前登录用户和角色。
- 确认三端服务端口和运行状态，处理本机 `8080` 端口占用问题。
- 同步更新 API 文档、项目进展规划和交互验收指南。

### 完成工作

- 后端新增用户登录相关代码：
  - `User` 实体，对应数据库 `user` 表。
  - `UserRepository`，支持按用户名查询。
  - `LoginRequest`、`UserResponse` 两个 DTO。
  - `AuthService`，支持演示阶段的简化登录逻辑。
  - `AuthController`，提供 `POST /api/auth/login` 和 `GET /api/auth/me`。
- 前端新增和完善登录交互：
  - 登录页支持选择 `admin`、`reviewer`、`user` 三类演示账号。
  - 登录成功后将用户信息保存到 `localStorage`。
  - 顶部栏展示当前用户名和角色。
  - 支持退出登录并返回登录页。
- 端口调整：
  - 本机 `8080` 被 NI Application Web Server 占用，普通权限无法停止。
  - SpringBoot 已统一切换到 `8081`。
  - 前端 API 基础地址同步改为 `http://localhost:8081`。
- 文档维护：
  - `docs/api.md` 补充登录接口说明。
  - `docs/project-progress.md` 更新当前进展和下一步任务。
  - `docs/acceptance-test-guide.md` 补充登录验收流程和接口命令。

### 验证结果

- `mvn -DskipTests package`：通过。
- `npm run build`：通过。
- `http://localhost:8081/api/health` 返回 `{"status":"ok"}`。
- `POST /api/auth/login` 使用 `reviewer / 123456` 登录成功，返回角色 `REVIEWER`。
- `GET /api/auth/me?userId=2` 返回审核员用户信息。
- `http://localhost:5173/login` 返回 `200`。
- `GET /api/statistics/overview` 可正常返回统计数据。

### 遇到的问题与处理

1. `8080` 端口被占用
   - 问题：`8080` 被 `NI Application Web Server` 占用。
   - 处理：尝试停止服务时权限不足，因此将 SpringBoot 改为 `8081`，并同步更新前端和文档中的接口地址。

2. PowerShell 中文显示乱码
   - 问题：PowerShell 读取 UTF-8 中文文件时显示为乱码。
   - 处理：使用 Node 按 UTF-8 读取文件确认内容正常，避免误判文件损坏。

### 当前项目状态

- 登录与角色展示第一版已完成。
- 当前可演示主链路：

```text
登录选择角色 -> 敏感词配置 -> 视频上传 -> AI 分析 -> 人工复审 -> 统计看板
```

### 下一步计划

1. 整理最终演示脚本。
2. 准备课程报告材料：系统架构、数据库设计、核心代码说明、测试结果。
3. 视时间补充更严格的角色权限控制。

## 2026-06-09 项目完整验收测试

### 本次目标

- 查清 `8080` 端口占用来源。
- 对当前 MVP 做一次完整业务闭环测试。
- 判断项目功能是否已经达到课程演示要求。

### 端口排查结果

- 当前 `8080` 端口未被监听，已经空闲。
- 之前占用 `8080` 的服务是 `NIApplicationWebServer`，显示名为 `NI Application Web Server`，属于 NI/Multisim 相关软件。
- 当前服务状态：
  - `NIApplicationWebServer`：Stopped，StartType 为 Automatic。
  - `NIApplicationWebServer64`：Stopped，StartType 为 Disabled。
  - `NISystemWebServer`：Running，但未占用 `8080`。
- 尝试停止和禁用 `NIApplicationWebServer` 时被系统拒绝，原因是当前 PowerShell 没有管理员权限。

### 完整测试结果

- 健康检查通过：
  - SpringBoot：`http://localhost:8081/api/health`
  - FastAPI：`http://localhost:8000/ai/health`
  - Vue：`http://localhost:5173/login`
- 登录测试通过：
  - `reviewer / 123456` 登录成功，返回角色 `REVIEWER`。
- 敏感词管理测试通过：
  - 新增临时敏感词 `codex_risk_20260609` 成功。
  - 编辑权重成功。
  - 删除临时敏感词成功。
- 视频上传测试通过：
  - 上传本地测试视频成功，生成 `videoId=10`。
- AI 分析测试通过：
  - 视频 `10` 分析后状态为 `AI_SUSPICIOUS`。
  - 风险等级为 `SUSPICIOUS`。
  - 风险分为 `50.0`。
  - 抽取关键帧 `1` 张。
  - 敏感词命中 `1` 条。
- 人工复审测试通过：
  - 视频 `10` 提交人工复审成功。
  - 最终状态为 `MANUAL_REJECTED`。
  - 复审日志数量为 `1`。
- 统计接口测试通过：
  - `GET /api/statistics/overview` 正常返回总视频数、今日上传、待复审、人工复审和 AI 通过率。
- 前端页面测试通过：
  - `/login`、`/dashboard`、`/upload`、`/videos`、`/review`、`/sensitive-words` 均返回 `200`。
- 构建验证通过：
  - `mvn -DskipTests package`：通过。
  - `npm run build`：通过。

### 当前结论

- 当前 MVP 功能已经达到课程演示要求。
- 已完成的核心链路为：

```text
登录 -> 敏感词管理 -> 视频上传 -> AI 分析 -> 人工复审 -> 统计看板
```

- 剩余工作主要是课程材料整理，不是核心功能开发：
  - 最终演示脚本。
  - 课程报告材料。
  - 架构图、数据库设计图、核心代码说明。

## 2026-06-09 按改进需求规格完成权限与审核流程改造

### 本次目标

- 按《改进需求规格》改造状态字段、角色权限、审核流程和前端角色视图。
- 将上传后的 AI 预审从手动触发改为后台自动执行。
- 将用户角色和审核状态统一为中文业务口径。

### 完成工作

- 数据库与实体：
  - `video` 表新增 `violation_category` 字段。
  - `status` 改为 `已上传`、`预审中`、`复审中`、`待申诉`、`通过`、`驳回`。
  - `ai_risk_level` 改为 `正常`、`可疑`、`违规`。
  - `user.role` 改为 `一般用户`、`审核员`、`管理员`。
- 权限与认证：
  - 新增注册接口，注册用户默认角色为 `一般用户`。
  - 登录成功后返回 JWT Token。
  - 新增后端拦截器，按 Token 中的角色限制接口访问。
  - 管理员可在用户管理页修改用户角色。
- 审核流程：
  - 上传接口不再接收前端传入的 `uploaderId`，改为从 Token 中读取。
  - 新增后台定时预审任务，自动分析状态为 `已上传` 的视频。
  - AI 返回 `正常` 时视频进入 `通过`；AI 返回 `可疑/违规` 时视频进入 `复审中`。
  - 复审提交支持 `通过`、`驳回`、`待申诉`，并要求填写复审意见。
  - 复审提交后不可再次修改。
- 前端视图：
  - 一般用户仅显示“视频上传”“我的上传”。
  - 审核员仅显示“人工复审”。
  - 管理员显示“统计看板”“视频管理”“敏感词管理”“用户管理”。
  - 一般用户详情页隐藏 AI 风险分、敏感词命中、关键帧和复审日志。
  - 审核员详情页显示元数据、播放器、关键帧、敏感词命中、AI 分数和复审日志。

### 验证结果

- `mvn -DskipTests package`：通过。
- `npm run build`：通过。
- 登录返回中文角色：
  - `admin -> 管理员`
  - `reviewer -> 审核员`
  - `user -> 一般用户`
- 普通用户访问统计接口返回 `403`，权限拦截生效。
- 普通用户上传视频时不传 `uploaderId`，上传成功后状态为 `已上传`。
- 后台自动预审生效，测试视频 `videoId=11` 自动从 `已上传` 变为 `复审中`。
- 审核员可查看 `videoId=11` 的 AI 风险等级、风险分、违规类别、敏感词命中和关键帧。
- 审核员提交 `待申诉` 成功，再次提交返回 `400`，满足“提交后不可修改”。

### 问题处理

- 通过 PowerShell 管道执行中文 SQL 时，数据库中部分中文被写成 `???`。
- 已改用 UTF-8 十六进制 SQL 修复本机数据库中的角色、状态和类别数据。

### 当前状态

- 改进需求中的核心功能已经落地。
- 剩余可选增强：
  - 更强 AI 模型接入。
  - 更精细的前端页面视觉优化。
  - 将本次本机迁移 SQL 整理成正式迁移脚本。

## 2026-06-09 用户显示名字段补充

### 本次目标

- 解决登录后页面显示 `admin`、`reviewer`、`user` 或角色名，不像真实用户名的问题。
- 将“登录账号”和“页面显示用户名”分离，便于课堂演示和后续多人协作扩展。

### 完成工作

- 数据库：
  - `user` 表新增 `display_name` 字段。
  - `sql/schema.sql` 已同步正式表结构。
  - `sql/migration-20260609-improvements.sql` 已补充兼容迁移逻辑，老数据会自动填充展示名。
  - 演示账号展示名调整为：
    - `admin`：系统管理员
    - `reviewer`：审核员一号
    - `user`：普通用户一号
- 后端：
  - `User` 实体新增 `displayName` 字段。
  - `UserResponse` 返回 `displayName`，前端无需再用角色或登录账号充当用户名。
  - `RegisterRequest` 支持传入 `displayName`。
  - 注册时如果没有填写展示名，则自动使用登录账号作为展示名。
- 前端：
  - 登录页将账号输入标注为“登录账号”。
  - 注册页新增“用户名”输入框，对应页面展示名。
  - 演示账号按钮显示“系统管理员”“审核员一号”“普通用户一号”。
  - 顶部栏、登录成功提示、用户管理列表优先展示 `displayName`。

### 验证结果

- 本机数据库已确认 `admin`、`reviewer`、`user` 的展示名与角色正确。
- 后端登录接口返回内容已包含 `displayName`。
- `mvn -DskipTests package`：通过。
- `npm run build`：通过。
- `git diff --check`：通过，仅有 Windows 换行提示。

### 当前状态

- 用户身份展示细节已补齐。
- 后续重点仍是最终演示、报告材料整理和可选 AI 高级模型增强。
