package com.hxb.myandroidapp_1021.domain.common

import kotlinx.serialization.Serializable

/**
 * 查询可以降级到只读缓存，业务提交始终要求服务端确认。
 */
data class QueryData<T>(val data: T, val fromCache: Boolean = false)

class AppException(
    override val message: String,
    val code: String = "UNKNOWN",
    val cacheAllowed: Boolean = false,
    cause: Throwable? = null
) : RuntimeException(message, cause)

@Serializable
data class ServiceStatus(val status: String)
