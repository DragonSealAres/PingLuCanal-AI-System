<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => {
  if (route.path.startsWith('/hazards')) return '/hazards'
  if (route.path.startsWith('/work-orders')) return '/work-orders'
  return '/dashboard'
})
const pageTitle = computed(() => route.meta.title || '首页')
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

      <el-menu :default-active="activeMenu" router class="side-menu">
        <el-menu-item index="/dashboard">首页</el-menu-item>
        <el-menu-item index="/hazards">隐患管理</el-menu-item>
        <el-menu-item index="/work-orders">工单管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div>
          <h1>平陆运河AI水上安全巡检平台</h1>
          <p>{{ pageTitle }}</p>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
