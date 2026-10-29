package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.time.Duration.Companion.milliseconds

/**
 * HorizontalPager轮播图
 *  pagerState常用属性：
 *      pageCount：总页数
 *      currentPage：当前页的索引
 *      targetPage：滚动目标页
 *      isScrollInProgress：用户是否正在滚动
 *  pagerState两个挂起函数：
 *      scrollToPage(page：Int)：瞬间跳转到指定页(无动画)，应用：点击底部Tab快速切换，不需要过渡动画
 *      animateScrollToPage(page：Int)：平滑滚动到指定页(有过渡动画)，应用：自动轮播、点击“下一页”按钮时丝滑滑动
 *  无限轮播+指示器：把虚拟页数设为Int.MAX_VALUE，从中间开始，左右永远滑不到头
 *     无限轮播原理：
 *         pageCount = Int.MAX_VALUE → 虚拟页数无穷大（约21亿页）
 *         initialPage = Int.MAX_VALUE / 2 → 从中间开始，左右都能滑很远
 *         page % realCount → 虚拟页号取模映射到真实索引，例如 3 张图，虚拟页 1073741824 → 1073741824 % 3 = 1 → 显示第 2 张
 *         自动轮播只需 currentPage + 1，因为页数无穷大，不需要处理边界
 */
@Composable
fun HorizontalPagerDemoAutoScrollImprove() {
    // 用颜色代替图片
    val colors = listOf(
        Color(0xFFE91E63), // 粉色
        Color(0xFF2196F3), // 蓝色
        Color(0xFF4CAF50)  // 绿色
    )
    // 获取真实的个数
    val realCount = colors.size
    if (realCount == 0) return
    // 设置虚拟页数为无穷大
    val virtualCount = Int.MAX_VALUE
    // 设置滚动的起始页的位置
    val startIndex = virtualCount / 2
    // 创建pagerState，管理轮播图的
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { virtualCount }
    )
    // 获取真实的下标，虚拟页号取模映射到真实索引
    val realIndex = pagerState.currentPage % realCount
    println("666-realIndex:${realIndex}")
    // 开启副作用
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000.milliseconds)
            // 用户正在拖动/惯性滑动时，这一轮先跳过
            if (pagerState.isScrollInProgress) continue
            try {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            } catch (e: CancellationException) {
                // 用户滑动会以更高优先级"抢占"滚动，animateScrollToPage 的协程被取消，
                // 抛出的 CancellationException 会一路冒出 LaunchedEffect，while 循环就死了。
                // ensureActive()：组件还活着就继续循环，只有组件销毁时才真正退出。
                ensureActive()
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState
            // page是虚拟页的索引
        ) { page ->
            // 获取真正的索引，这里的索引还包含了前后的虚拟页，会保证滑动时丝滑的过渡，因此不能用上面的realIndex代替
            val currentPageIndex = page % realCount
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors[currentPageIndex]),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "第${currentPageIndex + 1}页",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
        // 指示器小圆点：必须放在 Pager 外面，作为外层 Box 的直接子元素，
        // 这样 align 才会生效（Pager 的一页里只能放一个根布局）
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(realCount) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (realIndex == index) Color.White // 当前页：白色
                            else Color.White.copy(alpha = 0.5f) // 非当前页：半透明白
                        )
                )
                if (index != realCount - 1) {
                    Spacer(modifier = Modifier.width(6.dp)) // 圆点间距
                }
            }
        }
    }
}

@Composable
fun HorizontalPagerDemoAutoScroll() {
    // 用颜色代替图片
    val colors = listOf(
        Color(0xFFE91E63), // 粉色
        Color(0xFF2196F3), // 蓝色
        Color(0xFF4CAF50)  // 绿色
    )
    // 创建pagerState，管理轮播图的
    val pagerState = rememberPagerState(pageCount = { colors.size })
    // 自动轮播，使用LaunchedEffect副作用
    LaunchedEffect(pagerState) {
        while (true) {
            delay(2000.milliseconds)
            // isScrollInProgress为true表示用户正在手动滑动
            if (pagerState.isScrollInProgress) continue
            // 计算下一页
            val nextPage =
                if (pagerState.currentPage == colors.size - 1) 0 else pagerState.currentPage + 1
            pagerState.animateScrollToPage(nextPage)
        }
    }
    HorizontalPager(
        state = pagerState
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors[page]),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "第${page}页",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun BasicPagerDemo() {
    // 用颜色代替图片
    val colors = listOf(
        Color(0xFFE91E63), // 粉色
        Color(0xFF2196F3), // 蓝色
        Color(0xFF4CAF50)  // 绿色
    )
    // 创建pagerState，管理轮播图的
    val pagerState = rememberPagerState(pageCount = { colors.size })
    println("666-${pagerState.pageCount}")
    println("666-${pagerState.currentPage}")
    // pagerState.scrollToPage()
    // pagerState.animateScrollToPage()
    HorizontalPager(
        state = pagerState
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.6f)
                .background(colors[page]),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "第${page}页",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}