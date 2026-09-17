<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { reviewImages } from '../api/ai'
import { getHazards } from '../api/hazard'
import { getBusinessLogs } from '../api/operationLog'
import { getBaseUrl } from '../api/request'
import { approveWorkOrder, getWorkOrders } from '../api/workOrder'

const loading = ref(false)
const actionLoading = ref(false)
const reviewLoading = ref(false)
const workOrders = ref([])
const hazards = ref([])
const currentOrder = ref(null)
const currentHazard = ref(null)
const detailDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const reviewResult = ref(null)
const logs = ref([])

function statusTagType(status) {
  if (status === '待派单' || status === '待处理') return 'info'
  if (status === '处理中') return 'warning'
  if (status === '待复核') return 'primary'
  if (status === '已完成') return 'success'
  return 'info'
}

function riskTagType(level) {
  if (level === '高风险') return 'danger'
  if (level === '中风险') return 'warning'
  if (level === '低风险') return 'success'
  return 'info'
}

function formatTime(value) {
  return value ? value.replace('T', ' ').slice(0, 19) : '-'
}

function fileUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${getBaseUrl()}${url}`
}

function statusLabel(status) {
  return status === '待派单' ? '待处理' : status || '-'
}

function hazardFor(order) {
  return hazards.value.find((item) => item.id === order.hazardId)
}

async function loadWorkOrders() {
  loading.value = true
  try {
    const [orders, hazardList] = await Promise.all([getWorkOrders(), getHazards()])
    workOrders.value = orders || []
    hazards.value = hazardList || []
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  currentOrder.value = row
  currentHazard.value = hazardFor(row)
  logs.value = (await getBusinessLogs('WORK_ORDER', row.id)) || []
  detailDialogVisible.value = true
}

async function handleApprove(row) {
  await ElMessageBox.confirm(`确认最终复核工单 ${row.orderNo}？`, '最终复核', { type: 'warning' })
  actionLoading.value = true
  try {
    await approveWorkOrder(row.id)
    ElMessage.success('工单已完成')
    await loadWorkOrders()
  } finally {
    actionLoading.value = false
  }
}

async function handleAiReview(row) {
  const hazard = hazardFor(row)
  if (!hazard?.imageUrl || !row.handleImage) {
    ElMessage.error('处理前和处理后照片均存在后才能进行AI复核')
    return
  }

  reviewLoading.value = true
  reviewResult.value = null
  currentOrder.value = row
  currentHazard.value = hazard
  try {
    reviewResult.value = await reviewImages({
      beforeImageUrl: hazard.imageUrl,
      afterImageUrl: row.handleImage,
      hazardType: hazard.hazardType,
      riskLevel: hazard.riskLevel,
      handleRemark: row.handleRemark,
    })
    reviewDialogVisible.value = true
  } finally {
    reviewLoading.value = false
  }
}

onMounted(loadWorkOrders)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <div>
        <h2>工单管理</h2>
        <p>共 {{ workOrders.length }} 条工单记录</p>
      </div>
      <el-button type="primary" @click="loadWorkOrders">刷新</el-button>
    </div>

    <section class="panel">
      <el-table v-loading="loading" :data="workOrders" row-key="id" stripe height="calc(100vh - 238px)">
        <el-table-column prop="orderNo" label="工单编号" min-width="190" />
        <el-table-column label="关联隐患" width="130">
          <template #default="{ row }">{{ hazardFor(row)?.reportNo || `#${row.hazardId}` }}</template>
        </el-table-column>
        <el-table-column label="隐患类型" width="120">
          <template #default="{ row }">{{ hazardFor(row)?.hazardType || '-' }}</template>
        </el-table-column>
        <el-table-column label="风险等级" width="110">
          <template #default="{ row }">
            <el-tag v-if="hazardFor(row)" :type="riskTagType(hazardFor(row).riskLevel)">
              {{ hazardFor(row).riskLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="工单标题" min-width="180" show-overflow-tooltip />
        <el-table-column label="处置人员" width="130">
          <template #default="{ row }">{{ row.handlerName || row.handler }}</template>
        </el-table-column>
        <el-table-column label="派单人" width="130">
          <template #default="{ row }">{{ row.assignedByName || '-' }}</template>
        </el-table-column>
        <el-table-column label="最终复核人" width="130">
          <template #default="{ row }">{{ row.reviewUserName || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">查看详情</el-button>
            <el-button
              v-if="row.status === '待复核'"
              type="primary"
              link
              :loading="reviewLoading"
              @click="handleAiReview(row)"
            >
              AI复核
            </el-button>
            <el-button
              v-if="row.status === '待复核'"
              type="success"
              link
              :loading="actionLoading"
              @click="handleApprove(row)"
            >
              复核完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="detailDialogVisible" title="工单详情" width="760px">
      <el-descriptions v-if="currentOrder" :column="2" border>
        <el-descriptions-item label="工单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(currentOrder.status)">{{ statusLabel(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="关联隐患">{{ currentHazard?.reportNo || `#${currentOrder.hazardId}` }}</el-descriptions-item>
        <el-descriptions-item label="处置人员">{{ currentOrder.handlerName || currentOrder.handler }}</el-descriptions-item>
        <el-descriptions-item label="派单人">{{ currentOrder.assignedByName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="派单时间">{{ formatTime(currentOrder.assignedTime) }}</el-descriptions-item>
        <el-descriptions-item label="最终复核人">{{ currentOrder.reviewUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="复核时间">{{ formatTime(currentOrder.reviewTime) }}</el-descriptions-item>
        <el-descriptions-item label="工单标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="处置要求" :span="2">{{ currentOrder.requirement || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处置说明" :span="2">{{ currentOrder.handleRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理前照片">
          <el-image
            v-if="currentHazard?.imageUrl"
            class="detail-image-small"
            :src="fileUrl(currentHazard.imageUrl)"
            :preview-src-list="[fileUrl(currentHazard.imageUrl)]"
            preview-teleported
            fit="cover"
          />
        </el-descriptions-item>
        <el-descriptions-item label="处理后照片">
          <el-image
            v-if="currentOrder.handleImage"
            class="detail-image-small"
            :src="fileUrl(currentOrder.handleImage)"
            :preview-src-list="[fileUrl(currentOrder.handleImage)]"
            preview-teleported
            fit="cover"
          />
        </el-descriptions-item>
      </el-descriptions>

      <div class="trace-title">处置轨迹</div>
      <el-timeline>
        <el-timeline-item v-for="item in logs" :key="item.id" :timestamp="formatTime(item.createTime)">
          {{ item.operatorName }} {{ item.action }} {{ item.remark || '' }}
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="!logs.length" description="暂无轨迹" />
    </el-dialog>

    <el-dialog v-model="reviewDialogVisible" title="AI智能复核结果" width="860px">
      <div v-if="currentOrder && currentHazard && reviewResult" class="review-dialog">
        <div class="review-images">
          <el-image class="review-image" :src="fileUrl(currentHazard.imageUrl)" fit="cover" />
          <el-image class="review-image" :src="fileUrl(currentOrder.handleImage)" fit="cover" />
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="复核状态">
            <el-tag :type="reviewResult.resolved ? 'success' : 'warning'">
              {{ reviewResult.reviewStatus || '-' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="置信度">{{ reviewResult.confidence ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="处理前描述" :span="2">{{ reviewResult.beforeDescription || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处理后描述" :span="2">{{ reviewResult.afterDescription || '-' }}</el-descriptions-item>
          <el-descriptions-item label="剩余风险" :span="2">{{ reviewResult.remainingRisk || '-' }}</el-descriptions-item>
          <el-descriptions-item label="复核建议" :span="2">{{ reviewResult.recommendation || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else description="暂无复核结果" />
      <template #footer>
        <el-button @click="reviewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.review-dialog {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.review-images {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.review-image {
  width: 100%;
  height: 240px;
  border: 1px solid var(--surface-border);
  border-radius: 8px;
  background: #e2f2f6;
}

.detail-image-small {
  width: 180px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid var(--surface-border);
}

.trace-title {
  margin: 18px 0 10px;
  color: var(--text-title);
  font-weight: 700;
}
</style>
