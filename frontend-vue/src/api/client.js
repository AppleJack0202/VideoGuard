const API_BASE_URL = 'http://localhost:8080'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, options)
  const contentType = response.headers.get('content-type') || ''
  const data = contentType.includes('application/json') ? await response.json() : await response.text()

  if (!response.ok) {
    const message = typeof data === 'string' ? data : data.message || '请求失败'
    throw new Error(message)
  }

  return data
}

export function toAssetUrl(path) {
  if (!path) {
    return ''
  }
  return path.startsWith('http') ? path : `${API_BASE_URL}${path}`
}

export function uploadVideo({ file, title, description, uploaderId }) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('title', title)
  formData.append('description', description || '')
  formData.append('uploaderId', uploaderId || 1)

  return request('/api/videos/upload', {
    method: 'POST',
    body: formData
  })
}

export function analyzeVideo(videoId) {
  return request(`/api/videos/${videoId}/analyze`, {
    method: 'POST'
  })
}

export function fetchVideos(filters = {}) {
  const params = new URLSearchParams()
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      params.set(key, value)
    }
  })
  const query = params.toString()
  return request(`/api/videos${query ? `?${query}` : ''}`)
}

export function fetchVideoDetail(videoId) {
  return request(`/api/videos/${videoId}`)
}
