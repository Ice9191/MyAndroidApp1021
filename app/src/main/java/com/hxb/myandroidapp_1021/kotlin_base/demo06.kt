package com.hxb.myandroidapp_1021.kotlin_base

fun main(){
    // 通过主构造函数创建对象
    val vehicle1 = Vehicle("car")
    vehicle1.intro()

    // 通过次构造函数创建对象
    val vehicle2 = Vehicle("bicycle", 3)
    vehicle2.intro()
}

class Vehicle(var type: String){
    var wheels: Int = 0

    init {
        wheels = when(type){
            "car" -> 4
            "bicycle" -> 2
            "truck" -> 6
            else -> 0
        }
    }

    // 次构造函数
    constructor(type: String, wheels: Int):this(type){
        this.wheels = wheels
    }

    fun intro(){
        println("This vehicle is a $type and it has $wheels wheels.")
    }
}