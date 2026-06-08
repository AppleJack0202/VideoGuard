<template>
  <div class="page">
    <section class="metric-grid">
      <div v-for="item in metrics" :key="item.label" class="metric">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </div>
    </section>
    <section class="panel">
      <el-empty description="统计图表将在统计接口完成后接入" />
    </section>
  </div>
</template>

<script setup>
const metrics = [
  { label: '总视频数', value: 0 },
  { label: '今日上传', value: 0 },
  { label: 'AI 通过率', value: '0%' },
  { label: '待复审', value: 0 }
]
</script>

