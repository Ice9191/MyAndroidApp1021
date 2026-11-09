package com.hxb.myandroidapp_1021.kotlin_base

/**
 * let函数：作用域函数，它允许你在一个对象上执行代码块，并提供一种安全处理可空对象的方式。其主要用途：空安全处理、对象转换和临时作用域创建
 *  接收对象：通过it引用上下文对象
 *  返回值：返回Lambda表达式的最后一行结果
 *  空安全：常与安全调用符?.结合使用
 *
 * with函数：对象上下文操作，常用于对象初始化和操作
 *  在with的代码块内，你可以直接使用对象的所有属性和方法，就像在对象内部操作一样
 */
fun main(){
    letFun(name = "张三")

    val myCar = Car()
    println("准备开车回家")
    myCar.startEngine()
    myCar.turnOnAC()
    myCar.playMusic()
    myCar.setNavigation("家")
    // 连续加速
    repeat(3){
        myCar.accelerate()
    }
    println("安全到家")

    println("---------------------")

    // 使用with函数
    with(myCar){
        startEngine()
        turnOnAC()
        playMusic()
        setNavigation("家")
        repeat(3){
            accelerate()
        }
    }
}

fun letFun(name: String? = null){
    name.toString()
    name?.isEmail()
    name.equals("ccc")

    // 使用以下方式来写，更保险，算是语法糖
    name?.let {
        // it代替了name
        // 只有name不为空才会执行代码块中的内容
        println(it)
        println(it.isEmail())
        println(it == "ccc")
    }
}

class Car {
    // 定义汽车的内部状态
    var isEngineStarted: Boolean = false
    var isACOn: Boolean = false
    var isMusicPlaying: Boolean = false
    var destination: String? = null
    var speed: Int = 0

    // 1. 启动引擎
    fun startEngine() {
        isEngineStarted = true
        println("🚗 引擎已启动")
    }

    // 2. 打开空调
    fun turnOnAC() {
        isACOn = true
        println("❄️ 空调已打开")
    }

    // 3. 播放音乐
    fun playMusic() {
        isMusicPlaying = true
        println("🎵 音乐开始播放")
    }

    // 4. 设置导航目的地
    fun setNavigation(place: String) {
        destination = place
        println("🗺️ 导航目的地已设置为: $place")
    }

    fun accelerate(){
        speed += 10
        println("🚗 当前速度为: $speed km/h")
    }
}