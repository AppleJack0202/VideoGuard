# VideoGuard 项目进展规划

> 更新时间：2026-06-08  
> 用途：说明项目当前完成情况、正在进行的工作和后续计划

## 1. 已完成内容

截至 2026-06-08，项目已经完成这些内容：

- 已在 `D:\zaproject\Real_Projects\VideoGuard` 建立正式项目目录。
- 已建立 monorepo 结构：`backend-springboot`、`ai-service-fastapi`、`frontend-vue`、`docs`、`sql`、`samples`。
- 已配置 GitHub 仓库，并推送 `main`、`dev` 分支。
- 已完成 MySQL 数据库结构：`user`、`video`、`video_frame`、`sensitive_word`、`sensitive_hit`、`ai_review_result`、`review_log`。
- 已完成 FastAPI AI MVP：视频元数据提取、关键帧抽取、文本敏感词检测、图像风险模拟检测、综合 AI 风险评分。
- 已完成 SpringBoot 基础后端：健康检查、视频上传、视频列表、视频详情、视频播放静态资源访问。
- 已完成 SpringBoot 调用 FastAPI 并持久化 AI 结果：视频元数据、关键帧、敏感词命中、AI 分析结果均可入库。
- 已完成前端真实接口接入：
  - 上传页面可调用 `POST /api/videos/upload`。
  - 上传成功后可调用 `POST /api/videos/{id}/analyze`。
  - 视频管理页可调用 `GET /api/videos`，并支持按 `status`、`aiRiskLevel` 筛选。
  - 视频详情页可调用 `GET /api/videos/{id}`，展示视频播放、AI 分析结果、关键帧、敏感词命中、复审日志。
- 已完成人工复审模块第一版：
  - 后端支持 `GET /api/review/tasks` 查询待复审任务。
  - 后端支持 `GET /api/review/tasks/{videoId}` 查询复审详情。
  - 后端支持 `POST /api/review/tasks/{videoId}/submit` 提交复审结论。
  - 后端支持 `GET /api/review/logs/{videoId}` 查询复审日志。
  - 前端“人工复审”页面可展示任务列表、视频播放器、AI 证据、复审表单和复审日志。
- 已完成统计看板真实数据第一版：
  - 后端支持 `GET /api/statistics/overview`。
  - 后端支持风险等级分布、处理状态分布、每日上传趋势、敏感类别分布统计。
  - 前端统计看板接入真实接口。
  - 前端使用 ECharts 展示风险等级、上传趋势、处理状态、敏感类别分布。
- 当前本机服务端口：
  - Vue：`http://localhost:5173`
  - SpringBoot：`http://localhost:8080`
  - FastAPI：`http://localhost:8000`
  - MySQL：`localhost:3306`

## 2. 正在进行

当前阶段：

```text
统计看板真实数据第一版已完成，下一阶段进入登录角色、敏感词管理和演示材料整理
```

本阶段已验证：

- `npm run build` 通过。
- `mvn -DskipTests package` 通过。
- `http://localhost:8080/api/health` 返回 `{"status":"ok"}`。
- `http://localhost:5173/review` 返回 200。
- `GET /api/review/tasks` 可返回待复审视频。
- `POST /api/review/tasks/8/submit` 可提交人工复审。
- `GET /api/review/logs/8` 可查询复审日志。
- 视频 ID `8` 提交复审后状态更新为 `MANUAL_REJECTED`，最终结论为 `REJECT`，复审日志至少 1 条。
- `GET /api/statistics/overview` 可返回总视频数、今日上传、待复审、已人工复审、AI 通过率。
- `GET /api/statistics/risk-distribution` 可返回风险等级分布。
- `GET /api/statistics/status-distribution` 可返回处理状态分布。
- `GET /api/statistics/daily-upload?days=7` 可返回近 7 日上传趋势。
- `GET /api/statistics/category-distribution` 可返回敏感类别分布。

## 3. 待完成任务

优先级从高到低：

1. 完成登录与角色
   - 简化登录即可，课程演示重点不必做复杂权限。
   - 区分普通用户、审核员、管理员。

2. 完成敏感词管理
   - 新增敏感词。
   - 修改权重。
   - 启用或禁用敏感词。

3. 完成测试、演示脚本和课程报告材料
   - 准备正常视频和风险视频。
   - 准备 5 分钟演示流程。
   - 整理数据库表设计、系统架构图、核心代码说明。

## 4. 建议里程碑

```text
M1 已完成：项目骨架、数据库、三端服务、GitHub 协作
M2 已完成：视频上传、AI 分析、结果入库
M3 已完成：前端真实接口接入
M4 已完成：人工复审第一版
M5 已完成：统计看板真实数据第一版
M6 下一步：登录、敏感词管理、最终演示和报告材料
```
