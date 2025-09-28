<template>
  <div class="video-player-container">
    <!-- Modern Header -->
    <div class="modern-header">
      <div class="header-backdrop"></div>
      <div class="header-content">
        <button class="back-button" @click="router.back()">
          <el-icon><ArrowLeft /></el-icon>
          <span class="back-text">返回</span>
        </button>

        <div class="header-info">
          <div class="breadcrumb">
            <span @click="router.push('/')" class="breadcrumb-item">
              <el-icon><HomeFilled /></el-icon>
              首页
            </span>
            <el-icon class="breadcrumb-separator"><ArrowRight /></el-icon>
            <span @click="router.push('/courses')" class="breadcrumb-item">
              <el-icon><Collection /></el-icon>
              课程
            </span>
            <el-icon class="breadcrumb-separator"><ArrowRight /></el-icon>
            <span @click="router.push(`/course/${videoInfo.courseId}`)" class="breadcrumb-item">{{ videoInfo.courseTitle }}</span>
            <el-icon class="breadcrumb-separator"><ArrowRight /></el-icon>
            <span class="current">{{ videoInfo.title }}</span>
          </div>
          <h1 class="video-title">{{ videoInfo.title }}</h1>
        </div>

        <div class="header-actions">
          <div class="video-meta">
            <span class="meta-badge chapter-badge">
              <el-icon><FolderOpened /></el-icon>
              {{ videoInfo.sectionTitle }}
            </span>
            <span class="meta-badge course-badge">
              <el-icon><Reading /></el-icon>
              {{ videoInfo.courseTitle }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <div class="content-container">
        <div class="content-grid">
          <!-- Video Player Section -->
          <div class="player-section">
            <div class="player-wrapper">
              <video
                ref="videoPlayer"
                class="video-player"
                controls
                preload="metadata"
                :poster="videoInfo.posterUrl"
                style="width: 100%; height: auto; min-height: 400px;"
                @loadedmetadata="handleMetadataLoaded"
                @canplay="handleCanPlay"
                @error="handleVideoError"
                @loadstart="handleLoadStart"
                @waiting="handleWaiting"
                @playing="handlePlaying"
              >
                <source v-if="videoInfo.sourceUrl" :src="getVideoUrl(videoInfo.sourceUrl)" type="video/mp4" />
                <!-- Fallback message -->
                <p>您的浏览器不支持视频播放，请升级浏览器或下载视频文件观看。</p>
              </video>
            </div>

            <!-- Video Details -->
            <div class="video-details">
              <div class="details-header">
                <div class="title-section">
                  <h2 class="video-title-main">{{ videoInfo.title }}</h2>
                  <p class="video-subtitle">深入学习，掌握核心技能</p>
                </div>
                <div class="video-badges">
                  <span class="badge badge-chapter">
                    <el-icon><Collection /></el-icon>
                    {{ videoInfo.sectionTitle }}
                  </span>
                  <span v-if="progress.isCompleted" class="badge badge-completed">
                    <el-icon><CircleCheckFilled /></el-icon>
                    已完成
                  </span>
                  <span v-else-if="progress.percentage > 0" class="badge badge-progress">
                    <el-icon><Loading /></el-icon>
                    学习中
                  </span>
                </div>
              </div>

              <!-- Progress Section -->
              <div class="progress-section">
                <div class="progress-header">
                  <div class="progress-title-group">
                    <h3 class="progress-title">
                      <el-icon><DataAnalysis /></el-icon>
                      学习进度
                    </h3>
                    <span class="progress-hint">持续学习，即将完成</span>
                  </div>
                  <div class="progress-percentage-circle">
                    <span class="percentage-value">{{ Math.round(progress.percentage) }}</span>
                    <span class="percentage-symbol">%</span>
                  </div>
                </div>
                <div class="progress-container">
                  <div class="progress-track">
                    <div class="progress-fill" :style="{ width: progress.percentage + '%' }">
                      <span class="progress-glow"></span>
                    </div>
                  </div>
                </div>
                <div class="progress-info">
                  <div class="time-info">
                    <el-icon><Clock /></el-icon>
                    <span>已学习 {{ formatTime(progress.maxPositionSeconds) }} / 总时长 {{ formatTime(videoInfo.durationSeconds) }}</span>
                  </div>
                  <div v-if="progress.isCompleted" class="completion-badge">
                    <el-icon><Trophy /></el-icon>
                    <span>恭喜完成!</span>
                  </div>
                  <div v-else-if="progress.percentage >= 80" class="almost-done">
                    <el-icon><Flag /></el-icon>
                    <span>即将完成</span>
                  </div>
                </div>
              </div>

              <!-- Video Stats -->
              <div class="video-stats">
                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><Clock /></el-icon>
                  </div>
                  <div class="stat-content">
                    <span class="stat-label">课程时长</span>
                    <span class="stat-value">{{ formatTime(videoInfo.durationSeconds) }}</span>
                  </div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="stat-content">
                    <span class="stat-label">授课讲师</span>
                    <span class="stat-value">{{ videoInfo.instructorName || '专业讲师' }}</span>
                  </div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><VideoCamera /></el-icon>
                  </div>
                  <div class="stat-content">
                    <span class="stat-label">视频质量</span>
                    <span class="stat-value">1080P 高清</span>
                  </div>
                </div>
                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><Download /></el-icon>
                  </div>
                  <div class="stat-content">
                    <span class="stat-label">支持功能</span>
                    <span class="stat-value">倍速播放</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Sidebar -->
          <div class="sidebar-section">
            <!-- Course Navigation -->
            <div class="nav-card">
              <div class="card-header">
                <div class="card-header-content">
                  <h3 class="card-title">
                    <el-icon><Grid /></el-icon>
                    <span>课程大纲</span>
                  </h3>
                  <span class="card-badge">{{ videoInfo.sectionTitle }}</span>
                </div>
                <button class="action-button" @click="router.push(`/course/${videoInfo.courseId}`)">
                  <el-icon><ArrowRightBold /></el-icon>
                </button>
              </div>
              <div class="card-content">
                <div class="nav-content">
                  <div class="nav-item active">
                    <div class="nav-item-indicator"></div>
                    <div class="nav-item-content">
                      <span class="nav-item-title">{{ videoInfo.title }}</span>
                      <span class="nav-item-duration">正在播放</span>
                    </div>
                  </div>
                  <div class="nav-hint">
                    <el-icon><InfoFilled /></el-icon>
                    <span>查看完整课程目录，规划学习路径</span>
                  </div>
                  <button class="primary-button" @click="router.push(`/course/${videoInfo.courseId}`)">
                    <el-icon><Notebook /></el-icon>
                    进入课程主页
                  </button>
                </div>
              </div>
            </div>

            <!-- Study Tools -->
            <div class="tools-card">
              <div class="card-header">
                <div class="card-header-content">
                  <h3 class="card-title">
                    <el-icon><EditPen /></el-icon>
                    <span>学习工具</span>
                  </h3>
                </div>
              </div>
              <div class="card-content">
                <div class="tools-grid">
                  <div class="tool-item">
                    <div class="tool-icon">
                      <el-icon><Memo /></el-icon>
                    </div>
                    <span class="tool-name">笔记</span>
                    <span class="tool-status">即将推出</span>
                  </div>
                  <div class="tool-item">
                    <div class="tool-icon">
                      <el-icon><QuestionFilled /></el-icon>
                    </div>
                    <span class="tool-name">提问</span>
                    <span class="tool-status">即将推出</span>
                  </div>
                  <div class="tool-item">
                    <div class="tool-icon">
                      <el-icon><Share /></el-icon>
                    </div>
                    <span class="tool-name">分享</span>
                    <span class="tool-status">即将推出</span>
                  </div>
                  <div class="tool-item">
                    <div class="tool-icon">
                      <el-icon><Download /></el-icon>
                    </div>
                    <span class="tool-name">下载</span>
                    <span class="tool-status">即将推出</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Related Resources -->
            <div class="resources-card">
              <div class="card-header">
                <div class="card-header-content">
                  <h3 class="card-title">
                    <el-icon><Files /></el-icon>
                    <span>相关资源</span>
                  </h3>
                </div>
              </div>
              <div class="card-content">
                <div class="resource-list">
                  <div class="resource-item">
                    <el-icon><Document /></el-icon>
                    <span>课程讲义</span>
                    <el-icon class="resource-arrow"><ArrowRight /></el-icon>
                  </div>
                  <div class="resource-item">
                    <el-icon><Link /></el-icon>
                    <span>参考链接</span>
                    <el-icon class="resource-arrow"><ArrowRight /></el-icon>
                  </div>
                  <div class="resource-item">
                    <el-icon><FolderOpened /></el-icon>
                    <span>练习资料</span>
                    <el-icon class="resource-arrow"><ArrowRight /></el-icon>
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
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getVideo, getVideoProgress, updateVideoProgress } from '../api/video'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

const videoPlayer = ref(null)
const player = ref(null)
const videoInfo = ref({})
const progress = ref({
  lastPositionSeconds: 0,
  maxPositionSeconds: 0,
  percentage: 0,
  isCompleted: false,
  playbackRate: 1
})

let progressTimer = null
let lastUpdateTime = 0
let lastUpdatePosition = 0

// Watch for videoInfo changes to initialize player
watch(() => videoInfo.value.sourceUrl, (newUrl) => {
  if (newUrl && videoPlayer.value && !player.value) {
    nextTick(() => {
      initPlayer()
    })
  }
})

onMounted(async () => {
  await loadVideo()
  await loadProgress()
})

onUnmounted(() => {
  if (player.value) {
    saveProgress('pause')
    // player.value is now a native video element, not Video.js, so no dispose method
    // Just remove event listeners and clean up
    if (typeof player.value.dispose === 'function') {
      player.value.dispose()
    }
  }
  if (progressTimer) {
    clearInterval(progressTimer)
  }
})

const loadVideo = async () => {
  try {
    const res = await getVideo(route.params.id)
    videoInfo.value = res.data

    // Debug video info
    console.log('Video Info loaded:', videoInfo.value)
    console.log('Video Source URL:', videoInfo.value.sourceUrl)
    console.log('Full Video URL:', getVideoUrl(videoInfo.value.sourceUrl))

    // Try to load video manually if needed
    if (videoInfo.value.sourceUrl) {
      await nextTick()
      if (videoPlayer.value) {
        videoPlayer.value.load()
      }
    }
  } catch (error) {
    console.error('Error loading video:', error)
    ElMessage.error('加载视频失败')
    router.back()
  }
}

const loadProgress = async () => {
  try {
    const res = await getVideoProgress(route.params.id)
    progress.value = res.data

    if (progress.value.lastPositionSeconds > 0 && !progress.value.isCompleted) {
      const resumeTime = progress.value.lastPositionSeconds
      const minutes = Math.floor(resumeTime / 60)
      const seconds = resumeTime % 60
      const timeStr = `${minutes}:${seconds.toString().padStart(2, '0')}`

      ElMessageBox.confirm(
        `您上次观看到 ${timeStr}，是否从上次位置继续？`,
        '继续观看',
        {
          confirmButtonText: '继续',
          cancelButtonText: '从头开始',
          type: 'info'
        }
      ).then(() => {
        if (player.value) {
          player.value.currentTime = resumeTime
        }
      }).catch(() => {
        // User chose to start from beginning
      })
    }
  } catch (error) {
    console.error('Failed to load progress:', error)
  }
}

const initPlayer = () => {
  if (!videoPlayer.value) {
    console.error('Video element not found')
    return
  }

  try {
    // Use native HTML5 video element
    const video = videoPlayer.value
    player.value = video

    console.log('Using native HTML5 video player')
    console.log('Video source:', videoInfo.value.sourceUrl)
    console.log('Video element:', video)
    console.log('Browser supports MP4:', video.canPlayType('video/mp4'))
    console.log('Browser supports WebM:', video.canPlayType('video/webm'))
    console.log('Browser supports OGG:', video.canPlayType('video/ogg'))

    // Set initial playback rate
    if (progress.value.playbackRate) {
      video.playbackRate = progress.value.playbackRate
    }

    // Add native event listeners
    video.addEventListener('play', () => {
      console.log('Video started playing')
      startProgressTracking()
    })

    video.addEventListener('pause', () => {
      console.log('Video paused')
      saveProgress('pause')
    })

    video.addEventListener('ended', () => {
      console.log('Video ended')
      saveProgress('end')
    })

    video.addEventListener('error', (error) => {
      console.error('Video error:', error)
      const videoError = video.error
      if (videoError) {
        console.error('Video error code:', videoError.code)
        console.error('Video error message:', videoError.message)

        // Try to reload the video with different approach
        setTimeout(() => {
          console.log('Attempting video reload...')
          video.load()
        }, 2000)

        // Show more specific error messages
        let errorMessage = '视频播放错误'
        switch (videoError.code) {
          case 1:
            errorMessage = '视频播放被中断'
            break
          case 2:
            errorMessage = '网络错误，无法加载视频'
            break
          case 3:
            errorMessage = '视频解码错误'
            break
          case 4:
            errorMessage = '视频格式不支持'
            break
        }
        ElMessage.error(errorMessage)
      } else {
        ElMessage.error('视频播放错误，请检查网络连接')
      }
    })

    video.addEventListener('loadstart', () => {
      console.log('Video loading started')
    })

    video.addEventListener('canplay', () => {
      console.log('Video can start playing')
    })

    video.addEventListener('loadeddata', () => {
      console.log('Video data loaded')
    })

    video.addEventListener('loadedmetadata', () => {
      console.log('Video metadata loaded, duration:', video.duration)
      if (video.duration > 0) {
        ElMessage.success('视频加载成功，时长: ' + formatTime(video.duration))
      }
    })

    video.addEventListener('canplaythrough', () => {
      console.log('Video can play through without buffering')
    })

    // Add test button for debugging
    window.testVideo = () => {
      console.log('Testing video playback...')
      if (video.paused) {
        video.play().then(() => {
          console.log('Video started playing successfully')
        }).catch(err => {
          console.error('Video play failed:', err)
        })
      } else {
        video.pause()
        console.log('Video paused')
      }
    }

    console.log('Native video player initialized successfully')
    console.log('Call window.testVideo() in console to test video playback')

  } catch (error) {
    console.error('Player initialization failed:', error)
    ElMessage.error('视频播放器初始化失败')
    return
  }

  // Save progress before page unload
  window.addEventListener('beforeunload', () => {
    saveProgress('beforeunload')
  })
}

const startProgressTracking = () => {
  if (progressTimer) {
    clearInterval(progressTimer)
  }

  progressTimer = setInterval(() => {
    if (!player.value) return

    const currentTime = player.value.currentTime
    const duration = player.value.duration

    if (isNaN(currentTime) || isNaN(duration)) return

    // Save progress every 5 seconds or if progress increased by 3%
    const timeSinceLastUpdate = Date.now() - lastUpdateTime
    const progressIncrease = Math.abs(currentTime - lastUpdatePosition) / duration * 100

    if (timeSinceLastUpdate >= 5000 || progressIncrease >= 3) {
      saveProgress('heartbeat')
    }
  }, 1000)
}

const saveProgress = async (event) => {
  if (!player.value) return

  const currentTime = Math.floor(player.value.currentTime)
  const duration = Math.floor(player.value.duration)

  if (isNaN(currentTime) || isNaN(duration)) return

  const playbackRate = player.value.playbackRate || 1

  const data = {
    positionSeconds: currentTime,
    durationSeconds: duration,
    event: event,
    maxPositionSeconds: Math.max(currentTime, progress.value.maxPositionSeconds),
    playbackRate: playbackRate,
    device: 'web',
    clientTime: new Date().toISOString()
  }

  try {
    const res = await updateVideoProgress(route.params.id, data)
    progress.value.percentage = res.data.percentage
    progress.value.isCompleted = res.data.isCompleted
    lastUpdateTime = Date.now()
    lastUpdatePosition = currentTime
  } catch (error) {
    console.error('Failed to save progress:', error)
  }
}

const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60

  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// Get full video URL with proper base URL
const getVideoUrl = (url) => {
  if (!url) return ''

  // If URL is already absolute, return as is
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  // Get base URL from current location or use API base
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000'

  // Remove leading slash if present
  const cleanUrl = url.startsWith('/') ? url : '/' + url

  return baseUrl + cleanUrl
}

// Video event handlers for better debugging
const handleMetadataLoaded = (event) => {
  console.log('Video metadata loaded:', {
    duration: event.target.duration,
    videoWidth: event.target.videoWidth,
    videoHeight: event.target.videoHeight,
    readyState: event.target.readyState
  })
}

const handleCanPlay = (event) => {
  console.log('Video can play:', {
    currentSrc: event.target.currentSrc,
    readyState: event.target.readyState,
    networkState: event.target.networkState
  })
  ElMessage.success('视频加载成功')
}

const handleVideoError = (event) => {
  const video = event.target
  const error = video.error

  console.error('Video error occurred:', {
    error: error,
    currentSrc: video.currentSrc,
    networkState: video.networkState,
    readyState: video.readyState
  })

  let errorMessage = '视频播放错误'
  if (error) {
    switch (error.code) {
      case 1:
        errorMessage = '视频加载被中止'
        break
      case 2:
        errorMessage = '网络错误，无法加载视频'
        break
      case 3:
        errorMessage = '视频解码失败，格式不支持'
        break
      case 4:
        errorMessage = '视频源不可用或格式不支持'
        // Try alternative loading method
        tryAlternativeLoading()
        break
    }
  }

  ElMessage.error(errorMessage)
}

const handleLoadStart = () => {
  console.log('Video loading started')
  ElMessage.info('正在加载视频...')
}

const handleWaiting = () => {
  console.log('Video buffering...')
}

const handlePlaying = () => {
  console.log('Video playing')
}

// Try alternative video loading methods
const tryAlternativeLoading = async () => {
  if (!videoPlayer.value || !videoInfo.value.sourceUrl) return

  console.log('Trying alternative video loading...')

  // Method 1: Reset source and reload
  const video = videoPlayer.value
  const currentSrc = getVideoUrl(videoInfo.value.sourceUrl)

  // Remove all sources
  while (video.firstChild) {
    video.removeChild(video.firstChild)
  }

  // Set src directly on video element
  video.src = currentSrc

  try {
    await video.load()
    console.log('Alternative loading successful')
  } catch (err) {
    console.error('Alternative loading failed:', err)

    // Method 2: Try with a test video to check if player works
    console.log('Testing with sample video...')
    video.src = 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4'
    video.load()
  }
}
</script>

<style scoped>
/* Reset & Base */
.video-player-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  position: relative;
}

.video-player-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 50%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
              radial-gradient(circle at 80% 80%, rgba(255, 119, 198, 0.2) 0%, transparent 50%),
              radial-gradient(circle at 40% 20%, rgba(102, 126, 234, 0.2) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* Modern Header */
.modern-header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(229, 231, 235, 0.8);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
  overflow: hidden;
}

.header-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0.6;
  z-index: -1;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-6) var(--space-8);
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: var(--space-6);
  align-items: center;
}

.back-button {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border: 2px solid transparent;
  background-clip: padding-box;
  color: var(--primary);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  position: relative;
  overflow: hidden;
}

.back-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: var(--gradient-primary);
  transition: width 0.6s, height 0.6s;
  transform: translate(-50%, -50%);
  z-index: -1;
}

.back-button:hover {
  color: white;
  transform: translateX(-4px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.back-button:hover::before {
  width: 150%;
  height: 150%;
}

.back-text {
  font-size: var(--text-sm);
  display: none;
}

@media (min-width: 640px) {
  .back-text {
    display: inline;
  }
}

.header-info {
  min-width: 0;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--gray-500);
  margin-bottom: var(--space-3);
  flex-wrap: wrap;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.breadcrumb-item:hover {
  color: var(--primary);
  background: rgba(102, 126, 234, 0.08);
  transform: translateY(-1px);
}

.breadcrumb-separator {
  color: var(--gray-300);
  font-size: var(--text-xs);
}

.breadcrumb .current {
  color: var(--primary);
  font-weight: var(--font-semibold);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
}

.video-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
  line-height: var(--leading-tight);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.header-actions {
  text-align: right;
}

.video-meta {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  align-items: flex-end;
}

.video-meta {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  align-items: flex-end;
}

.meta-badge {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  white-space: nowrap;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.chapter-badge {
  color: var(--gray-700);
  background: linear-gradient(135deg, rgba(148, 163, 184, 0.1) 0%, rgba(203, 213, 225, 0.1) 100%);
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.course-badge {
  font-weight: var(--font-semibold);
  color: var(--primary);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.meta-badge:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Main Content */
.main-content {
  padding: var(--space-8) 0;
  position: relative;
  z-index: 1;
}

.content-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-8);
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: var(--space-8);
}

/* Player Section */
.player-section {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-2xl);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1),
              0 0 100px rgba(102, 126, 234, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
  position: relative;
}

.player-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--gradient-primary);
  opacity: 0.8;
}

.player-wrapper {
  position: relative;
  background: linear-gradient(135deg, #1a1a2e 0%, #0f0f1e 100%);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  overflow: hidden;
  box-shadow: inset 0 2px 10px rgba(0, 0, 0, 0.3);
}

.video-player {
  width: 100%;
  height: auto;
  min-height: 400px;
  display: block;
  outline: none;
}

.video-player::-webkit-media-controls-panel {
  background-color: rgba(0, 0, 0, 0.7);
}

.video-player::-webkit-media-controls-play-button,
.video-player::-webkit-media-controls-timeline,
.video-player::-webkit-media-controls-volume-slider {
  filter: invert(1);
}

.video-details {
  padding: var(--space-8);
  background: linear-gradient(180deg, rgba(249, 250, 251, 0) 0%, rgba(249, 250, 251, 0.5) 100%);
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-8);
  gap: var(--space-4);
  padding-bottom: var(--space-6);
  border-bottom: 2px solid var(--gray-100);
}

.title-section {
  flex: 1;
}

.video-subtitle {
  color: var(--gray-500);
  font-size: var(--text-base);
  margin-top: var(--space-2);
}

.video-title-main {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
  line-height: var(--leading-tight);
  flex: 1;
}

.video-badges {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  align-items: flex-end;
}

.badge {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  display: flex;
  align-items: center;
  gap: var(--space-2);
  white-space: nowrap;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.badge::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.badge:hover::before {
  left: 100%;
}

.badge-chapter {
  background: linear-gradient(135deg, #e0e7ff 0%, #cdd5f4 100%);
  color: var(--primary-dark);
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.badge-completed {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #047857;
  border: 1px solid rgba(16, 185, 129, 0.3);
  animation: pulse-success 2s infinite;
}

.badge-progress {
  background: linear-gradient(135deg, #fed7aa 0%, #fbbf24 100%);
  color: #92400e;
  border: 1px solid rgba(251, 191, 36, 0.3);
}

@keyframes pulse-success {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(16, 185, 129, 0);
  }
}

/* Progress Section */
.progress-section {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-6);
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
  overflow: hidden;
}

.progress-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 50%, rgba(102, 126, 234, 0.1) 0%, transparent 70%);
  pointer-events: none;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-5);
}

.progress-title-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.progress-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--gray-900);
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin: 0;
}

.progress-title .el-icon {
  color: var(--primary);
}

.progress-hint {
  font-size: var(--text-sm);
  color: var(--gray-500);
}

.progress-percentage-circle {
  display: flex;
  align-items: baseline;
  gap: 2px;
  padding: var(--space-3) var(--space-4);
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  border-radius: var(--radius-full);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.percentage-value {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: white;
}

.percentage-symbol {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: rgba(255, 255, 255, 0.8);
}

.progress-container {
  margin-bottom: var(--space-4);
}

.progress-track {
  height: 12px;
  background: rgba(229, 231, 235, 0.5);
  border-radius: var(--radius-full);
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--radius-full);
  position: relative;
  transition: width 0.5s ease;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

.progress-glow {
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100px);
  }
  100% {
    transform: translateX(100px);
  }
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--text-sm);
  padding-top: var(--space-3);
}

.time-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--gray-600);
  background: white;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.time-info .el-icon {
  color: var(--primary);
}

.completion-badge, .almost-done {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  font-weight: var(--font-medium);
}

.completion-badge {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  animation: bounce 1s ease infinite;
}

.almost-done {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}

/* Video Stats */
.video-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-4);
  margin-top: var(--space-6);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4);
  background: linear-gradient(135deg, rgba(249, 250, 251, 0.8) 0%, rgba(243, 244, 246, 0.8) 100%);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(229, 231, 235, 0.5);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  border-color: rgba(102, 126, 234, 0.3);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 1;
}

.stat-icon .el-icon {
  color: var(--primary);
  font-size: var(--text-xl);
}

.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  position: relative;
  z-index: 1;
}

.stat-label {
  font-size: var(--text-xs);
  color: var(--gray-500);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-value {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--gray-900);
}

/* Sidebar */
.sidebar-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.nav-card, .tools-card, .resources-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
  transition: all 0.3s ease;
}

.nav-card:hover, .tools-card:hover, .resources-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
}

.card-header {
  padding: var(--space-5) var(--space-6);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-bottom: 1px solid rgba(229, 231, 235, 0.5);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-content {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex: 1;
}

.card-badge {
  padding: var(--space-1) var(--space-3);
  background: white;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  color: var(--primary);
  font-weight: var(--font-medium);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--gray-900);
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin: 0;
}

.card-title .el-icon {
  color: var(--primary);
}

.action-button {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  color: var(--primary);
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-button:hover {
  background: var(--gradient-primary);
  color: white;
  border-color: transparent;
  transform: rotate(90deg);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.card-content {
  padding: var(--space-6);
}

/* Navigation Content */
.nav-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
  position: relative;
  cursor: pointer;
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-left: 3px solid var(--primary);
}

.nav-item-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
  animation: pulse 2s infinite;
}

.nav-item-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-item-title {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--gray-900);
}

.nav-item-duration {
  font-size: var(--text-xs);
  color: var(--primary);
  font-weight: var(--font-medium);
}

.nav-hint {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3);
  background: rgba(59, 130, 246, 0.05);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  color: var(--info);
}

.primary-button {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  background: var(--gradient-primary);
  color: white;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.primary-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* Video.js Overrides */
:deep(.video-js) {
  width: 100%;
  height: 600px;
  font-family: inherit;
}

:deep(.video-js .vjs-big-play-button) {
  background: rgba(102, 126, 234, 0.9);
  border: none;
  border-radius: 50%;
  width: 80px;
  height: 80px;
  font-size: 2rem;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

:deep(.video-js .vjs-big-play-button:hover) {
  background: var(--primary);
  transform: scale(1.1);
}

:deep(.video-js .vjs-control-bar) {
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  height: 60px;
  padding: 0 var(--space-4);
}

:deep(.video-js .vjs-progress-control) {
  height: 6px;
}

:deep(.video-js .vjs-progress-holder) {
  border-radius: 3px;
}

:deep(.video-js .vjs-play-progress) {
  background: var(--primary);
  border-radius: 3px;
}

/* Tools Grid */
.tools-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-4);
}

.tool-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4);
  background: rgba(249, 250, 251, 0.5);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(229, 231, 235, 0.5);
  transition: all 0.3s ease;
  cursor: pointer;
}

.tool-item:hover {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.tool-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: var(--radius-lg);
}

.tool-icon .el-icon {
  font-size: var(--text-xl);
  color: var(--primary);
}

.tool-name {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--gray-900);
}

.tool-status {
  font-size: var(--text-xs);
  color: var(--gray-500);
}

/* Resource List */
.resource-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.resource-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  background: rgba(249, 250, 251, 0.5);
  border-radius: var(--radius-lg);
  border: 1px solid transparent;
  transition: all 0.3s ease;
  cursor: pointer;
}

.resource-item:hover {
  background: white;
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateX(4px);
}

.resource-item .el-icon:first-child {
  color: var(--primary);
}

.resource-item span {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--gray-700);
}

.resource-arrow {
  color: var(--gray-400);
  transition: transform 0.3s ease;
}

.resource-item:hover .resource-arrow {
  transform: translateX(4px);
  color: var(--primary);
}

/* Responsive Design */
@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .sidebar-section {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-4);
  }

  .resources-card {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .sidebar-section {
    grid-template-columns: 1fr;
  }

  .tools-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .tool-item {
    padding: var(--space-3);
  }

  .tool-name {
    display: none;
  }

  .tool-status {
    display: none;
  }

  .header-content {
    grid-template-columns: auto 1fr;
    gap: var(--space-4);
    padding: var(--space-4);
  }

  .header-actions {
    display: none;
  }

  .breadcrumb {
    display: none;
  }

  .video-title {
    font-size: var(--text-xl);
  }

  .content-container {
    padding: 0 var(--space-4);
  }

  .video-details {
    padding: var(--space-6);
  }

  .details-header {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-3);
  }

  .video-badges {
    flex-direction: row;
    align-items: flex-start;
  }

  .progress-section {
    padding: var(--space-4);
  }

  .progress-header {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
  }

  .progress-info {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
  }

  .video-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-3);
  }

  .stat-card {
    padding: var(--space-3);
  }

  .stat-icon {
    width: 40px;
    height: 40px;
  }

  .stat-label {
    display: none;
  }

  :deep(.video-js) {
    height: 250px;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: var(--space-4) 0;
  }

  .video-details {
    padding: var(--space-4);
  }

  .video-title-main {
    font-size: var(--text-lg);
  }
}
</style>