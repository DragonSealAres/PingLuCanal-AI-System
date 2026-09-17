<script setup>
import { nextTick, ref } from 'vue'
import { askAssistant } from '../../api/chat'

const sessionId = ref('')
const question = ref('')
const sending = ref(false)
const messages = ref([])
const scrollTop = ref(0)

async function sendQuestion() {
  const cleanQuestion = question.value.trim()
  if (!cleanQuestion) {
    uni.showToast({ title: '请输入问题', icon: 'none' })
    return
  }

  messages.value.push({
    role: 'user',
    content: cleanQuestion,
    sources: [],
  })
  question.value = ''
  sending.value = true
  await scrollToBottom()

  try {
    const result = await askAssistant({
      sessionId: sessionId.value,
      userName: '移动端用户',
      question: cleanQuestion,
    })
    sessionId.value = result.sessionId
    messages.value.push({
      role: 'assistant',
      content: result.answer,
      sources: result.sources || [],
    })
    await scrollToBottom()
  } finally {
    sending.value = false
  }
}

async function scrollToBottom() {
  await nextTick()
  scrollTop.value += 9999
}

function showSource(source) {
  uni.showModal({
    title: source.documentName || '参考来源',
    content: `片段${source.chunkIndex || '-'}\n\n${source.content || ''}`,
    showCancel: false,
  })
}
</script>

<template>
  <view class="page assistant-page">
    <scroll-view class="message-list" scroll-y :scroll-top="scrollTop">
      <view v-if="messages.length === 0" class="empty-state">
        <text class="empty-title">AI航运服务问答助手</text>
        <text class="empty-desc">可咨询航道巡检、船舶过闸、隐患处置等课程演示知识库问题</text>
      </view>

      <view
        v-for="(item, index) in messages"
        :key="index"
        class="message-row"
        :class="item.role"
      >
        <view class="message-bubble">
          <text class="message-role">{{ item.role === 'assistant' ? 'AI助手' : '我' }}</text>
          <text class="message-content">{{ item.content }}</text>
          <view v-if="item.role === 'assistant' && item.sources.length" class="source-list">
            <text class="source-title">参考来源</text>
            <button
              v-for="source in item.sources"
              :key="`${source.documentName}-${source.chunkIndex}`"
              class="source-button"
              @click="showSource(source)"
            >
              《{{ source.documentName }}》片段{{ source.chunkIndex }}
            </button>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="input-bar">
      <textarea
        v-model="question"
        class="question-input"
        maxlength="500"
        auto-height
        placeholder="请输入航运服务问题"
      />
      <button class="send-button" :loading="sending" @click="sendQuestion">发送</button>
    </view>
  </view>
</template>

<style scoped>
.assistant-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #eef5f7;
}

.message-list {
  flex: 1;
  padding: 14px;
  box-sizing: border-box;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  max-width: 320px;
  margin: 22vh auto 0;
  padding: 36px 18px;
  text-align: center;
  color: #627d98;
}

.empty-state::before {
  content: '';
  width: 96px;
  height: 96px;
  margin-bottom: 8px;
  border-radius: 28px;
  background: #ffffff url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48' fill='none' stroke='%231f7a8c' stroke-width='2.4' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='8' y='8' width='32' height='24' rx='6'/%3E%3Cpath d='M19 32l-7 6v-6'/%3E%3Cpath d='M17 20h.1M24 20h.1M31 20h.1'/%3E%3C/svg%3E") center / 48px no-repeat;
  box-shadow: 0 12px 28px rgba(31, 122, 140, 0.14);
}

.empty-title {
  color: #102a43;
  font-size: 20px;
  font-weight: 700;
}

.empty-desc {
  font-size: 14px;
  line-height: 1.6;
}

.message-row {
  display: flex;
  margin-bottom: 12px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 78%;
  padding: 12px;
  border-radius: 10px;
  background: #ffffff;
  box-shadow: 0 6px 16px rgba(16, 42, 67, 0.08);
}

.message-row.user .message-bubble {
  color: #ffffff;
  background: #1f7a8c;
}

.message-role {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 700;
}

.message-content {
  display: block;
  font-size: 15px;
  line-height: 1.7;
  white-space: pre-wrap;
}

.source-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid #d9e2ec;
}

.source-title {
  color: #627d98;
  font-size: 12px;
}

.source-button {
  padding: 0;
  border: 0;
  text-align: left;
  color: #1f7a8c;
  font-size: 13px;
  line-height: 1.5;
  background: transparent;
}

.source-button::after {
  border: 0;
}

.input-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 72px;
  gap: 10px;
  padding: 12px;
  border-top: 1px solid #d9e2ec;
  background: #ffffff;
  box-shadow: 0 -4px 16px rgba(16, 42, 67, 0.06);
}

.question-input {
  width: 100%;
  min-height: 42px;
  max-height: 120px;
  padding: 10px;
  border: 1px solid #d9e2ec;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 15px;
  background: #f8fafc;
}

.send-button {
  height: 42px;
  border-radius: 8px;
  color: #ffffff;
  background: #12343b;
}

.send-button::after {
  border: 0;
}
</style>
