<template>
  <section class="page">
    <div class="panel detail-header">
      <div>
        <h2>{{ task?.title || '复审处理' }}</h2>
        <p>{{ task?.description || '查看 AI 证据并提交人工复审结论' }}</p>
      </div>
      <div class="toolbar action-toolbar">
        <el-button :icon="Back" @click="router.push('/review')">返回列表</el-button>
        <el-button :icon="Refresh" :loading="loading" @click="loadTask">刷新</el-button>
      </div>
    </div>

    <div class="detail-grid">
      <div class="panel">
        <video v-if="task" class="video-player" controls :src="toAssetUrl(task.fileUrl)" />
      </div>

      <div class="panel page">
        <el-descriptions title="审核信息" :column="1" border>
          <el-descriptions-item label="处理状态">{{ task?.status || '-' }}</el-descriptions-item>
          <el-descriptions-item label="AI 风险等级">{{ task?.aiRiskLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="AI 风险分">{{ task?.aiRiskScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="违规类别">{{ task?.violationCategory || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最终结论">{{ task?.finalResult || '-' }}</el-descriptions-item>
          <el-descriptions-item label="ASR 文本">
            <el-button v-if="task" link type="primary" @click="router.push(`/videos/${task.id}/asr`)">查看 ASR 文本</el-button>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-form :model="form" label-width="90px">
          <el-form-item label="视频状态">
            <el-radio-group v-model="form.status">
              <el-radio-button label="通过">通过</el-radio-button>
              <el-radio-button label="驳回">驳回</el-radio-button>
              <el-radio-button label="待申诉">待申诉</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="form.status !== '通过'" label="违规类别">
            <el-select v-model="form.violationCategory" placeholder="请选择违规类别">
              <el-option label="暴力" value="暴力" />
              <el-option label="色情" value="色情" />
              <el-option label="政治敏感" value="政治敏感" />
            </el-select>
          </el-form-item>
          <el-form-item label="审核意见">
            <el-input v-model="form.comment" type="textarea" :rows="4" placeholder="请输入人工复审意见" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">提交复审</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="panel page">
      <h3>敏感命中</h3>
      <el-table :data="task?.sensitiveHits || []" border>
        <el-table-column prop="sourceType" label="来源" width="120" />
        <el-table-column prop="word" label="命中内容" min-width="160" />
        <el-table-column prop="category" label="类别" width="140" />
        <el-table-column prop="weight" label="权重" width="100" />
        <el-table-column prop="contextText" label="上下文" min-width="180" />
      </el-table>
    </div>

    <div class="panel page">
      <div class="section-header">
        <h3>关键帧缩略图</h3>
        <span class="section-meta">共 {{ frames.length }} 帧</span>
      </div>
      <el-empty v-if="!frames.length" description="暂无抽帧" />
      <div v-else class="frame-grid">
        <div v-for="frame in pagedFrames" :key="frame.id" class="frame-item">
          <img :src="toAssetUrl(frame.frameUrl)" :alt="`frame-${frame.id}`" />
          <span>{{ frame.timestampSec ?? 0 }}s / {{ frame.riskScore ?? 0 }}分</span>
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

    <div class="panel page">
      <h3>复审日志</h3>
      <el-table :data="reviewLogs" border>
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
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back, Refresh } from '@element-plus/icons-vue'
import { fetchReviewLogs, fetchReviewTask, submitReview, toAssetUrl } from '../api/client'

const route = useRoute()
const router = useRouter()
const task = ref(null)
const reviewLogs = ref([])
const loading = ref(false)
const submitting = ref(false)
const framePageSize = 12
const frameCurrentPage = ref(1)
const frames = computed(() => task.value?.frames || [])
const pagedFrames = computed(() => {
  const start = (frameCurrentPage.value - 1) * framePageSize
  return frames.value.slice(start, start + framePageSize)
})

const form = reactive({
  status: '通过',
  violationCategory: '暴力',
  comment: ''
})

async function loadTask() {
  loading.value = true
  try {
    task.value = await fetchReviewTask(route.params.id)
    reviewLogs.value = await fetchReviewLogs(route.params.id)
    form.status = normalizeSubmitStatus(task.value.status)
    form.violationCategory = task.value.violationCategory || '暴力'
    form.comment = ''
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!task.value) {
    return
  }
  if (!form.comment.trim()) {
    ElMessage.warning('请输入人工复审意见')
    return
  }

  submitting.value = true
  try {
    task.value = await submitReview(task.value.id, {
      status: form.status,
      violationCategory: form.status === '通过' ? null : form.violationCategory,
      comment: form.comment.trim()
    })
    reviewLogs.value = await fetchReviewLogs(task.value.id)
    form.comment = ''
    ElMessage.success('复审已提交')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    submitting.value = false
  }
}

function normalizeSubmitStatus(status) {
  return ['通过', '驳回', '待申诉'].includes(status) ? status : '通过'
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

watch(frames, () => {
  if ((frameCurrentPage.value - 1) * framePageSize >= frames.value.length) {
    frameCurrentPage.value = 1
  }
})

onMounted(loadTask)
</script>
