package com.hxb.myandroidapp_1021.components

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

/**
 * 证明 Thread.sleep(10) 和 delay(10) 的区别
 * 核心区别：
 * 1. 归属与类型：
 *    - Thread.sleep 是 Java 原生方法（阻塞）。
 *    - delay 是 Kotlin 协程的挂起函数（suspend，非阻塞）。
 * 2. 对线程的影响：
 *    - Thread.sleep 会【霸占并阻塞】当前所在的物理线程，导致该线程上的其他所有协程/任务全部卡死。
 *    - delay 会【挂起当前协程并让出】当前线程，调度器会立刻去执行该线程上排队等待的其他协程。
 * 3. Android 实际应用场景：
 *    - 在 Android 主线程（UI 线程）里用 Thread.sleep -> 界面直接卡死（ANR）。
 *    - 在主线程的协程里用 delay -> 界面依然丝滑，只是当前协程逻辑暂停。
 * 4. 可取消性：
 *    - Thread.sleep 无法被协程的 cancel() 打断，必须硬等结束。
 *    - delay 会响应 cancel()，一旦协程被取消，delay 会立刻抛出 CancellationException 并停止。
 */
fun test02() = runBlocking {
    println("========== 实验 1：使用 Thread.sleep (阻塞) ==========")
    launch {
        println("【A】开始执行，准备 sleep 2000ms...")
        // 强行霸占单线程的 CPU 资源，不交出控制权
        Thread.sleep(2000)
        println("【A】睡醒了，执行完毕")
    }
    launch {
        // 虽然启动了协程 B，但线程被 A 霸占着，B 只能干等
        println("【B】我也想执行...")
    }
    delay(3000.milliseconds)
    println("========== 实验 2：使用 delay (挂起) ==========")
    launch {
        println("【A】开始执行，准备 delay 1000ms...")
        // 立刻交出 CPU 控制权，让调度器去跑其他的协程（比如 B）
        delay(1000.milliseconds)
        println("【A】延迟结束，执行完毕")
    }
    launch {
        // 因为 A 让出了线程，B 的这行代码会立刻得到执行
        println("【B】我也想执行...")
    }
    println("========== 实验 结束 ==========")
}

/**
 * 测试 emit 和 tryEmit 的区别
 * 核心区别：
 * 1. 函数类型： emit 是挂起函数 (suspend)，tryEmit 是普通函数。
 * 2. 返回值： emit 返回 Unit；tryEmit 返回 Boolean（true 代表发送成功，false 代表失败）。
 * 3. 缓冲区满时的行为：
 *    - emit 会【挂起等待】，直到缓冲区有空位，绝不丢数据。
 *    - tryEmit 会【立即返回 false】，放弃发送，不会阻塞当前线程。
 * 4. 适用场景：
 *    - emit 适合绝不能丢失的关键事件（如保存成功、页面跳转）。
 *    - tryEmit 适合允许丢弃的非关键事件（如 Toast 提示、防抖）。
 */
fun test01() = runBlocking {
    val flow = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    // 启动消费者
    launch {
        flow.collect {
            println("消费者收到：$it，开始处理...")
            delay(1000.milliseconds)
            println("消费者处理完成：$it")
        }
    }
    println("tryEmit 测试开始")
    delay(1000.milliseconds)
    println("========== 测试 tryEmit (非挂起) ==========")
    // 消费者此时空闲，1 被直接拿走处理，成功
    println("发送 1 (tryEmit): ${flow.tryEmit(1)}")
    delay(4000.milliseconds)
    launch {
        println("发送 2 (tryEmit): ${flow.tryEmit(2)}") // 此时会返回 true！
        delay(6000.milliseconds)
        println("发送 3 (tryEmit): ${flow.tryEmit(3)}") // true
        // 缓冲区是满的，4 立即返回 false，4这个数据被丢弃
        println("发送 4 (tryEmit): ${flow.tryEmit(4)}")
    }
    println("1")
    delay(8000.milliseconds)
    println("========== 测试 emit (挂起) ==========")
    launch {
        println("准备发送 5 (emit)... (此时缓冲区是满的，即将挂起)")
        val startTime = System.currentTimeMillis()
        // 因为 buffer 满了，这里的 emit 会挂起，直到消费者腾出空间
        flow.emit(5)
        // 这里的6没有被发送，因为缓冲区满了，5占了一个位置，但是6没有丢失，6被挂起
        flow.emit(6)
        val endTime = System.currentTimeMillis()
        println("👉 发送 5 完成！emit 挂起了 ${endTime - startTime} 毫秒")
    }

}