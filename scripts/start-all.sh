#!/bin/bash

# Hotel Management System - 一键启动脚本（Linux/Mac）
# 确保你已安装: Java 8, MySQL 8.0+, Node.js 14+

echo ""
echo "============================================"
echo "  Hotel Management System - 一键启动"
echo "============================================"
echo ""

# 检查 Java
echo "[INFO] 检查 Java 版本..."
if ! command -v java &> /dev/null; then
    echo "[ERROR] 未找到 Java，请先安装 Java 8"
    exit 1
fi
java -version

# 检查 MySQL
echo "[INFO] 检查 MySQL..."
if ! mysql -uroot -p123456 -e "SELECT 1" &> /dev/null; then
    echo "[ERROR] MySQL 连接失败，请检查 MySQL 服务是否运行"
    echo "        默认配置: root/123456"
    exit 1
fi

# 检查 Node.js
echo "[INFO] 检查 Node.js 版本..."
if ! command -v node &> /dev/null; then
    echo "[ERROR] 未找到 Node.js"
    exit 1
fi
node -v

echo ""
echo "[INFO] 所有依赖检查完成！"
echo ""

# 获取脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "[INFO] 项目根目录: $PROJECT_ROOT"
echo ""

# 启动后端
echo "============================================"
echo "[1/3] 启动后端服务 (Spring Boot)..."
echo "============================================"
cd "$PROJECT_ROOT/backend/api-server"
mvn spring-boot:run &
BACKEND_PID=$!
sleep 5

# 启动用户前端
echo "============================================"
echo "[2/3] 启动用户前台 (web-app)..."
echo "============================================"
cd "$PROJECT_ROOT/frontend/web-app"
if [ ! -d "node_modules" ]; then
    echo "[INFO] 首次启动，安装依赖..."
    npm install
fi
npm run dev &
APP_PID=$!
sleep 3

# 启动管理后台
echo "============================================"
echo "[3/3] 启动管理后台 (admin-web)..."
echo "============================================"
cd "$PROJECT_ROOT/admin/admin-web"
if [ ! -d "node_modules" ]; then
    echo "[INFO] 首次启动，安装依赖..."
    npm install
fi
npm run dev &
MANAGER_PID=$!

echo ""
echo "============================================"
echo "所有服务已启动！"
echo "============================================"
echo ""
echo "后台进程 ID:"
echo "  - 后端: $BACKEND_PID"
echo "  - 用户前台: $APP_PID"
echo "  - 管理后台: $MANAGER_PID"
echo ""
echo "服务地址："
echo "  - 后端 API: http://localhost:8080/ho-api"
echo "  - 用户前台: http://localhost:8888"
echo "  - 管理后台: http://localhost:9528"
echo ""
echo "停止所有服务："
echo "  kill $BACKEND_PID $APP_PID $MANAGER_PID"
echo ""
echo "或按 Ctrl+C 停止"
echo "============================================"
echo ""

# 等待所有后台进程
wait
