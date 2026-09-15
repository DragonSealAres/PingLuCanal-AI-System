import { request } from './request'

export function getMyWorkOrders(handler = '处置人员01') {
  return request({
    url: '/api/work-orders',
    method: 'GET',
    data: { handler },
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
