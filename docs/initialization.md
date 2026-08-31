# 代码仓库初始化指南

本文档用于初始化和运行《基于 Spring Boot 的 AI 驱动型心理咨询与危机干预系统》。项目采用前后端分离结构：后端使用 Spring Boot 3，前端使用 Vue 3 + Vite。

本仓库同时遵循 [UGS Core v0.2](git/ugs-core.md)。代码管理约定以
[`REPOSITORY_POLICY.md`](../REPOSITORY_POLICY.md) 为准。

## 1. 环境准备

请先安装以下工具：

| 工具 | 版本要求 | 用途 |
| --- | --- | --- |
| JDK | 17+ | 后端运行与编译 |
| Maven | 3.8+ | 后端构建 |
| Node.js | 18+ | 前端开发 |
| npm | 9+ | 前端包管理 |
| Git | 2.30+ | 版本控制 |
| MySQL | 8.0+ | 业务数据存储 |
| Redis | 6.0+ | 缓存，可选 |

确认版本：

```bash
java -version
mvn -version
node --version
npm --version
git --version
```

本仓库已经包含前后端项目骨架，不需要重复执行 `mvn archetype:generate` 或 `npm create vue`。如果从零开始创建同类仓库，建议使用 Spring Initializr 创建 Spring Boot 项目，而不是 Maven quickstart，因为 quickstart 不包含 Spring Boot 运行配置。

## 2. 目录结构

```text
ai-psychology-platform/
├── backend/
│   ├── src/main/java/com/psychology/
│   ├── src/main/resources/application.yml
│   ├── src/test/
│   └── pom.xml
├── frontend/
│   ├── src/router/
│   ├── src/views/
│   ├── package.json
│   └── vite.config.ts
├── docs/
│   ├── initialization.md
│   └── sql/init.sql
├── .editorconfig
├── .gitignore
└── README.md
```

## 3. 初始化数据库

启动 MySQL 后执行：

```sql
CREATE DATABASE IF NOT EXISTS psychology
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

然后执行仓库中的 `docs/sql/init.sql`。Linux/macOS 可使用：

```bash
mysql -u root -p psychology < docs/sql/init.sql
```

PowerShell 可使用：

```powershell
Get-Content .\docs\sql\init.sql | mysql -u root -p psychology
```

初始化脚本使用 `CREATE TABLE IF NOT EXISTS`，重复执行不会覆盖已有业务数据。

## 4. 配置后端

后端默认连接：

- 地址：`http://localhost:8080`
- 数据库：`localhost:3306/psychology`
- 数据库用户：`root`
- 数据库密码：`change-me`
- Redis：`localhost:6379`

开发环境建议通过环境变量覆盖默认值。PowerShell 示例：

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_password"
$env:REDIS_HOST = "localhost"
$env:JWT_SECRET = "replace-with-a-random-secret-at-least-32-bytes"
```

Linux/macOS 示例：

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export REDIS_HOST=localhost
export JWT_SECRET=replace-with-a-random-secret-at-least-32-bytes
```

启动后端：

```bash
cd backend
mvn spring-boot:run
```

验证接口：

```text
GET http://localhost:8080/api/test
```

预期返回包含 `"status":"ok"` 和 `"service":"psychology-backend"` 的 JSON。

## 5. 认证接口

认证基础已提供以下接口：

- `POST /api/auth/login`：使用账号和至少 8 位密码换取 Bearer access token。
- `GET /api/auth/me`：携带 `Authorization: Bearer <token>` 获取当前登录身份。
- `GET /api/test`：公开健康检查接口。

用户密码只保存为 BCrypt 哈希，登录响应不会返回密码或哈希。角色目前支持
`USER`、`COUNSELOR` 和 `ADMIN`；`/api/admin/**` 仅允许 `ADMIN` 访问。系统不包含
默认账号，须由后续用户管理流程创建账号。

生产环境必须使用随机生成且长度至少 32 字节的 `JWT_SECRET`，不能使用配置文件中的
开发默认值。JWT 当前有效期默认 24 小时。

## 6. 配置前端

在新终端执行：

```bash
cd frontend
npm install
npm run dev
```

前端地址为 <http://localhost:3000>。`vite.config.ts` 已配置 `/api` 代理，浏览器请求 `/api/test` 时会转发到 `http://localhost:8080/api/test`。

前端已有以下基础路由：

- `/home`：首页与后端连接检查
- `/login`：登录页占位
- `/`：重定向到 `/home`

## 7. 验证与构建

后端测试与打包：

```bash
cd backend
mvn clean test
mvn clean package -DskipTests
```

前端类型检查与生产构建：

```bash
cd frontend
npm run build
```

若当前机器仍是 Java 8 或 Node.js 16，构建会因版本不满足要求而失败。请先切换到 JDK 17+ 和 Node.js 18+，再执行上述命令。

## 8. Git 初始化与 UGS 变更流程

在仓库根目录执行：

```bash
git init
git branch -M main
git config core.hooksPath .githooks
```

如果本机尚未配置 Git 身份，请使用自己的真实信息配置（不要使用示例值）：

```bash
git config user.name "Your Name"
git config user.email "you@example.com"
```

首次治理 bootstrap 提交也必须符合 UGS 提交格式：

```bash
git add .
git commit \
  -m "chore(repo): bootstrap psychology platform" \
  -m "Add the Spring Boot, Vue 3, database, and repository governance skeleton." \
  -m "Refs: repo-bootstrap"
```

该 bootstrap 提交允许作为治理层落地的例外；后续非 trivial 变更必须使用 topic
branch 和 CR/PR。

本仓库使用 `continuous` profile：`main` 是唯一长期分支，默认合并策略为
`rebase-ff`。非 trivial 变更必须从 `main` 创建短生命周期 topic branch：

```bash
git switch main
git pull --ff-only
git switch -c feat/<scope>-<slug>
```

推荐的分支前缀为 `feat/`、`fix/`、`docs/`、`chore/`、`refactor/`、`test/`、
`build/`、`ci/` 和 `perf/`。不要创建长期 `develop` 分支。

提交消息必须符合 UGS 格式，且至少包含一个 trailer：

```text
<type>(<scope>): <summary>

<body>

Refs: <issue-or-change-id>
Tested-by: <name> <email>
```

GitHub PR 是首选 CR；PR 正文必须保留
`.github/pull_request_template.md` 中的 `Summary`、`Motivation`、
`Test Evidence`、`Risk`、`Rollback`、`Breaking Change` 和
`Backport Target` 七个部分。无 GitHub 时，使用 `cr/TEMPLATE.md` 创建
`cr/CR-XXXX-<slug>.md`。

提交前执行：

```bash
scripts/validate_repo.sh
scripts/validate_commit_range.sh main..HEAD
```

关联远程仓库时，将地址替换成实际地址。正常情况下应先推送 topic branch，
再通过 PR 或等价 CR 集成到 `main`：

```bash
git remote add origin https://github.com/your-username/ai-psychology-platform.git
git push -u origin feat/<scope>-<slug>
```

等价 CR 集成需要先在 `cr/` 中保存匹配的记录，再使用：

```bash
UGS_ALLOW_MAIN_PUSH=cr git push origin HEAD:main
```

`main` 的直接推送会被 `.githooks/pre-push` 拒绝；首次安装治理层时才允许使用
`UGS_ALLOW_MAIN_PUSH=bootstrap`。正式发布必须使用签名 annotated tag，详见
[`RELEASE.md`](../RELEASE.md)。

不要将数据库密码、API Key 或本地环境文件提交到仓库。生产环境应使用部署平台的密钥管理功能注入配置。

## 9. 常用命令

| 操作 | 命令 |
| --- | --- |
| 查看 Git 状态 | `git status` |
| 查看分支 | `git branch` |
| 创建 topic branch | `git switch -c feat/<scope>-<slug>` |
| 启用 UGS hooks | `git config core.hooksPath .githooks` |
| 校验仓库治理 | `scripts/validate_repo.sh` |
| 后端运行 | `cd backend && mvn spring-boot:run` |
| 后端测试 | `cd backend && mvn test` |
| 后端打包 | `cd backend && mvn clean package -DskipTests` |
| 前端开发 | `cd frontend && npm run dev` |
| 前端构建 | `cd frontend && npm run build` |
