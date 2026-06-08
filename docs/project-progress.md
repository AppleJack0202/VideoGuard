# VideoGuard 项目进展规划

> 更新时间：2026-06-08  
> 用途：说明项目当前完成情况、正在进行的工作和后续计划

## 1. 已完成内容

截至 2026-06-08，项目已经完成这些内容：

- 已在 `D:\zaproject\Real_Projects\VideoGuard` 建立正式项目目录。
- 已建立 monorepo 结构：
  - `backend-springboot`
  - `ai-service-fastapi`
  - `frontend-vue`
  - `docs`
  - `sql`
  - `samples`
- 已配置 GitHub 仓库，并推送 `main`、`dev` 分支。
- 已完成 MySQL 数据库结构：
  - `user`
  - `video`
  - `video_frame`
  - `sensitive_word`
  - `sensitive_hit`
  - `ai_review_result`
  - `review_log`
- 已完成 FastAPI AI MVP：
  - 视频元数据提取
  - 关键帧抽取
  - 文本敏感词检测
  - 图像风险模拟检测
  - 综合 AI 风险评分
- 已完成 SpringBoot 基础后端：
  - 健康检查
  - 视频上传
  - 视频列表
  - 视频详情
  - 视频播放静态资源访问
  - 调用 FastAPI 分析视频
  - AI 分析结果入库
  - 关键帧入库
  - 敏感词命中入库
- 已完成 Vue 基础前端骨架：
  - 统计看板页面
  - 上传页面
  - 视频管理页面
  - 人工复审页面
  - 登录页面
- 已完成前端真实接口接入：
  - 上传页面可调用 `POST /api/videos/upload`
  - 上传成功后可调用 `POST /api/videos/{id}/analyze`
  - 视频管理页可调用 `GET /api/videos`
  - 视频管理页支持按 `status` 和 `aiRiskLevel` 筛选
  - 视频详情页可调用 `GET /api/videos/{id}`
  - 视频详情页可展示视频播放、AI 分析结果、关键帧、敏感词命中
- 当前本机服务端口：
  - Vue：`http://localhost:5173`
  - SpringBoot：`http://localhost:8080`
  - FastAPI：`http://localhost:8000`
  - MySQL：`localhost:3306`

## 2. 正在进行

当前暂停点：

```text
前端真实接口接入已完成，下一阶段进入人工复审模块
```

本阶段已完成并验证的内容包括：

- 后端增加 Vue 跨域配置。
- 前端增加统一 API 客户端 `frontend-vue/src/api/client.js`。
- 上传页面接入真实上传接口。
- 视频列表页面接入真实列表接口。
- 新增视频详情页面，展示视频播放、AI 分析结果、关键帧、敏感词命中。

已验证：

- `npm run build` 通过。
- `mvn -DskipTests package` 通过。
- `http://localhost:8080/api/health` 返回 `{"status":"ok"}`。
- `http://localhost:5173` 返回 200。
- `GET /api/videos` 可返回测试视频。
- `GET /api/videos/5` 可返回 AI 分析详情。
- 冒烟测试上传视频 ID `7` 成功，分析后状态为 `AI_PASSED`，风险等级为 `PASS`，关键帧数量为 1。

## 3. 待完成任务

优先级从高到低：

1. 完成人工复审模块
   - 查询待复审视频。
   - 审核员提交最终结论。
   - 写入 `review_log`。
   - 更新 `video.final_result` 和 `video.final_comment`。

2. 完成统计看板真实数据
   - 总视频数。
   - 待复审数量。
   - 各风险等级数量。
   - 每日上传趋势。
   - 违规类别分布。

3. 完成登录与角色
   - 简化登录即可，课程演示重点不必做复杂权限。
   - 区分普通用户、审核员、管理员。

4. 完成敏感词管理
   - 新增敏感词。
   - 修改权重。
   - 启用或禁用敏感词。

5. 完成测试、演示脚本和课程报告材料
   - 准备正常视频和风险视频。
   - 准备 5 分钟演示流程。
   - 整理数据库表设计、系统架构图、核心代码说明。

## 4. 建议里程碑

```text
M1 已完成：项目骨架、数据库、三端服务、GitHub 协作
M2 已完成：视频上传、AI 分析、结果入库
M3 已完成：前端真实接口接入
M4 下一步：人工复审
M5 待完成：统计看板、登录、敏感词管理
M6 待完成：最终演示、报告、答辩材料
```
