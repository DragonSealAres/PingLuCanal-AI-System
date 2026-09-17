<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { createHazard } from '../../api/hazard'
import { getBaseUrl } from '../../api/request'
import { requireLogin } from '../../utils/auth'

const hazardTypes = ['漂浮物', '航道障碍物', '船舶异常', '航标异常', '水面污染', '岸线异常', '非法占道', '其他']
const riskLevels = ['低风险', '中风险', '高风险']
const pending = ref(null)
const submitting = ref(false)
const form = reactive({
  hazardType: '其他',
  riskLevel: '低风险',
  confidence: 0,
  description: '',
  suggestion: '',
})

const aiAnalysis = computed(() => pending.value?.aiResult || {})
const confidencePercent = computed(() => {
  const value = Number(aiAnalysis.value.confidence || 0)
  return Math.max(0, Math.min(100, Math.round(value * 100)))
})
const hasHazard = computed(() => aiAnalysis.value.hasHazard ?? aiAnalysis.value.hazard ?? false)
const hazardTypeIndex = computed(() => Math.max(0, hazardTypes.indexOf(form.hazardType)))
const riskLevelIndex = computed(() => Math.max(0, riskLevels.indexOf(form.riskLevel)))

function fullImageUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${getBaseUrl()}${url}`
}

function onHazardTypeChange(event) {
  form.hazardType = hazardTypes[event.detail.value]
}

function onRiskLevelChange(event) {
  form.riskLevel = riskLevels[event.detail.value]
}

function validate() {
  if (!pending.value?.imageUrl) {
    uni.showToast({ title: '缺少图片信息，请重新上报', icon: 'none' })
    return false
  }
  if (!form.description.trim()) {
    uni.showToast({ title: '请填写隐患描述', icon: 'none' })
    return false
  }
  return true
}

async function submitReport() {
  if (!requireLogin() || !validate()) return

  submitting.value = true
  try {
    const aiResult = {
      hasHazard: pending.value.aiResult?.hasHazard ?? true,
      hazardType: form.hazardType,
      riskLevel: form.riskLevel,
      confidence: Number(form.confidence || 0),
      description: form.description,
      suggestion: form.suggestion,
    }
    const result = await createHazard({
      location: pending.value.location,
      longitude: Number(pending.value.longitude),
      latitude: Number(pending.value.latitude),
      imageUrl: pending.value.imageUrl,
      hazardType: form.hazardType,
      riskLevel: form.riskLevel,
      description: form.description,
      aiResult: JSON.stringify(aiResult),
    })

    uni.removeStorageSync('pendingReport')
    uni.showModal({
      title: '上报成功',
      content: `隐患编号：${result.reportNo}`,
      showCancel: true,
      cancelText: '返回首页',
      confirmText: '我的上报',
      success: (res) => {
        if (res.confirm) {
          uni.redirectTo({ url: '/pages/my-reports/my-reports' })
        } else {
          uni.reLaunch({ url: '/pages/index/index' })
        }
      },
    })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!requireLogin()) return
  const data = uni.getStorageSync('pendingReport')
  if (!data) {
    uni.showToast({ title: '请先完成图片识别', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 800)
    return
  }
  pending.value = data
  form.hazardType = data.aiResult?.hazardType || '其他'
  form.riskLevel = data.aiResult?.riskLevel || '低风险'
  form.confidence = data.aiResult?.confidence || 0
  form.description = data.aiResult?.description || ''
  form.suggestion = data.aiResult?.suggestion || ''
})
</script>

<template>
  <view class="page confirm-page">
    <view v-if="pending" class="section">
      <view class="ai-result-header">
        <view>
          <text class="ai-result-title">AI图片识别完成</text>
          <text class="ai-result-subtitle">请人工确认后提交</text>
        </view>
        <text class="ai-result-status">置信度 {{ confidencePercent }}%</text>
      </view>
      <image class="report-image" :src="fullImageUrl(pending.imageUrl)" mode="aspectFill" />
      <view class="ai-insight">
        <text class="insight-label">AI判断</text>
        <text class="insight-text">{{ hasHazard ? '存在隐患' : '未发现明显隐患' }}</text>
        <text class="insight-label">AI描述</text>
        <text class="insight-text">{{ aiAnalysis.description || '-' }}</text>
        <text class="insight-label">AI建议</text>
        <text class="insight-text">{{ aiAnalysis.suggestion || '-' }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-title">人工确认结果</view>
      <view class="field">
        <text>隐患类型</text>
        <picker :range="hazardTypes" :value="hazardTypeIndex" @change="onHazardTypeChange">
          <view class="picker-value">{{ form.hazardType }}</view>
        </picker>
      </view>
      <view class="field">
        <text>风险等级</text>
        <picker :range="riskLevels" :value="riskLevelIndex" @change="onRiskLevelChange">
          <view class="picker-value">{{ form.riskLevel }}</view>
        </picker>
      </view>
      <view class="field">
        <text>隐患描述</text>
        <textarea v-model="form.description" maxlength="1000" placeholder="请输入隐患描述" />
      </view>
      <view class="field">
        <text>处置建议</text>
        <textarea v-model="form.suggestion" maxlength="1000" placeholder="请输入处置建议" />
      </view>
    </view>

    <button class="main-button primary" :loading="submitting" @click="submitReport">确认并上报</button>
  </view>
</template>

<style scoped>
.confirm-page {
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

.section-title,
.ai-result-title {
  display: block;
  color: #102a43;
  font-size: 17px;
  font-weight: 700;
}

.ai-result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 14px;
}

.ai-result-subtitle {
  display: block;
  margin-top: 4px;
  color: #627d98;
  font-size: 12px;
}

.ai-result-status {
  flex: 0 0 auto;
  padding: 4px 8px;
  border-radius: 999px;
  color: #276749;
  background: #dff6e8;
  font-size: 11px;
}

.report-image {
  width: 100%;
  height: 210px;
  border-radius: 12px;
  background: #d9e2ec;
}

.ai-insight {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-top: 14px;
  padding: 12px;
  border-left: 3px solid #1f7a8c;
  background: #f4fafb;
}

.insight-label {
  color: #1f7a8c;
  font-size: 12px;
  font-weight: 700;
}

.insight-text {
  margin-bottom: 5px;
  color: #334e68;
  font-size: 13px;
  line-height: 1.5;
}

.field {
  margin-top: 12px;
}

.field text {
  display: block;
  margin-bottom: 7px;
  color: #486581;
  font-size: 13px;
}

.picker-value,
.field textarea {
  min-height: 44px;
  padding: 12px;
  border: 1px solid #bcccdc;
  border-radius: 10px;
  background: #f8fafc;
}

.field textarea {
  width: 100%;
  height: 92px;
}

.main-button {
  height: 50px;
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
</style>
