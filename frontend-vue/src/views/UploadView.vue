<template>
  <section class="panel">
    <el-form label-width="88px" style="max-width: 560px">
      <el-form-item label="标题">
        <el-input placeholder="请输入视频标题" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input type="textarea" placeholder="请输入视频描述" />
      </el-form-item>
      <el-form-item label="视频文件">
        <el-upload drag action="#" :auto-upload="false">
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖拽视频到此处，或点击选择</div>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary">上传视频</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { UploadFilled } from '@element-plus/icons-vue'
</script>

