package com.hxb.myandroidapp_1021.kotlin_base

data class QuestionItem(
    val id: String,
    val stem: String,
    val difficulty: Int,
    val views: Int,
    val likeCount: Int
)

fun main(){
    val questions = listOf<QuestionItem>(
        QuestionItem("1", "What is the capital of France?", 1, 100, 10),
        QuestionItem("2", "What is the capital of Germany?", 2, 200, 20),
        QuestionItem("3", "意大利的首都是？", 3, 300, 30),
        QuestionItem("4", "What is the capital of Spain?", 4, 400, 40)
    )

    // filter：过滤
    // val easy = questions.filter { item -> item.difficulty <= 2 }
    // 改进，使用it代替item，使用Lambda表达式
    val easy = questions.filter { it.difficulty <= 2 }
    println(easy)

    // map：映射出一个新的列表
    val titles = questions.map { it.stem }
    println(titles)

    // sort：排序
    val byViews = questions.sortedBy { it.views }
    println(byViews)

    // sort：降序排序
    val byViewsDesc = questions.sortedByDescending { it.views }
    println(byViewsDesc)

    println("-----------------------------------------------")

    // 遍历
    questions.forEach { println("题目:${it.stem}，难度:${it.difficulty}") }

    // 查找：查找第一个满足条件的元素
    val res = questions.find { it.views > 100 }
    println(res)

    // 查找：查找最后一个满足条件的元素
    val resLast = questions.findLast { it.views > 100 }
    println(resLast)

    println("------------------------------------------------")

    // firstOrNull：查找第一个满足条件的元素，如果没有则返回null
    val hardest = questions.maxByOrNull { it.difficulty }
    println(hardest)

    // any all none ：判断是否满足条件
    val hasHard = questions.any { it.difficulty in 1..3 }
    val allEasy = questions.all { it.difficulty <= 1 }
    val noneImpossible = questions.none { it.difficulty > 3 }
    println("是否有难题：$hasHard，是否都是简单题：$allEasy，是否没有不可能题：$noneImpossible")

    // 计数
    val count = questions.count { it.difficulty >= 1 }
    println("难度大于等于1的题目数量：$count")

    // 分组
    val map = questions.groupBy { it.difficulty }
    println(map)

    println("------------------------------------------------")

    // 链式调用
    // 取出难度 >= 3，降序，取出第一个的stem
    // .出来的都是一个对象
    val stem = questions
        .filter { it.difficulty >= 3 }
        .maxByOrNull { it.views }
        ?.stem
    println(stem)
}