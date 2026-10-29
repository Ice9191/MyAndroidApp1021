package com.hxb.myandroidapp_1021.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.view_model.CounterViewModel
import com.hxb.myandroidapp_1021.view_model.LoginViewModel
import com.hxb.myandroidapp_1021.view_model.QuestionViewModel

/**
 * 传递ViewModel给组件，:后面是约束的类型
 * viewModel()是默认值，就是CounterViewModel的实例
 * viewModel: CounterViewModel = viewModel() → viewModel() 函数自动获取或创建 ViewModel 实例
 * collectAsState() → 把 StateFlow 转成 Compose 可观察的 State，值变化时界面自动更新
 * by 委托 → 让你直接用 count 而不是 count.value
 * viewModel.increment() → 调用 ViewModel 方法修改状态 → StateFlow 通知 → 界面自动刷新
 * 数据流完整路径：
 * 用户点击按钮 → 调用 viewModel.increment()
 * → ViewModel 修改 _count.value
 * → StateFlow 通知观察者
 * → collectAsState 收到新值
 * → Compose 重组 (重新渲染)
 * → 界面上的数字更新
 */
@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    // 状态变量
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loading by viewModel.loading.collectAsState()
    // 用来获取安卓系统能力的“通行证”，有了之后就可以调用安卓系统的能力了
    val context = LocalContext.current
    // 收集登录成功事件
    // 凡是收集都需要开启副作用
    LaunchedEffect(Unit) {
        // collect：回调函数，把loginSuccess的数据传给it
        viewModel.loginSuccess.collect {
            // Toast.LENGTH_SHORT：Toast显示的时间，当前为短时间
            Toast.makeText(context, "登录成功，$it", Toast.LENGTH_SHORT).show()
        }
    }
    // 收集登录失败事件
    LaunchedEffect(Unit) {
        viewModel.loginFailure.collect {
            // Toast.LENGTH_SHORT：Toast显示的时间，当前为短时间
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 用户名
        TextField(
            value = username,
            // 输入框内容改变时更新状态变量
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth()
        )
        // 密码
        TextField(
            value = password,
            // 输入框内容改变时更新状态变量
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        // 登录按钮
        Button(
            onClick = {
                // 这里就不需要开启协程作用域了，因为ViewModel已经使用了viewModelScope
                viewModel.login(username, password)
            },
            enabled = !loading
        ) {
            Text(if (loading) "登录中..." else "登录", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        // 提示文本
        Text("测试账号：admin/123456 或 test/test123", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun QuestionScreen(viewModel: QuestionViewModel = viewModel()) {
    // 状态变量
    val loading by viewModel.loading.collectAsState()
    val questionList by viewModel.questionList.collectAsState()
    // 进入页面获取请求
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(top = 30.dp)
        ) {
            items(items = questionList, key = { it.id }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(it.stem, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "难度${it.difficulty} · 浏览${it.views}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel()) {
    // collectAsState()将只读流变为组件的状态
    val count by viewModel.count.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("数量：$count", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            viewModel.increment()
        }) {
            Text("加1")
        }
        Button(onClick = {
            viewModel.reset()
        }) {
            Text("重置")
        }
    }
}