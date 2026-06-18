<template>
  <section class="panel page">
    <div class="toolbar filter-toolbar">
      <div class="filter-group">
        <span class="filter-label">处理状态</span>
        <el-select v-model="filters.status" clearable placeholder="全部状态" class="filter-control">
          <el-option label="已上传" value="已上传" />
          <el-option label="预审中" value="预审中" />
          <el-option label="复审中" value="复审中" />
          <el-option label="待申诉" value="待申诉" />
          <el-option label="通过" value="通过" />
          <el-option label="驳回" value="驳回" />
        </el-select>
      </div>
      <div class="filter-group">
        <span class="filter-label">风险等级</span>
        <el-select v-model="filters.aiRiskLevel" clearable placeholder="全部等级" class="filter-control">
          <el-option label="正常" value="正常" />
          <el-option label="可疑" value="可疑" />
          <el-option label="违规" value="违规" />
        </el-select>
      </div>
      <div class="filter-group">
        <span class="filter-label">违规类别</span>
        <el-select v-model="filters.violationCategory" clearable placeholder="全部类别" class="filter-control">
          <el-option label="暴力" value="暴力" />
          <el-option label="色情" value="色情" />
          <el-option label="政治敏感" value="政治敏感" />
          <el-option label="其他违规" value="其他违规" />
        </el-select>
      </div>
      <div class="toolbar-actions">
        <el-button :icon="Refresh" :loading="loading" @click="loadVideos">刷新</el-button>
      </div>
    </div>

    <el-table :data="videos" v-loading="loading" border>
      <el-table-column prop="title" label="视频标题" min-width="180" />
      <el-table-column prop="status" label="状态" width="150" />
      <el-table-column prop="aiRiskLevel" label="AI 风险等级" width="150">
        <template #default="{ row }">{{ row.aiRiskLevel || '-' }}</template>
      </el-table-column>
      <el-table-column prop="violationCategory" label="违规类别" width="190">
        <template #default="{ row }">
          <div v-if="splitCategories(row.violationCategory).length" class="category-tags">
            <el-tag
              v-for="category in splitCategories(row.violationCategory)"
              :key="category"
              :type="categoryTagType(category)"
              effect="light"
              size="small"
            >
              {{ category }}
            </el-tag>
          </div>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="contentCategory" label="内容分类" width="130">
        <template #default="{ row }">
          <el-tag v-if="row.contentCategory" effect="light" type="success" size="small">
            {{ row.contentCategory }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="reviewStrategy" label="审核策略" width="130">
        <template #default="{ row }">{{ row.reviewStrategy || '-' }}</template>
      </el-table-column>
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
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { fetchVideos } from '../api/client'
import { categoryTagType, splitCategories } from '../utils/categories'

const router = useRouter()
const videos = ref([])
const loading = ref(false)
const filters = reactive({
  status: '',
  aiRiskLevel: '',
  violationCategory: ''
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

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

watch(filters, loadVideos)
onMounted(loadVideos)
</script>
