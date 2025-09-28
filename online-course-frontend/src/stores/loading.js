import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useLoadingStore = defineStore('loading', () => {
  // 状态
  const isLoading = ref(false)
  const loadingTitle = ref('加载中...')
  const loadingMessage = ref('')
  const showProgress = ref(false)
  const progress = ref(0)
  const allowClose = ref(false)

  // 加载任务队列
  const loadingTasks = ref(new Set())

  // 显示加载
  const showLoading = (options = {}) => {
    const {
      title = '加载中...',
      message = '',
      showProgress: showProg = false,
      allowClose: allowCloseOpt = false,
      taskId = null
    } = options

    if (taskId) {
      loadingTasks.value.add(taskId)
    }

    loadingTitle.value = title
    loadingMessage.value = message
    showProgress.value = showProg
    allowClose.value = allowCloseOpt
    progress.value = 0
    isLoading.value = true
  }

  // 隐藏加载
  const hideLoading = (taskId = null) => {
    if (taskId) {
      loadingTasks.value.delete(taskId)
      // 如果还有其他任务在加载，不隐藏
      if (loadingTasks.value.size > 0) {
        return
      }
    }

    isLoading.value = false
    loadingTitle.value = '加载中...'
    loadingMessage.value = ''
    showProgress.value = false
    progress.value = 0
    allowClose.value = false
  }

  // 更新进度
  const updateProgress = (value, message = '') => {
    progress.value = Math.max(0, Math.min(100, value))
    if (message) {
      loadingMessage.value = message
    }
  }

  // 显示文件上传加载
  const showUploadLoading = (filename = '') => {
    showLoading({
      title: '上传文件',
      message: filename ? `正在上传: ${filename}` : '正在上传文件...',
      showProgress: true,
      allowClose: false,
      taskId: 'upload'
    })
  }

  // 显示数据加载
  const showDataLoading = (message = '正在获取数据...') => {
    showLoading({
      title: '数据加载',
      message,
      showProgress: false,
      allowClose: false,
      taskId: 'data'
    })
  }

  // 显示页面加载
  const showPageLoading = (pageName = '') => {
    showLoading({
      title: '页面加载',
      message: pageName ? `正在加载 ${pageName}...` : '正在加载页面...',
      showProgress: false,
      allowClose: false,
      taskId: 'page'
    })
  }

  // 显示操作加载
  const showActionLoading = (action = '处理中') => {
    showLoading({
      title: action,
      message: '请稍候...',
      showProgress: false,
      allowClose: false,
      taskId: 'action'
    })
  }

  // 强制清除所有加载
  const clearAllLoading = () => {
    loadingTasks.value.clear()
    isLoading.value = false
    loadingTitle.value = '加载中...'
    loadingMessage.value = ''
    showProgress.value = false
    progress.value = 0
    allowClose.value = false
  }

  // 加载状态计算属性
  const hasMultipleTasks = computed(() => loadingTasks.value.size > 1)
  const currentTaskCount = computed(() => loadingTasks.value.size)

  return {
    // 状态
    isLoading,
    loadingTitle,
    loadingMessage,
    showProgress,
    progress,
    allowClose,
    hasMultipleTasks,
    currentTaskCount,

    // 方法
    showLoading,
    hideLoading,
    updateProgress,
    showUploadLoading,
    showDataLoading,
    showPageLoading,
    showActionLoading,
    clearAllLoading
  }
})

// 自动加载装饰器
export const withLoading = (fn, options = {}) => {
  return async (...args) => {
    const loadingStore = useLoadingStore()
    const taskId = options.taskId || `task_${Date.now()}`

    try {
      loadingStore.showLoading({ ...options, taskId })
      const result = await fn(...args)
      return result
    } finally {
      loadingStore.hideLoading(taskId)
    }
  }
}

// 文件上传进度装饰器
export const withUploadProgress = (uploadFn) => {
  return async (file, onProgress = null) => {
    const loadingStore = useLoadingStore()

    loadingStore.showUploadLoading(file.name)

    try {
      const result = await uploadFn(file, (progress) => {
        loadingStore.updateProgress(progress, `上传进度: ${progress}%`)
        if (onProgress) onProgress(progress)
      })
      return result
    } finally {
      loadingStore.hideLoading('upload')
    }
  }
}