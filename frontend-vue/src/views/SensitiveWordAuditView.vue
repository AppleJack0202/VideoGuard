<template>
  <section class="page audit-page">
    <div class="panel audit-overview">
      <div class="audit-count">
        <span>待审核</span>
        <strong>{{ pendingWords.length }}</strong>
      </div>
      <div class="audit-stats">
        <div>
          <span>最高权重</span>
          <strong>{{ maxWeight }}</strong>
        </div>
        <div>
          <span>类别数</span>
          <strong>{{ categoryCount }}</strong>
        </div>
      </div>
      <el-button :icon="Refresh" :loading="loading" @click="loadPendingWords">刷新</el-button>
    </div>

    <section class="panel audit-list-panel">
      <div class="section-header audit-list-header">
        <h3>建议列表</h3>
        <div class="filter-group">
          <span class="filter-label">敏感类别</span>
          <el-select v-model="filters.category" clearable placeholder="全部类别" class="filter-control">
            <el-option label="暴力" value="暴力" />
            <el-option label="色情" value="色情" />
            <el-option label="政治敏感" value="政治敏感" />
          </el-select>
        </div>
      </div>

      <el-table class="audit-table" :data="pendingWords" v-loading="loading" border empty-text="暂无待审核敏感词">
        <el-table-column prop="word" label="敏感词" min-width="180">
          <template #default="{ row }">
            <strong class="word-cell">{{ row.word }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="类别" width="150">
          <template #default="{ row }">
            <el-tag effect="plain">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="权重" width="100" />
        <el-table-column prop="createdAt" label="提交时间" width="190">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="审核操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" :icon="Check" @click="approveWord(row)">通过</el-button>
            <el-button size="small" type="danger" :icon="Close" @click="rejectWord(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Close, Refresh } from '@element-plus/icons-vue'
import { deleteSensitiveWord, fetchSensitiveWords, updateSensitiveWord } from '../api/client'

const pendingWords = ref([])
const loading = ref(false)

const filters = reactive({
  category: ''
})

const maxWeight = computed(() => {
  if (!pendingWords.value.length) {
    return 0
  }
  return Math.max(...pendingWords.value.map((word) => word.weight || 0))
})

const categoryCount = computed(() => new Set(pendingWords.value.map((word) => word.category)).size)

async function loadPendingWords() {
  loading.value = true
  try {
    pendingWords.value = await fetchSensitiveWords({
      category: filters.category,
      enabled: 0
    })
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

async function approveWord(row) {
  try {
    await updateSensitiveWord(row.id, {
      word: row.word,
      category: row.category,
      weight: row.weight,
      enabled: 1
    })
    ElMessage.success('已通过')
    await loadPendingWords()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function rejectWord(row) {
  try {
    await ElMessageBox.confirm(`确认驳回“${row.word}”？`, '驳回确认', { type: 'warning' })
    await deleteSensitiveWord(row.id)
    ElMessage.success('已驳回')
    await loadPendingWords()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '驳回失败')
    }
  }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

watch(filters, loadPendingWords)
onMounted(loadPendingWords)
</script>
