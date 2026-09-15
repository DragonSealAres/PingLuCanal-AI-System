<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getHazardDetail } from '../../api/hazard'
import { getBaseUrl } from '../../api/request'
import { uploadImage } from '../../api/file'
import { finishWorkOrder, getWorkOrderDetail, startWorkOrder } from '../../api/workOrder'

const order = ref(null)
const hazard = ref(null)
const loading = ref(false)
const actionLoading = ref(false)
const localHandleImage = ref('')
const handleImage = ref('')
const selectedFile = ref(null)
const form = reactive({
  handleRemark: '',
})

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

function parseAiResult(value) {
  if (!value) return null
  if (typeof value === 'object') return value
  try {
    return JSON.parse(value)
  } catch {
    return null
  }
}

function formatTime(value) {
  return value ? value.replace('T', ' ').slice(0, 19) : '-'
}

function chooseHandleImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed', 'original'],
    sourceType: ['camera', 'album'],
    success: (res) => {
      localHandleImage.value = res.tempFilePaths[0]
      selectedFile.value = res.tempFiles?.[0] || null
      handleImage.value = ''
    },
    fail: () => {
      uni.showToast({ title: '没有选择图片', icon: 'none' })
    },
  })
}

async function uploadHandleImage() {
  if (!localHandleImage.value) {
    uni.showToast({ title: '请先拍摄或选择处理后照片', icon: 'none' })
    return false
  }

  actionLoading.value = true
  try {
    const result = await uploadImage(localHandleImage.value, selectedFile.value)
    handleImage.value = result.url
    uni.showToast({ title: '处理照片上传成功', icon: 'success' })
    return true
  } finally {
    actionLoading.value = false
  }
}

async function handleStart() {
  actionLoading.value = true
  try {
    order.value = await startWorkOrder(order.value.id)
    uni.showToast({ title: '已开始处理', icon: 'success' })
  } finally {
    actionLoading.value = false
  }
}

async function submitResult() {
  if (!form.handleRemark.trim()) {
    uni.showToast({ title: '请填写处理说明', icon: 'none' })
    return
  }
  if (!handleImage.value) {
    const uploaded = await uploadHandleImage()
    if (!uploaded) return
  }

  actionLoading.value = true
  try {
    order.value = await finishWorkOrder(order.value.id, {
      handleRemark: form.handleRemark.trim(),
      handleImage: handleImage.value,
    })
    uni.showToast({ title: '处理结果已提交，等待管理员复核', icon: 'none' })
  } finally {
    actionLoading.value = false
  }
}

async function loadDetail(id) {
  loading.value = true
  try {
    order.value = await getWorkOrderDetail(id)
    hazard.value = await getHazardDetail(order.value.hazardId)
    handleImage.value = order.value.handleImage || ''
    form.handleRemark = order.value.handleRemark || ''
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage?.options?.id
  if (!id) {
    uni.showToast({ title: '工单参数缺失', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 800)
    return
  }
  loadDetail(id)
})
</script>

<template>
  <view class="page order-detail-page">
    <view v-if="loading" class="loading-tip">正在加载工单详情...</view>
    <view v-if="order && hazard" class="section">
      <view class="order-head">
        <view>
          <text class="order-no">{{ order.orderNo }}</text>
          <text class="order-title">{{ order.title }}</text>
        </view>
        <text class="status-tag" :class="statusClass(order.status)">{{ statusLabel(order.status) }}</text>
      </view>
    </view>

    <view v-if="order && hazard" class="section">
      <view class="section-title">隐患信息</view>
      <image
        v-if="hazard.imageUrl"
        class="hazard-image"
        :src="fileUrl(hazard.imageUrl)"
        mode="aspectFill"
      />
      <view class="info-grid">
        <view class="info-item">
          <text class="label">关联隐患</text>
          <text class="value">{{ hazard.reportNo }}</text>
        </view>
        <view class="info-item">
          <text class="label">隐患类型</text>
          <text class="value">{{ hazard.hazardType }}</text>
        </view>
        <view class="info-item">
          <text class="label">风险等级</text>
          <text class="value risk-value" :class="riskClass(hazard.riskLevel)">{{ hazard.riskLevel }}</text>
        </view>
        <view class="info-item">
          <text class="label">处置人员</text>
          <text class="value">{{ order.handler }}</text>
        </view>
      </view>
      <view class="info-block">
        <text class="label">隐患位置</text>
        <text class="value">{{ hazard.location }}</text>
      </view>
      <view class="info-block">
        <text class="label">隐患描述</text>
        <text class="value">{{ hazard.description }}</text>
      </view>
      <view class="info-block">
        <text class="label">AI处置建议</text>
        <text class="value">{{ parseAiResult(hazard.aiResult)?.suggestion || '-' }}</text>
      </view>
      <view class="info-block">
        <text class="label">管理员处置要求</text>
        <text class="value">{{ order.requirement || '-' }}</text>
      </view>
    </view>

    <view v-if="order && order.status === '待派单'" class="section">
      <button class="main-button primary" :loading="actionLoading" @click="handleStart">开始处理</button>
    </view>

    <view v-if="order && order.status === '处理中'" class="section">
      <view class="section-title">现场处理结果</view>
      <view v-if="localHandleImage || handleImage" class="handle-preview-wrap">
        <image
          class="handle-preview"
          :src="fileUrl(localHandleImage || handleImage)"
          mode="aspectFill"
        />
      </view>
      <button class="main-button ghost" @click="chooseHandleImage">拍摄或选择处理后照片</button>
      <button
        class="main-button secondary"
        :loading="actionLoading"
        @click="uploadHandleImage"
      >
        上传处理照片
      </button>
      <view v-if="handleImage" class="upload-success">处理照片已上传</view>
      <textarea
        v-model="form.handleRemark"
        class="remark-input"
        maxlength="1000"
        placeholder="请填写处理说明，例如：已完成该航段漂浮树枝清理"
      />
      <button class="main-button primary" :loading="actionLoading" @click="submitResult">
        提交处理结果
      </button>
    </view>

    <view v-if="order && order.status === '待复核'" class="section notice-section">
      <text class="notice-title">处理结果已提交</text>
      <text class="notice-text">请等待管理员复核。</text>
      <image
        v-if="order.handleImage"
        class="handle-preview"
        :src="fileUrl(order.handleImage)"
        mode="aspectFill"
      />
      <text class="label">处理说明</text>
      <text class="value">{{ order.handleRemark || '-' }}</text>
    </view>

    <view v-if="order && order.status === '已完成'" class="section notice-section done-section">
      <text class="notice-title">工单已完成</text>
      <text class="notice-text">管理员已审核通过。</text>
      <image
        v-if="order.handleImage"
        class="handle-preview"
        :src="fileUrl(order.handleImage)"
        mode="aspectFill"
      />
      <text class="label">处理说明</text>
      <text class="value">{{ order.handleRemark || '-' }}</text>
    </view>
  </view>
</template>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  padding: 14px;
  background: #eef5f7;
}

.loading-tip {
  padding: 32px 0;
  color: #627d98;
  text-align: center;
  font-size: 14px;
}

.section {
  margin-bottom: 14px;
  padding: 16px;
  border-radius: 14px;
  background: #ffffff;
}

.order-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.order-no,
.order-title,
.label,
.value {
  display: block;
}

.order-no {
  color: #627d98;
  font-size: 13px;
}

.order-title {
  margin-top: 6px;
  color: #102a43;
  font-size: 19px;
  font-weight: 700;
}

.status-tag,
.risk-value {
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

.section-title {
  margin-bottom: 12px;
  color: #102a43;
  font-size: 17px;
  font-weight: 700;
}

.hazard-image,
.handle-preview {
  width: 100%;
  height: 210px;
  border-radius: 10px;
  background: #d9e2ec;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 14px;
}

.info-item,
.info-block {
  margin-bottom: 12px;
}

.label {
  margin-bottom: 6px;
  color: #829ab1;
  font-size: 12px;
}

.value {
  color: #334e68;
  font-size: 14px;
  line-height: 1.55;
  word-break: break-all;
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

.handle-preview-wrap {
  overflow: hidden;
  margin-bottom: 10px;
}

.main-button {
  height: 48px;
  margin-bottom: 10px;
  border: 0;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
}

.main-button::after {
  border: 0;
}

.main-button.primary {
  color: #ffffff;
  background: #12343b;
}

.main-button.secondary {
  color: #ffffff;
  background: #1f7a8c;
}

.main-button.ghost {
  color: #12343b;
  background: #d9eef2;
}

.remark-input {
  width: 100%;
  min-height: 110px;
  margin: 8px 0 12px;
  padding: 12px;
  border: 1px solid #bcccdc;
  border-radius: 10px;
  background: #f8fafc;
  font-size: 14px;
}

.upload-success {
  margin-bottom: 8px;
  color: #2f855a;
  font-size: 13px;
}

.notice-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notice-title {
  color: #276749;
  font-size: 18px;
  font-weight: 700;
}

.notice-text {
  color: #627d98;
  font-size: 14px;
}

.done-section .notice-title {
  color: #12343b;
}
</style>
