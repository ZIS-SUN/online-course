<template>
  <div class="course-detail-container">
    <!-- Modern Header -->
    <div class="modern-header">
      <div class="header-content">
        <el-button
          class="back-button"
          @click="router.back()"
          circle
        >
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <div class="breadcrumb">
          <span @click="router.push('/')">首页</span>
          <el-icon><ArrowRight /></el-icon>
          <span @click="router.push('/courses')">专业课程</span>
          <el-icon><ArrowRight /></el-icon>
          <span class="current">{{ course.title || '课程详情' }}</span>
        </div>
      </div>
    </div>

    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-background">
        <img :src="course.coverUrl || '/default-cover.jpg'" alt="course cover" />
        <div class="hero-overlay"></div>
        <div class="hero-particles">
          <div class="particle" v-for="i in 20" :key="i" :style="getParticleStyle(i)"></div>
        </div>
      </div>
      <div class="hero-content">
        <div class="course-info">
          <div class="course-badges">
            <div class="badge-container">
              <el-tag class="level-badge enhanced-badge" :type="getLevelType(course.level)">
                <el-icon><TrendCharts /></el-icon>
                {{ getLevelName(course.level) }}
              </el-tag>
              <el-tag v-if="course.isFree" class="free-badge enhanced-badge" type="success">
                <el-icon><Present /></el-icon>
                免费课程
              </el-tag>
              <div class="trending-badge" v-if="course.trending">
                <el-icon><TrendCharts /></el-icon>
                <span>热门</span>
              </div>
            </div>
          </div>
          <h1 class="course-title">
            <span class="title-text">{{ course.title }}</span>
            <div class="title-decoration"></div>
          </h1>
          <p class="course-subtitle">{{ course.subtitle }}</p>
          <div class="course-stats">
            <div class="stats-grid">
              <div class="stat-item enhanced-stat">
                <div class="stat-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ formatNumber(course.enrollmentCount || 0) }}</div>
                  <div class="stat-label">认证学员</div>
                </div>
              </div>
              <div class="stat-item enhanced-stat">
                <div class="stat-icon">
                  <el-icon><VideoPlay /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ getTotalVideos() }}</div>
                  <div class="stat-label">视频</div>
                </div>
              </div>
              <div class="stat-item enhanced-stat">
                <div class="stat-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ sections.length }}</div>
                  <div class="stat-label">章节</div>
                </div>
              </div>
              <div class="stat-item enhanced-stat" v-if="course.rating">
                <div class="stat-icon">
                  <el-icon><Star /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ course.rating || 4.8 }}</div>
                  <div class="stat-label">评分</div>
                </div>
              </div>
            </div>
          </div>
          <div class="course-actions">
            <div class="actions-container">
              <el-button
                v-if="!course.enrolled"
                class="primary-action enhanced-action"
                size="large"
                @click="handleEnroll"
                :loading="enrolling"
              >
                <el-icon><School /></el-icon>
                <span v-if="!enrolling">获取认证</span>
                <span v-else>正在注册...</span>
                <div class="action-shine"></div>
              </el-button>
              <el-button
                v-else
                class="success-action enhanced-action"
                size="large"
                @click="startLearning"
              >
                <el-icon><VideoPlay /></el-icon>
开始专业学习
                <div class="action-shine"></div>
              </el-button>

              <!-- 选课状态指示器 -->
              <div v-if="course.enrolled" class="enrollment-status">
                <el-tag class="status-tag enrolled-status" type="success">
                  <el-icon><Check /></el-icon>
                  已认证 - 可学习所有专业内容
                </el-tag>
              </div>
              <div v-else class="enrollment-status">
                <el-tag class="status-tag not-enrolled-status" type="warning">
                  <el-icon><Warning /></el-icon>
                  未认证 - 仅可预览部分内容
                </el-tag>
                <div class="enrollment-hint">
                  <p class="hint-text">
                    <el-icon><Present /></el-icon>
                    免费获取专业认证，解锁全部企业级内容！
                  </p>
                </div>
              </div>

              <div class="secondary-actions">
                <el-button class="action-btn" @click="addToWishlist">
                  <el-icon><Star /></el-icon>
                  收藏
                </el-button>
                <el-button class="action-btn" @click="shareAction">
                  <el-icon><Share /></el-icon>
                  分享
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <div class="content-container">
        <div class="content-grid">
          <!-- Course Description -->
          <div class="description-section">
            <div class="modern-card enhanced-card">
              <div class="card-header">
                <h2 class="section-title">
                  <el-icon><Document /></el-icon>
专业课程介绍
                </h2>
              </div>
              <div class="card-content">
                <div class="description-content">
                  <p class="description-text">{{ course.description }}</p>

                  <div class="course-highlights" v-if="course.highlights">
                    <h4 class="highlights-title">专业优势</h4>
                    <div class="highlights-grid">
                      <div class="highlight-item" v-for="(highlight, index) in getHighlights()" :key="index">
                        <el-icon class="highlight-icon"><Check /></el-icon>
                        <span>{{ highlight }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="instructor-info" v-if="course.instructor">
                    <h4 class="instructor-title">行业专家</h4>
                    <div class="instructor-card">
                      <div class="instructor-avatar">
                        <img :src="course.instructor.avatar || '/default-avatar.png'" :alt="course.instructor.name" />
                      </div>
                      <div class="instructor-details">
                        <h5 class="instructor-name">{{ course.instructor.name || '认证专家' }}</h5>
                        <p class="instructor-bio">{{ course.instructor.bio || '行业领先专家，专注企业级技能认证培训' }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Course Outline -->
          <div class="outline-section">
            <div class="modern-card sticky-card">
              <div class="card-header">
                <h2 class="section-title">
                  <el-icon><Menu /></el-icon>
专业大纲
                </h2>
                <div class="course-progress" v-if="course.enrolled">
                  <span class="progress-text">认证进度 0/{{ getTotalVideos() }}</span>
                  <el-progress :percentage="0" :show-text="false" :stroke-width="4" />
                </div>
              </div>
              <div class="card-content">
                <div class="course-outline">
                  <div
                    v-for="section in sections"
                    :key="section.id"
                    class="section-item"
                  >
                    <div class="section-header enhanced-section-header" @click="toggleSection(section.id)">
                      <div class="section-info">
                        <div class="section-expand-icon" :class="{ 'expanded': isExpanded(section.id) }">
                          <el-icon><ArrowRight /></el-icon>
                        </div>
                        <div class="section-details">
                          <span class="section-title">{{ section.title }}</span>
                          <div class="section-meta">
                            <span class="video-count">
                              <el-icon><VideoPlay /></el-icon>
                              {{ section.videos?.length || 0 }} 个视频
                            </span>
                            <span v-if="getTotalDuration(section)" class="section-duration">
                              <el-icon><Clock /></el-icon>
                              {{ getTotalDuration(section) }}
                            </span>
                          </div>
                        </div>
                      </div>
                      <div class="section-status">
                        <el-tag v-if="section.videos?.length > 0" size="small" class="section-tag">
                          有内容
                        </el-tag>
                        <el-tag v-else size="small" type="info" class="section-tag">
                          暂无视频
                        </el-tag>
                      </div>
                    </div>
                    <div v-if="isExpanded(section.id)" class="video-list enhanced-video-list">
                      <div
                        v-if="!section.videos || section.videos.length === 0"
                        class="empty-videos"
                      >
                        <el-icon class="empty-icon"><VideoCamera /></el-icon>
                        <span class="empty-text">该章节暂无视频内容</span>
                      </div>
                      <div
                        v-for="video in section.videos"
                        :key="video.id"
                        class="video-item enhanced-video-item"
                        :class="{
                          'video-locked': !video.isFreePreview && !course.enrolled,
                          'video-free': video.isFreePreview,
                          'video-enrolled': course.enrolled
                        }"
                        @click="playVideo(video)"
                      >
                        <div class="video-icon gradient-icon">
                          <el-icon v-if="video.isFreePreview || course.enrolled"><VideoPlay /></el-icon>
                          <el-icon v-else class="lock-icon"><Lock /></el-icon>
                        </div>
                        <div class="video-info">
                          <span class="video-title">{{ video.title }}</span>
                          <div class="video-meta">
                            <span class="video-duration">
                              <el-icon><Clock /></el-icon>
                              {{ formatDuration(video.durationSeconds) }}
                            </span>
                            <span v-if="video.sizeBytes" class="video-size">
                              <el-icon><Document /></el-icon>
                              {{ formatFileSize(video.sizeBytes) }}
                            </span>
                          </div>
                        </div>
                        <div class="video-badges">
                          <el-tag v-if="video.isFreePreview" size="small" class="preview-tag enhanced-preview">
                            <el-icon><View /></el-icon>
                            免费试看
                          </el-tag>
                          <el-tag v-else-if="course.enrolled" size="small" class="enrolled-tag">
                            <el-icon><Check /></el-icon>
                            已选课
                          </el-tag>
                          <el-tooltip
                            v-else
                            content="选课后可观看此视频"
                            placement="top"
                          >
                            <div class="lock-badge">
                              <el-icon class="lock-icon"><Lock /></el-icon>
                              <span class="lock-text">需选课</span>
                            </div>
                          </el-tooltip>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getCourse, getCourseOutline, enrollCourse } from '../api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  ArrowRight,
  VideoPlay,
  User,
  Document,
  Star,
  TrendCharts,
  Present,
  Share,
  Menu,
  Clock,
  VideoCamera,
  View,
  Lock,
  Check,
  School,
  Warning
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const course = ref({})
const sections = ref([])
const activeNames = ref([])
const enrolling = ref(false)
const expandedSections = ref(new Set())

onMounted(async () => {
  await loadCourse()
  await loadCourseOutline()
})

const loadCourse = async () => {
  try {
    const res = await getCourse(route.params.id)
    course.value = res.data
  } catch (error) {
    ElMessage.error('加载课程失败')
  }
}

const loadCourseOutline = async () => {
  try {
    const res = await getCourseOutline(route.params.id)
    sections.value = res.data
    activeNames.value = sections.value.map(s => s.id)

    // 默认展开有视频的章节
    sections.value.forEach(section => {
      if (section.videos && section.videos.length > 0) {
        expandedSections.value.add(section.id)
      }
    })
  } catch (error) {
    ElMessage.error('加载课程目录失败')
  }
}

const handleEnroll = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }

  enrolling.value = true
  try {
    await enrollCourse(route.params.id)
    course.value.enrolled = true
    ElMessage.success('选课成功！现在可以观看所有视频了')
    // 重新加载课程信息以确保状态同步
    await loadCourse()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '选课失败，请稍后重试')
  } finally {
    enrolling.value = false
  }
}

const startLearning = () => {
  // 找到第一个可用的视频（优先免费预览，其次已选课的视频）
  for (const section of sections.value) {
    if (section.videos && section.videos.length > 0) {
      for (const video of section.videos) {
        if (video.isFreePreview || course.value.enrolled) {
          router.push(`/video/${video.id}`)
          return
        }
      }
    }
  }

  // 如果没有找到可播放视频，提示用户
  if (!course.value.enrolled) {
    ElMessage.warning('请先选课以开始学习')
  } else {
    ElMessage.warning('该课程暂无可播放的视频内容')
  }
}

const playVideo = (video) => {
  // 如果是免费预览视频，直接播放
  if (video.isFreePreview) {
    router.push(`/video/${video.id}`)
    return
  }

  // 检查是否已选课
  if (!course.value.enrolled) {
    // 显示选课提示对话框
    ElMessageBox.confirm(
      '此视频需要选课后才能观看，是否立即选课？',
      '需要选课',
      {
        confirmButtonText: '立即选课',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'course-enroll-dialog'
      }
    ).then(() => {
      handleEnroll()
    }).catch(() => {
      // 用户取消选课
    })
    return
  }

  // 已选课，直接播放
  router.push(`/video/${video.id}`)
}

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const formatNumber = (num) => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const formatFileSize = (bytes) => {
  if (!bytes) return ''
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getParticleStyle = (index) => {
  const size = Math.random() * 4 + 2
  const x = Math.random() * 100
  const y = Math.random() * 100
  const delay = Math.random() * 3

  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${x}%`,
    top: `${y}%`,
    animationDelay: `${delay}s`
  }
}

const getHighlights = () => {
  return [
    '行业认证标准，权威专业认可',
    '企业级项目实战，真实案例教学',
    '系统化培训体系，循序渐进学习',
    '专家团队指导，一对一答疑',
    '终身技术支持，持续能力提升',
    '权威证书认证，职业发展助力'
  ]
}

const addToWishlist = () => {
  ElMessage.success('已添加到收藏夹')
}

const shareAction = () => {
  if (navigator.share) {
    navigator.share({
      title: course.value.title,
      text: course.value.subtitle,
      url: window.location.href
    })
  } else {
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪切板')
  }
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

const getTotalVideos = () => {
  return sections.value.reduce((total, section) => {
    return total + (section.videos?.length || 0)
  }, 0)
}

const getTotalDuration = (section) => {
  if (!section.videos || section.videos.length === 0) return ''
  const total = section.videos.reduce((sum, video) => sum + (video.durationSeconds || 0), 0)
  return formatDuration(total)
}

const toggleSection = (sectionId) => {
  if (expandedSections.value.has(sectionId)) {
    expandedSections.value.delete(sectionId)
  } else {
    expandedSections.value.add(sectionId)
  }
}

const isExpanded = (sectionId) => {
  return expandedSections.value.has(sectionId)
}

</script>

<style scoped>
/* Reset & Base */
.course-detail-container {
  min-height: 100vh;
  background: #f8fafc;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* Modern Header */
.modern-header {
  background: white;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.back-button {
  background: #f8fafc !important;
  border: 1px solid #e2e8f0 !important;
  color: #64748b !important;
  width: 44px !important;
  height: 44px !important;
  transition: all 0.3s !important;
}

.back-button:hover {
  background: #667eea !important;
  border-color: #667eea !important;
  color: white !important;
  transform: translateY(-1px);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #6b7280;
}

.breadcrumb span {
  cursor: pointer;
  transition: color 0.3s;
}

.breadcrumb span:not(.current):hover {
  color: #667eea;
}

.breadcrumb .current {
  color: #667eea;
  font-weight: 600;
}

/* Hero Section - 简化版本 */
.hero-section {
  position: relative;
  height: 300px;
  overflow: hidden;
  background: linear-gradient(135deg, #0056d3 0%, #004bb5 100%);
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.hero-background img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 86, 211, 0.1);
}

.hero-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: float-particle 6s infinite ease-in-out;
}

@keyframes float-particle {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-20px) scale(1.2);
    opacity: 0.6;
  }
}

.hero-content {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  align-items: center;
}

.course-info {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 2rem;
  color: white;
}

.course-badges {
  margin-bottom: 1.5rem;
}

.badge-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
}

.enhanced-badge {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(15px) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: white !important;
  font-weight: 600 !important;
  padding: 0.6rem 1.2rem !important;
  border-radius: 25px !important;
  display: flex !important;
  align-items: center !important;
  gap: 0.5rem !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1) !important;
}

.enhanced-badge:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
  background: rgba(255, 255, 255, 0.25) !important;
}

.trending-badge {
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 25px;
  font-size: 0.875rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
  animation: pulse-glow 2s infinite;
}

@keyframes pulse-glow {
  0%, 100% {
    box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
  }
  50% {
    box-shadow: 0 4px 25px rgba(255, 107, 107, 0.5);
  }
}

.course-title {
  margin-bottom: 1rem;
  position: relative;
}

.title-text {
  font-size: 2.25rem;
  font-weight: 700;
  line-height: 1.2;
  color: white;
  display: block;
  margin-bottom: 1rem;
}

.title-decoration {
  width: 60px;
  height: 4px;
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  border-radius: 2px;
  margin-top: 0.5rem;
  animation: expand-decoration 1s ease-out;
}

@keyframes title-shimmer {
  0%, 100% {
    background-position: -200% center;
  }
  50% {
    background-position: 200% center;
  }
}

@keyframes expand-decoration {
  from {
    width: 0;
  }
  to {
    width: 60px;
  }
}

.course-subtitle {
  font-size: 1.125rem;
  font-weight: 400;
  margin-bottom: 2rem;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.5;
}

.course-stats {
  margin-bottom: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.enhanced-stat {
  background: rgba(255, 255, 255, 0.1) !important;
  padding: 1rem !important;
  border-radius: 16px !important;
  backdrop-filter: blur(15px) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  gap: 0.75rem !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  cursor: pointer !important;
}

.enhanced-stat:hover {
  transform: translateY(-4px) !important;
  background: rgba(255, 255, 255, 0.18) !important;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15) !important;
}

.stat-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.25rem;
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.course-actions {
  margin-top: 1rem;
}

.actions-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.enhanced-action {
  padding: 1.25rem 2.5rem !important;
  font-size: 1.125rem !important;
  font-weight: 600 !important;
  border-radius: 20px !important;
  border: none !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 0.75rem !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  backdrop-filter: blur(15px) !important;
  position: relative !important;
  overflow: hidden !important;
  text-transform: uppercase !important;
  letter-spacing: 1px !important;
}

.enhanced-action.primary-action {
  background: #0056d3 !important;
  color: white !important;
  box-shadow: 0 4px 12px rgba(0, 86, 211, 0.3) !important;
}

.enhanced-action.success-action {
  background: #00a64f !important;
  color: white !important;
  box-shadow: 0 4px 12px rgba(0, 166, 79, 0.3) !important;
}

.enhanced-action:hover {
  transform: translateY(-4px) !important;
  box-shadow: 0 15px 45px rgba(0, 0, 0, 0.25) !important;
}

.action-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.enhanced-action:hover .action-shine {
  left: 100%;
}

.secondary-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
}

/* 选课状态样式 */
.enrollment-status {
  margin-top: 1rem;
  text-align: center;
}

.status-tag {
  padding: 0.75rem 1.5rem !important;
  font-size: 0.875rem !important;
  font-weight: 600 !important;
  border-radius: 25px !important;
  display: inline-flex !important;
  align-items: center !important;
  gap: 0.5rem !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
}

.enrolled-status {
  background: rgba(16, 185, 129, 0.1) !important;
  color: #059669 !important;
  border: 1px solid rgba(16, 185, 129, 0.3) !important;
}

.not-enrolled-status {
  background: rgba(251, 191, 36, 0.1) !important;
  color: #d97706 !important;
  border: 1px solid rgba(251, 191, 36, 0.3) !important;
}

.enrollment-hint {
  margin-top: 0.75rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.hint-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.875rem;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  justify-content: center;
  font-weight: 500;
}

.action-btn {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: white !important;
  padding: 0.75rem 1.5rem !important;
  border-radius: 15px !important;
  font-weight: 500 !important;
  transition: all 0.3s !important;
  backdrop-filter: blur(10px) !important;
  display: flex !important;
  align-items: center !important;
  gap: 0.5rem !important;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15) !important;
}

/* Main Content */
.main-content {
  padding: 2rem 0;
  background: #f8fafc;
}

.content-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 2rem;
}

.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
}

/* Modern Cards */
.modern-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.sticky-card {
  position: sticky;
  top: 120px;
  max-height: calc(100vh - 140px);
  overflow-y: auto;
}

.card-header {
  padding: 1.5rem 2rem;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
}

.course-progress {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 120px;
}

.progress-text {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
}

.card-content {
  padding: 2rem;
}

.description-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.description-text {
  line-height: 1.8;
  color: #374151;
  font-size: 1rem;
  margin: 0;
}

.course-highlights {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 1.5rem;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.highlights-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.highlights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 0.75rem;
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.highlight-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.highlight-icon {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.instructor-info {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 1.5rem;
  border-radius: 16px;
  color: white;
}

.instructor-title {
  font-size: 1.125rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: white;
}

.instructor-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 1rem;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.instructor-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.instructor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.instructor-details {
  flex: 1;
}

.instructor-name {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: white;
}

.instructor-bio {
  font-size: 0.875rem;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.enhanced-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.enhanced-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

/* Course Outline */
.course-outline {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* Enhanced Video List Styles */
.enhanced-video-list {
  background: #f8fafc;
}

.empty-videos {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  color: #6b7280;
  background: white;
}

.empty-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: #d1d5db;
}

.empty-text {
  font-size: 0.875rem;
  font-weight: 500;
}

.enhanced-video-item {
  background: white;
  border-radius: 12px;
  margin: 0.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
}

.enhanced-video-item:hover {
  background: #f8fafc;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.enhanced-video-item.video-free {
  border-color: #10b981;
  background: rgba(16, 185, 129, 0.02);
}

.enhanced-video-item.video-enrolled {
  border-color: #0056d3;
  background: rgba(0, 86, 211, 0.02);
}

.enhanced-video-item.video-locked {
  background: #f9fafb;
  opacity: 0.7;
  cursor: pointer;
}

.enhanced-video-item.video-locked:hover {
  background: #f3f4f6;
  border-color: #fbbf24;
  opacity: 1;
}

.gradient-icon {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
  border-radius: 12px !important;
  width: 40px !important;
  height: 40px !important;
}

.video-meta {
  display: flex;
  gap: 1rem;
  margin-top: 0.25rem;
}

.video-duration,
.video-size {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: #6b7280;
}

.enhanced-preview {
  background: rgba(16, 185, 129, 0.1) !important;
  color: #059669 !important;
  border: 1px solid rgba(16, 185, 129, 0.2) !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
  display: flex !important;
  align-items: center !important;
  gap: 0.25rem !important;
}

.lock-badge {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.4rem 0.8rem;
  background: rgba(251, 191, 36, 0.1);
  border: 1px solid rgba(251, 191, 36, 0.3);
  border-radius: 8px;
  transition: all 0.3s;
}

.lock-badge:hover {
  background: rgba(251, 191, 36, 0.2);
  transform: scale(1.05);
}

.lock-icon {
  color: #f59e0b;
  font-size: 0.875rem;
}

.lock-text {
  font-size: 0.75rem;
  font-weight: 500;
  color: #f59e0b;
}

.enrolled-tag {
  background: rgba(0, 86, 211, 0.1) !important;
  color: #0056d3 !important;
  border: 1px solid rgba(0, 86, 211, 0.2) !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
  display: flex !important;
  align-items: center !important;
  gap: 0.25rem !important;
}

.section-item {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.section-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

/* Enhanced Section Header */
.enhanced-section-header {
  padding: 1.25rem 1.5rem;
  background: #f8fafc;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: 1px solid #e2e8f0;
}

.enhanced-section-header:hover {
  background: #e2e8f0;
  transform: translateY(-1px);
}

.section-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
}

.section-expand-icon {
  width: 24px;
  height: 24px;
  background: transparent;
  color: #0056d3;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.section-expand-icon.expanded {
  transform: rotate(90deg);
  color: #004bb5;
}

.section-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.section-meta {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.video-count,
.section-duration {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
}

.section-status {
  display: flex;
  align-items: center;
}

.section-tag {
  border-radius: 12px !important;
  font-weight: 500 !important;
}

.video-list {
  background: white;
}

.video-item {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  cursor: pointer;
  transition: all 0.3s;
  border-top: 1px solid #f1f5f9;
}

.video-item:hover {
  background: #f8fafc;
  transform: translateX(4px);
}

.video-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.875rem;
}

.video-info {
  flex: 1;
  margin-left: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.video-title {
  font-size: 0.875rem;
  font-weight: 500;
  color: #1f2937;
}

.video-duration {
  font-size: 0.75rem;
  color: #6b7280;
}

.video-badges {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.preview-tag {
  background: rgba(16, 185, 129, 0.1) !important;
  color: #059669 !important;
  border: 1px solid rgba(16, 185, 129, 0.2) !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
}

.lock-icon {
  color: #9ca3af;
  font-size: 0.875rem;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .sticky-card {
    position: static;
    max-height: none;
  }

  .course-title {
    font-size: 2.5rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 1rem;
  }

  .breadcrumb {
    display: none;
  }

  .hero-section {
    height: 400px;
  }

  .course-title {
    font-size: 2rem;
  }

  .course-subtitle {
    font-size: 1rem;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .highlights-grid {
    grid-template-columns: 1fr;
  }

  .instructor-card {
    flex-direction: column;
    text-align: center;
  }

  .course-actions {
    flex-direction: column;
  }

  .content-container {
    padding: 0 1rem;
  }

  .card-header {
    padding: 1rem;
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .card-content {
    padding: 1rem;
  }

  .video-item {
    padding: 0.75rem 1rem;
  }
}

/* Element Plus Overrides */
:deep(.el-progress-bar__outer) {
  background: rgba(255, 255, 255, 0.2) !important;
}

:deep(.el-progress-bar__inner) {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
}

/* 选课对话框样式 */
:deep(.course-enroll-dialog) {
  border-radius: 16px !important;
}

:deep(.course-enroll-dialog .el-message-box__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  border-radius: 16px 16px 0 0 !important;
}

:deep(.course-enroll-dialog .el-message-box__title) {
  color: white !important;
}

:deep(.course-enroll-dialog .el-message-box__content) {
  padding: 2rem !important;
}

:deep(.course-enroll-dialog .el-button--primary) {
  background: #0056d3 !important;
  border-color: #0056d3 !important;
}
</style>