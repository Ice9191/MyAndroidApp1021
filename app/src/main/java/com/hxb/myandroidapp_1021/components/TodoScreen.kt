package com.hxb.myandroidapp_1021.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.view_model.TodoListViewModel

@Composable
fun TodoScreen(viewModel: TodoListViewModel = viewModel()) {
    // 列表
    val todos by viewModel.todos.collectAsState()
    // 输入框绑定
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toast.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    // 搭配界面
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 36.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // 输入区
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入待办...") }
            )
            Button(onClick = {
                viewModel.addTodo(inputText)
                inputText = ""  // 清空输入框
            }) { Text("添加") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 列表
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(todos, key = { it.id }) { todo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { viewModel.toggleTodo(todo) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        todo.title,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
                        color = if (todo.isDone) Color.Gray else Color.Black
                    )
                    Text(
                        "删除",
                        color = Color.Red,
                        modifier = Modifier.clickable { viewModel.deleteTodo(todo) }
                    )
                }
                HorizontalDivider()
            }
        }
    }
}