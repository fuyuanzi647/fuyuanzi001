<template>
  <div class="user-page">
    <el-tabs v-model="activeTab" class="user-tabs" @tab-change="onTabChange">
      <el-tab-pane label="用户管理" name="user">
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
            <el-dropdown trigger="click" @command="handleBatchCommand">
              <el-button>
                操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="batchDelete" :disabled="!userSelection.length">批量删除</el-dropdown-item>
                  <el-dropdown-item command="batchTransfer" :disabled="!userSelection.length">批量调岗</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-card>

        <el-card v-if="filterVisible" shadow="never" class="filter-card">
          <div class="filter-form">
            <div class="filter-item">
              <span class="label">姓名</span>
              <el-input v-model="userQuery.name" placeholder="请输入姓名" clearable class="w-160" @keyup.enter="loadUsers(1)" />
            </div>
            <div class="filter-item">
              <span class="label">登录账户</span>
              <el-input v-model="userQuery.username" placeholder="请输入登录账户" clearable class="w-160" @keyup.enter="loadUsers(1)" />
            </div>
            <div class="filter-item">
              <span class="label">状态</span>
              <el-select v-model="userQuery.status" placeholder="全部" clearable class="w-130" @change="loadUsers(1)">
                <el-option label="在职" :value="1" />
                <el-option label="离职" :value="0" />
              </el-select>
            </div>
            <div class="filter-item">
              <span class="label">当前部门</span>
              <el-select v-model="userQuery.departmentId" placeholder="全部" clearable filterable class="w-180">
                <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </div>
            <div class="filter-item">
              <span class="label">当前岗位</span>
              <el-select v-model="userQuery.positionId" placeholder="全部" clearable filterable class="w-180">
                <el-option v-for="p in positions" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </div>
            <div class="filter-item filter-actions">
              <el-button type="primary" @click="loadUsers(1)">查询</el-button>
              <el-button @click="resetUserFilter">重置</el-button>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="list-card">
          <template #header>
            <div class="list-header">
              <span class="list-title">用户列表</span>
              <span class="list-total">共 {{ userTotal }} 人</span>
            </div>
          </template>
          <div v-loading="userLoading" class="user-list">
            <el-empty v-if="!userLoading && users.length === 0" description="暂无用户数据" />
            <el-table v-else :data="users" border stripe @selection-change="(rows) => (userSelection = rows)">
              <el-table-column type="selection" width="45" align="center" />
              <el-table-column prop="realName" label="姓名" min-width="110">
                <template #default="{ row }">
                  <span class="name-cell">{{ row.realName }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="username" label="登录账户" min-width="120" show-overflow-tooltip />
              <el-table-column prop="phone" label="手机号" width="130" align="center">
                <template #default="{ row }">{{ row.phone || '-' }}</template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="dark">
                    {{ row.status === 1 ? '在职' : '离职' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="hireDate" label="入职日期" width="110" align="center">
                <template #default="{ row }">{{ row.hireDate || '-' }}</template>
              </el-table-column>
              <el-table-column prop="positionName" label="当前岗位" min-width="110">
                <template #default="{ row }">{{ row.positionName || '-' }}</template>
              </el-table-column>
              <el-table-column prop="departmentName" label="当前部门" min-width="130">
                <template #default="{ row }">{{ row.departmentName || '-' }}</template>
              </el-table-column>
              <el-table-column prop="employeeType" label="员工属性" width="100" align="center">
                <template #default="{ row }">{{ row.employeeType || '-' }}</template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" link type="primary" @click="openUserDetail(row)">查看</el-button>
                  <el-button size="small" link type="primary" @click="openEdit(row)">修改</el-button>
                  <el-button size="small" link type="warning" @click="openTransfer(row)">调岗</el-button>
                  <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="userQuery.current"
              v-model:page-size="userQuery.size"
              :total="userTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              background
              @size-change="loadUsers(1)"
              @current-change="loadUsers()"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="岗位档案" name="position">
        <el-card shadow="never" class="toolbar-card">
          <div class="toolbar">
            <el-button type="primary" @click="posFilterVisible = !posFilterVisible">
              <el-icon><Search /></el-icon>&nbsp;筛选
            </el-button>
            <el-button type="success" @click="openPosCreate">
              <el-icon><Plus /></el-icon>&nbsp;新增
            </el-button>
            <el-button type="warning" @click="handlePosImport">
              <el-icon><Upload /></el-icon>&nbsp;导入
            </el-button>
            <el-button type="info" @click="handlePosExport">
              <el-icon><Download /></el-icon>&nbsp;导出
            </el-button>
          </div>
        </el-card>

        <el-card v-if="posFilterVisible" shadow="never" class="filter-card">
          <div class="filter-form">
            <div class="filter-item">
              <span class="label">岗位名称</span>
              <el-input v-model="posQuery.name" placeholder="请输入岗位名称" clearable class="w-180" @keyup.enter="loadPositions(1)" />
            </div>
            <div class="filter-item">
              <span class="label">状态</span>
              <el-select v-model="posQuery.status" placeholder="全部" clearable class="w-130" @change="loadPositions(1)">
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
              </el-select>
            </div>
            <div class="filter-item filter-actions">
              <el-button type="primary" @click="loadPositions(1)">查询</el-button>
              <el-button @click="resetPosFilter">重置</el-button>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="list-card">
          <template #header>
            <div class="list-header">
              <span class="list-title">岗位列表</span>
              <span class="list-total">共 {{ posTotal }} 个岗位</span>
            </div>
          </template>
          <div v-loading="posLoading" class="user-list">
            <el-empty v-if="!posLoading && positions.length === 0" description="暂无岗位数据" />
            <el-table v-else :data="positions" border stripe>
              <el-table-column prop="name" label="岗位名称" min-width="160" />
              <el-table-column prop="code" label="岗位编码" min-width="140">
                <template #default="{ row }">{{ row.code || '-' }}</template>
              </el-table-column>
              <el-table-column prop="sort" label="排序" width="90" align="center" />
              <el-table-column prop="status" label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="dark">
                    {{ row.status === 1 ? '启用' : '停用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" link type="primary" @click="openPosDetail(row)">查看</el-button>
                  <el-button size="small" link type="primary" @click="openPosEdit(row)">修改</el-button>
                  <el-button size="small" link type="danger" @click="handlePosDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="posQuery.current"
              v-model:page-size="posQuery.size"
              :total="posTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              background
              @size-change="loadPositions(1)"
              @current-change="loadPositions()"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="业务员档案" name="salesman">
        <el-card shadow="never" class="toolbar-card">
          <div class="toolbar">
            <el-button type="primary" @click="salesmanFilterVisible = !salesmanFilterVisible">
              <el-icon><Search /></el-icon>&nbsp;筛选
            </el-button>
            <el-button type="success" @click="openSalesmanCreate">
              <el-icon><Plus /></el-icon>&nbsp;新增
            </el-button>
            <el-button type="warning" @click="handleSalesmanImport">
              <el-icon><Upload /></el-icon>&nbsp;导入
            </el-button>
            <el-button type="info" @click="handleSalesmanExport">
              <el-icon><Download /></el-icon>&nbsp;导出
            </el-button>
          </div>
        </el-card>

        <el-card v-if="salesmanFilterVisible" shadow="never" class="filter-card">
          <div class="filter-form">
            <div class="filter-item">
              <span class="label">姓名</span>
              <el-input v-model="salesmanQuery.name" placeholder="请输入姓名" clearable class="w-160" @keyup.enter="loadSalesmen(1)" />
            </div>
            <div class="filter-item">
              <span class="label">状态</span>
              <el-select v-model="salesmanQuery.status" placeholder="全部" clearable class="w-130" @change="loadSalesmen(1)">
                <el-option label="在职" :value="1" />
                <el-option label="离职" :value="0" />
              </el-select>
            </div>
            <div class="filter-item">
              <span class="label">部门</span>
              <el-select v-model="salesmanQuery.departmentId" placeholder="全部" clearable filterable class="w-180">
                <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </div>
            <div class="filter-item filter-actions">
              <el-button type="primary" @click="loadSalesmen(1)">查询</el-button>
              <el-button @click="resetSalesmanFilter">重置</el-button>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="list-card">
          <template #header>
            <div class="list-header">
              <span class="list-title">业务员列表</span>
              <span class="list-total">共 {{ salesmanTotal }} 人</span>
            </div>
          </template>
          <div v-loading="salesmanLoading" class="user-list">
            <el-empty v-if="!salesmanLoading && salesmen.length === 0" description="暂无业务员数据" />
            <el-table v-else :data="salesmen" border stripe>
              <el-table-column prop="name" label="姓名" min-width="110">
                <template #default="{ row }">
                  <span class="name-cell">{{ row.name }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="phone" label="手机号" width="130" align="center">
                <template #default="{ row }">{{ row.phone || '-' }}</template>
              </el-table-column>
              <el-table-column prop="positionName" label="当前岗位" min-width="120">
                <template #default="{ row }">{{ row.positionName || '-' }}</template>
              </el-table-column>
              <el-table-column prop="departmentName" label="当前部门" min-width="130">
                <template #default="{ row }">{{ row.departmentName || '-' }}</template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="dark">
                    {{ row.status === 1 ? '在职' : '离职' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" link type="primary" @click="openSalesmanDetail(row)">查看</el-button>
                  <el-button size="small" link type="primary" @click="openSalesmanEdit(row)">修改</el-button>
                  <el-button size="small" link type="danger" @click="handleSalesmanDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="salesmanQuery.current"
              v-model:page-size="salesmanQuery.size"
              :total="salesmanTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              background
              @size-change="loadSalesmen(1)"
              @current-change="loadSalesmen()"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="editVisible" :title="form.id ? '修改用户' : '新增用户'" width="540px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="登录账户" prop="username">
          <el-input v-model="form.username" placeholder="请输入登录账户" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="初始密码">
          <el-input v-model="form.password" type="password" placeholder="默认 123456" show-password />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="入职日期">
          <el-date-picker v-model="form.hireDate" type="date" placeholder="请选择入职日期" value-format="YYYY-MM-DD" class="w-full" />
        </el-form-item>
        <el-form-item label="当前岗位">
          <el-select v-model="form.positionId" placeholder="请选择岗位" clearable filterable class="w-full">
            <el-option v-for="p in positions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前部门">
          <el-select v-model="form.departmentId" placeholder="请选择部门" clearable filterable class="w-full">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="员工属性">
          <el-select v-model="form.employeeType" placeholder="请选择" clearable class="w-full">
            <el-option v-for="t in employeeTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="transferVisible" title="调岗" width="460px">
      <div class="transfer-tip">为「{{ transferForm.realName }}」调整岗位与部门</div>
      <el-form :model="transferForm" label-width="90px">
        <el-form-item label="当前岗位">
          <el-select v-model="transferForm.positionId" placeholder="请选择岗位" clearable filterable class="w-full">
            <el-option v-for="p in positions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前部门">
          <el-select v-model="transferForm.departmentId" placeholder="请选择部门" clearable filterable class="w-full">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleTransfer">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="用户详情" width="560px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ detail.realName }}</el-descriptions-item>
        <el-descriptions-item label="登录账户">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'danger'" effect="dark">
            {{ detail.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ detail.hireDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="员工属性">{{ detail.employeeType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前岗位">{{ detail.positionName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前部门">{{ detail.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="posDetailVisible" title="岗位详情" width="460px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="岗位名称">{{ posDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="岗位编码">{{ posDetail.code || '-' }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ posDetail.sort ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="posDetail.status === 1 ? 'success' : 'danger'" effect="dark">
            {{ posDetail.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ posDetail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="posDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="posEditVisible" :title="posForm.id ? '修改岗位' : '新增岗位'" width="460px" destroy-on-close>
      <el-form ref="posFormRef" :model="posForm" :rules="posRules" label-width="90px">
        <el-form-item label="岗位名称" prop="name">
          <el-input v-model="posForm.name" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码">
          <el-input v-model="posForm.code" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="posForm.sort" :min="0" :max="9999" class="w-full" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="posForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="posForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="posEditVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handlePosSave">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="salesmanDetailVisible" title="业务员详情" width="460px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="姓名">{{ salesmanDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ salesmanDetail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前岗位">{{ salesmanDetail.positionName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前部门">{{ salesmanDetail.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="salesmanDetail.status === 1 ? 'success' : 'danger'" effect="dark">
            {{ salesmanDetail.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ salesmanDetail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="salesmanDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="salesmanEditVisible" :title="salesmanForm.id ? '修改业务员' : '新增业务员'" width="480px" destroy-on-close>
      <el-form ref="salesmanFormRef" :model="salesmanForm" :rules="salesmanRules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="salesmanForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="salesmanForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="当前岗位">
          <el-select v-model="salesmanForm.positionId" placeholder="请选择岗位" clearable filterable class="w-full">
            <el-option v-for="p in positions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前部门">
          <el-select v-model="salesmanForm.departmentId" placeholder="请选择部门" clearable filterable class="w-full">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="salesmanForm.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="salesmanForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="salesmanEditVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSalesmanSave">确定</el-button>
      </template>
    </el-dialog>

    <input ref="fileInput" type="file" accept=".csv" style="display: none" @change="onFileChange" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Upload, Download, ArrowDown } from '@element-plus/icons-vue'
import {
  getUserPage,
  getUserDetail,
  createUser,
  updateUser,
  deleteUser,
  transferUser,
  exportUser,
  importUser,
  getPositionPage,
  getPositionList,
  createPosition,
  updatePosition,
  deletePosition,
  exportPosition,
  importPosition,
  getSalesmanPage,
  createSalesman,
  updateSalesman,
  deleteSalesman,
  exportSalesman,
  importSalesman
} from '../../api/user'
import { getDepartmentList } from '../../api/department'

const activeTab = ref('user')
const employeeTypes = ['正式', '试用', '实习', '劳务派遣', '其他']

const positions = ref([])
const departments = ref([])
const fileInput = ref()

const userSelection = ref([])
const users = ref([])
const userTotal = ref(0)
const userLoading = ref(false)
const filterVisible = ref(false)

const userQuery = reactive({
  current: 1,
  size: 10,
  name: '',
  username: '',
  status: null,
  departmentId: null,
  positionId: null
})

const formRef = ref()
const saving = ref(false)
const editVisible = ref(false)
const transferVisible = ref(false)
const detailVisible = ref(false)
const detail = ref({})

const form = reactive({
  id: null,
  realName: '',
  username: '',
  password: '',
  phone: '',
  status: 1,
  hireDate: '',
  positionId: null,
  departmentId: null,
  employeeType: '',
  remark: ''
})

const transferForm = reactive({
  id: null,
  realName: '',
  positionId: null,
  departmentId: null
})

const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入登录账户', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const positionsList = ref([])
const posFilterVisible = ref(false)
const posLoading = ref(false)
const posTotal = ref(0)
const posEditVisible = ref(false)
const posDetailVisible = ref(false)
const posDetail = ref({})

const posQuery = reactive({
  current: 1,
  size: 10,
  name: '',
  status: null
})

const posFormRef = ref()
const posForm = reactive({
  id: null,
  name: '',
  code: '',
  sort: 0,
  status: 1,
  remark: ''
})

const posRules = {
  name: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }]
}

const salesmen = ref([])
const salesmanTotal = ref(0)
const salesmanLoading = ref(false)
const salesmanFilterVisible = ref(false)
const salesmanDetailVisible = ref(false)
const salesmanEditVisible = ref(false)
const salesmanDetail = ref({})

const salesmanQuery = reactive({
  current: 1,
  size: 10,
  name: '',
  status: null,
  departmentId: null
})

const salesmanFormRef = ref()
const salesmanForm = reactive({
  id: null,
  name: '',
  phone: '',
  positionId: null,
  departmentId: null,
  status: 1,
  remark: ''
})

const salesmanRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function onTabChange() {
  if (activeTab.value === 'position') {
    loadPositions(1)
    loadOptions()
  } else if (activeTab.value === 'salesman') {
    loadSalesmen(1)
  } else {
    loadUsers(1)
  }
}

async function loadOptions() {
  const [posRes, deptRes] = await Promise.all([getPositionList(), getDepartmentList()])
  positions.value = posRes.data || []
  departments.value = deptRes.data || []
}

async function loadUsers(page) {
  if (page) userQuery.current = page
  userLoading.value = true
  try {
    const params = { ...userQuery }
    Object.keys(params).forEach((k) => {
      if (params[k] === null || params[k] === undefined || params[k] === '') delete params[k]
    })
    const res = await getUserPage(params)
    users.value = res.data.records
    userTotal.value = res.data.total
  } finally {
    userLoading.value = false
  }
}

function resetUserFilter() {
  userQuery.name = ''
  userQuery.username = ''
  userQuery.status = null
  userQuery.departmentId = null
  userQuery.positionId = null
  loadUsers(1)
}

function openCreate() {
  Object.assign(form, {
    id: null,
    realName: '',
    username: '',
    password: '',
    phone: '',
    status: 1,
    hireDate: '',
    positionId: null,
    departmentId: null,
    employeeType: '',
    remark: ''
  })
  editVisible.value = true
}

function openEdit(row) {
  Object.assign(form, {
    id: row.id,
    realName: row.realName,
    username: row.username,
    password: '',
    phone: row.phone,
    status: row.status,
    hireDate: row.hireDate || '',
    positionId: row.positionId,
    departmentId: row.departmentId,
    employeeType: row.employeeType,
    remark: row.remark
  })
  editVisible.value = true
}

async function openUserDetail(row) {
  const res = await getUserDetail(row.id)
  detail.value = res.data
  detailVisible.value = true
}

function openTransfer(row) {
  Object.assign(transferForm, {
    id: row.id,
    realName: row.realName,
    positionId: row.positionId,
    departmentId: row.departmentId
  })
  transferVisible.value = true
}

async function handleTransfer() {
  saving.value = true
  try {
    if (transferForm.id) {
      await transferUser(transferForm.id, transferForm.positionId, transferForm.departmentId)
    } else {
      for (const row of userSelection.value) {
        await transferUser(row.id, transferForm.positionId, transferForm.departmentId)
      }
    }
    ElMessage.success('调岗成功')
    transferVisible.value = false
    userSelection.value = []
    loadUsers()
  } finally {
    saving.value = false
  }
}

async function handleSave() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const payload = { ...form }
      if (!payload.password) delete payload.password
      if (form.id) {
        await updateUser(payload)
      } else {
        await createUser(payload)
      }
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      editVisible.value = false
      loadUsers()
    } finally {
      saving.value = false
    }
  })
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.realName}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadUsers()
}

async function handleBatchCommand(command) {
  if (command === 'batchDelete') {
    await handleBatchDelete()
  } else if (command === 'batchTransfer') {
    openBatchTransfer()
  }
}

async function handleBatchDelete() {
  if (!userSelection.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${userSelection.value.length} 个用户吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  for (const row of userSelection.value) {
    await deleteUser(row.id)
  }
  ElMessage.success('批量删除成功')
  userSelection.value = []
  loadUsers()
}

function openBatchTransfer() {
  const first = userSelection.value[0]
  Object.assign(transferForm, {
    id: null,
    realName: `选中 ${userSelection.value.length} 人`,
    positionId: first.positionId,
    departmentId: first.departmentId
  })
  transferVisible.value = true
}

async function handleExport() {
  const res = await exportUser()
  downloadCsv(res.data)
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
  if (activeTab.value === 'user') {
    await importUser(content)
  } else if (activeTab.value === 'position') {
    await importPosition(content)
  } else {
    await importSalesman(content)
  }
  ElMessage.success('导入成功')
  onTabChange()
}

function downloadCsv({ fileName, content }) {
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = fileName
  a.click()
  URL.revokeObjectURL(url)
}

async function loadPositions(page) {
  if (page) posQuery.current = page
  posLoading.value = true
  try {
    const params = { ...posQuery }
    Object.keys(params).forEach((k) => {
      if (params[k] === null || params[k] === undefined || params[k] === '') delete params[k]
    })
    const res = await getPositionPage(params)
    positionsList.value = res.data.records
    posTotal.value = res.data.total
  } finally {
    posLoading.value = false
  }
}

function resetPosFilter() {
  posQuery.name = ''
  posQuery.status = null
  loadPositions(1)
}

function openPosCreate() {
  Object.assign(posForm, { id: null, name: '', code: '', sort: 0, status: 1, remark: '' })
  posEditVisible.value = true
}

function openPosEdit(row) {
  Object.assign(posForm, {
    id: row.id,
    name: row.name,
    code: row.code,
    sort: row.sort ?? 0,
    status: row.status,
    remark: row.remark
  })
  posEditVisible.value = true
}

function openPosDetail(row) {
  posDetail.value = row
  posDetailVisible.value = true
}

async function handlePosSave() {
  await posFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (posForm.id) {
        await updatePosition({ ...posForm })
      } else {
        await createPosition({ ...posForm })
      }
      ElMessage.success(posForm.id ? '修改成功' : '新增成功')
      posEditVisible.value = false
      loadPositions()
      loadOptions()
    } finally {
      saving.value = false
    }
  })
}

async function handlePosDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除岗位「${row.name}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  await deletePosition(row.id)
  ElMessage.success('删除成功')
  loadPositions()
  loadOptions()
}

async function handlePosExport() {
  const res = await exportPosition()
  downloadCsv(res.data)
  ElMessage.success('导出成功')
}

function handlePosImport() {
  fileInput.value.value = ''
  fileInput.value.click()
}

async function loadSalesmen(page) {
  if (page) salesmanQuery.current = page
  salesmanLoading.value = true
  try {
    const params = { ...salesmanQuery }
    Object.keys(params).forEach((k) => {
      if (params[k] === null || params[k] === undefined || params[k] === '') delete params[k]
    })
    const res = await getSalesmanPage(params)
    salesmen.value = res.data.records
    salesmanTotal.value = res.data.total
  } finally {
    salesmanLoading.value = false
  }
}

function resetSalesmanFilter() {
  salesmanQuery.name = ''
  salesmanQuery.status = null
  salesmanQuery.departmentId = null
  loadSalesmen(1)
}

function openSalesmanCreate() {
  Object.assign(salesmanForm, {
    id: null, name: '', phone: '', positionId: null, departmentId: null, status: 1, remark: ''
  })
  salesmanEditVisible.value = true
}

function openSalesmanEdit(row) {
  Object.assign(salesmanForm, {
    id: row.id,
    name: row.name,
    phone: row.phone,
    positionId: row.positionId,
    departmentId: row.departmentId,
    status: row.status,
    remark: row.remark
  })
  salesmanEditVisible.value = true
}

function openSalesmanDetail(row) {
  salesmanDetail.value = row
  salesmanDetailVisible.value = true
}

async function handleSalesmanSave() {
  await salesmanFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (salesmanForm.id) {
        await updateSalesman({ ...salesmanForm })
      } else {
        await createSalesman({ ...salesmanForm })
      }
      ElMessage.success(salesmanForm.id ? '修改成功' : '新增成功')
      salesmanEditVisible.value = false
      loadSalesmen()
    } finally {
      saving.value = false
    }
  })
}

async function handleSalesmanDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除业务员「${row.name}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  await deleteSalesman(row.id)
  ElMessage.success('删除成功')
  loadSalesmen()
}

async function handleSalesmanExport() {
  const res = await exportSalesman()
  downloadCsv(res.data)
  ElMessage.success('导出成功')
}

function handleSalesmanImport() {
  fileInput.value.value = ''
  fileInput.value.click()
}

onMounted(async () => {
  await loadOptions()
  loadUsers(1)
})
</script>

<style scoped>
.user-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
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

.name-cell {
  font-weight: 500;
  color: #303133;
}

.transfer-tip {
  color: #606266;
  font-size: 14px;
  margin-bottom: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
