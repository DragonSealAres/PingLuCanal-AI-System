import { request } from './request'

export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'POST',
    data,
  })
}

export function getMe() {
  return request({
    url: '/api/auth/me',
    method: 'GET',
  })
}
