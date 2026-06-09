const API_BASE_URL = 'http://localhost:8081'

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

export function fetchReviewTasks(filters = {}) {
  const params = new URLSearchParams()
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      params.set(key, value)
    }
  })
  const query = params.toString()
  return request(`/api/review/tasks${query ? `?${query}` : ''}`)
}

export function fetchReviewTask(videoId) {
  return request(`/api/review/tasks/${videoId}`)
}

export function submitReview(videoId, payload) {
  return request(`/api/review/tasks/${videoId}/submit`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  })
}

export function fetchReviewLogs(videoId) {
  return request(`/api/review/logs/${videoId}`)
}

export function fetchStatisticsOverview() {
  return request('/api/statistics/overview')
}

export function fetchRiskDistribution() {
  return request('/api/statistics/risk-distribution')
}

export function fetchStatusDistribution() {
  return request('/api/statistics/status-distribution')
}

export function fetchDailyUploads(days = 7) {
  return request(`/api/statistics/daily-upload?days=${days}`)
}

export function fetchCategoryDistribution() {
  return request('/api/statistics/category-distribution')
}

export function fetchSensitiveWords(filters = {}) {
  const params = new URLSearchParams()
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      params.set(key, value)
    }
  })
  const query = params.toString()
  return request(`/api/sensitive-words${query ? `?${query}` : ''}`)
}

export function createSensitiveWord(payload) {
  return request('/api/sensitive-words', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  })
}

export function updateSensitiveWord(id, payload) {
  return request(`/api/sensitive-words/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  })
}

export function deleteSensitiveWord(id) {
  return request(`/api/sensitive-words/${id}`, {
    method: 'DELETE'
  })
}

export function login(payload) {
  return request('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  })
}

export function fetchCurrentUser(userId) {
  const query = userId ? `?userId=${userId}` : ''
  return request(`/api/auth/me${query}`)
}
