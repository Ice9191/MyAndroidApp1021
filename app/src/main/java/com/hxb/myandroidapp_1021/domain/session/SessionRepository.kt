package com.hxb.myandroidapp_1021.domain.session

import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val state: StateFlow<SessionState>
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun logout()
    suspend fun saveConfig(config: ServerConfig)
    suspend fun testConnection(config: ServerConfig)
}
