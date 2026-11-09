package com.hxb.myandroidapp_1021.kotlin_base

/**
 * 单例：object开头
 * 特点：全局(App)共享一个实例
 */
// 单例类
object HttpClient{
    var token: String = ""
    fun isLoggedIn(): Boolean = token.isNotEmpty()
}

fun main(){
    // 单例类不需要实例化，直接调用
    val res = HttpClient.isLoggedIn()
    println(if(res) "Logged in" else "Not logged in")

    HttpClient.token = "xxx"
    val res1 = HttpClient.isLoggedIn()
    println(if(res1) "Logged in" else "Not logged in")
}