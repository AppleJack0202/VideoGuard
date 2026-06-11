export function getStoredUser() {
  const raw = localStorage.getItem('videoguard_user')
  if (!raw) {
    return null
  }
  try {
    return normalizeUser(JSON.parse(raw))
  } catch {
    clearSession()
    return null
  }
}

export function saveStoredUser(user) {
  localStorage.setItem('videoguard_user', JSON.stringify(normalizeUser(user)))
}

export function clearSession() {
  localStorage.removeItem('videoguard_user')
  localStorage.removeItem('videoguard_token')
}

export function normalizeUser(user) {
  const roleMap = {
    USER: '一般用户',
    REVIEWER: '审核员',
    ADMIN: '管理员'
  }
  return {
    ...user,
    displayName: user.displayName || user.username,
    role: roleMap[user.role] || user.role
  }
}
