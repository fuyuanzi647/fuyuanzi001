<template>
  <el-container class="main-layout">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">
        <el-icon :size="24" color="#4096ff"><FirstAidKit /></el-icon>
        <span class="logo-title">浮元子流向管理</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activePath"
          router
          class="layout-menu"
          background-color="#001529"
          text-color="#a6adb4"
          active-text-color="#ffffff"
        >
          <template v-for="module in moduleMenus" :key="module.path">
            <el-sub-menu :index="module.path">
              <template #title>
                <el-icon><component :is="module.icon" /></el-icon>
                <span>{{ module.name }}</span>
              </template>
              <template v-for="child in module.children" :key="child.path">
                <el-sub-menu v-if="child.children && child.children.length" :index="child.path">
                  <template #title>
                    <el-icon><Folder /></el-icon>
                    <span>{{ child.name }}</span>
                  </template>
                  <el-menu-item
                    v-for="leaf in child.children"
                    :key="leaf.path"
                    :index="leaf.path"
                  >
                    {{ leaf.name }}
                  </el-menu-item>
                </el-sub-menu>
                <el-menu-item v-else :index="child.path">
                  {{ child.name }}
                </el-menu-item>
              </template>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-title">{{ currentTitle }}</div>
        <div class="header-right">
          <el-badge :value="3" :offset="[2, 2]">
            <el-icon :size="18"><Bell /></el-icon>
          </el-badge>
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { moduleMenus } from '../config/menu'

const route = useRoute()
const activePath = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '浮元子医药流向管理系统')
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.layout-aside {
  background-color: #001529;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-title {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.layout-menu {
  border-right: none;
}

.layout-header {
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #303133;
}

.layout-main {
  background: #f0f2f5;
  padding: 16px;
}
</style>
