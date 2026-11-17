# PDA 服务端接口契约

版本：2026-11-17。供 Node.js + Express + MySQL 8 服务端实现，覆盖本仓库正在使用的 PDA 页面。下列 HTTP 地址、JSON 名称及数据库英文名称是本次经用户授权制定的协议，以客户端 `data/*/ApiService.kt` 和 `domain/*/Model.kt` 为实现依据。

业务依据为《工器具库系统文档v2.docx》的正文及内嵌流程图、状态图、关系图。PC 管理页面、门禁设备和上游系统集成由服务端另行实现，PDA 不直接调用上游 IF 接口。

## 已确认的业务口径

- 工单拣选成功：明细已拣选、库存已备货。实际借出由一体机交接完成，不能在 PDA 拣选时记为已借出。
- 工单归还上架处理独立上架单；`ReturnOrder.orderNo` 是上架单号，`returnOrderNo` 是关联归还单号。逐件确认实际货位，成功一件即入库一件。
- 临时借出：保存单据后生成其他拣选任务，整单拣选完成后逐件确认借出。借出人、归还人使用人员工号输入；操作经办人始终来自登录身份。
- 用户于 2026-11-17 确认：临时归还逐件记录实际货位，直接恢复在库，不再生成其他上架任务。此规则替代文档 1.5.2 中“转待上架”的冲突分支。
- 用户于 2026-11-17 选择方案 A：报废、退货出库为终态；其他上架不支持 `SCRAP`、`RETURN`。重新入库走普通入库流程，使用新 RFID，旧记录保持逻辑删除及历史状态。
- 仓库由服务端带出并锁定；实际货位可以调整，但必须属于该仓库且有效。坏件、已安排待送检的器具不能通过普通归还上架恢复可借用状态。
- Room 只缓存查询结果。业务提交必须联网，不实现离线提交队列。

## 公共协议

基础地址为 `http(s)://主机:端口/api/v1/`，所有路径均相对于该地址。服务器配置只保存在设备 DataStore，不设置远程“保存 PDA IP”接口。客户端允许主机输入显式 `https://`；端口单独输入。

请求和响应使用 UTF-8 JSON。成功响应必须有 `data`，包括无业务数据的退出登录；不返回空的 204。成功使用 200，新建临时借出单也可返回 201。响应不得把中文显示文字作为已有枚举的替代编码。

```json
{"data":{"status":"ok"}}
```

错误使用相应 HTTP 状态，响应形状如下。`code` 是稳定的英文业务码，`message` 是可直接显示给用户的中文说明，不返回 SQL、密码或堆栈。

```json
{"error":{"code":"LOCATION_NOT_IN_WAREHOUSE","message":"实际货位不属于本单据的目标仓库"}}
```

| HTTP 状态 | 使用场景 |
| --- | --- |
| 400 | JSON、字段类型、分页、空字符串或重复 RFID 不合法 |
| 401 | 缺少、过期、已注销的 Token，账号已停用；客户端清理对应登录会话 |
| 403 | 缺少功能权限或不属于授权组织的数据 |
| 404 | 在当前授权范围内找不到单据、RFID 或货位 |
| 409 | 状态已变化、库存被占用、来源不合法、重复请求内容不同 |
| 429 | 登录或请求频率超过服务端限制 |
| 500 / 503 | 服务端故障或尚未就绪；只有查询允许使用本机缓存 |

推荐业务码：`VALIDATION_ERROR`、`INVALID_CREDENTIALS`、`UNAUTHENTICATED`、`FORBIDDEN`、`NOT_FOUND`、`ORDER_CLOSED`、`STATE_CONFLICT`、`RFID_NOT_IN_ORDER`、`RFID_RESERVED`、`LOCATION_NOT_IN_WAREHOUSE`、`TOOL_BLOCKED`、`UNSUPPORTED_SOURCE`、`REQUEST_ID_REUSED`。业务异常不能包装成 HTTP 200。

### 身份、权限和字段约束

除 `health` 和 `auth/login` 外均要求 `Authorization: Bearer <token>`。本协议采用不可预测的随机会话 Token，数据库只保存其 SHA-256 摘要；建议有效期 8 小时，准确到期时间由 `expiresAt` 返回。不设置刷新 Token 接口，过期后重新登录。退出时撤销该 Token。用户、角色或组织停用后，每次业务请求都应立即拒绝。

`userId`、单号、物资编码、组织编码、仓库及货位编码均为字符串；工单拣选、归还上架、临时借还的行号为整数。普通入库上架沿用当前模型的字符串行号。客户端不得把行号转成全局主键。

RFID 为服务端保存的规范编码，不带首尾空白。输入匹配可忽略字母大小写；响应返回数据库中的规范形式。不能给同一 RFID 重新建档、物理删除或复用历史 RFID。一般编码长度不超过 64，RFID 不超过 128，姓名不超过 100，备注和故障说明不超过 1000 字符；服务器按数据库实际列长度校验。

时间戳统一为带时区的 ISO 8601 UTC 字符串，例如 `2026-11-17T01:30:00Z`；纯日期为 `yyyy-MM-dd`。业务自然日按 `Asia/Shanghai` 解释。尚未发生的时间在非空字符串字段中返回 `""`，明确可空的字段返回 `null`。所有发生时间、流水号、单号和数量汇总由服务器生成。

权限码：`putaway.read/write`、`picking.read/write`、`returns.read/write`、`tempBorrow.read/write`、`tempReturn.read/write`、`otherPicking.read/write`、`otherPutaway.read/write`、`inventory.read/write`、`overdueAlerts.read`。这里的斜线表示两个独立权限，例如 `picking.read` 和 `picking.write`。角色名称仅用于显示，授权依据权限表和组织范围，不能依赖客户端传来的角色、姓名、仓库或车间文字。

### 列表分页

下文所有列表 GET 接口接收 `page`（从 1 开始）和 `pageSize`（默认 100，最大 200）。返回统一分页对象：

```json
{"data":{"items":[],"page":1,"pageSize":100,"total":0}}
```

`total` 是授权及仓库范围内的总条数，不是当前页条数。排序必须稳定，并以唯一标识作为最终排序键。PDA 当前按页读取完整授权列表，再执行已有的组合筛选；不能只返回首批固定模拟记录。单据明细为一件一行，数量根据完整明细统计，不根据当前页或本次读签数量计算。

客户端仅对网络故障和 5xx 降级到相同服务器、相同账号的缓存；401、403、409、响应结构错误不能用旧缓存掩盖。所有写接口始终访问服务端。

### 事务与重复提交

每个确认接口应在同一数据库事务内校验权限、锁定目标单据和库存、核对 RFID、变更本批明细与库存、汇总单据进度、写业务流水与操作日志。批次中任意一件失败，整批回滚，并在错误说明中指出对应 RFID。

单件确认及批次确认必须具有业务幂等性：已由同一单据完成且业务参数一致时，返回当前权威结果，不再次增加数量、不重复写流水；与首次实际货位、归属单据等实质参数冲突时返回 409。不能仅在前端禁用按钮。

临时借出创建额外使用 `requestId`：同账号、相同请求标识和内容重复提交返回同一单据；同标识但不同内容返回 409 `REQUEST_ID_REUSED`。网络超时不代表业务回滚，客户端会提示刷新核对结果。服务器不得把同一批 RFID 分配给多个未完成任务。

## 公共接口

### GET health

无需认证。真实检查 API 和业务数据库是否就绪；正常返回 `{"data":{"status":"ok"}}`，异常返回 503。配置测试不能仅检查输入是否非空。

### POST auth/login

请求 `LoginRequest`，响应 `LoginResponse`。校验账号、密码、人员/组织/角色启用状态；只保存密码的安全哈希。服务端必须提供一个有效默认仓库，登录响应的 `warehouseCode`、`warehouseName` 来自该配置。

```json
{"username":"demo-material","password":"<用户输入>"}
```

```json
{"data":{"userId":"u-demo","userName":"示例材料员","userRole":"材料员","token":"<随机会话凭证>","expiresAt":"2026-11-17T09:30:00Z","warehouseCode":"CK001","warehouseName":"示例工具库","permissions":["putaway.read","putaway.write","picking.read","picking.write"],"avatarUrl":""}}
```

### POST auth/logout

撤销当前 Token，成功返回 `{"data":{"status":"ok"}}`。PDA 离线退出仍会清除本机凭证和本账号缓存，服务端会话按有效期失效。

### GET me

响应 `DashboardData`，包含人员姓名、角色显示名、可选头像和当前权限集合。不要返回密码、Token 或前端图标、Compose 路由、菜单布局。PDA 按权限过滤本地菜单。

### GET locations/{code}?warehouse={warehouseCode}

响应 `PutawayLocation`。查询指定仓库的有效货位；不存在或已停用返回 404，属于别的仓库返回 409。`warehouse` 是仓库编码。普通上架在绑定时调用此接口，最终提交时服务端必须再次校验。

## 普通入库上架

| 方法和路径 | 请求 | 响应 data |
| --- | --- | --- |
| GET putaway-orders | 公共分页 | Page<PutawayOrder>，每单包含完整 details |
| GET putaway-orders/{orderNo} | 上架单号 | PutawayOrder |
| POST putaway-orders/confirm | PutawayRequest | 更新后的完整 PutawayOrder |

`inboundOrderNo` 关联已完成 RFID 绑定的入库单。接口仅返回当前授权范围的上架任务。异常来源单不能启动作业。

确认请求只提交 `orderNo`、`lineNo`、`rfid`、`location`。经办人由 Token 确定，不接受前端伪造操作人。核对来源入库行、单件上架行及 RFID 三者关联；有效货位必须属于明细锁定的仓库。成功后本件明细 `待上架 → 已上架`、库存 `待上架 → 在库`，保存实际货位、经办人和服务器时间；表头按完整明细汇总为待上架、部分上架或已上架。

## 工单拣选

| 方法和路径 | 请求 | 响应 data |
| --- | --- | --- |
| GET picking-orders | 公共分页 | Page<PickingOrder> |
| GET picking-orders/{orderNo} | 拣选单号 | PickingOrderDetail：order + 全部 tools |
| POST picking-orders/confirm | PickingConfirmRequest：orderNo + rfids | 更新后的 PickingOrderDetail |
| POST picking-orders/{orderNo}/close | 无 body | 更新后的 PickingOrderDetail |

必须按所选单号加载对应工具，不能复用其他单据的固定明细。`id` 是本单内的整数行号，`borrowOrderNo` 是关联借用单号；`workshop`、`team` 分别为车间、班组显示名称。

服务端生成拣选任务时按文档指定 RFID：从符合条件的在库好件中按最后更新时间倒序推荐并原子占用。确认必须匹配该行指定 RFID，不允许替换同型号其他工具。已送检、已送修、遗失、报废、退货等器具不能分配；已被其他单据占用的 RFID 不可重复分配。

确认仅对本批有效未完成行执行 `completed=true`、记录完成时间，库存变为 `已备货`；不产生实际借出流水或核销领用。实际交接时的已借出及借还流水由一体机/服务端业务产生。

关闭仅允许尚有未完成行且未关闭的单据。保留已拣记录；剩余行不再允许拣选，释放其分配占用。重复关闭返回当前状态。已全部拣选单不能再关闭。

## 工单归还上架

| 方法和路径 | 请求 | 响应 data |
| --- | --- | --- |
| GET return-putaway-orders | 公共分页 | Page<ReturnOrder> |
| GET return-putaway-orders/{orderNo} | 独立上架单号 | ReturnOrderDetail：order + 全部 tools |
| POST return-putaway-orders/confirm | ReturnConfirmRequest | 更新后的 ReturnOrderDetail |

`orderNo` 是上架单号，`returnOrderNo` 是关联归还单号，不能使用借用单号代替。`returnedCount`、`pendingCount` 在此接口分别表示已上架、待上架数量，沿用客户端字段名称。

```json
{"orderNo":"SJ-DEMO-001","lineNo":1,"rfid":"464C30310000000000000100","locationCode":"A0101"}
```

一条请求只处理一件。校验本件确实已归还且待上架、属于本上架单、仓库和实际货位有效。成功后库存立即恢复在库，记录实际货位、上架人和上架时间，再汇总完整上架单数量。

`canProcess`、`blockedReason` 来自服务端业务判断：归还时已登记坏件、正在等待送检或其他不允许普通上架的件应返回 `canProcess=false`，保留在原单及总数量内，转由相应送修/送检业务处理。PDA 不得删除异常行、虚增完成数或自动关闭整单。

## 临时借还

| 方法和路径 | 请求 | 响应 data |
| --- | --- | --- |
| GET temp/available-tools | 公共分页 | Page<TempTool>，仅有效、可用、未被占用的在库好件 |
| GET temp/borrow-orders | 公共分页 | Page<TempBorrowOrder>，包含全部 lines |
| POST temp/borrow-orders | TempBorrowRequest | 服务端生成的 TempBorrowOrder |
| POST temp/borrow-orders/confirm | TempBorrowConfirmRequest | 更新后的完整 TempBorrowOrder |
| GET temp/return-orders | 公共分页 | Page<TempReturnOrder>，包含全部 lines |
| POST temp/return-orders/confirm | TempReturnConfirmRequest | 更新后的完整 TempReturnOrder |

### 创建与借出

`borrowerEmployeeNo` 必须解析到启用人员，`rfids` 非空且不能重复；`workOrderNo` 可为空字符串，支持无工单临时业务。`workshop`、`team`、`remark` 是记录用文字，不作为客户端声明的数据权限。经办人、单号、创建时间由服务端生成。

创建时原子占用所有目标 RFID、创建临时借出明细和 `TEMPORARY` 来源的其他拣选任务。未完成任务的器具不能再被其他单据选择。响应中每行的 `picked` 根据关联拣选结果提供，不能由前端自己标记。

逐件确认借出前，要求本单全部行已经完成 PDA 拣选；核对 `orderNo + lineNo + rfid`。本件成功后库存 `已备货 → 已借出`，写经办人、借用人、借出时间、应还时间和借出流水，自动生成/追加关联临时归还单的待还行。同一借出单的归还单不应因分批借出而重复生成。

### 逐件直接归还入库

请求中的 `returnerEmployeeNo`、实际 `location` 必填；工单号可为空。列表可包含临时借出和正常工单借出的人工兜底待还记录，按原借用单、借出记录和 RFID 找到当前尚未归还的记录。已有工单关联不能被请求中的文字擅自替换。只允许本单待还件；货位须属于本件原锁定仓库且有效。

事务内核销本件借出记录、记录归还人/经办人/时间及本件实际货位、库存直接 `已借出 → 在库`，生成归还流水并关闭对应超期告警。`TempReturnLine.location` 保存本件货位，不能用表头最后一次输入覆盖其他已归还行。部分归还保留其他待还行。

此接口不创建“其他上架”任务，返回成功后即可进入下一轮借用。好坏件保持已有记录；不能因归还而把坏件擅自改为好件，可借用资格仍需同时满足好件及无其他占用。

## 其他作业

| 方法和路径 | 请求 | 响应 data |
| --- | --- | --- |
| GET other-picking-tasks | 公共分页 | Page<OtherPickingTask> |
| POST other-picking-tasks/confirm | OtherPickingRequest | 更新后的完整 OtherPickingTask |
| GET other-putaway-tasks | 公共分页 | Page<OtherPutawayTask> |
| POST other-putaway-tasks/confirm | OtherPutawayRequest | 更新后的完整 OtherPutawayTask |

任务身份为 `sourceType + sourceNo`，不得只按可能重号的来源单号查找。任务由相应来源业务在服务端生成，PDA 只查询和执行；不存在前端自己新建送修、送检、报废审批单的接口。

其他拣选请求提交本批次 `rfids`。成功后的库存目标：`TEMPORARY → 已备货`；`INSPECTION → 已送检`；`REPAIR → 已送修`；`SCRAP → 已报废并逻辑删除`；`RETURN → 已退货并逻辑删除`。报废必须满足在库坏件及来源批准条件，退货满足有效在库及来源批准条件。临时拣选不等于确认借出；其他来源不得错误套用临时借出终态。

其他上架请求必须使用 `items: [{rfid, location}]`，逐件绑定实际货位，不能把一个公共货位用于整批。仅支持 `INSPECTION`、`REPAIR`，任务只可由已登记合格返回的件生成；库存待上架，确认后恢复在库。质量不允许的件返回阻断原因。临时归还直接入库，不创建此类上架任务；`TEMPORARY`、`SCRAP`、`RETURN` 均不属于本接口合法来源，必须返回 400/409，不能复活旧库存。

## 库存和超期告警

| 方法和路径 | 请求 | 响应 data |
| --- | --- | --- |
| GET inventory | 公共分页 | Page<StockDetail> |
| PATCH inventory/{rfid}/condition | `{ "condition": "好件" }` 或 `坏件` | 更新后的完整 StockDetail |
| GET overdue-alerts | 公共分页 | Page<OverdueAlert> |

库存查询返回当前默认仓库、授权范围内的单件记录；逻辑删除的报废、退货、遗失库存不列为可用库存。下次送检日期由服务端提供，不能根据设备模拟日期计算。

好坏件变更只修改请求的质量状态并写审计日志；不自动修改库存流转状态，不凭此解除送检、送修或分配占用，也不自动清除故障描述。后续维修完成时按实际维修结果更新故障描述。变更权限由服务器检查。

超期告警仅查询展示。`overdueDays`、`status`、`handlingStatus` 由服务端提供；生成、催还、闭环及定时任务不在 PDA 执行。告警状态可为提醒中、已催还、已闭环，处理状态独立保存，不能把两者硬映射为同一枚举。

## 枚举和展示值

| 类型 | JSON 值 | 页面显示 |
| --- | --- | --- |
| PickingStatus | UNPICKED / PARTIALLY_PICKED / PICKED / CLOSED | 未拣选 / 部分拣选 / 已拣选 / 已关闭 |
| ReturnStatus | UNRETURNED / PARTIALLY_RETURNED / RETURNED | 待上架 / 部分上架 / 已上架 |
| 其他拣选 PickingStatus | PENDING / PARTIALLY / COMPLETED / CLOSED | 待拣选 / 部分拣选 / 已拣选 / 已关闭 |
| 其他上架 PutawayStatus | PENDING / PARTIALLY / COMPLETED / CLOSED | 待上架 / 部分上架 / 已上架 / 已关闭 |
| 其他拣选 SourceType | TEMPORARY / INSPECTION / REPAIR / SCRAP / RETURN | 临时 / 送检 / 送修 / 报废 / 退货 |
| 其他上架 SourceType | INSPECTION / REPAIR | 送检归还 / 送修归还 |

`PutawayOrder.status` 使用中文字符串：待上架、部分上架、已上架、异常；其明细 `status` 为待上架、已上架。库存及质量当前模型使用中文字符串：在库、待拣选、已备货、已借出、待上架、已送检、已送修、已报废、已退货、已遗失、盘亏；好件、坏件。服务端应严格按这些当前模型值返回，内部列值可通过明确映射实现。

`Other*Task.completedCount/status`、`PutawayOrder.completedCount/isCompleted`、`Temp*Order.status` 是客户端根据完整明细派生的展示值，不作为需要额外返回的构造字段。`isRead`、`readDate`、读签集合、输入草稿、筛选、弹窗、加载和提交标记都属于 UiState，不是接口或数据库业务列。

## 服务端实现交接顺序

1. 按数据库文档建立基础表、人员、角色权限、默认仓库与有效货位；提供测试环境账号及真实可执行来源任务。
2. 实现认证、health、me、货位校验，再实现查询接口，逐字段对照下方 DTO。
3. 实现单件和批次事务、并发占用、业务幂等、流水和日志；再验证部分完成、关闭、断线后核对和跨账号越权。
4. PC/一体机来源单据及上游 IF 对接负责创建入库、工单借用、交接、工单归还及送检/送修任务；不能让 PDA 用模拟数据补出不存在的业务单据。

## JSON 数据模型

本节由客户端领域模型生成。表中的类型表示 JSON 类型约束：`String` 为字符串，`Int` 为整数，`Boolean` 为布尔值，`List/Set` 为数组，`?` 表示允许 null；其他类型名引用对应模型。默认值只用于客户端兼容，服务端应返回完整业务字段，尤其是 `canProcess`、仓库、关联单号和数量。

### common.ServiceStatus

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| status | `String` | 业务状态或依明细派生的状态，参见枚举约定 |

### dashboard.DashboardData

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| userName | `String` | 人员显示名称 |
| userRole | `String` | 角色显示名称 |
| avatarUrl | `String` | 头像 URL；无头像为空字符串 |
| permissions | `Set<String>` | 当前授权功能码数组 |

### otherpicking.OtherPickingTask

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| sourceNo | `String` | 来源单据号 |
| sourceType | `SourceType` | 本模块来源枚举 |
| date | `String` | 任务来源时间 |
| tools | `List<OtherPickingTool>` | 当前单据完整器具行 |
| closed | `Boolean` | 来源任务是否关闭 |

### otherpicking.OtherPickingTool

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| rfid | `String` | 规范化 RFID |
| name | `String` | 器具名称 |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |
| completed | `Boolean` | 本行业务是否已完成，不是本次是否读签 |
| warehouse | `String` | 锁定仓库编码 |
| canProcess | `Boolean` | 服务端判定当前能否执行本业务 |
| blockedReason | `String` | 阻断原因；可执行时为空字符串 |

### otherpicking.OtherPickingRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| sourceNo | `String` | 来源单据号 |
| sourceType | `SourceType` | 本模块来源枚举 |
| rfids | `List<String>` | 本批 RFID 数组，不可为空或重复 |

### otherputaway.OtherPutawayTask

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| sourceNo | `String` | 来源单据号 |
| sourceType | `SourceType` | 本模块来源枚举 |
| date | `String` | 任务来源时间 |
| tools | `List<OtherPutawayTool>` | 当前单据完整器具行 |
| closed | `Boolean` | 来源任务是否关闭 |

### otherputaway.OtherPutawayTool

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| rfid | `String` | 规范化 RFID |
| name | `String` | 器具名称 |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |
| completed | `Boolean` | 本行业务是否已完成，不是本次是否读签 |
| warehouse | `String` | 锁定仓库编码 |
| canProcess | `Boolean` | 服务端判定当前能否执行本业务 |
| blockedReason | `String` | 阻断原因；可执行时为空字符串 |

### otherputaway.OtherPutawayRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| sourceNo | `String` | 来源单据号 |
| sourceType | `SourceType` | 本模块来源枚举 |
| items | `List<PutawayBinding>` | 本批逐件货位绑定 |

### otherputaway.PutawayBinding

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| rfid | `String` | 规范化 RFID |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |

### overduealert.OverdueAlert

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| alertId | `String` | 告警稳定标识 |
| alertType | `String` | 告警类型 |
| workOrderNo | `String` | 关联工单号；临时业务可为空 |
| borrowOrderNo | `String` | 关联借用单号 |
| borrower | `String` | 借用人显示名称 |
| toolName | `String` | 器具名称 |
| rfid | `String` | 规范化 RFID |
| itemCode | `String` | 物资编码 |
| borrowedAt | `String` | 本行实际借出 UTC 时间 |
| dueAt | `String` | 明确的应还 UTC 时间 |
| overdueDays | `Int?` | 服务端计算的超期天数 |
| status | `String?` | 业务状态或依明细派生的状态，参见枚举约定 |
| handlingStatus | `String?` | 独立处理状态 |

### picking.PickingOrder

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| status | `PickingStatus` | 业务状态或依明细派生的状态，参见枚举约定 |
| workshop | `String` | 车间显示文字 |
| borrower | `String` | 借用人显示名称 |
| pendingCount | `Int` | 尚未完成的完整单据行数 |
| plannedDate | `String` | 工单计划时间 |
| totalCount | `Int` | 完整单据总行数 |
| pickedCount | `Int` | 已拣选行数 |
| workOrderNo | `String` | 关联工单号；临时业务可为空 |
| createdBy | `String` | 创建人显示名称 |
| createdAt | `String` | 创建 UTC 时间 |
| borrowOrderNo | `String` | 关联借用单号 |
| team | `String` | 班组显示文字 |

### picking.PickingTool

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| id | `Int` | 本单明细整数行号 |
| name | `String` | 器具名称 |
| itemCode | `String` | 物资编码 |
| rfid | `String` | 规范化 RFID |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |
| unit | `String` | 计量单位 |
| warehouse | `String` | 锁定仓库编码 |
| condition | `String` | 好件或坏件 |
| completed | `Boolean` | 本行业务是否已完成，不是本次是否读签 |
| completedAt | `String` | 本行完成 UTC 时间；未完成为空字符串 |
| canProcess | `Boolean` | 服务端判定当前能否执行本业务 |
| blockedReason | `String` | 阻断原因；可执行时为空字符串 |

### picking.PickingOrderDetail

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| order | `PickingOrder` | 按模型所属业务解释 |
| tools | `List<PickingTool>` | 当前单据完整器具行 |

### picking.PickingConfirmRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| rfids | `List<String>` | 本批 RFID 数组，不可为空或重复 |

### putaway.PutawayOrder

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| inboundOrderNo | `String` | 关联入库单号 |
| orderNo | `String` | 本业务单据号 |
| status | `String` | 业务状态或依明细派生的状态，参见枚举约定 |
| operator | `String` | 服务端记录的上架经办人显示名称 |
| putawayTime | `String` | 最近一次上架 UTC 时间 |
| details | `List<PutawayDetail>` | 本单完整单件明细 |

### putaway.PutawayDetail

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| inboundLineNo | `String` | 来源入库行号 |
| lineNo | `String` | 本单内行号 |
| status | `String` | 业务状态或依明细派生的状态，参见枚举约定 |
| batchNo | `String` | 批次号 |
| rfid | `String` | 规范化 RFID |
| itemCode | `String` | 物资编码 |
| description | `String` | 物资描述 |
| unit | `String` | 计量单位 |
| warehouse | `String` | 锁定仓库编码 |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |
| inventoryStatus | `String` | 库存状态中文值 |

### putaway.PutawayRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| lineNo | `String` | 本单内行号 |
| rfid | `String` | 规范化 RFID |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |

### putaway.PutawayLocation

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| code | `String` | 货位编码或公共返回码，按模型定义 |
| warehouse | `String` | 锁定仓库编码 |

### returns.ReturnOrder

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| status | `ReturnStatus` | 业务状态或依明细派生的状态，参见枚举约定 |
| workshop | `String` | 车间显示文字 |
| returner | `String` | 归还人显示名称 |
| pendingCount | `Int` | 尚未完成的完整单据行数 |
| returnedDate | `String` | 关联归还时间 |
| totalCount | `Int` | 完整单据总行数 |
| returnedCount | `Int` | 在归还上架接口中表示已上架行数 |
| workOrderNo | `String` | 关联工单号；临时业务可为空 |
| createdBy | `String` | 创建人显示名称 |
| createdAt | `String` | 创建 UTC 时间 |
| returnOrderNo | `String` | 关联归还单号 |
| team | `String` | 班组显示文字 |

### returns.ReturnTool

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| id | `Int` | 本单明细整数行号 |
| name | `String` | 器具名称 |
| itemCode | `String` | 物资编码 |
| rfid | `String` | 规范化 RFID |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |
| unit | `String` | 计量单位 |
| warehouse | `String` | 锁定仓库编码 |
| condition | `String` | 好件或坏件 |
| completed | `Boolean` | 本行业务是否已完成，不是本次是否读签 |
| completedAt | `String` | 本行完成 UTC 时间；未完成为空字符串 |
| canProcess | `Boolean` | 服务端判定当前能否执行本业务 |
| blockedReason | `String` | 阻断原因；可执行时为空字符串 |

### returns.ReturnOrderDetail

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| order | `ReturnOrder` | 按模型所属业务解释 |
| tools | `List<ReturnTool>` | 当前单据完整器具行 |

### returns.ReturnConfirmRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| lineNo | `Int` | 本单内行号 |
| rfid | `String` | 规范化 RFID |
| locationCode | `String` | 本件实际货位编码 |

### session.LoginRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| username | `String` | 登录账号 |
| password | `String` | 用户输入密码，不保存 |

### session.LoginResponse

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| userId | `String` | 人员稳定标识 |
| userName | `String` | 人员显示名称 |
| userRole | `String` | 角色显示名称 |
| token | `String` | 原始 Bearer 会话凭证，仅登录返回 |
| expiresAt | `String` | 会话到期 UTC 时间 |
| warehouseCode | `String` | 锁定仓库编码 |
| warehouseName | `String` | 锁定仓库名称 |
| permissions | `Set<String>` | 当前授权功能码数组 |
| avatarUrl | `String` | 头像 URL；无头像为空字符串 |

### stockdetail.StockDetail

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| rfid | `String` | 规范化 RFID |
| itemCode | `String` | 物资编码 |
| description | `String` | 物资描述 |
| specification | `String?` | 规格型号 |
| unit | `String` | 计量单位 |
| category | `String?` | 物资分类 |
| inventoryStatus | `String` | 库存状态中文值 |
| condition | `String` | 好件或坏件 |
| faultDescription | `String?` | 当前故障描述 |
| warehouse | `String` | 锁定仓库编码 |
| location | `String?` | 按业务阶段定义的推荐或实际货位编码 |
| requiresInspection | `Boolean?` | 是否送检，null 表示资料未知 |
| inspectionCycle | `String?` | 送检周期显示文字，未知或免检为 null |
| nextInspectionDate | `String?` | 下一送检日期 yyyy-MM-dd |

### temp.TempTool

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| rfid | `String` | 规范化 RFID |
| itemCode | `String` | 物资编码 |
| name | `String` | 器具名称 |
| unit | `String` | 计量单位 |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |

### temp.TempBorrowLine

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| lineNo | `Int` | 本单内行号 |
| tool | `TempTool` | 单件器具基本信息 |
| borrowed | `Boolean` | 本行是否已确认实际借出 |
| borrowedAt | `String` | 本行实际借出 UTC 时间 |
| picked | `Boolean` | 本行关联其他拣选是否完成 |

### temp.TempBorrowOrder

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| workOrderNo | `String` | 关联工单号；临时业务可为空 |
| borrower | `String` | 借用人显示名称 |
| workshop | `String` | 车间显示文字 |
| team | `String` | 班组显示文字 |
| remark | `String` | 备注，可为空字符串 |
| createdBy | `String` | 创建人显示名称 |
| createdAt | `String` | 创建 UTC 时间 |
| lines | `List<TempBorrowLine>` | 本单完整明细 |

### temp.TempReturnLine

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| lineNo | `Int` | 本单内行号 |
| tool | `TempTool` | 单件器具基本信息 |
| borrowOrderNo | `String` | 关联借用单号 |
| borrower | `String` | 借用人显示名称 |
| borrowedAt | `String` | 本行实际借出 UTC 时间 |
| returned | `Boolean` | 本行是否已经完成归还核销 |
| returnedAt | `String` | 本行实际归还 UTC 时间 |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |

### temp.TempReturnOrder

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| workOrderNo | `String` | 关联工单号；临时业务可为空 |
| returner | `String` | 归还人显示名称 |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |
| remark | `String` | 备注，可为空字符串 |
| createdBy | `String` | 创建人显示名称 |
| createdAt | `String` | 创建 UTC 时间 |
| lines | `List<TempReturnLine>` | 本单完整明细 |
| returnerEmployeeNo | `String` | 归还人工号 |

### temp.TempBorrowRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| requestId | `String` | 创建临时借出单的稳定 UUID 请求标识 |
| borrowerEmployeeNo | `String` | 借用人工号，服务端解析人员标识 |
| workOrderNo | `String` | 关联工单号；临时业务可为空 |
| workshop | `String` | 车间显示文字 |
| team | `String` | 班组显示文字 |
| remark | `String` | 备注，可为空字符串 |
| rfids | `List<String>` | 本批 RFID 数组，不可为空或重复 |

### temp.TempBorrowConfirmRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| lineNo | `Int` | 本单内行号 |
| rfid | `String` | 规范化 RFID |

### temp.TempReturnConfirmRequest

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| orderNo | `String` | 本业务单据号 |
| lineNo | `Int` | 本单内行号 |
| rfid | `String` | 规范化 RFID |
| borrowOrderNo | `String` | 关联借用单号 |
| returnerEmployeeNo | `String` | 归还人工号 |
| workOrderNo | `String` | 关联工单号；临时业务可为空 |
| location | `String` | 按业务阶段定义的推荐或实际货位编码 |
| remark | `String` | 备注，可为空字符串 |

## 本次客户端验证（2026-11-17）

已通过 `:app:compileDebugKotlin :app:compileDebugJavaWithJavac :app:lintDebug`，包含 KSP、Room schema 生成和 Hilt Java 编译。Lint 为 0 错误、19 条警告，涉及依赖版本、未使用资源和图标位置；未批量处理无关资源。未运行 assemble/bundle APK 任务，未打包或安装。

静态核对了 28 个 Retrofit API 声明与本文路径、领域层依赖边界、顶部 import 写法，以及拣选/归还模型的对应结构。按项目要求未新增测试类、未运行单元或设备测试。真实服务端尚未提供，认证、业务事务及 RFID 新接入页面仍需后端和实机联调；编译通过不代表这些运行链路已经验证。

## 演示数据存放约定（2026-11-17）

用户明确要求：PDA 不保留任何模拟业务数据；开发和演示记录统一存入服务端数据库，通过正常接口返回。建库脚本、演示数据种子与交接说明见 [服务端数据库脚本](server/README.md)。脚本未在实际 MySQL 执行，服务端 agent 需要完成导入和运行验证；客户端不提供本地演示仓库或失败时的假数据兜底。
