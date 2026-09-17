<script setup>
import { reactive, ref } from 'vue'
import { login } from '../../api/auth'
import { setCurrentUser, setToken } from '../../utils/auth'

const loading = ref(false)
const form = reactive({
  username: 'inspector01',
  password: '123456',
})

async function submit() {
  if (!form.username || !form.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const result = await login({ ...form })
    setToken(result.token)
    setCurrentUser(result.user)
    uni.showToast({ title: '登录成功', icon: 'success' })
    uni.reLaunch({ url: '/pages/index/index' })
  } catch (error) {
    console.warn('Login failed', error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <view class="login-page">
    <view class="login-card">
      <view class="brand-mark">PL</view>
      <text class="title">平陆运河AI水上安全巡检</text>
      <text class="subtitle">移动端登录</text>

      <view class="field">
        <text>用户名</text>
        <input v-model="form.username" placeholder="请输入用户名" />
      </view>
      <view class="field">
        <text>密码</text>
        <input v-model="form.password" password placeholder="请输入密码" />
      </view>
      <button class="login-button" :loading="loading" @click="submit">登录</button>
    </view>
  </view>
</template>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(160deg, #eaf8fb 0%, #d7eff5 48%, #edf7f5 100%);
}

.login-card {
  width: 100%;
  padding: 24px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 16px 36px rgba(16, 74, 88, 0.16);
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  margin-bottom: 16px;
  border-radius: 12px;
  color: #0b2547;
  background: linear-gradient(145deg, #a7f3e3, #6fd8e8);
  font-weight: 800;
}

.title,
.subtitle {
  display: block;
}

.title {
  color: #07323c;
  font-size: 23px;
  font-weight: 800;
  line-height: 1.3;
}

.subtitle {
  margin: 6px 0 22px;
  color: #627d98;
  font-size: 13px;
}

.field {
  margin-bottom: 14px;
}

.field text {
  display: block;
  margin-bottom: 7px;
  color: #486581;
  font-size: 13px;
}

.field input {
  height: 46px;
  padding: 0 12px;
  border: 1px solid #bcccdc;
  border-radius: 10px;
  background: #f8fafc;
}

.login-button {
  height: 48px;
  margin-top: 8px;
  border: 0;
  border-radius: 12px;
  color: #fff;
  background: #12343b;
  font-size: 16px;
  font-weight: 700;
}

.login-button::after {
  border: 0;
}
</style>
