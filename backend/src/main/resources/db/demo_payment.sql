-- ============================================================
-- 货款管理演示数据（发货订单 + 回款记录）
-- 依赖基础资料：base_business / base_manufacturer / base_product
-- ============================================================

USE flow_management;

-- ------------------------------------------------------------
-- 商业公司
-- ------------------------------------------------------------
INSERT INTO base_business (name, code, region, contact, phone, address, status) VALUES
('国药控股国大药房有限公司', 'B001', '华东区', '张建国', '13800000001', '上海市黄浦区南京东路100号', 1),
('华润医药商业集团有限公司', 'B002', '华北区', '李伟', '13800000002', '北京市朝阳区建国路88号', 1),
('九州通医药集团股份有限公司', 'B003', '华中区', '王芳', '13800000003', '武汉市汉阳区龙阳大道8号', 1),
('上海医药集团股份有限公司', 'B004', '华东区', '陈晓', '13800000004', '上海市徐汇区肇嘉浜路1000号', 1),
('广州医药集团有限公司', 'B005', '华南区', '刘洋', '13800000005', '广州市荔湾区沙面北街45号', 1);

-- ------------------------------------------------------------
-- 厂家
-- ------------------------------------------------------------
INSERT INTO base_manufacturer (name, code, contact, phone, status) VALUES
('扬子江药业集团有限公司', 'M001', '周明', '13900000001', 1),
('江苏恒瑞医药股份有限公司', 'M002', '吴刚', '13900000002', 1),
('正大天晴药业集团股份有限公司', 'M003', '郑磊', '13900000003', 1),
('华北制药股份有限公司', 'M004', '孙悦', '13900000004', 1);

-- ------------------------------------------------------------
-- 产品
-- ------------------------------------------------------------
INSERT INTO base_product (name, code, spec, manufacturer_id, price, status) VALUES
('阿莫西林胶囊', 'P001', '0.25g*24粒', 1, 15.80, 1),
('布洛芬缓释胶囊', 'P002', '0.3g*20粒', 2, 22.50, 1),
('奥美拉唑肠溶胶囊', 'P003', '20mg*14粒', 3, 18.60, 1),
('阿托伐他汀钙片', 'P004', '20mg*7片', 4, 35.40, 1),
('氯雷他定片', 'P005', '10mg*6片', 2, 12.90, 1),
('左氧氟沙星片', 'P006', '0.5g*7片', 3, 28.00, 1);

-- ------------------------------------------------------------
-- 发货订单
-- ------------------------------------------------------------
-- 订单1：已全额回款
INSERT INTO shipment_order (order_no, business_id, total_amount, paid_amount, paid_quantity, ship_date, remark) VALUES
('SO20260701-001', 1, 3350.00, 3350.00, 150, '2026-07-01', '第一批集中采购订单');

INSERT INTO shipment_item (order_id, manufacturer_id, product_id, quantity, batch_no, amount) VALUES
(1, 1, 1, 100, 'YZJ20260501', 1580.00),
(1, 4, 4, 50, 'HB20260502', 1770.00);

INSERT INTO payment_record (order_id, business_id, amount, quantity, pay_date, office_date, pay_method, remark) VALUES
(1, 1, 3350.00, 150, '2026-07-20', '2026-07-18', '银行转账', '全额结清');

-- 订单2：部分回款
INSERT INTO shipment_order (order_no, business_id, total_amount, paid_amount, paid_quantity, ship_date, remark) VALUES
('SO20260715-002', 2, 5790.00, 2000.00, 80, '2026-07-15', '');

INSERT INTO shipment_item (order_id, manufacturer_id, product_id, quantity, batch_no, amount) VALUES
(2, 2, 2, 200, 'HR20260610', 4500.00),
(2, 2, 5, 100, 'HR20260611', 1290.00);

INSERT INTO payment_record (order_id, business_id, amount, quantity, pay_date, office_date, pay_method, remark) VALUES
(2, 2, 2000.00, 80, '2026-08-05', '2026-08-03', '银行承兑', '首期回款');

-- 订单3：未回款
INSERT INTO shipment_order (order_no, business_id, total_amount, paid_amount, paid_quantity, ship_date, remark) VALUES
('SO20260801-003', 3, 7820.00, 0.00, 0, '2026-08-01', '待回款');

INSERT INTO shipment_item (order_id, manufacturer_id, product_id, quantity, batch_no, amount) VALUES
(3, 3, 3, 300, 'ZDTJ20260705', 5580.00),
(3, 3, 6, 80, 'ZDTJ20260706', 2240.00);

-- 订单4：未回款
INSERT INTO shipment_order (order_no, business_id, total_amount, paid_amount, paid_quantity, ship_date, remark) VALUES
('SO20260810-004', 4, 2370.00, 0.00, 0, '2026-08-10', '');

INSERT INTO shipment_item (order_id, manufacturer_id, product_id, quantity, batch_no, amount) VALUES
(4, 1, 1, 150, 'YZJ20260720', 2370.00);
