<script setup>
function goReport() {
  uni.navigateTo({ url: '/pages/report/report' })
}

function goMyReports() {
  uni.navigateTo({ url: '/pages/my-reports/my-reports' })
}

function goMyWorkOrders() {
  uni.navigateTo({ url: '/pages/work-orders/work-orders' })
}

function goAiAssistant() {
  uni.navigateTo({ url: '/pages/ai-assistant/ai-assistant' })
}

const actions = [
  {
    title: '拍照巡检',
    desc: '拍照上传，AI识别隐患',
    type: 'primary',
    icon: 'camera',
    handler: goReport,
  },
  {
    title: '我的上报',
    desc: '查看已提交隐患记录',
    type: 'blue',
    icon: 'file',
    handler: goMyReports,
  },
  {
    title: '我的工单',
    desc: '查看并处理待办工单',
    type: 'green',
    icon: 'check',
    handler: goMyWorkOrders,
  },
  {
    title: 'AI航运助手',
    desc: '咨询过闸、巡检和处置问题',
    type: 'teal',
    icon: 'chat',
    handler: goAiAssistant,
  },
]
</script>

<template>
  <view class="page home-page">
    <view class="water-bg">
      <view class="sky-band" />
      <view class="grid-layer" />
      <view class="wave wave-1" />
      <view class="wave wave-2" />
      <view class="wave wave-3" />
      <view class="light-sweep" />
    </view>

    <view class="home-content">
      <view class="hero">
        <text class="hero-kicker">水上安全巡检</text>
        <text class="hero-title">平陆运河AI水上安全巡检助手</text>
        <text class="hero-desc">AI辅助识别，快速完成现场隐患上报</text>
      </view>

      <view class="status-strip">
        <view class="status-item">
          <text class="status-value">AI</text>
          <text class="status-label">智能识别</text>
        </view>
        <view class="status-divider" />
        <view class="status-item">
          <text class="status-value">闭环</text>
          <text class="status-label">工单处置</text>
        </view>
        <view class="status-divider" />
        <view class="status-item">
          <text class="status-value">RAG</text>
          <text class="status-label">航运问答</text>
        </view>
      </view>

      <view class="action-list">
        <view
          v-for="item in actions"
          :key="item.title"
          class="action-card"
          :class="[`action-${item.type}`]"
          @click="item.handler()"
        >
          <view class="card-copy">
            <text class="action-title">{{ item.title }}</text>
            <text class="action-desc">{{ item.desc }}</text>
          </view>

          <view class="card-icon" :class="[`icon-${item.icon}`]">
            <svg
              v-if="item.icon === 'camera'"
              viewBox="0 0 24 24"
              width="27"
              height="27"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M4 8.6A2.6 2.6 0 0 1 6.6 6h1.1a1 1 0 0 0 .85-.47l.66-1.06A1 1 0 0 1 10.06 4h3.88a1 1 0 0 1 .85.47l.66 1.06A1 1 0 0 0 16.3 6h1.1A2.6 2.6 0 0 1 20 8.6v7.8A2.6 2.6 0 0 1 17.4 19H6.6A2.6 2.6 0 0 1 4 16.4z" />
              <circle cx="12" cy="12.4" r="3.3" />
            </svg>

            <svg
              v-else-if="item.icon === 'file'"
              viewBox="0 0 24 24"
              width="26"
              height="26"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M14 3H7.5A2.5 2.5 0 0 0 5 5.5v13A2.5 2.5 0 0 0 7.5 21h9a2.5 2.5 0 0 0 2.5-2.5V8z" />
              <path d="M14 3v5h5" />
              <path d="M9 13.2h6M9 16.8h4" />
            </svg>

            <svg
              v-else-if="item.icon === 'check'"
              viewBox="0 0 24 24"
              width="26"
              height="26"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <rect x="4" y="4" width="16" height="16" rx="4.5" />
              <path d="M8.4 12.3l2.3 2.3 4.9-5.2" />
            </svg>

            <svg
              v-else
              viewBox="0 0 24 24"
              width="26"
              height="26"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M21 11.5a8.4 8.4 0 0 1-8.5 8.2 8.8 8.8 0 0 1-3.8-.86L3 20l1.2-5.3a8 8 0 0 1-.8-3.2 8.4 8.4 0 0 1 8.5-8.2 8.4 8.4 0 0 1 9.1 8.2z" />
              <path d="M8.4 10.2h7.2M8.4 13.6h4.8" />
            </svg>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.home-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(160deg, #eaf8fb 0%, #d7eff5 48%, #edf7f5 100%);
}

.water-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.sky-band {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(120% 70% at 12% 0%, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.34) 42%, rgba(255, 255, 255, 0) 68%),
    linear-gradient(180deg, rgba(236, 249, 252, 0.74) 0%, rgba(216, 243, 248, 0.46) 32%, rgba(18, 73, 84, 0.38) 100%),
    url('/static/bg-canal.png');
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  transform: scale(1.02);
}

.grid-layer {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(31, 122, 140, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(31, 122, 140, 0.06) 1px, transparent 1px);
  background-size: 44px 44px;
  -webkit-mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.78), rgba(0, 0, 0, 0) 86%);
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.78), rgba(0, 0, 0, 0) 86%);
  animation: gridDrift 28s linear infinite;
}

@keyframes gridDrift {
  from {
    background-position: 0 0, 0 0;
  }
  to {
    background-position: 44px 44px, 44px 44px;
  }
}

.wave {
  position: absolute;
  left: -375px;
  width: calc(100% + 750px);
  background-repeat: repeat-x;
  background-size: 375px 100%;
  background-position: 0 bottom;
}

.wave-1 {
  bottom: 132px;
  height: 210px;
  opacity: 0.44;
  animation: waveSlide 22s linear infinite;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 375 220' preserveAspectRatio='none'%3E%3Cpath d='M0,112 C62,80 125,142 187,112 C250,82 312,142 375,112 L375,220 L0,220 Z' fill='%23ffffff' fill-opacity='0.56'/%3E%3C/svg%3E");
}

.wave-2 {
  bottom: 62px;
  height: 220px;
  opacity: 0.58;
  animation: waveSlide 15s linear infinite reverse;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 375 220' preserveAspectRatio='none'%3E%3Cpath d='M0,142 C70,110 130,170 200,142 C268,112 318,168 375,142 L375,220 L0,220 Z' fill='%23a9dce8' fill-opacity='0.86'/%3E%3C/svg%3E");
}

.wave-3 {
  bottom: -8px;
  height: 220px;
  opacity: 0.62;
  animation: waveSlide 11s linear infinite;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 375 220' preserveAspectRatio='none'%3E%3Cpath d='M0,174 C80,146 140,198 210,174 C280,146 320,194 375,174 L375,220 L0,220 Z' fill='%23ffffff' fill-opacity='0.78'/%3E%3C/svg%3E");
}

@keyframes waveSlide {
  from {
    transform: translate3d(0, 0, 0);
  }
  to {
    transform: translate3d(375px, 0, 0);
  }
}

.light-sweep {
  position: absolute;
  top: -48%;
  left: -36%;
  width: 172%;
  height: 42%;
  background: linear-gradient(100deg, rgba(255, 255, 255, 0) 30%, rgba(255, 255, 255, 0.46) 50%, rgba(255, 255, 255, 0) 70%);
  transform: rotate(-9deg);
  animation: sweepDown 12s ease-in-out infinite;
}

@keyframes sweepDown {
  0% {
    transform: translate3d(-10%, -42%, 0) rotate(-9deg);
    opacity: 0;
  }
  28% {
    opacity: 0.8;
  }
  100% {
    transform: translate3d(10%, 300%, 0) rotate(-9deg);
    opacity: 0;
  }
}

.home-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 26px 20px 42px;
  box-sizing: border-box;
}

.hero {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 2px 20px;
  animation: fadeUp 0.65s cubic-bezier(0.2, 0.7, 0.3, 1) backwards;
}

.hero-kicker {
  padding: 5px 12px;
  border: 1px solid rgba(47, 143, 176, 0.34);
  border-radius: 999px;
  color: #0d5a70;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 2px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 6px 16px rgba(16, 74, 88, 0.1);
}

.hero-title {
  font-size: 28px;
  line-height: 1.3;
  font-weight: 800;
  letter-spacing: 0;
  color: #07323c;
  text-shadow: 0 2px 5px rgba(255, 255, 255, 0.6);
}

.hero-desc {
  color: #52717b;
  font-size: 14px;
  line-height: 1.55;
}

.status-strip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 20px;
  padding: 13px 12px;
  border: 1px solid rgba(47, 143, 176, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.7);
  -webkit-backdrop-filter: blur(12px);
  backdrop-filter: blur(12px);
  box-shadow: 0 12px 24px rgba(16, 74, 88, 0.1);
  animation: fadeUp 0.65s cubic-bezier(0.2, 0.7, 0.3, 1) 0.08s backwards;
}

.status-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.status-value {
  color: #0f5967;
  font-size: 15px;
  font-weight: 800;
  line-height: 1.1;
}

.status-label {
  color: #6b8790;
  font-size: 11px;
  line-height: 1.1;
}

.status-divider {
  width: 1px;
  height: 26px;
  background: rgba(47, 143, 176, 0.18);
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 100%;
}

.action-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  min-height: 92px;
  padding: 18px;
  border: 1px solid transparent;
  border-radius: 8px;
  box-sizing: border-box;
  overflow: hidden;
  -webkit-tap-highlight-color: transparent;
  background:
    linear-gradient(150deg, rgba(255, 255, 255, 0.92) 0%, rgba(239, 251, 254, 0.78) 100%) padding-box,
    linear-gradient(140deg, rgba(47, 143, 176, 0.46), rgba(255, 255, 255, 0.3) 48%, rgba(47, 143, 176, 0.36)) border-box;
  -webkit-backdrop-filter: blur(14px) saturate(150%);
  backdrop-filter: blur(14px) saturate(150%);
  box-shadow:
    0 15px 28px rgba(16, 74, 88, 0.14),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  animation: fadeUp 0.58s cubic-bezier(0.2, 0.7, 0.3, 1) backwards;
}

.action-list .action-card:nth-child(1) {
  animation-delay: 0.12s;
}

.action-list .action-card:nth-child(2) {
  animation-delay: 0.2s;
}

.action-list .action-card:nth-child(3) {
  animation-delay: 0.28s;
}

.action-list .action-card:nth-child(4) {
  animation-delay: 0.36s;
}

.action-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  height: 3px;
  background: linear-gradient(90deg, rgba(86, 214, 232, 0) 0%, rgba(86, 214, 232, 0.95) 34%, rgba(42, 200, 180, 0.86) 66%, rgba(86, 214, 232, 0) 100%);
  background-size: 220% 100%;
  opacity: 0.9;
  animation: stripFlow 4s linear infinite;
}

.action-primary {
  color: #ffffff;
  background:
    linear-gradient(135deg, rgba(8, 42, 52, 0.96) 0%, rgba(24, 97, 113, 0.92) 54%, rgba(48, 154, 176, 0.88) 100%) padding-box,
    linear-gradient(140deg, rgba(255, 255, 255, 0.58), rgba(53, 163, 184, 0.52) 50%, rgba(255, 255, 255, 0.36)) border-box;
  box-shadow:
    0 20px 34px rgba(15, 59, 70, 0.32),
    inset 0 1px 0 rgba(255, 255, 255, 0.24);
}

.action-green {
  background:
    linear-gradient(150deg, rgba(255, 255, 255, 0.93) 0%, rgba(226, 249, 242, 0.8) 100%) padding-box,
    linear-gradient(140deg, rgba(42, 161, 137, 0.5), rgba(255, 255, 255, 0.34) 50%, rgba(42, 161, 137, 0.42)) border-box;
}

.action-teal {
  background:
    linear-gradient(150deg, rgba(255, 255, 255, 0.93) 0%, rgba(228, 250, 247, 0.8) 100%) padding-box,
    linear-gradient(140deg, rgba(37, 128, 135, 0.5), rgba(255, 255, 255, 0.34) 50%, rgba(37, 128, 135, 0.4)) border-box;
}

.action-card:active {
  transform: translate3d(0, 2px, 0) scale(0.985);
  box-shadow:
    0 8px 18px rgba(16, 74, 88, 0.16),
    inset 0 2px 6px rgba(16, 74, 88, 0.12);
}

.card-copy {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 7px;
}

.action-title {
  color: #10333b;
  font-size: 21px;
  font-weight: 800;
  line-height: 1.25;
}

.action-desc {
  color: #738f99;
  font-size: 13px;
  line-height: 1.45;
}

.action-primary .action-title {
  color: #ffffff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
}

.action-primary .action-desc {
  color: #cfeaf0;
}

.card-icon {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border: 1px solid rgba(47, 143, 176, 0.28);
  border-radius: 8px;
  color: #2b8fa8;
  background: linear-gradient(150deg, #e8f7fb, #d2eef6);
  box-shadow:
    inset 0 1px 0 #ffffff,
    0 6px 14px rgba(47, 143, 176, 0.16);
}

.action-primary .card-icon {
  width: 52px;
  height: 52px;
  color: #ffffff;
  border-color: rgba(255, 255, 255, 0.36);
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.25), rgba(255, 255, 255, 0.08));
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.52),
    0 8px 18px rgba(0, 0, 0, 0.16);
}

.icon-check {
  color: #1f9a80;
  border-color: rgba(42, 161, 137, 0.28);
  background: linear-gradient(150deg, #e5faf3, #ccf0e5);
}

.icon-chat {
  color: #187985;
  border-color: rgba(37, 128, 135, 0.28);
  background: linear-gradient(150deg, #e6fbf8, #ccefeb);
}

@keyframes stripFlow {
  from {
    background-position: 120% 0;
  }
  to {
    background-position: -120% 0;
  }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translate3d(0, 18px, 0);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}
</style>
