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
</script>

<template>
  <view class="page home-page">
    <view class="water-bg">
      <view class="bg-photo" />
      <view class="bg-veil" />
      <view class="aurora" />
      <view class="water-grid" />
      <view class="glow glow-a" />
      <view class="glow glow-b" />
      <view class="glow glow-c" />
      <view class="bubble b1" />
      <view class="bubble b2" />
      <view class="bubble b3" />
      <view class="bubble b4" />
      <view class="bubble b5" />
      <view class="wave wave-1" />
      <view class="wave wave-2" />
      <view class="wave wave-3" />
      <view class="bg-sweep" />
    </view>

    <view class="home-content">
      <view class="hero">
        <text class="hero-kicker">水上安全巡检</text>
        <text class="hero-title">平陆运河AI水上安全巡检助手</text>
        <text class="hero-desc">AI辅助识别，快速完成现场隐患上报</text>
      </view>

      <view class="action-list">
        <view class="action-card primary" @click="goReport">
          <view class="card-head">
            <text class="action-title">拍照巡检</text>
            <view class="card-icon">
              <svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 8.6A2.6 2.6 0 0 1 6.6 6h1.1a1 1 0 0 0 .85-.47l.66-1.06A1 1 0 0 1 10.06 4h3.88a1 1 0 0 1 .85.47l.66 1.06A1 1 0 0 0 16.3 6h1.1A2.6 2.6 0 0 1 20 8.6v7.8A2.6 2.6 0 0 1 17.4 19H6.6A2.6 2.6 0 0 1 4 16.4z" />
                <circle cx="12" cy="12.4" r="3.3" />
              </svg>
            </view>
          </view>
          <text class="action-desc">拍照上传，AI识别隐患</text>
        </view>

        <view class="action-card report" @click="goMyReports">
          <view class="card-head">
            <text class="action-title">我的上报</text>
            <view class="card-icon">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 3H7.5A2.5 2.5 0 0 0 5 5.5v13A2.5 2.5 0 0 0 7.5 21h9a2.5 2.5 0 0 0 2.5-2.5V8z" />
                <path d="M14 3v5h5" />
                <path d="M9 13.2h6M9 16.8h4" />
              </svg>
            </view>
          </view>
          <text class="action-desc">查看已提交隐患记录</text>
        </view>

        <view class="action-card order" @click="goMyWorkOrders">
          <view class="card-head">
            <text class="action-title">我的工单</text>
            <view class="card-icon">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <rect x="4" y="4" width="16" height="16" rx="4.5" />
                <path d="M8.4 12.3l2.3 2.3 4.9-5.2" />
              </svg>
            </view>
          </view>
          <text class="action-desc">查看并处理待办工单</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
/* ---------- 页面底：静态兜底渐变（图片加载前的过渡） ---------- */
.home-page {
  position: relative;
  overflow: hidden;
  min-height: 100vh;
  background: linear-gradient(160deg, #e9f8fc 0%, #d5eef7 45%, #c3e6f0 100%);
}

/* 背景照片：缓慢推近，营造航拍流动感 */
.bg-photo {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-image: url('/static/bg-canal.png');
  background-size: cover;
  background-position: center 36%;
  background-repeat: no-repeat;
  transform-origin: 52% 40%;
  animation: kenBurns 36s ease-in-out infinite alternate;
}

@keyframes kenBurns {
  0% {
    transform: scale(1.03) translate3d(0, 0, 0);
  }
  100% {
    transform: scale(1.14) translate3d(-1.6%, -1.4%, 0);
  }
}

/* 半透明纱罩：压低照片对比度，保证文案与卡片可读 */
.bg-veil {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background:
    radial-gradient(120% 80% at 14% 4%, rgba(255, 255, 255, 0.88) 0%, rgba(255, 255, 255, 0.5) 34%, rgba(255, 255, 255, 0) 64%),
    linear-gradient(180deg, rgba(240, 252, 255, 0.84) 0%, rgba(226, 246, 252, 0.6) 30%, rgba(202, 234, 245, 0.48) 58%, rgba(176, 218, 235, 0.78) 100%);
  -webkit-backdrop-filter: blur(1.5px) saturate(126%);
  backdrop-filter: blur(1.5px) saturate(126%);
}

/* 顶部极光：缓慢旋转的锥形光，增加科技感 */
.aurora {
  position: absolute;
  top: -170px;
  left: -25%;
  width: 150%;
  height: 380px;
  border-radius: 50%;
  background: conic-gradient(
    from 180deg at 50% 40%,
    rgba(56, 176, 199, 0) 0deg,
    rgba(96, 220, 238, 0.42) 80deg,
    rgba(129, 212, 233, 0) 160deg,
    rgba(52, 206, 184, 0.34) 260deg,
    rgba(56, 176, 199, 0) 340deg
  );
  filter: blur(48px);
  opacity: 0.8;
  animation: auroraSpin 30s linear infinite;
}

@keyframes auroraSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* ---------- 背景装饰层 ---------- */
.water-bg {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  overflow: hidden;
  pointer-events: none;
}

.water-grid {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-image:
    linear-gradient(rgba(31, 122, 140, 0.055) 1px, transparent 1px),
    linear-gradient(90deg, rgba(31, 122, 140, 0.055) 1px, transparent 1px);
  background-size: 44px 44px;
  -webkit-mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0) 86%);
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0) 86%);
  animation: gridDrift 26s linear infinite;
}

@keyframes gridDrift {
  from {
    background-position: 0 0, 0 0;
  }
  to {
    background-position: 44px 44px, 44px 44px;
  }
}

/* 光晕 */
.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(42px);
}

.glow-a {
  top: -70px;
  right: -90px;
  width: 260px;
  height: 260px;
  background: radial-gradient(circle, rgba(56, 176, 199, 0.55), rgba(56, 176, 199, 0) 70%);
  animation: floatY 13s ease-in-out infinite;
}

.glow-b {
  bottom: 60px;
  left: -130px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(42, 160, 138, 0.4), rgba(42, 160, 138, 0) 70%);
  animation: floatY 17s ease-in-out infinite reverse;
}

.glow-c {
  top: 42%;
  right: -110px;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(129, 212, 233, 0.5), rgba(129, 212, 233, 0) 70%);
  animation: floatY 11s ease-in-out infinite;
}

@keyframes floatY {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(0, -26px, 0) scale(1.09);
  }
}

/* 上浮气泡 */
.bubble {
  position: absolute;
  bottom: -40px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.65);
  background: radial-gradient(circle at 32% 30%, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.22) 58%, rgba(255, 255, 255, 0) 74%);
  animation-name: rise;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}

.b1 {
  left: 9%;
  width: 13px;
  height: 13px;
  animation-duration: 15s;
  animation-delay: 0s;
}

.b2 {
  left: 26%;
  width: 8px;
  height: 8px;
  animation-duration: 12s;
  animation-delay: 3.5s;
}

.b3 {
  left: 48%;
  width: 17px;
  height: 17px;
  animation-duration: 19s;
  animation-delay: 1.4s;
}

.b4 {
  left: 68%;
  width: 10px;
  height: 10px;
  animation-duration: 14s;
  animation-delay: 6s;
}

.b5 {
  left: 86%;
  width: 14px;
  height: 14px;
  animation-duration: 17s;
  animation-delay: 2.4s;
}

@keyframes rise {
  0% {
    transform: translate3d(0, 0, 0) scale(0.85);
    opacity: 0;
  }
  14% {
    opacity: 0.9;
  }
  85% {
    opacity: 0.55;
  }
  100% {
    transform: translate3d(14px, -108vh, 0) scale(1.12);
    opacity: 0;
  }
}

/* 流动水波 */
.wave {
  position: absolute;
  left: -375px;
  width: calc(100% + 750px);
  background-repeat: repeat-x;
  background-size: 375px 100%;
  background-position: 0 bottom;
}

.wave-1 {
  bottom: 118px;
  height: 210px;
  opacity: 0.42;
  animation: waveSlide 20s linear infinite;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 375 220' preserveAspectRatio='none'%3E%3Cpath d='M0,110 C62,80 125,140 187,110 C250,80 312,140 375,110 L375,220 L0,220 Z' fill='%23ffffff' fill-opacity='0.55'/%3E%3C/svg%3E");
}

.wave-2 {
  bottom: 62px;
  height: 220px;
  opacity: 0.6;
  animation: waveSlide 14s linear infinite reverse;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 375 220' preserveAspectRatio='none'%3E%3Cpath d='M0,140 C70,110 130,170 200,140 C268,112 318,168 375,140 L375,220 L0,220 Z' fill='%23a9dce8' fill-opacity='0.85'/%3E%3C/svg%3E");
}

.wave-3 {
  bottom: -6px;
  height: 220px;
  opacity: 0.55;
  animation: waveSlide 10s linear infinite;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 375 220' preserveAspectRatio='none'%3E%3Cpath d='M0,172 C80,146 140,198 210,172 C280,146 320,194 375,172 L375,220 L0,220 Z' fill='%23ffffff' fill-opacity='0.78'/%3E%3C/svg%3E");
}

@keyframes waveSlide {
  from {
    transform: translate3d(0, 0, 0);
  }
  to {
    transform: translate3d(375px, 0, 0);
  }
}

/* 缓慢下扫的光带：让画面有呼吸感 */
.bg-sweep {
  position: absolute;
  top: -55%;
  left: -35%;
  width: 170%;
  height: 45%;
  background: linear-gradient(
    100deg,
    rgba(255, 255, 255, 0) 30%,
    rgba(255, 255, 255, 0.5) 50%,
    rgba(255, 255, 255, 0) 70%
  );
  transform: rotate(-9deg);
  animation: sweepDown 12s ease-in-out infinite;
}

@keyframes sweepDown {
  0% {
    transform: translate3d(-10%, -40%, 0) rotate(-9deg);
    opacity: 0;
  }
  28% {
    opacity: 0.85;
  }
  100% {
    transform: translate3d(10%, 300%, 0) rotate(-9deg);
    opacity: 0;
  }
}

/* ---------- 内容层 ---------- */
.home-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 26px 22px 44px;
}

.hero {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 2px 34px;
  animation: fadeUp 0.7s cubic-bezier(0.2, 0.7, 0.3, 1) backwards;
}

/* 顶部徽章：玻璃质感 + 呼吸光环 */
.hero-kicker {
  padding: 5px 14px;
  border-radius: 999px;
  border: 1px solid rgba(47, 143, 176, 0.35);
  color: #0d5a70;
  font-size: 12.5px;
  font-weight: 700;
  letter-spacing: 3px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.92), rgba(226, 247, 253, 0.72));
  animation: chipGlow 4.6s ease-in-out infinite;
}

@keyframes chipGlow {
  0%,
  100% {
    box-shadow:
      0 6px 16px rgba(16, 74, 88, 0.12),
      inset 0 1px 0 #ffffff,
      0 0 0 0 rgba(86, 200, 222, 0.45);
  }
  50% {
    box-shadow:
      0 6px 16px rgba(16, 74, 88, 0.12),
      inset 0 1px 0 #ffffff,
      0 0 0 7px rgba(86, 200, 222, 0);
  }
}

/* 标题：流动的渐变文字 */
.hero-title {
  font-size: 28px;
  line-height: 1.32;
  font-weight: 800;
  letter-spacing: 1px;
  background: linear-gradient(96deg, #04262c 0%, #0f5f78 26%, #1ea3a8 46%, #0d4a58 68%, #04262c 100%);
  background-size: 260% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  -webkit-text-fill-color: transparent;
  filter: drop-shadow(0 2px 5px rgba(255, 255, 255, 0.65));
  animation: titleFlow 9s ease-in-out infinite;
}

@keyframes titleFlow {
  0%,
  100% {
    background-position: 0% 0%;
  }
  50% {
    background-position: 100% 0%;
  }
}

.hero-desc {
  color: #53707a;
  font-size: 14px;
  line-height: 1.5;
  letter-spacing: 0.3px;
}

/* ---------- 卡片列表 ---------- */
.action-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 18px;
  width: 100%;
}

.action-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 82%;
  max-width: 340px;
  padding: 20px 20px 22px;
  border-radius: 22px;
  border: 1.5px solid transparent;
  background:
    linear-gradient(150deg, rgba(255, 255, 255, 0.9) 0%, rgba(238, 250, 254, 0.76) 100%) padding-box,
    linear-gradient(140deg, rgba(47, 143, 176, 0.62), rgba(255, 255, 255, 0.42) 48%, rgba(47, 143, 176, 0.48)) border-box;
  -webkit-backdrop-filter: blur(16px) saturate(150%);
  backdrop-filter: blur(16px) saturate(150%);
  box-shadow:
    0 18px 34px rgba(16, 74, 88, 0.16),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  overflow: hidden;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
  transition:
    transform 0.3s cubic-bezier(0.2, 0.7, 0.3, 1),
    box-shadow 0.3s ease,
    border-color 0.3s ease;
  animation: fadeUp 0.6s cubic-bezier(0.2, 0.7, 0.3, 1) backwards;
}

.action-list .action-card:nth-child(1) {
  animation-delay: 0.06s;
}

.action-list .action-card:nth-child(2) {
  animation-delay: 0.18s;
}

.action-list .action-card:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translate3d(0, 20px, 0);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

/* 卡片顶部流光条 */
.action-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  height: 3px;
  background: linear-gradient(
    90deg,
    rgba(86, 214, 232, 0) 0%,
    rgba(86, 214, 232, 0.95) 30%,
    rgba(42, 200, 180, 0.9) 62%,
    rgba(86, 214, 232, 0) 100%
  );
  background-size: 220% 100%;
  opacity: 0.9;
  animation: stripFlow 3.8s linear infinite;
  pointer-events: none;
}

@keyframes stripFlow {
  from {
    background-position: 120% 0;
  }
  to {
    background-position: -120% 0;
  }
}

/* 悬浮时掠过的高光 */
.action-card::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: linear-gradient(105deg, rgba(255, 255, 255, 0) 36%, rgba(255, 255, 255, 0.7) 50%, rgba(255, 255, 255, 0) 64%);
  transform: translate3d(-130%, 0, 0);
  transition: transform 0.75s ease;
  pointer-events: none;
}

.action-card:hover::after {
  transform: translate3d(130%, 0, 0);
}

/* 悬浮反馈 */
.action-card:hover {
  transform: translate3d(0, -7px, 0) scale(1.016);
  box-shadow:
    0 26px 46px rgba(16, 74, 88, 0.2),
    0 0 0 4px rgba(58, 168, 189, 0.13),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

/* 点击反馈 */
.action-card:active {
  transform: translate3d(0, -1px, 0) scale(0.982);
  box-shadow:
    0 8px 18px rgba(16, 74, 88, 0.18),
    inset 0 2px 6px rgba(16, 74, 88, 0.12);
  transition-duration: 0.09s;
}

/* 主卡片：深青渐变（半透，透出背景纹理） */
.action-card.primary {
  background:
    linear-gradient(135deg, rgba(8, 42, 52, 0.95) 0%, rgba(24, 97, 113, 0.9) 52%, rgba(48, 154, 176, 0.86) 100%) padding-box,
    linear-gradient(140deg, rgba(255, 255, 255, 0.68), rgba(53, 163, 184, 0.55) 50%, rgba(255, 255, 255, 0.4)) border-box;
  -webkit-backdrop-filter: blur(18px) saturate(165%);
  backdrop-filter: blur(18px) saturate(165%);
  box-shadow:
    0 22px 40px rgba(15, 59, 70, 0.36),
    inset 0 1px 0 rgba(255, 255, 255, 0.26);
}

.action-card.primary:hover {
  box-shadow:
    0 32px 56px rgba(15, 59, 70, 0.45),
    0 0 0 4px rgba(84, 196, 214, 0.24),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

/* 我的上报：冰蓝玻璃 + 蓝青描边 */
.action-card.report {
  background:
    linear-gradient(150deg, rgba(255, 255, 255, 0.92) 0%, rgba(228, 246, 253, 0.78) 100%) padding-box,
    linear-gradient(140deg, rgba(47, 143, 176, 0.66), rgba(255, 255, 255, 0.36) 50%, rgba(47, 143, 176, 0.5)) border-box;
}

/* 我的工单：薄荷玻璃 + 青绿描边 */
.action-card.order {
  background:
    linear-gradient(150deg, rgba(255, 255, 255, 0.92) 0%, rgba(226, 249, 242, 0.78) 100%) padding-box,
    linear-gradient(140deg, rgba(42, 161, 137, 0.66), rgba(255, 255, 255, 0.36) 50%, rgba(42, 161, 137, 0.5)) border-box;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

/* 图标：渐变内芯 + 细描边 */
.card-icon {
  display: flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 15px;
  border: 1px solid transparent;
  transition:
    transform 0.32s cubic-bezier(0.2, 0.7, 0.3, 1),
    box-shadow 0.32s ease;
}

.action-card.primary .card-icon {
  width: 50px;
  height: 50px;
  border-radius: 16px;
  color: #ffffff;
  background: linear-gradient(150deg, rgba(255, 255, 255, 0.26), rgba(255, 255, 255, 0.07));
  border-color: rgba(255, 255, 255, 0.4);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.55),
    0 8px 18px rgba(0, 0, 0, 0.18);
  animation: haloPulse 3.8s ease-in-out infinite;
}

@keyframes haloPulse {
  0%,
  100% {
    box-shadow:
      inset 0 1px 0 rgba(255, 255, 255, 0.55),
      0 8px 18px rgba(0, 0, 0, 0.18),
      0 0 0 0 rgba(140, 226, 240, 0.5);
  }
  50% {
    box-shadow:
      inset 0 1px 0 rgba(255, 255, 255, 0.55),
      0 8px 18px rgba(0, 0, 0, 0.18),
      0 0 0 9px rgba(140, 226, 240, 0);
  }
}

.action-card.report .card-icon {
  color: #2b8fb0;
  background: linear-gradient(150deg, #e8f6fc, #d0ebf6);
  border-color: rgba(47, 143, 176, 0.35);
  box-shadow:
    inset 0 1px 0 #ffffff,
    0 6px 14px rgba(47, 143, 176, 0.18);
}

.action-card.order .card-icon {
  color: #1f9a80;
  background: linear-gradient(150deg, #e4f9f2, #c9efe3);
  border-color: rgba(42, 161, 137, 0.35);
  box-shadow:
    inset 0 1px 0 #ffffff,
    0 6px 14px rgba(42, 161, 137, 0.18);
}

.action-card:hover .card-icon {
  transform: rotate(-7deg) scale(1.1);
}

.action-card:active .card-icon {
  transform: rotate(0deg) scale(0.94);
}

.action-title {
  color: #10333b;
  font-size: 21px;
  font-weight: 800;
  letter-spacing: 2px;
  transition: letter-spacing 0.32s ease;
}

.action-card:hover .action-title {
  letter-spacing: 3px;
}

.action-card.primary .action-title {
  color: #ffffff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.action-card.report .action-title {
  color: #14607a;
}

.action-card.order .action-title {
  color: #0f6b58;
}

.action-desc {
  color: #7b93a0;
  font-size: 13.5px;
  line-height: 1.5;
}

.action-card.primary .action-desc {
  color: #cfeaf0;
}

.action-card.report .action-desc {
  color: #6f8f9c;
}

.action-card.order .action-desc {
  color: #6f9a8f;
}
</style>
