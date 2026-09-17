<script setup>
import { computed, reactive, ref } from 'vue'
import { analyzeImage } from '../../api/ai'
import { uploadImage } from '../../api/file'
import { getBaseUrl } from '../../api/request'
import { requireLogin } from '../../utils/auth'

const imagePath = ref('')
const imageUrl = ref('')
const selectedFile = ref(null)
const uploading = ref(false)
const analyzing = ref(false)
const form = reactive({
  location: '',
  longitude: '108.123456',
  latitude: '22.123456',
})

const aiStatus = computed(() => {
  if (analyzing.value) return '分析中'
  if (imageUrl.value) return '可识别'
  return '待选择图片'
})

function fullImageUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${getBaseUrl()}${url}`
}

function chooseImage() {
  if (!requireLogin()) return
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed', 'original'],
    sourceType: ['camera', 'album'],
    success: (res) => {
      imagePath.value = res.tempFilePaths[0]
      selectedFile.value = res.tempFiles?.[0] || null
      imageUrl.value = ''
    },
  })
}

function validateForm() {
  if (!imagePath.value) {
    uni.showToast({ title: '请先选择图片', icon: 'none' })
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
  if (!imagePath.value) {
    uni.showToast({ title: '请先选择图片', icon: 'none' })
    return null
  }
  uploading.value = true
  try {
    const result = await uploadImage(imagePath.value, selectedFile.value)
    imageUrl.value = result.url
    uni.showToast({ title: '上传成功', icon: 'success' })
    return result
  } finally {
    uploading.value = false
  }
}

async function handleAnalyze() {
  if (!requireLogin() || !validateForm()) return
  if (!imageUrl.value) {
    const uploaded = await handleUpload()
    if (!uploaded) return
  }
  analyzing.value = true
  uni.showLoading({ title: 'AI分析中...' })
  try {
    const aiResult = await analyzeImage(imageUrl.value)
    uni.setStorageSync('pendingReport', {
      location: form.location,
      longitude: form.longitude,
      latitude: form.latitude,
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
      <view class="section-title">巡检图片</view>
      <view v-if="imagePath" class="preview-wrap">
        <image class="preview-image" :src="imagePath" mode="aspectFill" />
      </view>
      <button class="main-button ghost" @click="chooseImage">拍照或选择图片</button>
      <view v-if="imageUrl" class="success-tip">图片已上传：{{ imageUrl }}</view>
      <image v-if="imageUrl" class="server-preview-image" :src="fullImageUrl(imageUrl)" mode="aspectFill" />
    </view>

    <view class="section">
      <view class="section-title">上报信息</view>
      <view class="field">
        <text>位置</text>
        <input v-model="form.location" placeholder="请输入巡检位置" />
      </view>
      <view class="field-row">
        <view class="field compact">
          <text>经度</text>
          <input v-model="form.longitude" type="digit" />
        </view>
        <view class="field compact">
          <text>纬度</text>
          <input v-model="form.latitude" type="digit" />
        </view>
      </view>
    </view>

    <view class="ai-status">
      <text>AI状态：{{ aiStatus }}</text>
    </view>

    <view class="bottom-actions">
      <button class="main-button secondary" :loading="uploading" @click="handleUpload">上传图片</button>
      <button class="main-button primary" :loading="analyzing" @click="handleAnalyze">开始AI识别</button>
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
  box-shadow: 0 8px 20px rgba(16, 42, 67, 0.06);
}

.ai-status {
  margin-bottom: 14px;
  padding: 12px 16px;
  border-left: 3px solid #1f7a8c;
  border-radius: 10px;
  background: #f0f9fb;
  color: #1f7a8c;
  font-size: 13px;
  font-weight: 600;
}

.section-title {
  margin-bottom: 12px;
  color: #102a43;
  font-size: 17px;
  font-weight: 700;
}

.preview-wrap,
.server-preview-image {
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

.success-tip {
  margin-top: 10px;
  color: #2f855a;
  font-size: 13px;
  word-break: break-all;
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
  transition: transform 0.12s ease, opacity 0.12s ease;
}

.main-button:active {
  transform: scale(0.98);
  opacity: 0.9;
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

.bottom-actions {
  display: flex;
  gap: 10px;
  padding-bottom: 20px;
}

.bottom-actions .main-button {
  flex: 1;
}
</style>
