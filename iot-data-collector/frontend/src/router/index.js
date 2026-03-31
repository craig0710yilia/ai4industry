import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/data-sources'
  },
  {
    path: '/data-sources',
    name: 'DataSources',
    component: () => import('@/views/DataSourcesView.vue')
  },
  {
    path: '/data-points/:dataSourceId',
    name: 'DataPoints',
    component: () => import('@/views/DataPointsView.vue')
  },
  {
    path: '/monitor',
    name: 'Monitor',
    component: () => import('@/views/MonitorView.vue')
  },
  {
    path: '/backups',
    name: 'Backups',
    component: () => import('@/views/BackupsView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
