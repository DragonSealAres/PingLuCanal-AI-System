<script setup>
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { approveHazard, getHazardDetail } from '../api/hazard'
import { getBusinessLogs } from '../api/operationLog'
import { getBaseUrl } from '../api/request'
import { getWorkers } from '../api/user'
import { createWorkOrder } from '../api/workOrder'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const actionLoading = ref(false)
const hazard = ref(null)
const logs = ref([])
const workers = ref([])
const approveDialogVisible = ref(false)
const orderDialogVisible = ref(false)
const approveFormRef = ref(null)
const orderFormRef = ref(null)

const hazardTypes = ['漂浮物', '航道障碍物', '船舶异常', '航标异常', '水面污染', '岸线异常', '非法占道', '其他']
const riskLevels = ['低风险', '中风险', '高风险']
const approveForm = reactive({
  hazardType: '',
  riskLevel: '',
  description: '',
})
const orderForm = reactive({
  title: '',
  handlerId: null,
  requirement: '',
})

const approveRules = {
  hazardType: [{ required: true, message: '请选择隐患类型', trigger: 'change' }],
  riskLevel: [{ required: true, message: '请选择风险等级', trigger: 'change' }],
  description: [{ required: true, message: '请输入隐患描述', trigger: 'blur' }],
}
const orderRules = {
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  handlerId: [{ required: true, message: '请选择处置人员', trigger: 'change' }],
}

const aiInfo = computed(() => {
  const raw = hazard.value?.aiResult
  if (!raw) return null
  if (typeof raw === 'object') return raw
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
})

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
  return value ? value.replace('T', ' ').slice(0, 19) : '-'
}

function openApproveDialog() {
  approveForm.hazardType = hazard.value?.hazardType || aiInfo.value?.hazardType || '其他'
  approveForm.riskLevel = hazard.value?.riskLevel || aiInfo.value?.riskLevel || '低风险'
  approveForm.description = hazard.value?.description || aiInfo.value?.description || ''
  approveDialogVisible.value = true
}

async function submitApprove() {
  await approveFormRef.value.validate()
  actionLoading.value = true
  try {
    hazard.value = await approveHazard(route.params.id, { ...approveForm })
    approveDialogVisible.value = false
    ElMessage.success('隐患审核成功')
    await loadLogs()
  } finally {
    actionLoading.value = false
  }
}

async function openOrderDialog() {
  if (!workers.value.length) {
    workers.value = (await getWorkers()) || []
  }
  orderForm.title = `平陆运河${hazard.value?.hazardType || '隐患'}处置`
  orderForm.handlerId = workers.value[0]?.id || null
  orderForm.requirement = `请及时到 ${hazard.value?.location || '现场'} 处置，并上传处理结果。`
  orderDialogVisible.value = true
}

async function submitOrder() {
  await orderFormRef.value.validate()
  actionLoading.value = true
  try {
    const result = await createWorkOrder({
      hazardId: Number(route.params.id),
      ...orderForm,
    })
    hazard.value.status = '处理中'
    orderDialogVisible.value = false
    ElMessage.success(`工单生成成功：${result.orderNo}`)
    await loadLogs()
  } finally {
    actionLoading.value = false
  }
}

async function loadLogs() {
  logs.value = (await getBusinessLogs('HAZARD', Number(route.params.id))) || []
}

async function loadDetail() {
  loading.value = true
  try {
    const [detail] = await Promise.all([
      getHazardDetail(route.params.id),
      loadLogs(),
    ])
    hazard.value = detail
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <div v-loading="loading" class="page">
    <div class="toolbar">
      <div>
        <h2>隐患详情</h2>
        <p>{{ hazard?.reportNo || '-' }}</p>
      </div>
      <div class="toolbar-actions">
        <el-button v-if="hazard?.status === '待审核'" type="primary" @click="openApproveDialog">审核隐患</el-button>
        <el-button v-if="hazard?.status === '已审核'" type="success" @click="openOrderDialog">生成处置工单</el-button>
        <el-button @click="router.push('/hazards')">返回</el-button>
      </div>
    </div>

    <section v-if="hazard" class="detail-grid">
      <div class="panel image-panel">
        <el-image
          v-if="hazard.imageUrl"
          class="detail-image"
          :src="fileUrl(hazard.imageUrl)"
          :preview-src-list="[fileUrl(hazard.imageUrl)]"
          preview-teleported
          fit="cover"
        />
      </div>

      <div class="panel">
        <div class="panel-title">基本信息</div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="位置">{{ hazard.location }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">{{ hazard.status }}</el-descriptions-item>
          <el-descriptions-item label="经度">{{ hazard.longitude }}</el-descriptions-item>
          <el-descriptions-item label="纬度">{{ hazard.latitude }}</el-descriptions-item>
          <el-descriptions-item label="隐患类型">{{ hazard.hazardType }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">
            <el-tag :type="riskTagType(hazard.riskLevel)">{{ hazard.riskLevel }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="上报人">{{ hazard.reportUser }}</el-descriptions-item>
          <el-descriptions-item label="上报时间">{{ formatTime(hazard.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="首次审核人">{{ hazard.auditUserName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ formatTime(hazard.auditTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </section>

    <section v-if="hazard" class="panel detail-section">
      <div class="panel-title">AI分析</div>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="AI描述">{{ aiInfo?.description || hazard.description }}</el-descriptions-item>
        <el-descriptions-item label="AI处置建议">{{ aiInfo?.suggestion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="置信度">{{ aiInfo?.confidence ?? '-' }}</el-descriptions-item>
      </el-descriptions>
    </section>

    <section class="panel detail-section">
      <div class="panel-title">处置轨迹</div>
      <el-timeline class="trace-list">
        <el-timeline-item
          v-for="item in logs"
          :key="item.id"
          :timestamp="formatTime(item.createTime)"
          placement="top"
        >
          {{ item.operatorName }} {{ item.action }} {{ item.remark || '' }}
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="!logs.length" description="暂无轨迹" />
    </section>

    <el-dialog v-model="approveDialogVisible" title="审核隐患" width="560px">
      <el-form ref="approveFormRef" :model="approveForm" :rules="approveRules" label-width="90px">
        <el-form-item label="隐患类型" prop="hazardType">
          <el-select v-model="approveForm.hazardType" style="width: 100%">
            <el-option v-for="item in hazardTypes" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="approveForm.riskLevel" style="width: 100%">
            <el-option v-for="item in riskLevels" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="隐患描述" prop="description">
          <el-input v-model="approveForm.description" type="textarea" :rows="4" maxlength="1000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitApprove">确认审核</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="orderDialogVisible" title="生成处置工单" width="560px">
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="90px">
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="orderForm.title" maxlength="200" />
        </el-form-item>
        <el-form-item label="处置人员" prop="handlerId">
          <el-select v-model="orderForm.handlerId" style="width: 100%">
            <el-option
              v-for="worker in workers"
              :key="worker.id"
              :label="`${worker.realName}（${worker.username}）`"
              :value="worker.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处置要求">
          <el-input v-model="orderForm.requirement" type="textarea" :rows="4" maxlength="1000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitOrder">生成工单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.trace-list {
  padding: 18px 24px 4px;
}
</style>
