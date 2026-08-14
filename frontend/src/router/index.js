import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import PlaceholderView from '../views/PlaceholderView.vue'
import { moduleMenus } from '../config/menu'

function buildLeafRoutes(items, moduleName, apiPrefix) {
  const routes = []
  for (const item of items) {
    if (item.children && item.children.length) {
      routes.push(...buildLeafRoutes(item.children, moduleName || item.name, apiPrefix))
    } else {
      routes.push({
        path: item.path,
        name: item.path,
        component: PlaceholderView,
        meta: { title: item.name, moduleName: moduleName || item.name, apiPrefix }
      })
    }
  }
  return routes
}

let leafRoutes = []
for (const mod of moduleMenus) {
  leafRoutes = leafRoutes.concat(buildLeafRoutes(mod.children, mod.name, mod.apiPrefix))
}

export default createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: MainLayout,
      redirect: '/desktop/approval',
      children: leafRoutes
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/desktop/approval'
    }
  ]
})
