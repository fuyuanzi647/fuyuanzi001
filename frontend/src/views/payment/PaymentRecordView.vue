<template>
  <div class="payment-record-page">
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <el-button type="primary" @click="filterVisible = !filterVisible">
          <el-icon><Search /></el-icon>&nbsp;筛选
        </el-button>

        <div class="toolbar-right">
          <span class="label">排序</span>
          <el-select v-model="query.sort" class="w-150" @change="loadData(1)">
            <el-option label="回款日期倒序" value="payDateDesc" />
            <el-option label="回款日期正序" value="payDateAsc" />
            <el-option label="录入正序" value="createAsc" />
            <el-option label="金额倒序" value="amountDesc" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card v-if="filterVisible" shadow="never" class="filter-card">
      <div class="filter-form">
        <div class="filter-item">
          <span class="label">发货单号</span>
          <el-input v-model="query.orderNo" placeholder="请输入发货单号" clearable class="w-180" @keyup.enter="loadData(1)" />
        </div>
        <div class="filter-item">
          <span class="label">商业公司</span>
          <el-select v-model="query.businessId" placeholder="请选择商业公司" clearable filterable class="w-200">
            <el-option v-for="b in businesses" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">回款日期</span>
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
        <div class="filter-item">
          <span class="label">回款方式</span>
          <el-select v-model="query.payMethod" placeholder="全部方式" clearable class="w-150">
            <el-option label="银行转账" value="银行转账" />
            <el-option label="银行承兑" value="银行承兑" />
            <el-option label="商业承兑" value="商业承兑" />
            <el-option label="现金" value="现金" />
            <el-option label="其他" value="其他" />
          </el-select>
        </div>
        <div class="filter-item filter-actions">
          <el-button type="primary" @click="loadData(1)">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="list-header">
          <span class="list-title">回款记录</span>
          <span class="list-total">共 {{ total }} 条</span>
        </div>
      </template>

      <div v-loading="loading" class="record-list">
        <el-empty v-if="!loading && records.length === 0" description="暂无回款记录" />
        <el-table
          v-else
          :data="records"
          border
          stripe
          size="default"
          class="record-table"
        >
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column prop="payDate" label="回款日期" width="110" align="center" />
          <el-table-column prop="orderNo" label="发货单号" width="150" show-overflow-tooltip />
          <el-table-column prop="businessName" label="商业公司" min-width="160" show-overflow-tooltip />
          <el-table-column prop="productName" label="产品名称" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">{{ row.productName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="amount" label="回款金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount-value">{{ formatMoney(row.amount) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="回款数量" width="100" align="right">
            <template #default="{ row }">{{ formatNum(row.quantity) }}</template>
          </el-table-column>
          <el-table-column prop="officeDate" label="办事处日期" width="110" align="center">
            <template #default="{ row }">{{ row.officeDate || '-' }}</template>
          </el-table-column>
          <el-table-column prop="payMethod" label="回款方式" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.payMethod" effect="plain">{{ row.payMethod }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip>
            <template #default="{ row }">{{ row.remark || '-' }}</template>
          </el-table-column>
          <el-table-column prop="createTime" label="录入时间" width="170" align="center">
            <template #default="{ row }">{{ row.createTime || '-' }}</template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadData(1)"
          @current-change="loadData()"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPaymentRecordPage, getPaymentOptions } from '../../api/shipment'

const loading = ref(false)
const records = ref([])
const total = ref(0)
const businesses = ref([])

const query = reactive({
  current: 1,
  size: 10,
  orderNo: '',
  businessId: null,
  payMethod: null,
  sort: 'payDateDesc'
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

async function loadData(page) {
  if (page) query.current = page
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value && dateRange.value.length === 2) {
      params.payDateStart = dateRange.value[0]
      params.payDateEnd = dateRange.value[1]
    }
    const res = await getPaymentRecordPage(params)
    records.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function resetFilter() {
  query.orderNo = ''
  query.businessId = null
  query.payMethod = null
  dateRange.value = null
  loadData(1)
}

async function loadOptions() {
  const res = await getPaymentOptions()
  businesses.value = res.data.businesses || []
}

onMounted(async () => {
  await loadOptions()
  loadData(1)
})
</script>

<style scoped>
.payment-record-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toolbar-card {
  margin-bottom: 0;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toolbar-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
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

.amount-value {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
