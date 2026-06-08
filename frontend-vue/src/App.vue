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
        <el-menu-item index="/login">登录</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <span>{{ routeTitle }}</span>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const routeTitle = computed(() => route.meta.title || 'VideoGuard')
</script>
