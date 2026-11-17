package com.hxb.myandroidapp_1021.domain.session

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(val ipAddress: String = "", val port: String = "8080")

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(
    val userId: String,
    val userName: String,
    val userRole: String,
    val token: String,
    val expiresAt: String,
    val warehouseCode: String,
    val warehouseName: String,
    val permissions: Set<String>,
    val avatarUrl: String = ""
)

data class SessionState(
    val loaded: Boolean = false,
    val config: ServerConfig = ServerConfig(),
    val lastUsername: String = "",
    val user: LoginResponse? = null,
    val errorMessage: String? = null
)
