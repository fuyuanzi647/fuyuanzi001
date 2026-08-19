<template>
  <div class="receivable-page">
    <el-card shadow="never" class="tabs-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="应收明细" name="detail" />
        <el-tab-pane label="应收总览" name="overview" />
      </el-tabs>
    </el-card>

    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <el-button type="primary" @click="openFilter">
          <el-icon><Search /></el-icon>&nbsp;筛选
        </el-button>
      </div>
    </el-card>

    <el-card v-if="filterVisible" shadow="never" class="filter-card">
      <div class="filter-form">
        <div class="filter-item">
          <span class="label">发货单号</span>
          <el-input v-model="query.orderNo" placeholder="请输入发货单号" clearable class="w-180" @keyup.enter="loadData()" />
        </div>
        <div class="filter-item">
          <span class="label">区域</span>
          <el-input v-model="query.region" placeholder="请输入区域" clearable class="w-150" @keyup.enter="loadData()" />
        </div>
        <div class="filter-item">
          <span class="label">商业公司</span>
          <el-select v-model="query.businessId" placeholder="请选择商业公司" clearable filterable class="w-200">
            <el-option v-for="b in businesses" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">记账日期</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="w-260"
          />
        </div>
        <div class="filter-item filter-actions">
          <el-button type="primary" @click="loadData()">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="list-header">
          <span class="list-title">应收总览</span>
          <span class="list-total">共 {{ rows.length }} 家商业公司</span>
        </div>
      </template>

      <div v-loading="loading" class="record-list">
        <el-empty v-if="!loading && rows.length === 0" description="暂无应收数据" />
        <el-table
          v-else
          :data="rows"
          border
          stripe
          size="default"
          class="overview-table"
        >
          <el-table-column prop="region" label="区域" width="110" align="center">
            <template #default="{ row }">{{ row.region || '-' }}</template>
          </el-table-column>
          <el-table-column prop="businessName" label="商业公司名称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="orderCount" label="订单数量" width="90" align="center" />
          <el-table-column label="发货数量" width="110" align="right">
            <template #default="{ row }">{{ formatNum(row.shipQuantity) }}</template>
          </el-table-column>
          <el-table-column label="发货金额(元)" width="130" align="right">
            <template #default="{ row }">{{ formatMoney(row.shipAmount) }}</template>
          </el-table-column>
          <el-table-column label="回款金额(元)" width="130" align="right">
            <template #default="{ row }">
              <span class="paid">{{ formatMoney(row.paidAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="期内还款金额(60天)(元)" width="150" align="right">
            <template #default="{ row }">{{ formatMoney(row.periodPayAmount) }}</template>
          </el-table-column>
          <el-table-column label="应收数量" width="110" align="right">
            <template #default="{ row }">{{ formatNum(row.receivableQuantity) }}</template>
          </el-table-column>
          <el-table-column label="应收总额(元)" width="130" align="right">
            <template #default="{ row }">
              <span class="unpaid">{{ formatMoney(row.receivableAmount) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getReceivableOverview, getPaymentOptions } from '../../api/shipment'

const route = useRoute()
const router = useRouter()
const activeTab = computed(() => route.path.includes('overview') ? 'overview' : 'detail')

const loading = ref(false)
const rows = ref([])
const businesses = ref([])

const query = reactive({
  orderNo: '',
  businessId: null,
  region: ''
})
const dateRange = ref(null)
const filterVisible = ref(false)

function formatMoney(v) {
  const n = Number(v || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function formatNum(v) {
  const n = Number(v || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function handleTabChange(name) {
  if (name === 'overview') {
    router.push('/payment/receivable/overview')
  } else {
    router.push('/payment/receivable/detail')
  }
}

function openFilter() {
  filterVisible.value = !filterVisible.value
}

function resetFilter() {
  query.orderNo = ''
  query.businessId = null
  query.region = ''
  dateRange.value = null
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value && dateRange.value.length === 2) {
      params.shipDateStart = dateRange.value[0]
      params.shipDateEnd = dateRange.value[1]
    }
    const res = await getReceivableOverview(params)
    rows.value = res.data
  } finally {
    loading.value = false
  }
}

async function loadOptions() {
  const res = await getPaymentOptions()
  businesses.value = res.data.businesses || []
}

onMounted(async () => {
  await loadOptions()
  loadData()
})
</script>

<style scoped>
.receivable-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tabs-card {
  margin-bottom: 0;
}

.tabs-card :deep(.el-card__body) {
  padding: 0 16px;
}

.tabs-card :deep(.el-tabs__header) {
  margin: 0;
}

.toolbar-card {
  margin-bottom: 0;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.label {
  color: #606266;
  font-size: 13px;
  margin-right: 4px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-actions {
  gap: 4px;
}

.w-150 {
  width: 150px;
}

.w-180 {
  width: 180px;
}

.w-200 {
  width: 200px;
}

.w-260 {
  width: 260px;
}

.list-card :deep(.el-card__header) {
  padding: 12px 16px;
}

.list-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.list-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.list-total {
  font-size: 12px;
  color: #909399;
}

.record-list {
  min-height: 200px;
}

.paid {
  color: #67c23a;
  font-weight: 600;
}

.unpaid {
  color: #f56c6c;
  font-weight: 600;
}
</style>
