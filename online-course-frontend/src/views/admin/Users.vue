<template>
  <div class="user-management">
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户"
        style="width: 300px"
        prefix-icon="Search"
        @keyup.enter="loadUsers"
      />
      <el-button type="primary" @click="loadUsers" style="margin-left: 10px">搜索</el-button>
    </div>

    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'success'">
            {{ row.role === 'ADMIN' ? '管理员' : '学员' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" @click="resetPassword(row)">重置密码</el-button>
          <el-button
            v-if="row.status === 1"
            size="small"
            type="warning"
            @click="toggleStatus(row)"
          >
            禁用
          </el-button>
          <el-button
            v-else
            size="small"
            type="success"
            @click="toggleStatus(row)"
          >
            启用
          </el-button>
          <el-button
            v-if="row.role !== 'ADMIN'"
            size="small"
            type="danger"
            @click="deleteUser(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      @current-change="loadUsers"
    />

    <!-- 重置密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="重置密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResetPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../api/request'

const users = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const passwordDialogVisible = ref(false)
const passwordFormRef = ref()
const currentUser = ref(null)

const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  try {
    const res = await request({
      url: '/admin/users',
      method: 'get',
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        keyword: searchKeyword.value
      }
    })
    users.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

const resetPassword = (user) => {
  currentUser.value = user
  passwordForm.value = {
    newPassword: '',
    confirmPassword: ''
  }
  passwordDialogVisible.value = true
}

const confirmResetPassword = async () => {
  const valid = await passwordFormRef.value.validate()
  if (!valid) return

  try {
    await request({
      url: `/admin/users/${currentUser.value.id}/reset-password`,
      method: 'put',
      data: {
        newPassword: passwordForm.value.newPassword
      }
    })
    ElMessage.success('密码重置成功')
    passwordDialogVisible.value = false
  } catch (error) {
    ElMessage.error('密码重置失败')
  }
}

const toggleStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'

  await ElMessageBox.confirm(`确定要${action}该用户吗？`, '确认操作', {
    type: 'warning'
  })

  try {
    await request({
      url: `/admin/users/${user.id}/status`,
      method: 'put',
      data: {
        status: newStatus
      }
    })
    ElMessage.success(`${action}成功`)
    loadUsers()
  } catch (error) {
    ElMessage.error(`${action}失败`)
  }
}

const deleteUser = async (user) => {
  await ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复', '确认删除', {
    type: 'warning'
  })

  try {
    await request({
      url: `/admin/users/${user.id}`,
      method: 'delete'
    })
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}
</script>

<style scoped>
.user-management {
  background: white;
  border-radius: 16px;
  padding: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-bottom: 1px solid #e2e8f0;
}

.toolbar .el-input {
  max-width: 320px;
}

.toolbar .el-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.toolbar .el-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.toolbar .el-button {
  border-radius: 12px !important;
  padding: 0.75rem 1.5rem !important;
  font-weight: 600 !important;
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3) !important;
  transition: all 0.3s !important;
}

.toolbar .el-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4) !important;
}

:deep(.el-table) {
  border: none !important;
  border-radius: 0 !important;
}

:deep(.el-table__header) {
  background: #f8fafc !important;
}

:deep(.el-table th) {
  background: #f8fafc !important;
  border-bottom: 1px solid #e2e8f0 !important;
  color: #374151 !important;
  font-weight: 600 !important;
  font-size: 0.875rem !important;
  padding: 1rem 0.75rem !important;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f1f5f9 !important;
  padding: 1rem 0.75rem !important;
  font-size: 0.875rem !important;
}

:deep(.el-table__row) {
  transition: all 0.3s !important;
}

:deep(.el-table__row:hover) {
  background: #f8fafc !important;
}

:deep(.el-tag) {
  border-radius: 20px !important;
  padding: 0.25rem 0.75rem !important;
  font-size: 0.75rem !important;
  font-weight: 600 !important;
  border: none !important;
}

:deep(.el-button--small) {
  border-radius: 8px !important;
  padding: 0.5rem 1rem !important;
  font-size: 0.75rem !important;
  font-weight: 600 !important;
  transition: all 0.3s !important;
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #ef4444, #dc2626) !important;
  border: none !important;
  color: white !important;
}

:deep(.el-button--warning) {
  background: linear-gradient(135deg, #f59e0b, #d97706) !important;
  border: none !important;
  color: white !important;
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #10b981, #059669) !important;
  border: none !important;
  color: white !important;
}

:deep(.el-button--small:hover) {
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.el-pagination {
  margin-top: 0;
  padding: 1.5rem 2rem;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: #64748b;
  --el-pagination-border-radius: 8px;
}

:deep(.el-pager li) {
  margin: 0 0.25rem !important;
  border-radius: 8px !important;
  transition: all 0.3s !important;
}

:deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
}

/* Dialog Styling */
:deep(.el-dialog) {
  border-radius: 16px !important;
  overflow: hidden !important;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%) !important;
  padding: 1.5rem 2rem !important;
  border-bottom: 1px solid #e2e8f0 !important;
}

:deep(.el-dialog__title) {
  font-size: 1.25rem !important;
  font-weight: 700 !important;
  color: #1f2937 !important;
}

:deep(.el-dialog__body) {
  padding: 2rem !important;
}

:deep(.el-form-item__label) {
  font-weight: 600 !important;
  color: #374151 !important;
}

:deep(.el-input__wrapper) {
  border-radius: 12px !important;
  transition: all 0.3s !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: #667eea !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #667eea !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

:deep(.el-dialog__footer) {
  padding: 1.5rem 2rem !important;
  background: #f8fafc !important;
  border-top: 1px solid #e2e8f0 !important;
}

:deep(.el-dialog__footer .el-button) {
  border-radius: 12px !important;
  padding: 0.75rem 1.5rem !important;
  font-weight: 600 !important;
}

/* Responsive Design */
@media (max-width: 768px) {
  .user-management {
    border-radius: 8px;
    margin: 0.5rem;
  }

  .toolbar {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .toolbar .el-input {
    max-width: none;
  }

  :deep(.el-table__header),
  :deep(.el-table__body) {
    font-size: 0.75rem !important;
  }

  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 0.75rem 0.5rem !important;
  }

  .el-pagination {
    padding: 1rem;
  }
}
</style>