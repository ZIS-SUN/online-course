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
  background-color: #f5f5f5;
}

.header {
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
}

.page-title {
  font-size: 18px;
  font-weight: 500;
  flex: 1;
  text-align: center;
}

.main {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>