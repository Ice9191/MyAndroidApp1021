package com.hxb.myandroidapp_1021.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.models.LoginParams
import com.hxb.myandroidapp_1021.models.User
import com.hxb.myandroidapp_1021.utils.HttpClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 如果要使用上下文context，需要继承AndroidViewModel
 */
class LoginTwoViewModel(application: Application) : AndroidViewModel(application) {
    private val _loading = MutableStateFlow(false)
    var loading = _loading.asStateFlow()

    private val _loginSuccessEvent = MutableSharedFlow<User>(extraBufferCapacity = 1)
    val loginSuccessEvent = _loginSuccessEvent.asSharedFlow()

    private val _loginErrorEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val loginErrorEvent = _loginErrorEvent.asSharedFlow()

    fun login(username: String, password: String) {
        // 如果正在加载中，正在请求，则返回
        if (_loading.value) return
        // 开启协程
        viewModelScope.launch {
            // 怕崩掉可以加try catch
            try {
                // 发起网络请求
                val user = HttpClient.request {
                    HttpClient.apiService.login(
                        LoginParams(
                            username,
                            password
                        )
                    )
                }
                // 通知外部收集者
                _loginSuccessEvent.emit(user)
                Log.d("HttpClient", "登录成功: 用户名：${user.username}")
            } catch (e: Exception) {
                _loading.value = false
                _loginErrorEvent.emit(e.message ?: "登录失败")
            }
        }
        _loading.value = false
    }
}