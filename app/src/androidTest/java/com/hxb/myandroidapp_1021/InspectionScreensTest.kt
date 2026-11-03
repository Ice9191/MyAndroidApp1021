package com.hxb.myandroidapp_1021

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.hxb.myandroidapp_1021.components.index.InspectionConfirmScreen
import com.hxb.myandroidapp_1021.components.index.InspectionScanScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class InspectionScreensTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun searchAndConfirmToolThenReturnAndRemoveIt() {
        compose.setContent { InspectionScanScreen() }
        compose.onNodeWithText("下一步").assertIsNotEnabled()
        compose.onNodeWithText("手动选择").performScrollTo().performClick()
        compose.onNodeWithText("搜索工具名称、编号或RFID").performScrollTo()
            .performTextInput("GJ-01031")
        compose.onNodeWithText("绝缘靴").performScrollTo().assertExists()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("下一步").assertIsNotEnabled()
        compose.onNodeWithText("确认").performScrollTo().performClick()
        compose.onNodeWithText("下一步").assertIsEnabled().performClick()
        compose.onNodeWithText("送检·填写信息").assertExists()
        compose.onNodeWithText("已选工具：1件").assertExists()
        compose.onNodeWithContentDescription("返回").performClick()
        compose.onNodeWithText("已读取 1").performScrollTo().performClick()
        compose.onNodeWithText("绝缘靴").performScrollTo()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("取消读取").performScrollTo().performClick()
        compose.onNodeWithText("已读取 0").assertExists()
        compose.onNodeWithText("下一步").assertIsNotEnabled()
    }

    @Test
    fun confirmRequiresAllFieldsAndDate() {
        var completed = false
        compose.setContent { InspectionConfirmScreen(onConfirmClick = { completed = true }) }
        compose.onNodeWithText("确认").assertIsNotEnabled()
        compose.onNodeWithText("请输入送检原因").performScrollTo().performTextInput("绝缘层破损")
        compose.onNodeWithText("请输入送检机构").performScrollTo()
            .performTextInput("市电力维修中心")
        compose.onNodeWithText("确认").assertIsNotEnabled()
        compose.onNodeWithContentDescription("选择预计返回日期").performScrollTo().performClick()
        compose.onNodeWithText("确定").performClick()
        compose.onNodeWithText("确认").assertIsEnabled().performClick()
        compose.onNodeWithText("确认送检申请").assertExists()
        compose.onNode(hasText("确认") and hasAnyAncestor(isDialog())).performClick()
        compose.runOnIdle { assertTrue(completed) }
    }
}
