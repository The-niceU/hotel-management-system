# Project Optimization & Test Plan

## Scope Overview

- **Backend (`hotel/`)**: Spring Boot 2.0.5 + MyBatis + MySQL. Focus on auth, reservations, room/worker management, operational stability.
- **Frontend – Customer (`hotel_app/`)**: Vue 2 SPA for guests (login, register, browse rooms, book, view orders/profile).
- **Frontend – Admin (`hotel-manager/`)**: Vue 2 dashboard for staff/admins (orders, rooms, check-ins, workers, analytics).
- **Shared Infrastructure**: MySQL 5.7 schema/data scripts, npm/node toolchains, Maven build, CI (manual for now).

## Phase 1: Backend Assessment & Hardening

1. **Code Quality & Structure**
   - Review controllers/services for duplicate logic, missing validation, inconsistent error handling.
   - Enforce DTO/request validation (e.g., Hibernate Validator) instead of raw String parameters.
   - Centralize response/error codes.
2. **Security**
   - Strengthen password policy (salted hashing, password length checks) within current Spring Boot stack.
   - Sanitize inputs, ensure parameter binding avoids SQL injection (MyBatis already prepared but review custom SQL).
   - Add rate limiting / login attempt tracking（保留现有框架，只新增逻辑层实现）。
3. **Stability & Performance**
   - Audit database access for N+1 queries, add pagination/default limits.
   - Cache read-mostly configs (hotel info, room types) via Spring Cache.
   - Improve logging (structured logs, correlation IDs, WARN/ERROR coverage).
4. **Testing**
   - Add unit tests for services (mock mappers) covering auth, booking, room operations.
   - Add integration tests (Spring Boot Test + H2 or MySQL test schema) for key flows.
   - Provide Postman/HTTP scenario collection for manual regression.
5. **Tooling**
   - 在现有 Maven 版本基础上接入代码规范（Spotless/Checkstyle）与 Jacoco 覆盖率，而不是升级框架。

### Current Backend Findings (2025-12-24)

- **Build & Dependencies**: 保持 Spring Boot 2.0.5、MyBatis 1.3.2、mysql-connector 8.0.11 不变，但缺乏依赖管理/代码质量插件配置。
- **Configuration Gaps**: `mybatis.type-aliases-package` uses `classpath*` (should be package path), no Hikari/DataSource tuning, missing logging level control and profile separation, credentials hard-coded.
- **Security & Session Handling**: Passwords hashed with unsalted MD5; interceptors (e.g., `AdminInterceptor`) assume `session.getAttribute("role")` non-null leading to NPE and lack of graceful auth failures. No CSRF/token strategy, no login throttling.
- **Controller Surface**: Endpoints (e.g., `LoginController`, `OpOrderController`) bind raw query params, lack `@RequestBody` DTOs, validation annotations, or consistent error semantics; responses expose internal status codes (`-3/-2`).
- **Business Logic & Transactions**: `OrderServiceImpl.payOrder` uses bitwise `|` for null check, manually manages rollback, and mixes numeric error codes; cancellation/restock logic not wrapped in transactions; no pagination/filtering on list endpoints.
- **Testing Coverage**: Repository contains only starter dependency; no unit/integration tests, fixtures, or automation to guard regressions during refactors.

> These findings define the backlog for Phase 1: 在不升级框架的前提下引入校验/安全层、重构服务逻辑，并建立自动化测试，然后再进入前端优化。

## Phase 2: Frontend `hotel_app` Review & Refresh

1. **Build/Dependencies**
   - 保持当前 Vue2 + webpack 配置，仅修复 `node-sass` 兼容问题（必要时通过本地二进制或 postcss 方案），并补充 eslint/prettier 设置。
2. **Architecture**
   - Normalize API layer (`src/api/*.js`), ensure consistent error handling/toast notifications.
   - Introduce Vuex (if not already) or at least centralized auth/token/session store.
   - Add route guards for auth-required pages.
3. **UI/UX Modernization**
   - Establish updated design system (color palette, typography, spacing).
   - Redesign key pages (Landing, Login/Register, Room list/detail, Booking flow, Profile) with responsive layout, purposeful typography per instructions (avoid default stacks, customize fonts, gradients/backgrounds, animations).
   - Add loading states, empty states, validation feedback.
4. **Testing**
   - Component/unit tests via Jest + Vue Test Utils for critical components.
   - Cypress or Playwright smoke flows (login + booking + order view).

### Current `hotel_app` Findings (2025-12-24)

- **Build/Dependency Pain**: Webpack 3 脚手架依赖老旧且没有锁定版本；`axios` 被放在 devDependencies，导致生产安装缺失；无 lint/format/test 脚本。
- **API 层缺陷**: `utils/request.js` 将所有非 `code===1000` 的返回视为错误并 toast，但后端成功码本身就是 1000；网络错误直接强跳 `/404` 体验差；没有统一的 loading/重试策略。
- **认证状态**: 仅凭 Cookies (`user_id`,`username`) 判断登录，既无过期策略也无服务端校验；App 组件和 `main.js` 均直接读取 cookie，缺少 Vuex/全局 store，且 `App.vue` 未导入 `js-cookie`，存在运行错误风险。
- **路由/页面**: 路由守卫硬编码在 `router/index.js`，没有根据角色动态控制；页面间复用极少，`MyProfile`、`Order` 等直接在组件内部发请求并处理格式，缺乏数据层抽象。
- **UI/UX**: 大量默认 Muse UI 样式，登录/主页缺少更现代的版式与响应式处理；没有全局主题/色彩规范，也缺 loading/empty states。
- **测试与可维护性**: 无单元或端到端测试，`package.json` 无相关脚本；缺乏 README/运行说明。

> 以上问题构成 Phase 2 的具体任务清单：重整依赖脚本、重构 request/认证逻辑、补充状态管理与校验、重新设计关键页面并配套测试。

## Phase 3: Frontend `hotel-manager` Review & Refresh

1. **Build/Dependencies**
   - 同样沿用现有依赖版本，只在配置层面优化打包速度与 SVG/Icon 管线（例如更好地拆分 webpack loader 配置）。
2. **Architecture**
   - Strengthen store modules (permissions, dynamic routes) and API abstraction.
   - Improve form validation & optimistic updates for admin actions (room edits, order status changes).
3. **UI/UX Enhancements**
   - Introduce more intentional layout (dashboard cards, analytics, tables with clear hierarchy).
   - Add consistent color scheme distinct from customer app; purposeful typography; background treatments, transitions for panel loads.
4. **Testing**
   - Jest/component tests for shared widgets (Breadcrumb, Hamburger, Room forms).
   - E2E flows for typical admin tasks (login, approve order, edit room, register worker).

### Current `hotel-manager` Findings (2025-12-24)

- **Build/Dependency Concerns**: 基于 vue-admin-template 的 webpack4 配置仍依赖 `node-sass@4.x` 与旧版 Element UI；虽可用但安装过程对 Node 版本极度敏感，需要提供明确的环境约束与本地二进制方案。
- **权限与状态**: 路由守卫仅检查 cookie `admin_id`，Vuex `user` 模块未真正驱动菜单/路由；没有基于角色的细粒度控制，也缺乏 token/会话续期策略。
- **API & 错误处理**: `utils/request.js` 与前台类似，所有 `code!=1000` 都 toast，但后端返回值语义混乱（1001/1100 等）；网络错误强制跳首页，无法区分非致命报错；缺少全局 loading / 操作提示。
- **界面体验**: 继承 Element 默认风格，导航/侧栏与内容区域缺少清晰层级；表格密集、过滤/搜索缺失；无响应式布局，仪表盘/房型页面在小屏体验差；缺动画/反馈。
- **代码组织**: 大量视图直接在组件中处理 API 数据，缺乏 service 层封装；表单验证重复，`add/edit` 页面拷贝多份；SVG/icon pipeline 在开发模式下体积大，需梳理。
- **测试/文档**: 虽有 `npm run lint`，但没有单元或端到端测试；README 缺运行/部署说明，导致新成员难以上手。

> Phase 3 需要在不升级框架的前提下，完善权限/状态、统一请求反馈、重做关键管理界面视觉，并补齐测试与文档，使后台操作体验和可靠性提升。

## Phase 4: Integrated Testing & Verification

1. **Environment Setup**
   - Document required Node, npm, Java, MySQL versions.
   - Provide scripts for seeding DB and launching services locally.
2. **Automated Pipeline**
   - Backend: `mvn test` + coverage; Frontends: `npm run test`, `npm run build`.
   - Optional GitHub Actions workflow running all three packages.
3. **Manual Regression Checklist**
   - Guest flows: register → login → browse rooms → book → view order → cancel.
   - Admin flows: login → dashboard stats → manage rooms/types → approve/check-in/out → worker/user management.
   - Edge cases: invalid logins, duplicate registrations, network errors, device responsiveness.

## Phase 5: Documentation & Handoff

- Update READMEs for each package (setup, scripts, env vars, deployment instructions).
- Summarize optimizations, new UI guidelines, testing coverage.
- Note known limitations and future enhancements (e.g., role-based access refinement, analytics, notification system).

## Execution Order Summary

1. Backend refactors/tests (Phase 1).
2. Customer frontend modernization/tests (Phase 2).
3. Admin frontend modernization/tests (Phase 3).
4. Integrated regression + CI setup (Phase 4).
5. Documentation & handoff (Phase 5).

This phased plan ensures we touch every part of the stack methodically while keeping quality under control. Execute each phase only after previous one passes agreed acceptance tests.
