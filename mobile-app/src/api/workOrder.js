import { request } from './request'

export function getMyWorkOrders() {
  return request({
    url: '/api/work-orders/my',
    method: 'GET',
  })
}

export function getWorkOrderDetail(id) {
  return request({
    url: `/api/work-orders/${id}`,
    method: 'GET',
  })
}

export function startWorkOrder(id) {
  return request({
    url: `/api/work-orders/${id}/start`,
    method: 'PUT',
  })
}

export function finishWorkOrder(id, data) {
  return request({
    url: `/api/work-orders/${id}/finish`,
    method: 'PUT',
    data,
  })
}
