package com.hxb.myandroidapp_1021.kotlin_base

fun main(){
    val s1 = Student("Alice", 20)
    s1.intro()

    val s2 = Son("Bob", 15)
    s2.intro()
}

// 显式主构造函数
class Student constructor(var name: String, var age: Int){
    fun intro(){
        println("My name is $name and I am $age years old.")
    }
}

// 隐式主构造函数(常用写法)
class Son (var name: String, var age: Int){
    fun intro(){
        println("My name is $name and I am $age years old.")
    }
}