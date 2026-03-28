# 仓库目录命名与整理规范

本文档用于统一项目目录命名、职责边界和新增模块的放置方式。

## 1. 目标结构

```text
hotel/
├── backend/
│   └── api-server/
├── frontend/
│   └── web-app/
├── admin/
│   └── admin-web/
├── docs/
├── scripts/
├── PLAN.md
└── README.md
```

## 2. 命名规则

- 目录统一使用小写并采用 `kebab-case`（如 `api-server`、`web-app`、`admin-web`）。
- 按职责分层：
  - `backend/`：后端服务
  - `frontend/`：面向用户的前端应用
  - `admin/`：管理端/运营端应用
  - `docs/`：架构、API、部署、规范文档
  - `scripts/`：启动、运维、辅助脚本

## 3. 整理原则

- **单一职责**：每个一级目录只承担一种角色。
- **可发现性**：同类项目放在同一父目录下。
- **低风险迁移**：优先做“目录外层分组”，避免一次性改动子项目内部结构。
- **文档先行**：目录变更必须同步更新 `README.md` 与 `docs/SETUP.md`。

## 4. 新增模块放置建议

- 新增后端微服务：`backend/<service-name>/`
- 新增用户侧前端：`frontend/<app-name>/`
- 新增管理端前端：`admin/<app-name>/`
- 新增通用工具（可选）：`tools/`（脚本或本地开发工具）

## 5. 迁移检查清单

每次目录调整后，至少检查以下内容：

1. 启动脚本路径：`scripts/start-all.bat`、`scripts/start-all.sh`
2. 文档路径：`README.md`、`docs/SETUP.md`、`docs/ARCHITECTURE.md`
3. CI/构建脚本中的工作目录
4. IDE 配置（如 `.vscode/tasks.json`）是否引用旧路径

## 6. 后续可选优化

- 增加 `scripts/check-structure.*`，自动校验关键目录是否存在。
- 为每个子项目补充独立 `README.md` 的“从仓库根目录启动”命令示例。
