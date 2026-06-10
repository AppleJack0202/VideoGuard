<template>
  <section class="panel login-panel">
    <el-tabs v-model="mode">
      <el-tab-pane label="登录" name="login" />
      <el-tab-pane label="注册" name="register" />
    </el-tabs>
    <el-form :model="form" label-width="72px">
      <el-form-item label="登录账号">
        <el-input v-model="form.username" placeholder="请输入登录账号" />
      </el-form-item>
      <el-form-item v-if="mode === 'register'" label="用户名">
        <el-input v-model="form.displayName" placeholder="请输入页面显示的用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submitAuth">{{ mode === 'login' ? '登录' : '注册' }}</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/client'

const router = useRouter()
const mode = ref('login')
const loading = ref(false)
const form = reactive({
  username: '',
  displayName: '',
  password: ''
})

async function submitAuth() {
  if (!form.username || !form.password.trim()) {
    ElMessage.warning('请输入登录账号和密码')
    return
  }

  loading.value = true
  try {
    const action = mode.value === 'login' ? login : register
    const payload = {
      username: form.username,
      password: form.password.trim()
    }
    if (mode.value === 'register') {
      payload.displayName = form.displayName
    }
    const user = await action(payload)
    localStorage.setItem('videoguard_user', JSON.stringify(user))
    localStorage.setItem('videoguard_token', user.token)
    window.dispatchEvent(new Event('videoguard:user-updated'))
    ElMessage.success(`${mode.value === 'login' ? '已登录' : '注册成功'}：${user.displayName || user.username}`)
    router.push(defaultRoute(user.role))
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

function defaultRoute(role) {
  if (role === '一般用户') {
    return '/upload'
  }
  if (role === '审核员') {
    return '/review'
  }
  return '/dashboard'
}
</script>
