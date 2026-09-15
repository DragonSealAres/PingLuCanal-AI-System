import { request } from './request'

export function analyzeImage(imageUrl) {
  return request({
    url: '/api/ai/analyze-image',
    method: 'POST',
    data: { imageUrl },
  })
}
