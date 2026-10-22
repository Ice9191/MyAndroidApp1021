package com.hxb.myandroidapp_1021.kotlin_base

/**
 * 我们能不能自己去管控一个线程？
 * Dispatchers调度器：控制协程在哪个线程执行，用withContext切换线程
 * 实际项目中：
 *  我们会用到Retrofit的suspend函数会自动切换到IO线程，不需要手动withContext(Dispatchers.IO)
 *
 * 手动取消协程
 *  launch返回一个对象，通过它可以取消协程
 *  cancel()：取消协程
 *  join()：等待协程真正停止(取消是异步的，需要等一下)
 *  cancelAndJoin()：取消协程并等待协程真正停止
 *  实际项目中通常不需要手动取消协程，框架会自动处理，但理解取消原理对调试很重要
 */
fun main(){

}