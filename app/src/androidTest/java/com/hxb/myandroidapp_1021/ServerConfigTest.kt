package com.hxb.myandroidapp_1021

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 服务器配置模块测试，对应《PDA功能测试清单》第 3 章 C01-C06。
 * 入口：首页右上角设置图标；默认预填 192.168.1.100 / 8080。
 */
@RunWith(AndroidJUnit4::class)
class ServerConfigTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun waitForText(text: String, timeoutMillis: Long = 5000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun textExists(text: String): Boolean =
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    /**
     * 页面共两个输入框：第 0 个是 IP 地址，第 1 个是端口号。
     */
    private fun field(index: Int) = composeRule.onAllNodes(hasSetTextAction())[index]

    private fun ipField() = field(0)

    private fun portField() = field(1)

    // 从首页设置图标进入服务器配置页。
    private fun openConfig() {
        waitForText("张三 · 材料员")
        composeRule.onNodeWithContentDescription("Setting").slowClick()
        waitForText("服务器配置")
    }

    // C01 默认值和测试成功：先显示正在测试...，约 1.5 秒后显示连接成功。
    @Test
    fun c01_default_values_and_test_success() {
        openConfig()
        composeRule.onNodeWithText("192.168.1.100").assertIsDisplayed()
        composeRule.onNodeWithText("8080").assertIsDisplayed()
        composeRule.onNodeWithText("尚未测试").assertIsDisplayed()

        // 测试耗时 1.5 秒，这里不能停顿，否则会错过“正在测试...”状态
        composeRule.onNodeWithText("尚未测试").clickNow()
        composeRule.onNodeWithText("正在测试...").assertIsDisplayed()
        waitForText("连接成功", 4000)
    }

    // C02 显示连接成功后修改端口，按钮立即恢复尚未测试。
    @Test
    fun c02_change_after_success_resets_status() {
        openConfig()
        composeRule.onNodeWithText("尚未测试").clickNow()
        waitForText("连接成功", 4000)

        portField().performTextClearance()
        portField().slowType("8081")
        composeRule.onNodeWithText("尚未测试").assertIsDisplayed()
        assertFalse("修改端口后不应保留旧的连接成功状态", textExists("连接成功"))
    }

    // C03 测试期间修改输入，旧结果作废，按钮回到尚未测试而不是连接成功。
    @Test
    fun c03_edit_during_test_invalidates_result() {
        openConfig()
        composeRule.onNodeWithText("尚未测试").clickNow()

        // 在 1.5 秒测试窗口内立即修改端口
        portField().performTextClearance()
        portField().performTextInput("8081")

        // 等测试请求结束（正在测试...消失）后再断言结果已被作废
        composeRule.waitUntil(4000) { !textExists("正在测试...") }
        assertTrue("测试期间修改输入后应回到尚未测试", textExists("尚未测试"))
        assertFalse("测试期间修改输入不应显示连接成功", textExists("连接成功"))
    }

    // C04 空输入反馈：地址为空、端口为空都显示连接失败。
    @Test
    fun c04_empty_input_shows_failure() {
        openConfig()

        ipField().performTextClearance()
        composeRule.onNodeWithText("尚未测试").slowClick()
        waitForText("连接失败", 4000)

        ipField().slowType("192.168.1.100")
        portField().performTextClearance()
        composeRule.onNodeWithText("尚未测试").slowClick()
        waitForText("连接失败", 4000)
    }

    // C05 测试期间按钮灰色不可点，不会重复发起测试。
    @Test
    fun c05_testing_button_is_disabled() {
        openConfig()
        composeRule.onNodeWithText("尚未测试").clickNow()
        composeRule.onNodeWithText("正在测试...").assertIsDisplayed()
        composeRule.onNodeWithText("正在测试...").assertIsNotEnabled()
        waitForText("连接成功", 4000)
    }

    // C06（上）保存并退出直接返回上一页，重新进入不保留修改（当前不持久化）。
    @Test
    fun c06a_save_returns_and_does_not_persist() {
        openConfig()
        portField().performTextClearance()
        portField().slowType("8081")
        composeRule.onNodeWithText("保存并退出").slowClick()

        waitForText("张三 · 材料员")
        openConfig()
        composeRule.onNodeWithText("8080").assertIsDisplayed()
        composeRule.onNodeWithText("尚未测试").assertIsDisplayed()
    }

    // C06（下）从工单拣选读取页的设置图标进入，保存后回到该读取页。
    @Test
    fun c06b_save_from_scan_page_returns_to_scan_page() {
        waitForText("张三 · 材料员")
        composeRule.onNodeWithText("工单拣选").performScrollTo().slowClick()
        waitForText("开始拣选")
        composeRule.onAllNodesWithText("开始拣选")[0].slowClick()
        waitForText("RFID 待开放")

        composeRule.onNodeWithContentDescription("Setting").slowClick()
        waitForText("服务器配置")
        composeRule.onNodeWithText("保存并退出").slowClick()

        waitForText("RFID 待开放")
        composeRule.onNodeWithText("工单拣选").assertIsDisplayed()
    }
}
