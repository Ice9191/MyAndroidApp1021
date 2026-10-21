package com.hxb.myandroidapp_1021.kotlin_base

fun main() {
    val p1 = Person1("Alice", 20)
    p1.intro()

    val p2 = Person2("Bob")
    p2.intro()

    val p3 = Person2("Charlie", 25)
    p3.intro1()

    val p4 = Person2("David", 30, "New York")
    p4.intro2()

    val p5 = Person2("Eve", 9, "Los Angeles")
    p5.intro2()

    val p6 = Person2("Tom", 35, "Chicago")
    p6.intro2()
}

// 次构造函数(有主构造函数)
// 主构造函数不支持age
// 初始化顺序：主构造 -> init代码块 -> 次构造
class Person2(var name: String) {
    var age: Int = 0
    var city: String = ""

    // init代码块：在创建对象之前，进行验证，符合要求才创建对象，服务于主构造
    init {
        println("Init block")
        require(name.isNotEmpty(), { "Name cannot be empty" })
        // 次构造的校验不了
        // require(age > 18, { "Age must be greater than 18" })
    }

    // 次构造函数1
    // 若类有主构造函数，所有次构造函数必须直接或间接委托给主构造函数(使用this())
    constructor(name: String, age: Int) : this(name) {
        this.age = age
    }

    // 次构造函数2
    constructor(name: String, age: Int, city: String) : this(name) {
        this.age = age
        this.city = city
    }

    fun intro() {
        println("My name is $name.")
    }
    fun intro1() {
        println("My name is $name and I am $age years old.")
    }
    fun intro2() {
        println("My name is $name and I am $age years old from $city.")
    }
}

// 次构造函数(无主构造函数)
class Person1 {
    var name: String = ""
    var age: Int = 0

    // 次构造函数
    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }

    fun intro() {
        println("My name is $name and I am $age years old.")
    }
}
