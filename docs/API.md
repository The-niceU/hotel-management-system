# API 文档概览

## 基础信息

- **Base URL**: `http://localhost:8080/ho-api`
- **Content-Type**: `application/x-www-form-urlencoded`
- **响应格式**: JSON

## 响应码约定

| 代码 | 说明                        |
| ---- | --------------------------- |
| 1000 | 成功                        |
| 1001 | 用户未登录（需重新登录）    |
| 1100 | 管理员未登录（管理端）      |
| 其他 | 业务错误，查看 message 字段 |

## 主要模块

### 1. 公共模块（Common）

#### 登录

- **POST** `/login`
- 参数：`username`, `password`
- 返回：`user_id`, `username`, `session_id`

#### 注册

- **POST** `/register`
- 参数：`username`, `password`, `telephone`
- 返回：`user_id`

#### 酒店信息

- **GET** `/hotel/info`
- 参数：无
- 返回：酒店基本信息

### 2. 用户模块（User）

#### 获取个人信息

- **GET** `/user/profile`
- 返回：用户信息

#### 获取房间类型列表

- **GET** `/room/types`
- 返回：所有房间类型

#### 获取可用房间

- **GET** `/room/available`
- 参数：`checkInDate`, `checkOutDate`
- 返回：可用房间列表

#### 创建预订

- **POST** `/order/create`
- 参数：`roomId`, `checkInDate`, `checkOutDate`, `guestName`, `telephone`
- 返回：`orderId`

#### 获取我的订单

- **GET** `/user/orders`
- 返回：用户所有订单

### 3. 管理员模块（Admin）

#### 管理员登录

- **POST** `/admin/login`
- 参数：`username`, `password`
- 返回：`admin_id`, `admin_name`, `role`, `session_id`

#### 订单管理

- **GET** `/admin/orders` - 获取所有订单
- **POST** `/admin/order/checkout` - 结账
- **POST** `/admin/order/checkin` - 入住登记

#### 房间管理

- **GET** `/admin/rooms` - 获取所有房间
- **POST** `/admin/room/add` - 添加房间
- **PUT** `/admin/room/update` - 更新房间
- **DELETE** `/admin/room/delete` - 删除房间

#### 房间类型管理

- **GET** `/admin/room-types` - 获取所有房间类型
- **POST** `/admin/room-type/add` - 添加房间类型
- **PUT** `/admin/room-type/update` - 更新房间类型
- **DELETE** `/admin/room-type/delete` - 删除房间类型

#### 员工管理

- **GET** `/admin/workers` - 获取所有员工
- **POST** `/admin/worker/add` - 添加员工
- **PUT** `/admin/worker/update` - 更新员工
- **DELETE** `/admin/worker/delete` - 删除员工

## 常见业务流程

### 用户预订流程

1. 用户登录 → `/login`
2. 查看可用房间 → `/room/types` + `/room/available`
3. 创建订单 → `/order/create`
4. 查看订单 → `/user/orders`

### 管理员处理入住流程

1. 管理员登录 → `/admin/login`
2. 查看待入住订单 → `/admin/orders`
3. 入住登记 → `/admin/order/checkin`
4. 结账 → `/admin/order/checkout`

## 错误处理示例

请求失败时返回：

```json
{
  "code": 1001,
  "message": "用户未登录，请重新登录",
  "data": null
}
```

## 更多详情

详细 API 规格请查看后端 Controller 源代码：

- 用户 API: `backend/api-server/src/main/java/cn/mafangui/hotel/controller/user/`
- 管理 API: `backend/api-server/src/main/java/cn/mafangui/hotel/controller/worker/`
- 公共 API: `backend/api-server/src/main/java/cn/mafangui/hotel/controller/common/`
