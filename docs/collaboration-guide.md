# VideoGuard 团队协作指南

> 更新时间：2026-06-08  
> 适用对象：第一次合作开发本项目的组员  
> 仓库地址：`git@github.com:wade13265071018-hue/VideoGuard.git`

## 1. 推荐协作方式

本项目建议采用下面的分支规则：

```text
main      稳定演示分支，只放确认可运行的版本
dev       日常开发集成分支，大家的新功能先合到这里
feature/* 每个人自己的功能分支，例如 feature/review-api
```

不要几个人同时直接改 `main`。平时从 `dev` 拉代码，自己新建功能分支，完成后推送到 GitHub，再通过 Pull Request 合并到 `dev`。

## 2. 队友第一次拉取代码

队友电脑上需要先安装：

- Git
- JDK 17
- Maven
- Node.js 18 或更高版本
- Python 3.10 或更高版本
- MySQL 8

第一次获取代码：

```bash
cd D:/zaproject/Real_Projects
git clone git@github.com:wade13265071018-hue/VideoGuard.git
cd VideoGuard
git checkout dev
```

如果队友没有配置 SSH，也可以使用 HTTPS：

```bash
git clone https://github.com/wade13265071018-hue/VideoGuard.git
```

仓库所有者需要在 GitHub 仓库页面邀请队友：

```text
GitHub 仓库 -> Settings -> Collaborators -> Add people
```

## 3. 每天开始写代码前

每个人开始写代码前先同步最新版：

```bash
git checkout dev
git pull origin dev
git checkout -b feature/你的任务名
```

例如：

```bash
git checkout -b feature/manual-review
```

## 4. 写完一个小功能后

先检查改了哪些文件：

```bash
git status
```

运行对应模块的验证命令：

```bash
cd backend-springboot
mvn -DskipTests package
```

```bash
cd frontend-vue
npm run build
```

确认没问题后提交：

```bash
git add .
git commit -m "feat: add manual review api"
git push -u origin feature/manual-review
```

然后到 GitHub 创建 Pull Request：

```text
base: dev
compare: feature/manual-review
```

由另一个队友看一下改动，没有问题再合并。

## 5. 避免冲突的分工建议

第一次联合开发，最容易出问题的是多人同时改同一个文件。建议按模块分工：

```text
同学 A：前端页面
主要目录：frontend-vue/src/views、frontend-vue/src/api、frontend-vue/src/styles

同学 B：后端业务接口
主要目录：backend-springboot/src/main/java/com/videoguard

同学 C：AI 服务、测试视频、演示材料
主要目录：ai-service-fastapi、docs、samples
```

如果两个人都要改同一个文件，先在群里说一声。比如 `VideoController.java`、`router/index.js`、`main.css` 这种公共文件，最好一次只由一个人改。

## 6. Git 冲突时怎么处理

如果 `git pull` 或合并 PR 时出现 conflict，不要慌。基本流程：

```bash
git status
```

打开冲突文件，找到这些标记：

```text
<<<<<<< HEAD
本地内容
=======
别人提交的内容
>>>>>>> 分支名
```

人工保留正确内容，删除冲突标记，然后：

```bash
git add 冲突文件
git commit
```

如果不确定怎么处理，先截图或把冲突文件发给队友，不要随便 `git reset --hard`。

## 7. 不要提交的内容

下面这些内容不要放进 Git：

```text
node_modules/
.venv/
target/
dist/
uploads/
*.mp4
*.avi
*.mov
.env
数据库密码
大模型文件
```

当前 `.gitignore` 已经忽略了大部分运行产物，但提交前仍然要看一眼 `git status`。

