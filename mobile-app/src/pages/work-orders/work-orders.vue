<script setup>
import { onMounted, ref } from 'vue'
import { getMyWorkOrders } from '../../api/workOrder'
import { getCurrentUser, requireLogin } from '../../utils/auth'

const currentUser = ref(null)
const workOrders = ref([])
const loading = ref(false)

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

function formatTime(value) {
  return value ? value.replace('T', ' ').slice(0, 16) : '-'
}

function openDetail(order) {
  uni.navigateTo({ url: `/pages/work-order-detail/work-order-detail?id=${order.id}` })
}

async function loadOrders() {
  loading.value = true
  try {
    workOrders.value = (await getMyWorkOrders()) || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!requireLogin()) return
  currentUser.value = getCurrentUser()
  loadOrders()
})
</script>

<template>
  <view class="page work-orders-page">
    <view class="top-bar">
      <view>
        <text class="title">我的工单</text>
        <text class="subtitle">当前用户：{{ currentUser?.realName || currentUser?.username }}</text>
      </view>
      <button class="refresh-button" :loading="loading" @click="loadOrders">刷新</button>
    </view>

    <view v-if="!workOrders.length && !loading" class="empty">
      <text>暂无分配给你的工单</text>
    </view>

    <view v-for="order in workOrders" :key="order.id" class="order-card" @click="openDetail(order)">
      <view class="order-head">
        <text class="order-no">{{ order.orderNo }}</text>
        <text class="status-tag" :class="statusClass(order.status)">{{ statusLabel(order.status) }}</text>
      </view>
      <text class="order-title">{{ order.title }}</text>
      <text class="order-line">关联隐患：#{{ order.hazardId }}</text>
      <text class="order-line">处置要求：{{ order.requirement || '暂无处置要求' }}</text>
      <text class="order-meta">{{ order.handlerName || order.handler }} · {{ formatTime(order.createTime) }}</text>
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

.title,
.subtitle {
  display: block;
}

.title {
  color: #102a43;
  font-size: 21px;
  font-weight: 700;
}

.subtitle {
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
  background: #ffffff url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48' fill='none' stroke='%239fb3c8' stroke-width='2.4' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='12' y='7' width='24' height='34' rx='4'/%3E%3Cpath d='M18 23l3.5 3.5L30 18M18 32h12'/%3E%3C/svg%3E") center / 46px no-repeat;
  box-shadow: 0 10px 24px rgba(16, 42, 67, 0.08);
}

.empty::after {
  content: '管理员派单后，工单会显示在这里';
  margin-top: 6px;
  color: #9fb3c8;
  font-size: 12px;
}

.order-card {
  margin-bottom: 12px;
  padding: 14px;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 8px 20px rgba(16, 42, 67, 0.08);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.order-card:active {
  transform: scale(0.985);
  box-shadow: 0 4px 12px rgba(16, 42, 67, 0.1);
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

.status-tag {
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

.order-title {
  display: block;
  margin-top: 12px;
  color: #243b53;
  font-size: 16px;
  font-weight: 700;
}

.order-line {
  display: block;
  margin-top: 7px;
  color: #486581;
  font-size: 13px;
  line-height: 1.45;
}

.order-meta {
  display: block;
  margin-top: 8px;
  color: #9fb3c8;
  font-size: 11px;
}
</style>
