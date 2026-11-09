package com.hxb.myandroidapp_1021.rfid_write.models

/**
 * 盘点到的标签信息
 */
data class TagInfo(
    val epc: String,
    val rssi: Int,
    val readCount: Int
) {
    val epcByteLength: Int get() = epc.length / 2
}

sealed interface WriteResult {
    // 写入成功
    data object Success : WriteResult

    // UHF 模块未打开
    data object DeviceNotOpen : WriteResult

    // 输入校验未通过
    data class InvalidInput(val reason: String) : WriteResult

    // 没有目标标签
    data object NoTargetTag : WriteResult

    // 盘点超时
    data object ScanTimeout : WriteResult

    // SDK返回写入失败
    data class HardwareFail(val reason: String? = null) : WriteResult

    // 协程被取消
    data object Cancelled : WriteResult

    // 未知错误
    data class UnknownError(val throwable: Throwable) : WriteResult
}

/**
 * 写入请求 + 校验
 */
data class WriteRequest(
    val targetEpcHex: String,
    val newEpcHex: String,
    val accessPasswordHex: String = DEFAULT_PASSWORD,
    val powerDbm: Int = DEFAULT_POWER
) {
    companion object {
        const val DEFAULT_PASSWORD = "00000000"
        const val DEFAULT_POWER = 26
        const val MIN_POWER = 5
        const val MAX_POWER = 33

        // EPC载荷字节范围
        const val MIN_EPC_BYTES = 2
        const val MAX_EPC_BYTES = 62

        // 访问密码固定4字节
        private const val PASSWORD_HEX_LENGTH = 8
    }

    fun validate(): String? = when {
        !targetEpcHex.isValidHex() -> "目标标签 EPC 格式不正确"
        targetEpcHex.length / 2 !in MIN_EPC_BYTES..MAX_EPC_BYTES -> "目标标签 EPC 长度应在 ${MIN_EPC_BYTES}~${MAX_EPC_BYTES} 字节之间"
        !newEpcHex.isValidHex() -> "新 EPC 格式不正确，需为偶数长度的十六进制字符串"
        newEpcHex.length / 2 !in MIN_EPC_BYTES..MAX_EPC_BYTES -> "新 EPC 长度应在 ${MIN_EPC_BYTES}~${MAX_EPC_BYTES} 字节之间"
        accessPasswordHex.length != PASSWORD_HEX_LENGTH || !accessPasswordHex.isValidHex() -> "访问密码必须是 8 位十六进制字符"
        powerDbm !in MIN_POWER..MAX_POWER -> "发射功率应在 ${MIN_POWER}~${MAX_POWER} dBm 之间"
        else -> null
    }

    val isPowerValid: Boolean get() = powerDbm in MIN_POWER..MAX_POWER
}

/**
 * Hex字符串是否格式正确
 */
internal fun String.isValidHex(): Boolean {
    return !(isEmpty() || length % 2 != 0) && all { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }
}

data class WriteUiState(
    // 设备
    val deviceReady: Boolean = false,

    // 扫描阶段
    val isScanning: Boolean = false,
    val targetTag: TagInfo? = null,

    // 写入参数
    val newEpcInput: String = "",
    val accessPassword: String = WriteRequest.DEFAULT_PASSWORD,
    val powerDbm: Int = WriteRequest.DEFAULT_POWER,

    // 写入阶段
    val isWriting: Boolean = false,
    val lastResult: WriteResult? = null,

    // 一次性提示
    val message: UiMessage? = null
) {
    // 写入按钮是否可用
    val canWrite: Boolean
        get() = deviceReady && targetTag != null && newEpcInput.isNotBlank() && !isWriting && !isScanning

    // 扫描按钮是否可用
    val canScan: Boolean
        get() = deviceReady && !isScanning && !isWriting
}

sealed interface UiMessage {
    val text: String

    data class Success(override val text: String) : UiMessage
    data class Error(override val text: String) : UiMessage
    data class Info(override val text: String) : UiMessage

    companion object {
        fun from(result: WriteResult): UiMessage = when (result) {
            WriteResult.Success -> Success("写入成功")
            WriteResult.DeviceNotOpen -> Error("UHF 模块未打开")
            WriteResult.NoTargetTag -> Error("请先扫描并锁定目标标签")
            WriteResult.ScanTimeout -> Error("未发现标签，请靠近后重试")
            is WriteResult.InvalidInput -> Error(result.reason)
            is WriteResult.HardwareFail -> Error("硬件写入失败${result.reason?.let { "：$it" } ?: ""}")
            WriteResult.Cancelled -> Info("操作已取消")
            is WriteResult.UnknownError -> Error("未知错误：${result.throwable.message}")
        }
    }
}

/**
 * UHF 设备抽象
 */
interface UhfDriver {
    // 打开设备并设置初始功率
    suspend fun open(powerDbm: Int): Boolean

    // 单次盘点，超时返回 null
    suspend fun scanSingleTag(timeoutMs: Int): TagInfo?

    // 按请求写入 EPC
    suspend fun writeEpc(request: WriteRequest): WriteResult

    // 释放设备
    fun close()
}