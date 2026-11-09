package com.hxb.myandroidapp_1021.kotlin_base

// 为什么是小括号，而不是大括号？
// 首先它是一个类，data class这个类里面应该是属性，这个小括号就充当于主构造函数
// 以后给数据对象定类型的时候，都可以抽象成data class
data class Student1(
    val id: Int,            // 学号(不可变)
    var name: String,       // 姓名(可变)
    val className: String,  // 班级(不可变)
    var score: Int = 60     // 分数(可变，有默认值60)
)

fun main(){
    val stu1 = Student1(1, "张三", "101班", 88)
    val stu2 = Student1(2, "李四", "102班", 77)
    val stu3 = Student1(2, "李四", "102班", 77)

    // 内部自动实现了toString()方法
    println(stu1)
    println(stu2)

    // 自动实现了equals()方法，比的是内容
    println(stu2 == stu3) // true

    // copy，拷贝并修改
    val stu4 = stu1.copy(id = 4, name = "王五", className = "103班", score = 99)
    println(stu4)

    // 解构
    val (id, name, className, score) = stu2
    println("学号：$id，姓名：$name，班级：$className，分数：$score")

    // hashCode
    println(stu1.hashCode())
    println(stu2.hashCode())
    println(stu3.hashCode())
}