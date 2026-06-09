<template>
  <section class="panel login-panel">
    <el-tabs v-model="mode">
      <el-tab-pane label="登录" name="login" />
      <el-tab-pane label="注册" name="register" />
    </el-tabs>
    <el-form :model="form" label-width="72px">
      <el-form-item label="用户名">
        <el-select v-if="mode === 'login'" v-model="form.username" filterable>
          <el-option label="admin" value="admin" />
          <el-option label="reviewer" value="reviewer" />
          <el-option label="user" value="user" />
        </el-select>
        <el-input v-else v-model="form.username" placeholder="请输入新用户名" />
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
  username: 'reviewer',
  password: '123456'
})

async function submitAuth() {
  if (!form.username || !form.password.trim()) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const action = mode.value === 'login' ? login : register
    const user = await action({
      username: form.username,
      password: form.password.trim()
    })
    localStorage.setItem('videoguard_user', JSON.stringify(user))
    localStorage.setItem('videoguard_token', user.token)
    window.dispatchEvent(new Event('videoguard:user-updated'))
    ElMessage.success(`${mode.value === 'login' ? '已登录' : '注册成功'}：${user.username}`)
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
