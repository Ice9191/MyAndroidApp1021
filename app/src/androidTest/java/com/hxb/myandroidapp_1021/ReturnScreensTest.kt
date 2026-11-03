package com.hxb.myandroidapp_1021

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.hxb.myandroidapp_1021.components.index.ReturnConfirmScreen
import com.hxb.myandroidapp_1021.components.index.ReturnScanScreen
import com.hxb.myandroidapp_1021.components.index.ToolScanItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReturnScreensTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun confirmingLastToolEnablesNextAndPreservesStateOnBack() {
        compose.setContent { ReturnScanScreen() }
        compose.onNodeWithText("下一步").assertIsNotEnabled()
        compose.onNodeWithText("RFID读取（占位）").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("未还 1").performScrollTo().performClick()
        compose.onNodeWithText("E200 1162 B003").performScrollTo()
        compose.onAllNodes(isToggleable())[0].performClick()
        compose.onNodeWithText("人工确认").performScrollTo().performClick()
        compose.onNodeWithText("确认", useUnmergedTree = true).performClick()
        compose.onNodeWithText("下一步").assertIsEnabled().performClick()
        compose.onNodeWithText("完成").assertIsNotEnabled()
        compose.onNodeWithText("归还·绑定库位").assertExists()
        compose.onNodeWithText("本次3件已核对").assertExists()
        compose.onNodeWithContentDescription("返回").performClick()
        compose.onNodeWithText("下一步").assertIsEnabled()
    }

    @Test
    fun cancellingConfirmedToolDisablesNext() {
        compose.setContent { ReturnScanScreen() }
        compose.onNodeWithText("E200 1118 B001").performScrollTo()
        compose.onAllNodes(isToggleable())[0].performClick()
        compose.onNodeWithText("取消确认").performScrollTo().performClick()
        compose.onNodeWithText("确认").performClick()
        compose.onNodeWithText("已还 1").assertExists()
        compose.onNodeWithText("未还 2").assertExists()
        compose.onNodeWithText("下一步").assertIsNotEnabled()
    }

    @Test
    fun locationMustBeBoundAgainAfterEditing() {
        var completed = false
        compose.setContent {
            ReturnConfirmScreen(
                tools = listOf(ToolScanItem("1", rfid = "E200 1118 B001")),
                onConfirmClick = { completed = true }
            )
        }
        compose.onNodeWithText("完成").assertIsNotEnabled()
        compose.onNodeWithText("输入库位，如：中心工具库 / A区-03柜").performScrollTo().performTextInput("A区-03柜")
        compose.onNodeWithText("绑定库位").performScrollTo().performClick()
        compose.onNodeWithText("完成").assertIsEnabled()
        compose.onNodeWithText("A区-03柜").performScrollTo().performTextInput("-02层")
        compose.onNodeWithText("完成").assertIsNotEnabled()
        compose.onNodeWithText("绑定库位").performScrollTo().performClick()
        compose.onNodeWithText("完成").performClick()
        compose.onNodeWithText("确认").performClick()
        compose.runOnIdle { assertTrue(completed) }
    }
}
