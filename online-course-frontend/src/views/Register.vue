<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">用户注册</h2>
      <el-form ref="registerForm" :model="form" :rules="rules" label-width="0">
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
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称（选填）"
            prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱（选填）"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleRegister"
            :loading="loading"
            style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const registerForm = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await registerForm.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await authStore.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname || form.username,
      email: form.email
    })
    ElMessage.success('注册成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  position: relative;
  overflow: hidden;
  padding: var(--space-6);
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="20" cy="20" r="1" fill="white" opacity="0.05"/><circle cx="80" cy="40" r="1" fill="white" opacity="0.05"/><circle cx="40" cy="80" r="1" fill="white" opacity="0.05"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
}

.register-card {
  width: 100%;
  max-width: 500px;
  padding: var(--space-12) var(--space-10);
  background: white;
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-2xl);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

:deep(.el-card__body) {
  padding: 0 !important;
}

.register-title {
  text-align: center;
  margin-bottom: var(--space-8);
  font-size: var(--text-3xl);
  color: var(--gray-900);
  font-weight: var(--font-bold);
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

:deep(.el-form-item) {
  margin-bottom: var(--space-6) !important;
}

:deep(.el-input) {
  height: 52px !important;
}

:deep(.el-input__wrapper) {
  border-radius: var(--radius-lg) !important;
  box-shadow: var(--shadow-sm) !important;
  border: 2px solid var(--gray-300) !important;
  transition: var(--transition) !important;
  padding: 0 var(--space-4) !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--primary) !important;
  box-shadow: 0 4px 12px rgba(0, 86, 211, 0.15) !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 3px rgba(0, 86, 211, 0.1) !important;
}

:deep(.el-input__inner) {
  height: 48px !important;
  font-size: var(--text-base) !important;
  font-weight: var(--font-medium) !important;
}

:deep(.el-button--large) {
  height: 52px !important;
  font-size: var(--text-base) !important;
  font-weight: var(--font-semibold) !important;
  border-radius: var(--radius-lg) !important;
  background: var(--primary) !important;
  border: none !important;
  box-shadow: 0 6px 20px rgba(0, 86, 211, 0.3) !important;
  transition: var(--transition) !important;
  position: relative !important;
  overflow: hidden !important;
}

:deep(.el-button--large:hover) {
  transform: translateY(-2px) !important;
  background: var(--primary-dark) !important;
  box-shadow: 0 8px 25px rgba(0, 86, 211, 0.4) !important;
}

:deep(.el-button--large:active) {
  transform: translateY(0) !important;
}

.register-footer {
  text-align: center;
  margin-top: var(--space-8);
  color: var(--gray-600);
  font-size: var(--text-base);
}

.register-footer a {
  color: var(--primary);
  text-decoration: none;
  font-weight: var(--font-semibold);
  transition: color 0.3s;
}

.register-footer a:hover {
  color: var(--primary-light);
}

/* Form validation styling */
:deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1) !important;
}

:deep(.el-form-item__error) {
  color: #ef4444 !important;
  font-size: var(--text-sm) !important;
  margin-top: var(--space-1) !important;
}

/* Loading state */
:deep(.el-button.is-loading) {
  opacity: 0.8 !important;
}

/* Responsive Design */
@media (max-width: 640px) {
  .register-container {
    padding: var(--space-4);
  }

  .register-card {
    padding: var(--space-8) var(--space-6);
    border-radius: var(--radius-xl);
  }

  .register-title {
    font-size: var(--text-2xl);
    margin-bottom: var(--space-6);
  }

  :deep(.el-form-item) {
    margin-bottom: var(--space-5) !important;
  }

  :deep(.el-input),
  :deep(.el-button--large) {
    height: 48px !important;
  }

  :deep(.el-input__inner) {
    height: 44px !important;
  }
}

/* Animation */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-card {
  animation: slideUp 0.6s ease-out;
}
</style>