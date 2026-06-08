<template>
  <section class="panel page">
    <div class="toolbar">
      <el-select v-model="filters.status" clearable placeholder="处理状态">
        <el-option label="已上传" value="UPLOADED" />
        <el-option label="AI 正常" value="AI_PASSED" />
        <el-option label="AI 可疑" value="AI_SUSPICIOUS" />
        <el-option label="AI 违规" value="AI_VIOLATION" />
      </el-select>
      <el-select v-model="filters.aiRiskLevel" clearable placeholder="风险等级">
        <el-option label="正常" value="PASS" />
        <el-option label="可疑" value="SUSPICIOUS" />
        <el-option label="违规" value="VIOLATION" />
      </el-select>
      <el-button :loading="loading" @click="loadVideos">刷新</el-button>
      <el-button type="primary" @click="router.push('/upload')">上传视频</el-button>
    </div>

    <el-table :data="videos" v-loading="loading" border>
      <el-table-column prop="title" label="视频标题" min-width="180" />
      <el-table-column prop="status" label="状态" width="150" />
      <el-table-column prop="aiRiskLevel" label="AI 风险等级" width="150">
        <template #default="{ row }">{{ row.aiRiskLevel || '-' }}</template>
      </el-table-column>
      <el-table-column prop="aiRiskScore" label="风险分" width="110">
        <template #default="{ row }">{{ row.aiRiskScore ?? '-' }}</template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(秒)" width="110">
        <template #default="{ row }">{{ row.duration ?? '-' }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="上传时间" width="190">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="190" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="router.push(`/videos/${row.id}`)">详情</el-button>
          <el-button size="small" :loading="analyzingId === row.id" @click="handleAnalyze(row.id)">分析</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { analyzeVideo, fetchVideos } from '../api/client'

const router = useRouter()
const videos = ref([])
const loading = ref(false)
const analyzingId = ref(null)
const filters = reactive({
  status: '',
  aiRiskLevel: ''
})

async function loadVideos() {
  loading.value = true
  try {
    videos.value = await fetchVideos(filters)
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

async function handleAnalyze(videoId) {
  analyzingId.value = videoId
  try {
    await analyzeVideo(videoId)
    ElMessage.success('AI 分析完成')
    await loadVideos()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    analyzingId.value = null
  }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

watch(filters, loadVideos)
onMounted(loadVideos)
</script>
