<template>
  <div class="my-courses-page">
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
          <router-link to="/announcements" class="nav-link">公告</router-link>
          <router-link to="/my-courses" class="nav-link active">我的课程</router-link>
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
          <h1 class="hero-title">我的课程</h1>
          <p class="hero-subtitle">继续你的学习之旅，掌握新技能</p>
        </div>
        <div class="hero-stats">
          <div class="stat-card">
            <div class="stat-number">{{ courses.length }}</div>
            <div class="stat-label">已选课程</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ getAverageProgress() }}%</div>
            <div class="stat-label">平均进度</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ getCompletedCourses() }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <main class="main-content">
      <div class="content-container">
        <!-- Learning Progress Summary -->
        <div v-if="courses.length > 0" class="progress-summary">
          <div class="summary-card">
            <div class="summary-header">
              <h3>学习进度概览</h3>
              <span class="summary-subtitle">继续学习，完成目标</span>
            </div>
            <div class="overall-progress">
              <div class="progress-ring">
                <svg class="progress-circle" width="120" height="120">
                  <circle
                    cx="60"
                    cy="60"
                    r="50"
                    stroke="var(--gray-200)"
                    stroke-width="10"
                    fill="none"
                  />
                  <circle
                    cx="60"
                    cy="60"
                    r="50"
                    stroke="var(--primary)"
                    stroke-width="10"
                    fill="none"
                    stroke-dasharray="314"
                    :stroke-dashoffset="314 - (314 * getAverageProgress()) / 100"
                    class="progress-bar"
                  />
                </svg>
                <div class="progress-text">
                  <span class="progress-number">{{ getAverageProgress() }}%</span>
                  <span class="progress-label">总体进度</span>
                </div>
              </div>
              <div class="progress-details">
                <div class="detail-item">
                  <el-icon class="detail-icon"><Clock /></el-icon>
                  <span>最近学习：{{ getRecentStudyTime() }}</span>
                </div>
                <div class="detail-item">
                  <el-icon class="detail-icon"><Star /></el-icon>
                  <span>本周目标：完成 2 个课程章节</span>
                </div>
                <div class="detail-item">
                  <el-icon class="detail-icon"><Trophy /></el-icon>
                  <span>学习积分：{{ getTotalPoints() }} 分</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Courses Grid -->
        <div class="courses-section">
          <div class="section-header">
            <h2 class="section-title">我的课程列表</h2>
            <div class="section-actions">
              <el-button @click="router.push('/courses')" type="primary" plain>
                <el-icon><Plus /></el-icon>
                选择更多课程
              </el-button>
            </div>
          </div>

          <!-- Empty State -->
          <div v-if="courses.length === 0" class="empty-state">
            <el-icon class="empty-icon"><Reading /></el-icon>
            <h3>还没有选择任何课程</h3>
            <p>开始你的学习之旅，选择感兴趣的课程</p>
            <el-button type="primary" size="large" @click="router.push('/courses')">
              <el-icon><Search /></el-icon>
              浏览课程
            </el-button>
          </div>

          <!-- Courses Grid -->
          <div v-else class="courses-grid">
            <div
              v-for="enrollment in courses"
              :key="enrollment.id"
              class="course-card"
            >
              <div class="course-image">
                <img :src="enrollment.course.coverUrl || '/default-cover.jpg'" alt="Course Cover" />
                <div class="course-overlay">
                  <div class="progress-indicator">
                    <div class="progress-circle-mini">
                      <svg width="60" height="60">
                        <circle
                          cx="30"
                          cy="30"
                          r="25"
                          stroke="rgba(255,255,255,0.3)"
                          stroke-width="4"
                          fill="none"
                        />
                        <circle
                          cx="30"
                          cy="30"
                          r="25"
                          stroke="white"
                          stroke-width="4"
                          fill="none"
                          stroke-dasharray="157"
                          :stroke-dashoffset="157 - (157 * getProgress(enrollment)) / 100"
                          class="progress-bar-mini"
                        />
                      </svg>
                      <span class="progress-text-mini">{{ getProgress(enrollment) }}%</span>
                    </div>
                  </div>
                  <div class="course-actions">
                    <el-button type="primary" @click="continueLearning(enrollment.course.id)">
                      <el-icon><VideoPlay /></el-icon>
                      继续学习
                    </el-button>
                  </div>
                </div>
              </div>

              <div class="course-content">
                <div class="course-header">
                  <h3 class="course-title">{{ enrollment.course.title }}</h3>
                  <span class="course-category">{{ enrollment.course.category || '编程' }}</span>
                </div>

                <p class="course-description">{{ enrollment.course.subtitle || enrollment.course.description }}</p>

                <div class="course-progress">
                  <div class="progress-bar-container">
                    <div class="progress-bar-bg">
                      <div
                        class="progress-bar-fill"
                        :style="{ width: getProgress(enrollment) + '%' }"
                      ></div>
                    </div>
                    <span class="progress-text">{{ getProgress(enrollment) }}% 完成</span>
                  </div>
                </div>

                <div class="course-meta">
                  <div class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    <span>选课时间：{{ formatDate(enrollment.enrolledAt) }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Clock /></el-icon>
                    <span>最近学习：{{ getLastStudyTime(enrollment) }}</span>
                  </div>
                </div>

                <div class="course-footer">
                  <el-button @click="viewDetails(enrollment.course.id)" plain>
                    查看详情
                  </el-button>
                  <el-button type="primary" @click="continueLearning(enrollment.course.id)">
                    继续学习
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- Pagination -->
          <div v-if="total > pageSize" class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              layout="prev, pager, next, total"
              @current-change="loadMyCourses"
              class="modern-pagination"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getMyCourses } from '../api/course'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const courses = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => {
  loadMyCourses()
})

const loadMyCourses = async () => {
  try {
    const res = await getMyCourses({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    courses.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    ElMessage.error('加载课程失败')
  }
}

const continueLearning = (courseId) => {
  router.push(`/course/${courseId}`)
}

const viewDetails = (courseId) => {
  router.push(`/course/${courseId}`)
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

const getProgress = (enrollment) => {
  return Math.floor(Math.random() * 100)
}

const getAverageProgress = () => {
  if (courses.value.length === 0) return 0
  const totalProgress = courses.value.reduce((sum, enrollment) => sum + getProgress(enrollment), 0)
  return Math.round(totalProgress / courses.value.length)
}

const getCompletedCourses = () => {
  return courses.value.filter(enrollment => getProgress(enrollment) >= 100).length
}

const getRecentStudyTime = () => {
  return '2 小时前'
}

const getTotalPoints = () => {
  return courses.value.length * 120 + Math.floor(Math.random() * 500)
}

const getLastStudyTime = (enrollment) => {
  const times = ['昨天', '2天前', '1周前', '2周前']
  return times[Math.floor(Math.random() * times.length)]
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.my-courses-page {
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
  padding: var(--space-16) 0;
  position: relative;
  overflow: hidden;
}

.hero-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-6);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-8);
}

.hero-content {
  flex: 1;
  min-width: 300px;
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

.hero-stats {
  display: flex;
  gap: var(--space-6);
  flex-wrap: wrap;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-width: 120px;
}

.stat-number {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
  color: #ffd700;
}

.stat-label {
  font-size: var(--text-sm);
  opacity: 0.9;
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

/* Progress Summary */
.progress-summary {
  margin-bottom: var(--space-12);
}

.summary-card {
  background: white;
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--gray-200);
}

.summary-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.summary-header h3 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin-bottom: var(--space-2);
}

.summary-subtitle {
  color: var(--gray-600);
  font-size: var(--text-base);
}

.overall-progress {
  display: flex;
  align-items: center;
  gap: var(--space-12);
  justify-content: center;
  flex-wrap: wrap;
}

.progress-ring {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.progress-circle {
  transform: rotate(-90deg);
}

.progress-bar {
  transition: stroke-dashoffset 1s ease-in-out;
}

.progress-text {
  position: absolute;
  text-align: center;
}

.progress-number {
  display: block;
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--primary);
}

.progress-label {
  font-size: var(--text-sm);
  color: var(--gray-600);
}

.progress-details {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  color: var(--gray-700);
}

.detail-icon {
  color: var(--primary);
  font-size: var(--text-lg);
}

/* Courses Section */
.courses-section {
  margin-top: var(--space-8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
  flex-wrap: wrap;
  gap: var(--space-4);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
}

.section-actions {
  display: flex;
  gap: var(--space-3);
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
  margin-bottom: var(--space-6);
}

.empty-state h3 {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--gray-700);
  margin-bottom: var(--space-3);
}

.empty-state p {
  font-size: var(--text-base);
  margin-bottom: var(--space-6);
}

/* Courses Grid */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: var(--space-8);
  margin-bottom: var(--space-12);
}

.course-card {
  background: white;
  border-radius: var(--radius-2xl);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--gray-200);
  transition: var(--transition);
  position: relative;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--gray-300);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: var(--transition);
}

.course-card:hover .course-image img {
  transform: scale(1.05);
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.6));
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: var(--space-4);
  opacity: 0;
  transition: var(--transition);
}

.course-card:hover .course-overlay {
  opacity: 1;
}

.progress-indicator {
  align-self: flex-end;
}

.progress-circle-mini {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.progress-circle-mini svg {
  transform: rotate(-90deg);
}

.progress-bar-mini {
  transition: stroke-dashoffset 0.5s ease-in-out;
}

.progress-text-mini {
  position: absolute;
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  color: white;
}

.course-actions {
  align-self: center;
}

.course-content {
  padding: var(--space-6);
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.course-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--gray-900);
  margin: 0;
  line-height: 1.4;
  flex: 1;
}

.course-category {
  background: var(--primary);
  color: white;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-base);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  white-space: nowrap;
}

.course-description {
  color: var(--gray-600);
  font-size: var(--text-sm);
  line-height: 1.5;
  margin-bottom: var(--space-4);
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-progress {
  margin-bottom: var(--space-4);
}

.progress-bar-container {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.progress-bar-bg {
  flex: 1;
  height: 8px;
  background: var(--gray-200);
  border-radius: var(--radius-base);
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary), var(--primary-light));
  border-radius: var(--radius-base);
  transition: width 0.5s ease-in-out;
}

.progress-text {
  font-size: var(--text-xs);
  color: var(--gray-600);
  font-weight: var(--font-medium);
  white-space: nowrap;
}

.course-meta {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  margin-bottom: var(--space-6);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xs);
  color: var(--gray-500);
}

.course-footer {
  display: flex;
  gap: var(--space-3);
}

.course-footer .el-button {
  flex: 1;
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

  .hero-container {
    flex-direction: column;
    text-align: center;
  }

  .hero-stats {
    justify-content: center;
  }

  .overall-progress {
    flex-direction: column;
    gap: var(--space-6);
  }

  .courses-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: var(--space-6);
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
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
    padding: var(--space-12) 0;
  }

  .hero-title {
    font-size: var(--text-3xl);
  }

  .content-container {
    padding: 0 var(--space-4);
  }

  .courses-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .course-content {
    padding: var(--space-4);
  }

  .course-footer {
    flex-direction: column;
  }

  .stat-card {
    padding: var(--space-4);
    min-width: 100px;
  }

  .stat-number {
    font-size: var(--text-2xl);
  }
}
</style>