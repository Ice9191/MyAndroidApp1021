package com.hxb.myandroidapp_1021.data.network

import com.hxb.myandroidapp_1021.domain.common.AppException
import kotlinx.serialization.Serializable
import retrofit2.Response

@Serializable
data class Page<T>(val items: List<T>, val page: Int, val pageSize: Int, val total: Int)

/**
 * 当前页面使用本地组合筛选，先读取全部授权分页，避免只对第一页筛选。
 */
suspend fun <T> HttpClient.loadAll(request: suspend (Int) -> Response<ApiResponse<Page<T>>>): List<T> {
    val items = mutableListOf<T>()
    var page = 1
    do {
        val result = execute { request(page) }
        if (result.page != page || result.pageSize <= 0 || result.total < 0 ||
            (result.items.isEmpty() && items.size < result.total)
        ) {
            throw AppException("服务器分页数据不完整，请刷新后重试", "INVALID_RESPONSE")
        }
        items.addAll(result.items)
        if (items.size >= result.total) break
        page++
    } while (true)
    return items
}
