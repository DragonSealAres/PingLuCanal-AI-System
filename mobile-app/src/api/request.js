function resolveBaseUrl() {
  const configuredBaseUrl = import.meta.env?.VITE_API_BASE_URL
  if (configuredBaseUrl) {
    return configuredBaseUrl.replace(/\/$/, '')
  }

  if (typeof window !== 'undefined' && window.location.hostname) {
    return `${window.location.protocol}//${window.location.hostname}:8080`
  }

  return 'http://localhost:8080'
}

export function getBaseUrl() {
  return resolveBaseUrl()
}

function buildRequestFailMessage(error) {
  if (error?.errMsg?.includes('timeout')) return '后端连接超时'
  if (error?.errMsg?.includes('request:fail')) return '后端连接失败'
  return '网络请求失败'
}

function buildUploadFailMessage(error) {
  if (error?.errMsg?.includes('timeout')) return '图片上传超时'
  if (error?.errMsg?.includes('uploadFile:fail')) return '图片上传失败，请检查后端服务'
  return '图片上传失败'
}

export function request(options) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${resolveBaseUrl()}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(options.header || {}),
      },
      success: (res) => {
        const result = res.data
        if (res.statusCode >= 200 && res.statusCode < 300 && result?.code === 200) {
          resolve(result.data)
          return
        }

        const message = result?.message || `请求失败(${res.statusCode})`
        uni.showToast({ title: message, icon: 'none' })
        reject(new Error(message))
      },
      fail: (error) => {
        uni.showToast({ title: buildRequestFailMessage(error), icon: 'none' })
        reject(error)
      },
    })
  })
}

export function uploadFile(options) {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${resolveBaseUrl()}${options.url}`,
      file: options.file,
      filePath: options.filePath,
      name: options.name || 'file',
      formData: options.formData || {},
      timeout: options.timeout || 60000,
      success: (res) => {
        let result = res.data
        if (typeof result === 'string') {
          try {
            result = JSON.parse(result)
          } catch {
            result = null
          }
        }

        if (res.statusCode >= 200 && res.statusCode < 300 && result?.code === 200) {
          resolve(result.data)
          return
        }

        const message = result?.message || `上传失败(${res.statusCode})`
        uni.showToast({ title: message, icon: 'none' })
        reject(new Error(message))
      },
      fail: (error) => {
        uni.showToast({ title: buildUploadFailMessage(error), icon: 'none' })
        reject(error)
      },
    })
  })
}
