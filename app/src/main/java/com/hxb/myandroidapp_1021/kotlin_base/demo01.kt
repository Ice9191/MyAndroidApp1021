package com.hxb.myandroidapp_1021.kotlin_base

fun main() {
    demo03()
}

/**
 * Map
 */
fun demo03(){
    // 只读Map
    val scores = mapOf<String, Int>("Alice" to 90, "Bob" to 85, "Charlie" to 78)
    println(scores.keys)
    println(scores.values)
    println(scores["Alice"])
    // 可变Map
    val scoresMutable = mutableMapOf<String, Int>("Alice" to 90, "Bob" to 85, "Charlie" to 78)
    scoresMutable["David"] = 92
    println(scoresMutable)
}

/**
 * 集合
 */
fun demo02() {
    // 只读列表
    val subject = listOf<String>("Math", "Chinese", "English")
    println(subject[0])
    println(subject.size)
    println(subject.last())
    // 可变列表
    val cartList = mutableListOf<String>("Apple", "Banana", "Cherry")
    cartList.add("Date")
    cartList.remove("Banana")
    cartList[1] = "Grape"
    println(cartList)
}

/**
 * 常量和变量
 */
fun demo01() {
    // val：只读变量
    val name = "张三"
    // name = "李四"
    // var：可变变量
    var age = 20
    age = 18
}