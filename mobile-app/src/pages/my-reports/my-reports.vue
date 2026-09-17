<script setup>
import { onMounted, ref } from 'vue'
import { getMyHazards } from '../../api/hazard'
import { getBaseUrl } from '../../api/request'

const reports = ref([])
const loading = ref(false)

function fullImageUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${getBaseUrl()}${url}`
}

function riskClass(level) {
  if (level === '高风险') return 'risk-high'
  if (level === '中风险') return 'risk-mid'
  return 'risk-low'
}

function formatTime(value) {
  return value ? value.replace('T', ' ').slice(0, 16) : '-'
}

function showDetail(report) {
  let aiResult = null
  if (report.aiResult) {
    try {
      aiResult = JSON.parse(report.aiResult)
    } catch {
      aiResult = null
    }
  }
  uni.showModal({
    title: report.reportNo,
    content: `位置：${report.location}\n类型：${report.hazardType}\n风险：${report.riskLevel}\n描述：${report.description}\n建议：${aiResult?.suggestion || '-'}`,
    showCancel: false,
  })
}

async function loadReports() {
  loading.value = true
  try {
    reports.value = (await getMyHazards()) || []
  } finally {
    loading.value = false
  }
}

onMounted(loadReports)
</script>

<template>
  <view class="page reports-page">
    <view class="top-bar">
      <view>
        <text class="title">我的上报</text>
        <text class="subtitle">当前显示全部隐患记录</text>
      </view>
      <button class="refresh-button" :loading="loading" @click="loadReports">刷新</button>
    </view>

    <view v-if="!reports.length && !loading" class="empty">暂无上报记录</view>

    <view v-for="report in reports" :key="report.id" class="report-card" @click="showDetail(report)">
      <image v-if="report.imageUrl" class="report-thumb" :src="fullImageUrl(report.imageUrl)" mode="aspectFill" />
      <view class="report-main">
        <view class="report-head">
          <text class="report-no">{{ report.reportNo }}</text>
          <text class="risk-tag" :class="riskClass(report.riskLevel)">{{ report.riskLevel }}</text>
        </view>
        <view class="report-desc">{{ report.description }}</view>
        <view class="report-meta">
          <text>{{ report.hazardType }}</text>
          <text>{{ report.status }}</text>
        </view>
        <view class="report-time">{{ formatTime(report.createTime) }}</view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.reports-page {
  min-height: 100vh;
  padding: 14px;
  background: #eef5f7;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.title {
  display: block;
  font-size: 21px;
  font-weight: 700;
  color: #102a43;
}

.subtitle {
  display: block;
  margin-top: 4px;
  color: #627d98;
  font-size: 12px;
}

.refresh-button {
  width: 76px;
  height: 38px;
  border: 0;
  border-radius: 10px;
  color: #ffffff;
  background: #1f7a8c;
  font-size: 14px;
}

.refresh-button::after {
  border: 0;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 72px;
  padding: 36px 20px;
  color: #829ab1;
  font-size: 14px;
}

.empty::before {
  content: '';
  width: 92px;
  height: 92px;
  margin-bottom: 16px;
  border-radius: 50%;
  background: #ffffff url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48' fill='none' stroke='%239fb3c8' stroke-width='2.4' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='12' y='7' width='24' height='34' rx='4'/%3E%3Cpath d='M18 17h12M18 24h12M18 31h8'/%3E%3C/svg%3E") center / 46px no-repeat;
  box-shadow: 0 10px 24px rgba(16, 42, 67, 0.08);
}

.empty::after {
  content: '完成一次隐患上报后，记录会显示在这里';
  margin-top: 6px;
  color: #9fb3c8;
  font-size: 12px;
}

.report-card {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 8px 20px rgba(16, 42, 67, 0.08);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.report-card:active {
  transform: scale(0.985);
  box-shadow: 0 4px 12px rgba(16, 42, 67, 0.1);
}

.report-thumb {
  flex: 0 0 86px;
  width: 86px;
  height: 86px;
  border-radius: 10px;
  background: #d9e2ec;
}

.report-main {
  flex: 1;
  min-width: 0;
}

.report-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.report-no {
  overflow: hidden;
  color: #102a43;
  font-size: 14px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.risk-tag {
  flex: 0 0 auto;
  padding: 3px 7px;
  border-radius: 999px;
  font-size: 11px;
}

.risk-high {
  color: #9b1c1c;
  background: #fde2e2;
}

.risk-mid {
  color: #8a4b00;
  background: #fff3cd;
}

.risk-low {
  color: #276749;
  background: #dff6e8;
}

.report-desc {
  display: -webkit-box;
  overflow: hidden;
  margin-top: 8px;
  color: #486581;
  font-size: 13px;
  line-height: 1.4;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.report-meta {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  color: #627d98;
  font-size: 12px;
}

.report-time {
  margin-top: 5px;
  color: #9fb3c8;
  font-size: 12px;
}
</style>
