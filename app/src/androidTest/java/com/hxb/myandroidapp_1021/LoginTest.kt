package com.hxb.myandroidapp_1021

import android.os.SystemClock
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 登录模块测试，对应《PDA功能测试清单》第 2 章 B01-B06。
 * 账号、密码及预期身份由运行参数提供，认证由真实服务端处理。
 */
@RunWith(AndroidJUnit4::class)
class LoginTest {

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
     * 输入框按界面顺序定位：第 0 个是账号，第 1 个是密码。
     */
    private fun field(index: Int) = composeRule.onAllNodes(hasSetTextAction())[index]

    private fun fillCredentials(username: String, password: String) {
        if (username.isNotEmpty()) field(0).slowType(username)
        if (password.isNotEmpty()) field(1).slowType(password)
    }

    /**
     * 从首页退出登录进入登录页，这是当前版本进入登录页的唯一入口。
     */
    private fun enterLoginPage() {
        waitForText(testData("profileText"))
        composeRule.onNodeWithText("退出登录").slowClick()
        waitForText("账号登录")
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

    // B01 退出登录直接进入登录页，没有确认弹窗；登录页按系统返回只会退出应用。
    @Test
    fun b01_logout_has_no_dialog_and_back_exits_app() {
        enterLoginPage()
        composeRule.waitForIdle()
        assertFalse("退出登录不应出现确认弹窗", textExists("确认"))
        assertFalse("退出登录不应出现取消按钮", textExists("取消"))
        composeRule.onNodeWithText("账号登录").assertIsDisplayed()

        slowPressBack()
        assertAppExited()
    }

    // B02 账号密码都为空时提示请输入账号。
    @Test
    fun b02_empty_account_shows_error() {
        enterLoginPage()
        composeRule.onNodeWithText("登录").slowClick()
        waitForText("请输入账号")
        composeRule.onNodeWithText("账号登录").assertIsDisplayed()
    }

    // B03 账号已填、密码为空时提示请输入密码。
    @Test
    fun b03_empty_password_shows_error() {
        enterLoginPage()
        fillCredentials(testData("username"), "")
        composeRule.onNodeWithText("登录").slowClick()
        waitForText("请输入密码")
        composeRule.onNodeWithText("账号登录").assertIsDisplayed()
    }

    // B04 密码显隐切换，输入内容不丢失。
    @Test
    fun b04_toggle_password_visibility() {
        enterLoginPage()
        fillCredentials(testData("username"), testData("password"))

        // 密码显隐只是绘制层的变化，语义树始终是明文，因此用截图对比判断外观是否变化。
        // 先点标题清掉输入焦点，避免光标闪烁干扰像素比较。
        composeRule.onNodeWithText("工器具库系统：PDA端").slowClick()
        composeRule.waitForIdle()
        val masked = field(1).captureToImage()

        composeRule.onNodeWithContentDescription("Toggle password visibility").slowClick()
        composeRule.waitForIdle()
        val visible = field(1).captureToImage()
        assertFalse(
            "点击显示按钮后密码框外观应变化（隐藏转明文）",
            masked.asAndroidBitmap().sameAs(visible.asAndroidBitmap())
        )
        // 输入内容不丢失：切换后语义值仍是原来的密码
        assertTrue("切换显示后密码内容应保持不变", textExists(testData("password")))

        composeRule.onNodeWithContentDescription("Toggle password visibility").slowClick()
        composeRule.waitForIdle()
        val maskedAgain = field(1).captureToImage()
        assertFalse(
            "再次点击后密码框外观应变化（明文转隐藏）",
            visible.asAndroidBitmap().sameAs(maskedAgain.asAndroidBitmap())
        )
        assertTrue("再次切换后密码内容应保持不变", textExists(testData("password")))
    }

    // B05 登录加载期间按钮不可点、文字变为进度，加载完成后只进入一次首页。
    @Test
    fun b05_loading_state_and_single_navigation() {
        enterLoginPage()
        fillCredentials(testData("username"), testData("password"))
        composeRule.onNodeWithText("登录").slowClick()

        // 加载中：按钮文字消失，两个输入框仍在，停留在登录页
        composeRule.waitUntil(2000) {
            composeRule.onAllNodesWithText("登录").fetchSemanticsNodes().isEmpty()
        }
        assertTrue(composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size == 2)

        waitForText(testData("profileText"), 6000)
        composeRule.waitForIdle()
        assertFalse("登录成功后不应还停留在登录页", textExists("账号登录"))
    }

    // B06 使用服务端测试账号登录成功，登录页从返回栈移除。
    @Test
    fun b06_admin_login_success_and_login_removed_from_back_stack() {
        enterLoginPage()
        fillCredentials(testData("username"), testData("password"))
        composeRule.onNodeWithText("登录").slowClick()
        waitForText(testData("profileText"), 6000)

        slowPressBack()
        assertAppExited()
    }

    // B07 密码错误被拒绝，停留在登录页并提示账号或密码错误。
    @Test
    fun b07_wrong_password_is_rejected() {
        enterLoginPage()
        fillCredentials(testData("username"), testData("invalidPassword"))
        composeRule.onNodeWithText("登录").slowClick()
        waitForText("账号或密码错误")
        composeRule.onNodeWithText("账号登录").assertIsDisplayed()
        assertFalse("密码错误时不应进入首页", textExists(testData("profileText")))
    }

    // B08 未注册账号由服务端拒绝。
    @Test
    fun b08_wrong_account_is_rejected() {
        enterLoginPage()
        fillCredentials(testData("invalidUsername"), testData("password"))
        composeRule.onNodeWithText("登录").slowClick()
        waitForText("账号或密码错误")
        composeRule.onNodeWithText("账号登录").assertIsDisplayed()
        assertFalse("未注册账号不应进入首页", textExists(testData("profileText")))
    }
}
