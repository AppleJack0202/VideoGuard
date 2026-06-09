import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import MyUploadsView from '../views/MyUploadsView.vue'
import ReviewView from '../views/ReviewView.vue'
import SensitiveWordsView from '../views/SensitiveWordsView.vue'
import UploadView from '../views/UploadView.vue'
import UsersView from '../views/UsersView.vue'
import VideoDetailView from '../views/VideoDetailView.vue'
import VideosView from '../views/VideosView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/dashboard', component: DashboardView, meta: { title: '统计看板', roles: ['管理员'] } },
  { path: '/login', component: LoginView, meta: { title: '登录' } },
  { path: '/review', component: ReviewView, meta: { title: '人工复审', roles: ['审核员'] } },
  { path: '/sensitive-words', component: SensitiveWordsView, meta: { title: '敏感词管理', roles: ['管理员'] } },
  { path: '/upload', component: UploadView, meta: { title: '视频上传', roles: ['一般用户'] } },
  { path: '/my-videos', component: MyUploadsView, meta: { title: '我的上传', roles: ['一般用户'] } },
  { path: '/videos', component: VideosView, meta: { title: '视频管理', roles: ['管理员'] } },
  { path: '/users', component: UsersView, meta: { title: '用户管理', roles: ['管理员'] } },
  { path: '/videos/:id', component: VideoDetailView, meta: { title: '视频详情', roles: ['一般用户', '审核员', '管理员'] } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path === '/login') {
    return true
  }
  const raw = localStorage.getItem('videoguard_user')
  const token = localStorage.getItem('videoguard_token')
  const user = raw ? normalizeUser(JSON.parse(raw)) : null
  if (!user || !token) {
    return '/login'
  }
  const roles = to.meta.roles || []
  if (roles.length && !roles.includes(user.role)) {
    if (user.role === '一般用户') {
      return '/upload'
    }
    if (user.role === '审核员') {
      return '/review'
    }
    return '/dashboard'
  }
  return true
})

export default router

function normalizeUser(user) {
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
