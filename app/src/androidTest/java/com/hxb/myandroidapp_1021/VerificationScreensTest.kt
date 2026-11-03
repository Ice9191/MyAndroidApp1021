package com.hxb.myandroidapp_1021

import androidx.compose.ui.test.assertCountEquals
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
import com.hxb.myandroidapp_1021.components.index.InventoryDifference
import com.hxb.myandroidapp_1021.components.index.InventoryResultScreen
import com.hxb.myandroidapp_1021.components.index.InventoryScanScreen
import com.hxb.myandroidapp_1021.components.index.ScrapCheckScreen
import com.hxb.myandroidapp_1021.components.index.ToolScanItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VerificationScreensTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun scrapRequiresConfirmationBeforeEnablingOutbound() {
        var confirmed = false
        compose.setContent { ScrapCheckScreen(onConfirmClick = { confirmed = true }) }
        compose.onNodeWithText("确认出库").assertIsNotEnabled()
        compose.onNodeWithText("未读取 1").performScrollTo().performClick()
        compose.onNodeWithText("确认").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("绝缘绳").performScrollTo()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("确认出库").assertIsNotEnabled()
        compose.onNodeWithText("确认").performScrollTo().performClick()
        compose.onNodeWithText("确认出库").assertIsEnabled()
        compose.onNodeWithText("已读取 2").performScrollTo().performClick()
        compose.onNodeWithText("人工确认").performScrollTo().assertExists()
        compose.onNodeWithText("确认出库").performClick()
        compose.onNode(hasText("确认") and hasAnyAncestor(isDialog())).performClick()
        compose.runOnIdle { assertTrue(confirmed) }
    }

    @Test
    fun inventoryPendingToolsCannotBeManuallyCheckedAndResultMatchesCounts() {
        compose.setContent { InventoryScanScreen() }
        compose.onNodeWithText("待盘 6").performScrollTo().performClick()
        compose.onNodeWithText("绝缘扳手").performScrollTo().assertExists()
        compose.onAllNodes(isToggleable()).assertCountEquals(0)
        compose.onNodeWithText("提交后台比对").performClick()
        compose.onNodeWithText("账面24件    最终实盘18件").assertExists()
        compose.onNodeWithText("盘亏6件").assertExists()
        compose.onNodeWithText("盘盈0件").assertExists()
        compose.onNodeWithContentDescription("返回").performClick()
        compose.onNodeWithText("待盘 6").performScrollTo().assertExists()
    }

    @Test
    fun resultSeparatesShortageAndSurplusAndReturnsToList() {
        var returned = false
        compose.setContent {
            InventoryResultScreen(
                bookCount = 3, actualCount = 3,
                differences = listOf(
                    InventoryDifference(ToolScanItem("1", "绝缘扳手", "GJ-1", "E2001"), 2, 1),
                    InventoryDifference(ToolScanItem("2", "安全帽", "GJ-2", "E2002"), 1, 2)
                ),
                onReturnToListClick = { returned = true }
            )
        }
        compose.onNodeWithText("盘亏1件").assertExists()
        compose.onNodeWithText("盘盈1件").assertExists()
        compose.onNodeWithText("账面 1    /    实盘 2    /    差异 +1").performScrollTo().assertExists()
        compose.onNodeWithText("返回任务列表").performClick()
        compose.runOnIdle { assertTrue(returned) }
    }

    @Test
    fun resultWithNoDifferencesShowsEmptyState() {
        compose.setContent { InventoryResultScreen(bookCount = 24, actualCount = 24, differences = emptyList()) }
        compose.onNodeWithText("盘亏0件").assertExists()
        compose.onNodeWithText("盘盈0件").assertExists()
        compose.onNodeWithText("未发现盘盈盘亏").assertExists()
    }
}
