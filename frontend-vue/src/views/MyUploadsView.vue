<template>
  <section class="panel page">
    <div class="toolbar">
      <el-button :loading="loading" @click="loadVideos">刷新</el-button>
      <el-button type="primary" @click="router.push('/upload')">上传视频</el-button>
    </div>

    <el-table :data="videos" v-loading="loading" border>
      <el-table-column prop="title" label="视频标题" min-width="180" />
      <el-table-column prop="status" label="处理状态" width="130" />
      <el-table-column prop="duration" label="时长(秒)" width="110">
        <template #default="{ row }">{{ row.duration ?? '-' }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="上传时间" width="190">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="router.push(`/videos/${row.id}`)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchMyVideos } from '../api/client'

const router = useRouter()
const videos = ref([])
const loading = ref(false)

async function loadVideos() {
  loading.value = true
  try {
    videos.value = await fetchMyVideos()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

onMounted(loadVideos)
</script>
