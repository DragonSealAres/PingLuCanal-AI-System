import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, logout } from '../utils/auth'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export function getBaseUrl() {
  const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL
  if (configuredBaseUrl) {
    return configuredBaseUrl.replace(/\/$/, '')
  }

  if (typeof window !== 'undefined' && window.location.hostname) {
    return `${window.location.protocol}//${window.location.hostname}:8080`
  }

  return 'http://localhost:8080'
}

function redirectToLogin() {
  logout()
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

function resolveErrorMessage(error) {
  const responseMessage = error.response?.data?.message
  if (responseMessage) return responseMessage
  if (error.code === 'ECONNABORTED') return '后端连接超时，请确认服务是否正常'
  if (error.message === 'Network Error') return '后端连接失败，请检查后端服务或网络'
  return error.message || '请求失败'
}

service.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result && result.code === 200) {
      return result.data
    }

    if (result?.code === 401) {
      redirectToLogin()
    }
    const message = result?.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    if (error.response?.status === 401 || error.response?.data?.code === 401) {
      redirectToLogin()
    }
    const message = resolveErrorMessage(error)
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

export default service
