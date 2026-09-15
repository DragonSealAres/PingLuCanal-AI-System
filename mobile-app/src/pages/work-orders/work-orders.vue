<script setup>
import { onMounted, ref } from 'vue'
import { getHazards } from '../../api/hazard'
import { getMyWorkOrders } from '../../api/workOrder'
import { getBaseUrl } from '../../api/request'

const currentUser = '处置人员01'
const workOrders = ref([])
const loading = ref(false)

function fileUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${getBaseUrl()}${url}`
}

function statusLabel(status) {
  return status === '待派单' ? '待处理' : status || '-'
}

function statusClass(status) {
  if (status === '待派单' || status === '待处理') return 'status-pending'
  if (status === '处理中') return 'status-processing'
  if (status === '待复核') return 'status-review'
  if (status === '已完成') return 'status-done'
  return 'status-pending'
}

function riskClass(level) {
  if (level === '高风险') return 'risk-high'
  if (level === '中风险') return 'risk-mid'
  return 'risk-low'
}

function formatTime(value) {
  return value ? value.replace('T', ' ').slice(0, 16) : '-'
}

function openDetail(order) {
  uni.navigateTo({ url: `/pages/work-order-detail/work-order-detail?id=${order.id}` })
}

async function loadOrders() {
  loading.value = true
  try {
    const [orders, hazards] = await Promise.all([getMyWorkOrders(currentUser), getHazards()])
    const hazardMap = new Map((hazards || []).map((item) => [item.id, item]))
    workOrders.value = (orders || []).map((order) => ({
      ...order,
      hazard: hazardMap.get(order.hazardId) || null,
    }))
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<template>
  <view class="page work-orders-page">
    <view class="top-bar">
      <view>
        <text class="title">我的工单</text>
        <text class="subtitle">当前用户：{{ currentUser }}</text>
      </view>
      <button class="refresh-button" :loading="loading" @click="loadOrders">刷新</button>
    </view>

    <view v-if="!workOrders.length && !loading" class="empty">
      <text>暂无分配给你的工单</text>
    </view>

    <view
      v-for="order in workOrders"
      :key="order.id"
      class="order-card"
      @click="openDetail(order)"
    >
      <view class="order-head">
        <text class="order-no">{{ order.orderNo }}</text>
        <text class="status-tag" :class="statusClass(order.status)">{{ statusLabel(order.status) }}</text>
      </view>
      <view class="order-body">
        <image
          v-if="order.hazard?.imageUrl"
          class="order-thumb"
          :src="fileUrl(order.hazard.imageUrl)"
          mode="aspectFill"
        />
        <view class="order-content">
          <text class="order-title">{{ order.title }}</text>
          <view class="order-line">
            <text>{{ order.hazard?.hazardType || '隐患' }}</text>
            <text class="risk-tag" :class="riskClass(order.hazard?.riskLevel)">
              {{ order.hazard?.riskLevel || '-' }}
            </text>
          </view>
          <text class="order-location">{{ order.hazard?.location || '位置待补充' }}</text>
          <text class="order-meta">处置人员：{{ order.handler }} · {{ formatTime(order.createTime) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.work-orders-page {
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
  color: #102a43;
  font-size: 21px;
  font-weight: 700;
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
  margin-top: 90px;
  text-align: center;
  color: #829ab1;
}

.order-card {
  margin-bottom: 12px;
  padding: 14px;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 8px 20px rgba(16, 42, 67, 0.08);
}

.order-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.order-no {
  overflow: hidden;
  color: #102a43;
  font-size: 14px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag,
.risk-tag {
  flex: 0 0 auto;
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 11px;
}

.status-pending {
  color: #486581;
  background: #e6eef2;
}

.status-processing {
  color: #8a4b00;
  background: #fff3cd;
}

.status-review {
  color: #3c366b;
  background: #e9e7ff;
}

.status-done {
  color: #276749;
  background: #dff6e8;
}

.order-body {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.order-thumb {
  flex: 0 0 88px;
  width: 88px;
  height: 88px;
  border-radius: 10px;
  background: #d9e2ec;
}

.order-content {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 7px;
}

.order-title {
  overflow: hidden;
  color: #243b53;
  font-size: 16px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-line {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #486581;
  font-size: 13px;
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

.order-location {
  overflow: hidden;
  color: #486581;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-meta {
  color: #9fb3c8;
  font-size: 11px;
}
</style>
