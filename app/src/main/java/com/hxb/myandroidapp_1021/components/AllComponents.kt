package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.hxb.myandroidapp_1021.R

/**
 * 所有UI组件都是带@Composable注解的函数
 * Modifier修饰符：用于设置尺寸、间距、背景、点击等所有外观和行为，相当于CSS的集合
 * dp：密度无关像素，用于设置尺寸(宽高、间距、圆角)
 * sp：缩放无关像素，用于字体大小
 * Modifier顺序：后设置的会覆盖先设置的
 * 三大布局：
 *  Column：垂直排列
 *  Row：水平排列
 *  Box：堆叠布局
 * Spacer：间距
 * weight：按比例分配
 * Image：图片
 *  本地图片：res/drawable
 *  网络图片：加载网络图片需要用第三方库Coil(Compose生态最流行的图片加载库)
 * 加载状态处理(占位图、错误图)：使用subcomposeAsyncImage()函数
 */
@Composable
fun NetworkImageWithPlaceholderDemo() {
    Column(
        modifier = Modifier
            .padding(top = 36.dp)
            .fillMaxSize()
            .background(color = Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 精细控制
        SubcomposeAsyncImage(
            model = "https://picsum.photos/400/300s",
            contentDescription = "网络图片",
            modifier = Modifier
                .size(200.dp),
            contentScale = ContentScale.Crop,
            // 加载中...
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Cyan),
                    contentAlignment = Alignment.Center
                ) {
                    // 显示加载中的圆形进度条
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun NetworkImageDemo() {
    Column(
        modifier = Modifier
            .padding(top = 36.dp)
            .fillMaxSize()
            .background(color = Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://picsum.photos/400/300",
            contentDescription = "这是一张网络图片",
            modifier = Modifier
                .size(width = 300.dp, height = 300.dp)
                .clip(CircleShape)
                .background(color = Color.Green),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ImageDemo() {
    Column(
        modifier = Modifier
            .padding(top = 36.dp)
            .fillMaxSize()
            .background(color = Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 本地图片
        Image(
            painter = painterResource(id = R.drawable.one),
            contentDescription = "这是一张图片",
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(color = Color.Green),
            // 内容缩放方式
            // contentScale = ContentScale.Fit  // 保持比例，完整显示(可能留白)
            contentScale = ContentScale.Crop    // 保持比例，不会完整显示(可能裁剪)
        )
    }
}

@Composable
fun TextDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 36.dp)
            .background(color = Color.Red),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 基础文字
        Text(
            text = "普通文字",
            modifier = Modifier
                .background(color = Color.Green)
        )
        // 设置字号、粗细、颜色
        Text(
            text = "标题样式",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3),
            modifier = Modifier
                .background(color = Color.Green)
        )
        // 超长文字省略
        Text(
            text = "这是一段非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常长的文字",
            fontSize = 14.sp,
            maxLines = 1,                       // 最多显示1行
            overflow = TextOverflow.Ellipsis    // 溢出显示省略号...
        )
        // 居中对齐
        Text(
            text = "居中对齐的文字",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Cyan)
        )
    }
}

@Composable
fun WeightDemo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Cyan)
    ) {
        // 比例 1:1:1 -> 各占 1/3
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.Red)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.Green)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.Blue)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // 比例 2:1 -> 左边占 2/3，右边占 1/3
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
                    .background(Color(0xFFFFF9800))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color(0xFF2196F3))
            )
        }
    }
}

@Composable
fun SpacerDemo() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.Red)
    ) {
        Text(
            text = "标题",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        // 标题和内容之间8dp的间距
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .background(color = Color.Green)
        )
        Text(
            text = "这是文字内容",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun BoxDemo() {
    Box(
        modifier = Modifier
            .padding(top = 25.dp)
            .background(color = Color.Red)
            .size(300.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .background(color = Color.Green)
                .size(150.dp)
        )
        Text(
            text = "叠加文字",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.offset(x = 20.dp, y = 30.dp)
        )
    }
}

@Composable
fun RowDemo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 30.dp)
            .background(color = Color.Cyan),
        // 垂直对齐方式
        verticalAlignment = Alignment.CenterVertically,
        // 水平对齐方式
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "111",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .background(color = Color.Red)
        )
        Text(
            text = "222",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .background(color = Color.Green)
        )
        Text(
            text = "333",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .background(color = Color.Blue)
        )
    }
}

@Composable
fun ColumnDemo() {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxSize()
            .background(color = Color.Cyan),
        // 水平对齐方式
        horizontalAlignment = Alignment.CenterHorizontally,
        // 垂直对齐方式
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "第一行数据",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .background(color = Color.Red)
        )
        Text(
            text = "第二行数据",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .background(color = Color.Green)
        )
        Text(
            text = "第三行数据",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .background(color = Color.Blue)
        )
    }
}

@Composable
fun ModifierOrderDemo() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        // padding 在 background 前面 -> 外边框效果
        // 红色方块外面有 16dp 透明边距
        Box(
            modifier = Modifier
                .padding(all = 16.dp)        // <- 外边框
                .background(Color.Red) // <- 红色背景
                .size(80.dp)
        ) {
            Text("外", color = Color.White)
        }
        // padding 在 background 后面 -> 内边框效果
        // 红色方块更大，文字内缩 16dp
        Box(
            modifier = Modifier
                .padding(all = 16.dp)        // <- 和左边一样有外边框
                .background(Color.Red) // <- 红色背景 (包含 padding 区域)
                .padding(all = 16.dp)        // <- 内边距
                .size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("内", color = Color.White)
        }
    }
}

@Composable
fun ColorDemo() {
    Column(
        modifier = Modifier
            .background(color = Color.Cyan)
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        Text(
            text = "颜色1",
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(1f)
                .size(width = 100.dp, height = 50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(0xFF673AB7).copy(alpha = 0.9f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(3.dp, color = Color.Black, RoundedCornerShape(10.dp))
                .clickable {
                    println("666-点击了干嘛")
                }
        )
        Text(
            text = "颜色2",
            modifier = Modifier
                .background(color = Color.Green)
                .weight(1f)
        )
    }
}

@Composable
fun Greeting(name: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red)
            .padding(top = 40.dp)
    ) {
        Text(
            text = "Hello, $name!",
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Green)
                .padding(10.dp),
            fontSize = 20.sp,
            color = Color.Red
        )
    }
}