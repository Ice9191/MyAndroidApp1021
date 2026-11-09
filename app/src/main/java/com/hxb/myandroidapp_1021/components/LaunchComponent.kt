package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * LaunchedEffect 副作用
 *  什么是副作用：@Composable 函数应该是“纯函数”——只描述 UI。但有时需要在界面中执行一些额外操作（网络请求、定时器等），这些就是“副作用”。LaunchedEffect 就是用来安全执行副作用的。
 *  LaunchedEffect 和协程的关系：LaunchedEffect 内部启动了一个协程（第3讲学的），所以可以在里面调用 suspend 函数（如 delay、网络请求）。当 Composable 离开界面时，协程自动取消。
 *  key机制：切换分类时重新加载
 *      key 为 Unit 表示只执行一次，应用：进入页面时加载数据
 *      key 为 某个变量 表示变量变化时重新执行，应用：切换分类时重新加载数据
 *      key 为 多个变量 LaunchedEffect(key1, key2)任一变化时都重新执行，应用：分类+排序都变时刷新
 *  rememberCoroutineScope：LaunchedEffect 是自动执行的。如果需要在用户交互（如点击按钮）时启动协程，用 rememberCoroutineScope。
 *  使用场景：
 *      LaunchedEffect：进入页面/数据变化时自动执行
 *      rememberCoroutineScope：用户交互(点击)时手动执行
 */
@Composable
fun CoroutineScopeDemo() {
    var result by remember { mutableStateOf("点击按钮加载数据") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // 获取协程作用域
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, // 水平居中
        verticalArrangement = Arrangement.Center // 垂直居中
    ) {
        Text(result, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (loading) return@Button  // 防止重复点击
                loading = true
                result = "加载中..."
                // 在点击事件中启动协程
                scope.launch {
                    delay(2000.milliseconds) // 模拟网络请求
                    result = "加载完成！数据已返回"
                    loading = false
                }
            },
            enabled = !loading  // 加载中禁用按钮
        ) {
            Text(if (loading) "加载中..." else "加载数据")
        }
    }
}

@Composable
fun CategoryListDemo() {
    val categoryTab = listOf<String>("Android", "Java", "Kotlin")
    // 类型为 List<QuestionItem> 的空列表
    var questionList by remember { mutableStateOf<List<QuestionItem>>(emptyList()) }
    var selectedIndex by remember { mutableStateOf(0) }
    // 副作用
    LaunchedEffect(selectedIndex) {
        // 模拟网络请求
        questionList = emptyList()
        delay(500.milliseconds)
        questionList = when (selectedIndex) {
            0 -> listOf(
                QuestionItem("1", "Activity生命周期", 1, 100),
                QuestionItem("2", "Handler原理", 3, 500),
                QuestionItem("3", "View绘制流程", 3, 600)
            )

            1 -> listOf(
                QuestionItem("4", "Java集合框架", 2, 200),
                QuestionItem("5", "Java多线程", 4, 400),
                QuestionItem("6", "Java虚拟机", 5, 500)
            )

            2 -> listOf(
                QuestionItem("7", "Kotlin协程", 3, 300),
                QuestionItem("8", "Kotlin扩展函数", 2, 250),
                QuestionItem("9", "Kotlin高阶函数", 4, 400)
            )

            else -> emptyList()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // tab分类
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            categoryTab.forEachIndexed { index, category ->
                Text(
                    text = category,
                    fontSize = 16.sp,
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (index == selectedIndex) Color.Black else Color.Gray,
                    modifier = Modifier.clickable {
                        selectedIndex = index
                    }
                )
            }
        }
        // 列表
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(questionList, key = { it.id }) {
                QuestionRow(it)
                HorizontalDivider(color = Color.LightGray)
            }
        }
    }
}

@Composable
fun LaunchedEffectDemo() {
    // 状态变量
    var text by remember { mutableStateOf("加载中...") }
    // 开启副作用(获取网络数据)
    // Unit：表示只执行一次
    LaunchedEffect(Unit) {
        delay(2000.milliseconds)
        text = "返回结果"
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 20.sp)
    }
}