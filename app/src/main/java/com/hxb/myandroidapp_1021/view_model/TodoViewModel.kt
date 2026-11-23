package com.hxb.myandroidapp_1021.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hxb.myandroidapp_1021.local.TodoPreferences
import com.hxb.myandroidapp_1021.models.TodoItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    // UI 状态：待办列表
    private val _todos = MutableStateFlow<List<TodoItems>>(emptyList())
    val todos = _todos.asStateFlow()

    // DataStore操作类
    private val preferences = TodoPreferences(application)
}