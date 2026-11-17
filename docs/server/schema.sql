CREATE TABLE warehouses (
    code VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    default_marker TINYINT GENERATED ALWAYS AS
        (CASE WHEN is_default THEN 1 ELSE NULL END) STORED,
    UNIQUE KEY uk_default_warehouse (default_marker)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE locations (
    warehouse_code VARCHAR(64) NOT NULL,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (warehouse_code, code),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE organizations (
    code VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_code VARCHAR(64) NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (parent_code) REFERENCES organizations(code),
    KEY ix_org_parent (parent_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE roles (
    code VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    data_scope ENUM('ALL','ORG_TREE','SELF') NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE permissions (
    code VARCHAR(64) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE role_permissions (
    role_code VARCHAR(64) NOT NULL,
    permission_code VARCHAR(64) NOT NULL,
    PRIMARY KEY (role_code, permission_code),
    FOREIGN KEY (role_code) REFERENCES roles(code),
    FOREIGN KEY (permission_code) REFERENCES permissions(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    employee_no VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_code VARCHAR(64) NOT NULL,
    organization_code VARCHAR(64) NOT NULL,
    avatar_url VARCHAR(2048) NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_employee (employee_no),
    FOREIGN KEY (role_code) REFERENCES roles(code),
    FOREIGN KEY (organization_code) REFERENCES organizations(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE auth_sessions (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    token_hash BINARY(32) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    expires_at DATETIME(3) NOT NULL,
    revoked_at DATETIME(3) NULL,
    UNIQUE KEY uk_token_hash (token_hash),
    KEY ix_session_user_expiry (user_id, expires_at),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE materials (
    code VARCHAR(64) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    specification VARCHAR(255) NULL,
    unit VARCHAR(32) NOT NULL,
    category VARCHAR(100) NULL,
    requires_inspection BOOLEAN NULL,
    inspection_cycle_months INT NULL,
    updated_at DATETIME(3) NOT NULL,
    CHECK (inspection_cycle_months IS NULL OR inspection_cycle_months > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE inventory (
    rfid VARCHAR(128) PRIMARY KEY,
    material_code VARCHAR(64) NOT NULL,
    batch_no VARCHAR(64) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    location_code VARCHAR(64) NULL,
    inventory_status VARCHAR(32) NOT NULL,
    tool_condition ENUM('好件','坏件') NOT NULL,
    fault_description VARCHAR(1000) NULL,
    first_inspection_date DATE NULL,
    next_inspection_date DATE NULL,
    deleted_at DATETIME(3) NULL,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,
    FOREIGN KEY (material_code) REFERENCES materials(code),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code),
    FOREIGN KEY (warehouse_code, location_code) REFERENCES locations(warehouse_code, code),
    KEY ix_inventory_available (warehouse_code, inventory_status, tool_condition, deleted_at, updated_at),
    KEY ix_inventory_material (material_code, warehouse_code, updated_at),
    KEY ix_inventory_inspection (warehouse_code, next_inspection_date),
    CHECK (inventory_status IN ('在库','待拣选','已备货','已借出','待上架','已送检','已送修','已报废','已退货','已遗失','盘亏'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE inbound_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    source_no VARCHAR(64) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_by VARCHAR(64) NULL,
    created_at DATETIME(3) NOT NULL,
    UNIQUE KEY uk_inbound_source (source_no),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE inbound_lines (
    order_no VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    material_code VARCHAR(64) NOT NULL,
    batch_no VARCHAR(64) NOT NULL,
    quantity DECIMAL(18,3) NOT NULL,
    PRIMARY KEY (order_no, line_no),
    FOREIGN KEY (order_no) REFERENCES inbound_orders(order_no),
    FOREIGN KEY (material_code) REFERENCES materials(code),
    CHECK (line_no > 0 AND quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE work_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    organization_code VARCHAR(64) NOT NULL,
    workshop VARCHAR(100) NOT NULL,
    team VARCHAR(100) NOT NULL,
    planned_at DATETIME(3) NOT NULL,
    FOREIGN KEY (organization_code) REFERENCES organizations(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE borrow_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    work_order_no VARCHAR(64) NOT NULL,
    borrower_id VARCHAR(64) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    UNIQUE KEY uk_borrow_work_order (work_order_no),
    FOREIGN KEY (work_order_no) REFERENCES work_orders(order_no),
    FOREIGN KEY (borrower_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE borrow_lines (
    order_no VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    material_code VARCHAR(64) NOT NULL,
    quantity DECIMAL(18,3) NOT NULL,
    PRIMARY KEY (order_no, line_no),
    FOREIGN KEY (order_no) REFERENCES borrow_orders(order_no),
    FOREIGN KEY (material_code) REFERENCES materials(code),
    CHECK (line_no > 0 AND quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE picking_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    borrow_order_no VARCHAR(64) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    status ENUM('UNPICKED','PARTIALLY_PICKED','PICKED','CLOSED') NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    closed_at DATETIME(3) NULL,
    FOREIGN KEY (borrow_order_no) REFERENCES borrow_orders(order_no),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code),
    FOREIGN KEY (created_by) REFERENCES users(id),
    KEY ix_picking_status (warehouse_code, status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE picking_lines (
    order_no VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    borrow_order_no VARCHAR(64) NOT NULL,
    borrow_line_no INT NOT NULL,
    rfid VARCHAR(128) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    recommended_location VARCHAR(64) NULL,
    picked_at DATETIME(3) NULL,
    picked_by VARCHAR(64) NULL,
    PRIMARY KEY (order_no, line_no),
    UNIQUE KEY uk_picking_rfid (order_no, rfid),
    FOREIGN KEY (order_no) REFERENCES picking_orders(order_no),
    FOREIGN KEY (borrow_order_no, borrow_line_no) REFERENCES borrow_lines(order_no, line_no),
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    FOREIGN KEY (warehouse_code, recommended_location) REFERENCES locations(warehouse_code, code),
    FOREIGN KEY (picked_by) REFERENCES users(id),
    CHECK (line_no > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE return_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    borrow_order_no VARCHAR(64) NOT NULL,
    returner_id VARCHAR(64) NULL,
    returned_at DATETIME(3) NULL,
    created_by VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    FOREIGN KEY (borrow_order_no) REFERENCES borrow_orders(order_no),
    FOREIGN KEY (returner_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE return_lines (
    order_no VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    rfid VARCHAR(128) NOT NULL,
    loan_id VARCHAR(64) NOT NULL,
    returned_at DATETIME(3) NOT NULL,
    returner_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (order_no, line_no),
    UNIQUE KEY uk_return_rfid (order_no, rfid),
    FOREIGN KEY (order_no) REFERENCES return_orders(order_no),
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    FOREIGN KEY (returner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE putaway_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    origin_kind ENUM('INBOUND','WORKORDER_RETURN') NOT NULL,
    inbound_order_no VARCHAR(64) NULL,
    return_order_no VARCHAR(64) NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    FOREIGN KEY (inbound_order_no) REFERENCES inbound_orders(order_no),
    FOREIGN KEY (return_order_no) REFERENCES return_orders(order_no),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code),
    FOREIGN KEY (created_by) REFERENCES users(id),
    KEY ix_putaway_status (warehouse_code, origin_kind, status, created_at),
    CHECK ((origin_kind='INBOUND' AND inbound_order_no IS NOT NULL AND return_order_no IS NULL)
        OR (origin_kind='WORKORDER_RETURN' AND return_order_no IS NOT NULL AND inbound_order_no IS NULL))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE putaway_lines (
    order_no VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    inbound_order_no VARCHAR(64) NULL,
    inbound_line_no INT NULL,
    return_order_no VARCHAR(64) NULL,
    return_line_no INT NULL,
    rfid VARCHAR(128) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    recommended_location VARCHAR(64) NULL,
    actual_location VARCHAR(64) NULL,
    putaway_at DATETIME(3) NULL,
    putaway_by VARCHAR(64) NULL,
    PRIMARY KEY (order_no, line_no),
    UNIQUE KEY uk_putaway_rfid (order_no, rfid),
    FOREIGN KEY (order_no) REFERENCES putaway_orders(order_no),
    FOREIGN KEY (inbound_order_no, inbound_line_no) REFERENCES inbound_lines(order_no, line_no),
    FOREIGN KEY (return_order_no, return_line_no) REFERENCES return_lines(order_no, line_no),
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    FOREIGN KEY (warehouse_code, recommended_location) REFERENCES locations(warehouse_code, code),
    FOREIGN KEY (warehouse_code, actual_location) REFERENCES locations(warehouse_code, code),
    FOREIGN KEY (putaway_by) REFERENCES users(id),
    CHECK (line_no > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE temp_borrow_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    request_id CHAR(36) NOT NULL,
    request_hash BINARY(32) NOT NULL,
    work_order_no VARCHAR(64) NULL,
    borrower_id VARCHAR(64) NOT NULL,
    workshop VARCHAR(100) NOT NULL,
    team VARCHAR(100) NOT NULL,
    remark VARCHAR(1000) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    UNIQUE KEY uk_temp_request (created_by, request_id),
    FOREIGN KEY (borrower_id) REFERENCES users(id),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE temp_borrow_lines (
    order_no VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    rfid VARCHAR(128) NOT NULL,
    borrowed_at DATETIME(3) NULL,
    borrowed_by VARCHAR(64) NULL,
    PRIMARY KEY (order_no, line_no),
    UNIQUE KEY uk_temp_borrow_rfid (order_no, rfid),
    FOREIGN KEY (order_no) REFERENCES temp_borrow_orders(order_no),
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    FOREIGN KEY (borrowed_by) REFERENCES users(id),
    CHECK (line_no > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE loan_records (
    id VARCHAR(64) PRIMARY KEY,
    origin_kind ENUM('WORKORDER','TEMPORARY') NOT NULL,
    borrow_order_no VARCHAR(64) NOT NULL,
    borrow_line_no INT NOT NULL,
    work_order_no VARCHAR(64) NULL,
    rfid VARCHAR(128) NOT NULL,
    borrower_id VARCHAR(64) NOT NULL,
    operator_id VARCHAR(64) NOT NULL,
    borrowed_at DATETIME(3) NOT NULL,
    due_at DATETIME(3) NOT NULL,
    status ENUM('BORROWED','RETURNED','LOST') NOT NULL,
    returned_at DATETIME(3) NULL,
    returner_id VARCHAR(64) NULL,
    active_rfid VARCHAR(128) GENERATED ALWAYS AS
        (CASE WHEN status='BORROWED' THEN rfid ELSE NULL END) STORED,
    UNIQUE KEY uk_active_loan (active_rfid),
    UNIQUE KEY uk_loan_origin (origin_kind, borrow_order_no, borrow_line_no, rfid),
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    FOREIGN KEY (borrower_id) REFERENCES users(id),
    FOREIGN KEY (operator_id) REFERENCES users(id),
    FOREIGN KEY (returner_id) REFERENCES users(id),
    KEY ix_loan_due (status, due_at),
    KEY ix_loan_borrower (borrower_id, status, borrowed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE temp_return_orders (
    order_no VARCHAR(64) PRIMARY KEY,
    origin_kind ENUM('WORKORDER','TEMPORARY') NOT NULL,
    borrow_order_no VARCHAR(64) NOT NULL,
    work_order_no VARCHAR(64) NULL,
    returner_id VARCHAR(64) NULL,
    default_location VARCHAR(64) NULL,
    remark VARCHAR(1000) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    UNIQUE KEY uk_temp_return_borrow (origin_kind, borrow_order_no),
    FOREIGN KEY (returner_id) REFERENCES users(id),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code),
    FOREIGN KEY (warehouse_code, default_location) REFERENCES locations(warehouse_code, code),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE temp_return_lines (
    order_no VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    loan_id VARCHAR(64) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    actual_location VARCHAR(64) NULL,
    returned_at DATETIME(3) NULL,
    returned_by VARCHAR(64) NULL,
    returner_id VARCHAR(64) NULL,
    PRIMARY KEY (order_no, line_no),
    UNIQUE KEY uk_temp_return_loan (loan_id),
    FOREIGN KEY (order_no) REFERENCES temp_return_orders(order_no),
    FOREIGN KEY (loan_id) REFERENCES loan_records(id),
    FOREIGN KEY (warehouse_code, actual_location) REFERENCES locations(warehouse_code, code),
    FOREIGN KEY (returned_by) REFERENCES users(id),
    FOREIGN KEY (returner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE inventory_reservations (
    rfid VARCHAR(128) PRIMARY KEY,
    owner_kind ENUM('PICKING','TEMPORARY','OTHER_PICKING') NOT NULL,
    owner_order_no VARCHAR(64) NOT NULL,
    owner_line_no INT NOT NULL,
    reserved_at DATETIME(3) NOT NULL,
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    KEY ix_reservation_owner (owner_kind, owner_order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE loan_events (
    id VARCHAR(64) PRIMARY KEY,
    loan_id VARCHAR(64) NOT NULL,
    event_type ENUM('BORROW','RETURN') NOT NULL,
    actor_id VARCHAR(64) NOT NULL,
    occurred_at DATETIME(3) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    location_code VARCHAR(64) NULL,
    UNIQUE KEY uk_loan_event (loan_id, event_type),
    FOREIGN KEY (loan_id) REFERENCES loan_records(id),
    FOREIGN KEY (actor_id) REFERENCES users(id),
    FOREIGN KEY (warehouse_code, location_code) REFERENCES locations(warehouse_code, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

ALTER TABLE return_lines
    ADD CONSTRAINT fk_return_line_loan FOREIGN KEY (loan_id) REFERENCES loan_records(id),
    ADD UNIQUE KEY uk_return_line_loan (loan_id);

CREATE TABLE other_tasks (
    id VARCHAR(64) PRIMARY KEY,
    task_kind ENUM('PICKING','PUTAWAY') NOT NULL,
    source_type ENUM('TEMPORARY','INSPECTION','REPAIR','SCRAP','RETURN') NOT NULL,
    source_no VARCHAR(64) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    source_ready BOOLEAN NOT NULL DEFAULT FALSE,
    source_date DATETIME(3) NOT NULL,
    closed_at DATETIME(3) NULL,
    created_at DATETIME(3) NOT NULL,
    UNIQUE KEY uk_other_source (task_kind, source_type, source_no),
    FOREIGN KEY (warehouse_code) REFERENCES warehouses(code),
    KEY ix_other_list (warehouse_code, task_kind, source_type, source_date),
    CHECK (task_kind='PICKING' OR source_type IN ('INSPECTION','REPAIR'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE other_task_lines (
    task_id VARCHAR(64) NOT NULL,
    line_no INT NOT NULL,
    rfid VARCHAR(128) NOT NULL,
    warehouse_code VARCHAR(64) NOT NULL,
    original_location VARCHAR(64) NULL,
    actual_location VARCHAR(64) NULL,
    completed_at DATETIME(3) NULL,
    completed_by VARCHAR(64) NULL,
    PRIMARY KEY (task_id, line_no),
    UNIQUE KEY uk_other_rfid (task_id, rfid),
    FOREIGN KEY (task_id) REFERENCES other_tasks(id),
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    FOREIGN KEY (warehouse_code, original_location) REFERENCES locations(warehouse_code, code),
    FOREIGN KEY (warehouse_code, actual_location) REFERENCES locations(warehouse_code, code),
    FOREIGN KEY (completed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE overdue_alerts (
    alert_id VARCHAR(64) PRIMARY KEY,
    loan_id VARCHAR(64) NOT NULL,
    alert_type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NULL,
    handling_status VARCHAR(32) NULL,
    created_at DATETIME(3) NOT NULL,
    closed_at DATETIME(3) NULL,
    UNIQUE KEY uk_overdue_loan (loan_id),
    FOREIGN KEY (loan_id) REFERENCES loan_records(id),
    KEY ix_alert_status (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;

CREATE TABLE operation_logs (
    id VARCHAR(64) PRIMARY KEY,
    actor_id VARCHAR(64) NULL,
    module VARCHAR(64) NOT NULL,
    action VARCHAR(64) NOT NULL,
    order_no VARCHAR(64) NULL,
    line_no INT NULL,
    rfid VARCHAR(128) NULL,
    before_data JSON NULL,
    after_data JSON NULL,
    occurred_at DATETIME(3) NOT NULL,
    FOREIGN KEY (actor_id) REFERENCES users(id),
    FOREIGN KEY (rfid) REFERENCES inventory(rfid),
    KEY ix_log_order (module, order_no, occurred_at),
    KEY ix_log_rfid (rfid, occurred_at),
    KEY ix_log_actor (actor_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;
