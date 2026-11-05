package com.hxb.myandroidapp_1021.components.index.utils

import com.hxb.myandroidapp_1021.components.index.models.ScanTab
import com.hxb.myandroidapp_1021.components.index.models.ToolScanItem

/**
 * 扫码页「已读取 / 未读取」表格里勾选 / 取消勾选一行的公共写回逻辑。
 *
 * 页面把回调传给 [com.hxb.myandroidapp_1021.components.index.components.TabsContent] 的 `onToggle`：
 * ```
 * onToggle = { id, checked ->
 *     toggleChecked(selectedTab, id, checked, readList, unreadList)
 * }
 * ```
 *
 * 注意：readList / unreadList 必须是页面持有的 SnapshotStateList（作为 MutableList 传进来），
 * 这里只做**原位赋值**，才会触发重组；用 copy() 重建列表的话页面状态就断链了。
 *
 * @param tab 当前 Tab，决定改哪一份列表（[com.hxb.myandroidapp_1021.components.index.models.ScanTab.Read] 改 readList，否则改 unreadList）
 * @param id 被操作那一行的 id
 * @param checked 勾上还是取消
 */
fun toggleChecked(
    tab: ScanTab,
    id: String,
    checked: Boolean,
    readList: MutableList<ToolScanItem>,
    unreadList: MutableList<ToolScanItem>
) {
    val sourceList = if (tab == ScanTab.Read) readList else unreadList
    val index = sourceList.indexOfFirst { it.id == id }
    if (index != -1) {
        sourceList[index] = sourceList[index].copy(isChecked = checked)
    }
}
