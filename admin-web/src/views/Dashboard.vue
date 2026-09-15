<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { getHazards } from '../api/hazard'
import { getWorkOrders } from '../api/workOrder'

const hazards = ref([])
const workOrders = ref([])
const loading = ref(false)
const typeChartRef = ref(null)
const riskChartRef = ref(null)
let typeChart = null
let riskChart = null

const hazardTotal = computed(() => hazards.value.length)
const highRiskTotal = computed(() => hazards.value.filter((item) => item.riskLevel === '高风险').length)
const processingOrderTotal = computed(() => workOrders.value.filter((item) => item.status === '处理中').length)
const doneOrderTotal = computed(() => workOrders.value.filter((item) => item.status === '已完成').length)

function countBy(list, key) {
  return list.reduce((result, item) => {
    const value = item[key] || '未分类'
    result[value] = (result[value] || 0) + 1
    return result
  }, {})
}

function renderCharts() {
  const typeData = Object.entries(countBy(hazards.value, 'hazardType')).map(([name, value]) => ({ name, value }))
  const riskData = Object.entries(countBy(hazards.value, 'riskLevel')).map(([name, value]) => ({ name, value }))

  typeChart = typeChart || echarts.init(typeChartRef.value)
  riskChart = riskChart || echarts.init(riskChartRef.value)

  typeChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    color: ['#1f7a8c', '#e0a458', '#6a8d73', '#d1495b', '#4f6d7a', '#8f5f38', '#3d405b', '#7d8597'],
    series: [
      {
        name: '隐患类型',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '45%'],
        data: typeData.length ? typeData : [{ name: '暂无数据', value: 0 }],
      },
    ],
  })

  riskChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 36, right: 18, top: 28, bottom: 32 },
    color: ['#d1495b'],
    xAxis: { type: 'category', data: ['低风险', '中风险', '高风险'] },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '数量',
        type: 'bar',
        barWidth: 32,
        data: ['低风险', '中风险', '高风险'].map((name) => riskData.find((item) => item.name === name)?.value || 0),
      },
    ],
  })
}

async function loadData() {
  loading.value = true
  try {
    const [hazardList, workOrderList] = await Promise.all([getHazards(), getWorkOrders()])
    hazards.value = hazardList || []
    workOrders.value = workOrderList || []
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

function resizeCharts() {
  typeChart?.resize()
  riskChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  typeChart?.dispose()
  riskChart?.dispose()
})
</script>

<template>
  <div v-loading="loading" class="page dashboard-page">
    <div class="metric-grid">
      <section class="metric-card">
        <span>隐患总数</span>
        <strong>{{ hazardTotal }}</strong>
      </section>
      <section class="metric-card danger">
        <span>高风险隐患</span>
        <strong>{{ highRiskTotal }}</strong>
      </section>
      <section class="metric-card warning">
        <span>处理中工单</span>
        <strong>{{ processingOrderTotal }}</strong>
      </section>
      <section class="metric-card success">
        <span>已完成工单</span>
        <strong>{{ doneOrderTotal }}</strong>
      </section>
    </div>

    <div class="chart-grid">
      <section class="panel">
        <div class="panel-title">隐患类型统计</div>
        <div ref="typeChartRef" class="chart"></div>
      </section>
      <section class="panel">
        <div class="panel-title">风险等级统计</div>
        <div ref="riskChartRef" class="chart"></div>
      </section>
    </div>
  </div>
</template>
