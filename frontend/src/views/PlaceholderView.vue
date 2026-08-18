<template>
  <div class="placeholder-page">
    <el-card shadow="never" class="module-card">
      <template #header>
        <div class="card-header">
          <el-icon :size="20" color="#4096ff"><Grid /></el-icon>
          <span class="module-name">{{ meta.title }}</span>
          <el-tag v-if="meta.moduleName" size="small" type="info">
            {{ meta.moduleName }}
          </el-tag>
        </div>
      </template>

      <el-descriptions v-if="moduleInfo" :column="1" border class="module-desc">
        <el-descriptions-item label="模块编码">{{ moduleInfo.code }}</el-descriptions-item>
        <el-descriptions-item label="模块名称">{{ moduleInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="模块说明">{{ moduleInfo.description }}</el-descriptions-item>
        <el-descriptions-item label="功能清单">
          <el-tag
            v-for="feature in moduleInfo.features"
            :key="feature"
            size="small"
            class="feature-tag"
          >
            {{ feature }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-empty description="功能开发中，当前为框架占位页面" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getModuleSummary } from '../api'

const route = useRoute()
const meta = route.meta
const moduleInfo = ref(null)

onMounted(async () => {
  if (meta.apiPrefix) {
    try {
      const res = await getModuleSummary(meta.apiPrefix)
      moduleInfo.value = res.data
    } catch (e) {
      moduleInfo.value = null
    }
  }
})
</script>

<style scoped>
.module-card {
  min-height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.module-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.module-desc {
  margin-bottom: 24px;
}

.feature-tag {
  margin-right: 8px;
}
</style>
