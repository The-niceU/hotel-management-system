@echo off
REM Windows 一键启动脚本 - 启动所有三个服务
REM 确保你已安装: Java 8, MySQL 8.0+, Node.js 14+

echo.
echo ============================================
echo  Hotel Management System - 一键启动
echo ============================================
echo.

REM 检查 Java
echo [INFO] 检查 Java 版本...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] 未找到 Java，请先安装 Java 8
    pause
    exit /b 1
)

REM 检查 MySQL
echo [INFO] 检查 MySQL...
mysql -uroot -p123456 -e "SELECT 1" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] MySQL 连接失败，请检查 MySQL 服务是否运行
    echo        默认配置: root/123456
    pause
    exit /b 1
)

REM 检查 Node.js
echo [INFO] 检查 Node.js 版本...
node -v
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] 未找到 Node.js
    pause
    exit /b 1
)

echo.
echo [INFO] 所有依赖检查完成！
echo.

REM 获取当前脚本目录
set SCRIPT_DIR=%~dp0
set PROJECT_ROOT=%SCRIPT_DIR%..

echo [INFO] 项目根目录: %PROJECT_ROOT%
echo.

REM 启动后端
echo ============================================
echo [1/3] 启动后端服务 (Spring Boot)...
echo ============================================
cd /d "%PROJECT_ROOT%\backend\api-server"
start "Hotel Backend" cmd /k "mvn spring-boot:run"
timeout /t 5

REM 启动用户前端
echo ============================================
echo [2/3] 启动用户前台 (web-app)...
echo ============================================
cd /d "%PROJECT_ROOT%\frontend\web-app"
if not exist "node_modules" (
    echo [INFO] 首次启动，安装依赖...
    call npm install
)
start "Hotel App" cmd /k "npm run dev"
timeout /t 3

REM 启动管理后台
echo ============================================
echo [3/3] 启动管理后台 (admin-web)...
echo ============================================
cd /d "%PROJECT_ROOT%\admin\admin-web"
if not exist "node_modules" (
    echo [INFO] 首次启动，安装依赖...
    call npm install
)
start "Hotel Manager" cmd /k "npm run dev"

echo.
echo ============================================
echo 所有服务已启动！
echo ============================================
echo.
echo 请在新打开的窗口中查看各服务输出
echo.
echo 服务地址：
echo  - 后端 API: http://localhost:8080/ho-api
echo  - 用户前台: http://localhost:8888
echo  - 管理后台: http://localhost:9528
echo.
echo 关闭任一窗口即可停止相应服务
echo ============================================
echo.

pause
