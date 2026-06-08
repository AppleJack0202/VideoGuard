<template>
  <section class="panel">
    <el-form class="form-compact" :model="form" label-width="92px">
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="请输入视频标题" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入视频描述" />
      </el-form-item>
      <el-form-item label="上传人 ID">
        <el-input-number v-model="form.uploaderId" :min="1" />
      </el-form-item>
      <el-form-item label="视频文件">
        <el-upload
          drag
          action="#"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖拽视频到此处，或点击选择</div>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="uploading" @click="submitUpload">上传视频</el-button>
        <el-button :disabled="!uploadedVideo" :loading="analyzing" @click="startAnalyze">开始 AI 分析</el-button>
        <el-button v-if="uploadedVideo" @click="openDetail">查看详情</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { analyzeVideo, uploadVideo } from '../api/client'

const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  uploaderId: 1
})

const selectedFile = ref(null)
const uploadedVideo = ref(null)
const uploading = ref(false)
const analyzing = ref(false)

function currentVideoId() {
  return uploadedVideo.value?.videoId
}

function handleFileChange(file) {
  selectedFile.value = file.raw
  if (!form.title && file.name) {
    form.title = file.name.replace(/\.[^.]+$/, '')
  }
}

function handleFileRemove() {
  selectedFile.value = null
}

async function submitUpload() {
  if (!form.title.trim()) {
    ElMessage.warning('请填写视频标题')
    return
  }
  if (!selectedFile.value) {
    ElMessage.warning('请选择视频文件')
    return
  }

  uploading.value = true
  try {
    uploadedVideo.value = await uploadVideo({
      file: selectedFile.value,
      title: form.title.trim(),
      description: form.description,
      uploaderId: form.uploaderId
    })
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    uploading.value = false
  }
}

async function startAnalyze() {
  const videoId = currentVideoId()
  if (!videoId) {
    return
  }

  analyzing.value = true
  try {
    await analyzeVideo(videoId)
    ElMessage.success('AI 分析完成')
    router.push(`/videos/${videoId}`)
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    analyzing.value = false
  }
}

function openDetail() {
  const videoId = currentVideoId()
  if (videoId) {
    router.push(`/videos/${videoId}`)
  }
}
</script>
