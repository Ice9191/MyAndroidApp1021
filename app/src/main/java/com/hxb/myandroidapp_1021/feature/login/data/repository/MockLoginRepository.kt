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
     * 演示账号验证成功后等待两秒返回模拟人员信息，其他账号密码直接拒绝。
     */
    override suspend fun login(username: String, password: String): LoginUser? {
        if (username != mockUsername || password != mockPassword) return null
        delay(2000.milliseconds)
        return mockUser
    }

    // 模拟登录不保存凭证，退出时由现有页面流程清理展示状态。
    override fun logout() = Unit
}
