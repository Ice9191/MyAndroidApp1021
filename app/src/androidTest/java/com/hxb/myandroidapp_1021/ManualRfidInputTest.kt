package com.hxb.myandroidapp_1021

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.hxb.myandroidapp_1021.components.index.ReturnScanScreen
import com.hxb.myandroidapp_1021.components.index.components.ManualRfidInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ManualRfidInputTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun rejectsEmptyAndDuplicateInputThenNormalizesNewRfid() {
        val added = mutableListOf<String>()
        compose.setContent {
            ManualRfidInput(
                existingRfids = listOf("E200 1118 B001"), selectedCount = 0,
                readLabel = "手动读取", onRead = {}, onAdd = { added.add(it) }
            )
        }
        compose.onNodeWithText("手动读取").assertIsNotEnabled()
        compose.onNodeWithText("输入RFID编码").performTextInput("e2001118b001")
        compose.onNodeWithText("添加").performClick()
        compose.onNodeWithText("该RFID已在列表中，请勿重复添加").assertExists()
        compose.runOnIdle { assertEquals(0, added.size) }
        compose.onNodeWithText("e2001118b001").performTextReplacement("e200 9999 b004")
        compose.onNodeWithText("添加").performClick()
        compose.runOnIdle { assertEquals(listOf("E2009999B004"), added) }
        compose.onNodeWithText("手动读取").assertIsNotEnabled()
    }

    @Test
    fun clearingInputRestoresReadAction() {
        var readCount = 0
        compose.setContent {
            ManualRfidInput(
                existingRfids = emptyList(), selectedCount = 1,
                readLabel = "手动读取", onRead = { readCount++ }, onAdd = {}
            )
        }
        compose.onNodeWithText("输入RFID编码").performTextInput("E2001234")
        compose.onNodeWithText("添加").assertExists()
        compose.onNodeWithText("E2001234").performTextReplacement("")
        compose.onNodeWithText("手动读取").performClick()
        compose.runOnIdle { assertEquals(1, readCount) }
    }

    @Test
    fun returnPageAddsSelectedToolToUnreadList() {
        compose.setContent { ReturnScanScreen() }
        compose.onNodeWithText("未还 1").performScrollTo().performClick()
        compose.onNodeWithText("输入RFID编码").performScrollTo().performClick().performTextInput("e200 9999 b004")
        compose.onNodeWithText("e200 9999 b004").assertIsDisplayed()
        compose.onNodeWithText("添加").performScrollTo().performClick()
        compose.onNodeWithText("人工读取").assertExists()
        compose.onNodeWithText("E2009999B004").performScrollTo().assertExists()
        compose.onNode(isToggleable() and hasAnySibling(hasText("E2009999B004"))).assertIsOn()
        compose.onNodeWithText("未还 2").performScrollTo().assertExists()
        compose.onNodeWithText("已选1件").performScrollTo().assertExists()
    }
}
