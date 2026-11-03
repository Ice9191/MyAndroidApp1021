package com.hxb.myandroidapp_1021

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.hxb.myandroidapp_1021.components.index.ScrapListScreen
import com.hxb.myandroidapp_1021.components.index.ScrapOrder
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScrapListScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun showsOnlyApprovedOrdersAndPassesSelectedOrderToCallback() {
        val approved = ScrapOrder("BF20261027003", "李明", "中心工具库", 2)
        val unapproved = ScrapOrder("BF20261027004", "张伟", "中心工具库", 1, isApproved = false)
        var selected: ScrapOrder? = null
        compose.setContent {
            ScrapListScreen(orders = listOf(approved, unapproved), onCheckClick = { selected = it })
        }
        compose.onNodeWithText("共1张").assertExists()
        compose.onNodeWithText(approved.orderNo).assertExists()
        compose.onNodeWithText(unapproved.orderNo).assertDoesNotExist()
        compose.onNodeWithText("出库核对").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(approved, selected) }
    }

    @Test
    fun searchMatchesOrderApplicantAndWarehouseAndShowsEmptyState() {
        compose.setContent {
            ScrapListScreen(orders = listOf(
                ScrapOrder("BF001", "李明", "中心工具库", 2),
                ScrapOrder("BF002", "王健", "电气工具库", 3)
            ))
        }
        compose.onNodeWithText("搜索报废单号、申请人或库房").performTextInput("bf001")
        compose.onNodeWithText("共1张").assertExists()
        compose.onNodeWithText("BF001").assertExists()
        compose.onNode(hasSetTextAction()).performTextReplacement("王健")
        compose.onNodeWithText("BF002").assertExists()
        compose.onNode(hasSetTextAction()).performTextReplacement("电气")
        compose.onNodeWithText("共1张").assertExists()
        compose.onNode(hasSetTextAction()).performTextReplacement("不存在")
        compose.onNodeWithText("共0张").assertExists()
        compose.onNodeWithText("暂无匹配的报废单").assertExists()
    }
}
