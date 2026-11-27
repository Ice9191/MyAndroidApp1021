package com.hxb.myandroidapp_1021.navigation

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.withResumed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventHandler
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import kotlinx.coroutines.launch

internal fun NavController.runFrom(entry: NavBackStackEntry, action: () -> Unit) {
    if (currentBackStackEntry === entry && entry.lifecycle.currentState == Lifecycle.State.RESUMED) {
        action()
    }
}

@Composable
internal fun NavController.onCompletionFrom(
    entry: NavBackStackEntry,
    action: () -> Unit
): () -> Unit {
    val scope = rememberCoroutineScope()
    return {
        // 保存或提交可能在应用切到后台时完成，等来源页恢复后再处理返回。
        scope.launch {
            entry.lifecycle.withResumed { runFrom(entry, action) }
        }
    }
}

internal fun Modifier.blockNavigationGestures(navController: NavController): Modifier =
    pointerInput(navController) {
        awaitEachGesture {
            var blocked = false
            do {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                blocked = blocked || navController.isNavigationInProgress()
                if (blocked) event.changes.forEach { it.consume() }
            } while (event.changes.any { it.pressed })
        }
    }

@Composable
internal fun LockNavigationInput(navController: NavController, onLockChanged: (Boolean) -> Unit) {
    val hostLifecycle = LocalLifecycleOwner.current.lifecycle
    val dispatcher =
        checkNotNull(LocalNavigationEventDispatcherOwner.current).navigationEventDispatcher
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    val currentOnLockChanged = rememberUpdatedState(onLockChanged)

    DisposableEffect(navController, hostLifecycle, dispatcher, view, focusManager) {
        // 系统返回的拦截优先于页面处理器，已经开始的预测性返回仍由原处理器完成或取消。
        val backBlocker = object : NavigationEventHandler<NavigationEventInfo.None>(
            initialInfo = NavigationEventInfo.None,
            isBackEnabled = false,
            isForwardEnabled = false
        ) {}
        dispatcher.addHandler(backBlocker, NavigationEventDispatcher.PRIORITY_OVERLAY)

        val accessibilityDelegate = ViewCompat.getAccessibilityDelegate(view)
        val accessibilityImportance = view.importantForAccessibility
        val accessibilityBlocker = object : AccessibilityDelegateCompat() {
            override fun getAccessibilityNodeProvider(host: View) = null

            override fun performAccessibilityAction(host: View, action: Int, args: Bundle?) = false
        }
        var inputLocked = false

        fun updateLock(
            locked: Boolean = hostLifecycle.currentState == Lifecycle.State.RESUMED &&
                    navController.isNavigationInProgress()
        ) {
            if (inputLocked == locked) return
            inputLocked = locked
            currentOnLockChanged.value(locked)
            backBlocker.isBackEnabled = locked
            // 转场期间移除 Compose 的虚拟无障碍节点入口，已获取的节点也无法执行操作。
            if (accessibilityDelegate != null) {
                ViewCompat.setAccessibilityDelegate(
                    view,
                    if (locked) accessibilityBlocker else accessibilityDelegate
                )
            }
            view.importantForAccessibility = if (locked) {
                View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
            } else {
                accessibilityImportance
            }
            if (locked) focusManager.clearFocus(force = true)
        }

        val observer = LifecycleEventObserver { _, _ -> updateLock() }
        var entryLifecycle: Lifecycle? = null
        val destinationListener = NavController.OnDestinationChangedListener { controller, _, _ ->
            entryLifecycle?.removeObserver(observer)
            entryLifecycle = controller.currentBackStackEntry?.lifecycle
            entryLifecycle?.addObserver(observer)
            updateLock()
        }
        navController.addOnDestinationChangedListener(destinationListener)
        hostLifecycle.addObserver(observer)

        onDispose {
            navController.removeOnDestinationChangedListener(destinationListener)
            entryLifecycle?.removeObserver(observer)
            hostLifecycle.removeObserver(observer)
            updateLock(false)
            backBlocker.remove()
        }
    }
}

private fun NavController.isNavigationInProgress(): Boolean =
    currentBackStackEntry?.lifecycle?.currentState?.let { it != Lifecycle.State.RESUMED } == true
