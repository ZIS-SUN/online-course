<template>
  <div class="courses-container">
    <!-- Modern Navigation -->
    <nav class="modern-nav">
      <div class="container">
        <div class="modern-nav-content">
          <a href="/" class="modern-nav-brand">
            <div class="brand-icon">
              <el-icon :size="24"><School /></el-icon>
            </div>
            智慧学院
          </a>

          <div class="modern-nav-links">
            <router-link to="/" class="modern-nav-link">首页</router-link>
            <router-link to="/courses" class="modern-nav-link active">课程</router-link>
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

    <!-- Page Header -->
    <div class="modern-page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-text">
            <h1 class="modern-page-title">专业课程体系</h1>
            <p class="modern-page-subtitle">汇聚行业顶级专家智慧，打造企业级技能认证培训平台</p>
          </div>
          <div class="header-stats">
            <div class="stat-card">
              <div class="stat-number">2,500+</div>
              <div class="stat-label">专业课程</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">150K+</div>
              <div class="stat-label">认证学员</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">98%</div>
              <div class="stat-label">企业认可度</div>
            </div>
          </div>
        </div>

        <!-- Advanced Search Section -->
        <div class="search-hero">
          <div class="search-container">
            <div class="search-main">
              <div class="search-input-wrapper">
                <el-icon class="search-icon"><Search /></el-icon>
                <input
                  v-model="searchKeyword"
                  class="search-input"
                  placeholder="搜索专业课程、技术认证、行业专家..."
                  @keyup.enter="handleSearch"
                  @input="handleLiveSearch"
                />
                <button
                  v-if="searchKeyword"
                  class="clear-search-btn"
                  @click="clearSearch"
                >
                  <el-icon><Close /></el-icon>
                </button>
              </div>
              <button class="modern-btn modern-btn-primary modern-btn-lg search-btn" @click="handleSearch">
                <el-icon><Search /></el-icon>
搜索专业课程
              </button>
            </div>

            <!-- Search Suggestions -->
            <div v-if="showSuggestions && searchSuggestions.length > 0" class="search-suggestions">
              <div class="suggestions-header">
                <span>热门搜索</span>
              </div>
              <div class="suggestions-list">
                <button
                  v-for="suggestion in searchSuggestions"
                  :key="suggestion"
                  class="suggestion-item"
                  @click="applySuggestion(suggestion)"
                >
                  <el-icon><Search /></el-icon>
                  {{ suggestion }}
                </button>
              </div>
            </div>

            <!-- Quick Filters -->
            <div class="quick-filters">
              <button
                v-for="filter in quickFilters"
                :key="filter.key"
                class="quick-filter-btn"
                :class="{ active: isQuickFilterActive(filter) }"
                @click="applyQuickFilter(filter)"
              >
                <el-icon v-if="filter.icon" :class="filter.icon" />
                {{ filter.label }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <main class="main-content">
      <div class="container">
        <!-- Enhanced Filters -->
        <div class="filters-section">
          <div class="filters-container">
            <div class="filter-group">
              <h3 class="filter-title">
                <el-icon><TrendCharts /></el-icon>
                课程难度
              </h3>
              <div class="filter-options">
                <button
                  v-for="level in levels"
                  :key="level.value"
                  class="filter-chip enhanced-chip"
                  :class="{ active: selectedLevel === level.value }"
                  @click="handleLevelChange(level.value)"
                >
                  {{ level.label }}
                </button>
              </div>
            </div>

            <div class="filter-group">
              <h3 class="filter-title">
                <el-icon><PriceTag /></el-icon>
                课程类型
              </h3>
              <div class="filter-options">
                <button
                  class="filter-chip enhanced-chip"
                  :class="{ active: showFreeOnly === null }"
                  @click="handleFreeFilter(null)"
                >
                  全部
                </button>
                <button
                  class="filter-chip enhanced-chip"
                  :class="{ active: showFreeOnly === true }"
                  @click="handleFreeFilter(true)"
                >
                  <el-icon><Present /></el-icon>
                  免费课程
                </button>
                <button
                  class="filter-chip enhanced-chip"
                  :class="{ active: showFreeOnly === false }"
                  @click="handleFreeFilter(false)"
                >
                  <el-icon><Star /></el-icon>
                  付费课程
                </button>
              </div>
            </div>

            <div class="filter-actions">
              <button class="modern-btn modern-btn-secondary" @click="clearFilters">
                <el-icon><Refresh /></el-icon>
                清除筛选
              </button>
            </div>
          </div>
        </div>

        <!-- Course Grid -->
        <div class="courses-section">
          <div class="section-header">
            <div class="header-left">
              <h2 class="section-title">
                {{ getResultsTitle() }}
              </h2>
              <div class="results-info">
                <el-icon><DataBoard /></el-icon>
                共找到 {{ totalCourses }} 门课程
              </div>
            </div>
            <div class="header-right">
              <div class="view-options">
                <button class="view-btn active" title="网格视图">
                  <el-icon><Grid /></el-icon>
                </button>
                <button class="view-btn" title="列表视图">
                  <el-icon><List /></el-icon>
                </button>
              </div>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="loading" class="loading-grid">
            <div v-for="i in 9" :key="i" class="course-card-skeleton">
              <div class="skeleton-image"></div>
              <div class="skeleton-content">
                <div class="skeleton-title"></div>
                <div class="skeleton-subtitle"></div>
                <div class="skeleton-meta">
                  <div class="skeleton-meta-item"></div>
                  <div class="skeleton-meta-item"></div>
                  <div class="skeleton-meta-item"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="courses-grid enhanced-grid compact-grid" v-else-if="courses.length > 0">
            <div
              v-for="course in courses"
              :key="course.id"
              class="course-card modern-card enhanced-card"
              :class="{ 'loading': loadingCourseId === course.id }"
              @click="viewCourse(course.id)"
            >
              <div class="course-image">
                <img :src="course.coverUrl || '/default-cover.jpg'" :alt="course.title" loading="lazy" />
                <div class="course-overlay">
                  <div class="overlay-content">
                    <button class="play-button">
                      <el-icon><VideoPlay /></el-icon>
                    </button>
                    <div class="quick-actions">
                      <button class="action-btn" title="收藏">
                        <el-icon><Star /></el-icon>
                      </button>
                      <button class="action-btn" title="分享">
                        <el-icon><Share /></el-icon>
                      </button>
                    </div>
                  </div>
                </div>
                <div class="course-badges">
                  <span v-if="course.isFree" class="modern-tag modern-tag-success">免费</span>
                  <span class="modern-tag" :class="getLevelTagClass(course.level)">
                    {{ getLevelName(course.level) }}
                  </span>
                </div>
                <div class="course-progress" v-if="course.progress">
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{ width: course.progress + '%' }"></div>
                  </div>
                  <span class="progress-text">{{ course.progress }}%</span>
                </div>
              </div>

              <div class="course-content">
                <div class="content-header">
                  <h3 class="course-title">{{ course.title }}</h3>
                  <div class="course-actions">
                    <button class="action-icon" @click.stop="toggleFavorite(course.id)">
                      <el-icon><Star /></el-icon>
                    </button>
                    <button class="action-icon" @click.stop="shareAction(course)">
                      <el-icon><Share /></el-icon>
                    </button>
                  </div>
                </div>
                <p class="course-subtitle">{{ course.subtitle }}</p>

                <div class="course-meta">
                  <div class="meta-row">
                    <div class="meta-item">
                      <el-icon><User /></el-icon>
                      <span>{{ formatNumber(course.studentCount || 0) }}</span>
                    </div>
                    <div class="meta-item">
                      <el-icon><VideoPlay /></el-icon>
                      <span>{{ course.videoCount || 0 }}</span>
                    </div>
                    <div class="meta-item">
                      <el-icon><Clock /></el-icon>
                      <span>{{ course.duration || '2h' }}</span>
                    </div>
                  </div>
                </div>

                <div class="course-footer">
                  <div class="footer-left">
                    <div class="course-rating">
                      <div class="stars">
                        <el-icon v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.round(course.rating || 4.8) }">
                          <Star />
                        </el-icon>
                      </div>
                      <span class="rating-text">{{ course.rating || 4.8 }}</span>
                    </div>
                  </div>
                  <div class="footer-right">
                    <div class="course-price">
                      <span v-if="course.isFree" class="free-price">免费</span>
                      <template v-else>
                        <span class="current-price">¥{{ course.price || '199' }}</span>
                        <span v-if="course.originalPrice" class="original-price">¥{{ course.originalPrice }}</span>
                      </template>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-state">
            <div class="empty-icon">
              <el-icon :size="64"><Search /></el-icon>
            </div>
            <h3>没有找到相关课程</h3>
            <p>试试调整搜索关键词或筛选条件</p>
            <button class="modern-btn modern-btn-primary" @click="clearFilters">
              清除筛选
            </button>
          </div>

          <!-- Pagination -->
          <div class="pagination-container" v-if="totalPages > 1">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="totalCourses"
              layout="prev, pager, next, total"
              @current-change="handlePageChange"
              class="modern-pagination"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getCourses } from '../api/course'
import { ElMessage } from 'element-plus'
import { School, ArrowDown, Present, Star, Clock, Sunrise, Medal } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// Reactive data
const searchKeyword = ref('')
const selectedLevel = ref(null)
const showFreeOnly = ref(null)
const courses = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const totalCourses = ref(0)
const loading = ref(false)
const loadingCourseId = ref(null)

// Enhanced search features
const showSuggestions = ref(false)
const searchSuggestions = ref(['JavaScript', 'React', 'Vue.js', 'Python', 'Node.js', 'AI人工智能', '数据科学'])
const quickFilters = ref([
  { key: 'popular', label: '最受欢迎', icon: 'Star' },
  { key: 'latest', label: '最新发布', icon: 'Clock' },
  { key: 'free', label: '免费课程', icon: 'Present' },
  { key: 'beginner', label: '适合入门', icon: 'Sunrise' },
  { key: 'certification', label: '带认证', icon: 'Medal' }
])
const activeQuickFilters = ref([])

// Computed
const totalPages = computed(() => Math.ceil(totalCourses.value / pageSize.value))

// Level options
const levels = [
  { value: null, label: '全部' },
  { value: 'BEGINNER', label: '入门' },
  { value: 'INTERMEDIATE', label: '进阶' },
  { value: 'ADVANCED', label: '高级' }
]

// Lifecycle
onMounted(async () => {
  await loadCourses()
  if (authStore.token && !authStore.user) {
    await authStore.fetchCurrentUser()
  }
})

// Methods
const loadCourses = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    if (selectedLevel.value) {
      params.level = selectedLevel.value
    }

    if (showFreeOnly.value !== null) {
      params.isFree = showFreeOnly.value
    }

    const res = await getCourses(params)

    // Simulate network delay for better UX (remove in production)
    await new Promise(resolve => setTimeout(resolve, 300))

    courses.value = res.data.content || []
    totalCourses.value = res.data.totalElements || 0
  } catch (error) {
    console.error('Failed to load courses:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadCourses()
}

const handleLevelChange = (level) => {
  selectedLevel.value = selectedLevel.value === level ? null : level
  currentPage.value = 1
  loadCourses()
}

const handleFreeFilter = (isFree) => {
  showFreeOnly.value = isFree
  currentPage.value = 1
  loadCourses()
}

const clearFilters = () => {
  searchKeyword.value = ''
  selectedLevel.value = null
  showFreeOnly.value = null
  currentPage.value = 1
  loadCourses()
}

const handlePageChange = () => {
  loadCourses()
}

const viewCourse = (id) => {
  loadingCourseId.value = id
  // Add smooth transition delay
  setTimeout(() => {
    router.push(`/course/${id}`)
  }, 200)
}

const formatNumber = (num) => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

const getResultsTitle = () => {
  if (searchKeyword.value) {
    return `"${searchKeyword.value}" 的搜索结果`
  }
  if (selectedLevel.value) {
    const level = levels.find(l => l.value === selectedLevel.value)
    return `${level?.label}课程`
  }
  if (showFreeOnly.value === true) {
    return '免费课程'
  }
  if (showFreeOnly.value === false) {
    return '付费课程'
  }
  return '全部课程'
}

const getLevelName = (level) => {
  const names = {
    BEGINNER: '入门',
    INTERMEDIATE: '进阶',
    ADVANCED: '高级'
  }
  return names[level] || level
}

const getLevelTagClass = (level) => {
  const classes = {
    BEGINNER: 'modern-tag-primary',
    INTERMEDIATE: 'modern-tag-warning',
    ADVANCED: 'modern-tag-error'
  }
  return classes[level] || 'modern-tag-primary'
}

// Enhanced search methods
const handleLiveSearch = () => {
  showSuggestions.value = searchKeyword.value.length > 0
}

const clearSearch = () => {
  searchKeyword.value = ''
  showSuggestions.value = false
  loadCourses()
}

const applySuggestion = (suggestion) => {
  searchKeyword.value = suggestion
  showSuggestions.value = false
  handleSearch()
}

const isQuickFilterActive = (filter) => {
  return activeQuickFilters.value.includes(filter.key)
}

const applyQuickFilter = (filter) => {
  const index = activeQuickFilters.value.indexOf(filter.key)

  if (index > -1) {
    activeQuickFilters.value.splice(index, 1)
  } else {
    // Remove conflicting filters
    if (filter.key === 'free') {
      showFreeOnly.value = true
    } else if (filter.key === 'beginner') {
      selectedLevel.value = 'BEGINNER'
    }

    activeQuickFilters.value.push(filter.key)
  }

  loadCourses()
}

// Focus management for search
let searchTimeout = null
onMounted(() => {
  // Add click outside listener for suggestions
  document.addEventListener('click', (e) => {
    if (!e.target.closest('.search-hero')) {
      showSuggestions.value = false
    }
  })
})
</script>

<style scoped>
.courses-container {
  min-height: 100vh;
  background: var(--gray-50);
}

/* Brand Icon */
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

/* User Profile */
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

/* Nav Actions */
.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

/* Enhanced Search Hero */
.search-hero {
  margin-top: var(--space-12);
  position: relative;
}

.search-container {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

.search-main {
  display: flex;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.search-input-wrapper {
  position: relative;
  flex: 1;
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray-400);
  font-size: var(--text-lg);
  z-index: 2;
}

.search-input {
  width: 100%;
  padding: var(--space-4) var(--space-12) var(--space-4) var(--space-12);
  font-size: var(--text-lg);
  border: 2px solid var(--gray-200);
  border-radius: var(--radius-lg);
  background: white;
  transition: var(--transition);
  box-shadow: var(--shadow-lg);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.clear-search-btn {
  position: absolute;
  right: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  background: var(--gray-100);
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
  color: var(--gray-500);
}

.clear-search-btn:hover {
  background: var(--gray-200);
  color: var(--gray-700);
}

.search-btn {
  white-space: nowrap;
  padding: var(--space-4) var(--space-6);
}

/* Search Suggestions */
.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--gray-200);
  z-index: 1000;
  margin-top: var(--space-2);
}

.suggestions-header {
  padding: var(--space-3) var(--space-4);
  border-bottom: 1px solid var(--gray-200);
  background: var(--gray-50);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.suggestions-header span {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-600);
}

.suggestions-list {
  padding: var(--space-2);
  max-height: 200px;
  overflow-y: auto;
}

.suggestion-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  background: none;
  border: none;
  border-radius: var(--radius);
  cursor: pointer;
  transition: var(--transition);
  font-size: var(--text-sm);
  color: var(--gray-700);
  text-align: left;
}

.suggestion-item:hover {
  background: var(--gray-50);
  color: var(--primary);
}

.suggestion-item .el-icon {
  color: var(--gray-400);
  font-size: var(--text-sm);
}

/* Quick Filters */
.quick-filters {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.quick-filter-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: white;
  border: 1px solid var(--gray-300);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--gray-600);
  cursor: pointer;
  transition: var(--transition);
  box-shadow: var(--shadow);
}

.quick-filter-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: rgba(102, 126, 234, 0.05);
}

.quick-filter-btn.active {
  background: var(--primary);
  border-color: var(--primary);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.quick-filter-btn .el-icon {
  font-size: var(--text-sm);
}

/* Main Content */
.main-content {
  padding: var(--space-8) 0;
}

/* Filters Section */
.filters-section {
  display: flex;
  gap: var(--space-6);
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: var(--space-8);
  padding: var(--space-6);
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.filter-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-700);
  margin: 0;
  white-space: nowrap;
}

.filter-options {
  display: flex;
  gap: var(--space-2);
}

.filter-chip {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border: 1px solid var(--gray-300);
  border-radius: var(--radius-full);
  background: white;
  color: var(--gray-700);
  cursor: pointer;
  transition: var(--transition);
  white-space: nowrap;
}

.filter-chip:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.filter-chip.active {
  background: var(--primary);
  border-color: var(--primary);
  color: white;
}

.filter-actions {
  margin-left: auto;
}

/* Course Section */
.courses-section {
  margin-top: var(--space-8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.section-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
}

.results-info {
  color: var(--gray-500);
  font-size: var(--text-sm);
}

/* Course Grid */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--space-6);
}

.enhanced-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--space-6);
}

.compact-grid {
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--space-4);
}

/* Course Cards */
.course-card {
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.enhanced-card {
  border: 1px solid var(--gray-200);
  background: white;
}

.enhanced-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  border-color: var(--primary);
}

.enhanced-card.loading {
  opacity: 0.7;
  transform: scale(0.98);
  pointer-events: none;
}

/* Loading Skeleton */
.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--space-6);
}

.course-card-skeleton {
  background: white;
  border-radius: var(--radius-xl);
  overflow: hidden;
  border: 1px solid var(--gray-200);
  animation: pulse 2s infinite;
}

.skeleton-image {
  height: 200px;
  background: linear-gradient(90deg, var(--gray-200) 0%, var(--gray-300) 50%, var(--gray-200) 100%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-content {
  padding: var(--space-6);
}

.skeleton-title {
  height: 20px;
  background: linear-gradient(90deg, var(--gray-200) 0%, var(--gray-300) 50%, var(--gray-200) 100%);
  background-size: 200% 100%;
  border-radius: var(--radius);
  margin-bottom: var(--space-3);
  animation: shimmer 1.5s infinite;
}

.skeleton-subtitle {
  height: 16px;
  background: linear-gradient(90deg, var(--gray-200) 0%, var(--gray-300) 50%, var(--gray-200) 100%);
  background-size: 200% 100%;
  border-radius: var(--radius);
  width: 80%;
  margin-bottom: var(--space-4);
  animation: shimmer 1.5s infinite;
}

.skeleton-meta {
  display: flex;
  gap: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--gray-200);
}

.skeleton-meta-item {
  height: 12px;
  background: linear-gradient(90deg, var(--gray-200) 0%, var(--gray-300) 50%, var(--gray-200) 100%);
  background-size: 200% 100%;
  border-radius: var(--radius);
  flex: 1;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

.course-image {
  position: relative;
  aspect-ratio: 16/9;
  overflow: hidden;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.course-card:hover .course-image img {
  transform: scale(1.1);
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.3));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.course-card:hover .course-overlay {
  opacity: 1;
}

.overlay-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
}

.quick-actions {
  display: flex;
  gap: var(--space-3);
  transform: translateY(20px);
  transition: transform 0.3s ease 0.1s;
}

.course-card:hover .quick-actions {
  transform: translateY(0);
}

.action-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: var(--text-lg);
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1);
}

.play-button {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
  font-size: var(--text-3xl);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.play-button:hover {
  background: white;
  transform: scale(1.15);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.course-badges {
  position: absolute;
  top: var(--space-4);
  left: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  z-index: 2;
}

.course-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.8);
  padding: var(--space-2) var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.progress-bar {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: var(--radius-full);
  transition: width 0.3s ease;
}

.progress-text {
  color: white;
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  min-width: 32px;
}

.course-content {
  padding: var(--space-5);
}

.course-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin-bottom: var(--space-2);
  line-height: var(--leading-tight);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-subtitle {
  color: var(--gray-600);
  font-size: var(--text-sm);
  line-height: var(--leading-relaxed);
  margin-bottom: var(--space-4);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--gray-200);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xs);
  color: var(--gray-500);
  font-weight: var(--font-medium);
}

.meta-item .el-icon {
  color: var(--gray-400);
}

/* Course Footer */
.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--gray-100);
}

.course-rating {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.stars {
  display: flex;
  gap: 1px;
}

.star {
  color: var(--gray-300);
  font-size: var(--text-sm);
  transition: color 0.2s ease;
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  font-size: var(--text-xs);
  color: var(--gray-600);
  font-weight: var(--font-medium);
}

.course-price {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.current-price {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--primary);
}

.original-price {
  font-size: var(--text-sm);
  color: var(--gray-400);
  text-decoration: line-through;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: var(--space-12);
  color: var(--gray-500);
}

.empty-icon {
  margin-bottom: var(--space-6);
  color: var(--gray-300);
}

.empty-state h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--gray-700);
  margin-bottom: var(--space-3);
}

.empty-state p {
  margin-bottom: var(--space-6);
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: var(--space-8);
}

/* Responsive Design */
@media (max-width: 1024px) {
  .courses-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }

  .search-main {
    flex-direction: column;
    gap: var(--space-3);
  }

  .search-input-wrapper {
    order: 1;
  }

  .search-btn {
    order: 2;
    width: 100%;
  }
}

@media (max-width: 768px) {
  .modern-nav-content {
    padding: 0 var(--space-4);
  }

  .modern-nav-links {
    display: none;
  }

  .nav-actions {
    gap: var(--space-2);
  }

  .nav-actions .modern-btn {
    padding: var(--space-2) var(--space-3);
    font-size: var(--text-sm);
  }

  .modern-page-header {
    padding: var(--space-8) 0;
    text-align: center;
  }

  .modern-page-title {
    font-size: var(--text-2xl);
  }

  .search-hero {
    margin-top: var(--space-6);
  }

  .search-input {
    font-size: var(--text-base);
    padding: var(--space-3) var(--space-10) var(--space-3) var(--space-10);
  }

  .search-btn {
    padding: var(--space-3) var(--space-4);
    font-size: var(--text-sm);
  }

  .quick-filters {
    gap: var(--space-2);
  }

  .quick-filter-btn {
    padding: var(--space-2) var(--space-3);
    font-size: var(--text-xs);
  }

  .filters-section {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-4);
    padding: var(--space-4);
  }

  .filter-group {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
  }

  .filter-title {
    font-size: var(--text-xs);
    margin-bottom: var(--space-1);
  }

  .filter-options {
    flex-wrap: wrap;
    gap: var(--space-2);
  }

  .filter-chip {
    padding: var(--space-2) var(--space-3);
    font-size: var(--text-xs);
  }

  .filter-actions {
    margin-left: 0;
    margin-top: var(--space-2);
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
    text-align: center;
  }

  .section-title {
    font-size: var(--text-xl);
  }

  .courses-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .loading-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .course-card {
    margin: 0 var(--space-2);
  }

  .enhanced-card:hover {
    transform: translateY(-4px);
  }

  .play-button {
    width: 60px;
    height: 60px;
    font-size: var(--text-xl);
  }

  .quick-actions {
    gap: var(--space-2);
  }

  .action-btn {
    width: 36px;
    height: 36px;
    font-size: var(--text-base);
  }

  .course-content {
    padding: var(--space-4);
  }

  .course-title {
    font-size: var(--text-base);
  }

  .course-subtitle {
    font-size: var(--text-xs);
  }

  .course-meta {
    gap: var(--space-3);
  }

  .meta-item {
    font-size: var(--text-xs);
  }

  .main-content {
    padding: var(--space-8) 0;
  }

  .empty-state {
    padding: var(--space-12);
  }

  .empty-state h3 {
    font-size: var(--text-lg);
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 var(--space-3);
  }

  .modern-nav-brand {
    font-size: var(--text-lg);
  }

  .brand-icon {
    width: 28px;
    height: 28px;
  }

  .user-profile {
    padding: var(--space-2) var(--space-3);
    gap: var(--space-2);
  }

  .user-profile img {
    width: 28px;
    height: 28px;
  }

  .modern-page-title {
    font-size: var(--text-xl);
  }

  .modern-page-subtitle {
    font-size: var(--text-sm);
  }

  .search-input {
    padding: var(--space-3) var(--space-8) var(--space-3) var(--space-8);
  }

  .search-icon,
  .clear-search-btn {
    font-size: var(--text-base);
  }

  .search-icon {
    left: var(--space-3);
  }

  .clear-search-btn {
    right: var(--space-3);
    width: 28px;
    height: 28px;
  }

  .quick-filter-btn {
    padding: var(--space-1) var(--space-2);
    font-size: var(--text-xs);
  }

  .course-card {
    margin: 0;
  }

  .course-badges {
    top: var(--space-2);
    left: var(--space-2);
  }

  .course-badges .modern-tag {
    font-size: var(--text-xs);
    padding: var(--space-1) var(--space-2);
  }

  .play-button {
    width: 50px;
    height: 50px;
    font-size: var(--text-lg);
  }

  .action-btn {
    width: 32px;
    height: 32px;
    font-size: var(--text-sm);
  }

  .course-content {
    padding: var(--space-3);
  }

  .course-title {
    font-size: var(--text-sm);
    margin-bottom: var(--space-1);
  }

  .course-subtitle {
    font-size: var(--text-xs);
    margin-bottom: var(--space-3);
  }

  .course-meta {
    gap: var(--space-2);
    flex-wrap: wrap;
  }

  .meta-item {
    font-size: 11px;
    gap: var(--space-1);
  }

  .course-footer {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
  }

  .course-rating {
    justify-content: center;
  }

  .course-price {
    justify-content: center;
  }

  .current-price {
    font-size: var(--text-base);
  }

  .original-price {
    font-size: var(--text-xs);
  }
}

/* Element Plus Overrides */
:deep(.el-dropdown-menu) {
  border-radius: var(--radius-md) !important;
  box-shadow: var(--shadow-xl) !important;
  border: 1px solid var(--gray-200) !important;
}

:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-pager li) {
  border-radius: var(--radius) !important;
  margin: 0 var(--space-1) !important;
}

:deep(.el-pager li.is-active) {
  background: var(--gradient-primary) !important;
  color: white !important;
}

/* Mobile pagination adjustments */
@media (max-width: 768px) {
  :deep(.el-pagination) {
    flex-wrap: wrap;
    gap: var(--space-2);
  }

  :deep(.el-pager li) {
    margin: 0 var(--space-1) !important;
    min-width: 32px;
    height: 32px;
    font-size: var(--text-sm) !important;
  }

  :deep(.el-pagination .btn-prev),
  :deep(.el-pagination .btn-next) {
    min-width: 32px;
    height: 32px;
    font-size: var(--text-sm) !important;
  }
}

/* Touch interactions for mobile */
@media (hover: none) and (pointer: coarse) {
  .enhanced-card:hover {
    transform: none;
  }

  .enhanced-card:active {
    transform: scale(0.98);
  }

  .course-overlay {
    opacity: 0;
  }

  .course-card:active .course-overlay {
    opacity: 1;
  }

  .play-button:hover {
    transform: none;
  }

  .play-button:active {
    transform: scale(0.95);
  }

  .action-btn:hover {
    transform: none;
  }

  .action-btn:active {
    transform: scale(0.9);
  }
}
</style>