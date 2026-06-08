import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import ReviewView from '../views/ReviewView.vue'
import SensitiveWordsView from '../views/SensitiveWordsView.vue'
import UploadView from '../views/UploadView.vue'
import VideoDetailView from '../views/VideoDetailView.vue'
import VideosView from '../views/VideosView.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: DashboardView, meta: { title: '统计看板' } },
  { path: '/login', component: LoginView, meta: { title: '登录' } },
  { path: '/review', component: ReviewView, meta: { title: '人工复审' } },
  { path: '/sensitive-words', component: SensitiveWordsView, meta: { title: '敏感词管理' } },
  { path: '/upload', component: UploadView, meta: { title: '视频上传' } },
  { path: '/videos', component: VideosView, meta: { title: '视频管理' } },
  { path: '/videos/:id', component: VideoDetailView, meta: { title: '视频详情' } }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
