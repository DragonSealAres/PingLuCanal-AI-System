import request from './request'

export function reviewImages(data) {
  return request.post('/ai/review-images', data)
}
