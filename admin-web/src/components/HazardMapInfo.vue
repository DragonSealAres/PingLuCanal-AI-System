<script setup>
import { ref, watch } from 'vue'
import { getBaseUrl } from '../api/request'

const props = defineProps({
  hazard: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['view-detail'])
const imageFailed = ref(false)

watch(
  () => props.hazard?.imageUrl,
  () => {
    imageFailed.value = false
  },
)

function fileUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${getBaseUrl()}${url}`
}

function riskTagType(level) {
  if (level === '高风险') return 'danger'
  if (level === '中风险') return 'warning'
  if (level === '低风险') return 'success'
  return 'info'
}

function formatTime(value) {
  return value ? String(value).replace('T', ' ').slice(0, 19) : '-'
}
</script>

<template>
  <aside class="panel hazard-side-panel">
    <div class="panel-title">隐患点详情</div>

    <template v-if="hazard">
      <el-image
        v-if="hazard.imageUrl && !imageFailed"
        class="side-image"
        :src="fileUrl(hazard.imageUrl)"
        :preview-src-list="[fileUrl(hazard.imageUrl)]"
        preview-teleported
        fit="cover"
        @error="imageFailed = true"
      />
      <div v-else class="side-image-placeholder">暂无可用图片</div>

      <el-descriptions :column="1" border class="side-descriptions">
        <el-descriptions-item label="隐患编号">{{ hazard.reportNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="隐患类型">{{ hazard.hazardType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">
          <el-tag :type="riskTagType(hazard.riskLevel)">{{ hazard.riskLevel || '-' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="当前位置">{{ hazard.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="隐患描述">{{ hazard.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">{{ hazard.status || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上报时间">{{ formatTime(hazard.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-button class="detail-button" type="primary" @click="emit('view-detail', hazard)">
        查看详情
      </el-button>
    </template>

    <el-empty v-else description="请选择地图隐患点" />
  </aside>
</template>

<style scoped>
.hazard-side-panel {
  align-self: stretch;
  overflow: hidden;
}

.side-image,
.side-image-placeholder {
  width: calc(100% - 36px);
  height: 180px;
  margin: 0 18px 16px;
  border: 1px solid var(--surface-border);
  border-radius: 8px;
  background: linear-gradient(145deg, #eff8fa, #e2f2f6);
  box-shadow: inset 0 1px 2px rgba(16, 42, 67, 0.06);
}

.side-image-placeholder {
  display: grid;
  place-items: center;
  color: #829ab1;
}

.side-descriptions {
  margin: 0 18px;
}

.detail-button {
  width: calc(100% - 36px);
  margin: 16px 18px 18px;
}
</style>
