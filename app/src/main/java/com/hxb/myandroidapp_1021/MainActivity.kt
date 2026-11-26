package com.hxb.myandroidapp_1021

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.hxb.myandroidapp_1021.core.network.HttpClient
import com.hxb.myandroidapp_1021.navigation.AppNavHost
import com.hxb.myandroidapp_1021.core.ui.theme.ToolSystemPdaProjectTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var httpClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch { httpClient.restoreSession() }
        setContent {
            ToolSystemPdaProjectTheme {
                AppNavHost(httpClient.loginState)
            }
        }
    }
}
