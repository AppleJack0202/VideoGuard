# VideoGuard 交互验收指南

> 更新时间：2026-06-08  
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

在浏览器或 PowerShell 检查：

```text
http://localhost:8080/api/health
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
http://localhost:8080/api/health -> 200
http://localhost:8000/ai/health -> 200
http://localhost:5173/videos/5 -> 200
```

## 4. 页面交互验收流程

推荐演示流程：

1. 打开 `http://localhost:5173`。
2. 进入“视频上传”页面。
3. 选择一个 `.mp4` 视频。
4. 标题填写：

```text
测试违规演示视频
```

5. 描述填写：

```text
课程演示上传
```

6. 上传人 ID 填：

```text
3
```

7. 点击“上传视频”。
8. 上传成功后点击“开始 AI 分析”。
9. 分析完成后进入视频详情页。
10. 检查详情页是否展示：
    - 视频播放器。
    - 处理状态。
    - AI 风险等级。
    - 风险分。
    - AI 分析结果。
    - 敏感词命中。
    - 视频关键帧。
11. 进入“视频管理”页面。
12. 检查列表中是否出现刚上传的视频。
13. 使用“处理状态”和“风险等级”筛选。

## 5. 接口验收命令

健康检查：

```powershell
Invoke-RestMethod http://localhost:8080/api/health
Invoke-RestMethod http://localhost:8000/ai/health
```

查看视频列表：

```powershell
Invoke-RestMethod http://localhost:8080/api/videos
```

查看某个视频详情：

```powershell
Invoke-RestMethod http://localhost:8080/api/videos/5
```

触发 AI 分析：

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/api/videos/5/analyze
```

检查关键帧静态资源：

```powershell
Invoke-WebRequest http://localhost:8080/uploads/frames/5/frame_0000.jpg
```

## 6. 预期验收结果

风险标题 `测试违规演示视频` 命中敏感词后，预期结果类似：

```text
status = AI_SUSPICIOUS
aiRiskLevel = SUSPICIOUS
aiRiskScore = 40
frames 至少 1 条
sensitiveHits 至少 1 条
aiResult 不为空
```

最近一次冒烟测试结果：

```text
上传视频 ID = 7
上传状态 = UPLOADED
AI 分析状态 = AI_PASSED
AI 风险等级 = PASS
AI 风险分 = 5
关键帧数量 = 1
敏感词命中数量 = 0
```

## 7. 常见问题

1. `8080` 被占用

处理方式：

```powershell
Get-NetTCPConnection -LocalPort 8080
Stop-Process -Id 占用端口的进程ID -Force
```

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

5. 页面打不开

确认 Vue 服务是否启动：

```text
http://localhost:5173
```

如果端口不是 5173，以 Vite 控制台输出的地址为准。
