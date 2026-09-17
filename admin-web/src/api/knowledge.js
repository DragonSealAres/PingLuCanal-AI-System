import request from './request'

export function getKnowledgeDocuments() {
  return request.get('/knowledge/documents')
}

export function uploadKnowledgeDocument(file, documentName) {
  const formData = new FormData()
  formData.append('file', file)
  if (documentName) {
    formData.append('documentName', documentName)
  }
  return request.post('/knowledge/documents', formData)
}

export function deleteKnowledgeDocument(id) {
  return request.delete(`/knowledge/documents/${id}`)
}

export function reparseKnowledgeDocument(id) {
  return request.post(`/knowledge/documents/${id}/reparse`)
}

export function importDemoKnowledgeDocuments() {
  return request.post('/knowledge/demo-import')
}
