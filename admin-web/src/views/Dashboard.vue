<script setup>
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getHazards } from '../api/hazard'
import { getWorkOrders } from '../api/workOrder'

const router = useRouter()
const hazards = ref([])
const workOrders = ref([])
const loading = ref(false)
const dataError = ref('')
const typeChartRef = ref(null)
const riskChartRef = ref(null)
const trendChartRef = ref(null)
const orderChartRef = ref(null)
let typeChart = null
let riskChart = null
let trendChart = null
let orderChart = null

const hazardTypes = ['漂浮物', '航道障碍物', '船舶异常', '航标异常', '水面污染', '岸线异常', '非法占道', '其他']
const riskLevels = ['高风险', '中风险', '低风险']
const orderStatuses = ['待处理', '处理中', '待复核', '已完成']

const hazardTotal = computed(() => hazards.value.length)
const todayHazards = computed(() => hazards.value.filter((item) => getDateKey(item.createTime) === getDateKey(new Date())).length)
const highRiskTotal = computed(() => hazards.value.filter((item) => item.riskLevel === '高风险').length)
const processingOrderTotal = computed(() => workOrders.value.filter((item) => item.status === '处理中').length)
const pendingReviewOrderTotal = computed(() => workOrders.value.filter((item) => item.status === '待复核').length)
const doneOrderTotal = computed(() => workOrders.value.filter((item) => item.status === '已完成').length)
const recentHighRiskHazards = computed(() => hazards.value
  .filter((item) => item.riskLevel === '高风险')
  .sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))
  .slice(0, 5))

function normalizeOrderStatus(status) {
  return status === '待派单' ? '待处理' : status || '未分类'
}

function countByValues(list, key, values) {
  const counts = Object.fromEntries(values.map((value) => [value, 0]))
  list.forEach((item) => {
    const value = key === 'status' ? normalizeOrderStatus(item[key]) : item[key]
    if (counts[value] === undefined) counts[value] = 0
    counts[value] += 1
  })
  return counts
}

function parseLocalDate(value) {
  if (!value) return null
  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value
  }

  const match = String(value).match(
    /^(\d{4})-(\d{2})-(\d{2})(?:[T\s](\d{2}):(\d{2})(?::(\d{2}))?)?/,
  )
  if (match) {
    const [, year, month, day, hour = '0', minute = '0', second = '0'] = match
    return new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hour),
      Number(minute),
      Number(second),
    )
  }

  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? null : date
}

function getDateKey(value) {
  const date = parseLocalDate(value)
  if (!date) return ''
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function last7Days() {
  return Array.from({ length: 7 }, (_, index) => {
    const date = new Date()
    date.setDate(date.getDate() - (6 - index))
    return getDateKey(date)
  })
}

function shortDate(dateKey) {
  return dateKey.substring(5)
}

function formatTime(value) {
  const date = parseLocalDate(value)
  if (!date) return '-'
  const datePart = getDateKey(date)
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')
  return `${datePart} ${hour}:${minute}:${second}`
}

function riskTagType(level) {
  if (level === '高风险') return 'danger'
  if (level === '中风险') return 'warning'
  if (level === '低风险') return 'success'
  return 'info'
}

function chartDataFromCounts(counts, values) {
  return values.map((name) => ({ name, value: counts[name] || 0 }))
}

function isAllZero(data) {
  return data.every((item) => Number(typeof item === 'object' ? item.value : item) === 0)
}

function noDataGraphic(show) {
  return show
    ? {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: {
          text: '暂无数据',
          fill: '#829ab1',
          fontSize: 14,
        },
      }
    : []
}

function renderCharts() {
  const typeCounts = countByValues(hazards.value, 'hazardType', hazardTypes)
  const riskCounts = countByValues(hazards.value, 'riskLevel', riskLevels)
  const orderCounts = countByValues(workOrders.value, 'status', orderStatuses)
  const days = last7Days()
  const dailyCounts = countByValues(hazards.value.map((item) => ({ day: getDateKey(item.createTime) })), 'day', days)
  const typeData = chartDataFromCounts(typeCounts, hazardTypes)
  const riskData = riskLevels.map((name) => riskCounts[name] || 0)
  const trendData = days.map((day) => dailyCounts[day] || 0)
  const orderData = chartDataFromCounts(orderCounts, orderStatuses)

  typeChart = typeChart || echarts.init(typeChartRef.value)
  riskChart = riskChart || echarts.init(riskChartRef.value)
  trendChart = trendChart || echarts.init(trendChartRef.value)
  orderChart = orderChart || echarts.init(orderChartRef.value)

  typeChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    graphic: noDataGraphic(isAllZero(typeData)),
    color: ['#1f7a8c', '#e0a458', '#2f855a', '#d1495b', '#4f6d7a', '#805ad5', '#718096', '#2d3748'],
    series: [
      {
        name: '隐患类型',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '43%'],
        data: typeData,
      },
    ],
  })

  riskChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 36, right: 18, top: 28, bottom: 32 },
    xAxis: { type: 'category', data: riskLevels },
    yAxis: { type: 'value', minInterval: 1 },
    graphic: noDataGraphic(isAllZero(riskData)),
    color: ['#d1495b'],
    series: [
      {
        name: '隐患数量',
        type: 'bar',
        barWidth: 34,
        data: riskData,
        itemStyle: {
          color: (params) => ['#d1495b', '#e0a458', '#2f855a'][params.dataIndex],
        },
      },
    ],
  })

  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 42, right: 18, top: 32, bottom: 38 },
    xAxis: { type: 'category', boundaryGap: false, data: days.map(shortDate) },
    yAxis: { type: 'value', minInterval: 1 },
    graphic: noDataGraphic(trendData.every((value) => value === 0)),
    color: ['#1f7a8c'],
    series: [
      {
        name: '新增隐患',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        areaStyle: { opacity: 0.12 },
        data: trendData,
      },
    ],
  })

  orderChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    graphic: noDataGraphic(isAllZero(orderData)),
    color: ['#718096', '#e0a458', '#1f7a8c', '#2f855a'],
    series: [
      {
        name: '工单状态',
        type: 'pie',
        radius: ['45%', '68%'],
        center: ['50%', '43%'],
        data: orderData,
      },
    ],
  })
}

async function loadData() {
  loading.value = true
  dataError.value = ''
  try {
    const [hazardList, workOrderList] = await Promise.all([getHazards(), getWorkOrders()])
    hazards.value = Array.isArray(hazardList) ? hazardList : []
    workOrders.value = Array.isArray(workOrderList) ? workOrderList : []
    await nextTick()
    renderCharts()
  } catch (error) {
    dataError.value = error?.message || 'Dashboard数据加载失败'
    ElMessage.error(dataError.value)
    hazards.value = []
    workOrders.value = []
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

function resizeCharts() {
  typeChart?.resize()
  riskChart?.resize()
  trendChart?.resize()
  orderChart?.resize()
}

function disposeCharts() {
  typeChart?.dispose()
  riskChart?.dispose()
  trendChart?.dispose()
  orderChart?.dispose()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  disposeCharts()
})
</script>

<template>
  <div v-loading="loading" class="page dashboard-page">
    <div class="toolbar">
      <div>
        <h2>Dashboard数据看板</h2>
        <p>基于隐患与工单真实数据实时统计</p>
      </div>
      <div class="toolbar-actions">
        <el-button @click="loadData">刷新</el-button>
        <el-button type="primary" @click="router.push('/hazard-map')">进入航道隐患一张图</el-button>
      </div>
    </div>

    <el-alert v-if="dataError" :title="dataError" type="error" show-icon :closable="false" />

    <div class="metric-grid dashboard-metrics">
      <section class="metric-card">
        <span>隐患总数</span>
        <strong>{{ hazardTotal }}</strong>
      </section>
      <section class="metric-card">
        <span>今日新增隐患</span>
        <strong>{{ todayHazards }}</strong>
      </section>
      <section class="metric-card danger">
        <span>高风险隐患</span>
        <strong>{{ highRiskTotal }}</strong>
      </section>
      <section class="metric-card warning">
        <span>处理中工单</span>
        <strong>{{ processingOrderTotal }}</strong>
      </section>
      <section class="metric-card primary">
        <span>待复核工单</span>
        <strong>{{ pendingReviewOrderTotal }}</strong>
      </section>
      <section class="metric-card success">
        <span>已完成工单</span>
        <strong>{{ doneOrderTotal }}</strong>
      </section>
    </div>

    <div class="chart-grid">
      <section class="panel">
        <div class="panel-title">隐患类型分布</div>
        <div ref="typeChartRef" class="chart"></div>
      </section>
      <section class="panel">
        <div class="panel-title">风险等级统计</div>
        <div ref="riskChartRef" class="chart"></div>
      </section>
    </div>

    <section class="panel">
      <div class="panel-title">最近7天隐患趋势</div>
      <div ref="trendChartRef" class="chart chart-wide"></div>
    </section>

    <div class="chart-grid">
      <section class="panel">
        <div class="panel-title">工单状态分布</div>
        <div ref="orderChartRef" class="chart"></div>
      </section>
      <section class="panel high-risk-panel">
        <div class="panel-title">高风险隐患</div>
        <el-table :data="recentHighRiskHazards" height="312px" empty-text="暂无高风险隐患">
          <el-table-column prop="reportNo" label="隐患编号" min-width="160" show-overflow-tooltip />
          <el-table-column prop="hazardType" label="隐患类型" width="110" />
          <el-table-column prop="location" label="位置" min-width="150" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="92" />
          <el-table-column label="风险等级" width="100">
            <template #default="{ row }">
              <el-tag :type="riskTagType(row.riskLevel)" effect="light">{{ row.riskLevel }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="上报时间" width="170">
            <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="92">
            <template #default="{ row }">
              <el-button type="primary" link @click="router.push(`/hazards/${row.id}`)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>
    </div>
  </div>
</template>

<style scoped>
.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dashboard-metrics {
  grid-template-columns: repeat(6, minmax(0, 1fr));
}

.metric-card.primary {
  border-left-color: var(--brand-teal);
}

.chart-wide {
  height: 320px;
}

.high-risk-panel {
  overflow: hidden;
}

.table-image.small {
  width: 48px;
  height: 48px;
}

@media (max-width: 1440px) {
  .dashboard-metrics {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
