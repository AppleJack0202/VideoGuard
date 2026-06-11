<template>
  <section class="page">
    <div class="panel detail-header">
      <div>
        <h2>{{ video?.title || '视频详情' }}</h2>
        <p>{{ video?.description || '暂无描述' }}</p>
      </div>
      <div class="toolbar action-toolbar">
        <el-button :icon="Refresh" :loading="loading" @click="loadDetail">刷新</el-button>
        <el-button v-if="isAdmin" type="primary" :icon="Refresh" :loading="analyzing" @click="handleAnalyze">
          重新分析
        </el-button>
      </div>
    </div>

    <div class="detail-grid">
      <div class="panel">
        <video v-if="video" class="video-player" controls :src="toAssetUrl(video.fileUrl)" />
      </div>

      <div class="panel page">
        <el-descriptions title="视频信息" :column="1" border>
          <el-descriptions-item label="处理状态">{{ video?.status || '-' }}</el-descriptions-item>
          <el-descriptions-item label="时长">{{ formatDuration(video?.duration) }}</el-descriptions-item>
          <el-descriptions-item label="分辨率">{{ formatResolution(video) }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ formatFileSize(video?.fileSize) }}</el-descriptions-item>
          <el-descriptions-item v-if="canViewAuditEvidence" label="上传者">
            {{ formatUploader(video) }}
          </el-descriptions-item>
          <el-descriptions-item label="上传时间">{{ formatDate(video?.createdAt) }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions v-if="canViewAuditEvidence && video?.aiResult" title="审核信息" :column="1" border>
          <el-descriptions-item label="AI 风险等级">{{ video.aiResult.riskLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="违规类别">{{ video.violationCategory || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最终风险分">{{ video.aiResult.finalScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="文本风险分">{{ video.aiResult.textScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="图像风险分">{{ video.aiResult.imageScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="ASR 文本">
            <el-button link type="primary" @click="router.push(`/videos/${video.id}/asr`)">查看 ASR 文本</el-button>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </div>

    <div v-if="canViewAuditEvidence" class="panel page">
      <h3>敏感命中</h3>
      <el-table :data="video?.sensitiveHits || []" border>
        <el-table-column prop="sourceType" label="来源" width="120" />
        <el-table-column prop="word" label="命中内容" min-width="160" />
        <el-table-column prop="category" label="类别" width="140" />
        <el-table-column prop="weight" label="权重" width="100" />
        <el-table-column prop="contextText" label="上下文" min-width="180" />
      </el-table>
    </div>

    <div v-if="canViewAuditEvidence" class="panel page">
      <div class="section-header">
        <h3>视频抽帧</h3>
        <span class="section-meta">共 {{ frames.length }} 帧</span>
      </div>
      <el-empty v-if="!frames.length" description="暂无抽帧" />
      <div v-else class="frame-grid">
        <div v-for="frame in pagedFrames" :key="frame.id" class="frame-item">
          <img :src="toAssetUrl(frame.frameUrl)" :alt="`frame-${frame.id}`" />
          <span>{{ frame.timestampSec ?? 0 }}s</span>
        </div>
      </div>
      <el-pagination
        v-if="frames.length > framePageSize"
        v-model:current-page="frameCurrentPage"
        class="frame-pagination"
        layout="total, prev, pager, next, jumper"
        :page-size="framePageSize"
        :total="frames.length"
      />
    </div>

    <div v-if="canViewAuditEvidence" class="panel page">
      <h3>复审日志</h3>
      <el-table :data="video?.reviewLogs || []" border>
        <el-table-column prop="reviewerDisplayName" label="审核员" width="140">
          <template #default="{ row }">{{ row.reviewerDisplayName || `用户#${row.reviewerId}` }}</template>
        </el-table-column>
        <el-table-column prop="beforeStatus" label="原状态" width="140" />
        <el-table-column prop="afterStatus" label="新状态" width="150" />
        <el-table-column prop="afterResult" label="结论" width="100" />
        <el-table-column prop="comment" label="意见" min-width="180" />
        <el-table-column prop="createdAt" label="时间" width="190">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { analyzeVideo, fetchVideoDetail, toAssetUrl } from '../api/client'

const route = useRoute()
const router = useRouter()
const video = ref(null)
const loading = ref(false)
const analyzing = ref(false)
const framePageSize = 12
const frameCurrentPage = ref(1)
const currentUser = computed(() => {
  const raw = localStorage.getItem('videoguard_user')
  return raw ? JSON.parse(raw) : null
})
const canViewAuditEvidence = computed(() => ['审核员', '管理员'].includes(currentUser.value?.role))
const isAdmin = computed(() => currentUser.value?.role === '管理员')
const frames = computed(() => video.value?.frames || [])
const pagedFrames = computed(() => {
  const start = (frameCurrentPage.value - 1) * framePageSize
  return frames.value.slice(start, start + framePageSize)
})

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

async function handleAnalyze() {
  analyzing.value = true
  try {
    await analyzeVideo(route.params.id)
    ElMessage.success('AI 分析完成')
    await loadDetail()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    analyzing.value = false
  }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

function formatDuration(value) {
  return value === null || value === undefined ? '-' : `${value}s`
}

function formatResolution(value) {
  if (!value?.width || !value?.height) {
    return '-'
  }
  return `${value.width} x ${value.height}`
}

function formatFileSize(value) {
  if (!value) {
    return '-'
  }
  return `${(value / 1024 / 1024).toFixed(2)} MB`
}

function formatUploader(value) {
  if (!value?.uploaderId) {
    return '-'
  }
  const account = value.uploaderUsername || '-'
  const displayName = value.uploaderDisplayName && value.uploaderDisplayName !== value.uploaderUsername
    ? `（${value.uploaderDisplayName}）`
    : ''
  return `#${value.uploaderId} / ${account}${displayName}`
}

watch(frames, () => {
  if ((frameCurrentPage.value - 1) * framePageSize >= frames.value.length) {
    frameCurrentPage.value = 1
  }
})

onMounted(loadDetail)
</script>
