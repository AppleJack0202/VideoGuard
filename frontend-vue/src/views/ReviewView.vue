<template>
  <section class="panel">
    <el-table :data="tasks" border>
      <el-table-column prop="title" label="视频标题" />
      <el-table-column prop="riskLevel" label="风险等级" width="140" />
      <el-table-column prop="riskScore" label="风险分" width="120" />
      <el-table-column prop="status" label="状态" width="160" />
    </el-table>
  </section>
</template>

<script setup>
const tasks = [
  { title: '演示可疑视频', riskLevel: 'SUSPICIOUS', riskScore: 45, status: 'AI_SUSPICIOUS' }
]
</script>

