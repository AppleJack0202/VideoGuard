<template>
  <el-container class="app-shell">
    <el-aside class="sidebar" width="232px">
      <div class="brand">
        <strong>VideoGuard</strong>
        <span>AI 内容审核</span>
      </div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">{{ item.label }}</el-menu-item>
        <el-menu-item index="/login">登录</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <span>{{ routeTitle }}</span>
        <div class="user-chip">
          <template v-if="currentUser">
            <span>{{ currentUser.displayName || currentUser.username }}</span>
            <el-tag size="small">{{ currentUser.role }}</el-tag>
            <el-button size="small" text @click="logout">退出</el-button>
          </template>
          <el-button v-else size="small" @click="router.push('/login')">登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearSession, getStoredUser, saveStoredUser } from './utils/session'

const route = useRoute()
const router = useRouter()
const currentUser = ref(null)

const routeTitle = computed(() => route.meta.title || 'VideoGuard')

function loadUser() {
  currentUser.value = getStoredUser()
  if (currentUser.value) {
    saveStoredUser(currentUser.value)
  }
}

function logout() {
  clearSession()
  loadUser()
  router.push('/login')
}

const menuItems = computed(() => {
  const role = currentUser.value?.role
  if (role === '一般用户') {
    return [
      { path: '/upload', label: '视频上传' },
      { path: '/my-videos', label: '我的上传' }
    ]
  }
  if (role === '审核员') {
    return [
      { path: '/review', label: '人工复审' },
      { path: '/sensitive-words', label: '敏感词管理' }
    ]
  }
  if (role === '管理员') {
    return [
      { path: '/dashboard', label: '统计看板' },
      { path: '/videos', label: '视频管理' },
      { path: '/sensitive-words', label: '敏感词管理' },
      { path: '/users', label: '用户管理' }
    ]
  }
  return []
})

onMounted(() => {
  loadUser()
  window.addEventListener('videoguard:user-updated', loadUser)
})

onBeforeUnmount(() => {
  window.removeEventListener('videoguard:user-updated', loadUser)
})
</script>
