<template>
  <div class="login-container">
    <div class="login-left">
      <div class="login-branding">
        <div class="brand-logo">
          <div class="logo-container">
            <div class="logo-icon">
              <el-icon :size="48">
                <School />
              </el-icon>
            </div>
            <div class="logo-effects">
              <div class="logo-ring"></div>
              <div class="logo-dots">
                <span class="dot dot-1"></span>
                <span class="dot dot-2"></span>
                <span class="dot dot-3"></span>
              </div>
            </div>
          </div>
        </div>
        <h1 class="brand-title">
          <span class="title-main">智慧</span><span class="title-accent">学院</span>
        </h1>
        <p class="brand-subtitle">国家认证的专业技能培训平台，与全球500+知名企业合作</p>
        <div class="feature-list">
          <div class="feature-item" data-feature="video">
            <div class="feature-icon">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="feature-content">
              <h4>4K超清视频教学</h4>
              <p>专业制作，企业级教学体验</p>
            </div>
          </div>
          <div class="feature-item" data-feature="course">
            <div class="feature-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="feature-content">
              <h4>ISO认证证书</h4>
              <p>国际标准认证，全球通用</p>
            </div>
          </div>
          <div class="feature-item" data-feature="ai">
            <div class="feature-icon">
              <el-icon><Cpu /></el-icon>
            </div>
            <div class="feature-content">
              <h4>AI智能评估</h4>
              <p>专业能力测评与职业规划</p>
            </div>
          </div>
        </div>

        <div class="stats-container">
          <div class="stat-item">
            <div class="stat-number">2,500+</div>
            <div class="stat-label">企业级课程</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">150K+</div>
            <div class="stat-label">认证学员</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">98.5%</div>
            <div class="stat-label">就业成功率</div>
          </div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <el-card class="login-card">
        <div class="card-header">
          <h2 class="login-title">企业级学习平台</h2>
          <p class="login-subtitle">专业技能认证·职业发展提升·行业精英培养</p>
        </div>
        <div class="demo-accounts">
          <div class="demo-header">
            <el-icon><Key /></el-icon>
            <span>快速登录</span>
          </div>
          <div class="demo-buttons">
            <el-button class="demo-btn" @click="fillAdmin">
              <el-icon><UserFilled /></el-icon>
              <div class="btn-content">
                <span class="role">管理员</span>
                <span class="credentials">admin / password</span>
              </div>
            </el-button>
            <el-button class="demo-btn" @click="fillStudent">
              <el-icon><User /></el-icon>
              <div class="btn-content">
                <span class="role">学生</span>
                <span class="credentials">student1 / password</span>
              </div>
            </el-button>
          </div>
        </div>
        <el-form ref="loginForm" :model="form" :rules="rules" label-width="0" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              @click="handleLogin"
              :loading="loading"
              class="login-btn"
            >
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
              <el-icon v-if="!loading" class="btn-icon"><Right /></el-icon>
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <div class="footer-links">
            <span>还没有账号？</span>
            <router-link to="/register" class="register-link">
              立即注册
              <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div class="footer-help">
            <a href="#" class="help-link">忘记密码？</a>
            <span class="divider">|</span>
            <a href="#" class="help-link">联系客服</a>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loginForm = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await loginForm.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const fillAdmin = () => {
  form.username = 'admin'
  form.password = 'password'
}

const fillStudent = () => {
  form.username = 'student1'
  form.password = 'password'
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="20" cy="20" r="1" fill="white" opacity="0.05"/><circle cx="80" cy="40" r="1" fill="white" opacity="0.05"/><circle cx="40" cy="80" r="1" fill="white" opacity="0.05"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
}

.login-left {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 60px;
  position: relative;
  z-index: 1;
}

.login-branding {
  max-width: 500px;
  color: white;
  text-align: center;
}

.brand-logo {
  margin-bottom: 40px;
  position: relative;
}

.logo-container {
  position: relative;
  display: inline-block;
  animation: float 3s ease-in-out infinite;
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 2;
}

.logo-effects {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

.logo-ring {
  width: 100px;
  height: 100px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: rotate 10s linear infinite;
}

.logo-dots {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.dot {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: orbit 8s linear infinite;
}

.dot-1 {
  animation-delay: 0s;
  transform: rotate(0deg) translateX(60px);
}

.dot-2 {
  animation-delay: -2.67s;
  transform: rotate(120deg) translateX(60px);
}

.dot-3 {
  animation-delay: -5.33s;
  transform: rotate(240deg) translateX(60px);
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes orbit {
  from { transform: rotate(0deg) translateX(60px) rotate(0deg); }
  to { transform: rotate(360deg) translateX(60px) rotate(-360deg); }
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: -1px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.title-main {
  background: linear-gradient(45deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-accent {
  background: linear-gradient(45deg, #ffd700 0%, #ffed4e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
}

.title-accent::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(45deg, #ffd700 0%, #ffed4e 100%);
  border-radius: 2px;
  animation: shine 2s ease-in-out infinite;
}

@keyframes shine {
  0%, 100% { opacity: 0.6; transform: scaleX(1); }
  50% { opacity: 1; transform: scaleX(1.1); }
}

.brand-subtitle {
  font-size: 18px;
  margin-bottom: 60px;
  opacity: 0.9;
  line-height: 1.6;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.feature-item:hover {
  transform: translateX(8px) translateY(-2px);
  background: rgba(255, 255, 255, 0.18);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.feature-item:hover::before {
  left: 100%;
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
  transition: all 0.3s;
}

.feature-item:hover .feature-icon {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.3);
}

.feature-content {
  flex: 1;
}

.feature-content h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.feature-content p {
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.4;
}

.stats-container {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-top: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #ffd700;
  margin-bottom: 4px;
  animation: countUp 1s ease-out;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

@keyframes countUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 1;
}

.login-card {
  width: 100%;
  max-width: 480px;
  padding: 50px 40px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 30px 60px rgba(0,0,0,0.12);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 28px;
  color: #2c3e50;
  font-weight: 700;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
  font-weight: 400;
}

.demo-accounts {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 30px;
  border: 1px solid #dee2e6;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
}

.demo-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #6c757d;
  font-weight: 600;
  font-size: 14px;
}

.demo-header .el-icon {
  color: #409eff;
}

.demo-buttons {
  display: flex;
  gap: 12px;
}

.demo-btn {
  flex: 1;
  height: auto;
  padding: 12px 16px;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  background: white;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.demo-btn:hover {
  border-color: #409eff;
  background: #f0f8ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.demo-btn .el-icon {
  color: #409eff;
  font-size: 16px;
}

.btn-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.btn-content .role {
  font-weight: 600;
  color: #2c3e50;
  font-size: 13px;
}

.btn-content .credentials {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 11px;
  color: #6c757d;
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 25px;
}

.login-form :deep(.el-input) {
  height: 52px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border: 2px solid #e1e6ef;
  transition: all 0.3s;
  padding: 0 16px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(0, 86, 211, 0.1);
}

.login-form :deep(.el-input__inner) {
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}

.login-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: var(--primary);
  border: none;
  box-shadow: 0 6px 20px rgba(0, 86, 211, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-btn:hover {
  transform: translateY(-2px);
  background: var(--primary-dark);
  box-shadow: 0 8px 25px rgba(0, 86, 211, 0.4);
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:active {
  transform: translateY(0);
}

.btn-icon {
  transition: transform 0.3s;
}

.login-btn:hover .btn-icon {
  transform: translateX(2px);
}

.login-footer {
  margin-top: 30px;
}

.footer-links {
  text-align: center;
  margin-bottom: 16px;
  color: #8492a6;
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.register-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 6px;
}

.register-link:hover {
  color: #667eea;
  background: rgba(64, 158, 255, 0.05);
}

.register-link .el-icon {
  transition: transform 0.3s;
}

.register-link:hover .el-icon {
  transform: translateX(2px);
}

.footer-help {
  text-align: center;
  font-size: 13px;
  color: #bdc3c7;
}

.help-link {
  color: #95a5a6;
  text-decoration: none;
  transition: color 0.3s;
  padding: 4px 8px;
  border-radius: 4px;
}

.help-link:hover {
  color: #409eff;
  background: rgba(64, 158, 255, 0.05);
}

.divider {
  margin: 0 8px;
  color: #dee2e6;
}

@media (max-width: 1200px) {
  .login-left {
    padding: 60px 40px;
  }

  .brand-title {
    font-size: 40px;
  }

  .brand-subtitle {
    font-size: 16px;
  }
}

@media (max-width: 1024px) {
  .login-container {
    flex-direction: column;
  }

  .login-left {
    flex: none;
    min-height: 40vh;
    padding: 40px 20px;
  }

  .brand-title {
    font-size: 36px;
  }

  .feature-list {
    gap: 16px;
  }

  .feature-item {
    padding: 16px;
  }

  .feature-icon {
    width: 40px;
    height: 40px;
  }

  .feature-content h4 {
    font-size: 15px;
  }

  .feature-content p {
    font-size: 12px;
  }

  .stats-container {
    gap: 12px;
    padding: 16px;
  }

  .stat-number {
    font-size: 20px;
  }

  .demo-buttons {
    flex-direction: column;
  }

  .login-right {
    flex: none;
    padding: 20px;
  }

  .login-card {
    padding: 40px 30px;
  }
}

@media (max-width: 768px) {
  .login-left {
    min-height: 30vh;
    padding: 30px 20px;
  }

  .brand-title {
    font-size: 28px;
  }

  .feature-list {
    flex-direction: column;
    gap: 12px;
  }

  .login-card {
    padding: 30px 20px;
  }

  .demo-btn {
    padding: 10px 12px;
  }

  .btn-content .role {
    font-size: 12px;
  }

  .btn-content .credentials {
    font-size: 10px;
  }
}
</style>