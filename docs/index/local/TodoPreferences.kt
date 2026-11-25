package com.hxb.myandroidapp_1021.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hxb.myandroidapp_1021.models.TodoItems
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

// 顶级声明一个实例，确保全局只有一个DataStore实例，挂载到Context上，实例的名称为 "todo_store"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "todo_store")

/**
 * 用这个类去操作数据存储
 */
class TodoPreferences(context: Context) {
    // 获取dataStore
    private val dataStore = context.dataStore

    // json初始化
    private val json = Json { ignoreUnknownKeys = true }

    // 定义key
    private val TODO_KEY = stringPreferencesKey("todo_list")

    // 保存待办列表 → kotlin对象序列化成 JSON → 存入 DataStore
    suspend fun saveTodos(todos: List<TodoItems>) {
        val jsonStr = json.encodeToString(ListSerializer(TodoItems.serializer()), todos)
        dataStore.edit { it[TODO_KEY] = jsonStr }
    }

    // 从 DataStore 读取 JSON → 反序列化成 List<TodoItem> kotlin对象
    suspend fun loadTodos(): List<TodoItems> {
        return dataStore.data.map { prefs ->
            val jsonStr = prefs[TODO_KEY] ?: ""
            if (jsonStr.isEmpty()) emptyList()
            else json.decodeFromString(ListSerializer(TodoItems.serializer()), jsonStr)
        }.first()
    }

    private val NICK_NAME_KEY = stringPreferencesKey("nick_name")

    suspend fun saveNickName(nickName: String) {
        // nickName就是一个普通的字符串，直接进行保存，上面不是字符串，因此才需要反序列化
        dataStore.edit { it[NICK_NAME_KEY] = nickName }
    }

    suspend fun getNickName(): String {
        return dataStore.data.map { it[NICK_NAME_KEY] ?: "" }.first()
    }
}