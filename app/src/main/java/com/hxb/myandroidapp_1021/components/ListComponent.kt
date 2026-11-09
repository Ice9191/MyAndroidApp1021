package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * LazyColumn列表：只渲染可见区域的项，性能高效
 * 带数据模型的列表：实际项目中，列表项不会只是字符串，而是数据对象
 *  混合内容列表：LazyColumn 可以混合不同类型的 item——顶部放标题、中间放列表、底部放提示
 */
// 数据模型
data class QuestionItem(
    val id: String,
    val stem: String,       // 题目标题
    val difficulty: Int,    // 难度 1-5
    val views: Int          // 浏览量
)

@Composable
fun ListDemoTwo() {
    // 模拟数据
    val mockQuestions = listOf(
        QuestionItem(id = "1", stem = "Activity生命周期有哪些回调？", difficulty = 1, views = 120),
        QuestionItem(id = "2", stem = "Handler的底层原理是什么？", difficulty = 3, views = 580),
        QuestionItem(id = "3", stem = "协程的挂起与恢复机制是怎样的？", difficulty = 4, views = 820),
        QuestionItem(id = "4", stem = "View的绘制流程分哪几步？", difficulty = 3, views = 650),
        QuestionItem(id = "5", stem = "如何分析和定位内存泄漏？", difficulty = 4, views = 890),
        QuestionItem(
            id = "6",
            stem = "LiveData和StateFlow有什么区别？",
            difficulty = 3,
            views = 720
        ),
        QuestionItem(id = "7", stem = "Room数据库如何做增量更新？", difficulty = 2, views = 340),
        QuestionItem(id = "8", stem = "Compose的重组机制是怎样的？", difficulty = 5, views = 950),
        QuestionItem(id = "9", stem = "Room数据库如何做增量更新？", difficulty = 2, views = 340),
        QuestionItem(id = "10", stem = "Compose的重组机制是怎样的？", difficulty = 5, views = 950),
        QuestionItem(id = "11", stem = "Room数据库如何做增量更新？", difficulty = 2, views = 340),
        QuestionItem(id = "12", stem = "Compose的重组机制是怎样的？", difficulty = 5, views = 950)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
            .padding(top = 35.dp)
    ) {
        // 顶部标题
        // item{}渲染单个项
        item {
            Text(
                text = "面试题列表",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        // 列表项
        // 键
        items(mockQuestions, key = { it.id }) {
            QuestionRow(it)
            // 加个分隔线
            HorizontalDivider(color = Color.LightGray)
        }
        // 底部提示（单个 item）
        item {
            Text(
                text = "没有更多了~",
                fontSize = 14.sp,
                color = Color.Gray,
                // 文字居中
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

// 子组件
@Composable
fun QuestionRow(item: QuestionItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 第一行：题目标题
        Text(
            text = item.stem,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        // 间距 4dp
        Spacer(modifier = Modifier.height(4.dp))
        // 第二行: 难度 + 浏览量 (灰色小字)
        Text(
            text = "难度${item.difficulty} · 浏览${item.views}",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ListDemo() {
    // 准备数据
    val myList = listOf<String>(
        "Item 1", "Item 2", "Item 3", "Item 4", "Item 5",
        "Item 6", "Item 7", "Item 8", "Item 9", "Item 10",
        "Item 11", "Item 12", "Item 13", "Item 14", "Item 15",
        "Item 16", "Item 17", "Item 18", "Item 19", "Item 20",
        "Item 21", "Item 22", "Item 23", "Item 24", "Item 25",
        "Item 26", "Item 27", "Item 28", "Item 29", "Item 30"
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red)
            .padding(top = 35.dp)
    ) {
        items(myList) {
            Text(
                text = it,
                modifier = Modifier
                    .padding(6.dp)
                    .background(color = Color.Cyan)
                    .padding(6.dp)
            )
        }
    }
}