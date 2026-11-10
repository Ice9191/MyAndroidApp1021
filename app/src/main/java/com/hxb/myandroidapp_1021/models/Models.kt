package com.hxb.myandroidapp_1021.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

data class DisplayField(
    val label: String,
    val value: String
)

data class InfoRow(
    val left: DisplayField,
    val right: DisplayField,
    val leftBold: Boolean = false,
    val rightColor: Color? = null
)

/**
 * 送检申请单（列表展示 + 审批状态）
 */
data class InspectionRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val applyDate: String,
    val status: String   // 已审批 / 未审批
)

/**
 * 盘点任务单（列表展示 + 盘点状态）
 */
data class InventoryRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val inventoryDate: String,
    val status: String   // 已盘点 / 未盘点
)

/**
 * 盘点结果里的一件工器具：工具信息 + 盘点结果
 */
data class InventoryResultItem(
    val tool: ToolScanItem,
    val result: String
)

data class MenuItemData(
    val title: String,
    val icon: ImageVector,
    val route: Any
)

data class MenuSectionData(
    val title: String,
    val items: List<MenuItemData>
)

/**
 * 列表页一张单据卡片要展示什么、按钮点了干什么、怎么匹配搜索词。
 *
 * @param orderNo 取单号
 * @param fields 取卡片里的「标签 值」明细行
 * @param statusText 单号右侧的状态文字，返回 null 则不显示
 * @param statusColor 状态文字颜色
 * @param actionText 单号右侧按钮文字，null 则不显示按钮
 * @param onActionClick 按钮点击
 * @param searchMatcher 搜索匹配规则，null 表示不按搜索词过滤
 */
data class OrderCardSpec<T>(
    val orderNo: (T) -> String,
    val fields: (T) -> List<DisplayField>,
    val statusText: (T) -> String? = { null },
    val statusColor: (T) -> Color = { Color(0xFF1E71C8) },
    val actionText: String? = null,
    val onActionClick: (T) -> Unit = {},
    val searchMatcher: ((T, String) -> Boolean)? = null
)

/**
 * 列表区的文案：搜索框提示、列表头左侧文字、空数据 / 无匹配提示。
 *
 * @param searchPlaceholder 搜索框提示文字
 * @param headerLabel 列表头左侧文字
 * @param emptyText 列表为空时的提示
 * @param noResultText 有搜索词但没匹配到时的提示
 */
data class OrderListTexts(
    val searchPlaceholder: String,
    val headerLabel: String? = null,
    val emptyText: String = "暂无数据",
    val noResultText: String = "暂无匹配数据"
)

data class PickingOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val expectedTime: String,
    val status: String
)

/**
 * 送修申请单
 */
data class RepairRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val applyDate: String,
    val status: String   // 已审批 / 未审批
)

/**
 * 归还单
 */
data class ReturnOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val totalCount: Int,
    val returnedCount: Int,
    val unreturnedCount: Int,
    val status: ReturnStatus
)

/**
 * 归还单状态
 */
@Serializable
enum class ReturnStatus(val label: String) {
    PARTIAL("部分归还"),
    UNRETURNED("未归还")
}

data class ScanColors(
    val themeBlue: Color,
    val textGray: Color,
    val bgColor: Color
)

/**
 * 扫码页的三个 Tab
 */
enum class ScanTab {
    All,      // 全部
    Read,     // 已读取
    Unread    // 未读取
}

/**
 * 扫码页三个 Tab 的文案，默认值就是拣选业务的措辞；
 * 归还 / 送检 / 送修 / 报废 / 报废出库 / 盘点 用同名参数覆盖即可。
 *
 * @param date 「全部」Tab 卡片里日期那一行的标签
 * @param readStatus 已读取时的状态文字
 * @param unreadStatus 未读取时的状态文字
 * @param readAction 已读取 Tab 右上角按钮文字
 * @param unreadAction 未读取 Tab 右上角按钮文字
 */
data class ScanTabsLabels(
    val date: String = "拣选日期",
    val readStatus: String = "已拣选",
    val unreadStatus: String = "未拣选",
    val readAction: String = "取消读取",
    val unreadAction: String = "手动读取"
)

/**
 * 报废单
 */
data class ScrapOrder(
    val orderNo: String,
    val applicant: String,
    val warehouse: String,
    val toolCount: Int,
    val isApproved: Boolean = true,
    val outboundStatus: String = "待出库"
)

/**
 * 报废申请单
 */
data class ScrapRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val applyDate: String,
    val status: String   // 已审批 / 未审批
)

data class ToolCheckRow(
    val id: String,
    val rfid: String,
    val subtitle: String = "",
    val quantity: Int = 1,
    val checked: Boolean = false
)

/**
 * 只读工器具表格里的一行。
 *
 * @param rfid 第一行：RFID
 * @param name 第二行：名称（表格 showName = false 时不显示）
 * @param quantity 右列显示的数量，value 为空时才显示
 * @param value 右列自定义文字（如盘点的「已盘 / 盘亏」），不为空时优先显示它
 * @param valueColor 右列文字颜色，null 用默认深色
 */
data class ToolInfoRow(
    val rfid: String,
    val name: String = "",
    val quantity: Int = 1,
    val value: String? = null,
    val valueColor: Color? = null
)

/**
 * 扫码页里的一件工器具（拣选 / 归还 / 送检 / 送修 / 报废 / 报废出库 / 盘点共用）
 *
 * @param pickDate 读取日期，不同业务写入的都是这一列（拣选日期 / 归还日期 / 送检日期…）
 * @param isChecked 「已读取 / 未读取」表格里的勾选状态
 */
data class ToolScanItem(
    val id: String,
    val name: String = "",
    val code: String = "",
    val rfid: String,
    var isChecked: Boolean = false,
    val pickDate: String = ""
)