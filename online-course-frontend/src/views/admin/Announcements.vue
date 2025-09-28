<template>
  <div class="announcement-management">
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="showCreateDialog">发布公告</el-button>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索公告"
        style="width: 300px; margin-left: auto"
        prefix-icon="Search"
        @keyup.enter="loadAnnouncements"
      />
    </div>

    <el-table :data="announcements" style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题">
        <template #default="{ row }">
          <div class="title-cell">
            <el-icon v-if="row.isPinned" color="#E6A23C"><Top /></el-icon>
            {{ row.title }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeTagType(row.type)">{{ getTypeName(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100">
        <template #default="{ row }">
          <el-tag :type="getPriorityTagType(row.priority)">{{ getPriorityName(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="publishTime" label="发布时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.publishTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" @click="viewAnnouncement(row)">查看</el-button>
          <el-button size="small" @click="editAnnouncement(row)">编辑</el-button>
          <el-button
            v-if="row.status === 'DRAFT'"
            size="small"
            type="success"
            @click="publishAnnouncement(row)"
          >
            发布
          </el-button>
          <el-button
            v-if="row.status === 'PUBLISHED'"
            size="small"
            type="warning"
            @click="archiveAnnouncement(row)"
          >
            归档
          </el-button>
          <el-button size="small" type="danger" @click="deleteAnnouncement(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      @current-change="loadAnnouncements"
    />

    <!-- 创建/编辑公告对话框 -->
    <el-dialog
      v-model="announcementDialogVisible"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="800px"
    >
      <el-form :model="announcementForm" :rules="announcementRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="announcementForm.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            rows="10"
            placeholder="支持Markdown格式"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-select v-model="announcementForm.type" style="width: 100%">
                <el-option label="系统公告" value="SYSTEM" />
                <el-option label="课程公告" value="COURSE" />
                <el-option label="活动公告" value="ACTIVITY" />
                <el-option label="维护公告" value="MAINTENANCE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="announcementForm.priority" style="width: 100%">
                <el-option label="低" value="LOW" />
                <el-option label="普通" value="NORMAL" />
                <el-option label="高" value="HIGH" />
                <el-option label="紧急" value="URGENT" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否置顶">
              <el-switch v-model="announcementForm.isPinned" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="过期时间">
              <el-date-picker
                v-model="announcementForm.expireTime"
                type="datetime"
                placeholder="选择过期时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="announcementDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAnnouncement">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看公告对话框 -->
    <el-dialog v-model="viewDialogVisible" title="公告详情" width="700px">
      <div v-if="currentAnnouncement">
        <h2>{{ currentAnnouncement.title }}</h2>
        <div class="announcement-meta">
          <el-tag :type="getTypeTagType(currentAnnouncement.type)">
            {{ getTypeName(currentAnnouncement.type) }}
          </el-tag>
          <el-tag :type="getPriorityTagType(currentAnnouncement.priority)">
            {{ getPriorityName(currentAnnouncement.priority) }}
          </el-tag>
          <span>发布时间：{{ formatDate(currentAnnouncement.publishTime) }}</span>
          <span>浏览次数：{{ currentAnnouncement.viewCount }}</span>
        </div>
        <el-divider />
        <div class="announcement-content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../api/request'

const announcements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const announcementDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const currentAnnouncement = ref(null)

const announcementForm = ref({
  title: '',
  content: '',
  type: 'SYSTEM',
  priority: 'NORMAL',
  isPinned: false,
  expireTime: null
})

const announcementRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

onMounted(() => {
  loadAnnouncements()
})

const loadAnnouncements = async () => {
  try {
    const res = await request({
      url: '/admin/announcements',
      method: 'get',
      params: {
        page: currentPage.value - 1,
        size: pageSize.value
      }
    })
    announcements.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    ElMessage.error('加载公告失败')
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  announcementForm.value = {
    title: '',
    content: '',
    type: 'SYSTEM',
    priority: 'NORMAL',
    isPinned: false,
    expireTime: null
  }
  announcementDialogVisible.value = true
}

const editAnnouncement = (announcement) => {
  isEdit.value = true
  announcementForm.value = {
    id: announcement.id,
    title: announcement.title,
    content: announcement.content,
    type: announcement.type,
    priority: announcement.priority,
    isPinned: announcement.isPinned,
    expireTime: announcement.expireTime
  }
  announcementDialogVisible.value = true
}

const viewAnnouncement = (announcement) => {
  currentAnnouncement.value = announcement
  viewDialogVisible.value = true
}

const saveAnnouncement = async () => {
  try {
    if (isEdit.value) {
      await request({
        url: `/admin/announcements/${announcementForm.value.id}`,
        method: 'put',
        data: announcementForm.value
      })
      ElMessage.success('更新成功')
    } else {
      const res = await request({
        url: '/admin/announcements',
        method: 'post',
        data: announcementForm.value
      })
      ElMessage.success('创建成功')

      // 询问是否立即发布
      if (res.data.status === 'DRAFT') {
        ElMessageBox.confirm('是否立即发布该公告？', '提示', {
          confirmButtonText: '立即发布',
          cancelButtonText: '稍后发布',
          type: 'info'
        }).then(() => {
          publishAnnouncement(res.data)
        }).catch(() => {})
      }
    }
    announcementDialogVisible.value = false
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const publishAnnouncement = async (announcement) => {
  try {
    await request({
      url: `/admin/announcements/${announcement.id}/publish`,
      method: 'post'
    })
    ElMessage.success('发布成功')
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const archiveAnnouncement = async (announcement) => {
  try {
    await request({
      url: `/admin/announcements/${announcement.id}/archive`,
      method: 'post'
    })
    ElMessage.success('归档成功')
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('归档失败')
  }
}

const deleteAnnouncement = async (announcement) => {
  await ElMessageBox.confirm('确定要删除这个公告吗？', '确认删除', {
    type: 'warning'
  })

  try {
    await request({
      url: `/admin/announcements/${announcement.id}`,
      method: 'delete'
    })
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('删除失败')
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

const getTypeName = (type) => {
  const names = {
    SYSTEM: '系统公告',
    COURSE: '课程公告',
    ACTIVITY: '活动公告',
    MAINTENANCE: '维护公告'
  }
  return names[type] || type
}

const getTypeTagType = (type) => {
  const types = {
    SYSTEM: 'primary',
    COURSE: 'success',
    ACTIVITY: 'warning',
    MAINTENANCE: 'danger'
  }
  return types[type] || 'info'
}

const getPriorityName = (priority) => {
  const names = {
    LOW: '低',
    NORMAL: '普通',
    HIGH: '高',
    URGENT: '紧急'
  }
  return names[priority] || priority
}

const getPriorityTagType = (priority) => {
  const types = {
    LOW: 'info',
    NORMAL: 'success',
    HIGH: 'warning',
    URGENT: 'danger'
  }
  return types[priority] || 'info'
}

const getStatusName = (status) => {
  const names = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ARCHIVED: '已归档'
  }
  return names[status] || status
}

const getStatusTagType = (status) => {
  const types = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    ARCHIVED: 'warning'
  }
  return types[status] || 'info'
}
</script>

<style scoped>
.announcement-management {
  background: white;
  border-radius: var(--radius-xl);
  padding: 0;
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--gray-200);
  overflow: hidden;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-6) var(--space-8);
  background: linear-gradient(135deg, var(--gray-50) 0%, var(--gray-100) 100%);
  border-bottom: 1px solid var(--gray-200);
}

.toolbar .el-input {
  max-width: 320px;
}

.toolbar .el-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-lg);
  border: 1px solid var(--gray-300);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
}

.toolbar .el-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.toolbar .el-button {
  border-radius: var(--radius-lg) !important;
  padding: var(--space-3) var(--space-6) !important;
  font-weight: var(--font-semibold) !important;
  background: var(--gradient-primary) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3) !important;
  transition: var(--transition) !important;
}

.toolbar .el-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4) !important;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-weight: var(--font-medium);
}

:deep(.el-table) {
  border: none !important;
  border-radius: 0 !important;
}

:deep(.el-table__header) {
  background: var(--gray-50) !important;
}

:deep(.el-table th) {
  background: var(--gray-50) !important;
  border-bottom: 1px solid var(--gray-200) !important;
  color: var(--gray-700) !important;
  font-weight: var(--font-semibold) !important;
  font-size: var(--text-sm) !important;
  padding: var(--space-4) var(--space-3) !important;
}

:deep(.el-table td) {
  border-bottom: 1px solid var(--gray-100) !important;
  padding: var(--space-4) var(--space-3) !important;
  font-size: var(--text-sm) !important;
}

:deep(.el-table__row) {
  transition: var(--transition) !important;
}

:deep(.el-table__row:hover) {
  background: var(--gray-50) !important;
}

:deep(.el-tag) {
  border-radius: var(--radius-full) !important;
  padding: var(--space-1) var(--space-3) !important;
  font-size: var(--text-xs) !important;
  font-weight: var(--font-semibold) !important;
  border: none !important;
}

:deep(.el-button--small) {
  border-radius: var(--radius-md) !important;
  padding: var(--space-2) var(--space-3) !important;
  font-size: var(--text-xs) !important;
  font-weight: var(--font-semibold) !important;
  transition: var(--transition) !important;
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
  box-shadow: var(--shadow-md) !important;
}

.el-pagination {
  margin-top: 0;
  padding: var(--space-6) var(--space-8);
  background: var(--gray-50);
  border-top: 1px solid var(--gray-200);
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: var(--gray-600);
  --el-pagination-border-radius: var(--radius-md);
}

:deep(.el-pager li) {
  margin: 0 var(--space-1) !important;
  border-radius: var(--radius-md) !important;
  transition: var(--transition) !important;
}

:deep(.el-pager li.is-active) {
  background: var(--gradient-primary) !important;
  color: white !important;
}

/* Dialog Styling */
:deep(.el-dialog) {
  border-radius: var(--radius-xl) !important;
  overflow: hidden !important;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, var(--gray-50) 0%, var(--gray-100) 100%) !important;
  padding: var(--space-6) var(--space-8) !important;
  border-bottom: 1px solid var(--gray-200) !important;
}

:deep(.el-dialog__title) {
  font-size: var(--text-xl) !important;
  font-weight: var(--font-bold) !important;
  color: var(--gray-900) !important;
}

:deep(.el-dialog__body) {
  padding: var(--space-8) !important;
}

:deep(.el-form-item__label) {
  font-weight: var(--font-semibold) !important;
  color: var(--gray-700) !important;
}

:deep(.el-input__wrapper) {
  border-radius: var(--radius-lg) !important;
  transition: var(--transition) !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--primary) !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

:deep(.el-textarea__inner) {
  border-radius: var(--radius-lg) !important;
  transition: var(--transition) !important;
}

:deep(.el-textarea__inner:hover) {
  border-color: var(--primary) !important;
}

:deep(.el-textarea__inner:focus) {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: var(--radius-lg) !important;
}

:deep(.el-date-editor .el-input__wrapper) {
  border-radius: var(--radius-lg) !important;
}

:deep(.el-dialog__footer) {
  padding: var(--space-6) var(--space-8) !important;
  background: var(--gray-50) !important;
  border-top: 1px solid var(--gray-200) !important;
}

:deep(.el-dialog__footer .el-button) {
  border-radius: var(--radius-lg) !important;
  padding: var(--space-3) var(--space-6) !important;
  font-weight: var(--font-semibold) !important;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin: var(--space-4) 0;
  color: var(--gray-600);
  font-size: var(--text-sm);
  flex-wrap: wrap;
}

.announcement-content {
  line-height: 1.8;
  color: var(--gray-800);
  white-space: pre-wrap;
  font-size: var(--text-base);
  padding: var(--space-4);
  background: var(--gray-50);
  border-radius: var(--radius-lg);
  border: 1px solid var(--gray-200);
}

/* Responsive Design */
@media (max-width: 768px) {
  .announcement-management {
    border-radius: var(--radius-lg);
    margin: var(--space-2);
  }

  .toolbar {
    flex-direction: column;
    gap: var(--space-4);
    align-items: stretch;
    padding: var(--space-4);
  }

  .toolbar .el-input {
    max-width: none;
  }

  :deep(.el-table__header),
  :deep(.el-table__body) {
    font-size: var(--text-xs) !important;
  }

  :deep(.el-table th),
  :deep(.el-table td) {
    padding: var(--space-3) var(--space-2) !important;
  }

  .el-pagination {
    padding: var(--space-4);
  }

  .announcement-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }

  :deep(.el-dialog) {
    width: 95% !important;
    margin: var(--space-4) auto !important;
  }

  :deep(.el-dialog__body) {
    padding: var(--space-4) !important;
  }

  :deep(.el-dialog__header) {
    padding: var(--space-4) !important;
  }

  :deep(.el-dialog__footer) {
    padding: var(--space-4) !important;
  }
}
</style>