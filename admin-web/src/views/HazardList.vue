<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getHazards } from '../api/hazard'

const router = useRouter()
const loading = ref(false)
const hazards = ref([])
const backendBaseUrl = 'http://localhost:8080'

function fileUrl(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : `${backendBaseUrl}${url}`
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

async function loadHazards() {
  loading.value = true
  try {
    hazards.value = (await getHazards()) || []
  } finally {
    loading.value = false
  }
}

onMounted(loadHazards)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <div>
        <h2>隐患管理</h2>
        <p>共 {{ hazards.length }} 条隐患记录</p>
      </div>
      <el-button type="primary" @click="loadHazards">刷新</el-button>
    </div>

    <section class="panel">
      <el-table v-loading="loading" :data="hazards" row-key="id" height="calc(100vh - 238px)">
        <el-table-column prop="reportNo" label="隐患编号" min-width="170" />
        <el-table-column label="图片" width="96">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              class="table-image"
              :src="fileUrl(row.imageUrl)"
              :preview-src-list="[fileUrl(row.imageUrl)]"
              preview-teleported
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="hazardType" label="隐患类型" width="120" />
        <el-table-column label="风险等级" width="120">
          <template #default="{ row }">
            <el-tag :type="riskTagType(row.riskLevel)" effect="light">{{ row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="隐患描述" min-width="240" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="reportUser" label="上报人" width="130" />
        <el-table-column label="上报时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="router.push(`/hazards/${row.id}`)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>
