# AI Psychology Platform

《基于 Spring Boot 的 AI 驱动型心理咨询与危机干预系统》代码仓库。

本仓库按 [UGS Core v0.2](docs/git/ugs-core.md) 管理代码变更：`main` 是唯一长期分支，日常变更使用短生命周期 topic branch，通过 CR/PR 采用 `rebase-ff` 集成。

## 项目结构

```text
ai-psychology-platform/
├── backend/                 # Spring Boot 后端
├── frontend/                # Vue 3 前端
├── docs/
│   ├── git/                 # UGS 规范文档
│   ├── initialization.md    # 仓库初始化与运行指南
│   └── sql/init.sql         # 数据库初始化脚本
├── .githooks/               # UGS 受管 hooks
├── .github/                 # PR 模板、CODEOWNERS、CI 校验
├── cr/                      # 非 GitHub 场景的 CR 记录
├── keys/                    # release tag SSH signer 注册表
├── scripts/                 # UGS 本地校验脚本
├── CONTRIBUTING.md          # 贡献与变更流程
├── RELEASE.md               # 发布与签名校验
└── REPOSITORY_POLICY.md     # 项目级 UGS 策略
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- npm 9+
- MySQL 8.0+
- Redis 6.0+（可选）

## 快速启动

1. 创建数据库并执行 `docs/sql/init.sql`。
2. 按需设置后端环境变量 `DB_USERNAME`、`DB_PASSWORD`、`REDIS_HOST`、`JWT_SECRET`。
3. 启动后端：

   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. 安装并启动前端：

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. 打开 <http://localhost:3000>。后端探针地址为 <http://localhost:8080/api/test>。

认证接口已提供 `POST /api/auth/login` 和 `GET /api/auth/me`；完整初始化与运行说明请参阅 [docs/initialization.md](docs/initialization.md)。

## UGS 变更流程

```bash
git config core.hooksPath .githooks
git switch main
git pull --ff-only
git switch -c feat/<scope>-<slug>
```

提交必须使用 UGS 格式，并以 trailer 结尾，例如：

```text
feat(auth): add user login endpoint

Add the first login endpoint and keep authentication behavior isolated from
the initial health-check API.

Refs: issue-123
Tested-by: Maintainer <maintainer@example.com>
```

打开 PR 前运行 `scripts/validate_repo.sh`、后端测试和前端构建。正式发布使用由 `keys/allowed_signers` 中受信任维护者签名的 `v<major>.<minor>.<patch>` annotated tag。

## 项目治理文档

- [REPOSITORY_POLICY.md](REPOSITORY_POLICY.md)：本仓库 UGS 声明
- [CONTRIBUTING.md](CONTRIBUTING.md)：分支、提交、CR 与审查流程
- [RELEASE.md](RELEASE.md)：SemVer 与正式发布签名流程
- [cr/README.md](cr/README.md)：等价 CR 记录格式
- [keys/README.md](keys/README.md)：可信 SSH signer 管理
