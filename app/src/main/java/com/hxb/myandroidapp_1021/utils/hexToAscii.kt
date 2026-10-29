package com.hxb.myandroidapp_1021.utils

/**
 * 16进制字符串转ASCII字符串
 * 例如："414243" -> "ABC"
 */
fun hexToAscii(hexString: String): String {
    if (hexString.isEmpty() || hexString.length % 2 != 0) {
        return "无效的十六进制格式"
    }

    val output = StringBuilder()
    try {
        // 每次取 2 个字符（1个字节）
        var i = 0
        while (i < hexString.length) {
            val hexPair = hexString.substring(i, i + 2)
            // 将 16进制字符串 转为 10进制整数，再转为 Char
            val decimalValue = hexPair.toInt(16)
            output.append(decimalValue.toChar())
            i += 2
        }
    } catch (e: Exception) {
        return "转换失败：${e.message}"
    }
    return output.toString()
}