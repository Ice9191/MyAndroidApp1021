package com.hxb.myandroidapp_1021.kotlin_base

fun main() {
    val man = Man("John", 30, "football")
    man.intro()
    man.work("developer")
    man.play()
}

// 定义一个接口
interface IPerson {
    // 抽象属性
    val name: String
    val age: Int

    // 抽象方法
    fun intro()
    fun work(job: String)

    // 默认实现(非正常情况)
    fun play() {
        println("$name is playing")
    }
}

interface IPlay {
    val hobby: String
    fun play()
}

// 类继承并实现了接口
class Man(override val name: String, override val age: Int, override val hobby: String) : IPerson, IPlay {
    override fun intro() {
        println("$name is introducing, I am $age years old")
    }

    override fun work(job: String) {
        println("$name is working as a $job")
    }

    override fun play() {
        println("$name is playing $hobby")
    }
}