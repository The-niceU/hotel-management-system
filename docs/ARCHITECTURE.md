# 系统架构说明

## 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端层 (Frontend)                        │
├──────────────────────────┬──────────────────────────────────┤
│   用户前台 (hotel_app)   │  管理后台 (hotel-manager)        │
│   Vue 2 + Muse UI       │  Vue 2 + Element UI              │
│   Port: 8888            │  Port: 9528                      │
└──────────────┬───────────┴──────────────────┬────────────────┘
               │                              │
               │         HTTP/AJAX             │
               │                              │
┌──────────────┴──────────────────────────────┴────────────────┐
│              API 网关 (API Gateway)                            │
│              Base URL: http://localhost:8080/ho-api          │
│              Content-Type: application/x-www-form-urlencoded │
└───────────────────────────┬────────────────────────────────────┘
                            │
┌───────────────────────────┴────────────────────────────────────┐
│               后端层 (Backend) - Spring Boot                    │
│                                                                │
│  ┌────────────────────────────────────────────────────────┐   │
│  │  Controller 层                                          │   │
│  │  ├── CommonController (登录、注册、酒店信息)            │   │
│  │  ├── UserController (用户相关)                         │   │
│  │  ├── WorkerController (管理员相关)                     │   │
│  │  └── 其他 Controller                                  │   │
│  └────────────────────────────────────────────────────────┘   │
│                          ↓                                    │
│  ┌────────────────────────────────────────────────────────┐   │
│  │  Service 层 (业务逻辑)                                 │   │
│  │  ├── UserService                                       │   │
│  │  ├── OrderService                                      │   │
│  │  ├── RoomService                                       │   │
│  │  └── 其他 Service                                     │   │
│  └────────────────────────────────────────────────────────┘   │
│                          ↓                                    │
│  ┌────────────────────────────────────────────────────────┐   │
│  │  DAO 层 (MyBatis Mapper)                               │   │
│  │  └── 数据访问对象                                       │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                │
│  Port: 8080                                                   │
│  Java 8 + Spring Boot 2.0.5 + MyBatis                        │
└───────────────────────────┬────────────────────────────────────┘
                            │
┌───────────────────────────┴────────────────────────────────────┐
│              数据库层 (Database)                                │
│              MySQL 8.0+                                        │
│              Database: hotel                                   │
│                                                                │
│  主要表:                                                       │
│  ├── user (用户)                                              │
│  ├── hotel (酒店信息)                                         │
│  ├── room_type (房间类型)                                     │
│  ├── room_info (房间信息)                                     │
│  ├── order_info (订单)                                        │
│  ├── check_in (入住)                                          │
│  ├── worker (员工)                                            │
│  └── 其他业务表                                                │
└────────────────────────────────────────────────────────────────┘
```

## 模块划分

### 1. 后端 (backend/api-server)

**主要功能模块：**

| 模块       | 说明                     | Controller          | Service        |
| ---------- | ------------------------ | ------------------- | -------------- |
| 认证       | 用户登录、注册、会话管理 | CommonController    | UserService    |
| 用户管理   | 用户信息、订单、支付     | UserController      | UserService    |
| 订单管理   | 订单创建、查询、取消     | UserOrderController | OrderService   |
| 房间管理   | 房间信息、类型、价格     | UserRoomController  | RoomService    |
| 入住管理   | 入住登记、退房结账       | OpCheckInController | CheckInService |
| 管理员功能 | 后台管理、统计           | AdminController     | AdminService   |
| 员工管理   | 员工信息、权限           | OpUserController    | WorkerService  |

**技术栈：**

- 框架：Spring Boot 2.0.5
- ORM：MyBatis
- 数据库：MySQL 8.0
- 构建：Maven
- Java 版本：Java 8

**关键配置：**

- 文件：`src/main/resources/application.yml`
- 端口：8080
- Context Path：`/ho-api`
- 数据库连接：`localhost:3306/hotel`

### 2. 用户前台 (frontend/web-app)

**主要功能：**

- 酒店信息浏览
- 用户注册/登录
- 房间预订
- 订单查看
- 个人资料管理

**技术栈：**

- 框架：Vue 2
- UI 库：Muse UI
- 构建：Webpack
- 包管理：npm
- Node 版本：14+

**关键文件：**

- 配置：`config/dev.env.js`、`config/prod.env.js`
- 入口：`src/main.js`
- 路由：`src/router/index.js`
- API：`src/api/*.js`

**端口：8888**

### 3. 管理后台 (admin/admin-web)

**主要功能：**

- 订单管理
- 入住结账
- 房间管理
- 员工管理
- 业务统计

**技术栈：**

- 框架：Vue 2
- UI 库：Element UI
- 状态管理：Vuex
- 构建：Webpack
- 包管理：npm
- Node 版本：14+

**关键文件：**

- 配置：`config/dev.env.js`、`config/prod.env.js`
- 入口：`src/main.js`
- 路由：`src/router/router.js`
- 状态：`src/store/`
- API：`src/api/*.js`

**端口：9528**

## 数据库设计

### 核心表结构

```
user (用户表)
├── user_id (主键)
├── username (用户名)
├── password (密码)
├── telephone (电话)
└── ... 其他字段

order_info (订单表)
├── order_id (主键)
├── user_id (外键)
├── room_id (外键)
├── check_in_date (入住日期)
├── check_out_date (退房日期)
├── order_status (订单状态)
└── ... 其他字段

room_info (房间表)
├── room_id (主键)
├── room_type_id (外键)
├── room_number (房间号)
├── room_status (房间状态)
└── ... 其他字段

room_type (房间类型表)
├── room_type_id (主键)
├── room_type_name (类型名)
├── room_price (价格)
└── ... 其他字段
```

## 流程设计

### 用户预订流程

```
1. 用户打开前台 → 浏览房间
2. 选择日期范围 → 查询可用房间
3. 选择房间 → 点击预订
4. 填写预订信息 → 确认
5. 系统创建订单 → 返回订单号
6. 用户支付（可选实现）
7. 订单完成
```

### 管理员处理流程

```
1. 管理员打开后台 → 登录
2. 查看待入住订单
3. 确认客人身份 → 登记入住
4. 客人退房 → 确认退房
5. 结账计费 → 记录
6. 订单完成
```

## 安全性设计

- 会话管理：基于 Cookie/Session
- 密码：存储（当前明文，生产环境应加密）
- 跨域：CORS 配置（GlobalCorsConfig）
- 权限：基于 Role 和 Session

## 扩展点

未来可以扩展的功能：

1. **支付集成**：集成支付宝/微信支付
2. **邮件通知**：订单确认、取消通知
3. **短信通知**：发送验证码、订单信息
4. **评价系统**：用户对酒店的评价
5. **积分系统**：会员积分兑换
6. **数据分析**：报表、统计
7. **Docker 部署**：容器化部署
8. **CI/CD**：自动化部署流程

## 部署架构（可选）

```
┌─────────────┐
│   Nginx     │ (反向代理、静态文件)
└──────┬──────┘
       │
   ┌───┴─────┬──────────┐
   │         │          │
┌──▼──┐  ┌──▼──┐   ┌───▼──┐
│前台 │  │后台 │   │后端 │
│:80 │  │:81  │   │:8080 │
└─────┘  └─────┘   └──┬───┘
                      │
                   ┌──▼──┐
                   │MySQL│
                   └─────┘
```

## 监控和日志

- 日志：`spring.log`（Spring Boot 默认日志）
- 错误追踪：通过 API 响应码区分
- 性能监控：可集成 Spring Boot Actuator

## 性能优化建议

1. **数据库优化**：
   - 添加适当索引
   - 查询优化

2. **缓存**：
   - 集成 Redis 缓存热点数据

3. **CDN**：
   - 前端静态资源上 CDN

4. **负载均衡**：
   - 后端多实例部署

5. **消息队列**：
   - 异步处理订单等业务
