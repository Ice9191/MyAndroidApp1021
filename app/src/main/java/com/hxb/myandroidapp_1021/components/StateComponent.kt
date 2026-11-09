package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 状态管理：数据引起UI更新
 *  什么是状态：在Compose中，状态就是会变化的数据，当状态变化时，界面自动更新，这个变量就是状态变量
 *  remember: 记住值，界面重新绘制（重组）时不丢失
 *  mutableStateOf: 创建可观察的状态，值变化时自动刷新UI
 *  为什么要remember：Compose 界面会频繁重组（数据变化时重新执行 @Composable 函数）
 *                  ，如果没有 remember，每次重组 mutableStateOf(0) 都会重新创建，值就会重置为 0
 * by关键字：属性委托，简化代码
 *  by 是 Kotlin 的属性委托语法。
 *  var count by remember { mutableStateOf(0) } 的效果是：把 mutableStateOf 的 .value 读写操作委托给 count 变量，这样你就可以直接用 count 代替 count.value。
 * 状态提升：将状态从子组件提升到父组件，子组件只负责展示和回调
 *  应用1：当父组件和子组件需要共用一些属性或状态的时候，那么这个状态不应该定义在子组件，而是应该定义在父组件里面
 *  应用2：当一个状态变量不仅仅在父组件要用，而且还要在其他子组件使用时，那么就可以这个状态变量提升到父组件
 *  项目中的例子：
 *      1. 登录页的用户名/密码输入框就是状态提升——状态在 LoginPage 中管理，传给输入框组件
 *      2. 这样父组件可以直接获取输入的值进行登录请求
 */
@Composable
fun ParentComp() {
    // 定义状态变量text
    // 状态变量要传递给子组件
    var text by remember { mutableStateOf("你好") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("输入的内容：${text}")
        // 放一个子组件
        ChildComp(text, onTextChange = { text = it })
    }
}

@Composable
fun ChildComp(
    text: String,
    // 子组件要修改父组件的东西，需要通过事件回调
    onTextChange: (String) -> Unit
) {
    // 输入框
    TextField(value = text, onValueChange = onTextChange)
}

@Composable
fun CounterDemoNoBy() {
    // 写法1：用.value来访问，比较麻烦
    // val count = remember { mutableIntStateOf(0) }
    // count.intValue++        // 修改值
    // println(count.intValue) // 获取值
    // 写法2：使用by关键字
    var count by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text("点击了${count.intValue}次", fontSize = 25.sp)
        Text("点击了${count}次", fontSize = 25.sp)
        // 这里和Vue3的类似
        // Button(onClick = { count.intValue++ }) {
        Button(onClick = { count++ }) {
            Text("点击我+1")
        }
    }
}

@Composable
fun CounterDemo() {
    // 普通变量
    // var count = 0
    // 能记录状态的可以观察的状态变量(状态变化驱动视图更新)
    // mutableStateOf(0) -> 创建一个可观察的状态，初始值 0
    // remember { } -> 在界面重组（重新绘制）时保持值不丢失
    // 两者结合 = 状态变化时自动刷新 UI + 重组时不丢失数据
    var count by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("点击了${count}次", fontSize = 25.sp)
        Button(onClick = { count++ }) {
            Text("点击我+1")
        }
    }
}