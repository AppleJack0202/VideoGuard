<template>
  <section class="page">
    <div class="panel detail-header">
      <div>
        <h2>{{ video?.title || 'ASR 文本' }}</h2>
        <p>{{ video?.description || '查看视频语音识别文本' }}</p>
      </div>
      <div class="toolbar action-toolbar">
        <el-button :icon="Back" @click="router.back()">返回</el-button>
        <el-button :icon="Refresh" :loading="loading" @click="loadDetail">刷新</el-button>
        <el-button type="primary" :icon="Refresh" :loading="refreshingAsr" @click="handleRefreshAsr">
          {{ hasAsrText ? '刷新 ASR' : '生成 ASR' }}
        </el-button>
      </div>
    </div>

    <div class="panel page">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="处理状态">{{ video?.status || '-' }}</el-descriptions-item>
        <el-descriptions-item label="AI 风险等级">{{ video?.aiResult?.riskLevel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="ASR 风险分">{{ video?.aiResult?.asrScore ?? '-' }}</el-descriptions-item>
      </el-descriptions>

      <pre class="asr-text-block">{{ asrText }}</pre>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back, Refresh } from '@element-plus/icons-vue'
import { fetchVideoDetail, refreshVideoAsr } from '../api/client'

const route = useRoute()
const router = useRouter()
const video = ref(null)
const loading = ref(false)
const refreshingAsr = ref(false)

const asrText = computed(() => video.value?.aiResult?.asrText || '暂无 ASR 文本')
const hasAsrText = computed(() => Boolean(video.value?.aiResult?.asrText))

async function loadDetail() {
  loading.value = true
  try {
    video.value = await fetchVideoDetail(route.params.id)
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

async function handleRefreshAsr() {
  refreshingAsr.value = true
  try {
    video.value = await refreshVideoAsr(route.params.id)
    ElMessage.success('ASR 文本已更新')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    refreshingAsr.value = false
  }
}

onMounted(loadDetail)
</script>
