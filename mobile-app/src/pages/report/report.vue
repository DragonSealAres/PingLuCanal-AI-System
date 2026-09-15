<script setup>
import { computed, reactive, ref } from 'vue'
import { analyzeImage } from '../../api/ai'
import { uploadImage } from '../../api/file'
import { getBaseUrl } from '../../api/request'

const imagePath = ref('')
const imageUrl = ref('')
const selectedFile = ref(null)
const uploadError = ref('')
const uploading = ref(false)
const analyzing = ref(false)

const aiStatus = computed(() => {
  if (analyzing.value) return 'analyzing'
  if (imageUrl.value) return 'ready'
  return 'waiting'
})

const form = reactive({
  reportUser: '巡检员01',
  location: '',
  longitude: '108.123456',
  latitude: '22.123456',
})

function fullImageUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${getBaseUrl()}${url}`
}

function chooseImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed', 'original'],
    sourceType: ['camera', 'album'],
    success: (res) => {
      imagePath.value = res.tempFilePaths[0]
      selectedFile.value = res.tempFiles?.[0] || null
      imageUrl.value = ''
      uploadError.value = ''
    },
    fail: () => {
      uni.showToast({ title: '没有选择图片', icon: 'none' })
    },
  })
}

function formatFileSize(size) {
  if (!size) return ''
  if (size < 1024 * 1024) {
    return `${Math.ceil(size / 1024)}KB`
  }
  return `${(size / 1024 / 1024).toFixed(2)}MB`
}

function validateImageSelected() {
  if (!imagePath.value) {
    uni.showToast({ title: '请先拍照或选择图片', icon: 'none' })
    return false
  }
  return true
}

function validateReportForm() {
  if (!form.reportUser.trim()) {
    uni.showToast({ title: '请填写上报人', icon: 'none' })
    return false
  }
  if (!form.location.trim()) {
    uni.showToast({ title: '请填写位置', icon: 'none' })
    return false
  }
  if (!form.longitude || !form.latitude) {
    uni.showToast({ title: '请填写经纬度', icon: 'none' })
    return false
  }
  return true
}

async function handleUpload() {
  if (!validateImageSelected()) return

  uploading.value = true
  uploadError.value = ''
  try {
    const result = await uploadImage(imagePath.value, selectedFile.value)
    imageUrl.value = result.url
    uni.showToast({ title: '图片上传成功', icon: 'success' })
    return result
  } catch (error) {
    uploadError.value = error?.message || '图片上传失败'
    throw error
  } finally {
    uploading.value = false
  }
}

async function handleAnalyze() {
  if (!validateImageSelected() || !validateReportForm()) return
  if (!imageUrl.value) {
    try {
      await handleUpload()
    } catch {
      return
    }
  }
  if (!imageUrl.value) return

  analyzing.value = true
  uni.showLoading({ title: 'AI正在分析航道隐患...' })
  try {
    const aiResult = await analyzeImage(imageUrl.value)
    uni.setStorageSync('pendingReport', {
      ...form,
      imageUrl: imageUrl.value,
      localImagePath: imagePath.value,
      aiResult,
    })
    uni.navigateTo({ url: '/pages/confirm/confirm' })
  } finally {
    analyzing.value = false
    uni.hideLoading()
  }
}
</script>

<template>
  <view class="page report-page">
    <view class="section">
      <view class="section-heading">
        <view>
          <view class="section-title">巡检图片</view>
          <view class="section-subtitle">拍照或选择现场图片，上传后由 AI 辅助识别</view>
        </view>
        <view class="step-badge">1 / 4</view>
      </view>
      <view v-if="imagePath" class="preview-wrap">
        <image class="preview-image" :src="imagePath" mode="aspectFill" />
      </view>
      <view v-if="selectedFile" class="file-meta">
        <text>{{ selectedFile.name || '已选择图片' }}</text>
        <text>{{ formatFileSize(selectedFile.size) }}</text>
      </view>
      <button class="main-button ghost" @click="chooseImage">拍照或选择图片</button>
      <view v-if="imageUrl" class="success-tip">
        <text class="success-icon">✓</text>
        <text>图片已上传，服务器地址：{{ imageUrl }}</text>
      </view>
      <view v-if="imageUrl" class="server-preview-wrap">
        <image class="server-preview-image" :src="fullImageUrl(imageUrl)" mode="aspectFill" />
      </view>
      <view v-if="uploadError" class="error-tip">{{ uploadError }}</view>
    </view>

    <view class="ai-flow">
      <view class="flow-line">
        <view class="flow-step active">
          <view class="flow-number">1</view>
          <text>选择图片</text>
        </view>
        <view class="flow-connector" :class="{ done: imageUrl }" />
        <view class="flow-step" :class="{ active: imageUrl }">
          <view class="flow-number">2</view>
          <text>上传图片</text>
        </view>
        <view class="flow-connector" :class="{ done: aiStatus === 'analyzing' || aiStatus === 'ready' }" />
        <view class="flow-step" :class="{ active: aiStatus === 'analyzing' || aiStatus === 'ready' }">
          <view class="flow-number">3</view>
          <text>AI识别</text>
        </view>
        <view class="flow-connector" />
        <view class="flow-step">
          <view class="flow-number">4</view>
          <text>确认上报</text>
        </view>
      </view>
    </view>

    <view class="ai-status" :class="`ai-status-${aiStatus}`">
      <view class="ai-status-mark">AI</view>
      <view class="ai-status-content">
        <text v-if="aiStatus === 'waiting'" class="ai-status-title">AI识别待开始</text>
        <text v-else-if="aiStatus === 'ready'" class="ai-status-title">图片已准备好，可开始 AI 识别</text>
        <text v-else class="ai-status-title">AI正在分析航道隐患...</text>
        <text v-if="aiStatus === 'waiting'" class="ai-status-desc">请先选择巡检图片</text>
        <text v-else-if="aiStatus === 'ready'" class="ai-status-desc">系统将提取隐患类型、风险等级、描述和处置建议</text>
        <text v-else class="ai-status-desc">正在调用多模态图片分析服务，请稍候</text>
      </view>
      <view v-if="aiStatus === 'analyzing'" class="ai-spinner" />
      <text v-else-if="aiStatus === 'ready'" class="ai-status-action">下一步</text>
    </view>

    <view class="section">
      <view class="section-heading">
        <view class="section-title">上报信息</view>
        <view class="step-badge">填写</view>
      </view>
      <view class="field">
        <text>上报人</text>
        <input v-model="form.reportUser" placeholder="请输入上报人" />
      </view>
      <view class="field">
        <text>位置</text>
        <input v-model="form.location" placeholder="请输入巡检位置" />
      </view>
      <view class="field-row">
        <view class="field compact">
          <text>经度</text>
          <input v-model="form.longitude" type="digit" placeholder="经度" />
        </view>
        <view class="field compact">
          <text>纬度</text>
          <input v-model="form.latitude" type="digit" placeholder="纬度" />
        </view>
      </view>
    </view>

    <view class="bottom-actions">
      <button class="main-button secondary" :loading="uploading" @click="handleUpload">上传图片</button>
      <button class="main-button primary" :loading="analyzing" @click="handleAnalyze">
        {{ analyzing ? 'AI分析中...' : '开始AI识别' }}
      </button>
    </view>
  </view>
</template>

<style scoped>
.report-page {
  min-height: 100vh;
  padding: 14px;
  background: #eef5f7;
}

.section {
  margin-bottom: 14px;
  padding: 16px;
  border-radius: 14px;
  background: #ffffff;
}

.section-title {
  font-size: 17px;
  font-weight: 700;
  color: #102a43;
}

.section-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 12px;
}

.section-subtitle {
  margin-top: 5px;
  color: #829ab1;
  font-size: 12px;
  line-height: 1.4;
}

.step-badge {
  flex: 0 0 auto;
  padding: 4px 8px;
  border-radius: 999px;
  color: #1f7a8c;
  background: #e3f4f6;
  font-size: 11px;
}

.preview-wrap {
  overflow: hidden;
  width: 100%;
  height: 210px;
  margin-bottom: 12px;
  border-radius: 12px;
  background: #d9e2ec;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.server-preview-wrap {
  overflow: hidden;
  width: 100%;
  height: 160px;
  margin-top: 10px;
  border-radius: 10px;
  background: #d9e2ec;
}

.server-preview-image {
  width: 100%;
  height: 100%;
}

.file-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin: 8px 0 12px;
  color: #627d98;
  font-size: 12px;
}

.field {
  margin-bottom: 12px;
}

.field text {
  display: block;
  margin-bottom: 7px;
  color: #486581;
  font-size: 13px;
}

.field input {
  height: 44px;
  padding: 0 12px;
  border: 1px solid #bcccdc;
  border-radius: 10px;
  background: #f8fafc;
}

.field-row {
  display: flex;
  gap: 10px;
}

.compact {
  flex: 1;
}

.main-button {
  height: 48px;
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

.success-tip {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-top: 10px;
  color: #2f855a;
  font-size: 13px;
  word-break: break-all;
}

.success-icon {
  flex: 0 0 auto;
  font-weight: 700;
}

.error-tip {
  margin-top: 10px;
  color: #c53030;
  font-size: 13px;
}

.ai-flow {
  margin-bottom: 14px;
  padding: 14px 10px;
  border-radius: 14px;
  background: #ffffff;
}

.flow-line {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.flow-step {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  flex-direction: column;
  gap: 5px;
  color: #9fb3c8;
  font-size: 11px;
}

.flow-step.active {
  color: #1f7a8c;
}

.flow-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  color: #829ab1;
  background: #e6eef2;
  font-size: 12px;
  font-weight: 700;
}

.flow-step.active .flow-number {
  color: #ffffff;
  background: #1f7a8c;
}

.flow-connector {
  flex: 1;
  height: 2px;
  margin: 11px 4px 0;
  background: #e6eef2;
}

.flow-connector.done {
  background: #7cc8d1;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
  padding: 14px;
  border: 1px solid #bce3e8;
  border-radius: 14px;
  background: #effbfc;
}

.ai-status-analyzing {
  border-color: #f3d28b;
  background: #fffaf0;
}

.ai-status-mark {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  color: #ffffff;
  background: #1f7a8c;
  font-size: 13px;
  font-weight: 800;
}

.ai-status-analyzing .ai-status-mark {
  background: #c27c0e;
}

.ai-status-content {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 4px;
}

.ai-status-title {
  color: #12343b;
  font-size: 15px;
  font-weight: 700;
}

.ai-status-desc {
  color: #627d98;
  font-size: 12px;
  line-height: 1.45;
}

.ai-status-action {
  flex: 0 0 auto;
  color: #1f7a8c;
  font-size: 12px;
}

.ai-spinner {
  flex: 0 0 auto;
  width: 18px;
  height: 18px;
  border: 2px solid #f1dcae;
  border-top-color: #c27c0e;
  border-radius: 50%;
  animation: spin 0.9s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.bottom-actions {
  display: flex;
  gap: 10px;
  padding-bottom: 20px;
}

.bottom-actions .main-button {
  flex: 1;
}
</style>
