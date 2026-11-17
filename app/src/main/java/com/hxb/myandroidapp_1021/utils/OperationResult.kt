package com.hxb.myandroidapp_1021.utils

import com.hxb.myandroidapp_1021.domain.common.AppException
import kotlinx.coroutines.CancellationException

suspend fun <T> operationResult(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (error: CancellationException) {
    throw error
} catch (error: Exception) {
    Result.failure(error)
}

fun Throwable.userMessage(): String = if (this is AppException) message else "操作失败，请重试"
