<template>
  <el-container class="app-shell">
    <el-aside class="sidebar" width="232px">
      <div class="brand">
        <strong>VideoGuard</strong>
        <span>AI 内容审核</span>
      </div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/dashboard">统计看板</el-menu-item>
        <el-menu-item index="/upload">视频上传</el-menu-item>
        <el-menu-item index="/videos">视频管理</el-menu-item>
        <el-menu-item index="/review">人工复审</el-menu-item>
        <el-menu-item index="/sensitive-words">敏感词管理</el-menu-item>
        <el-menu-item index="/login">登录</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <span>{{ routeTitle }}</span>
        <div class="user-chip">
          <template v-if="currentUser">
            <span>{{ currentUser.username }}</span>
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

const route = useRoute()
const router = useRouter()
const currentUser = ref(null)

const routeTitle = computed(() => route.meta.title || 'VideoGuard')

function loadUser() {
  const raw = localStorage.getItem('videoguard_user')
  currentUser.value = raw ? JSON.parse(raw) : null
}

function logout() {
  localStorage.removeItem('videoguard_user')
  loadUser()
  router.push('/login')
}

onMounted(() => {
  loadUser()
  window.addEventListener('videoguard:user-updated', loadUser)
})

onBeforeUnmount(() => {
  window.removeEventListener('videoguard:user-updated', loadUser)
})
</script>
