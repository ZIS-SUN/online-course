<template>
  <div class="apple-admin">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <div class="logo-icon">
            <el-icon :size="24"><School /></el-icon>
          </div>
          <span class="logo-text">LearnSpace</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section">
          <div class="nav-section-title">概览</div>
          <div class="nav-item" :class="{ active: currentMenu === 'dashboard' }" @click="handleMenuSelect('dashboard')">
            <el-icon><HomeFilled /></el-icon>
            <span>仪表盘</span>
          </div>
          <div class="nav-item" :class="{ active: currentMenu === 'analytics' }" @click="handleMenuSelect('analytics')">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据分析</span>
          </div>
        </div>

        <div class="nav-section">
          <div class="nav-section-title">内容管理</div>
          <div class="nav-item" :class="{ active: currentMenu === 'courses' }" @click="handleMenuSelect('courses')">
            <el-icon><Reading /></el-icon>
            <span>课程管理</span>
            <span class="nav-badge">12</span>
          </div>
          <div class="nav-item" :class="{ active: currentMenu === 'categories' }" @click="handleMenuSelect('categories')">
            <el-icon><Grid /></el-icon>
            <span>分类管理</span>
          </div>
          <div class="nav-item" :class="{ active: currentMenu === 'announcements' }" @click="handleMenuSelect('announcements')">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </div>
        </div>

        <div class="nav-section">
          <div class="nav-section-title">用户管理</div>
          <div class="nav-item" :class="{ active: currentMenu === 'users' }" @click="handleMenuSelect('users')">
            <el-icon><User /></el-icon>
            <span>所有用户</span>
          </div>
          <div class="nav-item" :class="{ active: currentMenu === 'teachers' }" @click="handleMenuSelect('teachers')">
            <el-icon><UserFilled /></el-icon>
            <span>教师管理</span>
          </div>
          <div class="nav-item" :class="{ active: currentMenu === 'students' }" @click="handleMenuSelect('students')">
            <el-icon><Avatar /></el-icon>
            <span>学员管理</span>
          </div>
        </div>

        <div class="nav-section">
          <div class="nav-section-title">系统设置</div>
          <div class="nav-item" :class="{ active: currentMenu === 'settings' }" @click="handleMenuSelect('settings')">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </div>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="user-menu" @click="userMenuVisible = !userMenuVisible">
          <img :src="authStore.user?.avatarUrl || '/default-avatar.png'" class="user-avatar" />
          <div class="user-info">
            <div class="user-name">{{ authStore.user?.nickname || authStore.user?.username }}</div>
            <div class="user-role">管理员</div>
          </div>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <transition name="menu">
          <div v-if="userMenuVisible" class="user-dropdown">
            <div class="dropdown-item" @click="goToProfile">
              <el-icon><User /></el-icon>
              个人资料
            </div>
            <div class="dropdown-item" @click="goToSettings">
              <el-icon><Setting /></el-icon>
              设置
            </div>
            <div class="dropdown-divider"></div>
            <div class="dropdown-item danger" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </div>
          </div>
        </transition>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Top Bar -->
      <header class="top-bar">
        <div class="top-bar-left">
          <button class="menu-toggle">
            <el-icon><Menu /></el-icon>
          </button>
          <h1 class="page-title">{{ pageTitle }}</h1>
        </div>
        <div class="top-bar-right">
          <div class="search-box">
            <el-icon><Search /></el-icon>
            <input type="text" placeholder="Search..." v-model="searchQuery" @keyup.enter="handleSearch" />
          </div>
          <button class="icon-button">
            <el-icon><Bell /></el-icon>
            <span class="badge" v-if="notificationCount > 0">{{ notificationCount }}</span>
          </button>
          <button class="icon-button">
            <el-icon><Message /></el-icon>
          </button>
          <button class="icon-button">
            <el-icon><FullScreen /></el-icon>
          </button>
        </div>
      </header>

      <!-- Content Area -->
      <div class="content-wrapper">
        <!-- Dashboard Overview -->
        <div v-if="currentMenu === 'dashboard'" class="dashboard-overview">
          <!-- Welcome Section -->
          <div class="welcome-section">
            <div class="welcome-content">
              <h1>欢迎回来，{{ authStore.user?.nickname || '管理员' }}！</h1>
              <p>今天是个开始新工作的好日子</p>
            </div>
            <div class="welcome-actions">
              <el-button type="primary" @click="handleMenuSelect('courses')">
                <el-icon><Plus /></el-icon>
                添加课程
              </el-button>
              <el-button @click="handleMenuSelect('announcements')">
                <el-icon><Bell /></el-icon>
                发布公告
              </el-button>
            </div>
          </div>

          <!-- Stats Cards -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon courses">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ dashboardStats.totalCourses }}</div>
                <div class="stat-label">课程总数</div>
                <div class="stat-change positive">+12% 本月</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon users">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ dashboardStats.totalUsers }}</div>
                <div class="stat-label">注册用户</div>
                <div class="stat-change positive">+8% 本月</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon enrollments">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ dashboardStats.totalEnrollments }}</div>
                <div class="stat-label">课程报名</div>
                <div class="stat-change positive">+15% 本月</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon revenue">
                <el-icon><Coin /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">￥{{ dashboardStats.revenue }}</div>
                <div class="stat-label">本月收入</div>
                <div class="stat-change positive">+23% 本月</div>
              </div>
            </div>
          </div>

          <!-- Charts Section -->
          <div class="charts-section">
            <div class="chart-card">
              <div class="chart-header">
                <h3>用户增长趋势</h3>
                <div class="chart-period">
                  <el-button size="small" type="primary" plain>7天</el-button>
                  <el-button size="small" plain>30天</el-button>
                  <el-button size="small" plain>90天</el-button>
                </div>
              </div>
              <div class="chart-container" ref="userTrendChart"></div>
            </div>

            <div class="chart-card">
              <div class="chart-header">
                <h3>热门课程排行</h3>
                <el-button size="small" text type="primary">查看全部</el-button>
              </div>
              <div class="popular-courses">
                <div class="course-rank-item" v-for="(course, index) in popularCourses" :key="index">
                  <div class="rank-number">{{ index + 1 }}</div>
                  <img :src="course.cover" :alt="course.title" class="course-thumb" />
                  <div class="course-info">
                    <div class="course-title">{{ course.title }}</div>
                    <div class="course-stats">{{ course.enrollments }} 人报名</div>
                  </div>
                  <div class="course-trend">
                    <el-icon class="trend-up"><CaretTop /></el-icon>
                    <span>{{ course.growth }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Additional Charts -->
          <div class="charts-section">
            <div class="chart-card">
              <div class="chart-header">
                <h3>收入统计</h3>
                <el-button size="small" text type="primary">详细报告</el-button>
              </div>
              <div class="chart-container" ref="revenueChart"></div>
            </div>

            <div class="chart-card">
              <div class="chart-header">
                <h3>课程分类分布</h3>
                <el-button size="small" text type="primary">查看分析</el-button>
              </div>
              <div class="chart-container" ref="categoryChart"></div>
            </div>
          </div>

          <!-- Recent Activity -->
          <div class="activity-section">
            <div class="section-header">
              <h3>最近活动</h3>
              <el-button size="small" text type="primary">查看全部</el-button>
            </div>
            <div class="activity-list">
              <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
                <div class="activity-icon" :class="activity.type">
                  <el-icon><component :is="activity.icon" /></el-icon>
                </div>
                <div class="activity-content">
                  <div class="activity-title">{{ activity.title }}</div>
                  <div class="activity-desc">{{ activity.description }}</div>
                </div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </div>

        <Courses v-else-if="currentMenu === 'courses'" />
        <Categories v-else-if="currentMenu === 'categories'" />
        <Users v-else-if="currentMenu === 'users'" />
        <Announcements v-else-if="currentMenu === 'announcements'" />
        <Analytics v-else-if="currentMenu === 'analytics'" />
        <Settings v-else-if="currentMenu === 'settings'" />
        <div v-else class="coming-soon">
          <el-icon :size="64"><DocumentAdd /></el-icon>
          <h2>即将推出</h2>
          <p>该功能正在开发中</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import Courses from './Courses.vue'
import Users from './Users.vue'
import Announcements from './Announcements.vue'
import Categories from './Categories.vue'
import Analytics from './Analytics.vue'
import Settings from './Settings.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const currentMenu = ref('dashboard')
const userMenuVisible = ref(false)
const searchQuery = ref('')
const notificationCount = ref(3)

// Chart refs
const userTrendChart = ref(null)
const revenueChart = ref(null)
const categoryChart = ref(null)

const pageTitle = computed(() => {
  const titles = {
    'dashboard': '仪表盘',
    'courses': '课程管理',
    'categories': '分类管理',
    'announcements': '公告管理',
    'users': '用户管理',
    'teachers': '教师管理',
    'students': '学员管理',
    'settings': '系统设置',
    'analytics': '数据分析'
  }
  return titles[currentMenu.value] || '仪表盘'
})

onMounted(() => {
  // 设置初始菜单
  const pathMap = {
    '/admin/courses': 'courses',
    '/admin/users': 'users',
    '/admin/announcements': 'announcements'
  }
  currentMenu.value = pathMap[route.path] || 'dashboard'

  // 初始化图表
  nextTick(() => {
    initCharts()
  })
})

const handleMenuSelect = (key) => {
  currentMenu.value = key

  // 路由映射
  const routeMap = {
    'dashboard': '/admin',
    'courses': '/admin/courses',
    'categories': '/admin/categories',
    'users': '/admin/users',
    'announcements': '/admin/announcements',
    'analytics': '/admin/analytics',
    'settings': '/admin/settings'
  }

  if (routeMap[key]) {
    router.push(routeMap[key])
  }
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('Successfully signed out')
  router.push('/login')
}

const goToProfile = () => {
  router.push('/profile')
  userMenuVisible.value = false
}

const goToSettings = () => {
  currentMenu.value = 'settings'
  userMenuVisible.value = false
}

const handleSearch = () => {
  if (searchQuery.value) {
    ElMessage.info(`Searching for: ${searchQuery.value}`)
  }
}

// Dashboard data
const dashboardStats = ref({
  totalCourses: 156,
  totalUsers: 2834,
  totalEnrollments: 1247,
  revenue: '12,586'
})

const popularCourses = ref([
  {
    title: 'Vue.js 3.0 完全指南',
    cover: '/api/placeholder/60/40',
    enrollments: 1234,
    growth: 12
  },
  {
    title: 'React 前端开发实战',
    cover: '/api/placeholder/60/40',
    enrollments: 987,
    growth: 8
  },
  {
    title: 'Node.js 后端开发',
    cover: '/api/placeholder/60/40',
    enrollments: 756,
    growth: 15
  },
  {
    title: 'TypeScript 进阶教程',
    cover: '/api/placeholder/60/40',
    enrollments: 543,
    growth: 6
  }
])

const recentActivities = ref([
  {
    type: 'user',
    icon: 'User',
    title: '新用户注册',
    description: '张三刚刚注册了账号',
    time: '2分钟前'
  },
  {
    type: 'course',
    icon: 'Reading',
    title: '课程发布',
    description: '《Python 机器学习》课程已发布',
    time: '15分钟前'
  },
  {
    type: 'enrollment',
    icon: 'Trophy',
    title: '课程报名',
    description: '李四报名了《Vue.js 3.0 完全指南》',
    time: '1小时前'
  },
  {
    type: 'announcement',
    icon: 'Bell',
    title: '公告发布',
    description: '系统维护公告已发布',
    time: '2小时前'
  }
])

// Close user menu when clicking outside
watch(userMenuVisible, (newVal) => {
  if (newVal) {
    setTimeout(() => {
      document.addEventListener('click', closeUserMenu)
    }, 0)
  } else {
    document.removeEventListener('click', closeUserMenu)
  }
})

const closeUserMenu = (e) => {
  if (!e.target.closest('.user-menu') && !e.target.closest('.user-dropdown')) {
    userMenuVisible.value = false
  }
}

// 初始化图表
const initCharts = () => {
  // User Trend Chart
  if (userTrendChart.value) {
    const userChart = echarts.init(userTrendChart.value)
    const userOption = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['新增用户', '活跃用户'] },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '新增用户',
          type: 'line',
          data: [120, 200, 150, 80, 170, 110, 200],
          smooth: true,
          lineStyle: { color: '#0056d3' },
          itemStyle: { color: '#0056d3' }
        },
        {
          name: '活跃用户',
          type: 'line',
          data: [320, 450, 380, 290, 410, 350, 480],
          smooth: true,
          lineStyle: { color: '#10b981' },
          itemStyle: { color: '#10b981' }
        }
      ]
    }
    userChart.setOption(userOption)
  }

  // Revenue Chart
  if (revenueChart.value) {
    const revenueChartInstance = echarts.init(revenueChart.value)
    const revenueOption = {
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
      },
      yAxis: { type: 'value' },
      series: [
        {
          type: 'bar',
          data: [8500, 12300, 10200, 15600, 13400, 18900, 16700],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#ff6b35' },
              { offset: 1, color: '#ea580c' }
            ])
          }
        }
      ]
    }
    revenueChartInstance.setOption(revenueOption)
  }

  // Category Chart
  if (categoryChart.value) {
    const categoryChartInstance = echarts.init(categoryChart.value)
    const categoryOption = {
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [
        {
          type: 'pie',
          radius: '50%',
          data: [
            { value: 35, name: '编程开发' },
            { value: 25, name: '设计创意' },
            { value: 20, name: '数据科学' },
            { value: 12, name: '商业管理' },
            { value: 8, name: '语言学习' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    categoryChartInstance.setOption(categoryOption)
  }
}
</script>

<style scoped>
/* Apple Design System Inspired Styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.apple-admin {
  display: flex;
  height: 100vh;
  background: var(--gray-50);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Sidebar */
.sidebar {
  width: 280px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-header {
  padding: 28px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.logo {
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo-icon {
  width: 44px;
  height: 44px;
  background: var(--primary);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 86, 211, 0.3);
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.5px;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  padding: 20px 16px;
  overflow-y: auto;
  scrollbar-width: none;
}

.sidebar-nav::-webkit-scrollbar {
  display: none;
}

.nav-section {
  margin-bottom: 28px;
}

.nav-section-title {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.75px;
  color: #86868b;
  padding: 0 16px;
  margin-bottom: 10px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  margin: 3px 0;
  border-radius: 12px;
  color: #1d1d1f;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.04);
  transform: translateX(2px);
}

.nav-item.active {
  background: var(--primary);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 86, 211, 0.3);
}

.nav-item .el-icon {
  font-size: 20px;
}

.nav-badge {
  position: absolute;
  right: 16px;
  background: rgba(255, 59, 48, 0.9);
  color: white;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}

/* User Menu */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-menu:hover {
  background: rgba(0, 0, 0, 0.04);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.08);
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #86868b;
}

.user-dropdown {
  position: absolute;
  bottom: 70px;
  left: 16px;
  right: 16px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  padding: 8px;
  z-index: 1000;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  color: #1d1d1f;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.dropdown-item:hover {
  background: #f5f5f7;
}

.dropdown-item.danger {
  color: #ff3b30;
}

.dropdown-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.08);
  margin: 6px 8px;
}

/* Main Content */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Top Bar */
.top-bar {
  height: 72px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.top-bar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.menu-toggle {
  display: none;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.2s;
}

.menu-toggle:hover {
  background: rgba(0, 0, 0, 0.04);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.5px;
}

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(0, 0, 0, 0.04);
  border-radius: 12px;
  padding: 10px 16px;
  width: 280px;
  transition: all 0.2s;
}

.search-box:focus-within {
  background: rgba(0, 0, 0, 0.06);
  box-shadow: 0 0 0 4px rgba(0, 86, 211, 0.1);
}

.search-box input {
  border: none;
  background: none;
  outline: none;
  flex: 1;
  font-size: 14px;
  color: #1d1d1f;
  font-family: inherit;
}

.search-box input::placeholder {
  color: #86868b;
}

.icon-button {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  border: none;
  background: transparent;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.icon-button:hover {
  background: rgba(0, 0, 0, 0.04);
}

.icon-button:active {
  transform: scale(0.95);
}

.icon-button .badge {
  position: absolute;
  top: -2px;
  right: -2px;
  min-width: 20px;
  height: 20px;
  background: #ff3b30;
  color: white;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  border: 2px solid #f5f5f7;
}

/* Content Wrapper */
.content-wrapper {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  overflow-x: hidden;
}

.content-wrapper::-webkit-scrollbar {
  width: 10px;
}

.content-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.content-wrapper::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  border: 2px solid transparent;
  background-clip: padding-box;
}

.content-wrapper::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.15);
  background-clip: padding-box;
}

/* Coming Soon */
.coming-soon {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #86868b;
}

.coming-soon h2 {
  margin-top: 20px;
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
}

.coming-soon p {
  margin-top: 8px;
  font-size: 16px;
}

/* Dashboard Styles */
.dashboard-overview {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

/* Welcome Section */
.welcome-section {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-lg);
}

.welcome-content h1 {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
  color: white;
}

.welcome-content p {
  font-size: var(--text-lg);
  opacity: 0.9;
  color: white;
  margin-bottom: 0;
}

.welcome-actions {
  display: flex;
  gap: var(--space-3);
}

.welcome-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
}

.welcome-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.welcome-actions .el-button.el-button--primary {
  background: white;
  color: var(--primary);
  border: none;
}

.welcome-actions .el-button.el-button--primary:hover {
  background: var(--gray-100);
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
}

.stat-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-icon.courses {
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
}

.stat-icon.users {
  background: linear-gradient(135deg, var(--success), #059669);
}

.stat-icon.enrollments {
  background: linear-gradient(135deg, var(--warning), #d97706);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, var(--accent), #ea580c);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  line-height: 1;
  margin-bottom: var(--space-1);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--gray-600);
  margin-bottom: var(--space-1);
}

.stat-change {
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  padding: 2px 8px;
  border-radius: var(--radius-base);
  display: inline-block;
}

.stat-change.positive {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

/* Charts Section */
.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-6);
}

.chart-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.chart-header h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
}

.chart-period {
  display: flex;
  gap: var(--space-2);
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--gray-400);
  background: var(--gray-50);
  border-radius: var(--radius-lg);
}

.chart-placeholder p {
  margin-top: var(--space-3);
  font-size: var(--text-sm);
  color: var(--gray-500);
}

.chart-container {
  height: 300px;
  width: 100%;
}

/* Popular Courses */
.popular-courses {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.course-rank-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  transition: var(--transition);
}

.course-rank-item:hover {
  background: var(--gray-50);
}

.rank-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
}

.course-thumb {
  width: 40px;
  height: 30px;
  border-radius: var(--radius-base);
  object-fit: cover;
  background: var(--gray-200);
}

.course-info {
  flex: 1;
}

.course-title {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--gray-900);
  margin-bottom: 2px;
  line-height: 1.3;
}

.course-stats {
  font-size: var(--text-xs);
  color: var(--gray-500);
}

.course-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--success);
}

.trend-up {
  font-size: 12px;
}

/* Activity Section */
.activity-section {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.section-header h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.activity-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  transition: var(--transition);
}

.activity-item:hover {
  background: var(--gray-50);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.activity-icon.user {
  background: linear-gradient(135deg, var(--success), #059669);
}

.activity-icon.course {
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
}

.activity-icon.enrollment {
  background: linear-gradient(135deg, var(--warning), #d97706);
}

.activity-icon.announcement {
  background: linear-gradient(135deg, var(--accent), #ea580c);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--gray-900);
  margin-bottom: 2px;
}

.activity-desc {
  font-size: var(--text-xs);
  color: var(--gray-500);
}

.activity-time {
  font-size: var(--text-xs);
  color: var(--gray-400);
  font-weight: var(--font-medium);
}

/* Responsive Dashboard */
@media (max-width: 1024px) {
  .charts-section {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  }

  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: var(--space-6);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .welcome-actions {
    flex-direction: column;
    width: 100%;
  }

  .welcome-actions .el-button {
    width: 100%;
  }
}

/* Animations */
.menu-enter-active,
.menu-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-enter-from,
.menu-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* Responsive */
@media (max-width: 1024px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
    transform: translateX(-100%);
    box-shadow: 0 0 40px rgba(0, 0, 0, 0.1);
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .menu-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .content-wrapper {
    padding: 24px;
  }
}

@media (max-width: 768px) {
  .top-bar {
    padding: 0 20px;
  }

  .search-box {
    width: 200px;
  }

  .page-title {
    font-size: 22px;
  }

  .content-wrapper {
    padding: 16px;
  }
}
</style>