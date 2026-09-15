<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { getHazards } from '../api/hazard'
import { approveWorkOrder, finishWorkOrder, getWorkOrders, startWorkOrder } from '../api/workOrder'

const loading = ref(false)
const actionLoading = ref(false)
const workOrders = ref([])
const hazards = ref([])
const finishDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const currentHazard = ref(null)
const finishFormRef = ref(null)
const finishForm = reactive({
  handleRemark: '',
  handleImage: '',
})

const finishRules = {
  handleRemark: [{ required: true, message: '请输入处理说明', trigger: 'blur' }],
}

function statusTagType(status) {
  if (status === '待派单') return 'info'
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
  return url.startsWith('http') ? url : `http://localhost:8080${url}`
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

function openDetail(row) {
  currentOrder.value = row
  currentHazard.value = hazardFor(row)
  detailDialogVisible.value = true
}

async function handleStart(row) {
  await ElMessageBox.confirm(`确认开始处理工单 ${row.orderNo}？`, '开始处理', { type: 'warning' })
  actionLoading.value = true
  try {
    await startWorkOrder(row.id)
    ElMessage.success('工单已进入处理中')
    await loadWorkOrders()
  } finally {
    actionLoading.value = false
  }
}

function openFinishDialog(row) {
  currentOrder.value = row
  finishForm.handleRemark = ''
  finishForm.handleImage = row.handleImage || ''
  finishDialogVisible.value = true
}

async function submitFinish() {
  await finishFormRef.value.validate()
  actionLoading.value = true
  try {
    await finishWorkOrder(currentOrder.value.id, { ...finishForm })
    ElMessage.success('处理结果已提交')
    finishDialogVisible.value = false
    await loadWorkOrders()
  } finally {
    actionLoading.value = false
  }
}

async function handleApprove(row) {
  await ElMessageBox.confirm(`确认审核完成工单 ${row.orderNo}？`, '审核完成', { type: 'warning' })
  actionLoading.value = true
  try {
    await approveWorkOrder(row.id)
    ElMessage.success('工单已完成')
    await loadWorkOrders()
  } finally {
    actionLoading.value = false
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
      <el-table v-loading="loading" :data="workOrders" row-key="id" height="calc(100vh - 238px)">
        <el-table-column prop="orderNo" label="工单编号" min-width="190" />
        <el-table-column label="关联隐患" width="110">
          <template #default="{ row }">#{{ row.hazardId }}</template>
        </el-table-column>
        <el-table-column label="隐患图片" width="96">
          <template #default="{ row }">
            <el-image
              v-if="hazardFor(row)?.imageUrl"
              class="table-image"
              :src="fileUrl(hazardFor(row).imageUrl)"
              :preview-src-list="[fileUrl(hazardFor(row).imageUrl)]"
              preview-teleported
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="隐患类型" width="110">
          <template #default="{ row }">{{ hazardFor(row)?.hazardType || '-' }}</template>
        </el-table-column>
        <el-table-column label="风险等级" width="110">
          <template #default="{ row }">
            <el-tag v-if="hazardFor(row)" :type="riskTagType(hazardFor(row).riskLevel)">
              {{ hazardFor(row).riskLevel }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="位置" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ hazardFor(row)?.location || '-' }}</template>
        </el-table-column>
        <el-table-column prop="title" label="工单标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="handler" label="处理人员" width="130" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handleRemark" label="处理说明" min-width="240" show-overflow-tooltip />
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="230">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">查看详情</el-button>
            <el-button
              v-if="row.status === '待派单'"
              type="primary"
              link
              :loading="actionLoading"
              @click="handleStart(row)"
            >
              开始处理
            </el-button>
            <el-button
              v-if="row.status === '处理中'"
              type="primary"
              link
              :loading="actionLoading"
              @click="openFinishDialog(row)"
            >
              提交处理结果
            </el-button>
            <el-button
              v-if="row.status === '待复核'"
              type="success"
              link
              :loading="actionLoading"
              @click="handleApprove(row)"
            >
              审核完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="detailDialogVisible" title="工单详情" width="700px">
      <el-descriptions v-if="currentOrder" :column="2" border>
        <el-descriptions-item label="工单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="工单状态">
          <el-tag :type="statusTagType(currentOrder.status)">{{ currentOrder.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="关联隐患">{{ currentHazard?.reportNo || `#${currentOrder.hazardId}` }}</el-descriptions-item>
        <el-descriptions-item label="处置人员">{{ currentOrder.handler }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">
          {{ currentOrder.status === '待复核' || currentOrder.status === '已完成'
            ? formatTime(currentOrder.updateTime)
            : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="工单标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="处置要求" :span="2">{{ currentOrder.requirement || '-' }}</el-descriptions-item>
        <el-descriptions-item label="隐患类型">{{ currentHazard?.hazardType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">{{ currentHazard?.riskLevel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="隐患位置" :span="2">{{ currentHazard?.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理说明" :span="2">{{ currentOrder.handleRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理前照片" :span="1">
          <el-image
            v-if="currentHazard?.imageUrl"
            class="detail-image-small"
            :src="fileUrl(currentHazard.imageUrl)"
            :preview-src-list="[fileUrl(currentHazard.imageUrl)]"
            preview-teleported
            fit="cover"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="处理后照片" :span="1">
          <el-image
            v-if="currentOrder.handleImage"
            class="detail-image-small"
            :src="fileUrl(currentOrder.handleImage)"
            :preview-src-list="[fileUrl(currentOrder.handleImage)]"
            preview-teleported
            fit="cover"
          />
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="finishDialogVisible" title="提交处理结果" width="520px">
      <el-form ref="finishFormRef" :model="finishForm" :rules="finishRules" label-width="110px">
        <el-form-item label="处理说明" prop="handleRemark">
          <el-input v-model="finishForm.handleRemark" type="textarea" :rows="4" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="处理图片">
          <el-input v-model="finishForm.handleImage" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="finishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitFinish">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.detail-image-small {
  width: 180px;
  height: 120px;
  border-radius: 6px;
  border: 1px solid #d9e2ec;
}
</style>
