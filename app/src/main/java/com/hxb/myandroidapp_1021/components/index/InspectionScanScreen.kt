package com.hxb.myandroidapp_1021.components.index

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hxb.myandroidapp_1021.components.index.components.ApplicationScanScreen

@Composable
fun InspectionScanScreen(onBackClick: () -> Unit = {}, onCompleteClick: () -> Unit = {}) {
    // shortcut: 当前展示示例工具，接入工具查询接口时替换。
    ApplicationScanScreen(
        applicationType = "送检",
        initialTools = listOf(
            ToolScanItem("GJ-01031", "绝缘靴", "GJ-01031", "E200 1031 A003"),
            ToolScanItem("GJ-01118", "绝缘操作杆", "GJ-01118", "E200 1118 B001"),
            ToolScanItem("GJ-01162", "绝缘绳", "GJ-01162", "E200 1162 B003")
        ),
        onBackClick = onBackClick
    ) { tools, onBack ->
        InspectionConfirmScreen(tools = tools, onBackClick = onBack, onConfirmClick = onCompleteClick)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun InspectionScanScreenPreview() {
    InspectionScanScreen()
}
