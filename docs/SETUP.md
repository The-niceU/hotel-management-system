# 开发环境搭建指南

## 前置要求

### 系统要求

- Windows 10+ / macOS 10.14+ / Ubuntu 18.04+
- 4GB RAM 以上

### 必须安装

- **Java 8** (`jdk1.8` 或 `jdk-1.8`)
- **MySQL 8.0+**
- **Node.js 14.x** 及以上
- **Git**

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/The-niceU/hotel-management-system.git
cd hotel-management-system
```

### 2. 初始化数据库

#### 创建数据库

```bash
mysql -uroot -p
```

在 MySQL 命令行中执行：

```sql
CREATE DATABASE hotel DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hotel;
source backend/api-server/hotel.sql;
exit
```

或者直接导入：

```bash
mysql -uroot -p hotel < backend/api-server/hotel.sql
```

### 3. 启动后端服务

```bash
cd backend/api-server
# 确保使用 Java 8
java -version

# 编译并运行
mvn clean spring-boot:run
```

后端地址：`http://localhost:8080/ho-api`

### 4. 启动前端（用户端）

```bash
cd frontend/web-app
npm install
npm run dev
```

用户前台地址：`http://localhost:8888`

### 5. 启动管理后台

```bash
cd admin/admin-web
npm install
npm run dev
```

管理后台地址：`http://localhost:9528`

## 故障排除

### Java 版本问题

如果遇到 `module java.base does not opens` 错误，说明用的是 Java 9+，需要改用 Java 8：

```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-1.8
set PATH=%JAVA_HOME%\bin;%PATH%

# Linux/Mac
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH
```

### MySQL 连接失败

检查配置文件：`backend/api-server/src/main/resources/application.yml`

默认配置：

- 主机：`localhost:3306`
- 用户：`root`
- 密码：`123456`
- 数据库：`hotel`

### npm 安装慢

使用国内镜像：

```bash
npm install --registry=https://registry.npm.taobao.org
```

## 项目配置

### 后端配置

- 配置文件：`backend/api-server/src/main/resources/application.yml`
- 支持自定义端口、数据库连接等

### 前端配置

- 用户端：`frontend/web-app/config/dev.env.js`
- 管理端：`admin/admin-web/config/dev.env.js`
- API 基地址默认：`http://localhost:8080/ho-api`

## 使用一键启动脚本

### Windows

```bash
scripts\start-all.bat
```

### Linux/Mac

```bash
bash scripts/start-all.sh
```

这会自动启动所有三个服务（后端 + 前端 + 管理端）。

## 建议的开发工具

- **IDE**: VS Code 或 IntelliJ IDEA
- **数据库管理**: Navicat / DBeaver / MySQL Workbench
- **API 测试**: Postman / Insomnia
- **Git**: Git Bash / Sourcetree

## 获取帮助

遇到问题？请提交 Issue 或查看 README.md。
