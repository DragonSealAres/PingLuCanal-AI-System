const TOKEN_KEY = 'pinglu_admin_token'
const USER_KEY = 'pinglu_admin_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getCurrentUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function setCurrentUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearCurrentUser() {
  localStorage.removeItem(USER_KEY)
}

export function logout() {
  clearToken()
  clearCurrentUser()
}

export function isAdminUser(user = getCurrentUser()) {
  return user?.role === 'ADMIN' || user?.role === 'SUPER_ADMIN'
}
