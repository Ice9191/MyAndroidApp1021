package com.hxb.myandroidapp_1021.kotlin_base

/**
 * 枚举解决了什么问题？
 *  1. 接口需要的某个属性的值是几个不规律的数字
 *      全部(10) 待付款(25) 待收货(2)
 */
enum class SortType(val value: Int) {
    Default(0),
    All(10),
    WaitForPay(25),
    WaitForReceive(2)
}

fun main() {
    val sort = SortType.All
    println(sort)
    println(sort.name)
    println(sort.value)
    sortFun(sort)
}

fun sortFun(sort: SortType){
    // 配合when使用
    when (sort) {
        SortType.Default -> println("默认排序")
        SortType.All -> println("全部")
        SortType.WaitForPay -> println("待付款")
        SortType.WaitForReceive -> println("待收货")
    }
}