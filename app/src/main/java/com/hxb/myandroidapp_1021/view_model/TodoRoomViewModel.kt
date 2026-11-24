package com.hxb.myandroidapp_1021.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.local.room.TodoDatabase
import com.hxb.myandroidapp_1021.local.room.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val _todos = MutableStateFlow<List<TodoEntity>>(emptyList())
    val todos = _todos.asStateFlow()

    /**
     * 得拿到TodoDao里面的insert方法，需要先拿到TodoDatabase的数据库实例，然后通过数据库实例拿到TodoDao，最后调用insert方法
     */
    private val db = TodoDatabase.getDatabase(application.applicationContext)
    private val todoDao = db.todoDao()

    // init不能少，不然会没有数据
    init {
        loadTodos()
    }

    /**
     * 更新获取表中的数据
     */
    private fun loadTodos() {
        viewModelScope.launch {
            _todos.value = todoDao.getAll()
        }
    }

    // 增加todos
    fun addTodo(text: String) {
        viewModelScope.launch {
            todoDao.insert(TodoEntity(text = text))
            loadTodos() // 重新查询刷新列表
        }
    }

    // 修改todos
    fun updateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            // 在传入的todo基础上产生一个新的todo对象，然后修改isDone属性
            todoDao.update(todo.copy(isDone = !todo.isDone))
            loadTodos() // 重新查询刷新列表
        }
    }

    // 删除todos
    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todoDao.delete(todo)
            loadTodos() // 重新查询刷新列表
        }
    }
}