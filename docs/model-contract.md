# 数据模型与模块规范说明

## 字段依据与边界

参考用户提供的《工器具库系统文档v2.docx》：人员、权限管理、拣选单、借用交接、工单归还、归还上架，以及 IF-001、IF-003 接口数据示例。

原文路径：`D:/Cache/xwechat_files/wxid_8hqhdb6yab4x22_974e/msg/file/2026-11/工器具库系统文档v2.docx`。本次本地提取结果：`C:/Users/s1340/AppData/Local/Temp/myandroidapp-system-reference.md`。

文档明确了业务字段的中文含义，但没有给出本 PDA 应用完整的 HTTP 地址、JSON 英文字段、数据库表/列名、状态代码或字段类型约束。当前 Model 中的 Kotlin 名称是接口对接草案，不声称已经与真实数据库一一对应。未确定部分在 Model 中以 `TODO: 字段未确定` 标记。

`@Serializable` 的业务类用于表达接口需要返回或接收的数据；`*UiState` 用于加载、输入、筛选、校验和提交等前端状态，不应把这些 UI 状态当成数据库列。

## 业务数据字段

| 模型 | 需要的数据 | 依据及待确认项 |
| --- | --- | --- |
| `DashboardData` | `userName` 人员名称、`userRole` 角色、`avatarUrl` 头像地址 | 人员/权限章节；头像沿用现有页面，字段未确定，TODO 已标记 |
| `LoginRequest` | `username` 用户账号、`password` 密码 | 现有登录页；登录接口、密码协议字段未确定 |
| `LoginResponse` | `userName`、`userRole`、可空 `token` | 人员/权限章节；认证凭证字段未确定，token 是 TODO 占位 |
| `ServerConfig` | `ipAddress` 地址、`port` 端口 | 现有配置页；配置接口及端口的后端类型字段未确定，暂保留字符串输入 |
| `PickingOrder` / `ReturnOrder` | 单号、状态、车间/班组、借用人/归还人、未完成数量、计划日期/归还时间、总数量、已完成数量、工单号、创建人、创建时间 | 两者均为 11 个构造字段；依据工单、借还及现有页面。数量是后端统计结果，不要求一定是物理数据库列；JSON 命名、创建信息和时间格式需确认 |
| `PickingToolScanItem` / `ReturnToolScanItem` | `id` 行号/标识、`name` 名称、`itemCode` 物资编码、`rfid` 标签码、`location` 货位、`unit` 单位、`isRead` 本次读取标记、`readDate` 本次读取时间 | 两者均为 8 个构造字段；文档明确物资、RFID、货位和单位。行主键类型与读签暂存/提交字段未确定。读取不等于最终借出或归还成功 |
| `PickingConfirmData` | `handler` 经办人、`outboundTime` 出库时间 | 借用交接章节；具体接口命名沿用 TODO 约束 |
| `PickingConfirmRequest` | `orderNo`、`employeeId`、`rfids` | 出库确认请求；人员主键和行标识关联需后端确认 |
| `ReturnConfirmRequest` | `orderNo`、`employeeId`、`locationCode`、`rfids` | 归还上架请求；货位绑定校验接口及字段未确定 |

## Picking / Returns 一致性约束

- List：相同的 Screen/Content 分层；相同 `InputFieldOne`、`OrderList`、`ScrollToTopButton`；相同查询/状态事件与组合筛选函数。
- Scan：相同的三字段扫描状态、单据加载流程、`selectOrder`、`toggleRead`；相同 `ScanTopInfo`、`ScanControlButtons`、`ScanTabs`、`ToolsScanList` 与嵌套滚动实现。
- Manual：相同的勾选生命周期、Tab、工具卡片、确认弹窗和读取/取消读取操作。
- ViewModel：私有 `MutableStateFlow`、公开只读 `StateFlow`，`init -> loadData -> viewModelScope.launch -> delay -> mock* -> update` 的加载写法一致。模拟单据和工具直接列出，不按件数生成数据。
- Confirm：保留业务差异，均使用共享扫描状态；拣选需要领料人工号，归还需要归还人工号及重新绑定后的库位。
- 业务状态取值遵循文档：拣选支持关闭，归还文档没有定义关闭，不能为了形式统一而虚构新的业务状态。

目前工具明细仍为固定模拟数据，不是按所选单据查询的真实库存。真实接口接入时，后端应按单号返回匹配的完整工具明细与数量。

## 状态和导航

所有 Routes 都是无参数地址。页面间只共享 ViewModel，不传递业务参数。列表选择先更新共享扫描 ViewModel，然后只发送无参数导航事件。Screen 内部把状态与回调传给 Content、组件，属于正常 UDF。

`ui/common/Model.kt` 只放至少两个业务模块使用的展示模型或兼容的共用工具模型。仅本模块使用的请求、响应、业务数据和 UiState 都保存在模块 `Model.kt`。菜单图标和路由属于 Dashboard 本地展示配置，不是后端字段。

## 验证约定

按用户要求，不新增测试类、不运行自动测试。使用主应用编译/打包和静态一致性检查验证本次修改；既有测试中的旧模型和页面接口尚未同步，交由用户维护。
