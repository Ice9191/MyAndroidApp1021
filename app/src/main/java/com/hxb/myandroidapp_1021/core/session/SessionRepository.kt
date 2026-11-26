package com.hxb.myandroidapp_1021.core.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock
import kotlin.time.Instant

@Singleton
internal class SessionRepository @Inject constructor(
    private val store: SessionStore,
    private val tokenCipher: TokenCipher
) {
    enum class Status { RESTORING, SIGNED_IN, SIGNED_OUT }

    class ActiveSession(
        val baseUrl: HttpUrl,
        val token: String,
        val expiresAt: Instant,
        internal val encryptedToken: String
    )

    class RestoredSession(val baseUrl: HttpUrl?, val session: ActiveSession?)

    private val mutex = Mutex()
    private val _status = MutableStateFlow(Status.RESTORING)
    val status = _status.asStateFlow()

    /**
     * 冷启动先确定有效服务器地址，再恢复属于该地址且尚未过期的会话。
     */
    suspend fun restore(fixedBaseUrl: HttpUrl? = null): RestoredSession = mutex.withLock {
        val saved = store.read()
        val baseUrl = fixedBaseUrl ?: saved.configuredBaseUrl?.toHttpUrlOrNull()
        val session = if (baseUrl != null && saved.session != null) {
            restoreSession(baseUrl, saved.session)
        } else {
            null
        }
        if (saved.session != null && session == null) {
            store.update { it.copy(session = null) }
        }
        _status.value = if (session == null) Status.SIGNED_OUT else Status.SIGNED_IN
        RestoredSession(baseUrl, session)
    }

    suspend fun saveConfiguredBaseUrl(baseUrl: HttpUrl) = mutex.withLock {
        val address = baseUrl.toString()
        val saved = store.update { current ->
            current.copy(
                configuredBaseUrl = address,
                session = current.session?.takeIf { it.baseUrl == address }
            )
        }
        if (saved.session == null) _status.value = Status.SIGNED_OUT
    }

    /**
     * 会话只写入发起登录时使用的服务器，换址后返回的旧登录结果需要作废。
     */
    suspend fun saveSession(
        baseUrl: HttpUrl,
        token: String,
        expiresAt: String,
        fixedBaseUrl: HttpUrl? = null
    ): ActiveSession = mutex.withLock {
        require(token.isNotBlank()) { "Session token is blank" }
        val expiry = Instant.parse(expiresAt)
        require(expiry > Clock.System.now()) { "Session has expired" }
        val currentBaseUrl = fixedBaseUrl ?: store.read().configuredBaseUrl?.toHttpUrlOrNull()
        check(baseUrl == currentBaseUrl) { "Session server address has changed" }

        val encryptedToken = withContext(Dispatchers.IO) {
            tokenCipher.encrypt(token, sessionMetadata(baseUrl, expiry))
        }
        store.update {
            it.copy(
                session = SessionStore.StoredSession(
                    baseUrl.toString(),
                    encryptedToken,
                    expiry.toString()
                )
            )
        }
        _status.value = Status.SIGNED_IN
        ActiveSession(baseUrl, token, expiry, encryptedToken)
    }

    /**
     * 传入请求发起时的会话，避免迟到的 401 清除后续登录保存的新会话。
     */
    suspend fun clearSession(expectedSession: ActiveSession? = null) = mutex.withLock {
        val saved = store.update { current ->
            if (expectedSession == null || current.session?.encryptedToken == expectedSession.encryptedToken) {
                current.copy(session = null)
            } else {
                current
            }
        }
        if (saved.session == null) _status.value = Status.SIGNED_OUT
    }

    private suspend fun restoreSession(
        baseUrl: HttpUrl,
        saved: SessionStore.StoredSession
    ): ActiveSession? {
        if (saved.baseUrl != baseUrl.toString()) return null
        val expiry = Instant.parseOrNull(saved.expiresAt) ?: return null
        if (expiry <= Clock.System.now()) return null

        val token = withContext(Dispatchers.IO) {
            tokenCipher.decrypt(saved.encryptedToken, sessionMetadata(baseUrl, expiry))
        }
        if (token.isNullOrBlank() || expiry <= Clock.System.now()) return null
        return ActiveSession(baseUrl, token, expiry, saved.encryptedToken)
    }

    // 将服务器地址和到期时间绑定到密文，恢复时同时校验这两项元数据。
    private fun sessionMetadata(baseUrl: HttpUrl, expiresAt: Instant): ByteArray =
        "$baseUrl\n$expiresAt".encodeToByteArray()
}
