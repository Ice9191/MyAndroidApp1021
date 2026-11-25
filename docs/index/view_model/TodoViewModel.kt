package com.hxb.myandroidapp_1021.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.local.TodoPreferences
import com.hxb.myandroidapp_1021.models.TodoItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    // UI 状态：待办列表
    private val _todos = MutableStateFlow<List<TodoItems>>(emptyList())
    val todos = _todos.asStateFlow()

    // DataStore操作实例，传入上下文
    private val preferences = TodoPreferences(application.applicationContext)

    // 初始化方法
    init {
        viewModelScope.launch {
            _todos.value = preferences.loadTodos()
        }
    }

    private fun saveToDataStore(todos: List<TodoItems>) {
        viewModelScope.launch {
            preferences.saveTodos(todos)
        }
    }

    // 增加todos
    fun addTodo(text: String) {
        val newTodo = TodoItems(text = text)
        val updated = _todos.value + newTodo
        _todos.value = updated
        saveToDataStore(updated)
    }

    // 修改todos
    fun updateTodo(id: String) {
        val updated = _todos.value.map {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
        _todos.value = updated
        saveToDataStore(updated)
    }

    // 删除todos
    fun deleteTodo(id: String) {
        val updated = _todos.value.filter { it.id != id }
        _todos.value = updated
        saveToDataStore(updated)
    }
}