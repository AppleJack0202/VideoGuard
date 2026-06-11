<template>
  <section class="panel page">
    <div class="toolbar filter-toolbar">
      <div class="filter-group">
        <span class="filter-label">敏感类别</span>
        <el-select v-model="filters.category" clearable placeholder="全部类别" class="filter-control">
          <el-option label="暴力" value="暴力" />
          <el-option label="色情" value="色情" />
          <el-option label="政治敏感" value="政治敏感" />
        </el-select>
      </div>
      <div class="filter-group">
        <span class="filter-label">启用状态</span>
        <el-select v-model="filters.enabled" clearable placeholder="全部状态" class="filter-control">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </div>
      <div class="toolbar-actions">
        <el-button :icon="Refresh" :loading="loading" @click="loadWords">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="openCreate">
          {{ isAdmin ? '新增敏感词' : '提交敏感词建议' }}
        </el-button>
      </div>
    </div>

    <el-table :data="words" v-loading="loading" border>
      <el-table-column prop="word" label="敏感词" min-width="160" />
      <el-table-column prop="category" label="类别" width="140" />
      <el-table-column prop="weight" label="权重" width="100" />
      <el-table-column prop="enabled" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="row.enabled === 1 ? 'success' : 'info'">{{ row.enabled === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="190">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column v-if="isAdmin" label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.enabled === 1 ? 'warning' : 'success'"
            @click="toggleEnabled(row)"
          >
            {{ row.enabled === 1 ? '停用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="removeWord(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="460px">
      <el-form :model="form" label-width="82px">
        <el-alert
          v-if="!isAdmin"
          class="form-alert"
          type="info"
          show-icon
          :closable="false"
          title="审核员提交的敏感词默认停用，需管理员确认后才参与 AI 检测。"
        />
        <el-form-item label="敏感词">
          <el-input v-model="form.word" placeholder="请输入敏感词" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.category" allow-create filterable placeholder="请选择或输入类别">
            <el-option label="暴力" value="暴力" />
            <el-option label="色情" value="色情" />
            <el-option label="政治敏感" value="政治敏感" />
          </el-select>
        </el-form-item>
        <el-form-item label="权重">
          <el-input-number v-model="form.weight" :min="1" :max="100" />
        </el-form-item>
        <el-form-item v-if="isAdmin" label="启用">
          <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveWord">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import {
  createSensitiveWord,
  deleteSensitiveWord,
  fetchSensitiveWords,
  updateSensitiveWord
} from '../api/client'

const words = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const currentUser = computed(() => {
  const raw = localStorage.getItem('videoguard_user')
  return raw ? JSON.parse(raw) : null
})
const isAdmin = computed(() => currentUser.value?.role === '管理员')
const dialogTitle = computed(() => {
  if (editingId.value) {
    return '编辑敏感词'
  }
  return isAdmin.value ? '新增敏感词' : '提交敏感词建议'
})

const filters = reactive({
  category: '',
  enabled: ''
})

const form = reactive({
  word: '',
  category: '暴力',
  weight: 20,
  enabled: 1
})

async function loadWords() {
  loading.value = true
  try {
    words.value = await fetchSensitiveWords(filters)
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { word: '', category: '暴力', weight: 20, enabled: isAdmin.value ? 1 : 0 })
  dialogVisible.value = true
}

function openEdit(row) {
  if (!isAdmin.value) {
    return
  }
  editingId.value = row.id
  Object.assign(form, {
    word: row.word,
    category: row.category,
    weight: row.weight,
    enabled: row.enabled
  })
  dialogVisible.value = true
}

async function saveWord() {
  if (!form.word.trim()) {
    ElMessage.warning('请输入敏感词')
    return
  }
  if (!form.category.trim()) {
    ElMessage.warning('请输入类别')
    return
  }

  saving.value = true
  try {
    const payload = {
      word: form.word.trim(),
      category: form.category.trim(),
      weight: form.weight,
      enabled: isAdmin.value ? form.enabled : 0
    }
    if (editingId.value) {
      if (!isAdmin.value) {
        ElMessage.warning('审核员不能编辑敏感词')
        return
      }
      await updateSensitiveWord(editingId.value, payload)
    } else {
      await createSensitiveWord(payload)
    }
    dialogVisible.value = false
    ElMessage.success(isAdmin.value ? '保存成功' : '敏感词建议已提交，等待管理员启用')
    await loadWords()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    saving.value = false
  }
}

async function toggleEnabled(row) {
  try {
    await updateSensitiveWord(row.id, {
      word: row.word,
      category: row.category,
      weight: row.weight,
      enabled: row.enabled === 1 ? 0 : 1
    })
    ElMessage.success(row.enabled === 1 ? '已停用' : '已启用')
    await loadWords()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function removeWord(row) {
  try {
    await ElMessageBox.confirm(`确认删除敏感词“${row.word}”？`, '删除确认', { type: 'warning' })
    await deleteSensitiveWord(row.id)
    ElMessage.success('删除成功')
    await loadWords()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '-'
}

watch(filters, loadWords)
onMounted(loadWords)
</script>
