package com.hxb.myandroidapp_1021.routes

/**
 * 定义路由常量，可以理解为URL的路径
 * 创建 NavController + NavHost
 *  NavController 是导航的核心控制器，NavHost 是页面容器
 */
// 单例
object Routes{
    // 主tab页面
    const val HOME = "home"
    const val PROJECT = "project"
    const val MINE = "mine"
    // 登录页面
    const val LOGIN = "login"
    // 跳转到二级页面需要传递参数
    const val QUESTION = "question/{itemId}"
    // 如果地址中需要携带参数，安卓中更推荐使用函数的形式
    fun question(itemId: String): String = "question/$itemId"
}