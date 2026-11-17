package com.hxb.myandroidapp_1021

import android.os.SystemClock
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 首页模块测试，对应《PDA功能测试清单》第 1 章 A01-A06。
 * 应用启动直接进入首页，不经过登录页。
 */
@RunWith(AndroidJUnit4::class)
class DashboardTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun waitForText(text: String, timeoutMillis: Long = 5000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForDescription(description: String, timeoutMillis: Long = 5000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithContentDescription(description).fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun textExists(text: String): Boolean =
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    /**
     * 等待服务端返回测试环境配置的人员身份。
     */
    private fun waitForDashboard() {
        waitForText(testData("profileText"))
    }

    /**
     * 点击首页菜单项，菜单超出屏幕时需要先滚动到可见区域。
     */
    private fun clickMenu(title: String) {
        waitForText(title)
        composeRule.onNodeWithText(title).performScrollTo().slowClick()
    }

    /**
     * 点击左上角返回，回到上一层页面。
     */
    private fun clickBack() {
        waitForDescription("Back")
        composeRule.onNodeWithContentDescription("Back").slowClick()
    }

    /**
     * 断言系统返回键退出了应用：Activity 销毁后不能再调用 onActivity，只能轮询场景状态。
     */
    private fun assertAppExited() {
        val deadline = System.currentTimeMillis() + 3000
        while (System.currentTimeMillis() < deadline) {
            if (composeRule.activityRule.scenario.state == Lifecycle.State.DESTROYED) return
            SystemClock.sleep(50)
        }
        throw AssertionError("按系统返回键后应用未退出（Activity 未销毁）")
    }

    // A01 全部菜单能进入，标题与该入口一致，返回后回到首页。
    @Test
    fun a01_all_menu_entries_open_and_back_to_dashboard() {
        waitForDashboard()
        val menus = listOf(
            "工器具上架",
            "工单拣选",
            "工单归还上架",
            "临时借出",
            "临时归还",
            "其他拣选",
            "其他上架",
            "库存明细",
            "超期告警"
        )
        menus.forEach { title ->
            clickMenu(title)
            waitForDescription("Back")
            composeRule.waitForIdle()
            // 子页面标题与入口文字一致，此时首页已被移出组合
            composeRule.onNodeWithText(title).assertIsDisplayed()
            assertFalse("进入 $title 后不应停留在首页", textExists("欢迎回来"))
            clickBack()
            composeRule.waitForIdle()
            waitForText("欢迎回来")
            composeRule.onNodeWithText("工器具库系统").assertIsDisplayed()
        }
    }

    // A02 设置入口进入服务器配置，返回后回到首页。
    @Test
    fun a02_settings_entry_opens_server_config() {
        waitForDashboard()
        composeRule.onNodeWithContentDescription("Setting").slowClick()
        waitForText("服务器配置")
        clickBack()
        waitForText("欢迎回来")
        composeRule.onNodeWithText("工器具库系统").assertIsDisplayed()
    }

    // A03 读取页/新建页的首页图标回到首页；再次进入是列表页。
    @Test
    fun a03_home_icon_in_inner_pages_and_reentry_state() {
        waitForDashboard()

        // 工单拣选：列表 -> 读取页 -> 首页图标
        clickMenu("工单拣选")
        waitForText("开始拣选")
        composeRule.onAllNodesWithText("开始拣选")[0].slowClick()
        waitForText("RFID 待开放")
        composeRule.onNodeWithContentDescription("Dashboard").slowClick()
        waitForDashboard()
        clickMenu("工单拣选")
        waitForText("开始拣选")
        composeRule.waitForIdle()
        assertFalse("再次进入工单拣选不应直接打开读取页", textExists("RFID 待开放"))
        clickBack()
        waitForDashboard()

        // 临时借出：列表 -> 新建表单 -> 首页图标
        clickMenu("临时借出")
        waitForText("新建临时借出")
        composeRule.onNodeWithText("新建临时借出").slowClick()
        waitForText("临时借出 · 新建")
        composeRule.onNodeWithContentDescription("Dashboard").slowClick()
        waitForDashboard()
        clickMenu("临时借出")
        waitForText("新建临时借出")
        composeRule.waitForIdle()
        assertFalse("再次进入临时借出不应直接打开新建表单", textExists("临时借出 · 新建"))
    }

    // A04 快速点两次同一个菜单只出现一层页面，连续返回不出现空白页。
    @Test
    fun a04_double_click_menu_opens_single_page() {
        waitForDashboard()
        val menu = composeRule.onNodeWithText("库存明细")
        menu.performScrollTo().slowClick()
        // 第一次点击后页面开始滑动，首页仍在组合中，此时再点一次模拟重复点击
        menu.slowClick()
        waitForText("筛选")
        clickBack()
        composeRule.waitForIdle()
        // 只弹一层：一次返回必须直接回到首页
        waitForText("欢迎回来")
        assertFalse(
            "一次返回后不应还有子页面的返回按钮",
            composeRule.onAllNodesWithContentDescription("Back").fetchSemanticsNodes().isNotEmpty()
        )
    }

    // A05 系统返回键：列表页返回上一层，首页返回退出应用；登录页返回退出的断言在 B01。
    @Test
    fun a05_system_back_key_on_dashboard_and_list() {
        waitForDashboard()
        clickMenu("库存明细")
        waitForText("筛选")
        slowPressBack()
        waitForText("欢迎回来")

        slowPressBack()
        assertAppExited()
    }

    // A06 首页显示登录人、头像和服务器已连接状态。
    @Test
    fun a06_dashboard_user_and_server_status() {
        waitForDashboard()
        composeRule.onNodeWithText(testData("profileText")).assertIsDisplayed()
        composeRule.onNodeWithText("服务器已连接").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Logo").assertIsDisplayed()
        composeRule.onNodeWithText("工器具库系统").assertIsDisplayed()
    }
}
