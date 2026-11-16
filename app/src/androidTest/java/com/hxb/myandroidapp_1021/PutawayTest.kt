package com.hxb.myandroidapp_1021

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 工器具上架模块测试，对应《PDA功能测试清单》第 4 章 D01-D16。
 * 起始单据 SJ20261111001：3 件，初始 1 件已上架（E280 0003 货位 A1-03），进度 1/3。
 */
@RunWith(AndroidJUnit4::class)
class PutawayTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun waitForText(text: String, timeoutMillis: Long = 5000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForTextGone(text: String, timeoutMillis: Long = 5000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun textExists(text: String): Boolean =
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    private fun textExistsPartly(text: String): Boolean =
        composeRule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()

    private fun waitForTextPartly(text: String, timeoutMillis: Long = 3000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    /**
     * 列表页只有搜索框一个输入框。
     */
    private fun searchField() = composeRule.onAllNodes(hasSetTextAction())[0]

    /**
     * 上架作业页输入框顺序：第 0 个是 RFID，第 1 个是实际货位。
     */
    private fun rfidField() = composeRule.onAllNodes(hasSetTextAction())[0]

    private fun locationField() = composeRule.onAllNodes(hasSetTextAction())[1]

    /**
     * “绑定货位”按钮在货位输入框下方，小屏上会落到可视区域之外，
     * 因此先断言可用，再直接触发语义点击，避免依赖滚动位置。
     */
    private fun clickBindLocation() {
        composeRule.onNodeWithText("绑定货位").assertIsEnabled()
        composeRule.onNodeWithText("绑定货位").semanticsClickNow()
    }

    private fun openPutawayList() {
        waitForText("张三 · 材料员")
        composeRule.onNodeWithText("工器具上架").performScrollTo().slowClick()
        waitForText("请输入上架单号或入库单号")
    }

    /**
     * 先按单号过滤到一张单据，再点该单据的“开始上架”进入作业页。
     */
    private fun openOrder(orderNo: String) {
        searchField().performTextClearance()
        searchField().slowType(orderNo)
        waitForText("共1张")
        composeRule.onAllNodesWithText("开始上架")[0].slowClick()
        waitForText("上架作业")
        // 作业页操作人由 ViewModel 延迟 500ms 加载
        waitForText("材料员")
    }

    /**
     * 识别工具并绑定货位，进入可提交状态。
     */
    private fun identifyAndBind(rfid: String, location: String) {
        rfidField().performTextClearance()
        rfidField().slowType(rfid)
        composeRule.onNodeWithText("识别 RFID").slowClick()
        locationField().performTextClearance()
        locationField().slowType(location)
        clickBindLocation()
        waitForText("已绑定货位 $location")
    }

    /**
     * 打开确认弹窗并确认。结果提示改为顶部通知、只显示 1 秒，所以确认按钮不能带停顿。
     */
    private fun confirmPutaway(expectedNotification: String = "上架成功，可继续扫描下一件") {
        composeRule.onNodeWithText("确认本件上架").slowClick()
        composeRule.onNodeWithText("确认").clickNow()
        waitForText(expectedNotification, 4000)
    }

    // D01 按上架单号或入库单号查询，清空后恢复 3 张单。
    @Test
    fun d01_search_by_order_no_and_inbound_no() {
        openPutawayList()
        waitForText("共3张")

        searchField().performTextClearance()
        searchField().slowType("SJ20261111001")
        waitForText("共1张")
        assertTrue(textExists("SJ20261111001"))

        searchField().performTextClearance()
        searchField().slowType("RK20261111001")
        waitForText("共1张")
        assertTrue("按入库单号搜索应找到同一张上架单", textExists("SJ20261111001"))

        searchField().performTextClearance()
        waitForText("共3张")
    }

    // D02 状态筛选：未完成 2 张、已完成 1 张、全部 3 张。
    @Test
    fun d02_status_filter_counts() {
        openPutawayList()
        waitForText("共3张")

        composeRule.onNodeWithText("全部").slowClick()
        composeRule.onNodeWithText("未完成").slowClick()
        waitForText("共2张")

        composeRule.onNodeWithText("未完成").slowClick()
        composeRule.onNodeWithText("已完成").slowClick()
        waitForText("共1张")

        composeRule.onNodeWithText("已完成").slowClick()
        composeRule.onNodeWithText("全部").slowClick()
        waitForText("共3张")
    }

    // D03 开始上架进入作业页，显示单号、状态、操作人和进度。
    @Test
    fun d03_open_order_shows_work_page_header() {
        openPutawayList()
        openOrder("SJ20261111001")

        composeRule.onNodeWithText("上架作业").assertIsDisplayed()
        assertTrue(textExists("上架单号"))
        assertTrue(textExists("SJ20261111001"))
        assertTrue(textExists("入库单号"))
        assertTrue(textExists("RK20261111001"))
        assertTrue(textExists("单据状态"))
        assertTrue(textExists("部分上架"))
        assertTrue(textExists("当前操作人"))
        assertTrue(textExists("材料员"))
        assertTrue(textExists("上架进度 1/3"))
    }

    // D04 错误 RFID 被拒绝；RFID 为空时识别按钮不可点。
    @Test
    fun d04_invalid_rfid_rejected_and_empty_disabled() {
        openPutawayList()
        openOrder("SJ20261111001")

        composeRule.onNodeWithText("识别 RFID").assertIsNotEnabled()

        rfidField().slowType("abc")
        // 校验失败通过顶部通知提示，只显示 1 秒，点击不能带停顿
        composeRule.onNodeWithText("识别 RFID").clickNow()
        waitForText("RFID 不属于当前上架单，请核对工具")
        composeRule.onNodeWithText("确认本件上架").assertIsNotEnabled()
        assertTrue("进度不应变化", textExists("上架进度 1/3"))
    }

    // D05 识别工具后显示 RFID、名称、物资编码、批次号和仓库。
    @Test
    fun d05_identify_tool_shows_detail() {
        openPutawayList()
        openOrder("SJ20261111001")

        rfidField().slowType("E280 0001")
        composeRule.onNodeWithText("识别 RFID").slowClick()
        waitForText("当前工具")
        assertTrue("应显示工具名称", textExists("绝缘扳手"))
        assertTrue("应显示物资编码", textExists("GJ-001"))
        assertTrue("应显示批次号", textExists("PC001"))
        assertTrue("应显示目标仓库", textExists("中心工具库"))
        assertFalse("识别成功后不应有错误提示", textExists("RFID 不属于当前上架单，请核对工具"))
    }

    // D06 无效货位和跨仓库货位都不能绑定。
    @Test
    fun d06_invalid_and_cross_warehouse_location() {
        openPutawayList()
        openOrder("SJ20261111001")
        rfidField().slowType("E280 0001")
        composeRule.onNodeWithText("识别 RFID").slowClick()
        waitForText("当前工具")

        locationField().performTextClearance()
        locationField().slowType("abc")
        clickBindLocation()
        waitForText("货位不存在，请重新扫描或输入")
        composeRule.onNodeWithText("确认本件上架").assertIsNotEnabled()

        locationField().performTextClearance()
        locationField().slowType("B1-01")
        clickBindLocation()
        waitForText("货位不属于当前工具的目标仓库")
        composeRule.onNodeWithText("确认本件上架").assertIsNotEnabled()
    }

    // D07 绑定合法货位后提示成功，确认按钮由灰变可点。
    @Test
    fun d07_bind_location_enables_confirm() {
        openPutawayList()
        openOrder("SJ20261111001")
        rfidField().slowType("E280 0001")
        composeRule.onNodeWithText("识别 RFID").slowClick()
        waitForText("当前工具")

        locationField().slowType("A1-01")
        clickBindLocation()
        waitForText("已绑定货位 A1-01")
        composeRule.onNodeWithText("确认本件上架").assertIsEnabled()
    }

    // D08 上架弹窗内容正确，取消后进度不变、仍停留当前页。
    @Test
    fun d08_confirm_dialog_cancel_keeps_progress() {
        openPutawayList()
        openOrder("SJ20261111001")
        identifyAndBind("E280 0001", "A1-01")

        composeRule.onNodeWithText("确认本件上架").slowClick()
        waitForTextPartly("工具：绝缘扳手")
        assertTrue("弹窗应包含 RFID", textExistsPartly("RFID：E280 0001"))
        assertTrue("弹窗应包含仓库", textExistsPartly("仓库：中心工具库"))
        assertTrue("弹窗应包含实际货位", textExistsPartly("实际货位：A1-01"))
        assertTrue("弹窗应包含上架人", textExistsPartly("上架人：材料员"))

        composeRule.onNodeWithText("取消").slowClick()
        waitForTextGone("工具：绝缘扳手", 3000)
        assertTrue("取消后进度不应变化", textExists("上架进度 1/3"))
    }

    // D09 确认后单件上架成功：提示、明细状态、货位和进度都更新。
    @Test
    fun d09_confirm_item_success() {
        openPutawayList()
        openOrder("SJ20261111001")
        identifyAndBind("E280 0001", "A1-01")
        confirmPutaway()

        assertTrue("进度应变为 2/3", textExists("上架进度 2/3"))
        assertTrue("页签数量应更新", textExists("已上架 2"))
        assertTrue(textExists("全部 3"))
        assertTrue("明细应保留绑定的货位", textExists("A1-01"))
        assertTrue("明细库存状态应为在库", textExists("在库"))
    }

    // D10 重复上架同一件被拒绝，进度不再增加。
    @Test
    fun d10_duplicate_putaway_rejected() {
        openPutawayList()
        openOrder("SJ20261111001")
        identifyAndBind("E280 0001", "A1-01")
        confirmPutaway()

        rfidField().performTextClearance()
        rfidField().slowType("E280 0001")
        composeRule.onNodeWithText("识别 RFID").clickNow()
        waitForText("该工具已上架，无需重复操作")
        assertTrue("重复操作后进度仍应为 2/3", textExists("上架进度 2/3"))
    }

    // D11 完成整单：进度 3/3、状态已上架、页签 3/0/3 和完成提示。
    @Test
    fun d11_complete_whole_order() {
        openPutawayList()
        openOrder("SJ20261111001")
        identifyAndBind("E280 0001", "A1-01")
        confirmPutaway()
        identifyAndBind("E280 0002", "A1-02")
        confirmPutaway("本单已全部上架，工具库存已更新")

        assertTrue("进度应为 3/3", textExists("上架进度 3/3"))
        assertTrue("单据状态应为已上架", textExists("已上架"))
        assertTrue(textExists("全部 3"))
        assertTrue(textExists("待上架 0"))
        assertTrue(textExists("已上架 3"))
        waitForText("本单已全部上架，工具库存已更新")
    }

    // D12 返回列表后本单显示已上架、上架人、时间和 3/3 进度。
    @Test
    fun d12_back_to_list_reflects_result() {
        openPutawayList()
        openOrder("SJ20261111001")
        identifyAndBind("E280 0001", "A1-01")
        confirmPutaway()
        identifyAndBind("E280 0002", "A1-02")
        confirmPutaway("本单已全部上架，工具库存已更新")

        composeRule.onNodeWithContentDescription("Back").slowClick()
        // 返回后搜索框仍保留进入前的单号，占位提示不会出现，用列表页的“开始上架”按钮判断已回到列表
        waitForText("开始上架")
        waitForText("共1张")
        assertTrue("列表应显示完成进度", textExists("上架进度 3/3"))
        assertTrue("列表应显示上架人", textExists("材料员"))
    }

    // D13 对已完成件再次识别，提示无需重复操作且不重复计数。
    @Test
    fun d13_completed_item_identify_again() {
        openPutawayList()
        openOrder("SJ20261111001")

        rfidField().slowType("E280 0003")
        composeRule.onNodeWithText("识别 RFID").clickNow()
        waitForText("该工具已上架，无需重复操作")
        assertTrue("进度不应重复计数", textExists("上架进度 1/3"))
    }

    // D14 绑定货位后修改 RFID 或货位输入，绑定失效且确认按钮变灰。
    @Test
    fun d14_edit_after_bind_invalidates_binding() {
        openPutawayList()
        openOrder("SJ20261111001")
        identifyAndBind("E280 0002", "A1-02")

        rfidField().slowType("x")
        composeRule.onNodeWithText("确认本件上架").assertIsNotEnabled()

        // 恢复识别并重新绑定，再修改货位输入
        rfidField().performTextClearance()
        identifyAndBind("E280 0002", "A1-02")
        composeRule.onNodeWithText("确认本件上架").assertIsEnabled()
        locationField().slowType("9")
        composeRule.onNodeWithText("确认本件上架").assertIsNotEnabled()
    }

    // D15 当前模拟数据没有“异常”状态单据，本项无数据可测，补数据后再补用例。

    // D16 首次进入列表先显示加载状态，随后出现数据。
    @Test
    fun d16_list_shows_loading_indicator_first() {
        waitForText("张三 · 材料员")
        composeRule.onNodeWithText("工器具上架").performScrollTo().clickNow()

        composeRule.waitUntil(2000) {
            composeRule.onAllNodes(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
                .fetchSemanticsNodes().isNotEmpty()
        }
        waitForText("共3张")
    }
}
