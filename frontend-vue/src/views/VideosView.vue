<template>
  <section class="panel">
    <el-table :data="videos" border>
      <el-table-column prop="title" label="视频标题" />
      <el-table-column prop="status" label="状态" width="160" />
      <el-table-column prop="riskLevel" label="AI 风险等级" width="160" />
      <el-table-column prop="riskScore" label="风险分" width="120" />
    </el-table>
  </section>
</template>

<script setup>
const videos = [
  { title: '演示正常视频', status: 'UPLOADED', riskLevel: '-', riskScore: 0 }
]
</script>

