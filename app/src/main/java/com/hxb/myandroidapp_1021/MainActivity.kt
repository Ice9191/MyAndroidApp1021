package com.hxb.myandroidapp_1021

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hxb.myandroidapp_1021.navigation.AppNavHost
import com.hxb.myandroidapp_1021.core.ui.theme.ToolSystemPdaProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolSystemPdaProjectTheme {
                AppNavHost()
            }
        }
    }
}
