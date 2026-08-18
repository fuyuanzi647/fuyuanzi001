-- ============================================================
-- 浮元子医药流向管理系统 - 数据库初始化脚本
-- 数据库: flow_management
-- 说明: 创建框架阶段的基础表结构，公共字段统一为
--       id / create_time / update_time / deleted
-- ============================================================

CREATE DATABASE IF NOT EXISTS flow_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE flow_management;

-- ------------------------------------------------------------
-- 1. 系统桌面
-- ------------------------------------------------------------

-- 审批流程
CREATE TABLE IF NOT EXISTS sys_approval (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title       VARCHAR(200) NOT NULL COMMENT '审批标题',
    biz_type    VARCHAR(50)  NOT NULL COMMENT '业务类型',
    biz_id      BIGINT       NOT NULL COMMENT '业务ID',
    applicant_id BIGINT      NOT NULL COMMENT '申请人ID',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0待审1通过2驳回3撤销',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程';

-- 备忘录
CREATE TABLE IF NOT EXISTS sys_memo (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title       VARCHAR(200) NOT NULL COMMENT '标题',
    content     TEXT         COMMENT '内容',
    remind_time DATETIME     COMMENT '提醒时间',
    owner_id    BIGINT       NOT NULL COMMENT '所属用户ID',
    done        TINYINT      NOT NULL DEFAULT 0 COMMENT '是否完成:0否1是',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备忘录';

-- 消息提醒
CREATE TABLE IF NOT EXISTS sys_message (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    receiver_id BIGINT       NOT NULL COMMENT '接收人ID',
    type        VARCHAR(50)  NOT NULL COMMENT '消息类型',
    title       VARCHAR(200) NOT NULL COMMENT '标题',
    content     VARCHAR(500) COMMENT '内容',
    read_status TINYINT      NOT NULL DEFAULT 0 COMMENT '已读:0否1是',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息提醒';

-- ------------------------------------------------------------
-- 2. 采集中心
-- ------------------------------------------------------------

-- 数据采集任务（自动实时爬取流向数据）
CREATE TABLE IF NOT EXISTS flow_collect_task (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '采集任务名称',
    source      VARCHAR(100) NOT NULL COMMENT '数据来源',
    cron_expr   VARCHAR(100) COMMENT '调度表达式',
    last_run_time DATETIME   COMMENT '上次执行时间',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0停用1启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据采集任务';

-- 流向计划（流向上报、流向任务）
CREATE TABLE IF NOT EXISTS flow_plan (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '计划名称',
    plan_type   VARCHAR(50)  NOT NULL COMMENT '计划类型:上报/任务',
    data_month  VARCHAR(7)   NOT NULL COMMENT '数据月份 yyyy-MM',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0草稿1已发布2已完成',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流向计划';

-- 流向识别策略
CREATE TABLE IF NOT EXISTS flow_identify_strategy (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '策略名称',
    rule_config TEXT         COMMENT '识别规则配置(JSON)',
    enabled     TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用:0否1是',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流向识别策略';

-- 数据工具（数据月份、识别客户）
CREATE TABLE IF NOT EXISTS data_tool (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tool_type   VARCHAR(50)  NOT NULL COMMENT '工具类型:数据月份/识别客户',
    tool_value  VARCHAR(200) COMMENT '工具值',
    remark      VARCHAR(500) COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据工具';

-- ------------------------------------------------------------
-- 3. 数据中心
-- ------------------------------------------------------------

-- 流向数据
CREATE TABLE IF NOT EXISTS flow_data (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    data_month    VARCHAR(7)   NOT NULL COMMENT '数据月份 yyyy-MM',
    product_id    BIGINT       NOT NULL COMMENT '产品ID',
    terminal_id   BIGINT       NOT NULL COMMENT '终端ID',
    business_id   BIGINT       NOT NULL COMMENT '商业公司ID',
    quantity      DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '流向数量',
    amount        DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '流向金额',
    collect_task_id BIGINT     COMMENT '来源采集任务ID',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是',
    KEY idx_month (data_month),
    KEY idx_terminal (terminal_id),
    KEY idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流向数据';

-- 核算数据
CREATE TABLE IF NOT EXISTS settle_data (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    data_month  VARCHAR(7)   NOT NULL COMMENT '数据月份 yyyy-MM',
    product_id  BIGINT       NOT NULL COMMENT '产品ID',
    business_id BIGINT       NOT NULL COMMENT '商业公司ID',
    quantity    DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '核算数量',
    amount      DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '核算金额',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是',
    KEY idx_month (data_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核算数据';

-- 库存管理
CREATE TABLE IF NOT EXISTS inventory (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    product_id  BIGINT       NOT NULL COMMENT '产品ID',
    business_id BIGINT       NOT NULL COMMENT '商业公司ID',
    quantity    DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '库存数量',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是',
    UNIQUE KEY uk_product_business (product_id, business_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存';

-- 退换货管理
CREATE TABLE IF NOT EXISTS return_exchange (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_no    VARCHAR(50)  NOT NULL COMMENT '退换货单号',
    biz_type    VARCHAR(20)  NOT NULL COMMENT '类型:退货/换货',
    product_id  BIGINT       NOT NULL COMMENT '产品ID',
    business_id BIGINT       NOT NULL COMMENT '商业公司ID',
    quantity    DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '数量',
    amount      DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '金额',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0待处理1已处理',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退换货';

-- ------------------------------------------------------------
-- 4. 货款管理
-- ------------------------------------------------------------

-- 发货订单
CREATE TABLE IF NOT EXISTS shipment_order (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_no     VARCHAR(50)  NOT NULL COMMENT '发货单号',
    business_id  BIGINT       NOT NULL COMMENT '商业公司ID',
    total_amount DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '订单合计金额',
    paid_amount  DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '已回金额',
    paid_quantity DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '已回数量',
    ship_date    DATE         COMMENT '记账日期',
    remark       VARCHAR(500) COMMENT '备注',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货订单';

-- 发货订单明细
CREATE TABLE IF NOT EXISTS shipment_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_id    BIGINT       NOT NULL COMMENT '发货订单ID',
    manufacturer_id BIGINT   NOT NULL COMMENT '出库厂家ID',
    product_id  BIGINT       NOT NULL COMMENT '产品ID',
    quantity    DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '数量',
    batch_no    VARCHAR(100) COMMENT '批号',
    amount      DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '金额',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货订单明细';

-- 回款记录
CREATE TABLE IF NOT EXISTS payment_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_id    BIGINT       NOT NULL COMMENT '发货订单ID',
    business_id BIGINT       NOT NULL COMMENT '商业公司ID',
    amount      DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '回款金额',
    quantity    DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '回款数量',
    pay_date    DATE         COMMENT '回款日期',
    office_date DATE         COMMENT '办事处日期',
    pay_method  VARCHAR(50)  COMMENT '回款方式',
    remark      VARCHAR(500) COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回款记录';

-- 应收管理
CREATE TABLE IF NOT EXISTS receivable (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    business_id BIGINT       NOT NULL COMMENT '商业公司ID',
    total_amount DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '应收总额',
    paid_amount  DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '已收金额',
    remain_amount DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '剩余应收',
    due_date    DATE         COMMENT '到期日',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收';

-- 应付管理（流向应付）
CREATE TABLE IF NOT EXISTS payable (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    business_id BIGINT       NOT NULL COMMENT '商业公司ID',
    total_amount DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '应付总额',
    paid_amount  DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '已付金额',
    remain_amount DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '剩余应付',
    due_date    DATE         COMMENT '到期日',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应付';

-- ------------------------------------------------------------
-- 5. 报表管理（报表多为统计视图，先建报表任务配置）
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS report_config (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    report_type VARCHAR(50)  NOT NULL COMMENT '报表类型:终端表/空间表/空间工资表',
    data_month  VARCHAR(7)   NOT NULL COMMENT '数据月份 yyyy-MM',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0未生成1已生成',
    file_url    VARCHAR(500) COMMENT '生成文件地址',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表配置';

-- ------------------------------------------------------------
-- 6. 数据分析（基于流向/核算/货款数据，无独立存储表）
-- ------------------------------------------------------------

-- ------------------------------------------------------------
-- 7. 竞标管理
-- ------------------------------------------------------------

-- 竞标目录
CREATE TABLE IF NOT EXISTS bid_catalog (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '竞标名称',
    region      VARCHAR(200) COMMENT '区域',
    start_date  DATE         COMMENT '开始日期',
    end_date    DATE         COMMENT '结束日期',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0未开始1进行中2已结束',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞标目录';

-- 申请竞标
CREATE TABLE IF NOT EXISTS bid_apply (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    catalog_id  BIGINT       NOT NULL COMMENT '竞标目录ID',
    applicant_id BIGINT      NOT NULL COMMENT '申请人ID',
    apply_content TEXT        COMMENT '申请内容',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0待审1通过2驳回',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请竞标';

-- 竞标公示
CREATE TABLE IF NOT EXISTS bid_announcement (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    catalog_id  BIGINT       NOT NULL COMMENT '竞标目录ID',
    title       VARCHAR(200) NOT NULL COMMENT '公示标题',
    content     TEXT         COMMENT '公示内容',
    publish_time DATETIME    COMMENT '公示时间',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞标公示';

-- 竞标奖惩
CREATE TABLE IF NOT EXISTS bid_reward_penalty (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    catalog_id  BIGINT       NOT NULL COMMENT '竞标目录ID',
    user_id     BIGINT       NOT NULL COMMENT '人员ID',
    award_type  VARCHAR(20)  NOT NULL COMMENT '类型:奖励/处罚',
    amount      DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '金额',
    reason      VARCHAR(500) COMMENT '原因',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞标奖惩';

-- 竞标考核
CREATE TABLE IF NOT EXISTS bid_assessment (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    catalog_id  BIGINT       NOT NULL COMMENT '竞标目录ID',
    user_id     BIGINT       NOT NULL COMMENT '人员ID',
    score       DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '考核得分',
    assess_comment VARCHAR(500) COMMENT '考核说明',
    assess_time DATETIME     COMMENT '考核时间',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞标考核';

-- 竞标档案
CREATE TABLE IF NOT EXISTS bid_archive (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    catalog_id  BIGINT       NOT NULL COMMENT '竞标目录ID',
    archive_type VARCHAR(50) COMMENT '档案类型',
    file_url    VARCHAR(500) COMMENT '文件地址',
    remark      VARCHAR(500) COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞标档案';

-- 竞标合同
CREATE TABLE IF NOT EXISTS bid_contract (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    catalog_id  BIGINT       NOT NULL COMMENT '竞标目录ID',
    contract_no VARCHAR(50)  NOT NULL COMMENT '合同编号',
    sign_date   DATE         COMMENT '签订日期',
    amount      DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '合同金额',
    file_url    VARCHAR(500) COMMENT '合同文件',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0执行中1已完成',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞标合同';

-- 竞标后台管理（配置，复用竞标目录表，提供后台字段扩展）
CREATE TABLE IF NOT EXISTS bid_backend_config (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    config_key  VARCHAR(100) NOT NULL COMMENT '配置项',
    config_value VARCHAR(500) COMMENT '配置值',
    remark      VARCHAR(500) COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞标后台配置';

-- ------------------------------------------------------------
-- 8. 考核管理
-- ------------------------------------------------------------

-- 考核发布
CREATE TABLE IF NOT EXISTS assessment_publish (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title       VARCHAR(200) NOT NULL COMMENT '考核标题',
    batch_type  VARCHAR(50)  NOT NULL COMMENT '考核类型:竞标考核/批量考核',
    data_month  VARCHAR(7)   COMMENT '数据月份 yyyy-MM',
    publish_time DATETIME    COMMENT '发布时间',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0草稿1已发布2已完成',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核发布';

-- 考核结果
CREATE TABLE IF NOT EXISTS assessment_result (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    publish_id  BIGINT       NOT NULL COMMENT '考核发布ID',
    user_id     BIGINT       NOT NULL COMMENT '人员ID',
    score       DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '得分',
    result      VARCHAR(50)  COMMENT '考核结果',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核结果';

-- ------------------------------------------------------------
-- 9. 基础信息
-- ------------------------------------------------------------

-- 终端信息
CREATE TABLE IF NOT EXISTS base_terminal (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '终端名称',
    code        VARCHAR(50)  COMMENT '终端编码',
    type        VARCHAR(50)  COMMENT '终端类型',
    region      VARCHAR(200) COMMENT '所属区域',
    contact     VARCHAR(50)  COMMENT '联系人',
    phone       VARCHAR(20)  COMMENT '联系电话',
    address     VARCHAR(500) COMMENT '地址',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:0停用1启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='终端信息';

-- 商业信息
CREATE TABLE IF NOT EXISTS base_business (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '商业公司名称',
    code        VARCHAR(50)  COMMENT '商业编码',
    region      VARCHAR(200) COMMENT '所属区域',
    contact     VARCHAR(50)  COMMENT '联系人',
    phone       VARCHAR(20)  COMMENT '联系电话',
    address     VARCHAR(500) COMMENT '地址',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:0停用1启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商业信息';

-- 厂家信息
CREATE TABLE IF NOT EXISTS base_manufacturer (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '厂家名称',
    code        VARCHAR(50)  COMMENT '厂家编码',
    contact     VARCHAR(50)  COMMENT '联系人',
    phone       VARCHAR(20)  COMMENT '联系电话',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:0停用1启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='厂家信息';

-- 产品信息
CREATE TABLE IF NOT EXISTS base_product (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(200) NOT NULL COMMENT '产品名称',
    code        VARCHAR(50)  COMMENT '产品编码',
    spec        VARCHAR(200) COMMENT '规格',
    manufacturer_id BIGINT   COMMENT '厂家ID',
    price       DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '价格',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:0停用1启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品信息';

-- ------------------------------------------------------------
-- 10. 运营配置
-- ------------------------------------------------------------

-- 人员配置
CREATE TABLE IF NOT EXISTS config_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '登录名',
    password    VARCHAR(200) NOT NULL COMMENT '密码',
    real_name   VARCHAR(50)  COMMENT '姓名',
    department_id BIGINT     COMMENT '所属部门ID',
    role_id     BIGINT       COMMENT '角色ID',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:0停用1启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员配置';

-- 权限分配（角色）
CREATE TABLE IF NOT EXISTS config_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(50)  NOT NULL COMMENT '角色名称',
    code        VARCHAR(50)  NOT NULL COMMENT '角色编码',
    permission  TEXT         COMMENT '权限配置(JSON)',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色/权限';

-- 性质分类
CREATE TABLE IF NOT EXISTS config_nature (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    type        VARCHAR(50)  COMMENT '分类类型',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='性质分类';

-- 部门区域
CREATE TABLE IF NOT EXISTS config_department (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(50)  NOT NULL COMMENT '部门/区域名称',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '上级ID',
    type        VARCHAR(20)  COMMENT '类型:部门/区域',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0否1是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门区域';
