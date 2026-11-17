package com.hxb.myandroidapp_1021.feature.login.data.repository

import com.hxb.myandroidapp_1021.feature.login.domain.model.LoginUser
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MockLoginRepository @Inject constructor() : LoginRepository {
    private val mockUsername = "admin"
    private val mockPassword = "123456"
    private val mockUser = LoginUser(userName = "admin", userRole = "材料员")

    /**
     * 沿用演示账号规则：错误账号立即拒绝，正确账号延迟两秒返回模拟人员信息。
     */
    override suspend fun login(username: String, password: String): LoginUser? {
        if (username != mockUsername || password != mockPassword) return null
        delay(2000.milliseconds)
        return mockUser
    }
}
