package com.hxb.myandroidapp_1021.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.utils.SettingManager
import com.hxb.myandroidapp_1021.view_model.TodoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(viewModel: TodoViewModel = viewModel()) {
    var inputText by remember { mutableStateOf("") }
    val todos by viewModel.todos.collectAsState()

    // 昵称
    val nickname by SettingManager.nickname.collectAsState()
    // 控制弹窗的显示和隐藏
    var showNameDialog by remember { mutableStateOf(false) }
    // 在事件里面开启一个协程
    val scope = rememberCoroutineScope()

    if (showNameDialog) {
        var tempName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("设置昵称") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("输入昵称") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        SettingManager.setNickname(tempName)
                    }
                    showNameDialog = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("取消") }
            }
        )
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 用户昵称
            Text(
                if (nickname.isEmpty()) "欢迎使用代办清单" else "欢迎回来，$nickname",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = {
                showNameDialog = true
            }) {
                Text(if (nickname.isEmpty()) "设置昵称" else "修改昵称")
            }
            Spacer(modifier = Modifier.height(10.dp))
            // 输入区：输入框 + 添加按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("输入待办事项") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.addTodo(inputText.trim())
                            inputText = ""
                        }
                    }
                ) { Text("添加") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 列表区
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = todos, key = { it.id }) { todo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = todo.isDone,
                                onCheckedChange = {
                                    viewModel.updateTodo(todo.id)
                                }
                            )
                            Text(
                                text = todo.text,
                                modifier = Modifier.weight(1f),
                                color = if (todo.isDone) Color.Red else Color.Unspecified,
                                textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                            IconButton(
                                onClick = {
                                    viewModel.deleteTodo(todo.id)
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "删除")
                            }
                        }
                    }
                }
            }

            // 空列表提示
            if (todos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有待办，添加一个吧！", color = Color.Gray)
                }
            }
        }
    }
}