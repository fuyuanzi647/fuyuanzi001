<template>
  <div class="shipment-page">
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <el-button type="primary" @click="openFilter">
          <el-icon><Search /></el-icon>&nbsp;筛选
        </el-button>
        <el-button type="success" @click="openCreate">
          <el-icon><Plus /></el-icon>&nbsp;新增
        </el-button>

        <div class="toolbar-right">
          <span class="label">状态</span>
          <el-select v-model="query.status" placeholder="全部状态" clearable class="w-130" @change="loadData(1)">
            <el-option label="全部状态" :value="null" />
            <el-option label="未回款" :value="0" />
            <el-option label="部分回款" :value="1" />
            <el-option label="已回款" :value="2" />
          </el-select>

          <span class="label">排序</span>
          <el-select v-model="query.sort" class="w-150" @change="loadData(1)">
            <el-option label="录入倒序" value="createDesc" />
            <el-option label="录入正序" value="createAsc" />
            <el-option label="记账日期正序" value="shipDateAsc" />
            <el-option label="记账日期倒序" value="shipDateDesc" />
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
          <span class="list-title">发货订单</span>
          <span class="list-total">共 {{ total }} 单</span>
        </div>
      </template>

      <div v-loading="loading" class="order-list">
        <el-empty v-if="!loading && orders.length === 0" description="暂无发货订单" />
        <el-table
          v-else
          :data="orders"
          border
          stripe
          size="default"
          class="shipment-table"
          :row-class-name="rowClassName"
        >
          <el-table-column type="expand">
            <template #default="{ row }">
              <div class="expand-detail">
                <div class="expand-title">发货明细</div>
                <el-table :data="row.items" size="small" border>
                  <el-table-column type="index" label="#" width="50" align="center" />
                  <el-table-column prop="manufacturerName" label="出库厂家" min-width="160" />
                  <el-table-column prop="productName" label="产品名称" min-width="160" />
                  <el-table-column prop="quantity" label="数量" width="100" align="right">
                    <template #default="{ row: item }">{{ formatNum(item.quantity) }}</template>
                  </el-table-column>
                  <el-table-column prop="batchNo" label="批号" width="120" align="center" />
                  <el-table-column prop="amount" label="金额" width="120" align="right">
                    <template #default="{ row: item }">{{ formatMoney(item.amount) }}</template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="orderNo" label="发货单号" min-width="150">
            <template #default="{ row }">
              <span class="order-no">{{ row.orderNo }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="businessName" label="商业名称" min-width="170" show-overflow-tooltip />

          <el-table-column label="回款状态" width="170" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.status === 2" type="success" effect="dark" size="large" class="status-paid">
                已回款
              </el-tag>
              <el-tag v-else-if="row.status === 1" type="danger" effect="dark" size="large" class="status-partial">
                部分回款 · {{ row.overdueDays >= 0 ? row.overdueDays : 0 }} 天
              </el-tag>
              <el-tag v-else type="danger" effect="dark" size="large" class="status-unpaid">
                未回款 · {{ row.overdueDays >= 0 ? row.overdueDays : 0 }} 天
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="totalAmount" label="订单合计" width="120" align="right">
            <template #default="{ row }">
              <span class="amount-value">{{ formatMoney(row.totalAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="paidAmount" label="已回金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount-value paid">{{ formatMoney(row.paidAmount) }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="paidQuantity" label="已回数量" width="100" align="right">
            <template #default="{ row }">{{ formatNum(row.paidQuantity) }}</template>
          </el-table-column>

          <el-table-column prop="shipDate" label="记账日期" width="110" align="center">
            <template #default="{ row }">{{ row.shipDate || '-' }}</template>
          </el-table-column>

          <el-table-column label="操作" width="240" align="center" fixed="right">
            <template #default="{ row }">
              <div class="order-actions">
                <el-button size="small" @click="openDetail(row)">查看</el-button>
                <el-button size="small" type="primary" plain @click="openEdit(row)">修改</el-button>
                <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
                <el-button v-if="row.status !== 2" size="small" type="warning" @click="openPayment(row)">
                  添加回款
                </el-button>
              </div>
            </template>
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

    <el-dialog v-model="editVisible" :title="form.id ? '修改发货订单' : '新增发货订单'" width="760px" top="6vh" destroy-on-close>
      <el-form ref="editFormRef" :model="form" :rules="editRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发货单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="请输入发货单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商业公司" prop="businessId">
              <el-select v-model="form.businessId" placeholder="请选择" filterable class="w-full">
                <el-option v-for="b in businesses" :key="b.id" :label="b.name" :value="b.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="记账日期" prop="shipDate">
          <el-date-picker v-model="form.shipDate" type="date" placeholder="请选择记账日期" value-format="YYYY-MM-DD" class="w-full" />
        </el-form-item>
        <el-form-item label="发货明细">
          <div class="items-editor">
            <div v-for="(item, index) in form.items" :key="index" class="item-editor-row">
              <el-select v-model="item.manufacturerId" placeholder="出库厂家" filterable class="w-170">
                <el-option v-for="m in manufacturers" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
              <el-select v-model="item.productId" placeholder="产品" filterable class="w-170">
                <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
              <el-input-number v-model="item.quantity" :min="0" :precision="2" placeholder="数量" class="w-110" controls-position="right" />
              <el-input v-model="item.batchNo" placeholder="批号" class="w-110" />
              <el-input-number v-model="item.amount" :min="0" :precision="2" placeholder="金额" class="w-130" controls-position="right" />
              <el-button type="danger" text @click="removeItem(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button type="primary" text @click="addItem">
              <el-icon><Plus /></el-icon> 添加明细
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="发货订单详情" width="820px" top="6vh" destroy-on-close>
      <div v-if="detail" class="detail-body">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="发货单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="商业公司">{{ detail.businessName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="记账日期">{{ detail.shipDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回款状态">
            <el-tag v-if="detail.status === 2" type="success">已回款</el-tag>
            <el-tag v-else-if="detail.status === 1" type="danger">部分回款</el-tag>
            <el-tag v-else type="danger">未回款</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单合计">{{ formatMoney(detail.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="已回金额">{{ formatMoney(detail.paidAmount) }}</el-descriptions-item>
          <el-descriptions-item label="已回数量">{{ formatNum(detail.paidQuantity) }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section-title">发货明细</div>
        <el-table :data="detail.items" size="small" border>
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column prop="manufacturerName" label="出库厂家" min-width="150" />
          <el-table-column prop="productName" label="产品名称" min-width="150" />
          <el-table-column prop="quantity" label="数量" width="90" align="right">
            <template #default="{ row }">{{ formatNum(row.quantity) }}</template>
          </el-table-column>
          <el-table-column prop="batchNo" label="批号" width="110" align="center" />
          <el-table-column prop="amount" label="金额" width="110" align="right">
            <template #default="{ row }">{{ formatMoney(row.amount) }}</template>
          </el-table-column>
        </el-table>

        <div class="detail-section-title">回款记录</div>
        <el-table :data="payments" size="small" border v-loading="paymentLoading">
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column prop="payDate" label="回款日期" width="110" align="center" />
          <el-table-column prop="amount" label="回款金额" width="120" align="right">
            <template #default="{ row }">{{ formatMoney(row.amount) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="回款数量" width="100" align="right">
            <template #default="{ row }">{{ formatNum(row.quantity) }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="140" />
        </el-table>
      </div>
    </el-dialog>

    <el-dialog v-model="paymentVisible" title="添加回款" width="480px" destroy-on-close>
      <el-form ref="paymentFormRef" :model="paymentForm" :rules="paymentRules" label-width="90px">
        <el-form-item label="订单">
          <span class="payment-order-no">{{ paymentForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="未回金额">
          <span class="remain-amount">{{ formatMoney(paymentForm.remainAmount) }}</span>
        </el-form-item>
        <el-form-item label="回款金额" prop="amount">
          <el-input-number v-model="paymentForm.amount" :min="0.01" :precision="2" :max="paymentForm.remainAmount" class="w-full" controls-position="right" />
        </el-form-item>
        <el-form-item label="回款数量">
          <el-input-number v-model="paymentForm.quantity" :min="0" :precision="2" class="w-full" controls-position="right" />
        </el-form-item>
        <el-form-item label="回款日期" prop="payDate">
          <el-date-picker v-model="paymentForm.payDate" type="date" placeholder="请选择回款日期" value-format="YYYY-MM-DD" class="w-full" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="paymentForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleAddPayment">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getShipmentPage,
  getShipmentDetail,
  createShipment,
  updateShipment,
  deleteShipment,
  addShipmentPayment,
  getOrderPayments,
  getPaymentOptions
} from '../../api/shipment'

const loading = ref(false)
const saving = ref(false)
const orders = ref([])
const total = ref(0)
const businesses = ref([])
const manufacturers = ref([])
const products = ref([])

const query = reactive({
  current: 1,
  size: 10,
  orderNo: '',
  businessId: null,
  status: null,
  sort: 'createDesc'
})
const dateRange = ref(null)
const filterVisible = ref(false)

const editVisible = ref(false)
const editFormRef = ref()
const detailVisible = ref(false)
const detail = ref(null)
const payments = ref([])
const paymentLoading = ref(false)
const paymentVisible = ref(false)
const paymentFormRef = ref()

const emptyItem = () => ({
  manufacturerId: null,
  productId: null,
  quantity: 0,
  batchNo: '',
  amount: 0
})

const form = reactive({
  id: null,
  orderNo: '',
  businessId: null,
  shipDate: '',
  remark: '',
  items: []
})

const editRules = {
  orderNo: [{ required: true, message: '请输入发货单号', trigger: 'blur' }],
  businessId: [{ required: true, message: '请选择商业公司', trigger: 'change' }],
  shipDate: [{ required: true, message: '请选择记账日期', trigger: 'change' }]
}

const paymentForm = reactive({
  orderId: null,
  orderNo: '',
  remainAmount: 0,
  amount: 0,
  quantity: 0,
  payDate: '',
  remark: ''
})

const paymentRules = {
  amount: [{ required: true, message: '请输入回款金额', trigger: 'blur' }],
  payDate: [{ required: true, message: '请选择回款日期', trigger: 'change' }]
}

function formatMoney(v) {
  const n = Number(v || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function formatNum(v) {
  const n = Number(v || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function rowClassName({ row }) {
  return 'order-status-' + row.status
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
    const res = await getShipmentPage(params)
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openFilter() {
  filterVisible.value = !filterVisible.value
}

function resetFilter() {
  query.orderNo = ''
  query.businessId = null
  query.status = null
  dateRange.value = null
  loadData(1)
}

function openCreate() {
  Object.assign(form, {
    id: null,
    orderNo: '',
    businessId: null,
    shipDate: '',
    remark: '',
    items: [{ ...emptyItem() }]
  })
  editVisible.value = true
}

function openEdit(order) {
  Object.assign(form, {
    id: order.id,
    orderNo: order.orderNo,
    businessId: order.businessId,
    shipDate: order.shipDate,
    remark: order.remark,
    items: order.items.map(i => ({
      id: i.id,
      manufacturerId: i.manufacturerId,
      productId: i.productId,
      quantity: i.quantity,
      batchNo: i.batchNo,
      amount: i.amount
    }))
  })
  editVisible.value = true
}

function addItem() {
  form.items.push({ ...emptyItem() })
}

function removeItem(index) {
  if (form.items.length <= 1) {
    ElMessage.warning('至少保留一条明细')
    return
  }
  form.items.splice(index, 1)
}

async function handleSave() {
  if (!form.items.length) {
    ElMessage.warning('请添加发货明细')
    return
  }
  if (form.items.some(i => !i.manufacturerId || !i.productId || !i.quantity || !i.amount)) {
    ElMessage.warning('请填写完整的发货明细')
    return
  }
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (form.id) {
        await updateShipment({ ...form })
        ElMessage.success('修改成功')
      } else {
        await createShipment({ ...form })
        ElMessage.success('新增成功')
      }
      editVisible.value = false
      loadData()
    } finally {
      saving.value = false
    }
  })
}

async function openDetail(order) {
  detailVisible.value = true
  detail.value = null
  payments.value = []
  const res = await getShipmentDetail(order.id)
  detail.value = res.data
  paymentLoading.value = true
  try {
    const pRes = await getOrderPayments(order.id)
    payments.value = pRes.data
  } finally {
    paymentLoading.value = false
  }
}

async function handleDelete(order) {
  try {
    await ElMessageBox.confirm(`确定删除发货单 ${order.orderNo} 吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  await deleteShipment(order.id)
  ElMessage.success('删除成功')
  loadData()
}

function openPayment(order) {
  Object.assign(paymentForm, {
    orderId: order.id,
    orderNo: order.orderNo,
    remainAmount: (Number(order.totalAmount || 0) - Number(order.paidAmount || 0)),
    amount: 0,
    quantity: 0,
    payDate: '',
    remark: ''
  })
  paymentVisible.value = true
}

async function handleAddPayment() {
  await paymentFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      await addShipmentPayment({
        orderId: paymentForm.orderId,
        amount: paymentForm.amount,
        quantity: paymentForm.quantity,
        payDate: paymentForm.payDate,
        remark: paymentForm.remark
      })
      ElMessage.success('回款添加成功')
      paymentVisible.value = false
      loadData()
    } finally {
      saving.value = false
    }
  })
}

async function loadOptions() {
  const res = await getPaymentOptions()
  businesses.value = res.data.businesses || []
  manufacturers.value = res.data.manufacturers || []
  products.value = res.data.products || []
}

onMounted(async () => {
  await loadOptions()
  loadData(1)
})
</script>

<style scoped>
.shipment-page {
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

.w-130 {
  width: 130px;
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

.w-110 {
  width: 110px;
}

.w-170 {
  width: 170px;
}

.w-full {
  width: 100%;
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

.order-grid {
  min-height: 200px;
}

.order-list {
  min-height: 200px;
}

.shipment-table {
  width: 100%;
}

.shipment-table :deep(.order-status-0) {
  --el-table-tr-bg-color: #fef0f0;
}

.shipment-table :deep(.order-status-0:hover > td) {
  --el-table-tr-bg-color: #fde2e2;
}

.shipment-table :deep(.order-status-1) {
  --el-table-tr-bg-color: #fdf6ec;
}

.shipment-table :deep(.order-status-1:hover > td) {
  --el-table-tr-bg-color: #faecd8;
}

.order-no {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.amount-value {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.amount-value.paid {
  color: #67c23a;
}

.expand-detail {
  padding: 12px 16px;
  background: #fafafa;
}

.expand-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
}

.status-paid {
  min-width: 90px;
}

.status-partial {
  min-width: 150px;
}

.status-unpaid {
  min-width: 140px;
}

.order-actions {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
  justify-content: center;
}

.items-editor {
  width: 100%;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px;
}

.item-editor-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.item-editor-row:last-of-type {
  margin-bottom: 0;
}

.detail-body {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 8px;
}

.payment-order-no {
  color: #303133;
  font-weight: 600;
}

.remain-amount {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
