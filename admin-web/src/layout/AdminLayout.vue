<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCurrentUser, logout } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const currentUser = computed(() => getCurrentUser() || {})
const activeMenu = computed(() => {
  if (route.path.startsWith('/hazard-map')) return '/hazard-map'
  if (route.path.startsWith('/hazards')) return '/hazards'
  if (route.path.startsWith('/work-orders')) return '/work-orders'
  if (route.path.startsWith('/knowledge')) return '/knowledge'
  if (route.path.startsWith('/ai-assistant')) return '/ai-assistant'
  if (route.path.startsWith('/users')) return '/users'
  return '/dashboard'
})
const pageTitle = computed(() => route.meta.title || '首页')
const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long',
})
const canManageUsers = computed(() => currentUser.value.role === 'SUPER_ADMIN')

function handleCommand(command) {
  if (command === 'logout') {
    logout()
    router.replace('/login')
  }
}
</script>

<template>
  <el-container class="admin-shell">
    <el-aside class="admin-aside" width="220px">
      <div class="brand">
        <div class="brand-mark">PL</div>
        <div>
          <div class="brand-title">AI巡检</div>
          <div class="brand-subtitle">Safety Console</div>
        </div>
      </div>

      <div class="menu-group">功能导航</div>

      <el-menu :default-active="activeMenu" router class="side-menu">
        <el-menu-item index="/dashboard"><span>首页</span></el-menu-item>
        <el-menu-item index="/hazard-map"><span>航道一张图</span></el-menu-item>
        <el-menu-item index="/hazards"><span>隐患管理</span></el-menu-item>
        <el-menu-item index="/work-orders"><span>工单管理</span></el-menu-item>
        <el-menu-item index="/knowledge"><span>知识库管理</span></el-menu-item>
        <el-menu-item index="/ai-assistant"><span>AI航运助手</span></el-menu-item>
        <el-menu-item v-if="canManageUsers" index="/users"><span>用户管理</span></el-menu-item>
      </el-menu>

      <div class="aside-footer">
        <span class="aside-avatar">{{ currentUser.realName?.slice(0, 1) || '管' }}</span>
        <div>
          <div class="aside-user-name">{{ currentUser.realName || currentUser.username }}</div>
          <div class="aside-user-role">{{ currentUser.role }}</div>
        </div>
      </div>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div>
          <h1>平陆运河AI水上安全巡检平台</h1>
          <p>{{ pageTitle }}</p>
        </div>
        <div class="header-meta">
          <span class="header-date">{{ today }}</span>
          <el-dropdown class="header-user" trigger="click" @command="handleCommand">
            <span class="header-user-trigger">
              {{ currentUser.realName || currentUser.username }}
              <small>{{ currentUser.role }}</small>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.header-user {
  margin-left: 12px;
}

.header-user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 12px;
  border: 1px solid var(--surface-border);
  border-radius: 999px;
  color: var(--text-strong);
  background: #fff;
  cursor: pointer;
}

.header-user-trigger small {
  color: var(--text-muted);
}
</style>
