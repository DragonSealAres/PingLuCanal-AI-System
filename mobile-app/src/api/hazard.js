import { request } from './request'

export function createHazard(data) {
  return request({
    url: '/api/hazards',
    method: 'POST',
    data,
  })
}

export function getHazards() {
  return request({
    url: '/api/hazards',
    method: 'GET',
  })
}

export function getHazardDetail(id) {
  return request({
    url: `/api/hazards/${id}`,
    method: 'GET',
  })
}
