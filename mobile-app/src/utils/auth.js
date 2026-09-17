const TOKEN_KEY = 'pinglu_mobile_token'
const USER_KEY = 'pinglu_mobile_user'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function getCurrentUser() {
  return uni.getStorageSync(USER_KEY) || null
}

export function setCurrentUser(user) {
  uni.setStorageSync(USER_KEY, user)
}

export function logout() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(USER_KEY)
}

export function requireLogin() {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' })
    return false
  }
  return true
}
