package com.hxb.myandroidapp_1021

// 导入需要用到的工具类，组件/颜色/尺寸
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hxb.myandroidapp_1021.components.ToolInspectionScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// 定义一个类，类继承自ComponentActivity
class MainActivity : ComponentActivity() {
    // 重写并调用了父类的onCreate方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 安全间距
        enableEdgeToEdge()

        // 执行一段耗时任务，会导致UI卡顿，因此不能在主线程中执行
        // Thread.sleep(50000)
        // println("50秒后我执行了")

        // 用协程的delay替代(界面不卡)
        // 开启协程
        // lifecycleScope.launch{}：创建一个协程作用域(实际开发中慎用)
        // 也可以解决回调地狱
        // lifecycleScope.launch {
        //     delay(6000.milliseconds) // 延迟6秒，但不阻塞线程
        //     println("aaaaaaaaaaaaa-6秒后我执行了")
        // }

        // 测试使用
        // 执行顺序：两个协程已启动 -> 协程2完成 -> 协程1完成
        // launch{}：在作用域中启动一个新协程
        // lifecycleScope.launch {
        //     // 协程1
        //     launch {
        //         delay(timeMillis = 1000)
        //         println("666-协程1完成")
        //     }
        //     // 协程2
        //     launch {
        //         delay(timeMillis = 500)
        //         println("666-协程2完成")
        //     }
        //     println("666-两个协程已启动")
        // }

        // println("666-项目启动了")
        // // 开启协程作用域
        // lifecycleScope.launch {
        //     println("666-开始了")
        //     // 调用接口函数
        //     // 凡是用suspend挂起的函数，后面的代码都需要等它执行完毕才能执行，但是它不会阻塞线程
        //     val username = getUsername()
        //     println("666-获取用户名结果：$username")
        //     println("666-结束了")
        // }
        // println("666-项目结束了")

        // lifecycleScope.launch {
        //     // launch：启动协程，继续往下执行
        //     launch {
        //         delay(1000.milliseconds)
        //         println("666-launch协程1完成")
        //     }
        //     println("666-launch已启动，这行立即执行")
        // }

        // lifecycleScope.launch {
        //     // async: 启动协程并返回结果
        //     // async也不会阻塞线程
        //     val deferred = async {
        //         delay(5000.milliseconds)
        //         "666-异步结果"
        //     }
        //     println("666-async已启动，等待结果...")
        //     // await() 挂起等待结果
        //     val result = deferred.await()
        //     println("666-拿到结果: $result")
        // }

        // lifecycleScope.launch {
        //     println("666-开始加载")
        //     loadAllData()
        //     println("666-所有任务完成")
        // }

        // launch参数内默认是Dispatchers.Main，也可以换成Dispatchers.IO
        // lifecycleScope.launch(Dispatchers.Main) {
        //     println("666-当前的线程是：${Thread.currentThread().name}") // main线程
        //     // 去IO线程(一般在网络请求、文件读写才去IO线程)
        //     // 切换到IO线程
        //     withContext(Dispatchers.IO){
        //         println("666-当前的线程是：${Thread.currentThread().name}") // IO线程
        //         delay(5000.milliseconds)
        //         println("666-网络请求结果:xxx")
        //     }
        //     // 切换到 Default 线程（CPU密集型计算）
        //     withContext(Dispatchers.Default) {
        //         println("666-Default线程: ${Thread.currentThread().name}")
        //         val list = (1..1000).sortedDescending()
        //         println("666-排序完成, 最大值: ${list.first()}")
        //     }
        //     println("666-回到主线程：${Thread.currentThread().name}") // main线程
        // }

        // lifecycleScope.launch {
        //     var count = 0
        //     // 手动取消协程
        //     val task = launch {
        //         while (true) {
        //             delay(timeMillis = 1000)  // 每隔1秒
        //             count++
        //             println("666-第${count}次执行")
        //         }
        //     }
        //     delay(timeMillis = 3500)  // 让主协程等3.5秒，观察定时任务输出
        //     println("666-准备取消协程")
        //     // task.cancel() // 给协程发送取消信号
        //     // task.join() // 等待协程真正停止(取消是异步的，需要等一下)
        //     task.cancelAndJoin() // cancel + join的简写，一行搞定
        //     println("666-主线程执行完了")
        // }

        // 设置页面的内容，写ComposeUI
        Log.d("ScreenSize", "宽: ${resources.displayMetrics.widthPixels}, 高: ${resources.displayMetrics.heightPixels}, 密度: ${resources.displayMetrics.densityDpi}")
        setContent {
            // 脚手架，现在先不用
            // MyAndroidApp1021Theme {
            //     Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            //         Greeting(
            //             name = "Android",
            //             modifier = Modifier.padding(innerPadding)
            //         )
            //     }
            // }

            ToolInspectionScreen()
        }
    }
}

suspend fun loadAllData() = coroutineScope {
    // 任务1
    launch {
        delay(3000.milliseconds)
        println("666-任务1的线程是：${Thread.currentThread().name}") // main线程
        println("666-任务1完成")
    }
    // 任务2
    launch {
        delay(1500.milliseconds)
        println("666-任务2的线程是：${Thread.currentThread().name}") // main线程
        println("666-任务2完成")
    }
    // 任务3
    val result = async {
        delay(2000.milliseconds)
        println("666-任务3的线程是：${Thread.currentThread().name}") // main线程
        "任务3结果"
    }.await()
    println("666-任务3完成: $result")
}

/**
 * 模拟网络请求
 */
suspend fun getUsername(): String {
    println("666-调用接口获取用户名...")
    // 有delay，就说明函数里面有耗时操作(异步)，这时就需要使用suspend，变为同步操作
    delay(10000.milliseconds)
    println("666-接口服务器返回用户名...")
    return "666-张三"
}