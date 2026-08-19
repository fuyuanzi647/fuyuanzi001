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

        <div class="toolbar-right">
          <span class="label">排序</span>
          <el-select v-model="query.sort" class="w-150" @change="loadData(1)">
            <el-option label="录入倒序" value="createDesc" />
            <el-option label="录入正序" value="createAsc" />
            <el-option label="记账日期正序" value="shipDateAsc" />
            <el-option label="记账日期倒序" value="shipDateDesc" />
            <el-option label="应收天数倒序" value="daysDesc" />
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
          <span class="label">区域</span>
          <el-input v-model="query.region" placeholder="请输入区域" clearable class="w-150" @keyup.enter="loadData(1)" />
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
          <el-button type="primary" @click="loadData(1)">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="list-header">
          <span class="list-title">应收明细</span>
          <span class="list-total">共 {{ total }} 单</span>
        </div>
      </template>

      <div v-loading="loading" class="record-list">
        <el-empty v-if="!loading && records.length === 0" description="暂无应收明细" />
        <el-table
          v-else
          :data="displayRows"
          border
          stripe
          size="default"
          class="receivable-table"
          :span-method="spanMethod"
        >
          <el-table-column label="区域" min-width="100">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'">{{ row.region || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="商业公司名称" min-width="180">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'">{{ row.businessName || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="产品名称" min-width="200">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'">{{ row.productName || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="发货数量" width="110" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'">{{ formatNum(row.shipQuantity) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="发货金额(元)" width="120" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'" class="amount-value">{{ formatMoney(row.shipAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="订单金额(元)" width="120" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'" class="amount-value">{{ formatMoney(row.orderAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="剩余应收金额(元)" width="130" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'" class="amount-value unpaid">{{ formatMoney(row.receivableAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="回款金额(元)" width="120" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'sub'" class="amount-value paid">{{ formatMoney(row.paidAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="期内还款金额(60天)(元)" width="150" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'sub'" class="amount-value">{{ formatMoney(row.periodPayAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="应收数量" width="100" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'">{{ formatNum(row.receivableQuantity) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="应收总额(元)" width="120" align="right">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'" class="amount-value unpaid">{{ formatMoney(row.receivableAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="应收款日期(天)" width="110" align="center">
            <template #default="{ row }">
              <span v-if="row._rowType === 'main'">{{ row.receivableDays }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="130" align="center" fixed="right">
            <template #default="{ row }">
              <el-dropdown v-if="row._rowType === 'main'" trigger="click" @command="(cmd) => handleCommand(cmd, row)">
                <el-button size="small" type="primary">
                  操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="view">查看</el-dropdown-item>
                    <el-dropdown-item command="remark">备注</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="pageTotal" class="total-box">
        <div class="total-title">本页合计</div>
        <div class="total-row">
          <span>发货数量：<b>{{ formatNum(pageTotal.shipQuantity) }}</b></span>
          <span>发货金额：<b>{{ formatMoney(pageTotal.shipAmount) }} 元</b></span>
          <span>订单金额：<b>{{ formatMoney(pageTotal.orderAmount) }} 元</b></span>
          <span>回款金额：<b>{{ formatMoney(pageTotal.paidAmount) }} 元</b></span>
          <span>期内还款：<b>{{ formatMoney(pageTotal.periodPayAmount) }} 元</b></span>
          <span>应收数量：<b>{{ formatNum(pageTotal.receivableQuantity) }}</b></span>
          <span>应收总额：<b class="danger">{{ formatMoney(pageTotal.receivableAmount) }} 元</b></span>
        </div>
      </div>

      <div v-if="queryTotal" class="total-box total-query">
        <div class="total-title">本次查询合计</div>
        <div class="total-row">
          <span>发货数量：<b>{{ formatNum(queryTotal.shipQuantity) }}</b></span>
          <span>发货金额：<b>{{ formatMoney(queryTotal.shipAmount) }} 元</b></span>
          <span>订单金额：<b>{{ formatMoney(queryTotal.orderAmount) }} 元</b></span>
          <span>回款金额：<b>{{ formatMoney(queryTotal.paidAmount) }} 元</b></span>
          <span>期内还款：<b>{{ formatMoney(queryTotal.periodPayAmount) }} 元</b></span>
          <span>应收数量：<b>{{ formatNum(queryTotal.receivableQuantity) }}</b></span>
          <span>应收总额：<b class="danger">{{ formatMoney(queryTotal.receivableAmount) }} 元</b></span>
        </div>
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

    <el-dialog v-model="detailVisible" title="应收明细查看" width="760px" top="6vh" destroy-on-close>
      <div v-if="detail" class="detail-body">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="发货单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ detail.region || '-' }}</el-descriptions-item>
          <el-descriptions-item label="商业公司">{{ detail.businessName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="产品名称">{{ detail.productName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发货数量">{{ formatNum(detail.shipQuantity) }}</el-descriptions-item>
          <el-descriptions-item label="发货金额">{{ formatMoney(detail.shipAmount) }} 元</el-descriptions-item>
          <el-descriptions-item label="订单金额">{{ formatMoney(detail.orderAmount) }} 元</el-descriptions-item>
          <el-descriptions-item label="回款金额">{{ formatMoney(detail.paidAmount) }} 元</el-descriptions-item>
          <el-descriptions-item label="期内还款金额(60天)">{{ formatMoney(detail.periodPayAmount) }} 元</el-descriptions-item>
          <el-descriptions-item label="应收数量">{{ formatNum(detail.receivableQuantity) }}</el-descriptions-item>
          <el-descriptions-item label="应收总额">
            <span class="danger">{{ formatMoney(detail.receivableAmount) }} 元</span>
          </el-descriptions-item>
          <el-descriptions-item label="应收款日期(天)">{{ detail.receivableDays }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <el-dialog v-model="remarkVisible" title="订单备注" width="480px" top="12vh" destroy-on-close>
      <el-input v-model="remarkForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      <template #footer>
        <el-button @click="remarkVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveRemark">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getReceivablePage,
  getPaymentOptions,
  updateShipmentRemark,
  deleteShipment
} from '../../api/shipment'

const route = useRoute()
const router = useRouter()
const activeTab = computed(() => route.path.includes('overview') ? 'overview' : 'detail')

const loading = ref(false)
const saving = ref(false)
const records = ref([])
const total = ref(0)
const pageTotal = ref(null)
const queryTotal = ref(null)
const businesses = ref([])

const query = reactive({
  current: 1,
  size: 10,
  orderNo: '',
  businessId: null,
  region: '',
  sort: 'createDesc'
})
const dateRange = ref(null)
const filterVisible = ref(false)

const detailVisible = ref(false)
const detail = ref(null)
const remarkVisible = ref(false)
const remarkForm = reactive({ orderId: null, orderNo: '', remark: '' })

const displayRows = computed(() => {
  const rows = []
  for (const r of records.value) {
    rows.push({ ...r, _rowType: 'main' })
    rows.push({ ...r, _rowType: 'sub' })
  }
  return rows
})

function spanMethod({ row, columnIndex }) {
  if (row._rowType === 'main') {
    if (columnIndex === 7 || columnIndex === 8) return [1, 0]
  } else {
    if (columnIndex === 0 || columnIndex === 1 || columnIndex === 2 ||
        columnIndex === 3 || columnIndex === 4 || columnIndex === 5 ||
        columnIndex === 6 || columnIndex === 9 || columnIndex === 10 ||
        columnIndex === 11 || columnIndex === 12) return [1, 0]
  }
  return [1, 1]
}

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
  loadData(1)
}

async function loadData(page) {
  if (page) query.current = page
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value && dateRange.value.length === 2) {
      params.shipDateStart = dateRange.value[0]
      params.shipDateEnd = dateRange.value[1]
    }
    const res = await getReceivablePage(params)
    records.value = res.data.records
    total.value = res.data.total
    pageTotal.value = res.data.pageTotal
    queryTotal.value = res.data.queryTotal
  } finally {
    loading.value = false
  }
}

function handleCommand(command, row) {
  switch (command) {
    case 'view':
      openDetail(row)
      break
    case 'remark':
      openRemark(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

function openDetail(row) {
  detail.value = row
  detailVisible.value = true
}

function openRemark(row) {
  remarkForm.orderId = row.orderId
  remarkForm.orderNo = row.orderNo
  remarkForm.remark = row.remark || ''
  remarkVisible.value = true
}

async function handleSaveRemark() {
  saving.value = true
  try {
    await updateShipmentRemark({
      id: remarkForm.orderId,
      remark: remarkForm.remark
    })
    ElMessage.success('备注保存成功')
    remarkVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除发货单 ${row.orderNo} 吗？删除后应收数据将同步移除。`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  await deleteShipment(row.orderId)
  ElMessage.success('删除成功')
  loadData()
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

.receivable-table :deep(.el-table__body tr.sub-row td) {
  background: #fafafa;
}

.amount-value {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.amount-value.paid {
  color: #67c23a;
}

.amount-value.unpaid {
  color: #f56c6c;
}

.total-box {
  margin-top: 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 10px 16px;
  background: #fafafa;
}

.total-query {
  background: #f0f9eb;
  border-color: #e1f3d8;
}

.total-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.total-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 24px;
  font-size: 13px;
  color: #606266;
}

.danger {
  color: #f56c6c;
  font-weight: 600;
}

.detail-body {
  padding: 4px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
