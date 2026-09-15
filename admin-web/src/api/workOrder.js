import request from './request'

export function getWorkOrders() {
  return request.get('/work-orders')
}

export function createWorkOrder(data) {
  return request.post('/work-orders/create', data)
}

export function startWorkOrder(id) {
  return request.put(`/work-orders/${id}/start`)
}

export function finishWorkOrder(id, data) {
  return request.put(`/work-orders/${id}/finish`, data)
}

export function approveWorkOrder(id) {
  return request.put(`/work-orders/${id}/approve`)
}
