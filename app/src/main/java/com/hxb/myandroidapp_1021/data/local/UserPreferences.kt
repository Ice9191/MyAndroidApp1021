package com.hxb.myandroidapp_1021.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hxb.myandroidapp_1021.domain.common.AppException
import com.hxb.myandroidapp_1021.domain.session.LoginResponse
import com.hxb.myandroidapp_1021.domain.session.ServerConfig
import com.hxb.myandroidapp_1021.domain.session.SessionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject
import javax.inject.Singleton


private val Context.userDataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val cipher: SessionCipher,
    private val json: Json
) {
    private val host = stringPreferencesKey("server_host")
    private val port = stringPreferencesKey("server_port")
    private val username = stringPreferencesKey("last_username")
    private val session = stringPreferencesKey("encrypted_session")
    private val _state = MutableStateFlow(SessionState())
    val state = _state.asStateFlow()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                context.userDataStore.data.collect { values ->
                    val user = values[session]?.let { encrypted ->
                        // 重装或密钥失效后重新登录，不将不可解密的凭证作为有效会话。
                        try {
                            json.decodeFromString<LoginResponse>(cipher.decrypt(encrypted))
                        } catch (_: GeneralSecurityException) {
                            null
                        } catch (_: IllegalArgumentException) {
                            null
                        }
                    }
                    _state.value = SessionState(
                        loaded = true,
                        config = ServerConfig(values[host].orEmpty(), values[port] ?: "8080"),
                        lastUsername = values[username].orEmpty(),
                        user = user
                    )
                }
            } catch (_: IOException) {
                _state.value = SessionState(
                    loaded = true,
                    errorMessage = "无法读取本地配置，请重新保存服务器地址"
                )
            }
        }
    }

    suspend fun saveConfig(config: ServerConfig) {
        context.userDataStore.edit { values ->
            val previousConfig = ServerConfig(values[host].orEmpty(), values[port] ?: "8080")
            if (config != previousConfig) values.remove(session)
            values[host] = config.ipAddress
            values[port] = config.port
        }
        _state.update { current ->
            current.copy(config = config, user = if (config == current.config) current.user else null)
        }
    }

    suspend fun saveSession(user: LoginResponse, account: String, config: ServerConfig) {
        val encrypted = cipher.encrypt(json.encodeToString(user))
        context.userDataStore.edit { values ->
            // 原登录响应只能写入发起请求时的服务器，配置切换和会话写入在同一事务中校验。
            if (ServerConfig(values[host].orEmpty(), values[port] ?: "8080") != config) {
                throw AppException("服务器配置已变化，请重新登录", "SESSION_CHANGED")
            }
            values[session] = encrypted
            values[username] = account
        }
        _state.update { current ->
            if (current.config == config) current.copy(
                user = user,
                lastUsername = account
            ) else current
        }
        if (_state.value.config != config) throw AppException(
            "服务器配置已变化，请重新登录",
            "SESSION_CHANGED"
        )
    }

    suspend fun clearSession() {
        context.userDataStore.edit { it.remove(session) }
        _state.update { it.copy(user = null) }
    }
}
