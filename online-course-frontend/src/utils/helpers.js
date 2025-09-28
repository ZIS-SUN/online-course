// 生成唯一ID
export const generateId = (prefix = 'id') => {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
}

// 格式化数字
export const formatNumber = (num) => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// 格式化时间
export const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60

  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 防抖函数
export const debounce = (func, wait, immediate = false) => {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      timeout = null
      if (!immediate) func(...args)
    }
    const callNow = immediate && !timeout
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
    if (callNow) func(...args)
  }
}

// 节流函数
export const throttle = (func, limit) => {
  let inThrottle
  return function(...args) {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

// 深拷贝
export const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (obj instanceof Object) {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

// 下载文件
export const downloadFile = (url, filename) => {
  const link = document.createElement('a')
  link.href = url
  link.download = filename || 'download'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 复制到剪贴板
export const copyToClipboard = async (text) => {
  if (navigator.clipboard && window.isSecureContext) {
    try {
      await navigator.clipboard.writeText(text)
      return true
    } catch (err) {
      console.error('Failed to copy text: ', err)
      return false
    }
  } else {
    // 降级方案
    const textArea = document.createElement('textarea')
    textArea.value = text
    textArea.style.position = 'fixed'
    textArea.style.left = '-999999px'
    textArea.style.top = '-999999px'
    document.body.appendChild(textArea)
    textArea.focus()
    textArea.select()

    try {
      const result = document.execCommand('copy')
      document.body.removeChild(textArea)
      return result
    } catch (err) {
      console.error('Failed to copy text: ', err)
      document.body.removeChild(textArea)
      return false
    }
  }
}

// 检查是否为移动设备
export const isMobile = () => {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

// 获取文件扩展名
export const getFileExtension = (filename) => {
  return filename.slice((filename.lastIndexOf('.') - 1 >>> 0) + 2)
}

// 文件大小格式化
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 颜色工具
export const hexToRgb = (hex) => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result ? {
    r: parseInt(result[1], 16),
    g: parseInt(result[2], 16),
    b: parseInt(result[3], 16)
  } : null
}

export const rgbToHex = (r, g, b) => {
  return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1)
}

// 日期格式化
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

// 相对时间
export const timeAgo = (date) => {
  const now = new Date()
  const diffInSeconds = Math.floor((now - new Date(date)) / 1000)

  if (diffInSeconds < 60) return '刚刚'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}分钟前`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}小时前`
  if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)}天前`
  if (diffInSeconds < 31536000) return `${Math.floor(diffInSeconds / 2592000)}个月前`
  return `${Math.floor(diffInSeconds / 31536000)}年前`
}

// 表单验证规则
export const validationRules = {
  required: (message = '此字段为必填项') => ({
    validator: (value) => value !== null && value !== undefined && value !== '',
    message
  }),

  email: (message = '请输入有效的邮箱地址') => ({
    validator: (value) => {
      if (!value) return true
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(value)
    },
    message
  }),

  phone: (message = '请输入有效的手机号码') => ({
    validator: (value) => {
      if (!value) return true
      const phoneRegex = /^1[3-9]\d{9}$/
      return phoneRegex.test(value)
    },
    message
  }),

  minLength: (min, message) => ({
    validator: (value) => {
      if (!value) return true
      return value.length >= min
    },
    message: message || `至少需要${min}个字符`
  }),

  maxLength: (max, message) => ({
    validator: (value) => {
      if (!value) return true
      return value.length <= max
    },
    message: message || `不能超过${max}个字符`
  }),

  pattern: (regex, message = '格式不正确') => ({
    validator: (value) => {
      if (!value) return true
      return regex.test(value)
    },
    message
  }),

  numeric: (message = '请输入数字') => ({
    validator: (value) => {
      if (!value) return true
      return !isNaN(value) && !isNaN(parseFloat(value))
    },
    message
  }),

  url: (message = '请输入有效的URL') => ({
    validator: (value) => {
      if (!value) return true
      try {
        new URL(value)
        return true
      } catch {
        return false
      }
    },
    message
  })
}

// 本地存储工具
export const storage = {
  get: (key, defaultValue = null) => {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : defaultValue
    } catch {
      return defaultValue
    }
  },

  set: (key, value) => {
    try {
      localStorage.setItem(key, JSON.stringify(value))
      return true
    } catch {
      return false
    }
  },

  remove: (key) => {
    try {
      localStorage.removeItem(key)
      return true
    } catch {
      return false
    }
  },

  clear: () => {
    try {
      localStorage.clear()
      return true
    } catch {
      return false
    }
  }
}

// URL 参数工具
export const urlParams = {
  get: (param) => {
    const urlParams = new URLSearchParams(window.location.search)
    return urlParams.get(param)
  },

  getAll: () => {
    const urlParams = new URLSearchParams(window.location.search)
    const params = {}
    for (const [key, value] of urlParams) {
      params[key] = value
    }
    return params
  },

  set: (param, value) => {
    const url = new URL(window.location)
    url.searchParams.set(param, value)
    window.history.pushState({}, '', url)
  },

  remove: (param) => {
    const url = new URL(window.location)
    url.searchParams.delete(param)
    window.history.pushState({}, '', url)
  }
}