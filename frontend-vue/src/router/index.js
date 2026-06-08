import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import ReviewView from '../views/ReviewView.vue'
import UploadView from '../views/UploadView.vue'
import VideosView from '../views/VideosView.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: DashboardView, meta: { title: '统计看板' } },
  { path: '/login', component: LoginView, meta: { title: '登录' } },
  { path: '/review', component: ReviewView, meta: { title: '人工复审' } },
  { path: '/upload', component: UploadView, meta: { title: '视频上传' } },
  { path: '/videos', component: VideosView, meta: { title: '视频管理' } }
]

export default createRouter({
  history: createWebHistory(),
  routes
})

