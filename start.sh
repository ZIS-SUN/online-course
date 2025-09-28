#!/bin/bash

echo "在线课程学习系统启动脚本"
echo "========================"

# 检查MySQL是否运行
echo "检查MySQL服务..."
if ! mysqladmin ping -h"localhost" -u"root" -p"1234" --silent; then
    echo "错误: MySQL服务未运行或连接失败"
    echo "请确保MySQL已启动并且账号密码正确(root/1234)"
    exit 1
fi

# 初始化数据库
echo "初始化数据库..."
mysql -u root -p1234 < online-course-backend/database-init.sql 2>/dev/null
echo "数据库初始化完成"

# 启动后端服务
echo "启动后端服务..."
cd online-course-backend
mvn clean compile &
BACKEND_PID=$!
mvn spring-boot:run &
cd ..

# 等待后端启动
echo "等待后端服务启动..."
sleep 10

# 检查后端是否启动成功
if curl -s http://localhost:8080/api > /dev/null; then
    echo "后端服务启动成功: http://localhost:8080"
else
    echo "警告: 后端服务可能未完全启动"
fi

# 启动前端服务
echo "启动前端服务..."
cd online-course-frontend
npm run dev &
FRONTEND_PID=$!
cd ..

echo ""
echo "========================"
echo "系统启动完成!"
echo "前端地址: http://localhost:5173"
echo "后端API地址: http://localhost:8080/api"
echo "Swagger文档: http://localhost:8080/api/swagger-ui.html"
echo ""
echo "默认账号:"
echo "管理员: admin / admin123"
echo "测试用户: testuser / user123"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
wait