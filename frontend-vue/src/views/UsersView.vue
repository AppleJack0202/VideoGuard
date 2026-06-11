<template>
  <section class="panel page">
    <div class="toolbar action-toolbar">
      <el-button :icon="Refresh" :loading="loading" @click="loadUsers">刷新</el-button>
    </div>

    <el-table :data="users" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="displayName" label="用户名" min-width="150">
        <template #default="{ row }">{{ row.displayName || row.username }}</template>
      </el-table-column>
      <el-table-column prop="username" label="登录账号" min-width="140" />
      <el-table-column prop="role" label="角色" width="180">
        <template #default="{ row }">
          <el-select v-model="row.role" @change="(role) => changeRole(row, role)">
            <el-option label="一般用户" value="一般用户" />
            <el-option label="审核员" value="审核员" />
            <el-option label="管理员" value="管理员" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="190">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { fetchUsers, updateUserRole } from '../api/client'

const users = ref([])
const loading = ref(false)

async function loadUsers() {
  loading.value = true
  try {
    users.value = await fetchUsers()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

async function changeRole(row, role) {
  try {
    await updateUserRole(row.id, role)
    ElMessage.success('角色已更新')
  } catch (error) {
    ElMessage.error(error.message)
    await loadUsers()
  }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

onMounted(loadUsers)
</script>
