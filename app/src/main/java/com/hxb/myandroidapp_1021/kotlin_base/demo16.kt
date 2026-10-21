package com.hxb.myandroidapp_1021.kotlin_base

/**
 * run函数：作用域内计算，常用于对象配置+计算结果，替代构造器
 *  run可以创建一个临时工作区，可以执行多个操作，最后返回一个结果
 *
 * apply函数：作用域内计算，专门用来配置对象，最后返回一个对象
 *  1. 拿到一个新对象
 *  2. 在它的身体里做各种设置
 *  3. 最后自动返回配置好的对象
 */
fun main() {
    runDemo()

    // 使用 apply 配置手机
    val myPhone = Phone().apply {
        // 设置主人名字
        owner = "小明"
        // 更换壁纸
        wallpaper = "星空壁纸"
        // 更换铃声
        ringtone = "欢乐颂"
        // 安装应用
        apps.add("微信")
        apps.add("支付宝")
        apps.add("抖音")

        // 注意：这里不需要 return，apply 会自动返回配置好的对象
    }
    myPhone.showSettings()

    // 再配置一个手机
    val momsPhone = Phone().apply {
        owner = "妈妈"
        wallpaper = "家庭照片"
        ringtone = "经典铃声"
        apps.add("微信")
        apps.add("淘宝")
    }
    momsPhone.showSettings()
}

fun runDemo() {
    // 简化多步计算
    val area = run {
        val width = 5
        val height = 10
        width * height
    }
    println("Area: $area")

    // 临时变量隔离
    val result = run {
        val x = 5
        val y = 10
        x + y
    }
    println("Result: $result")

    // 安全转换
    val str: String = "Hello, World!"
    val length = str.run {
        length
    }
    println("Length: $length")

    // 对象初始化
    val person = run {
        val name = "张三"
        val age = 18

        // 创建描述字符串
        "$name $age"
    }
    println(person)
}

// 创建一个手机对象
class Phone {
    var owner = ""
    var wallpaper = "默认壁纸"
    var ringtone = "默认铃声"
    var apps = mutableListOf<String>()

    fun showSettings() {
        println("\n手机设置:")
        println("主人: $owner")
        println("壁纸: $wallpaper")
        println("铃声: $ringtone")
        println("安装的应用: ${apps.joinToString()}")
    }
}