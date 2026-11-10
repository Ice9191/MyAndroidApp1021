package com.hxb.myandroidapp_1021

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hxb.myandroidapp_1021.pages.CameraWithFrameLog
import com.hxb.myandroidapp_1021.ui.theme.MyAndroidApp1021Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAndroidApp1021Theme {
                CameraWithFrameLog()
            }
        }
    }
}
