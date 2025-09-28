<template>
  <div class="profile-container">
    <el-container>
      <el-header class="header">
        <div class="logo" @click="router.push('/')">智慧学院</div>
        <span class="page-title">个人中心</span>
        <el-button icon="ArrowLeft" @click="router.push('/')">返回首页</el-button>
      </el-header>

      <el-main class="main">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" size="small" @click="editMode = !editMode">
                {{ editMode ? '取消' : '编辑' }}
              </el-button>
            </div>
          </template>

          <el-form :model="userInfo" label-width="100px" :disabled="!editMode">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userInfo.nickname" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userInfo.phone" />
            </el-form-item>
            <el-form-item label="角色">
              <el-tag>{{ userInfo.role === 'ADMIN' ? '管理员' : '认证学员' }}</el-tag>
            </el-form-item>
            <el-form-item v-if="editMode">
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>修改密码</span>
          </template>

          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const editMode = ref(false)
const passwordFormRef = ref()

const userInfo = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  role: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = () => {
  const user = authStore.user
  if (user) {
    userInfo.username = user.username
    userInfo.nickname = user.nickname
    userInfo.email = user.email
    userInfo.phone = user.phone
    userInfo.role = user.role
  }
}

const saveProfile = async () => {
  // TODO: Implement API call to update profile
  ElMessage.success('个人信息已更新')
  editMode.value = false
}

const changePassword = async () => {
  const valid = await passwordFormRef.value.validate()
  if (!valid) return

  // TODO: Implement API call to change password
  ElMessage.success('密码修改成功，请重新登录')
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--color-gray-50) 0%, var(--color-gray-100) 100%);
}

.header {
  background: var(--color-white);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-6);
  box-shadow: var(--shadow-soft);
  border-bottom: 1px solid var(--color-border-primary);
}

.logo {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  cursor: pointer;
  transition: transform var(--transition-base);
}

.logo:hover {
  transform: translateX(-4px);
}

.page-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  flex: 1;
  text-align: center;
  letter-spacing: var(--letter-spacing-tight);
}

.main {
  padding: var(--spacing-8) var(--spacing-6);
  max-width: 900px;
  margin: 0 auto;
}

.main :deep(.el-card) {
  border-radius: var(--radius-xl);
  border: none;
  box-shadow: var(--shadow-soft);
  transition: all var(--transition-base);
  overflow: hidden;
}

.main :deep(.el-card:hover) {
  box-shadow: var(--shadow-medium);
  transform: translateY(-2px);
}

.main :deep(.el-card__header) {
  background: linear-gradient(135deg, var(--color-gray-50), var(--color-white));
  border-bottom: 2px solid var(--color-border-primary);
  padding: var(--spacing-6);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.main :deep(.el-form-item__label) {
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
  letter-spacing: var(--letter-spacing-wide);
  text-transform: uppercase;
}

.main :deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-xs);
  transition: all var(--transition-base);
}

.main :deep(.el-input__wrapper:hover) {
  box-shadow: var(--shadow-sm);
}

.main :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  border-color: var(--color-primary);
}

.main :deep(.el-button--primary) {
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  border: none;
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-3) var(--spacing-6);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}

.main :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-primary);
}

.main :deep(.el-tag) {
  border-radius: var(--radius-full);
  padding: var(--spacing-1) var(--spacing-3);
  font-weight: var(--font-weight-medium);
  background: linear-gradient(135deg, var(--color-primary-lighter), var(--color-primary-light));
  color: var(--color-primary-darker);
  border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 0 var(--spacing-4);
  }

  .logo {
    font-size: var(--font-size-base);
  }

  .page-title {
    font-size: var(--font-size-base);
  }

  .main {
    padding: var(--spacing-4);
  }
}
</style>