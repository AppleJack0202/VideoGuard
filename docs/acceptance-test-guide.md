# VideoGuard 交互验收指南

> 更新时间：2026-06-09
> 用途：说明如何启动项目、检查服务、通过页面和接口验证功能

## 1. 启动前检查

确认 MySQL 正在运行，数据库名为：

```text
video_guard
```

当前本机 MySQL 账号：

```text
用户名：root
密码：1234
```

如果第一次运行，需要导入数据库：

```bash
cd D:/zaproject/Real_Projects/VideoGuard
mysql -u root -p video_guard < sql/schema.sql
mysql --default-character-set=utf8mb4 -u root -p video_guard < sql/seed.sql
```

## 2. 启动三个服务

FastAPI：

```bash
cd D:/zaproject/Real_Projects/VideoGuard/ai-service-fastapi
.venv/Scripts/activate
python -m uvicorn app.main:app --host 127.0.0.1 --port 8000
```

SpringBoot：

```bash
cd D:/zaproject/Real_Projects/VideoGuard/backend-springboot
mvn spring-boot:run
```

Vue：

```bash
cd D:/zaproject/Real_Projects/VideoGuard/frontend-vue
npm run dev
```

浏览器打开：

```text
http://localhost:5173
```

## 3. 健康检查

```text
http://localhost:8081/api/health
http://localhost:8000/ai/health
http://localhost:5173
```

期望结果：

```text
SpringBoot 返回 {"status":"ok"}
FastAPI 返回 {"status":"ok"}
Vue 能打开页面
```

当前已验证的服务地址：

```text
http://localhost:8081/api/health -> 200
http://localhost:8000/ai/health -> 200
http://localhost:5173/videos/5 -> 200
http://localhost:5173/review -> 200
http://localhost:5173/dashboard -> 200
http://localhost:5173/login -> 200
```

## 4. 页面交互验收流程

### 4.1 登录与角色展示

1. 打开 `http://localhost:5173/login`。
2. 点击演示账号“审核员一号”，密码填写 `123456`。
3. 点击“登录”。
4. 登录成功后应进入“人工复审”。
5. 顶部栏应显示当前用户名 `审核员一号` 和角色 `审核员`。
6. 点击“退出”后应回到登录页。

可用于演示的账号：

```text
admin    -> 系统管理员 / 管理员
reviewer -> 审核员一号 / 审核员
user     -> 普通用户一号 / 一般用户
```

说明：当前是课程演示阶段，种子用户密码为占位值，任意非空密码均可登录。

### 4.2 上传与 AI 分析

1. 打开 `http://localhost:5173`。
2. 进入“视频上传”页面。
3. 选择一个 `.mp4` 视频。
4. 标题填写：`测试违规演示视频`。
5. 描述填写：`课程演示上传`。
6. 点击“上传视频”。
7. 上传后等待后台自动预审。
8. 进入“我的上传”或视频详情页，检查状态从 `已上传` 变为 `通过` 或 `复审中`。

### 4.3 视频管理

1. 进入“视频管理”页面。
2. 检查列表中是否出现刚上传的视频。
3. 使用“处理状态”和“风险等级”筛选。
4. 点击“详情”进入视频详情页。
5. 点击“分析”可重新触发 AI 分析。

### 4.4 人工复审

1. 进入“人工复审”页面。
2. 左侧待复审任务列表应显示状态为 `复审中` 的视频。
3. 点击一条任务，右侧应显示视频播放器、AI 风险分、敏感词命中和复审表单。
4. 复审结论选择“通过”“驳回”或“待申诉”。
5. 如不是“通过”，选择违规类别。
6. 填写审核意见。
7. 点击“提交复审”。
8. 检查复审日志表是否新增记录，审核员应显示 `审核员一号`。
9. 回到视频详情页，检查最终结论和复审日志。

### 4.5 统计看板

1. 进入“统计看板”页面。
2. 检查顶部指标是否显示真实数字：
   - 总视频数
   - 今日上传
   - AI 通过率
   - 待复审
   - 已人工复审
3. 检查页面是否显示四个图表：
   - 风险等级分布
   - 近 7 日上传趋势
   - 处理状态分布
   - 敏感类别分布
4. 上传、AI 分析或提交复审后，刷新统计看板，观察指标变化。

### 4.6 敏感词管理

1. 进入“敏感词管理”页面。
2. 检查表格中是否显示现有敏感词。
3. 使用“敏感类别”和“启用状态”筛选。
4. 点击“新增敏感词”，填写敏感词、类别、权重、启用状态。
5. 点击“编辑”，修改权重或类别。
6. 点击“停用”或“启用”，检查状态是否变化。
7. 点击“删除”，确认记录从列表中移除。

## 5. 接口验收命令

健康检查：

```powershell
Invoke-RestMethod http://localhost:8081/api/health
Invoke-RestMethod http://localhost:8000/ai/health
```

登录检查：

```powershell
$body = @{ username = "reviewer"; password = "123456" } | ConvertTo-Json
Invoke-RestMethod -Method Post http://localhost:8081/api/auth/login -ContentType "application/json; charset=utf-8" -Body $body
Invoke-RestMethod "http://localhost:8081/api/auth/me?userId=2"
```

查看视频列表：

```powershell
Invoke-RestMethod http://localhost:8081/api/videos
```

查看某个视频详情：

```powershell
Invoke-RestMethod http://localhost:8081/api/videos/5
```

触发 AI 分析：

```powershell
Invoke-RestMethod -Method Post http://localhost:8081/api/videos/5/analyze
```

查看待复审任务：

```powershell
Invoke-RestMethod http://localhost:8081/api/review/tasks
```

提交人工复审：

```powershell
$body = @{ status = "驳回"; violationCategory = "暴力"; comment = "人工复审确认驳回。" } | ConvertTo-Json
Invoke-RestMethod -Method Post http://localhost:8081/api/review/tasks/8/submit -Headers $headers -ContentType "application/json; charset=utf-8" -Body $body
```

查看复审日志：

```powershell
Invoke-RestMethod http://localhost:8081/api/review/logs/8
```

查看统计概览：

```powershell
Invoke-RestMethod http://localhost:8081/api/statistics/overview
```

查看风险等级分布：

```powershell
Invoke-RestMethod http://localhost:8081/api/statistics/risk-distribution
```

查看每日上传趋势：

```powershell
Invoke-RestMethod "http://localhost:8081/api/statistics/daily-upload?days=7"
```

查看处理状态分布：

```powershell
Invoke-RestMethod http://localhost:8081/api/statistics/status-distribution
```

查看敏感类别分布：

```powershell
Invoke-RestMethod http://localhost:8081/api/statistics/category-distribution
```

查看敏感词列表：

```powershell
Invoke-RestMethod http://localhost:8081/api/sensitive-words
```

新增敏感词：

```powershell
$body = @{ word = "测试词"; category = "custom"; weight = 20; enabled = 1 } | ConvertTo-Json
Invoke-RestMethod -Method Post http://localhost:8081/api/sensitive-words -ContentType "application/json; charset=utf-8" -Body $body
```

检查关键帧静态资源：

```powershell
Invoke-WebRequest http://localhost:8081/uploads/frames/5/frame_0000.jpg
```

## 6. 预期验收结果

风险标题 `测试违规演示视频` 命中敏感词后，预期结果类似：

```text
status = 复审中
aiRiskLevel = 可疑
aiRiskScore = 40
frames 至少 1 条
sensitiveHits 至少 1 条
aiResult 不为空
```

人工复审提交后，预期结果类似：

```text
finalResult = 正常 / 可疑 / 违规
finalComment 不为空
status = 通过 / 待申诉 / 驳回
reviewLogs 至少 1 条
```

最近一次复审冒烟测试结果：

```text
视频 ID = 8
提交后状态 = 驳回
最终结论 = 违规
复审日志数量 >= 1
```

最近一次统计接口验收结果：

```text
totalVideos = 5
todayUploads = 5
pendingReviews = 1
manualReviewed = 1
aiPassRate = 60.0
riskDistribution 包含 正常 和 可疑
statusDistribution 包含 通过、复审中、驳回
categoryDistribution 包含 暴力
```

最近一次敏感词管理验收结果：

```text
初始敏感词数量 = 6
新增 smoke_test_word 成功
编辑为 smoke_test_word_updated 成功
停用后 enabled = 0
按 custom + disabled 筛选可查到
删除测试词后筛选结果为 0
```

## 7. 常见问题

1. `8080` 被占用

```powershell
Get-NetTCPConnection -LocalPort 8080
Stop-Process -Id 占用端口的进程ID -Force
```

当前本机 `8080` 被 NI Application Web Server 占用且普通权限无法停止，因此本项目已切换为 `8081`。

2. MySQL 连不上

先确认服务是否启动，再确认密码是否为 `1234`。如果队友电脑密码不同，需要改环境变量 `DB_PASSWORD` 或本地配置。

3. PowerShell 显示中文乱码

有时 PowerShell 只是显示乱码，数据库实际存储可能是正确 UTF-8。测试中文时，优先用浏览器页面或 Python/Apifox/Postman 检查。

4. 上传后没有 AI 结果

检查 FastAPI 是否启动：

```text
http://localhost:8000/ai/health
```

再检查 SpringBoot 日志是否有调用 AI 服务失败。

5. 人工复审列表为空

复审任务默认只显示状态为 `复审中` 且尚未人工复审的视频。可以先上传风险标题视频，等待后台自动预审后，再回到“人工复审”页面查看。

## 8. 改进版验收流程

### 8.1 登录与角色

1. 打开 `http://localhost:5173/login`。
2. 分别登录：
   - `admin / 123456`：进入管理员视图。
   - `reviewer / 123456`：进入审核员视图。
   - `user / 123456`：进入一般用户视图。
3. 检查左侧菜单：
   - 一般用户：视频上传、我的上传。
   - 审核员：人工复审。
   - 管理员：统计看板、视频管理、敏感词管理、用户管理。

### 8.2 一般用户上传与自动预审

1. 使用 `user / 123456` 登录。
2. 进入“视频上传”。
3. 选择视频，标题填写 `测试违规 自动预审`。
4. 点击“上传视频”。
5. 进入“我的上传”，刷新观察状态：
   - 初始为 `已上传`。
   - 后台自动预审后变为 `通过` 或 `复审中`。
6. 点击详情，普通用户只应看到视频播放和处理状态，不应看到风险分、敏感词命中、关键帧和复审日志。

### 8.3 审核员复审

1. 使用 `reviewer / 123456` 登录。
2. 进入“人工复审”。
3. 按风险等级 `可疑` 和违规类别 `暴力` 筛选。
4. 点击“详情”可查看审核详情页。
5. 点击“复审”后选择：
   - 视频状态：`通过` / `驳回` / `待申诉`
   - 违规类别：`暴力` / `色情` / `政治敏感`
   - 复审意见：必填
6. 提交后该视频不可再次复审。

### 8.4 管理员管理

1. 使用 `admin / 123456` 登录。
2. 进入“统计看板”，检查今日上传量、复审中数量、状态分布和违规类别分布。
3. 进入“视频管理”，按处理状态、风险等级、违规类别筛选。
4. 进入“敏感词管理”，新增或删除敏感词，类别应为 `暴力`、`色情`、`政治敏感`。
5. 进入“用户管理”，修改用户角色。

### 8.5 接口验收提示

除登录和注册外，接口都需要 JWT：

```powershell
$loginBody = @{ username = "admin"; password = "123456" } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post http://localhost:8081/api/auth/login -ContentType "application/json; charset=utf-8" -Body $loginBody
$headers = @{ Authorization = "Bearer " + $login.token }
Invoke-RestMethod http://localhost:8081/api/statistics/overview -Headers $headers
```
