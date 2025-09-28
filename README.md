# 智慧学院 - 企业级在线课程平台

> 基于Spring Boot + Vue3的专业在线课程学习系统，专注企业级技能认证培训

[![技术栈](https://img.shields.io/badge/技术栈-Spring%20Boot%20%2B%20Vue3-blue.svg)](https://github.com/ZIS-SUN/online-course)
[![版本](https://img.shields.io/badge/版本-v2.0-green.svg)](https://github.com/ZIS-SUN/online-course/releases)
[![许可证](https://img.shields.io/badge/许可证-MIT-orange.svg)](LICENSE)

## 🌟 项目简介

智慧学院是一个现代化的企业级在线课程平台，提供专业的技能认证培训体验。系统支持高清视频播放、智能进度跟踪、专业课程管理等企业级功能。

### ✨ 核心特性

🎯 **专业认证体系** - 企业级技能认证，权威专业认可
📚 **2,500+ 专业课程** - 涵盖前沿技术与行业实践
👥 **150K+ 认证学员** - 专业学习社区与经验分享
🏆 **98% 企业认可度** - 行业领先的培训质量标准
🎥 **高清视频教学** - 4K画质，沉浸式学习体验
🤖 **AI智能推荐** - 个性化学习路径定制

## 🏗️ 系统架构

### 技术栈
- **后端**: Spring Boot 3.1.5 + MySQL 8.0 + JWT + Spring Security
- **前端**: Vue 3 + Vite + Element Plus + Video.js
- **数据库**: MySQL 8.0
- **认证**: JWT Token + 角色权限控制

### 架构设计
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Vue3 前端      │    │  Spring Boot    │    │   MySQL 数据库   │
│   智慧学院UI     │◄──►│   RESTful API   │◄──►│   课程数据管理   │
│   现代化交互     │    │   业务逻辑处理   │    │   用户信息存储   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 功能模块

### 📱 学员端功能
- ✅ **用户认证** - JWT安全登录，密码加密存储
- ✅ **专业课程** - 分类浏览，高级搜索筛选
- ✅ **智能推荐** - AI个性化课程推荐
- ✅ **视频学习** - 高清播放，倍速调节，断点续播
- ✅ **进度跟踪** - 智能记录，学习分析
- ✅ **认证管理** - 个人认证档案，技能证书

### 🛠️ 管理员功能
- ✅ **课程管理** - 专业课程发布与维护
- ✅ **内容管理** - 章节视频组织与编辑
- ✅ **用户管理** - 学员信息与权限控制
- ✅ **数据分析** - 学习统计与行为分析
- ✅ **系统监控** - 平台运行状态监控

## 🎯 快速开始

### 环境要求
- **JDK**: 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Maven**: 3.6+

### 一键启动脚本
```bash
# 克隆项目
git clone https://github.com/ZIS-SUN/online-course.git
cd online-course

# 执行一键启动脚本
chmod +x start.sh
./start.sh
```

### 手动启动

#### 📊 数据库配置
```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE online_course CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 导入初始数据（可选）
mysql -u root -p online_course < database-init.sql
```

#### 🖥️ 后端启动
```bash
cd online-course-backend
mvn clean install
mvn spring-boot:run
```
🌐 后端服务: http://localhost:8080

#### 🎨 前端启动
```bash
cd online-course-frontend
npm install
npm run dev
```
🌐 前端服务: http://localhost:5173

## 🔐 默认账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | `admin` | `admin123` | 全部管理权限 |
| 测试用户 | `testuser` | `user123` | 学员权限 |

## 📁 项目结构

```
智慧学院/
├── 📂 online-course-backend/        # Spring Boot 后端
│   ├── 📂 src/main/java/com/example/learn/
│   │   ├── 🔐 auth/                 # 认证授权模块
│   │   ├── 📚 course/               # 课程管理模块
│   │   ├── 📈 progress/             # 学习进度模块
│   │   ├── 👤 user/                 # 用户管理模块
│   │   ├── 📢 announcement/         # 公告模块
│   │   └── ⚙️ config/               # 系统配置
│   └── 📂 src/main/resources/       # 配置文件
│
├── 📂 online-course-frontend/       # Vue3 前端
│   ├── 📂 src/
│   │   ├── 🔌 api/                  # API接口封装
│   │   ├── 🧩 components/           # 可复用组件
│   │   ├── 📱 views/                # 页面视图
│   │   ├── 🗃️ stores/               # Pinia状态管理
│   │   ├── 🛣️ router/               # Vue Router路由
│   │   └── 🎨 styles/               # 样式系统
│   └── 📦 package.json
│
├── 📋 start.sh                     # 一键启动脚本
├── 🔐 测试账号.md                   # 测试账号说明
└── 📖 README.md                    # 项目文档
```

## 🔗 相关链接

- 📚 **API文档**: http://localhost:8080/swagger-ui.html
- 🎯 **在线演示**: [待部署]
- 📝 **更新日志**: [CHANGELOG.md]
- 🐛 **问题反馈**: [GitHub Issues](https://github.com/ZIS-SUN/online-course/issues)

## 🛡️ 核心技术特性

### 🎥 视频播放系统
- **智能进度记录** - 每5秒自动保存，防数据丢失
- **断点续播** - 用户友好的续播体验
- **防跳过机制** - 记录最大观看位置
- **完成度判定** - 90%观看标记完成

### 🔒 安全认证体系
- **JWT Token认证** - 无状态安全验证
- **角色权限控制** - 细粒度权限管理
- **接口级安全** - Spring Security集成
- **前端路由守卫** - 页面访问控制

### 🎨 现代化UI设计
- **响应式设计** - 完美适配多端设备
- **专业视觉** - 企业级界面设计
- **流畅动效** - 现代化交互体验
- **无障碍支持** - 友好的用户体验

## 🌟 最新更新 (v2.0)

### ✨ 全面UI升级
- 🎨 **专业品牌形象** - LearnSpace → 智慧学院
- 📊 **企业级数据** - 2,500+课程，150K+认证学员
- 🏅 **认证体系** - 专业技能认证流程
- 🎯 **用户体验** - 现代化交互设计

### 🚀 功能增强
- 🔍 **智能搜索** - 高级筛选与推荐
- 📈 **学习分析** - 深度学习数据分析
- 💫 **性能优化** - 更快的加载与响应
- 🛡️ **安全提升** - 增强的数据保护

## 📈 开发规划

### 🎯 短期目标
- [ ] Redis缓存集成
- [ ] 对象存储服务(OSS)
- [ ] 实时通讯功能
- [ ] 移动端APP

### 🌐 长期愿景
- [ ] 微服务架构重构
- [ ] AI智能教学助手
- [ ] 多语言国际化
- [ ] 企业级私有部署

## 🤝 贡献指南

欢迎提交 Pull Request 或 Issue！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 开源协议

本项目采用 [MIT](LICENSE) 开源协议

## 🙏 致谢

感谢所有为智慧学院项目贡献的开发者和用户！

---

<div align="center">

**🎓 智慧学院 - 让学习更智能，让成长更专业**

[🌐 官网](https://github.com/ZIS-SUN/online-course) | [📚 文档](https://github.com/ZIS-SUN/online-course/wiki) | [💬 社区](https://github.com/ZIS-SUN/online-course/discussions)

</div>