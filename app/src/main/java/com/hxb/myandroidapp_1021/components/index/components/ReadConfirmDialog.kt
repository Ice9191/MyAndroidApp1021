package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.hxb.myandroidapp_1021.components.index.models.ScanTab
import com.hxb.myandroidapp_1021.components.index.models.ToolScanItem


/**
 * 扫码页「手动读取 / 取消读取」确认弹窗（弹窗 + 搬运逻辑一起封在这里）。
 * 归还 / 送检 / 送修 / 报废 / 报废出库 / 盘点 / 拣选都共用这一份逻辑。
 *
 * 逻辑说明：
 *  - [ScanTab.Unread]（未读取 Tab）：「手动读取」——把未读取里勾选的件搬到已读取，并把日期字段写成 [today]
 *  - [ScanTab.Read]（已读取 Tab）：「取消读取」——把已读取里勾选的件搬回未读取，并清空日期字段
 *  - 搬运时把 isChecked 复位为 false；「确认」执行完动作后调 [onDismiss] 关弹窗，「取消」直接关
 *
 * 注意：readList / unreadList 必须是页面持有的 SnapshotStateList，
 * 这里用 addAll / removeAll 原地改，才会触发页面重组。
 *
 * 用法：
 * ```
 * ReadConfirmDialog(
 *     visible = showConfirmDialog,
 *     tab = selectedTab,
 *     readList = readList,
 *     unreadList = unreadList,
 *     today = today,
 *     onDismiss = { showConfirmDialog = false }
 * )
 * ```
 *
 * @param visible 是否显示弹窗
 * @param tab 当前选中的 Tab（[ScanTab.Read] 取消读取 / [ScanTab.Unread] 手动读取）
 * @param readList 已读取列表
 * @param unreadList 未读取列表
 * @param today 读取日期（拣选 / 归还 / 送检 / 盘点日期等，统一存在 ToolScanItem.pickDate 里）
 * @param onDismiss 关闭弹窗（确认执行完动作后也会调它）
 */
@Composable
fun ReadConfirmDialog(
    visible: Boolean,
    tab: ScanTab,
    readList: SnapshotStateList<ToolScanItem>,
    unreadList: SnapshotStateList<ToolScanItem>,
    today: String,
    onDismiss: () -> Unit
) {
    if (!visible) return
    val themeBlue = Color(0xFF1E71C8)
    val textGray = Color(0xFF888888)
    val isManualRead = tab == ScanTab.Unread // 未读取 -> 手动读取
    val actionLabel = if (isManualRead) "手动读取" else "取消读取"
    val targetCount = if (isManualRead) {
        unreadList.count { it.isChecked }
    } else {
        readList.count { it.isChecked }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认操作") },
        text = { Text("是否${actionLabel}选中的 $targetCount 件工具？") },
        containerColor = Color.White,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isManualRead) {
                        // 未读取 -> 已读取，记录拣选日期
                        val selectedItems = unreadList.filter { it.isChecked }
                        if (selectedItems.isNotEmpty()) {
                            readList.addAll(
                                selectedItems.map {
                                    it.copy(isChecked = false, pickDate = today)
                                }
                            )
                            unreadList.removeAll(selectedItems)
                        }
                    } else {
                        // 已读取 -> 未读取，清掉拣选日期
                        val selectedItems = readList.filter { it.isChecked }
                        if (selectedItems.isNotEmpty()) {
                            unreadList.addAll(
                                selectedItems.map { it.copy(isChecked = false, pickDate = "") }
                            )
                            readList.removeAll(selectedItems)
                        }
                    }
                    onDismiss()
                }
            ) {
                Text("确认", color = themeBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = textGray)
            }
        }
    )
}
