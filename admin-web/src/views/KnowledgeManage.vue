<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import {
  deleteKnowledgeDocument,
  getKnowledgeDocuments,
  importDemoKnowledgeDocuments,
  reparseKnowledgeDocument,
  uploadKnowledgeDocument,
} from '../api/knowledge'

const documents = ref([])
const loading = ref(false)
const actionLoading = ref(false)
const uploadName = ref('')

function statusTagType(status) {
  if (status === '可用') return 'success'
  if (status === '解析中' || status === '上传中') return 'warning'
  if (status === '解析失败') return 'danger'
  return 'info'
}

function formatTime(value) {
  return value ? String(value).replace('T', ' ').slice(0, 19) : '-'
}

async function loadDocuments() {
  loading.value = true
  try {
    documents.value = (await getKnowledgeDocuments()) || []
  } finally {
    loading.value = false
  }
}

async function handleUpload(options) {
  actionLoading.value = true
  try {
    await uploadKnowledgeDocument(options.file, uploadName.value)
    ElMessage.success('知识文档上传并解析完成')
    uploadName.value = ''
    options.onSuccess()
    await loadDocuments()
  } catch (error) {
    options.onError(error)
  } finally {
    actionLoading.value = false
  }
}

async function handleImportDemo() {
  actionLoading.value = true
  try {
    await importDemoKnowledgeDocuments()
    ElMessage.success('课程演示知识文档导入完成')
    await loadDocuments()
  } finally {
    actionLoading.value = false
  }
}

async function handleReparse(row) {
  actionLoading.value = true
  try {
    await reparseKnowledgeDocument(row.id)
    ElMessage.success('重新解析完成')
    await loadDocuments()
  } finally {
    actionLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除知识文档《${row.documentName}》？`, '删除文档', { type: 'warning' })
  actionLoading.value = true
  try {
    await deleteKnowledgeDocument(row.id)
    ElMessage.success('知识文档已删除')
    await loadDocuments()
  } finally {
    actionLoading.value = false
  }
}

onMounted(loadDocuments)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <div>
        <h2>知识库管理</h2>
        <p>上传课程演示资料，系统会解析文本、切片并生成Embedding</p>
      </div>
      <div class="toolbar-actions">
        <el-button @click="loadDocuments">刷新</el-button>
        <el-button type="success" :loading="actionLoading" @click="handleImportDemo">
          导入演示资料
        </el-button>
      </div>
    </div>

    <section class="panel upload-panel">
      <div class="panel-title">上传文档</div>
      <div class="upload-row">
        <el-input
          v-model="uploadName"
          class="upload-name"
          placeholder="文档名称，可留空使用文件名"
          maxlength="120"
        />
        <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          accept=".txt,.md,.markdown,.pdf"
        >
          <el-button type="primary" :loading="actionLoading">选择并上传</el-button>
        </el-upload>
      </div>
      <p class="upload-tip">支持 TXT、Markdown、PDF。Word 可在后续阶段扩展。</p>
    </section>

    <section class="panel">
      <el-table v-loading="loading" :data="documents" row-key="id" stripe height="calc(100vh - 330px)">
        <el-table-column prop="documentName" label="文档名称" min-width="220" show-overflow-tooltip />
        <el-table-column prop="fileName" label="文件名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="fileType" label="类型" width="90" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="chunkCount" label="片段数" width="100" />
        <el-table-column label="更新时间" width="180">
          <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="primary" link :loading="actionLoading" @click="handleReparse(row)">
              重新解析
            </el-button>
            <el-button type="danger" link :loading="actionLoading" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<style scoped>
.toolbar-actions,
.upload-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.upload-panel {
  padding-bottom: 18px;
}

.upload-row {
  padding: 16px 18px 0;
}

.upload-name {
  max-width: 360px;
}

.upload-tip {
  margin: 10px 18px 0;
  color: var(--text-muted);
  font-size: 13px;
  line-height: 1.6;
}
</style>
