<script setup>
import { ElMessage } from 'element-plus'
import { nextTick, onMounted, ref } from 'vue'
import { askAssistant, getChatMessages, getChatSessions } from '../api/chat'

const sessions = ref([])
const messages = ref([])
const currentSessionId = ref('')
const question = ref('')
const loading = ref(false)
const sending = ref(false)
const messagesRef = ref(null)
const sourceDialogVisible = ref(false)
const currentSource = ref(null)

function parseSources(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) return raw
  try {
    return JSON.parse(raw)
  } catch {
    return []
  }
}

function formatTime(value) {
  return value ? String(value).replace('T', ' ').slice(0, 19) : ''
}

async function loadSessions() {
  sessions.value = (await getChatSessions()) || []
}

async function openSession(sessionId) {
  currentSessionId.value = sessionId
  const list = (await getChatMessages(sessionId)) || []
  messages.value = list.map((item) => ({
    ...item,
    sources: parseSources(item.sources),
  }))
  await scrollToBottom()
}

function newSession() {
  currentSessionId.value = ''
  messages.value = []
}

async function sendQuestion() {
  const cleanQuestion = question.value.trim()
  if (!cleanQuestion) {
    ElMessage.warning('请输入问题')
    return
  }

  sending.value = true
  messages.value.push({
    role: 'user',
    content: cleanQuestion,
    createTime: new Date().toISOString(),
    sources: [],
  })
  question.value = ''
  await scrollToBottom()

  try {
    const result = await askAssistant({
      sessionId: currentSessionId.value,
      userName: '管理员',
      question: cleanQuestion,
    })
    currentSessionId.value = result.sessionId
    messages.value.push({
      role: 'assistant',
      content: result.answer,
      createTime: new Date().toISOString(),
      sources: result.sources || [],
    })
    await loadSessions()
    await scrollToBottom()
  } finally {
    sending.value = false
  }
}

async function scrollToBottom() {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

function showSource(source) {
  currentSource.value = source
  sourceDialogVisible.value = true
}

onMounted(async () => {
  loading.value = true
  try {
    await loadSessions()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div v-loading="loading" class="page ai-assistant-page">
    <div class="toolbar">
      <div>
        <h2>AI航运服务问答助手</h2>
        <p>优先基于本地知识库资料回答，并展示参考来源</p>
      </div>
      <el-button type="primary" @click="newSession">新建会话</el-button>
    </div>

    <section class="assistant-layout">
      <aside class="panel session-panel">
        <div class="panel-title">历史会话</div>
        <div class="session-list">
          <button
            v-for="item in sessions"
            :key="item.sessionId"
            class="session-item"
            :class="{ active: item.sessionId === currentSessionId }"
            @click="openSession(item.sessionId)"
          >
            <span>{{ item.title }}</span>
            <small>{{ formatTime(item.updateTime) }}</small>
          </button>
          <el-empty v-if="sessions.length === 0" description="暂无会话" />
        </div>
      </aside>

      <main class="panel chat-panel">
        <div ref="messagesRef" class="message-list">
          <div
            v-for="(item, index) in messages"
            :key="`${item.role}-${index}`"
            class="message-row"
            :class="item.role"
          >
            <div class="message-avatar">{{ item.role === 'assistant' ? 'AI' : '我' }}</div>
            <div class="message-bubble">
              <div class="message-role">{{ item.role === 'assistant' ? 'AI助手' : '用户' }}</div>
              <div class="message-content">{{ item.content }}</div>
              <div v-if="item.role === 'assistant' && item.sources?.length" class="source-list">
                <div class="source-title">参考来源</div>
                <el-button
                  v-for="source in item.sources"
                  :key="`${source.documentName}-${source.chunkIndex}`"
                  link
                  type="primary"
                  @click="showSource(source)"
                >
                  《{{ source.documentName }}》片段{{ source.chunkIndex }}
                </el-button>
              </div>
            </div>
          </div>
          <el-empty v-if="messages.length === 0" description="请输入航运服务问题" />
        </div>

        <div class="input-bar">
          <el-input
            v-model="question"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="例如：船舶过闸需要注意哪些事项？"
            @keydown.ctrl.enter.prevent="sendQuestion"
          />
          <el-button type="primary" :loading="sending" @click="sendQuestion">发送</el-button>
        </div>
      </main>
    </section>

    <el-dialog v-model="sourceDialogVisible" title="参考来源片段" width="720px">
      <el-descriptions v-if="currentSource" :column="1" border>
        <el-descriptions-item label="来源文档">{{ currentSource.documentName }}</el-descriptions-item>
        <el-descriptions-item label="片段序号">{{ currentSource.chunkIndex }}</el-descriptions-item>
        <el-descriptions-item label="相似度">
          {{ Number(currentSource.score || 0).toFixed(3) }}
        </el-descriptions-item>
        <el-descriptions-item label="片段内容">{{ currentSource.content }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style scoped>
.assistant-layout {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 16px;
  min-height: calc(100vh - 180px);
}

.session-panel,
.chat-panel {
  overflow: hidden;
}

.session-panel {
  display: flex;
  flex-direction: column;
}

.session-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 8px;
  min-height: 0;
  padding: 12px;
  overflow-y: auto;
}

.session-list::-webkit-scrollbar {
  width: 6px;
}

.session-list::-webkit-scrollbar-track {
  background: transparent;
}

.session-list::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(31, 122, 140, 0.18);
}

.session-list::-webkit-scrollbar-thumb:hover {
  background: rgba(31, 122, 140, 0.32);
}

.session-item {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
  padding: 12px 12px 12px 15px;
  overflow: hidden;
  border: 1px solid var(--surface-border);
  border-radius: var(--radius-sm);
  text-align: left;
  color: var(--text-title);
  background: #ffffff;
  cursor: pointer;
  transition:
    border-color 0.22s ease,
    background-color 0.22s ease,
    box-shadow 0.22s ease,
    transform 0.22s ease;
}

.session-item::before {
  content: "";
  position: absolute;
  top: 8px;
  bottom: 8px;
  left: 0;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: linear-gradient(180deg, var(--brand-teal-light), var(--brand-teal));
  transform: scaleY(0);
  transition: transform 0.24s cubic-bezier(0.4, 0, 0.2, 1);
}

.session-item:hover {
  border-color: var(--brand-teal-light);
  box-shadow: 0 6px 16px rgba(16, 42, 67, 0.07);
  transform: translateY(-1px);
}

.session-item.active {
  border-color: var(--brand-teal-light);
  background: #eef9fb;
  box-shadow: 0 6px 16px rgba(31, 122, 140, 0.14);
}

.session-item.active::before {
  transform: scaleY(1);
}

.session-item span {
  font-weight: 700;
}

.session-item.active span {
  color: var(--brand-teal);
}

.session-item small {
  color: var(--text-muted);
  font-size: 12px;
}

.chat-panel {
  display: flex;
  flex-direction: column;
}

.message-list {
  flex: 1;
  height: calc(100vh - 300px);
  min-height: 420px;
  padding: 18px;
  overflow-y: auto;
  background: #f8fafc;
}

.message-list::-webkit-scrollbar {
  width: 8px;
}

.message-list::-webkit-scrollbar-track {
  background: transparent;
}

.message-list::-webkit-scrollbar-thumb {
  border: 2px solid transparent;
  border-radius: 999px;
  background-clip: content-box;
  background-color: rgba(31, 122, 140, 0.2);
}

.message-list::-webkit-scrollbar-thumb:hover {
  background-color: rgba(31, 122, 140, 0.34);
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 14px;
}

.message-row.user {
  flex-direction: row-reverse;
}

.message-avatar {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 6px rgba(16, 42, 67, 0.14);
  user-select: none;
}

.message-row.assistant .message-avatar {
  background: linear-gradient(135deg, var(--brand-teal-light), var(--brand-teal));
}

.message-row.user .message-avatar {
  background: linear-gradient(135deg, var(--brand-navy-light), var(--brand-navy));
}

.message-bubble {
  max-width: 72%;
  padding: 14px 16px;
  border: 1px solid var(--surface-border);
  border-radius: var(--radius-md);
  background: #ffffff;
  box-shadow:
    0 1px 2px rgba(16, 42, 67, 0.04),
    0 6px 16px rgba(16, 42, 67, 0.06);
}

.message-row.assistant .message-bubble {
  border-top-left-radius: 4px;
}

.message-row.user .message-bubble {
  color: #ffffff;
  border-color: var(--brand-teal);
  border-top-right-radius: 4px;
  background: #1f7a8c;
  box-shadow:
    0 1px 2px rgba(16, 42, 67, 0.06),
    0 8px 18px rgba(31, 122, 140, 0.22);
}

.message-role {
  margin-bottom: 8px;
  color: var(--brand-teal);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.4px;
}

.message-row.user .message-role {
  color: rgba(255, 255, 255, 0.88);
}

.message-content {
  white-space: pre-wrap;
  line-height: 1.7;
}

.source-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--surface-border-soft);
}

.source-title {
  width: 100%;
  color: var(--text-muted);
  font-size: 13px;
}

.input-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 96px;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid var(--surface-border-soft);
  background: var(--surface-header);
}
</style>
