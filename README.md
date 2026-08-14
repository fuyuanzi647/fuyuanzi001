# 浮元子医药流向管理软件

医药流向管理系统，覆盖采集、数据、货款、报表、分析、竞标、考核、基础信息与运营配置全流程。

## 技术栈

- 前端：Vue 3 + Element Plus + Vite + Pinia + Vue Router + Axios
- 后端：Java 17 + Spring Boot 3.2 + MyBatis-Plus
- 数据库：MySQL 8

## 目录结构

```
backend/   Spring Boot 后端服务（端口 8080）
frontend/  Vue3 前端应用（端口 5173，/api 反向代理到后端）
```

## 快速启动

### 后端

```bash
# 1. 初始化数据库
mysql -uroot -p < backend/src/main/resources/db/init.sql

# 2. 配置数据库连接（环境变量或 application.yml）
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=flow_management
export MYSQL_USER=root
export MYSQL_PASSWORD=root

# 3. 启动
cd backend && mvn spring-boot:run
```

### 前端

```bash
cd frontend && npm install && npm run dev
```

## 功能模块

| 模块 | 功能 |
|------|------|
| 系统桌面 | 审批流程、备忘录、消息提醒 |
| 采集中心 | 数据采集、流向计划、识别策略、数据工具 |
| 数据中心 | 流向数据、核算数据、库存管理、退换货管理 |
| 货款管理 | 发货订单、回款记录、应收管理、应付管理 |
| 报表管理 | 终端表、空间表、空间工资表 |
| 数据分析 | 发货回款应收款数据分析、流向分析 |
| 竞标管理 | 竞标目录、申请竞标、竞标公示、竞标奖惩、竞标考核、竞标档案、竞标合同、后台管理 |
| 考核管理 | 竞标考核、批量考核 |
| 基础信息 | 终端信息、商业信息、厂家信息、产品信息 |
| 运营配置 | 人员配置、权限分配、性质分类、部门区域 |

## 数据库初始化

框架阶段已预置全部模块基础表结构：`backend/src/main/resources/db/init.sql`。
