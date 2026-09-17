import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '../layout/AdminLayout.vue'
import Dashboard from '../views/Dashboard.vue'
import HazardMap from '../views/HazardMap.vue'
import HazardList from '../views/HazardList.vue'
import HazardDetail from '../views/HazardDetail.vue'
import WorkOrderList from '../views/WorkOrderList.vue'
import KnowledgeManage from '../views/KnowledgeManage.vue'
import AiAssistant from '../views/AiAssistant.vue'
import Login from '../views/Login.vue'
import UserManage from '../views/UserManage.vue'
import { getCurrentUser, getToken, isAdminUser, logout } from '../utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { public: true, title: '登录' },
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: Dashboard, meta: { title: '首页' } },
      { path: 'hazard-map', name: 'HazardMap', component: HazardMap, meta: { title: '航道一张图' } },
      { path: 'hazards', name: 'HazardList', component: HazardList, meta: { title: '隐患管理' } },
      { path: 'hazards/:id', name: 'HazardDetail', component: HazardDetail, meta: { title: '隐患详情' } },
      { path: 'work-orders', name: 'WorkOrderList', component: WorkOrderList, meta: { title: '工单管理' } },
      { path: 'knowledge', name: 'KnowledgeManage', component: KnowledgeManage, meta: { title: '知识库管理' } },
      { path: 'ai-assistant', name: 'AiAssistant', component: AiAssistant, meta: { title: 'AI航运助手' } },
      { path: 'users', name: 'UserManage', component: UserManage, meta: { title: '用户管理', superAdminOnly: true } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  if (to.meta.public) {
    if (to.path === '/login' && getToken() && isAdminUser()) {
      return '/dashboard'
    }
    return true
  }

  const user = getCurrentUser()
  if (!getToken() || !isAdminUser(user)) {
    logout()
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.meta.superAdminOnly && user.role !== 'SUPER_ADMIN') {
    return '/dashboard'
  }

  return true
})

export default router
