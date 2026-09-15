import { uploadFile } from './request'

export function uploadImage(filePath, file) {
  return uploadFile({
    url: '/api/files/upload',
    filePath,
    file,
    name: 'file',
  })
}
