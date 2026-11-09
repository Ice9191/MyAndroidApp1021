package com.hxb.myandroidapp_1021.kotlin_base

class AppConfig {
    // 伴生对象：相当于Java中的静态成员
    companion object {
        // const：常量，编译期确定的值，必须是固定的值
        // val：不可变的变量，运行时才确实，可以是确定的值，也可以是一个表达式
        const val BASE_URL = "baidu.com"
        const val PAGE_SIZE = 10
        const val TIME_OUT = 3000
    }
}

fun main(){
    println(AppConfig.BASE_URL)
}