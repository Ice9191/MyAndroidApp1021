package com.hxb.myandroidapp_1021.utils

import android.content.Context
import com.hxb.myandroidapp_1021.local.TodoPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 全局单例
 * 为什么不直接写在ViewModel中，而是写在一个单例中？
 *  因为昵称和Todos不太一样，Todos是组件中会用，但是昵称是全局都有可能用到
 *  像这种全局都会用的东西，一般都是会抽成一个单例，方便在任何地方用
 */
object SettingManager {
    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private var preferences: TodoPreferences? = null

    /**
     * 初始化
     */
    suspend fun init(context: Context) {
        // 创建preferences实例
        preferences = TodoPreferences(context.applicationContext)
        // 从DataStore中读取昵称
        _nickname.value = preferences?.getNickName() ?: ""
    }

    /**
     * 设置昵称
     */
    suspend fun setNickname(nickname: String) {
        // 更新内存
        _nickname.value = nickname
        // 更新本地
        preferences?.saveNickName(nickname)
    }
}