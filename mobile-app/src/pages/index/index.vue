<script setup>
import { computed, onMounted, ref } from 'vue'
import { getCurrentUser, logout, requireLogin } from '../../utils/auth'

const user = ref(null)

const roleLabel = computed(() => {
  if (user.value?.role === 'INSPECTOR') return '巡检员'
  if (user.value?.role === 'WORKER') return '处置人员'
  if (user.value?.role === 'ADMIN') return '管理员'
  if (user.value?.role === 'SUPER_ADMIN') return '系统管理员'
  return ''
})

const actions = computed(() => {
  const common = [
    {
      title: 'AI航运助手',
      desc: '咨询过闸、巡检和处置问题',
      type: 'teal',
      handler: () => uni.navigateTo({ url: '/pages/ai-assistant/ai-assistant' }),
    },
  ]
  if (user.value?.role === 'INSPECTOR') {
    return [
      {
        title: '拍照巡检',
        desc: '拍照上传，AI识别隐患',
        type: 'primary',
        handler: () => uni.navigateTo({ url: '/pages/report/report' }),
      },
      {
        title: '我的上报',
        desc: '查看本人提交的隐患记录',
        type: 'blue',
        handler: () => uni.navigateTo({ url: '/pages/my-reports/my-reports' }),
      },
      ...common,
    ]
  }
  if (user.value?.role === 'WORKER') {
    return [
      {
        title: '我的工单',
        desc: '只查看分配给自己的工单',
        type: 'green',
        handler: () => uni.navigateTo({ url: '/pages/work-orders/work-orders' }),
      },
      ...common,
    ]
  }
  return [
    {
      title: '我的工单',
      desc: '移动端基础查看',
      type: 'green',
      handler: () => uni.navigateTo({ url: '/pages/work-orders/work-orders' }),
    },
    ...common,
  ]
})

function handleLogout() {
  logout()
  uni.reLaunch({ url: '/pages/login/login' })
}

onMounted(() => {
  if (!requireLogin()) return
  user.value = getCurrentUser()
})
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
        <text class="hero-desc">AI辅助识别，责任闭环追踪</text>
      </view>

      <view class="user-strip">
        <view>
          <text class="user-name">{{ user?.realName || user?.username }}</text>
          <text class="user-role">{{ roleLabel }}</text>
        </view>
        <button class="logout-button" @click="handleLogout">退出</button>
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
          <view class="card-icon">{{ item.title.slice(0, 1) }}</view>
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
  transform: scale(1.02);
}

.grid-layer {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(31, 122, 140, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(31, 122, 140, 0.06) 1px, transparent 1px);
  background-size: 44px 44px;
}

.wave {
  position: absolute;
  left: -375px;
  width: calc(100% + 750px);
  background-repeat: repeat-x;
  background-size: 375px 100%;
}

.wave-1,
.wave-2,
.wave-3 {
  bottom: 0;
  height: 220px;
  opacity: 0.45;
}

.light-sweep {
  position: absolute;
  top: -48%;
  left: -36%;
  width: 172%;
  height: 42%;
  background: linear-gradient(100deg, rgba(255, 255, 255, 0) 30%, rgba(255, 255, 255, 0.46) 50%, rgba(255, 255, 255, 0) 70%);
  transform: rotate(-9deg);
}

.home-content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  padding: 26px 20px 42px;
  box-sizing: border-box;
}

.hero {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px 2px 20px;
}

.hero-kicker {
  align-self: flex-start;
  padding: 5px 12px;
  border: 1px solid rgba(47, 143, 176, 0.34);
  border-radius: 999px;
  color: #0d5a70;
  font-size: 12px;
  font-weight: 700;
  background: rgba(255, 255, 255, 0.76);
}

.hero-title {
  color: #07323c;
  font-size: 28px;
  line-height: 1.3;
  font-weight: 800;
}

.hero-desc {
  color: #52717b;
  font-size: 14px;
}

.user-strip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  padding: 13px 12px;
  border: 1px solid rgba(47, 143, 176, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 12px 24px rgba(16, 74, 88, 0.1);
}

.user-name,
.user-role {
  display: block;
}

.user-name {
  color: #102a43;
  font-size: 17px;
  font-weight: 800;
}

.user-role {
  margin-top: 3px;
  color: #627d98;
  font-size: 12px;
}

.logout-button {
  width: 70px;
  height: 34px;
  border: 0;
  border-radius: 8px;
  color: #fff;
  background: #1f7a8c;
  font-size: 13px;
}

.logout-button::after {
  border: 0;
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.action-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 92px;
  padding: 18px;
  border: 1px solid rgba(47, 143, 176, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 15px 28px rgba(16, 74, 88, 0.14);
}

.action-primary {
  color: #fff;
  background: linear-gradient(135deg, rgba(8, 42, 52, 0.96), rgba(48, 154, 176, 0.88));
}

.action-title {
  display: block;
  color: #10333b;
  font-size: 21px;
  font-weight: 800;
}

.action-primary .action-title,
.action-primary .action-desc {
  color: #fff;
}

.action-desc {
  display: block;
  margin-top: 7px;
  color: #738f99;
  font-size: 13px;
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  color: #2b8fa8;
  background: #e8f7fb;
  font-weight: 800;
}
</style>
