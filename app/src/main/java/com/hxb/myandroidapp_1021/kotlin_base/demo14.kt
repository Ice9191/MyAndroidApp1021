package com.hxb.myandroidapp_1021.kotlin_base

/**
 * Lambda表达式就是一段可以当作参数传递的代码块
 * 接收函数作为参数的函数叫做高阶函数
 */
fun main(){
    // 调用时，传入Lambda表达式
    calc(10, 5, {a, b -> a + b})
    calc(10, 5, {a, b -> a * b})
    calc(10, 5, {a, b -> a - b})
    calc(10, 5, {a, b -> a / b})
    // 简化
    calc(10, 5){a, b -> a + b}
}

fun calc(num1: Int, num2: Int, operation:(Int, Int)-> Int): Int {
    return operation(num1, num2)
}