<script setup>
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '../api/auth'
import { isAdminUser, setCurrentUser, setToken } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const formRef = ref(null)
const form = reactive({
  username: 'admin01',
  password: '123456',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const result = await login({ ...form })
    if (!isAdminUser(result.user)) {
      ElMessage.error('该账号无管理后台权限')
      return
    }
    setToken(result.token)
    setCurrentUser(result.user)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <section class="login-panel">
      <div class="login-brand">
        <div class="brand-mark">PL</div>
        <div>
          <h1>平陆运河AI水上安全巡检平台</h1>
          <p>Management Console</p>
        </div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" class="login-button" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>
    </section>
  </div>
</template>

<style scoped>
.login-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 24px;
  background:
    linear-gradient(180deg, rgba(234, 248, 251, 0.84), rgba(218, 241, 246, 0.92)),
    url('/favicon.svg');
  background-size: cover, 240px;
  background-position: center, right 8% bottom 8%;
  background-repeat: no-repeat;
}

.login-panel {
  width: min(420px, 100%);
  padding: 28px;
  border: 1px solid var(--surface-border);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 45px rgba(16, 42, 67, 0.16);
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
}

.login-brand h1 {
  color: var(--text-title);
  font-size: 22px;
  line-height: 1.25;
}

.login-brand p {
  margin-top: 4px;
  color: var(--text-muted);
}

.login-button {
  width: 100%;
  margin-top: 8px;
}
</style>
