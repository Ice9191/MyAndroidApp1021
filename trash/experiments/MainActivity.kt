package com.hxb.myandroidapp_1021

// 导入需要用到的工具类，组件/颜色/尺寸
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.theme.ToolSystemPdaProjectTheme

// 定义一个类，类继承自ComponentActivity
class MainActivity : ComponentActivity() {
    // 重写并调用了父类的onCreate方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 安全间距
        enableEdgeToEdge()
        setContent {
            ToolSystemPdaProjectTheme {
                TopAppBar(
                    title = "其他拣选",
                    onBackClick = {},
                    onHomeClick = {}
                )
            }
        }
    }
}
