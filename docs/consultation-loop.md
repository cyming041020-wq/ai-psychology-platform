# 可运行咨询闭环

当前版本提供从注册到 AI 咨询消息持久化的最小闭环。AI 回复使用本地 Mock 实现，后续可在不改动控制器的情况下替换为真实模型适配器。

## 启动

1. 创建 MySQL 数据库 `psychology`，并配置 `DB_USERNAME`、`DB_PASSWORD` 和长度至少为 32 字节的 `JWT_SECRET`。
2. 启动后端：

   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. 启动前端：

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

打开 <http://localhost:3000>，点击“注册新账号”，注册后会自动进入咨询工作台。

## API 闭环

```text
POST /api/auth/register       注册并返回 Bearer token
POST /api/consultations       创建 AI 咨询会话
GET  /api/consultations       查看当前用户的会话列表
GET  /api/consultations/{id}  查看会话及消息历史
POST /api/consultations/{id}/messages
                              保存用户消息并返回 Mock AI 回复
```

所有咨询接口都需要 `Authorization: Bearer <accessToken>`。Flyway 会自动执行 `V2__create_consultation_messages.sql`；使用手动初始化方式时，请执行更新后的 `docs/sql/init.sql`。

## 当前边界

- 当前回复是 Mock 内容，不构成诊断、治疗或危机处置建议。
- 会话数据按登录用户隔离，访问其他用户的会话统一返回 404。
- 危机识别、人工咨询师接管和真实模型接入留待后续独立变更。
