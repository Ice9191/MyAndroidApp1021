package com.hxb.myandroidapp_1021.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 校验日期区间输入：格式为 yyyy-MM-dd 且开始不晚于结束；无错误返回 null。
 */
fun filterDateError(start: String, end: String): String? {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
    val allValid = listOf(start, end).all { value ->
        value.isBlank() || (value.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                runCatching { format.parse(value) }.isSuccess)
    }
    return when {
        !allValid -> "日期请按 yyyy-MM-dd 输入有效日期"
        start.isNotBlank() && end.isNotBlank() && start > end -> "开始日期不能晚于结束日期"
        else -> null
    }
}
