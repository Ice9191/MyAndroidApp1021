package com.hxb.myandroidapp_1021

import android.os.SystemClock
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry

/**
 * 演示慢放时长（毫秒），默认 1500；可用运行参数 demoPauseMs 覆盖，传 0 关闭慢放。
 * 例：-Pandroid.testInstrumentationRunnerArguments.demoPauseMs=0
 */
internal val DEMO_PAUSE_MS: Long =
    InstrumentationRegistry.getArguments().getString("demoPauseMs")?.toLongOrNull() ?: 1500L

/**
 * 点击后停顿，供演示观察。
 */
internal fun SemanticsNodeInteraction.slowClick() {
    performClick()
    pauseForDemo()
}

/**
 * 输入文本后停顿，供演示观察。
 */
internal fun SemanticsNodeInteraction.slowType(text: String) {
    performTextInput(text)
    pauseForDemo()
}

/**
 * 只点击、不停顿：用于必须立即观察中间状态的用例（如连接测试进行中读取按钮状态），
 * 否则 1.5 秒的演示停顿会错过被测的瞬时状态。
 */
internal fun SemanticsNodeInteraction.clickNow() {
    performClick()
}

/**
 * 直接触发语义点击、不停顿：用于点击后立即弹出顶部通知（只显示 1 秒）的按钮，
 * 演示停顿会错过通知内容。
 */
internal fun SemanticsNodeInteraction.semanticsClickNow() {
    performSemanticsAction(SemanticsActions.OnClick)
}

/**
 * 直接触发语义点击并停顿：用于小屏上落在可视区域之外的按钮（如“绑定货位”），
 * 效果与真实点击一致，但不受滚动位置影响，调用前应先断言按钮可用。
 */
internal fun SemanticsNodeInteraction.slowSemanticsClick() {
    semanticsClickNow()
    pauseForDemo()
}

/**
 * 系统返回键后停顿，供演示观察。
 */
internal fun slowPressBack() {
    Espresso.pressBackUnconditionally()
    pauseForDemo()
}

private fun pauseForDemo() {
    if (DEMO_PAUSE_MS > 0) SystemClock.sleep(DEMO_PAUSE_MS)
}

/**
 * 读取服务端测试环境参数；缺少参数直接失败，不回退到前端演示数据。
 */
internal fun testData(key: String): String =
    requireNotNull(InstrumentationRegistry.getArguments().getString(key)) {
        "请通过 android.testInstrumentationRunnerArguments.$key 提供服务端测试数据"
    }
