package com.hxb.myandroidapp_1021.kotlin_base

fun main() {
    val animal = Animal("Dog")
    animal.eat()

    val dog = Dog("Buddy")
    // 没有重写就调用父类的方法，有重写就调用子类的方法
    dog.eat()

    val cat = Cat("Whiskers")
    cat.eat()
}

// kotlin默认所有类不可继承，方法也不能被重写，除非类和方法使用open修饰符
open class Animal(var name: String) {
    open fun eat() {
        println("$name is eating")
    }
}

class Dog(name: String) : Animal(name) {
    // 重写父类的方法
    override fun eat() {
        // 一般为调用父类的方法
        super.eat()
        println("$name is eating bones")
    }
}

class Cat(name: String) : Animal(name) {
    override fun eat() {
        super.eat()
        println("$name is eating mice")
    }
}