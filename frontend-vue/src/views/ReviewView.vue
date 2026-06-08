<template>
  <section class="review-workbench">
    <div class="panel page">
      <div class="toolbar">
        <el-select v-model="filters.status" clearable placeholder="处理状态">
          <el-option label="AI 可疑" value="AI_SUSPICIOUS" />
          <el-option label="AI 违规" value="AI_VIOLATION" />
        </el-select>
        <el-select v-model="filters.aiRiskLevel" clearable placeholder="风险等级">
          <el-option label="可疑" value="SUSPICIOUS" />
          <el-option label="违规" value="VIOLATION" />
        </el-select>
        <el-button :loading="loadingTasks" @click="loadTasks">刷新</el-button>
      </div>

      <el-table
        :data="tasks"
        v-loading="loadingTasks"
        border
        highlight-current-row
        @row-click="selectTask"
      >
        <el-table-column prop="title" label="视频标题" min-width="170" />
        <el-table-column prop="aiRiskLevel" label="风险" width="100" />
        <el-table-column prop="aiRiskScore" label="分数" width="80" />
        <el-table-column prop="status" label="状态" width="140" />
      </el-table>
    </div>

    <div class="page">
      <div class="panel detail-header">
        <div>
          <h2>{{ currentTask?.title || '请选择复审任务' }}</h2>
          <p>{{ currentTask?.description || '从左侧任务列表选择一条视频后开始复审' }}</p>
        </div>
        <el-button v-if="currentTask" @click="router.push(`/videos/${currentTask.id}`)">查看详情页</el-button>
      </div>

      <div v-if="currentTask" class="detail-grid">
        <div class="panel">
          <video class="video-player" controls :src="toAssetUrl(currentTask.fileUrl)" />
        </div>

        <div class="panel page">
          <div class="metric-grid detail-metrics">
            <div class="metric">
              <span>处理状态</span>
              <strong>{{ currentTask.status || '-' }}</strong>
            </div>
            <div class="metric">
              <span>AI 风险等级</span>
              <strong>{{ currentTask.aiRiskLevel || '-' }}</strong>
            </div>
            <div class="metric">
              <span>AI 风险分</span>
              <strong>{{ currentTask.aiRiskScore ?? '-' }}</strong>
            </div>
            <div class="metric">
              <span>最终结论</span>
              <strong>{{ currentTask.finalResult || '-' }}</strong>
            </div>
          </div>

          <el-form :model="form" label-width="90px">
            <el-form-item label="审核员 ID">
              <el-input-number v-model="form.reviewerId" :min="1" />
            </el-form-item>
            <el-form-item label="复审结论">
              <el-radio-group v-model="form.finalResult">
                <el-radio-button label="PASS">通过</el-radio-button>
                <el-radio-button label="REJECT">驳回</el-radio-button>
              </el-radio-group>
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

      <div v-if="currentTask" class="panel page">
        <h3>敏感命中</h3>
        <el-table :data="currentTask.sensitiveHits || []" border>
          <el-table-column prop="sourceType" label="来源" width="120" />
          <el-table-column prop="word" label="命中内容" min-width="160" />
          <el-table-column prop="category" label="类别" width="140" />
          <el-table-column prop="weight" label="权重" width="100" />
          <el-table-column prop="contextText" label="上下文" min-width="180" />
        </el-table>
      </div>

      <div v-if="currentTask" class="panel page">
        <h3>复审日志</h3>
        <el-table :data="reviewLogs" border>
          <el-table-column prop="reviewerId" label="审核员" width="100" />
          <el-table-column prop="beforeStatus" label="原状态" width="140" />
          <el-table-column prop="afterStatus" label="新状态" width="150" />
          <el-table-column prop="afterResult" label="结论" width="100" />
          <el-table-column prop="comment" label="意见" min-width="180" />
          <el-table-column prop="createdAt" label="时间" width="190">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  fetchReviewLogs,
  fetchReviewTask,
  fetchReviewTasks,
  submitReview,
  toAssetUrl
} from '../api/client'

const router = useRouter()
const tasks = ref([])
const currentTask = ref(null)
const reviewLogs = ref([])
const loadingTasks = ref(false)
const loadingDetail = ref(false)
const submitting = ref(false)

const filters = reactive({
  status: '',
  aiRiskLevel: ''
})

const form = reactive({
  reviewerId: 2,
  finalResult: 'PASS',
  comment: ''
})

async function loadTasks() {
  loadingTasks.value = true
  try {
    tasks.value = await fetchReviewTasks(filters)
    if (tasks.value.length && !currentTask.value) {
      await selectTask(tasks.value[0])
    }
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loadingTasks.value = false
  }
}

async function selectTask(row) {
  loadingDetail.value = true
  try {
    currentTask.value = await fetchReviewTask(row.id)
    reviewLogs.value = await fetchReviewLogs(row.id)
    form.comment = ''
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loadingDetail.value = false
  }
}

async function handleSubmit() {
  if (!currentTask.value) {
    return
  }
  if (!form.comment.trim()) {
    ElMessage.warning('请输入人工复审意见')
    return
  }

  submitting.value = true
  try {
    currentTask.value = await submitReview(currentTask.value.id, {
      reviewerId: form.reviewerId,
      finalResult: form.finalResult,
      comment: form.comment.trim()
    })
    reviewLogs.value = await fetchReviewLogs(currentTask.value.id)
    tasks.value = tasks.value.filter((task) => task.id !== currentTask.value.id)
    ElMessage.success('复审已提交')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    submitting.value = false
  }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

watch(filters, async () => {
  currentTask.value = null
  reviewLogs.value = []
  await loadTasks()
})
onMounted(loadTasks)
</script>
