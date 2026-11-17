package com.hxb.myandroidapp_1021.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation

/**
 * 页面前台且允许扫描时提供会话作用域；离开页面或提交时自动取消并释放设备。
 */
@Composable
fun ScannerLifecycle(enabled: Boolean, onSession: (CoroutineScope?) -> Unit) {
    val owner = LocalLifecycleOwner.current
    val callback = rememberUpdatedState(onSession)
    LaunchedEffect(owner, enabled) {
        if (!enabled) return@LaunchedEffect
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            callback.value(this)
            try {
                awaitCancellation()
            } finally {
                callback.value(null)
            }
        }
    }
}
