package com.hxb.myandroidapp_1021

// 导入需要用到的工具类，组件/颜色/尺寸
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hxb.myandroidapp_1021.ui.navigation.AppNavHost
import com.hxb.myandroidapp_1021.ui.theme.ToolSystemPdaProjectTheme
import dagger.hilt.android.AndroidEntryPoint

// 定义一个类，类继承自ComponentActivity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // 重写并调用了父类的onCreate方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 安全间距
        enableEdgeToEdge()
        setContent {
            ToolSystemPdaProjectTheme {
                AppNavHost()
            }
        }
    }
}
