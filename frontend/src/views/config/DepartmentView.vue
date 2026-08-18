<template>
  <div class="department-page">
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <el-button type="primary" @click="filterVisible = !filterVisible">
          <el-icon><Search /></el-icon>&nbsp;筛选
        </el-button>
        <el-button type="success" @click="openCreate">
          <el-icon><Plus /></el-icon>&nbsp;新增
        </el-button>
        <el-button type="warning" @click="handleImport">
          <el-icon><Upload /></el-icon>&nbsp;导入
        </el-button>
        <el-button type="info" @click="handleExport">
          <el-icon><Download /></el-icon>&nbsp;导出
        </el-button>

        <div class="toolbar-right">
          <span class="label">状态</span>
          <el-select v-model="query.status" placeholder="全部状态" clearable class="w-130" @change="loadData(1)">
            <el-option label="全部状态" :value="null" />
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </div>
      </div>
    </el-card>

    <el-card v-if="filterVisible" shadow="never" class="filter-card">
      <div class="filter-form">
        <div class="filter-item">
          <span class="label">部门名称</span>
          <el-input v-model="query.name" placeholder="请输入部门名称" clearable class="w-180" @keyup.enter="loadData(1)" />
        </div>
        <div class="filter-item">
          <span class="label">部门类型</span>
          <el-select v-model="query.type" placeholder="请选择类型" clearable class="w-160">
            <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
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
          <span class="list-title">部门列表</span>
          <span class="list-total">共 {{ total }} 个部门</span>
        </div>
      </template>

      <div v-loading="loading" class="department-list">
        <el-empty v-if="!loading && departments.length === 0" description="暂无部门数据" />
        <el-table v-else :data="departments" border stripe size="default">
          <el-table-column prop="name" label="部门名称" min-width="200">
            <template #default="{ row }">
              <span class="dept-name">{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="部门类型" width="140" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.type" :type="row.type === '区域' ? 'warning' : 'primary'" effect="plain">
                {{ row.type }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="dark">
                {{ row.status === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link type="primary" @click="openDetail(row)">查看</el-button>
              <el-button size="small" link type="primary" @click="openEdit(row)">修改</el-button>
              <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="editVisible" :title="form.id ? '修改部门' : '新增部门'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择部门类型" class="w-full">
            <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="同级排序">
          <el-input-number v-model="form.sort" :min="0" :max="9999" class="w-full" controls-position="right" />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择上级部门" clearable filterable class="w-full">
            <el-option label="顶级部门" :value="0" />
            <el-option
              v-for="d in parentOptions"
              :key="d.id"
              :label="d.name"
              :value="d.id"
              :disabled="d.id === form.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="部门详情" width="520px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="部门名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="部门类型">{{ detail.type || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'danger'" effect="dark">
            {{ detail.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="同级排序">{{ detail.sort ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="上级部门">{{ detail.parentName || '顶级部门' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <input ref="fileInput" type="file" accept=".csv" style="display: none" @change="onFileChange" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Upload, Download } from '@element-plus/icons-vue'
import {
  getDepartmentPage,
  getDepartmentList,
  getDepartmentDetail,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  exportDepartment,
  importDepartment
} from '../../api/department'

const typeOptions = ['区域', '部门', '办事处']

const filterVisible = ref(false)
const loading = ref(false)
const saving = ref(false)
const departments = ref([])
const allDepartments = ref([])
const total = ref(0)

const query = reactive({
  current: 1,
  size: 10,
  name: '',
  type: null,
  status: null
})

const formRef = ref()
const fileInput = ref()
const editVisible = ref(false)
const detailVisible = ref(false)
const detail = ref({})

const form = reactive({
  id: null,
  name: '',
  type: '',
  status: 1,
  sort: 0,
  parentId: 0,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择部门类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const parentOptions = ref([])

function openCreate() {
  Object.assign(form, {
    id: null,
    name: '',
    type: '',
    status: 1,
    sort: 0,
    parentId: 0,
    remark: ''
  })
  buildParentOptions()
  editVisible.value = true
}

function openEdit(row) {
  Object.assign(form, {
    id: row.id,
    name: row.name,
    type: row.type,
    status: row.status,
    sort: row.sort ?? 0,
    parentId: row.parentId ?? 0,
    remark: row.remark
  })
  buildParentOptions()
  editVisible.value = true
}

async function openDetail(row) {
  const res = await getDepartmentDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
}

function buildParentOptions() {
  parentOptions.value = allDepartments.value.filter(d => d.id !== form.id)
}

async function loadData(page) {
  if (page) query.current = page
  loading.value = true
  try {
    const params = { ...query }
    if (!params.type) params.type = null
    if (params.status === null || params.status === undefined || params.status === '') delete params.status
    const res = await getDepartmentPage(params)
    departments.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadOptions() {
  const res = await getDepartmentList()
  allDepartments.value = res.data || []
}

function resetFilter() {
  query.name = ''
  query.type = null
  query.status = null
  loadData(1)
}

async function handleSave() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (form.id) {
        await updateDepartment({ ...form })
      } else {
        await createDepartment({ ...form })
      }
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      editVisible.value = false
      await loadOptions()
      loadData()
    } finally {
      saving.value = false
    }
  })
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除部门「${row.name}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  await deleteDepartment(row.id)
  ElMessage.success('删除成功')
  await loadOptions()
  loadData()
}

async function handleExport() {
  const res = await exportDepartment()
  const { fileName, content } = res.data
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

function handleImport() {
  fileInput.value.value = ''
  fileInput.value.click()
}

async function onFileChange(event) {
  const file = event.target.files[0]
  if (!file) return
  if (!file.name.toLowerCase().endsWith('.csv')) {
    ElMessage.error('请选择 CSV 文件')
    return
  }
  const content = await file.text()
  await importDepartment(content)
  ElMessage.success('导入成功')
  await loadOptions()
  loadData()
}

onMounted(async () => {
  await loadOptions()
  loadData(1)
})
</script>

<style scoped>
.department-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
  font-size: 14px;
}

.w-full {
  width: 100%;
}

.w-130 {
  width: 130px;
}

.w-160 {
  width: 160px;
}

.w-180 {
  width: 180px;
}

.filter-card {
  padding: 4px 8px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-actions {
  margin-left: auto;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.list-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.list-total {
  font-size: 13px;
  color: #909399;
}

.dept-name {
  font-weight: 500;
  color: #303133;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
