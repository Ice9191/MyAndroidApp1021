package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

/**
 * 通用下拉刷新容器：管理下拉状态、首次自动刷新、刷新完成提示和刷新头部高度。
 * content 必须把 refreshHeader 放到可滚动列表的第一个位置，头部展开时才能推动列表数据。
 * hasLoaded 用于首次自动下拉和刷新完成判断；autoRefresh 为 false 时跳过首次自动刷新。
 * 首次自动刷新会等待页面完全进入窗口后再开始，避免页面转场过程中就展开刷新头部。
 */
@Composable
fun PullToRefresh(
    isRefreshing: Boolean,
    hasLoaded: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    autoRefresh: Boolean = true,
    content: @Composable (refreshHeader: @Composable () -> Unit) -> Unit
) {
    val pullState = rememberPullToRefreshState()
    var wasRefreshing by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(false) }
    var completedLabel by remember { mutableStateOf(false) }
    var isPageFullyVisible by remember { mutableStateOf(false) }
    val pullProgress = pullState.distanceFraction.coerceIn(0f, 1f)
    val refreshingForUi = isRefreshing || wasRefreshing

    LaunchedEffect(isPageFullyVisible) {
        if (autoRefresh && isPageFullyVisible && !hasLoaded && !isRefreshing) {
            delay(100.milliseconds)
            pullState.animateToThreshold()
            delay(300.milliseconds)
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            wasRefreshing = true
            showCompleted = false
            completedLabel = false
        } else if (wasRefreshing && hasLoaded) {
            wasRefreshing = false
            showCompleted = true
            completedLabel = true
            delay(500.milliseconds)
            showCompleted = false
            delay(600.milliseconds)
            completedLabel = false
        }
    }

    PullToRefreshBox(
        isRefreshing = refreshingForUi || showCompleted,
        onRefresh = onRefresh,
        state = pullState,
        modifier = modifier.onGloballyPositioned { coordinates ->
            // 页面完全进入窗口后再自动下拉，避免页面还在转场时就开始刷新
            if (!isPageFullyVisible && coordinates.positionInWindow().x <= 0.5f) {
                isPageFullyVisible = true
            }
        },
        indicator = {}
    ) {
        content {
            PullRefreshHeader(
                progress = pullProgress,
                isRefreshing = refreshingForUi,
                isCompleted = completedLabel
            )
        }
    }
}

/**
 * 刷新头部：高度随下拉进度变化，图标和文字作为一行整体移动。
 * 刷新完成时不再保留图标占位，文字单独水平居中。
 */
@Composable
private fun PullRefreshHeader(
    progress: Float,
    isRefreshing: Boolean,
    isCompleted: Boolean
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val headerHeight = (clampedProgress * 60).dp
    val text = when {
        isCompleted -> "刷新完成"
        isRefreshing -> "正在刷新..."
        clampedProgress >= 1f -> "释放立即刷新"
        else -> "下拉刷新"
    }
    val arrowRotation by animateFloatAsState(
        targetValue = 180f * clampedProgress,
        label = "pullRefreshArrow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isCompleted) {
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRefreshing) {
                        DottedSpinner(modifier = Modifier.size(18.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(arrowRotation)
                        )
                    }
                }
            }
            Text(
                text = text,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 点状旋转加载图标，模拟截图中的环形点状进度。
 */
@Composable
private fun DottedSpinner(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pullRefreshSpinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing)
        ),
        label = "pullRefreshSpinnerRotation"
    )
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = modifier.rotate(rotation)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val ringRadius = size.minDimension * 0.32f
        val dotRadius = size.minDimension * 0.055f
        repeat(12) { index ->
            val angle = index * (2.0 * PI / 12.0)
            drawCircle(
                color = color.copy(alpha = 0.15f + 0.85f * index / 11f),
                radius = dotRadius,
                center = Offset(
                    x = center.x + cos(angle).toFloat() * ringRadius,
                    y = center.y + sin(angle).toFloat() * ringRadius
                )
            )
        }
    }
}
