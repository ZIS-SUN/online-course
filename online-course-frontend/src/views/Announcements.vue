<template>
  <div class="announcements-page">
    <!-- Modern Navigation -->
    <nav class="modern-nav">
      <div class="nav-container">
        <div class="nav-brand">
          <router-link to="/" class="brand-link">
            <div class="brand-icon">
              <el-icon :size="32"><School /></el-icon>
            </div>
            <span class="brand-text">LearnSpace</span>
          </router-link>
        </div>

        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/courses" class="nav-link">课程</router-link>
          <router-link to="/announcements" class="nav-link active">公告</router-link>
          <router-link v-if="authStore.isLoggedIn" to="/my-courses" class="nav-link">我的课程</router-link>
        </div>

        <div class="nav-actions">
          <template v-if="!authStore.isLoggedIn">
            <el-button @click="router.push('/login')" class="login-btn">登录</el-button>
            <el-button type="primary" @click="router.push('/register')" class="register-btn">注册</el-button>
          </template>
          <el-dropdown v-else class="user-dropdown">
            <div class="user-profile">
              <div class="user-avatar">
                <el-icon><User /></el-icon>
              </div>
              <span class="user-name">{{ authStore.user?.nickname || authStore.user?.username }}</span>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item v-if="authStore.isAdmin" @click="router.push('/admin')">
                  <el-icon><Setting /></el-icon>
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <h1 class="hero-title">公告中心</h1>
          <p class="hero-subtitle">了解系统最新动态和重要通知</p>
        </div>
        <div class="hero-decoration">
          <div class="decoration-circle circle-1"></div>
          <div class="decoration-circle circle-2"></div>
          <div class="decoration-circle circle-3"></div>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <main class="main-content">
      <div class="content-container">
        <!-- Filters Section -->
        <div class="filters-section">
          <div class="filters-container">
            <div class="filters-group">
              <el-select v-model="filterType" placeholder="全部类型" clearable @change="loadAnnouncements" class="filter-select">
                <el-option label="系统公告" value="SYSTEM" />
                <el-option label="课程公告" value="COURSE" />
                <el-option label="活动公告" value="ACTIVITY" />
                <el-option label="维护公告" value="MAINTENANCE" />
              </el-select>
              <el-select v-model="filterPriority" placeholder="全部优先级" clearable @change="loadAnnouncements" class="filter-select">
                <el-option label="普通" value="NORMAL" />
                <el-option label="重要" value="HIGH" />
                <el-option label="紧急" value="URGENT" />
              </el-select>
            </div>
            <div class="results-count">
              <span>共找到 {{ total }} 条公告</span>
            </div>
          </div>
        </div>

        <!-- Announcements Grid -->
        <div class="announcements-grid">
          <div
            v-for="announcement in announcements"
            :key="announcement.id"
            class="announcement-card"
            @click="viewAnnouncement(announcement.id)"
          >
            <div class="card-header">
              <div class="title-area">
                <el-icon v-if="announcement.isPinned" class="pin-icon"><Top /></el-icon>
                <h3 class="announcement-title">{{ announcement.title }}</h3>
              </div>
              <div class="tags-area">
                <span
                  class="type-tag"
                  :class="`type-${announcement.type?.toLowerCase()}`"
                >
                  {{ getAnnouncementTypeName(announcement.type) }}
                </span>
                <span
                  v-if="announcement.priority === 'URGENT'"
                  class="priority-tag urgent"
                >
                  紧急
                </span>
                <span
                  v-else-if="announcement.priority === 'HIGH'"
                  class="priority-tag high"
                >
                  重要
                </span>
                <span
                  v-if="!announcement.isRead && authStore.isLoggedIn"
                  class="status-tag unread"
                >
                  未读
                </span>
              </div>
            </div>

            <div class="card-content">
              <p class="announcement-summary">
                {{ truncateContent(announcement.content, 120) }}
              </p>
            </div>

            <div class="card-footer">
              <div class="meta-info">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDate(announcement.publishTime) }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ announcement.viewCount }} 次浏览
                </span>
              </div>
              <div class="read-more">
                <span>查看详情</span>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="announcements.length === 0" class="empty-state">
          <el-icon class="empty-icon"><Bell /></el-icon>
          <h3>暂无公告</h3>
          <p>当前没有可显示的公告信息</p>
        </div>

        <!-- Pagination -->
        <div v-if="total > pageSize" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="prev, pager, next, total"
            @current-change="loadAnnouncements"
            class="modern-pagination"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import request from '../api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const announcements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterType = ref('')
const filterPriority = ref('')

onMounted(() => {
  loadAnnouncements()
})

const loadAnnouncements = async () => {
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      status: 'PUBLISHED'
    }

    if (filterType.value) {
      params.type = filterType.value
    }

    if (filterPriority.value) {
      params.priority = filterPriority.value
    }

    const res = await request({
      url: '/announcements',
      method: 'get',
      params
    })

    announcements.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    console.error('Failed to load announcements:', error)
    ElMessage.error('加载公告失败')
  }
}

const viewAnnouncement = (id) => {
  router.push(`/announcement/${id}`)
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const truncateContent = (content, maxLength) => {
  if (!content) return ''
  const plainText = content.replace(/[#*`>\-\[\]]/g, '').trim()
  if (plainText.length <= maxLength) return plainText
  return plainText.substring(0, maxLength) + '...'
}

const getAnnouncementTypeName = (type) => {
  const names = {
    SYSTEM: '系统',
    COURSE: '课程',
    ACTIVITY: '活动',
    MAINTENANCE: '维护'
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
</script>

<style scoped>
.announcements-page {
  min-height: 100vh;
  background: var(--gray-50);
}

/* Modern Navigation */
.modern-nav {
  background: white;
  border-bottom: 1px solid var(--gray-200);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-6);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80px;
}

.nav-brand .brand-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  text-decoration: none;
  color: var(--gray-900);
}

.brand-icon {
  width: 48px;
  height: 48px;
  background: var(--primary);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.brand-text {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
}

.nav-links {
  display: flex;
  align-items: center;
  gap: var(--space-8);
}

.nav-link {
  text-decoration: none;
  color: var(--gray-600);
  font-weight: var(--font-medium);
  transition: var(--transition);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-lg);
}

.nav-link:hover,
.nav-link.active {
  color: var(--primary);
  background: rgba(0, 86, 211, 0.1);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.user-dropdown .user-profile {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition);
}

.user-dropdown .user-profile:hover {
  background: var(--gray-100);
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: var(--primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-name {
  font-weight: var(--font-medium);
  color: var(--gray-700);
}

.dropdown-arrow {
  color: var(--gray-500);
  transition: var(--transition);
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: white;
  padding: var(--space-20) 0 var(--space-16);
  position: relative;
  overflow: hidden;
}

.hero-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-6);
  position: relative;
  z-index: 2;
}

.hero-content {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.hero-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-4);
  line-height: 1.1;
}

.hero-subtitle {
  font-size: var(--text-lg);
  opacity: 0.9;
  margin: 0;
}

.hero-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 20%;
  right: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  left: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* Main Content */
.main-content {
  padding: var(--space-16) 0;
}

.content-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-6);
}

/* Filters Section */
.filters-section {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--gray-200);
}

.filters-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-4);
}

.filters-group {
  display: flex;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.filter-select {
  min-width: 160px;
}

.results-count {
  color: var(--gray-600);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

/* Announcements Grid */
.announcements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-12);
}

.announcement-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: var(--transition);
  position: relative;
  overflow: hidden;
}

.announcement-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary), var(--primary-light));
  transform: scaleX(0);
  transition: var(--transition);
}

.announcement-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--gray-300);
}

.announcement-card:hover::before {
  transform: scaleX(1);
}

.card-header {
  margin-bottom: var(--space-4);
}

.title-area {
  display: flex;
  align-items: flex-start;
  gap: var(--space-2);
  margin-bottom: var(--space-3);
}

.pin-icon {
  color: var(--warning);
  margin-top: 2px;
}

.announcement-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--gray-900);
  margin: 0;
  line-height: 1.4;
}

.tags-area {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.type-tag, .priority-tag, .status-tag {
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-base);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.type-tag.type-system {
  background: rgba(0, 86, 211, 0.1);
  color: var(--primary);
}

.type-tag.type-course {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.type-tag.type-activity {
  background: rgba(245, 158, 11, 0.1);
  color: var(--warning);
}

.type-tag.type-maintenance {
  background: rgba(239, 68, 68, 0.1);
  color: var(--error);
}

.priority-tag.urgent {
  background: rgba(239, 68, 68, 0.1);
  color: var(--error);
}

.priority-tag.high {
  background: rgba(245, 158, 11, 0.1);
  color: var(--warning);
}

.status-tag.unread {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.card-content {
  margin-bottom: var(--space-4);
}

.announcement-summary {
  color: var(--gray-600);
  line-height: 1.6;
  margin: 0;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--space-4);
  border-top: 1px solid var(--gray-100);
}

.meta-info {
  display: flex;
  gap: var(--space-4);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  color: var(--gray-500);
}

.read-more {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--primary);
  font-weight: var(--font-medium);
  transition: var(--transition);
}

.announcement-card:hover .read-more {
  transform: translateX(4px);
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: var(--space-20) var(--space-6);
  color: var(--gray-500);
}

.empty-icon {
  font-size: 64px;
  color: var(--gray-300);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--gray-700);
  margin-bottom: var(--space-2);
}

.empty-state p {
  font-size: var(--text-base);
  margin: 0;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  padding-top: var(--space-8);
}

.modern-pagination {
  background: white;
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

/* Responsive Design */
@media (max-width: 1024px) {
  .nav-container {
    padding: 0 var(--space-4);
  }

  .nav-links {
    gap: var(--space-4);
  }

  .hero-title {
    font-size: var(--text-3xl);
  }

  .announcements-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .filters-container {
    flex-direction: column;
    align-items: stretch;
  }

  .filters-group {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .nav-container {
    height: 64px;
    padding: 0 var(--space-3);
  }

  .nav-links {
    display: none;
  }

  .brand-text {
    display: none;
  }

  .hero-section {
    padding: var(--space-12) 0 var(--space-8);
  }

  .hero-title {
    font-size: var(--text-2xl);
  }

  .content-container {
    padding: 0 var(--space-4);
  }

  .announcement-card {
    padding: var(--space-4);
  }

  .card-footer {
    flex-direction: column;
    gap: var(--space-3);
    align-items: stretch;
  }
}
</style>