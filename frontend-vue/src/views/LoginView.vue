<template>
  <section class="panel login-panel">
    <el-form :model="form" label-width="72px">
      <el-form-item label="用户名">
        <el-select v-model="form.username" filterable>
          <el-option label="admin" value="admin" />
          <el-option label="reviewer" value="reviewer" />
          <el-option label="user" value="user" />
        </el-select>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="演示阶段任意非空密码" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submitLogin">登录</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/client'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: 'reviewer',
  password: '123456'
})

async function submitLogin() {
  if (!form.username || !form.password.trim()) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const user = await login({
      username: form.username,
      password: form.password.trim()
    })
    localStorage.setItem('videoguard_user', JSON.stringify(user))
    window.dispatchEvent(new Event('videoguard:user-updated'))
    ElMessage.success(`已登录为 ${user.username}`)
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}
</script>
