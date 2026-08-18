export const moduleMenus = [
  {
    path: '/desktop',
    name: '系统桌面',
    icon: 'Odometer',
    apiPrefix: '/api/desktop',
    children: [
      { path: '/desktop/approval', name: '审批流程' },
      { path: '/desktop/memo', name: '备忘录' },
      { path: '/desktop/message', name: '消息提醒' }
    ]
  },
  {
    path: '/collection',
    name: '采集中心',
    icon: 'Download',
    apiPrefix: '/api/collection',
    children: [
      { path: '/collection/collect', name: '数据采集' },
      {
        path: '/collection/plan',
        name: '流向计划',
        children: [
          { path: '/collection/plan/report', name: '流向上报' },
          { path: '/collection/plan/task', name: '流向任务' },
          { path: '/collection/plan/identify', name: '流向识别' }
        ]
      },
      { path: '/collection/strategy', name: '识别策略' },
      {
        path: '/collection/tool',
        name: '数据工具',
        children: [
          { path: '/collection/tool/month', name: '数据月份' },
          { path: '/collection/tool/customer', name: '识别客户' }
        ]
      }
    ]
  },
  {
    path: '/data',
    name: '数据中心',
    icon: 'DataBase',
    apiPrefix: '/api/data',
    children: [
      { path: '/data/flow', name: '流向数据' },
      { path: '/data/settle', name: '核算数据' },
      { path: '/data/inventory', name: '库存管理' },
      { path: '/data/return-exchange', name: '退换货管理' }
    ]
  },
  {
    path: '/payment',
    name: '货款管理',
    icon: 'Money',
    apiPrefix: '/api/payment',
    children: [
      { path: '/payment/shipment', name: '发货订单' },
      { path: '/payment/record', name: '回款记录' },
      {
        path: '/payment/receivable',
        name: '应收管理',
        children: [
          { path: '/payment/receivable/overview', name: '应收总览' },
          { path: '/payment/receivable/detail', name: '应收明细' }
        ]
      },
      {
        path: '/payment/payable',
        name: '应付管理',
        children: [
          { path: '/payment/payable/flow', name: '流向应付' }
        ]
      }
    ]
  },
  {
    path: '/report',
    name: '报表管理',
    icon: 'Document',
    apiPrefix: '/api/report',
    children: [
      { path: '/report/terminal', name: '终端表' },
      { path: '/report/space', name: '空间表' },
      { path: '/report/space-salary', name: '空间工资表' }
    ]
  },
  {
    path: '/analysis',
    name: '数据分析',
    icon: 'TrendCharts',
    apiPrefix: '/api/analysis',
    children: [
      { path: '/analysis/finance', name: '发货回款应收款数据分析' },
      { path: '/analysis/flow', name: '流向分析' }
    ]
  },
  {
    path: '/bid',
    name: '竞标管理',
    icon: 'Trophy',
    apiPrefix: '/api/bid',
    children: [
      { path: '/bid/catalog', name: '竞标目录' },
      { path: '/bid/apply', name: '申请竞标' },
      { path: '/bid/announcement', name: '竞标公示' },
      { path: '/bid/reward-penalty', name: '竞标奖惩' },
      { path: '/bid/assessment', name: '竞标考核' },
      { path: '/bid/archive', name: '竞标档案' },
      { path: '/bid/contract', name: '竞标合同' },
      { path: '/bid/backend', name: '后台管理' }
    ]
  },
  {
    path: '/assessment',
    name: '考核管理',
    icon: 'EditPen',
    apiPrefix: '/api/assessment',
    children: [
      { path: '/assessment/bid', name: '竞标考核' },
      {
        path: '/assessment/batch',
        name: '批量考核',
        children: [
          { path: '/assessment/batch/publish', name: '考核发布' },
          { path: '/assessment/batch/result', name: '考核结果' }
        ]
      }
    ]
  },
  {
    path: '/base',
    name: '基础信息',
    icon: 'Files',
    apiPrefix: '/api/base',
    children: [
      { path: '/base/terminal', name: '终端信息' },
      { path: '/base/business', name: '商业信息' },
      { path: '/base/manufacturer', name: '厂家信息' },
      { path: '/base/product', name: '产品信息' }
    ]
  },
  {
    path: '/config',
    name: '运营配置',
    icon: 'Setting',
    apiPrefix: '/api/config',
    children: [
      { path: '/config/user', name: '人员配置' },
      { path: '/config/permission', name: '权限分配' },
      { path: '/config/nature', name: '性质分类' },
      { path: '/config/department', name: '部门区域' }
    ]
  }
]

export function flattenMenus(items, parentName = '') {
  const result = []
  for (const item of items) {
    const row = {
      path: item.path,
      name: item.name,
      moduleName: parentName || item.name
    }
    result.push(row)
    if (item.children && item.children.length) {
      result.push(...flattenMenus(item.children, parentName || item.name))
    }
  }
  return result
}
