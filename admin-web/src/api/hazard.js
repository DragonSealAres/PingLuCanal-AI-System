import request from './request'

export function getHazards() {
  return request.get('/hazards')
}

export function getHazardDetail(id) {
  return request.get(`/hazards/${id}`)
}

export function approveHazard(id, data) {
  return request.put(`/hazards/${id}/approve`, data)
}
