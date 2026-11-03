package com.hxb.myandroidapp_1021.components.index

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hxb.myandroidapp_1021.components.index.components.ApplicationScanScreen

@Composable
fun RepairScanScreen(onBackClick: () -> Unit = {}, onCompleteClick: () -> Unit = {}) {
    // shortcut: 当前展示示例工具，接入工具查询接口时替换。
    ApplicationScanScreen(
        applicationType = "送修",
        initialTools = listOf(
            ToolScanItem("GJ-02008", "数字万用表", "GJ-02008", "E200 2008 D001"),
            ToolScanItem("GJ-02009", "钳形电流表", "GJ-02009", "E200 2009 D002"),
            ToolScanItem("GJ-02010", "绝缘电阻测试仪", "GJ-02010", "E200 2010 D003")
        ),
        onBackClick = onBackClick
    ) { tools, onBack ->
        RepairConfirmScreen(tools = tools, onBackClick = onBack, onConfirmClick = onCompleteClick)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun RepairScanScreenPreview() {
    RepairScanScreen()
}
