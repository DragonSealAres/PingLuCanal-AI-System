import { request } from './request'

export function askAssistant(data) {
  return request({
    url: '/api/chat/ask',
    method: 'POST',
    data,
  })
}
