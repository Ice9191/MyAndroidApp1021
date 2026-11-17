SET NAMES utf8mb4;

SET @seed_now = UTC_TIMESTAMP(3);

START TRANSACTION;

INSERT INTO warehouses (code, name, enabled, is_default) VALUES
    ('DEMO_WH', '数据库演示工具库', TRUE, TRUE);

INSERT INTO locations (warehouse_code, code, name, enabled) VALUES
    ('DEMO_WH', 'DEMO_A01', '数据库演示货位 DEMO_A01', TRUE),
    ('DEMO_WH', 'DEMO_A02', '数据库演示货位 DEMO_A02', TRUE),
    ('DEMO_WH', 'DEMO_A03', '数据库演示货位 DEMO_A03', TRUE);

INSERT INTO organizations (code, name, parent_code, enabled) VALUES
    ('DEMO_ORG', '数据库演示组织', NULL, TRUE);

INSERT INTO roles (code, name, data_scope, enabled) VALUES
    ('DEMO_ROLE', '材料员', 'ORG_TREE', TRUE);

INSERT INTO permissions (code, description) VALUES
    ('putaway.read', 'putaway.read'),
    ('putaway.write', 'putaway.write'),
    ('picking.read', 'picking.read'),
    ('picking.write', 'picking.write'),
    ('returns.read', 'returns.read'),
    ('returns.write', 'returns.write'),
    ('tempBorrow.read', 'tempBorrow.read'),
    ('tempBorrow.write', 'tempBorrow.write'),
    ('tempReturn.read', 'tempReturn.read'),
    ('tempReturn.write', 'tempReturn.write'),
    ('otherPicking.read', 'otherPicking.read'),
    ('otherPicking.write', 'otherPicking.write'),
    ('otherPutaway.read', 'otherPutaway.read'),
    ('otherPutaway.write', 'otherPutaway.write'),
    ('inventory.read', 'inventory.read'),
    ('inventory.write', 'inventory.write'),
    ('overdueAlerts.read', 'overdueAlerts.read');

INSERT INTO role_permissions (role_code, permission_code) VALUES
    ('DEMO_ROLE', 'putaway.read'),
    ('DEMO_ROLE', 'putaway.write'),
    ('DEMO_ROLE', 'picking.read'),
    ('DEMO_ROLE', 'picking.write'),
    ('DEMO_ROLE', 'returns.read'),
    ('DEMO_ROLE', 'returns.write'),
    ('DEMO_ROLE', 'tempBorrow.read'),
    ('DEMO_ROLE', 'tempBorrow.write'),
    ('DEMO_ROLE', 'tempReturn.read'),
    ('DEMO_ROLE', 'tempReturn.write'),
    ('DEMO_ROLE', 'otherPicking.read'),
    ('DEMO_ROLE', 'otherPicking.write'),
    ('DEMO_ROLE', 'otherPutaway.read'),
    ('DEMO_ROLE', 'otherPutaway.write'),
    ('DEMO_ROLE', 'inventory.read'),
    ('DEMO_ROLE', 'inventory.write'),
    ('DEMO_ROLE', 'overdueAlerts.read');

INSERT INTO users (id, username, employee_no, name, password_hash, role_code, organization_code, enabled, created_at, updated_at) VALUES
    ('DEMO_USER', 'pda_demo', 'DEMO0001', '数据库演示材料员', @pda_seed_password_hash, 'DEMO_ROLE', 'DEMO_ORG', TRUE, @seed_now, @seed_now);

INSERT INTO materials (code, description, specification, unit, category, requires_inspection, inspection_cycle_months, updated_at) VALUES
    ('DEMO_M01', '绝缘扳手', '10mm', '把', '手工具', TRUE, 12, @seed_now),
    ('DEMO_M02', '数字万用表', '通用型', '台', '仪表', TRUE, 12, @seed_now),
    ('DEMO_M03', '绝缘手套', '通用型', '副', '防护用品', FALSE, NULL, @seed_now);

INSERT INTO inventory (rfid, material_code, batch_no, warehouse_code, location_code, inventory_status, tool_condition, first_inspection_date, next_inspection_date, deleted_at, created_at, updated_at) VALUES
    ('D17A00000000000000000001', 'DEMO_M01', 'DEMO_BATCH', 'DEMO_WH', NULL, '待上架', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000002', 'DEMO_M02', 'DEMO_BATCH', 'DEMO_WH', NULL, '待上架', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000003', 'DEMO_M03', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '在库', '好件', DATE(@seed_now), NULL, NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000004', 'DEMO_M01', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '待拣选', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000005', 'DEMO_M02', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '已备货', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000006', 'DEMO_M03', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '待上架', '好件', DATE(@seed_now), NULL, NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000007', 'DEMO_M01', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '待拣选', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000008', 'DEMO_M02', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '已借出', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000009', 'DEMO_M03', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '在库', '好件', DATE(@seed_now), NULL, NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A0000000000000000000A', 'DEMO_M01', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '在库', '坏件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A0000000000000000000B', 'DEMO_M02', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '在库', '坏件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A0000000000000000000C', 'DEMO_M03', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '在库', '好件', DATE(@seed_now), NULL, NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A0000000000000000000D', 'DEMO_M01', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '待上架', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A0000000000000000000E', 'DEMO_M02', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '待上架', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A0000000000000000000F', 'DEMO_M03', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '在库', '好件', DATE(@seed_now), NULL, NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000010', 'DEMO_M01', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '已报废', '坏件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), @seed_now, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now),
    ('D17A00000000000000000011', 'DEMO_M02', 'DEMO_BATCH', 'DEMO_WH', 'DEMO_A01', '已退货', '好件', DATE(@seed_now), DATE_ADD(DATE(@seed_now), INTERVAL 30 DAY), @seed_now, DATE_SUB(@seed_now, INTERVAL 7 DAY), @seed_now);

INSERT INTO inbound_orders (order_no, source_no, warehouse_code, status, created_by, created_at) VALUES
    ('DEMO_IN01', 'DEMO_TRANSFER01', 'DEMO_WH', '已绑定', 'DEMO_USER', @seed_now);

INSERT INTO inbound_lines (order_no, line_no, material_code, batch_no, quantity) VALUES
    ('DEMO_IN01', 1, 'DEMO_M01', 'DEMO_BATCH', 1),
    ('DEMO_IN01', 2, 'DEMO_M02', 'DEMO_BATCH', 1),
    ('DEMO_IN01', 3, 'DEMO_M03', 'DEMO_BATCH', 1);

INSERT INTO work_orders (order_no, organization_code, workshop, team, planned_at) VALUES
    ('DEMO_WO01', 'DEMO_ORG', '数据库演示车间', '数据库演示班组', @seed_now),
    ('DEMO_WO02', 'DEMO_ORG', '数据库演示车间', '数据库演示班组', @seed_now);

INSERT INTO borrow_orders (order_no, work_order_no, borrower_id, created_by, created_at) VALUES
    ('DEMO_BO01', 'DEMO_WO01', 'DEMO_USER', 'DEMO_USER', @seed_now),
    ('DEMO_BO02', 'DEMO_WO02', 'DEMO_USER', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 7 DAY));

INSERT INTO borrow_lines (order_no, line_no, material_code, quantity) VALUES
    ('DEMO_BO01', 1, 'DEMO_M01', 1),
    ('DEMO_BO01', 2, 'DEMO_M02', 1),
    ('DEMO_BO02', 1, 'DEMO_M03', 1);

INSERT INTO picking_orders (order_no, borrow_order_no, warehouse_code, status, created_by, created_at) VALUES
    ('DEMO_PK01', 'DEMO_BO01', 'DEMO_WH', 'PARTIALLY_PICKED', 'DEMO_USER', @seed_now);

INSERT INTO picking_lines (order_no, line_no, borrow_order_no, borrow_line_no, rfid, warehouse_code, recommended_location, picked_at, picked_by) VALUES
    ('DEMO_PK01', 1, 'DEMO_BO01', 1, 'D17A00000000000000000004', 'DEMO_WH', 'DEMO_A01', NULL, NULL),
    ('DEMO_PK01', 2, 'DEMO_BO01', 2, 'D17A00000000000000000005', 'DEMO_WH', 'DEMO_A01', @seed_now, 'DEMO_USER');

INSERT INTO temp_borrow_orders (order_no, request_id, request_hash, work_order_no, borrower_id, workshop, team, remark, warehouse_code, created_by, created_at) VALUES
    ('DEMO_TB01', '00000000-0000-4000-8000-000000000001', UNHEX(SHA2('server-seed-DEMO_TB01',256)), NULL, 'DEMO_USER', '数据库演示车间', '数据库演示班组', '待拣选的临时借出', 'DEMO_WH', 'DEMO_USER', @seed_now),
    ('DEMO_TB02', '00000000-0000-4000-8000-000000000002', UNHEX(SHA2('server-seed-DEMO_TB02',256)), NULL, 'DEMO_USER', '数据库演示车间', '数据库演示班组', '已借出，待直接归还', 'DEMO_WH', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 7 DAY));

INSERT INTO temp_borrow_lines (order_no, line_no, rfid, borrowed_at, borrowed_by) VALUES
    ('DEMO_TB01', 1, 'D17A00000000000000000007', NULL, NULL),
    ('DEMO_TB02', 1, 'D17A00000000000000000008', DATE_SUB(@seed_now, INTERVAL 7 DAY), 'DEMO_USER');

INSERT INTO loan_records (id, origin_kind, borrow_order_no, borrow_line_no, work_order_no, rfid, borrower_id, operator_id, borrowed_at, due_at, status, returned_at, returner_id) VALUES
    ('DEMO_LOAN06', 'WORKORDER', 'DEMO_BO02', 1, 'DEMO_WO02', 'D17A00000000000000000006', 'DEMO_USER', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 7 DAY), DATE_SUB(@seed_now, INTERVAL 2 DAY), 'RETURNED', DATE_SUB(@seed_now, INTERVAL 1 DAY), 'DEMO_USER'),
    ('DEMO_LOAN08', 'TEMPORARY', 'DEMO_TB02', 1, NULL, 'D17A00000000000000000008', 'DEMO_USER', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 7 DAY), DATE_SUB(@seed_now, INTERVAL 2 DAY), 'BORROWED', NULL, NULL);

INSERT INTO return_orders (order_no, borrow_order_no, returner_id, returned_at, created_by, created_at) VALUES
    ('DEMO_RT01', 'DEMO_BO02', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 1 DAY), 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 1 DAY));

INSERT INTO return_lines (order_no, line_no, rfid, loan_id, returned_at, returner_id) VALUES
    ('DEMO_RT01', 1, 'D17A00000000000000000006', 'DEMO_LOAN06', DATE_SUB(@seed_now, INTERVAL 1 DAY), 'DEMO_USER');

INSERT INTO putaway_orders (order_no, origin_kind, inbound_order_no, return_order_no, warehouse_code, status, created_by, created_at) VALUES
    ('DEMO_PA01', 'INBOUND', 'DEMO_IN01', NULL, 'DEMO_WH', '部分上架', 'DEMO_USER', @seed_now),
    ('DEMO_PA02', 'WORKORDER_RETURN', NULL, 'DEMO_RT01', 'DEMO_WH', '待上架', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 1 DAY));

INSERT INTO putaway_lines (order_no, line_no, inbound_order_no, inbound_line_no, return_order_no, return_line_no, rfid, warehouse_code, recommended_location, actual_location, putaway_at, putaway_by) VALUES
    ('DEMO_PA01', 1, 'DEMO_IN01', 1, NULL, NULL, 'D17A00000000000000000001', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_PA01', 2, 'DEMO_IN01', 2, NULL, NULL, 'D17A00000000000000000002', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_PA01', 3, 'DEMO_IN01', 3, NULL, NULL, 'D17A00000000000000000003', 'DEMO_WH', 'DEMO_A01', 'DEMO_A01', @seed_now, 'DEMO_USER'),
    ('DEMO_PA02', 1, NULL, NULL, 'DEMO_RT01', 1, 'D17A00000000000000000006', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL);

INSERT INTO temp_return_orders (order_no, origin_kind, borrow_order_no, work_order_no, returner_id, default_location, remark, warehouse_code, created_by, created_at) VALUES
    ('DEMO_TR01', 'TEMPORARY', 'DEMO_TB02', NULL, NULL, NULL, '逐件填写实际货位后直接在库', 'DEMO_WH', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 7 DAY));

INSERT INTO temp_return_lines (order_no, line_no, loan_id, warehouse_code, actual_location, returned_at, returned_by, returner_id) VALUES
    ('DEMO_TR01', 1, 'DEMO_LOAN08', 'DEMO_WH', NULL, NULL, NULL, NULL);

INSERT INTO other_tasks (id, task_kind, source_type, source_no, warehouse_code, source_ready, source_date, created_at) VALUES
    ('DEMO_OP_TEMP01', 'PICKING', 'TEMPORARY', 'DEMO_TB01', 'DEMO_WH', TRUE, @seed_now, @seed_now),
    ('DEMO_OP_TEMP02', 'PICKING', 'TEMPORARY', 'DEMO_TB02', 'DEMO_WH', TRUE, DATE_SUB(@seed_now, INTERVAL 7 DAY), DATE_SUB(@seed_now, INTERVAL 7 DAY)),
    ('DEMO_OP_INSPECT', 'PICKING', 'INSPECTION', 'DEMO_INSPECT_OUT', 'DEMO_WH', FALSE, @seed_now, @seed_now),
    ('DEMO_OP_REPAIR', 'PICKING', 'REPAIR', 'DEMO_REPAIR_OUT', 'DEMO_WH', FALSE, @seed_now, @seed_now),
    ('DEMO_OP_SCRAP', 'PICKING', 'SCRAP', 'DEMO_SCRAP_OUT', 'DEMO_WH', FALSE, @seed_now, @seed_now),
    ('DEMO_OP_RETURN', 'PICKING', 'RETURN', 'DEMO_SUPPLIER_RETURN', 'DEMO_WH', FALSE, @seed_now, @seed_now),
    ('DEMO_OA_INSPECT', 'PUTAWAY', 'INSPECTION', 'DEMO_INSPECT_BACK', 'DEMO_WH', FALSE, @seed_now, @seed_now),
    ('DEMO_OA_REPAIR', 'PUTAWAY', 'REPAIR', 'DEMO_REPAIR_BACK', 'DEMO_WH', FALSE, @seed_now, @seed_now);

INSERT INTO other_task_lines (task_id, line_no, rfid, warehouse_code, original_location, actual_location, completed_at, completed_by) VALUES
    ('DEMO_OP_TEMP01', 1, 'D17A00000000000000000007', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_OP_TEMP02', 1, 'D17A00000000000000000008', 'DEMO_WH', 'DEMO_A01', NULL, DATE_SUB(@seed_now, INTERVAL 7 DAY), 'DEMO_USER'),
    ('DEMO_OP_INSPECT', 1, 'D17A00000000000000000009', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_OP_REPAIR', 1, 'D17A0000000000000000000A', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_OP_SCRAP', 1, 'D17A0000000000000000000B', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_OP_RETURN', 1, 'D17A0000000000000000000C', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_OA_INSPECT', 1, 'D17A0000000000000000000D', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL),
    ('DEMO_OA_REPAIR', 1, 'D17A0000000000000000000E', 'DEMO_WH', 'DEMO_A01', NULL, NULL, NULL);

INSERT INTO inventory_reservations (rfid, owner_kind, owner_order_no, owner_line_no, reserved_at) VALUES
    ('D17A00000000000000000004', 'PICKING', 'DEMO_PK01', 1, @seed_now),
    ('D17A00000000000000000005', 'PICKING', 'DEMO_PK01', 2, @seed_now),
    ('D17A00000000000000000007', 'TEMPORARY', 'DEMO_TB01', 1, @seed_now),
    ('D17A00000000000000000009', 'OTHER_PICKING', 'DEMO_OP_INSPECT', 1, @seed_now),
    ('D17A0000000000000000000A', 'OTHER_PICKING', 'DEMO_OP_REPAIR', 1, @seed_now),
    ('D17A0000000000000000000B', 'OTHER_PICKING', 'DEMO_OP_SCRAP', 1, @seed_now),
    ('D17A0000000000000000000C', 'OTHER_PICKING', 'DEMO_OP_RETURN', 1, @seed_now);

INSERT INTO loan_events (id, loan_id, event_type, actor_id, occurred_at, warehouse_code, location_code) VALUES
    ('DEMO_EVT06B', 'DEMO_LOAN06', 'BORROW', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 7 DAY), 'DEMO_WH', 'DEMO_A01'),
    ('DEMO_EVT06R', 'DEMO_LOAN06', 'RETURN', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 1 DAY), 'DEMO_WH', 'DEMO_A01'),
    ('DEMO_EVT08B', 'DEMO_LOAN08', 'BORROW', 'DEMO_USER', DATE_SUB(@seed_now, INTERVAL 7 DAY), 'DEMO_WH', 'DEMO_A01');

INSERT INTO overdue_alerts (alert_id, loan_id, alert_type, status, handling_status, created_at, closed_at) VALUES
    ('DEMO_ALERT08', 'DEMO_LOAN08', '超期未还', '提醒中', '待处理', DATE_SUB(@seed_now, INTERVAL 2 DAY), NULL);

INSERT INTO operation_logs (id, actor_id, module, action, order_no, line_no, rfid, before_data, after_data, occurred_at) VALUES
    ('DEMO_LOG03', 'DEMO_USER', 'putaway', 'CONFIRM', 'DEMO_PA01', 3, 'D17A00000000000000000003', NULL, JSON_OBJECT('inventoryStatus','在库','location','DEMO_A01'), @seed_now),
    ('DEMO_LOG08', 'DEMO_USER', 'tempBorrow', 'CONFIRM', 'DEMO_TB02', 1, 'D17A00000000000000000008', NULL, JSON_OBJECT('inventoryStatus','已借出'), DATE_SUB(@seed_now, INTERVAL 7 DAY));

COMMIT;
