import request from './request'

export function getBusinessLogs(businessType, businessId) {
  return request.get('/operation-logs/business', {
    params: { businessType, businessId },
  })
}
