import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '../layout/AdminLayout.vue'
import Dashboard from '../views/Dashboard.vue'
import HazardList from '../views/HazardList.vue'
import HazardDetail from '../views/HazardDetail.vue'
import WorkOrderList from '../views/WorkOrderList.vue'

const routes = [
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '首页' },
      },
      {
        path: 'hazards',
        name: 'HazardList',
        component: HazardList,
        meta: { title: '隐患管理' },
      },
      {
        path: 'hazards/:id',
        name: 'HazardDetail',
        component: HazardDetail,
        meta: { title: '隐患详情' },
      },
      {
        path: 'work-orders',
        name: 'WorkOrderList',
        component: WorkOrderList,
        meta: { title: '工单管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
