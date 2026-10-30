package com.hxb.myandroidapp_1021.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.models.QuestionItem
import com.hxb.myandroidapp_1021.models.TodoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * 要用ViewModel，需要继承ViewModel类
 * StateFlow 是一个持有单个值的可观察数据流。值变化时通知所有观察者（UI 界面）自动更新
 * 简单理解：它就像一个“会广播的变量”——你改了它的值，所有正在看它的人都能收到通知
 * StateFlow和SharedFlow的区别：
 *  值：持有当前值（持续状态） / 不持有（事件消费即消失）
 *  重复值：相同值不通知 / 每次都通知
 *  初始值：必须有 / 不需要
 *  新订阅者：立即收到当前值 / 不收到之前的事件
 *  适用场景：loading、data、error 等持续状态 / 导航跳转、Toast 提示等一次性事件
 * StateFlow 和 SharedFlow 的收集方式不同：
 *  StateFlow → 用 collectAsState() 收集，转成 Compose State，界面自动更新
 *  SharedFlow → 用 LaunchedEffect { flow.collect { } } 收集，处理一次性事件
 */

/**
 * 开发过程中，把所有的ViewModel都放在这里是没问题的
 */
class TodoListViewModel: ViewModel(){
    /**
     * 这两行代码是“单向数据流(UDF)”状态封装模式
     *  核心：把数据的“读写权限”分开：让ViewModel内部可以随意修改数据，但让外部(UI)只能看，不能改
     */
    // 列表数据 -> 持续性
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos = _todos.asStateFlow()

    /**
     * 这两行代码是封装的是事件
     */
    // Toast提示 -> 一次性
    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toast = _toast.asSharedFlow()
    // 自增Id
    private var nextId = 1
    // 提供方法
    // 增加
    fun addTodo(title: String){
        if(title.isBlank()){
            viewModelScope.launch {
                _toast.emit("标题不能为空")
            }
            return
        }
        // 列表追加
        _todos.value += TodoItem(nextId++, title, false)
    }
    // 切换完成状态
    fun toggleTodo(todo: TodoItem){
        _todos.value = _todos.value.map {
            if(it.id == todo.id) it.copy(isDone = !it.isDone) else it
        }
    }
    // 删除
    fun deleteTodo(todo: TodoItem){
        _todos.value = _todos.value.filter { it.id != todo.id }
        viewModelScope.launch {
            _toast.emit("删除成功：${todo.title}")
        }
        return
    }
}

class LoginViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // 登录成功
    // 设置1个缓冲区。如果没有收集者时 emit 会挂起等待，设置缓冲区后事件不会丢失
    // 如果不设置缓冲区，它只是一个信号，不能算作一个事件
    // 缓冲区也有防止界面卡顿的作用
    private val _loginSuccess = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val loginSuccess = _loginSuccess.asSharedFlow()

    // 登录失败
    private val _loginFailure = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val loginFailure = _loginFailure.asSharedFlow()
    private val validateUsers = mapOf("admin" to "123456", "test" to "123456")
    fun login(username: String, password: String) {
        if (_loading.value) return
        viewModelScope.launch {
            _loading.value = true
            try {
                delay(2000.milliseconds)
                if (validateUsers[username] == password) {
                    // 登录成功 -> 发射成功事件
                    _loginSuccess.emit("token=xxx")
                } else {
                    // 登录失败 -> 发射失败事件
                    _loginFailure.emit("用户名或密码错误")
                }
            } catch (e: Exception) {
                // 有错误消息就发射错误消息，没有就发射"登录失败"
                _loginFailure.emit(e.message ?: "登录失败")
            }
            _loading.value = false
        }
    }
}

// 用不到上下文context，就继承ViewModel()，否则需要继承AndroidViewModel()
class QuestionViewModel : ViewModel() {
    // 模拟数据
    private val mockData = listOf(
        QuestionItem("1", "Activity生命周期有哪些回调？", 1, 120),
        QuestionItem("2", "Handler的底层原理是什么？", 3, 580),
        QuestionItem("3", "协程的挂起与恢复机制是怎样的？", 4, 820),
        QuestionItem("4", "View的绘制流程分哪几步？", 3, 650),
        QuestionItem("5", "如何分析和定位内存泄漏？", 4, 890)
    )

    // 加载状态
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // 数据列表
    private val _questionList = MutableStateFlow<List<QuestionItem>>(emptyList())
    val questionList = _questionList.asStateFlow()

    // 发请求获取数据
    fun loadData() {
        // 必须开启一个协程作用域
        // 这里从lifecycleScope.launch {  }改为viewModelScope.launch {  }
        viewModelScope.launch {
            _loading.value = true
            delay(2000.milliseconds)
            _questionList.value = mockData
            _loading.value = false
        }
    }
}

class CounterViewModel : ViewModel() {
    // 创建一个私有的可变状态流，初始值为0
    // 数据变化，就会自动通知UI更新的变量
    // _count 前面有 _ 且是 private → 只能在 ViewModel 内部修改
    private val _count = MutableStateFlow(0)

    // 对外暴露只读状态
    // asStateFlow()方法将可变状态流变为只读状态流，外部只能读不能改
    // 这样 UI 层不能随意修改数据，必须通过 ViewModel 的方法来修改，保证数据流向单向可控
    val count = _count.asStateFlow()

    // 定义一个+1的方法
    fun increment() {
        _count.value++
    }

    // 重置方法
    fun reset() {
        _count.value = 0
    }
}