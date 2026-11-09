package com.hxb.myandroidapp_1021.kotlin_base

fun main(){
    // 对象1
    val p1 = Person()
    println("p1: ${p1.name}, ${p1.age}")

    // 对象2
    val p2 = Person()
    println("p2: ${p2.name}, ${p2.age}")
}

class Person{
    var name: String = ""
    var age: Int = 0

    fun intro(){
        println("My name is $name and I am $age years old.")
    }
}