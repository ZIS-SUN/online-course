import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// Request interceptor
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 0) {
      ElMessage.error(res.message || 'Error')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    // 处理超时错误
    if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
      // 不显示全局错误消息，让调用方自己处理超时
      console.warn('Request timeout:', error.config?.url)
    } else if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.error('Please login first')
    } else if (error.response?.status >= 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (!error.response) {
      // 网络错误或服务器无响应
      ElMessage.error('网络连接失败，请检查网络设置')
    }
    return Promise.reject(error)
  }
)

export default request