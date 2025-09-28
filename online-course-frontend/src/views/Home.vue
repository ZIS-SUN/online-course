<template>
  <div class="home-container">
    <!-- Modern Navigation -->
    <nav class="modern-nav">
      <div class="container">
        <div class="modern-nav-content">
          <router-link to="/" class="modern-nav-brand">
            <div class="brand-icon">
              <el-icon :size="24"><School /></el-icon>
            </div>
智慧学院
          </router-link>

          <div class="modern-nav-links">
            <router-link to="/" class="modern-nav-link active">首页</router-link>
            <router-link to="/courses" class="modern-nav-link">课程</router-link>
            <router-link to="/announcements" class="modern-nav-link">公告</router-link>
          </div>

          <div class="nav-actions">
            <template v-if="!authStore.isLoggedIn">
              <button class="modern-btn modern-btn-secondary" @click="router.push('/login')">
                登录
              </button>
              <button class="modern-btn modern-btn-primary" @click="router.push('/register')">
                注册
              </button>
            </template>
            <el-dropdown v-else>
              <div class="user-profile">
                <img :src="authStore.user?.avatarUrl || '/default-avatar.png'" alt="avatar" />
                <span>{{ authStore.user?.nickname || authStore.user?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/my-courses')">我的课程</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isAdmin" @click="router.push('/admin')">管理后台</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-background">
        <div class="hero-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
          <div class="shape shape-5"></div>
        </div>
        <div class="hero-grid-pattern"></div>
      </div>

      <div class="hero-content">
        <div class="hero-text">
          <div class="hero-badge">
            <el-icon><Trophy /></el-icon>
            <span>国家级在线教育认证平台</span>
          </div>
          <h1 class="hero-title">
            构建未来的
            <span class="gradient-text">数字化学习</span>
            <br>专业技能提升平台
          </h1>
          <p class="hero-subtitle">
            与全球500+知名企业及顶级院校合作，为你提供权威认证的专业课程。
            <br>已成功培养超过10万名行业精英，平均薪资提升68%。
          </p>

          <div class="hero-features">
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon><VideoPlay /></el-icon>
              </div>
              <span>4K超清视频</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon><Medal /></el-icon>
              </div>
              <span>ISO认证证书</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <span>1V1专家指导</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon><Cpu /></el-icon>
              </div>
              <span>AI智能评估</span>
            </div>
          </div>

          <div class="hero-stats">
            <div class="stat">
              <div class="stat-number">2,500+</div>
              <div class="stat-label">企业级课程</div>
            </div>
            <div class="stat">
              <div class="stat-number">150K+</div>
              <div class="stat-label">认证学员</div>
            </div>
            <div class="stat">
              <div class="stat-number">98.5%</div>
              <div class="stat-label">就业成功率</div>
            </div>
            <div class="stat">
              <div class="stat-number">500+</div>
              <div class="stat-label">合作企业</div>
            </div>
          </div>

          <div class="hero-actions">
            <el-button type="primary" size="large" class="cta-button" @click="router.push('/courses')">
              <el-icon><VideoPlay /></el-icon>
              立即开始学习
            </el-button>
            <el-button size="large" class="outline-button" @click="router.push('/announcements')">
              <el-icon><Document /></el-icon>
              了解更多
            </el-button>
          </div>

          <div class="hero-trust">
            <p class="trust-text">已获得超过 150,000+ 企业专业人士的信赖</p>
            <div class="trust-avatars">
              <div class="avatar-group">
                <div class="trust-avatar avatar-1">A</div>
                <div class="trust-avatar avatar-2">B</div>
                <div class="trust-avatar avatar-3">C</div>
                <div class="trust-avatar avatar-4">D</div>
                <div class="trust-more">+1000</div>
              </div>
            </div>
          </div>
        </div>

        <div class="hero-visual">
          <div class="hero-image">
            <div class="hero-dashboard">
              <div class="dashboard-header">
                <div class="dashboard-tabs">
                  <div class="tab active">学习进度</div>
                  <div class="tab">课程推荐</div>
                </div>
              </div>
              <div class="dashboard-content">
                <div class="progress-item">
                  <div class="progress-info">
                    <h4>JavaScript 进阶</h4>
                    <span>75% 完成</span>
                  </div>
                  <div class="progress-bar">
                    <div class="progress-fill" style="width: 75%"></div>
                  </div>
                </div>
                <div class="progress-item">
                  <div class="progress-info">
                    <h4>React 开发</h4>
                    <span>60% 完成</span>
                  </div>
                  <div class="progress-bar">
                    <div class="progress-fill" style="width: 60%"></div>
                  </div>
                </div>
                <div class="progress-item">
                  <div class="progress-info">
                    <h4>Node.js 实战</h4>
                    <span>45% 完成</span>
                  </div>
                  <div class="progress-bar">
                    <div class="progress-fill" style="width: 45%"></div>
                  </div>
                </div>
              </div>
            </div>

            <div class="floating-card card-1">
              <div class="card-icon">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="card-content">
                <h4>课程认证</h4>
                <p>权威认证证书</p>
              </div>
            </div>

            <div class="floating-card card-2">
              <div class="card-icon">
                <el-icon><Star /></el-icon>
              </div>
              <div class="card-content">
                <h4>专家讲师</h4>
                <p>业界资深导师</p>
              </div>
            </div>

            <div class="floating-card card-3">
              <div class="card-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="card-content">
                <h4>互动学习</h4>
                <p>实时问答交流</p>
              </div>
            </div>

            <div class="floating-notification">
              <div class="notification-avatar">
                <div class="avatar-placeholder">张</div>
              </div>
              <div class="notification-content">
                <p><strong>张同学</strong> 刚刚完成了 React 课程</p>
                <span>2分钟前</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features" v-if="!announcementsLoading && pinnedAnnouncements.length > 0">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">最新公告</h2>
          <el-button type="primary" text @click="router.push('/announcements')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="announcements-modern">
          <div
            v-for="(announcement, index) in pinnedAnnouncements.slice(0, 3)"
            :key="announcement.id"
            class="announcement-modern"
            :class="`announcement-${index + 1}`"
            @click="viewAnnouncement(announcement.id)"
          >
            <div class="announcement-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="announcement-content">
              <div class="announcement-meta">
                <el-tag :type="getAnnouncementTagType(announcement.type)" size="small">
                  {{ getAnnouncementTypeName(announcement.type) }}
                </el-tag>
                <span class="announcement-date">{{ formatDate(announcement.publishTime) }}</span>
              </div>
              <h3 class="announcement-title">{{ announcement.title }}</h3>
              <p class="announcement-excerpt">{{ announcement.content?.substring(0, 100) }}...</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Courses Section -->
    <section class="courses-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">热门课程</h2>
          <el-button type="primary" text @click="router.push('/courses')">
            查看全部课程
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <!-- 加载状态 -->
        <div v-if="coursesLoading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- 课程列表 -->
        <div v-else-if="featuredCourses.length > 0" class="courses-modern-grid">
          <div
            v-for="course in featuredCourses.slice(0, 6)"
            :key="course.id"
            class="course-modern-card"
            @click="viewCourse(course.id)"
          >
            <div class="course-image">
              <img :src="course.coverUrl || '/default-cover.jpg'" alt="course cover" />
              <div class="course-overlay">
                <el-button type="primary" circle size="large">
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
              </div>
              <div class="course-badges">
                <span v-if="course.isFree" class="badge badge-free">免费</span>
                <span class="badge badge-level">{{ getLevelName(course.level) }}</span>
              </div>
            </div>
            <div class="course-info">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-subtitle">{{ course.subtitle }}</p>
              <div class="course-meta">
                <div class="meta-item">
                  <el-icon><VideoPlay /></el-icon>
                  <span>{{ course.videoCount || 0 }} 视频</span>
                </div>
                <div class="meta-item">
                  <el-icon><Document /></el-icon>
                  <span>{{ course.sectionCount || 0 }} 章节</span>
                </div>
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>{{ course.studentCount || 0 }} 学员</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <el-empty description="暂无课程数据" />
        </div>
      </div>
    </section>

    <!-- Partners Section -->
    <section class="partners-section">
      <div class="container">
        <div class="section-header-center">
          <h2 class="section-title">权威合作伙伴</h2>
          <p class="section-subtitle">与全球领先企业和院校共同打造专业课程体系</p>
        </div>
        <div class="partners-grid">
          <div class="partner-logo">
            <div class="logo-placeholder">Microsoft</div>
          </div>
          <div class="partner-logo">
            <div class="logo-placeholder">Google</div>
          </div>
          <div class="partner-logo">
            <div class="logo-placeholder">Amazon</div>
          </div>
          <div class="partner-logo">
            <div class="logo-placeholder">IBM</div>
          </div>
          <div class="partner-logo">
            <div class="logo-placeholder">清华大学</div>
          </div>
          <div class="partner-logo">
            <div class="logo-placeholder">北京大学</div>
          </div>
          <div class="partner-logo">
            <div class="logo-placeholder">阿里巴巴</div>
          </div>
          <div class="partner-logo">
            <div class="logo-placeholder">腾讯</div>
          </div>
        </div>
        <div class="certification-badges">
          <div class="cert-badge">
            <el-icon><Medal /></el-icon>
            <span>ISO 9001认证</span>
          </div>
          <div class="cert-badge">
            <el-icon><Trophy /></el-icon>
            <span>国家教育部认证</span>
          </div>
          <div class="cert-badge">
            <el-icon><Star /></el-icon>
            <span>高新技术企业</span>
          </div>
          <div class="cert-badge">
            <el-icon><Shield /></el-icon>
            <span>信息安全等保三级</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="modern-footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <div class="brand">
              <div class="brand-icon">
                <el-icon :size="28"><School /></el-icon>
              </div>
              <span class="brand-text">智慧学院</span>
            </div>
            <p>国家认证的专业技能培训与职业发展平台</p>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h4>产品</h4>
              <a href="/courses">在线课程</a>
              <a href="/announcements">公告中心</a>
            </div>
            <div class="link-group">
              <h4>支持</h4>
              <a href="#">帮助中心</a>
              <a href="#">联系我们</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 智慧学院教育科技有限公司. 保留所有权利。 | 京ICP备20240001号-1</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getCourses } from '../api/course'
import request from '../api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const activeMenu = ref('/')
const featuredCourses = ref([])
const pinnedAnnouncements = ref([])
const coursesLoading = ref(true)
const announcementsLoading = ref(true)

onMounted(async () => {
  // 并行加载数据，但不等待公告加载完成
  const loadPromises = [
    loadFeaturedCourses(),
    loadPinnedAnnouncements()
  ]

  if (authStore.token && !authStore.user) {
    loadPromises.push(authStore.fetchCurrentUser())
  }

  // 使用 allSettled 确保所有请求都执行，即使某些失败
  await Promise.allSettled(loadPromises)
})

const loadFeaturedCourses = async () => {
  coursesLoading.value = true
  try {
    const res = await getCourses({ page: 0, size: 8 })
    featuredCourses.value = res.data.content
  } catch (error) {
    console.error('Failed to load courses:', error)
    // 如果是超时错误，显示友好提示
    if (error.code === 'ECONNABORTED') {
      ElMessage.warning('加载课程超时，请稍后刷新页面重试')
    } else {
      ElMessage.error('加载课程失败')
    }
    featuredCourses.value = []
  } finally {
    coursesLoading.value = false
  }
}

const handleMenuSelect = (index) => {
  router.push(index)
}

const loadPinnedAnnouncements = async () => {
  announcementsLoading.value = true
  try {
    // 设置较短的超时时间，避免长时间等待
    const res = await request({
      url: '/announcements/pinned',
      method: 'get',
      timeout: 5000 // 5秒超时
    })
    pinnedAnnouncements.value = res.data
  } catch (error) {
    console.error('Failed to load announcements:', error)
    // 公告加载失败不影响页面正常显示
    if (error.code === 'ECONNABORTED') {
      console.log('Announcements request timed out, continuing without announcements')
    }
    // 设置空数组，避免显示错误
    pinnedAnnouncements.value = []
  } finally {
    announcementsLoading.value = false
  }
}

const viewCourse = (id) => {
  router.push(`/course/${id}`)
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

const getLevelName = (level) => {
  const names = {
    BEGINNER: '入门',
    INTERMEDIATE: '进阶',
    ADVANCED: '高级'
  }
  return names[level] || level
}

const getLevelType = (level) => {
  const types = {
    BEGINNER: 'success',
    INTERMEDIATE: 'warning',
    ADVANCED: 'danger'
  }
  return types[level] || 'info'
}
</script>

<style scoped>
/* Reset & Base */
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* Loading & Empty States */
.loading-container {
  padding: 3rem 0;
}

.empty-state {
  padding: 4rem 0;
  text-align: center;
}

/* Modern Navigation */
.modern-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 1rem 0;
}

.nav-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.brand-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.brand-text {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: #64748b;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.nav-link:hover,
.nav-link.active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.login-btn,
.signup-btn {
  border-radius: 12px !important;
  padding: 0.75rem 1.5rem !important;
  font-weight: 600 !important;
  transition: all 0.3s !important;
}

.login-btn {
  background: transparent !important;
  border: 2px solid #e2e8f0 !important;
  color: #64748b !important;
}

.login-btn:hover {
  border-color: #667eea !important;
  color: #667eea !important;
}

.signup-btn {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4) !important;
}

.signup-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4) !important;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 12px;
  transition: background 0.3s;
}

.user-avatar:hover {
  background: rgba(102, 126, 234, 0.1);
}

.user-avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

/* Hero Section */
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  padding-top: 80px;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.hero-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.hero-grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(circle at 2px 2px, rgba(255, 255, 255, 0.15) 1px, transparent 0);
  background-size: 40px 40px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% { transform: translate(0, 0); }
  100% { transform: translate(40px, 40px); }
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
  backdrop-filter: blur(10px);
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  right: 10%;
  animation-delay: 0s;
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.2), rgba(245, 158, 11, 0.1));
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: 20%;
  left: 5%;
  animation-delay: 2s;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.2), rgba(52, 211, 153, 0.1));
}

.shape-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 70%;
  animation-delay: 4s;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(96, 165, 250, 0.1));
}

.shape-4 {
  width: 120px;
  height: 120px;
  top: 30%;
  left: 15%;
  animation-delay: 1s;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(167, 139, 250, 0.1));
}

.shape-5 {
  width: 80px;
  height: 80px;
  bottom: 40%;
  right: 25%;
  animation-delay: 3s;
  background: linear-gradient(135deg, rgba(236, 72, 153, 0.2), rgba(251, 113, 133, 0.1));
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero-text {
  color: white;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  margin-bottom: var(--space-6);
  backdrop-filter: blur(10px);
  animation: fadeInUp 0.8s ease-out;
}

.hero-badge .el-icon {
  color: var(--accent);
}

.hero-features {
  display: flex;
  gap: var(--space-6);
  margin-bottom: var(--space-8);
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: var(--transition);
  animation: fadeInUp 0.8s ease-out;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
}

.feature-icon {
  width: 40px;
  height: 40px;
  background: var(--gradient-accent);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: var(--text-lg);
}

.feature-item span {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.hero-trust {
  margin-top: var(--space-8);
  animation: fadeInUp 1s ease-out;
}

.trust-text {
  font-size: var(--text-sm);
  opacity: 0.8;
  margin-bottom: var(--space-3);
}

.avatar-group {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.trust-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  margin-left: -8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: white;
  background: linear-gradient(45deg, #667eea 0%, #764ba2 100%);
}

.trust-avatar.avatar-1 {
  background: linear-gradient(45deg, #667eea 0%, #764ba2 100%);
}

.trust-avatar.avatar-2 {
  background: linear-gradient(45deg, #f093fb 0%, #f5576c 100%);
}

.trust-avatar.avatar-3 {
  background: linear-gradient(45deg, #4facfe 0%, #00f2fe 100%);
}

.trust-avatar.avatar-4 {
  background: linear-gradient(45deg, #43e97b 0%, #38f9d7 100%);
}

.trust-avatar:first-child {
  margin-left: 0;
}

.trust-more {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  margin-left: -8px;
  backdrop-filter: blur(10px);
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 1.5rem;
  letter-spacing: -0.02em;
}

.gradient-text {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin-bottom: 3rem;
  line-height: 1.6;
}

.hero-stats {
  display: flex;
  gap: 3rem;
  margin-bottom: 3rem;
}

.stat {
  text-align: center;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.875rem;
  opacity: 0.8;
  font-weight: 500;
}

.hero-actions {
  display: flex;
  gap: 1rem;
}

.cta-button {
  background: linear-gradient(135deg, #fbbf24, #f59e0b) !important;
  border: none !important;
  border-radius: 16px !important;
  padding: 1rem 2rem !important;
  font-size: 1.125rem !important;
  font-weight: 600 !important;
  box-shadow: 0 8px 30px rgba(251, 191, 36, 0.4) !important;
  transition: all 0.3s !important;
}

.cta-button:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 12px 40px rgba(251, 191, 36, 0.5) !important;
}

.outline-button {
  background: transparent !important;
  border: 2px solid rgba(255, 255, 255, 0.3) !important;
  color: white !important;
  border-radius: 16px !important;
  padding: 1rem 2rem !important;
  font-size: 1.125rem !important;
  font-weight: 600 !important;
  backdrop-filter: blur(10px) !important;
  transition: all 0.3s !important;
}

.outline-button:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
}

/* Hero Visual */
.hero-visual {
  position: relative;
  height: 600px;
}

.hero-image {
  position: relative;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-2xl);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
}

.hero-dashboard {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 320px;
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-2xl);
  overflow: hidden;
  animation: floatDashboard 3s ease-in-out infinite;
}

@keyframes floatDashboard {
  0%, 100% { transform: translate(-50%, -50%) translateY(0px); }
  50% { transform: translate(-50%, -50%) translateY(-10px); }
}

.dashboard-header {
  padding: var(--space-4);
  background: var(--gradient-background);
  border-bottom: 1px solid var(--gray-200);
}

.dashboard-tabs {
  display: flex;
  gap: var(--space-1);
}

.tab {
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--gray-600);
  cursor: pointer;
  transition: var(--transition);
}

.tab.active {
  background: var(--primary);
  color: white;
}

.dashboard-content {
  padding: var(--space-4);
}

.progress-item {
  margin-bottom: var(--space-4);
}

.progress-item:last-child {
  margin-bottom: 0;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.progress-info h4 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-900);
}

.progress-info span {
  font-size: var(--text-xs);
  color: var(--primary);
  font-weight: var(--font-medium);
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: var(--gray-200);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: var(--radius-full);
  transition: width 0.3s ease;
}

.floating-card {
  position: absolute;
  background: white;
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  box-shadow: var(--shadow-xl);
  border: 1px solid rgba(255, 255, 255, 0.8);
  animation: floatCard 4s ease-in-out infinite;
  backdrop-filter: blur(10px);
  min-width: 180px;
}

.card-1 {
  top: 10%;
  right: -15%;
  animation-delay: 0s;
}

.card-2 {
  bottom: 35%;
  left: -20%;
  animation-delay: 1.5s;
}

.card-3 {
  top: 40%;
  right: -10%;
  animation-delay: 3s;
}

@keyframes floatCard {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-15px);
  }
}

.card-icon {
  width: 48px;
  height: 48px;
  background: var(--gradient-primary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: var(--text-xl);
  flex-shrink: 0;
}

.card-content h4 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-900);
  margin-bottom: var(--space-1);
}

.card-content p {
  font-size: var(--text-xs);
  color: var(--gray-600);
}

.floating-notification {
  position: absolute;
  bottom: 10%;
  left: -25%;
  background: white;
  border-radius: var(--radius-lg);
  padding: var(--space-3);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--gray-200);
  animation: slideInLeft 2s ease-out infinite;
  min-width: 200px;
}

@keyframes slideInLeft {
  0%, 90%, 100% {
    transform: translateX(-20px);
    opacity: 0.8;
  }
  10%, 80% {
    transform: translateX(0);
    opacity: 1;
  }
}

.notification-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.notification-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.notification-content p {
  font-size: var(--text-xs);
  color: var(--gray-900);
  margin-bottom: var(--space-1);
}

.notification-content span {
  font-size: var(--text-xs);
  color: var(--gray-500);
}

/* Sections */
.features,
.courses-section {
  padding: 6rem 0;
  background: white;
}

.partners-section {
  padding: 6rem 0;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-top: 1px solid var(--gray-200);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #1f2937;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-header .el-button {
  font-size: 1.125rem !important;
  font-weight: 600 !important;
  padding: 0.75rem 1.5rem !important;
  border-radius: 12px !important;
}

/* Modern Announcements */
.announcements-modern {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
}

.announcement-modern {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.announcement-modern::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  transition: height 0.3s ease;
}

.announcement-modern:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: rgba(102, 126, 234, 0.2);
}

.announcement-modern:hover::before {
  height: 8px;
}

.announcement-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  color: white;
  font-size: 1.5rem;
}

.announcement-content {
  flex: 1;
}

.announcement-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.announcement-date {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

.announcement-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 1rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.announcement-excerpt {
  color: #6b7280;
  line-height: 1.6;
  font-size: 0.95rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Modern Courses Grid */
.courses-modern-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 2rem;
}

.course-modern-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.course-modern-card:hover {
  transform: translateY(-12px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border-color: rgba(102, 126, 234, 0.2);
}

.course-image {
  position: relative;
  aspect-ratio: 16/9;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.course-modern-card:hover .course-image img {
  transform: scale(1.1);
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8), rgba(118, 75, 162, 0.8));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
}

.course-modern-card:hover .course-overlay {
  opacity: 1;
}

.course-overlay .el-button {
  background: rgba(255, 255, 255, 0.9) !important;
  border: none !important;
  color: #667eea !important;
  font-size: 1.5rem !important;
  width: 60px !important;
  height: 60px !important;
  transform: scale(0.8);
  transition: all 0.3s ease !important;
}

.course-modern-card:hover .course-overlay .el-button {
  transform: scale(1);
  background: white !important;
}

.course-badges {
  position: absolute;
  top: 1rem;
  left: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: flex-start;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.badge-free {
  background: rgba(16, 185, 129, 0.9);
  color: white;
}

.badge-level {
  background: rgba(255, 255, 255, 0.9);
  color: #374151;
}

.course-info {
  padding: 1.5rem;
}

.course-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8rem;
}

.course-subtitle {
  color: #6b7280;
  font-size: 0.875rem;
  line-height: 1.5;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.625rem;
}

.course-meta {
  display: flex;
  gap: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #f3f4f6;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

.meta-item .el-icon {
  color: #9ca3af;
  font-size: 0.875rem;
}

/* Modern Footer */
.modern-footer {
  background: #1f2937;
  color: white;
  padding: 4rem 0 2rem;
}

.footer-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 4rem;
  margin-bottom: 3rem;
}

.footer-section .brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.footer-section .brand-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.footer-section .brand-text {
  font-size: 1.25rem;
  font-weight: 700;
}

.footer-section p {
  color: #9ca3af;
  line-height: 1.6;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 2rem;
}

.link-group h4 {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: white;
}

.link-group a {
  display: block;
  color: #9ca3af;
  text-decoration: none;
  margin-bottom: 0.5rem;
  transition: color 0.3s;
}

.link-group a:hover {
  color: #667eea;
}

.footer-bottom {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid #374151;
  color: #9ca3af;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 3rem;
    text-align: center;
  }

  .hero-visual {
    height: 450px;
  }

  .hero-title {
    font-size: 3rem;
  }

  .hero-features {
    justify-content: center;
  }

  .floating-card {
    display: none;
  }

  .floating-notification {
    display: none;
  }

  .courses-modern-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 1.5rem;
  }

  .announcements-modern {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 1rem;
  }

  .nav-content {
    padding: 0 1rem;
  }

  .nav-links {
    display: none;
  }

  .hero {
    padding-top: 60px;
  }

  .hero-visual {
    height: 350px;
  }

  .hero-dashboard {
    width: 280px;
  }

  .hero-title {
    font-size: 2.5rem;
    line-height: 1.2;
  }

  .hero-subtitle {
    font-size: 1rem;
    margin-bottom: 2rem;
  }

  .hero-features {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-4);
    margin-bottom: var(--space-6);
  }

  .feature-item {
    flex-direction: column;
    text-align: center;
    padding: var(--space-4);
  }

  .hero-stats {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  .hero-actions {
    flex-direction: column;
    gap: var(--space-3);
  }

  .cta-button,
  .outline-button {
    width: 100%;
    justify-content: center;
  }

  .hero-trust {
    margin-top: var(--space-6);
  }

  .section-title {
    font-size: 2rem;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }

  .hero-features {
    grid-template-columns: 1fr;
  }

  .hero-stats {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .stat-number {
    font-size: 2rem;
  }

  .hero-dashboard {
    width: 260px;
  }

  .hero-visual {
    height: 300px;
  }

  .trust-avatars {
    justify-content: center;
  }
}

.avatar-placeholder {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(45deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
  color: white;
}

/* Modern Navigation Styles */
.modern-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--gray-200);
  transition: all 0.3s ease;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.modern-nav-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.modern-nav-brand {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  text-decoration: none;
  transition: var(--transition);
}

.modern-nav-brand:hover {
  color: var(--primary);
}

.brand-icon {
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  border-radius: var(--radius);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.modern-nav-links {
  display: flex;
  align-items: center;
  gap: var(--space-8);
}

.modern-nav-link {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--gray-600);
  text-decoration: none;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius);
  transition: var(--transition);
  position: relative;
}

.modern-nav-link:hover {
  color: var(--primary);
  background: rgba(102, 126, 234, 0.1);
}

.modern-nav-link.active {
  color: var(--primary);
  background: rgba(102, 126, 234, 0.1);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.modern-btn {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border: none;
  cursor: pointer;
  transition: var(--transition);
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
}

.modern-btn-secondary {
  background: var(--gray-100);
  color: var(--gray-700);
}

.modern-btn-secondary:hover {
  background: var(--gray-200);
  color: var(--gray-900);
}

.modern-btn-primary {
  background: var(--gradient-primary);
  color: white;
}

.modern-btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-lg);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition);
  border: 1px solid var(--gray-200);
  background: white;
}

.user-profile:hover {
  background: var(--gray-50);
  border-color: var(--primary);
}

.user-profile img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

/* Partners Section Styles */
.section-header-center {
  text-align: center;
  margin-bottom: 4rem;
}

.section-subtitle {
  font-size: var(--text-lg);
  color: var(--gray-600);
  margin-top: var(--space-4);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.partners-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: var(--space-8);
  margin-bottom: 4rem;
  align-items: center;
}

.partner-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  transition: var(--transition);
  border: 1px solid var(--gray-200);
}

.partner-logo:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary);
}

.logo-placeholder {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-700);
  text-align: center;
  padding: var(--space-2);
}

.certification-badges {
  display: flex;
  justify-content: center;
  gap: var(--space-6);
  flex-wrap: wrap;
}

.cert-badge {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  border: 2px solid transparent;
  background-clip: padding-box;
  transition: var(--transition);
  position: relative;
}

.cert-badge::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  z-index: -1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.cert-badge:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  color: white;
}

.cert-badge:hover::before {
  opacity: 1;
}

.cert-badge .el-icon {
  color: var(--primary);
  font-size: var(--text-lg);
  transition: var(--transition);
}

.cert-badge:hover .el-icon {
  color: white;
}

.cert-badge span {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-700);
  transition: var(--transition);
}

.cert-badge:hover span {
  color: white;
}

/* Responsive for Partners */
@media (max-width: 768px) {
  .partners-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-4);
  }

  .certification-badges {
    flex-direction: column;
    align-items: center;
    gap: var(--space-3);
  }

  .cert-badge {
    width: 100%;
    max-width: 300px;
    justify-content: center;
  }
}

</style>