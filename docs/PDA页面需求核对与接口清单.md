# PDA 接口清单

以下按首页的分组和功能入口分类，各功能下的接口从 1 开始编号，公共接口单独列出。接口和字段依据当前 PDA 页面及《工器具库系统文档v2》整理。查询接口列出的明细可以与列表一起返回，也可以单独提供详情接口。

**TODO：字段未确定：所有接口的实际地址、HTTP 方法、正式 JSON 字段名、后端字段类型、必填性和可空规则不确定。** 下表英文名称和类型仅对照当前模拟模型；无法从现有模型确定字段名的行已移除，相应章节标注“字段未确定，待后端确认”。`[]`表示数组项，“表头/明细”表示数据层级，不是已确定的 JSON 外层字段名。

通用响应中的成功标识、错误码、错误信息字段名及类型不确定；列表接口的分页参数、总条数、下一页标识字段名及类型不确定。

## 入库管理

### 工器具上架

#### 1 查询工器具上架单及明细

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 响应 | 表头[].inboundOrderNo | 入库单号 | String |
| 响应 | 表头[].orderNo | 上架单号 | String |
| 响应 | 表头[].status | 上架单状态 | String；状态编码不确定 |
| 响应 | 表头[].operator | 上架人 | String |
| 响应 | 表头[].putawayTime | 上架时间 | String；时间格式不确定 |
| 响应 | 明细[].orderNo | 所属上架单号 | String |
| 响应 | 明细[].inboundLineNo | 来源入库行号 | String；正式标识与类型不确定 |
| 响应 | 明细[].lineNo | 上架行号 | String；正式标识与类型不确定 |
| 响应 | 明细[].status | 明细上架状态 | String；状态编码不确定 |
| 响应 | 明细[].batchNo | 批次号 | String |
| 响应 | 明细[].rfid | RFID 编码 | String |
| 响应 | 明细[].itemCode | 物资编码 | String |
| 响应 | 明细[].description | 物资描述 | String |
| 响应 | 明细[].unit | 单位 | String |
| 响应 | 明细[].warehouse | 仓库 | String |
| 响应 | 明细[].location | 实际货位 | String |
| 响应 | 明细[].inventoryStatus | 库存状态 | String；状态编码不确定 |

#### 2 确认单件工器具上架

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 上架单号 | String |
| 请求 | lineNo | 上架明细行号 | String |
| 请求 | rfid | 本件 RFID | String |
| 请求 | location | 本件实际货位 | String |
| 请求 | operator | 操作人 | String；是否从登录凭证获取不确定 |
| 响应 | orderNo | 上架单号 | String |
| 响应 | lineNo | 本次完成的行号 | String |
| 响应 | rfid | 本次完成的 RFID | String |
| 响应 | status | 本件上架状态 | String；状态编码不确定 |
| 响应 | location | 保存后的实际货位 | String |
| 响应 | inventoryStatus | 更新后的库存状态 | String；状态编码不确定 |
| 响应 | operator | 实际记录的上架人 | String |
| 响应 | putawayTime | 服务端记录的上架时间 | String；时间格式不确定 |

## 工单作业

### 工单拣选

#### 1 查询工单拣选单

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 响应 | 单据[].orderNo | 拣选单号 | String |
| 响应 | 单据[].status | 拣选单状态 | PickingStatus：未拣选、部分拣选、已拣选、已关闭；接口编码不确定 |
| 响应 | 单据[].workshop | 归属车间 | String |
| 响应 | 单据[].borrower | 借用人 | String |
| 响应 | 单据[].pendingCount | 待拣选数量 | Int |
| 响应 | 单据[].plannedDate | 工单计划日期 | String；时间格式不确定 |
| 响应 | 单据[].totalCount | 总数量 | Int |
| 响应 | 单据[].pickedCount | 已拣选数量 | Int |
| 响应 | 单据[].workOrderNo | 工单号 | String |
| 响应 | 单据[].createdBy | 创建人 | String |
| 响应 | 单据[].createdAt | 创建时间 | String；时间格式不确定 |

#### 2 查询工单拣选明细

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 拣选单号 | String |
| 响应 | 明细[].id | 明细行标识 | Int；正式主键和类型不确定 |
| 响应 | 明细[].name | 工具名称 | String |
| 响应 | 明细[].itemCode | 物资编码 | String |
| 响应 | 明细[].rfid | 指定 RFID | String |
| 响应 | 明细[].location | 货位 | String |
| 响应 | 明细[].unit | 单位 | String |
| 响应 | handler | 当前确认页经办人 | String；是否直接采用当前登录人员不确定 |
| 响应 | outboundTime | 当前确认页出库时间 | String；正式接口的时间含义、格式及是否应在提交成功后返回不确定 |

#### 3 确认工单拣选

文档为 PDA 拣选、一体机交接；当前页面为“确认出库”。该接口是否同时完成交接借出不确定。

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 拣选单号 | String |
| 请求 | employeeId | 当前确认页的领料人工号 | String；PDA只拣选时是否需要不确定 |
| 请求 | rfids[] | 本批次核对的 RFID | List<String> |
| 响应 | orderNo | 拣选单号 | String |
| 响应 | status | 更新后的单据状态 | PickingStatus；接口编码不确定 |
| 响应 | pickedCount | 已拣选数量 | Int |
| 响应 | pendingCount | 待拣选数量 | Int |
| 响应 | totalCount | 总数量 | Int |

#### 4 关闭工单拣选单

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 需要关闭的拣选单号 | String |
| 响应 | orderNo | 拣选单号 | String |
| 响应 | status | 关闭后的单据状态 | PickingStatus；接口编码不确定 |
| 响应 | pendingCount | 关闭时剩余未拣选数量 | Int |

### 工单归还上架

#### 1 查询工单归还上架任务

文档要求归还后生成独立上架单，当前使用 ReturnOrder。正式查询对象及归还单、上架单的关联字段不确定。

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 响应 | 单据[].orderNo | 当前模型中的归还单号 | String |
| 响应 | 单据[].status | 归还单状态 | ReturnStatus：待归还、部分归还、已归还；接口编码不确定 |
| 响应 | 单据[].workshop | 归属车间或工班 | String；当前共用一个字段，正式拆分不确定 |
| 响应 | 单据[].returner | 归还人 | String |
| 响应 | 单据[].pendingCount | 未归还数量 | Int |
| 响应 | 单据[].returnedDate | 归还时间 | String；时间格式不确定 |
| 响应 | 单据[].totalCount | 总数量 | Int |
| 响应 | 单据[].returnedCount | 已归还数量 | Int |
| 响应 | 单据[].workOrderNo | 工单号 | String |
| 响应 | 单据[].createdBy | 创建人 | String |
| 响应 | 单据[].createdAt | 创建时间 | String；时间格式不确定 |

#### 2 查询工单归还上架明细

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 当前选中归还单号 | String；正式接口应使用归还单标识还是上架单标识不确定 |
| 响应 | 明细[].id | 明细行标识 | Int；正式主键和类型不确定 |
| 响应 | 明细[].name | 工具名称 | String |
| 响应 | 明细[].itemCode | 物资编码 | String |
| 响应 | 明细[].rfid | 本件 RFID | String |
| 响应 | 明细[].location | 货位 | String |
| 响应 | 明细[].unit | 单位 | String |

#### 3 确认工单归还上架

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 当前归还单号 | String；正式提交的上架任务标识不确定 |
| 请求 | employeeId | 归还人工号 | String；上架时是否仍需提交归还人不确定 |
| 请求 | locationCode | 当前页面整批绑定的货位码 | String |
| 请求 | rfids[] | 本批次核对的 RFID | List<String> |

## 临时借还

### 临时借出

#### 1 查询临时借出单及明细

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 响应 | 单据[].orderNo | 临时借出单号 | String |
| 响应 | 单据[].workOrderNo | 工单号 | String；无工单场景是否允许为空不确定 |
| 响应 | 单据[].borrower | 借用人 | String；正式使用姓名、工号或人员标识不确定 |
| 响应 | 单据[].workshop | 车间 | String |
| 响应 | 单据[].team | 工班 | String |
| 响应 | 单据[].remark | 备注 | String |
| 响应 | 单据[].createdBy | 创建人 | String |
| 响应 | 单据[].createdAt | 创建时间 | String；时间格式不确定 |
| 响应 | 单据[].status | 单据状态 | 当前为派生String；正式字段及编码不确定 |
| 响应 | 明细[].lineNo | 借出明细行号 | Int；正式标识和类型不确定 |
| 响应 | 明细[].borrowed | 本件是否已借出 | Boolean；正式是否使用状态编码不确定 |
| 响应 | 明细[].borrowedAt | 本件借出时间 | String；时间格式不确定 |
| 响应 | 明细[].tool.rfid | RFID 编码 | String |
| 响应 | 明细[].tool.itemCode | 物资编码 | String |
| 响应 | 明细[].tool.name | 工具名称 | String |
| 响应 | 明细[].tool.unit | 单位 | String |
| 响应 | 明细[].tool.location | 存放货位 | String |

#### 2 新建临时借出单

可选器具使用“库存管理 → 库存明细”中的“1 查询库存明细和可借器具”接口，筛选允许临时借出的在库器具。

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | workOrderNo | 工单号 | String；是否必填不确定 |
| 请求 | borrower | 借用人 | String；身份标识规则不确定 |
| 请求 | workshop | 车间 | String |
| 请求 | team | 工班 | String |
| 请求 | remark | 备注 | String |
| 响应 | orderNo | 后端生成的临时借出单号 | String |
| 响应 | createdBy | 创建人 | String |
| 响应 | createdAt | 创建时间 | String；时间格式不确定 |
| 响应 | status | 初始单据状态 | 正式字段及编码不确定 |

#### 3 确认单件临时借出

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 临时借出单号 | String |
| 请求 | lineNo | 本件明细行号 | Int；正式标识和类型不确定 |
| 请求 | rfid | 本件 RFID | String |
| 响应 | orderNo | 临时借出单号 | String |
| 响应 | lineNo | 本次处理行号 | Int；正式类型不确定 |
| 响应 | borrowed | 本件是否已借出 | Boolean；正式是否使用状态编码不确定 |
| 响应 | borrowedAt | 服务端借出时间 | String；时间格式不确定 |
| 响应 | status | 更新后的整单状态 | 正式字段及编码不确定 |

### 临时归还

#### 1 查询临时归还单及待还明细

是否同时覆盖正常工单借用的手工归还不确定。

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 响应 | 单据[].orderNo | 临时归还单号 | String |
| 响应 | 单据[].workOrderNo | 工单号 | String；无工单场景是否允许为空不确定 |
| 响应 | 单据[].returner | 归还人 | String；正式人员标识规则不确定 |
| 响应 | 单据[].location | 登记的归还库位 | String |
| 响应 | 单据[].remark | 备注 | String |
| 响应 | 单据[].createdBy | 创建人 | String |
| 响应 | 单据[].createdAt | 创建时间 | String；时间格式不确定 |
| 响应 | 单据[].status | 单据状态 | 当前为派生String；正式字段及编码不确定 |
| 响应 | 明细[].lineNo | 归还明细行号 | Int；正式标识和类型不确定 |
| 响应 | 明细[].borrowOrderNo | 原借用单号 | String |
| 响应 | 明细[].borrower | 原借用人 | String |
| 响应 | 明细[].borrowedAt | 原借出时间 | String；时间格式不确定 |
| 响应 | 明细[].returned | 本件是否已归还 | Boolean；正式是否使用状态编码不确定 |
| 响应 | 明细[].returnedAt | 本件归还时间 | String；时间格式不确定 |
| 响应 | 明细[].tool.rfid | RFID 编码 | String |
| 响应 | 明细[].tool.itemCode | 物资编码 | String |
| 响应 | 明细[].tool.name | 工具名称 | String |
| 响应 | 明细[].tool.unit | 单位 | String |
| 响应 | 明细[].tool.location | 存放货位 | String |

#### 2 确认单件临时归还

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | orderNo | 临时归还单号 | String |
| 请求 | lineNo | 本件归还行号 | Int；正式标识和类型不确定 |
| 请求 | rfid | 本件 RFID | String |
| 请求 | borrowOrderNo | 原借用单号 | String |
| 请求 | returner | 归还人 | String；正式身份标识规则不确定 |
| 请求 | workOrderNo | 工单号 | String；是否必填不确定 |
| 请求 | location | 归还库位 | String |
| 请求 | remark | 备注 | String |
| 响应 | orderNo | 临时归还单号 | String |
| 响应 | lineNo | 本次处理行号 | Int；正式类型不确定 |
| 响应 | returned | 本件是否已归还 | Boolean；正式是否使用状态编码不确定 |
| 响应 | returnedAt | 服务端归还时间 | String；时间格式不确定 |
| 响应 | status | 更新后的整单状态 | 正式字段及编码不确定 |

## 其他作业

### 其他拣选

#### 1 查询其他拣选任务及明细

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | sourceNo | 来源单号搜索条件 | String |
| 请求 | sourceType | 来源类型筛选 | 临时、送检、送修、报废、退货；接口编码不确定 |
| 响应 | 任务[].sourceNo | 来源单号 | String |
| 响应 | 任务[].sourceType | 来源类型 | SourceType；正式接口编码不确定 |
| 响应 | 任务[].date | 任务日期 | String；时间格式不确定 |
| 响应 | 明细[].rfid | RFID 编码 | String |
| 响应 | 明细[].name | 工具名称 | String |
| 响应 | 明细[].location | 原货位 | String |

#### 2 确认本批次其他拣选

不同来源的确认是否直接完成出库不确定；临时拣选、送检、送修、报废、退货不能默认使用同一种库存终态。

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | sourceNo | 来源单号 | String |
| 请求 | sourceType | 来源类型 | 接口编码不确定 |

### 其他上架

#### 1 查询其他上架任务及明细

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | sourceNo | 来源单号搜索条件 | String |
| 请求 | sourceType | 来源类型筛选 | 临时归还、送检归还、送修归还、报废归还、退货归还；接口编码不确定 |
| 响应 | 任务[].sourceNo | 来源单号 | String |
| 响应 | 任务[].sourceType | 来源类型 | SourceType；正式接口编码不确定 |
| 响应 | 任务[].date | 任务日期 | String；时间格式不确定 |
| 响应 | 明细[].rfid | RFID 编码 | String |
| 响应 | 明细[].name | 工具名称 | String |
| 响应 | 明细[].location | 原货位 | String |

#### 2 确认本批次其他上架

报废归还、退货归还的库存恢复规则不确定。

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | sourceNo | 来源单号 | String |
| 请求 | sourceType | 来源类型 | 接口编码不确定 |

## 库存管理

### 库存明细

#### 1 查询库存明细和可借器具

列表返回完整字段时可直接显示详情；否则另提供按 RFID 查询单件详情，响应字段同本表。

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | status | 库存状态筛选 | String；当前筛选模型名称，正式字段及编码不确定 |
| 请求 | condition | 好坏件筛选 | String；状态编码不确定 |
| 请求 | warehouse | 仓库筛选 | String |
| 请求 | location | 货位筛选 | String |
| 请求 | startDate | 下次送检开始日期 | String；正式日期格式不确定 |
| 请求 | endDate | 下次送检结束日期 | String；正式日期格式不确定 |
| 响应 | 库存[].rfid | RFID 编码 | String |
| 响应 | 库存[].itemCode | 物资编码 | String |
| 响应 | 库存[].description | 物资描述 | String |
| 响应 | 库存[].specification | 规格型号 | String? |
| 响应 | 库存[].unit | 单位 | String |
| 响应 | 库存[].category | 物资分类 | String? |
| 响应 | 库存[].inventoryStatus | 库存状态 | String；状态编码不确定 |
| 响应 | 库存[].condition | 好坏件状态 | String；状态编码不确定 |
| 响应 | 库存[].faultDescription | 当前故障描述 | String? |
| 响应 | 库存[].warehouse | 仓库 | String |
| 响应 | 库存[].location | 货位 | String? |
| 响应 | 库存[].requiresInspection | 是否送检 | Boolean? |
| 响应 | 库存[].inspectionCycle | 送检周期 | String?；正式类型及周期单位不确定 |
| 响应 | 库存[].nextInspectionDate | 下次送检日期 | String?；时间格式不确定 |

#### 2 变更库存好坏件状态

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | rfid | 目标器具 RFID | String |
| 请求 | condition | 目标好坏件状态 | String；接口编码不确定 |
| 请求 | faultDescription | 故障描述 | String?；是否需要、是否必填不确定 |
| 响应 | rfid | 目标器具 RFID | String |
| 响应 | condition | 保存后的好坏件状态 | String；接口编码不确定 |
| 响应 | faultDescription | 保存后的故障描述 | String?；好件是否清空不确定 |
| 响应 | inventoryStatus | 当前库存状态 | String；接口编码不确定 |

## 告警记录

### 超期告警

#### 1 查询超期告警

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 响应 | 告警[].alertId | 告警标识 | String |
| 响应 | 告警[].alertType | 告警类型 | String |
| 响应 | 告警[].workOrderNo | 工单号 | String |
| 响应 | 告警[].borrowOrderNo | 借用单号 | String |
| 响应 | 告警[].borrower | 借用人 | String |
| 响应 | 告警[].toolName | 器具名称 | String |
| 响应 | 告警[].rfid | RFID 编码 | String |
| 响应 | 告警[].itemCode | 物资编码 | String |
| 响应 | 告警[].borrowedAt | 借出时间 | String；时间格式不确定 |
| 响应 | 告警[].dueAt | 应还时间 | String；时间格式不确定 |
| 响应 | 告警[].overdueDays | 超期天数 | Int?；由后端计算，计算口径不确定 |
| 响应 | 告警[].status | 告警业务状态 | String?；编码不确定 |
| 响应 | 告警[].handlingStatus | 处理状态 | String?；与业务状态的对应关系不确定 |

#### 2 获取超期告警参数

字段未确定，待后端确认。

#### 3 保存超期告警参数

字段未确定，待后端确认。

## 公共接口

### 1 测试服务器连接

字段未确定，待后端确认。

### 2 用户登录

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | username | 用户账号 | String |
| 请求 | password | 密码 | String；传输约定不确定 |
| 响应 | userName | 用户姓名 | String |
| 响应 | userRole | 角色 | String |
| 响应 | token | 认证凭证 | String?；凭证格式、有效期不确定 |

### 3 获取当前用户信息与权限

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 响应 | userName | 用户姓名 | String |
| 响应 | userRole | 角色 | String |
| 响应 | avatarUrl | 头像地址 | String；是否由后端提供不确定 |

### 4 退出登录

是否需要服务端注销接口不确定，取决于认证方式。

字段未确定，待后端确认。

### 5 查询或校验人员

字段未确定，待后端确认。

### 6 查询车间和工班

字段未确定，待后端确认。

### 7 查询及校验货位

| 方向 | 字段 | 含义 | 当前模型类型或不确定项 |
| --- | --- | --- | --- |
| 请求 | code | 扫描或输入的货位码 | String |
| 请求 | warehouse | 目标仓库 | String；提交仓库编码还是名称不确定 |
| 响应 | code | 货位编码 | String |
| 响应 | warehouse | 所属仓库 | String |
