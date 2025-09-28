<template>
  <div class="announcement-detail-container">
    <el-container>
      <el-header class="header">
        <div class="logo">在线课程学习系统</div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/courses">全部课程</el-menu-item>
          <el-menu-item index="/my-courses" v-if="authStore.isLoggedIn">我的课程</el-menu-item>

          <div class="user-section">
            <template v-if="!authStore.isLoggedIn">
              <el-button @click="router.push('/login')">登录</el-button>
              <el-button type="primary" @click="router.push('/register')">注册</el-button>
            </template>
            <el-dropdown v-else>
              <span class="user-info">
                <el-icon><User /></el-icon>
                {{ authStore.user?.nickname || authStore.user?.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isAdmin" @click="router.push('/admin')">
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-menu>
      </el-header>

      <el-main class="main">
        <el-card v-if="announcement" class="announcement-card">
          <el-button
            icon="ArrowLeft"
            @click="router.push('/')"
            style="margin-bottom: 20px"
          >
            返回首页
          </el-button>

          <div class="announcement-header">
            <h1>{{ announcement.title }}</h1>
            <div class="announcement-meta">
              <el-tag
                size="small"
                :type="getAnnouncementTagType(announcement.type)"
              >
                {{ getAnnouncementTypeName(announcement.type) }}
              </el-tag>
              <el-tag
                v-if="announcement.priority === 'URGENT'"
                size="small"
                type="danger"
              >
                紧急
              </el-tag>
              <el-tag
                v-else-if="announcement.priority === 'HIGH'"
                size="small"
                type="warning"
              >
                重要
              </el-tag>
              <span class="meta-info">
                <el-icon><Calendar /></el-icon>
                发布时间：{{ formatDate(announcement.publishTime) }}
              </span>
              <span class="meta-info">
                <el-icon><View /></el-icon>
                浏览次数：{{ announcement.viewCount }}
              </span>
            </div>
          </div>

          <el-divider />

          <div class="announcement-content" v-html="renderContent(announcement.content)"></div>

          <el-divider />

          <div class="announcement-footer">
            <div class="other-announcements">
              <h3>其他公告</h3>
              <el-space direction="vertical" :size="10" style="width: 100%">
                <el-link
                  v-for="item in otherAnnouncements"
                  :key="item.id"
                  @click="viewAnnouncement(item.id)"
                  type="primary"
                >
                  <el-icon><Document /></el-icon>
                  {{ item.title }}
                  <el-tag
                    v-if="item.priority === 'URGENT'"
                    size="small"
                    type="danger"
                    style="margin-left: 10px"
                  >
                    紧急
                  </el-tag>
                </el-link>
              </el-space>
            </div>
          </div>
        </el-card>

        <el-empty v-else description="公告不存在" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import request from '../api/request'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = ref('/')
const announcement = ref(null)
const otherAnnouncements = ref([])

onMounted(async () => {
  await loadAnnouncement()
  await loadOtherAnnouncements()
})

const loadAnnouncement = async () => {
  try {
    const res = await request({
      url: `/announcements/${route.params.id}`,
      method: 'get'
    })
    announcement.value = res.data
  } catch (error) {
    ElMessage.error('加载公告失败')
    console.error('Failed to load announcement:', error)
  }
}

const loadOtherAnnouncements = async () => {
  try {
    const res = await request({
      url: '/announcements',
      method: 'get',
      params: {
        page: 0,
        size: 5,
        status: 'PUBLISHED'
      }
    })
    otherAnnouncements.value = res.data.content.filter(item => item.id !== parseInt(route.params.id))
  } catch (error) {
    console.error('Failed to load other announcements:', error)
  }
}

const viewAnnouncement = (id) => {
  router.push(`/announcement/${id}`)
  loadAnnouncement()
  loadOtherAnnouncements()
}

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
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

const getAnnouncementTypeName = (type) => {
  const names = {
    SYSTEM: '系统公告',
    COURSE: '课程公告',
    ACTIVITY: '活动公告',
    MAINTENANCE: '维护公告'
  }
  return names[type] || type
}

const getAnnouncementTagType = (type) => {
  const types = {
    SYSTEM: 'primary',
    COURSE: 'success',
    ACTIVITY: 'warning',
    MAINTENANCE: 'danger'
  }
  return types[type] || 'info'
}

const renderContent = (content) => {
  if (!content) return ''
  try {
    return marked(content)
  } catch (error) {
    return content.replace(/\n/g, '<br>')
  }
}
</script>

<style scoped>
.announcement-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.menu {
  flex: 1;
  margin-left: 50px;
}

.user-section {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 10px;
}

.main {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.announcement-card {
  margin-top: 20px;
}

.announcement-header h1 {
  font-size: 28px;
  margin-bottom: 15px;
  color: #333;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.announcement-content {
  font-size: 16px;
  line-height: 1.8;
  color: #444;
  min-height: 200px;
}

.announcement-content :deep(h1),
.announcement-content :deep(h2),
.announcement-content :deep(h3) {
  margin: 20px 0 10px 0;
}

.announcement-content :deep(p) {
  margin: 10px 0;
}

.announcement-content :deep(ul),
.announcement-content :deep(ol) {
  margin: 10px 0;
  padding-left: 30px;
}

.announcement-content :deep(blockquote) {
  margin: 15px 0;
  padding: 10px 20px;
  background-color: #f5f5f5;
  border-left: 4px solid #409eff;
}

.announcement-footer {
  margin-top: 30px;
}

.other-announcements h3 {
  margin-bottom: 15px;
  color: #333;
}
</style>