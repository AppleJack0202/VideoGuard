<template>
  <div class="page" v-loading="loading">
    <section class="metric-grid">
      <div v-for="item in metrics" :key="item.label" class="metric">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </div>
    </section>

    <section class="chart-grid">
      <div class="panel">
        <h3>风险等级分布</h3>
        <div ref="riskChartRef" class="chart-box" />
      </div>
      <div class="panel">
        <h3>近 7 日上传趋势</h3>
        <div ref="dailyChartRef" class="chart-box" />
      </div>
      <div class="panel">
        <h3>处理状态分布</h3>
        <div ref="statusChartRef" class="chart-box" />
      </div>
      <div class="panel">
        <h3>敏感类别分布</h3>
        <div ref="categoryChartRef" class="chart-box" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import {
  fetchCategoryDistribution,
  fetchDailyUploads,
  fetchRiskDistribution,
  fetchStatisticsOverview,
  fetchStatusDistribution
} from '../api/client'

const loading = ref(false)
const overview = ref({
  totalVideos: 0,
  todayUploads: 0,
  pendingReviews: 0,
  manualReviewed: 0,
  aiPassRate: 0
})

const riskChartRef = ref(null)
const dailyChartRef = ref(null)
const statusChartRef = ref(null)
const categoryChartRef = ref(null)
const charts = []

const metrics = computed(() => [
  { label: '总视频数', value: overview.value.totalVideos ?? 0 },
  { label: '今日上传', value: overview.value.todayUploads ?? 0 },
  { label: 'AI 通过率', value: `${overview.value.aiPassRate ?? 0}%` },
  { label: '待复审', value: overview.value.pendingReviews ?? 0 },
  { label: '已人工复审', value: overview.value.manualReviewed ?? 0 }
])

async function loadDashboard() {
  loading.value = true
  try {
    const [overviewData, riskData, dailyData, statusData, categoryData] = await Promise.all([
      fetchStatisticsOverview(),
      fetchRiskDistribution(),
      fetchDailyUploads(7),
      fetchStatusDistribution(),
      fetchCategoryDistribution()
    ])

    overview.value = overviewData
    await nextTick()
    renderPie(riskChartRef.value, riskData, '风险等级')
    renderLine(dailyChartRef.value, dailyData)
    renderBar(statusChartRef.value, statusData, '视频数量')
    renderBar(categoryChartRef.value, categoryData, '命中次数')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

function renderPie(element, data, name) {
  const chart = ensureChart(element)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        name,
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '44%'],
        data: toSeriesData(data),
        label: { formatter: '{b}: {c}' }
      }
    ]
  })
}

function renderLine(element, data) {
  const chart = ensureChart(element)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 36, right: 18, top: 28, bottom: 32 },
    xAxis: { type: 'category', data: data.map((item) => item.name) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '上传数',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        areaStyle: { opacity: 0.14 },
        data: data.map((item) => item.count)
      }
    ]
  })
}

function renderBar(element, data, name) {
  const chart = ensureChart(element)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 44, right: 18, top: 28, bottom: 58 },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.name),
      axisLabel: { rotate: data.length > 3 ? 24 : 0 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ name, type: 'bar', barMaxWidth: 42, data: data.map((item) => item.count) }]
  })
}

function ensureChart(element) {
  let chart = echarts.getInstanceByDom(element)
  if (!chart) {
    chart = echarts.init(element)
    charts.push(chart)
  }
  return chart
}

function toSeriesData(data) {
  return data.length ? data.map((item) => ({ name: item.name, value: item.count })) : [{ name: '暂无数据', value: 0 }]
}

function resizeCharts() {
  charts.forEach((chart) => chart.resize())
}

onMounted(() => {
  loadDashboard()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  charts.forEach((chart) => chart.dispose())
})
</script>
