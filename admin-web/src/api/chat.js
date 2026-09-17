import request from './request'

export function askAssistant(data) {
  return request.post('/chat/ask', data)
}

export function getChatSessions() {
  return request.get('/chat/sessions')
}

export function getChatMessages(sessionId) {
  return request.get(`/chat/sessions/${sessionId}/messages`)
}
