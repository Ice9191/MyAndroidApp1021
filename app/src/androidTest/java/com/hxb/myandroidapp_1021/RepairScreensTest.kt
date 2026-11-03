package com.hxb.myandroidapp_1021

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.hxb.myandroidapp_1021.components.index.RepairConfirmScreen
import com.hxb.myandroidapp_1021.components.index.RepairScanScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RepairScreensTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun searchAndConfirmToolThenReturnAndRemoveIt() {
        compose.setContent { RepairScanScreen() }
        compose.onNodeWithText("下一步").assertIsNotEnabled()
        compose.onNodeWithText("手动选择").performScrollTo().performClick()
        compose.onNodeWithText("搜索工具名称、编号或RFID").performScrollTo().performTextInput("GJ-02008")
        compose.onNodeWithText("数字万用表").performScrollTo().assertExists()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("下一步").assertIsNotEnabled()
        compose.onNodeWithText("确认").performScrollTo().performClick()
        compose.onNodeWithText("下一步").assertIsEnabled().performClick()
        compose.onNodeWithText("送修·填写信息").assertExists()
        compose.onNodeWithText("已选工具：1件").assertExists()
        compose.onNodeWithContentDescription("返回").performClick()
        compose.onNodeWithText("已读取 1").performScrollTo().performClick()
        compose.onNodeWithText("数字万用表").performScrollTo()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("取消读取").performScrollTo().performClick()
        compose.onNodeWithText("已读取 0").assertExists()
        compose.onNodeWithText("下一步").assertIsNotEnabled()
    }

    @Test
    fun confirmRequiresAllFieldsAndDate() {
        var completed = false
        compose.setContent { RepairConfirmScreen(onConfirmClick = { completed = true }) }
        compose.onNodeWithText("确认").assertIsNotEnabled()
        compose.onNodeWithText("请输入送修原因").performScrollTo().performTextInput("绝缘层破损")
        compose.onNodeWithText("请输入故障描述").performScrollTo().performTextInput("电压档读数不稳定，更换电池后仍异常")
        compose.onNodeWithText("确认").assertIsNotEnabled()
        compose.onNodeWithContentDescription("选择预计返回日期").performScrollTo().performClick()
        compose.onNodeWithText("确定").performClick()
        compose.onNodeWithText("确认").assertIsEnabled().performClick()
        compose.onNodeWithText("确认送修申请").assertExists()
        compose.onNode(hasText("确认") and hasAnyAncestor(isDialog())).performClick()
        compose.runOnIdle { assertTrue(completed) }
    }
}
