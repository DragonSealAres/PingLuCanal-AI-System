import request from './request'

export function getUsers() {
  return request.get('/users')
}

export function createUser(data) {
  return request.post('/users', data)
}

export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

export function resetUserPassword(id, password) {
  return request.put(`/users/${id}/password`, { password })
}

export function getWorkers() {
  return request.get('/users/workers')
}
