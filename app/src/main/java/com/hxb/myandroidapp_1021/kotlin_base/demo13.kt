package com.hxb.myandroidapp_1021.kotlin_base

fun main(){
    fn1()
}

/**
 * 在String类中，添加了isEmail()方法
 * 扩展函数让你不修改原类就能给它添加新方法
 */
fun String.isEmail(): Boolean{
    // this代表这个类实例化出来的对象
    return this.contains("@") && this.contains(".")
}

fun Int.toPx(): Int = (this * 2.5).toInt()

fun String.upperCaseFirst(): String{
    // 这里的isEmpty()简化了，实际上是this.isEmpty()
    if(isEmpty()) return this
    return this[0].uppercaseChar() + this.substring(1)
}

// 给系统类扩充属性
val String.isLong: Boolean
    get() = this.length > 10

fun fn1(){
    val str = "张三"
    // str.toString()
    val myEmail = "abc@126.com"
    println(myEmail.isEmail())
    println(str.isEmail())

    val height = 100 // 转dp
    println(height.toPx())

    val str1 = "jack"
    println(str1.upperCaseFirst())

    val str2 = "abcdefghijk"
    println(str1.isLong) // false
    println(str2.isLong) // true
}