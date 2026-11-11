package com.hxb.myandroidapp_1021.ui.picking

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PickingScanScrollTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tabsFilterToolsAndButtonsReportEventsWithCurrentEnabledState() {
        val tools = listOf(
            PickingToolScanItem(1, "已读工具", ToolScanItemStatus.PICKED, rfid = "R1", recommendedLocation = "A1", unit = "件"),
            PickingToolScanItem(2, "未读工具", ToolScanItemStatus.UNPICKED, rfid = "R2", recommendedLocation = "A2", unit = "件")
        )
        val uiState = mutableStateOf(PickingScanUiState(tools = tools))
        var manualClicked = false
        var nextClicked = false
        composeRule.setContent {
            MaterialTheme {
                PickingScanContent(
                    uiState = uiState.value,
                    onManualClick = { manualClicked = true },
                    onNextClick = { nextClicked = true }
                )
            }
        }
        composeRule.onNodeWithText("RFID 待开放").assertIsNotEnabled()
        composeRule.onNodeWithText("下一步").assertIsNotEnabled()
        composeRule.onNodeWithText("人工拣选").performClick()
        composeRule.onNodeWithText("已读取 1").performClick()
        composeRule.onNodeWithText("已读工具").assertIsDisplayed()
        composeRule.onNodeWithText("未读工具").assertDoesNotExist()
        composeRule.onNodeWithText("未读取 1").performClick()
        composeRule.onNodeWithText("未读工具").assertIsDisplayed()
        composeRule.onNodeWithText("已读工具").assertDoesNotExist()
        composeRule.runOnIdle {
            uiState.value = uiState.value.copy(tools = tools.map { it.copy(status = ToolScanItemStatus.PICKED) })
        }
        composeRule.onNodeWithText("下一步").performClick()
        composeRule.runOnIdle { assertTrue(manualClicked && nextClicked) }
    }

    @Test
    fun listGesturesStayInsideListAndTabGesturesScrollPage() {
        val viewModel = PickingScanViewModel()
        composeRule.setContent {
            MaterialTheme {
                PickingScanScreen(viewModel = viewModel, onManualClick = {})
            }
        }

        composeRule.waitUntil { !viewModel.uiState.value.isLoading }
        composeRule.waitForIdle()
        val scrollables = composeRule.onAllNodes(hasScrollAction())
        fun scrollPosition(index: Int): Float = scrollables[index].fetchSemanticsNode()
            .config[SemanticsProperties.VerticalScrollAxisRange].value()

        scrollables[1].performTouchInput { swipeDown() }
        composeRule.waitForIdle()
        assertEquals(0f, scrollPosition(0), 0.1f)

        repeat(4) { scrollables[1].performTouchInput { swipeUp() } }
        composeRule.waitForIdle()
        assertTrue(scrollPosition(1) > 0f)
        assertEquals(0f, scrollPosition(0), 0.1f)

        val tabCenter = composeRule.onNodeWithText("全部 5").fetchSemanticsNode().boundsInRoot.center
        composeRule.onRoot().performTouchInput {
            swipe(tabCenter, Offset(tabCenter.x, 1f))
        }
        composeRule.waitForIdle()
        assertTrue(scrollPosition(0) > 0f)

        val buttonTop = composeRule.onNodeWithText("人工拣选")
            .fetchSemanticsNode().boundsInRoot.top
        val viewportTop = scrollables[0].fetchSemanticsNode().boundsInRoot.top
        val expectedButtonTop = viewportTop + with(composeRule.density) { 12.dp.toPx() }
        assertEquals(expectedButtonTop, buttonTop, with(composeRule.density) { 2.dp.toPx() })

        val movedTabCenter = composeRule.onNodeWithText("全部 5")
            .fetchSemanticsNode().boundsInRoot.center
        val pageHeight = composeRule.onRoot().fetchSemanticsNode().boundsInRoot.height
        composeRule.onRoot().performTouchInput {
            swipe(movedTabCenter, Offset(movedTabCenter.x, pageHeight - 1f))
        }
        composeRule.waitForIdle()
        assertEquals(0f, scrollPosition(0), 0.1f)
    }
}
