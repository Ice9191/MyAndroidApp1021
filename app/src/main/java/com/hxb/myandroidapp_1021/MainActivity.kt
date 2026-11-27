package com.hxb.myandroidapp_1021

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.hxb.myandroidapp_1021.core.network.HttpClient
import com.hxb.myandroidapp_1021.core.ui.common.LocalNavigationInputLocked
import com.hxb.myandroidapp_1021.core.ui.theme.ToolSystemPdaProjectTheme
import com.hxb.myandroidapp_1021.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var httpClient: HttpClient

    private var navigationInputLocked by mutableStateOf(false)
    private val blockedKeys = mutableSetOf<Pair<Int, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch { httpClient.restoreSession() }
        setContent {
            ToolSystemPdaProjectTheme {
                CompositionLocalProvider(LocalNavigationInputLocked provides navigationInputLocked) {
                    AppNavHost(httpClient.loginState) { navigationInputLocked = it }
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // 拦下转场中按住的键，直到松开或识别到下一次独立按键。
        val key = event.deviceId to event.keyCode
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (event.repeatCount == 0) blockedKeys.remove(key)
            if (navigationInputLocked) blockedKeys.add(key)
        }
        val blocked = if (event.action == KeyEvent.ACTION_UP) {
            blockedKeys.remove(key)
        } else {
            key in blockedKeys
        }
        return navigationInputLocked || blocked || super.dispatchKeyEvent(event)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent): Boolean =
        navigationInputLocked || super.dispatchKeyShortcutEvent(event)
}
