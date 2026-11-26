package com.hxb.myandroidapp_1021.core.network

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.hxb.myandroidapp_1021.core.session.SessionRepository
import com.hxb.myandroidapp_1021.core.session.SessionRepository.ActiveSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Invocation
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Path
import retrofit2.http.Url
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Call as RetrofitCall

internal val BASE_URL: String? = null

private const val BUSINESS_SUCCESS_CODE = 200

@Singleton
class HttpClient @Inject
internal constructor(
    @ApplicationContext context: Context,
    private val sessionRepository: SessionRepository
) {
    @Serializable
    data class ApiResponse<T>(
        val success: Boolean,
        val code: Int,
        val message: String?,
        val data: T?
    )

    @Serializable
    private data class ErrorBody(val message: String? = null)

    private inner class Server(val baseUrl: HttpUrl) {
        private val loginPath = "${baseUrl.encodedPath}login"

        val retrofit: Retrofit = createRetrofit(baseUrl) { request ->
            okHttpClient.newCall(request.newBuilder().tag(Server::class.java, this).build())
        }

        fun requiresToken(url: HttpUrl): Boolean =
            url.encodedPath != loginPath && url.scheme == baseUrl.scheme &&
                    url.host == baseUrl.host && url.port == baseUrl.port
    }

    private class Session(
        val server: Server? = null,
        val credentials: ActiveSession? = null,
        val isRestoring: Boolean = false,
        val loginRequiredMessage: String? = null
    ) {
        val token: String? get() = credentials?.token
    }

    internal data class LoginState(
        val isRestoring: Boolean = true,
        val isSignedIn: Boolean = false,
        val message: String? = null
    )

    private class AuthenticationRequiredException :
        IOException(NetworkError.UNAUTHORIZED.userMessage)

    private class ServerChangedException : IOException(NetworkError.SERVER_CHANGED.userMessage)

    private val json = Json { ignoreUnknownKeys = true }
    private val diagnosticsEnabled =
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    private val fixedBaseUrl = BASE_URL?.toHttpUrl()
    private val sessionChanges = Mutex()
    private val session = MutableStateFlow(
        Session(server = fixedBaseUrl?.let { Server(it) }, isRestoring = true)
    )

    // 地址和会话一起替换，进行中的请求仍绑定发起时的服务器。
    internal var baseUrl: HttpUrl?
        get() = session.value.server?.baseUrl
        private set(value) {
            session.update { currentSession ->
                if (currentSession.server?.baseUrl == value) return@update currentSession
                Session(
                    server = value?.let { Server(it) },
                    loginRequiredMessage = if (currentSession.token != null) {
                        NetworkError.SERVER_CHANGED.userMessage
                    } else {
                        currentSession.loginRequiredMessage
                    }
                )
            }
        }

    internal val loginState = session.map {
        LoginState(it.isRestoring, it.credentials != null, it.loginRequiredMessage)
    }.distinctUntilChanged()

    private val okHttpClient = OkHttpClient.Builder()
        .callTimeout(100, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            val server = request.tag(Server::class.java)
            val currentSession = session.value
            if (server !== currentSession.server) throw ServerChangedException()
            if (server?.requiresToken(request.url) != true) {
                return@addInterceptor chain.proceed(request)
            }

            if (currentSession.token == null) {
                requireLogin(currentSession)
                throw AuthenticationRequiredException()
            }

            // 一次请求的重试和重定向沿用发起时的会话。
            chain.proceed(request.newBuilder().tag(Session::class.java, currentSession).build())
        }
        .addNetworkInterceptor { chain ->
            val request = chain.request()
            val server = request.tag(Server::class.java)
            val activeSession = session.value
            if (server !== activeSession.server) throw ServerChangedException()

            val currentSession = request.tag(Session::class.java)
            if (currentSession == null || server?.requiresToken(request.url) != true) {
                return@addNetworkInterceptor chain.proceed(
                    request.newBuilder().removeHeader("Authorization").build()
                )
            }
            if (currentSession !== activeSession) throw AuthenticationRequiredException()

            chain.proceed(
                request.newBuilder().header("Authorization", "Bearer ${currentSession.token}")
                    .build()
            )
        }
        .build()

    internal fun createRetrofit(baseUrl: HttpUrl, callFactory: Call.Factory): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .callFactory(callFactory)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    internal val retrofit: Retrofit
        get() = session.value.server?.retrofit
            ?: throw NetworkException(NetworkError.SERVER_NOT_CONFIGURED)

    internal suspend fun restoreSession() = sessionChanges.withLock {
        val current = session.value
        if (!current.isRestoring) return@withLock
        session.value = try {
            val restored = accessSessionStore { sessionRepository.restore(fixedBaseUrl) }
            Session(
                server = current.server ?: restored.baseUrl?.let { Server(it) },
                credentials = restored.session
            )
        } catch (error: NetworkException) {
            Session(server = current.server, loginRequiredMessage = error.userMessage)
        }
    }

    internal suspend fun saveConfiguredBaseUrl(baseUrl: HttpUrl) = sessionChanges.withLock {
        if (fixedBaseUrl != null) return@withLock
        withContext(NonCancellable) {
            accessSessionStore { sessionRepository.saveConfiguredBaseUrl(baseUrl) }
            this@HttpClient.baseUrl = baseUrl
        }
    }

    internal suspend fun saveSession(retrofit: Retrofit, token: String, expiresAt: String) =
        sessionChanges.withLock {
            val server = session.value.server
            if (server?.retrofit !== retrofit) throw NetworkException(NetworkError.SERVER_CHANGED)
            withContext(NonCancellable) {
                val credentials = accessSessionStore {
                    sessionRepository.saveSession(server.baseUrl, token, expiresAt, fixedBaseUrl)
                }
                session.value = Session(server = server, credentials = credentials)
            }
        }

    internal suspend fun clearToken() = sessionChanges.withLock {
        withContext(NonCancellable) {
            accessSessionStore { sessionRepository.clearSession() }
            session.value = Session(server = session.value.server)
        }
    }

    private suspend fun invalidateSession(requestSession: Session, message: String) =
        sessionChanges.withLock {
            if (requestSession !== session.value) return@withLock
            val credentials = requestSession.credentials ?: return@withLock
            withContext(NonCancellable) {
                try {
                    accessSessionStore { sessionRepository.clearSession(credentials) }
                } catch (_: NetworkException) {
                    // 服务端已拒绝凭证，本地写入失败时仍结束内存会话。
                }
                requireLogin(requestSession, message)
            }
        }

    private suspend fun <T> accessSessionStore(action: suspend () -> T): T {
        return try {
            action()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.w(
                "HttpClient",
                "result=SESSION_STORAGE_FAILURE cause=${error.javaClass.simpleName}"
            )
            throw NetworkException(NetworkError.SESSION_STORAGE_FAILURE, cause = error)
        }
    }

    // 同一会话只触发一次跳转，迟到的响应不能让新登录的会话失效。
    private fun requireLogin(
        currentSession: Session,
        message: String = NetworkError.UNAUTHORIZED.userMessage
    ) {
        if (currentSession.loginRequiredMessage == null) {
            session.compareAndSet(
                currentSession,
                Session(server = currentSession.server, loginRequiredMessage = message)
            )
        }
    }

    private fun readErrorMessage(body: ResponseBody?): String? {
        if (body == null) return null
        return try {
            json.decodeFromString<ErrorBody>(body.string()).message
        } catch (_: SerializationException) {
            null
        } catch (_: IOException) {
            null
        }
    }

    private suspend fun <T> awaitNetworkResponse(
        call: RetrofitCall<ApiResponse<T>>
    ): Response<ApiResponse<T>> =
        try {
            call.awaitResponse()
        } catch (error: SerializationException) {
            throw NetworkException(NetworkError.INVALID_RESPONSE, cause = error)
        } catch (error: AuthenticationRequiredException) {
            throw NetworkException(NetworkError.UNAUTHORIZED, cause = error)
        } catch (error: ServerChangedException) {
            throw NetworkException(NetworkError.SERVER_CHANGED, cause = error)
        } catch (error: InterruptedIOException) {
            throw NetworkException(NetworkError.TIMEOUT, cause = error)
        } catch (error: IOException) {
            throw NetworkException(NetworkError.TRANSPORT, cause = error)
        }

    /**
     * HTTP、业务和接口数据校验结束后记录同一条诊断日志。
     */
    internal suspend fun <T> request(
        call: RetrofitCall<ApiResponse<T>>,
        expectedStatus: Int? = null,
        validateData: (T) -> Unit = {}
    ): T {
        val startTime = System.nanoTime()
        var response: Response<ApiResponse<T>>? = null
        val result = runCatching {
            val received = awaitNetworkResponse(call)
            response = received
            validateResponse(received, expectedStatus).also(validateData)
        }
        if (diagnosticsEnabled) {
            val elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            logRequest(call, response, result.exceptionOrNull(), elapsedMillis)
        }
        return result.getOrThrow()
    }

    private suspend fun <T> validateResponse(
        response: Response<ApiResponse<T>>,
        expectedStatus: Int?
    ): T {
        val rawRequest = response.raw().request
        val server = rawRequest.tag(Server::class.java)
        if (server != null && server !== session.value.server) {
            response.errorBody()?.close()
            throw NetworkException(NetworkError.SERVER_CHANGED)
        }
        val status = response.code()
        if (!response.isSuccessful || (expectedStatus != null && status != expectedStatus)) {
            val error = NetworkException.Http(status, readErrorMessage(response.errorBody()))
            if (status == 401 && server?.requiresToken(rawRequest.url) == true) {
                rawRequest.tag(Session::class.java)
                    ?.let { invalidateSession(it, error.userMessage) }
            }
            throw error
        }
        val res = response.body() ?: throw NetworkException(NetworkError.INVALID_RESPONSE)
        if (!res.success || res.code != BUSINESS_SUCCESS_CODE) {
            throw NetworkException.Business(res.code, res.message)
        }
        return res.data ?: throw NetworkException(NetworkError.INVALID_RESPONSE)
    }

    private fun <T> logRequest(
        call: RetrofitCall<ApiResponse<T>>,
        response: Response<ApiResponse<T>>?,
        error: Throwable?,
        elapsedMillis: Long
    ) {
        val originalRequest = runCatching { call.request() }.getOrNull()
        val request = response?.raw()?.request ?: originalRequest
        val apiMethod = originalRequest?.tag(Invocation::class.java)?.method()
        val api = apiMethod?.let { "${it.declaringClass.simpleName}.${it.name}" } ?: "-"
        val hasDynamicPath = apiMethod?.parameterAnnotations?.any { annotations ->
            annotations.any { it is Path || it is Url }
        } ?: true
        // 只显示可确认的静态路径，省略动态路径、查询参数和重定向后的新路径。
        val includePath = !hasDynamicPath &&
                request?.url?.encodedPath == originalRequest.url.encodedPath
        val target = request?.url?.let { url ->
            if (includePath) {
                url.newBuilder().username("").password("").query(null).fragment(null).build()
                    .toString()
            } else {
                url.redact()
            }
        } ?: "-"
        val outcome = when (error) {
            null -> "SUCCESS"
            is CancellationException -> "CANCELED"
            is NetworkException.Http -> "HTTP_ERROR"
            is NetworkException -> if (error.cause is SerializationException) "DECODE_ERROR" else error.type.name
            else -> error.javaClass.simpleName
        }
        val status = response?.code() ?: "-"
        val businessCode = response?.body()?.code ?: "-"
        Log.d(
            "HttpClient",
            "api=$api method=${request?.method ?: "-"} target=$target result=$outcome " +
                    "http=$status business=$businessCode elapsedMs=$elapsedMillis"
        )
    }

    internal enum class NetworkError(val userMessage: String) {
        SERVER_NOT_CONFIGURED("请先配置服务器"),
        SERVER_CHANGED("服务器已切换，请重新登录"),
        SESSION_STORAGE_FAILURE("无法读写本地登录会话，请重试"),
        BAD_REQUEST("请求参数有误，请检查后重试"),
        UNAUTHORIZED("登录状态失效，请重新登录"),
        FORBIDDEN("当前账号没有执行此操作的权限"),
        NOT_FOUND("未找到请求的资源"),
        SERVER_ERROR("服务器暂时无法处理请求，请稍后重试"),
        OTHER_HTTP_ERROR("请求失败，请稍后重试"),
        BUSINESS("业务请求失败"),
        TIMEOUT("请求超时，请稍后重试"),
        TRANSPORT("网络连接异常，请检查网络后重试"),
        INVALID_RESPONSE("服务器响应无效，请稍后重试");

        companion object {
            fun fromStatusCode(statusCode: Int): NetworkError = when (statusCode) {
                400 -> BAD_REQUEST
                401 -> UNAUTHORIZED
                403 -> FORBIDDEN
                404 -> NOT_FOUND
                in 500..599 -> SERVER_ERROR
                else -> OTHER_HTTP_ERROR
            }
        }
    }

    internal open class NetworkException(
        val type: NetworkError,
        serverMessage: String? = null,
        cause: Throwable? = null
    ) : Exception(serverMessage?.takeUnless(String::isBlank) ?: type.userMessage, cause) {
        val userMessage: String
            get() = message ?: type.userMessage

        class Http(statusCode: Int, serverMessage: String?) :
            NetworkException(NetworkError.fromStatusCode(statusCode), serverMessage)

        class Business(val code: Int, serverMessage: String?) :
            NetworkException(NetworkError.BUSINESS, serverMessage)
    }
}
