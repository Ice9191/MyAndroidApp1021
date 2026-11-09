package com.hxb.myandroidapp_1021.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RfidMemoryArea(val label: String) {
    EPC("EPC 区"),
    USER("User 区")
}

enum class RfidDataFormat(val label: String) {
    HEX("Hex"),
    ASCII("ASCII")
}

data class RfidLog(
    val time: String,
    val message: String,
    val isError: Boolean = false
)

@Composable
fun WriteRfidTestPage() {
    var epcValue by remember { mutableStateOf("E28011700000020A1B2C3D4E") }
    var userValue by remember { mutableStateOf("TEST-USER-001") }
    var selectedArea by remember { mutableStateOf(RfidMemoryArea.EPC) }
    var selectedFormat by remember { mutableStateOf(RfidDataFormat.HEX) }
    var simulateFailure by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var lastReadValue by remember { mutableStateOf<String?>(null) }

    val logs = remember { mutableStateListOf<RfidLog>() }
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    fun log(message: String, isError: Boolean = false) {
        logs.add(0, RfidLog(timeFormat.format(Date()), message, isError))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "RFID 写入测试",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "测试前请只放一张标签靠近天线，并适当降低读写功率。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("写入区域", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RfidMemoryArea.entries.forEach { area ->
                            FilterChip(
                                selected = selectedArea == area,
                                onClick = {
                                    if (!busy) {
                                        selectedArea = area
                                        lastReadValue = null
                                    }
                                },
                                label = { Text(area.label) },
                                enabled = !busy
                            )
                        }
                    }

                    Text("数据格式", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RfidDataFormat.entries.forEach { format ->
                            FilterChip(
                                selected = selectedFormat == format,
                                onClick = { if (!busy) selectedFormat = format },
                                label = { Text(format.label) },
                                enabled = !busy
                            )
                        }
                    }

                    OutlinedTextField(
                        value = epcValue,
                        onValueChange = { epcValue = it },
                        label = { Text("EPC 数据") },
                        singleLine = true,
                        enabled = !busy,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = userValue,
                        onValueChange = { userValue = it },
                        label = { Text("User 数据") },
                        singleLine = true,
                        enabled = !busy,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "当前将写入：${selectedArea.label} / ${selectedFormat.label}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("模拟写入失败")
                        Switch(
                            checked = simulateFailure,
                            onCheckedChange = { if (!busy) simulateFailure = it },
                            enabled = !busy
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val data = if (selectedArea == RfidMemoryArea.EPC) epcValue else userValue
                        log("点击写入：${selectedArea.label} / ${selectedFormat.label}，数据：$data")
                    },
                    enabled = !busy,
                    modifier = Modifier.weight(1f)
                ) {
                    if (busy) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("写入并回读校验")
                    }
                }

                OutlinedButton(
                    onClick = {
                        log("点击读取：${selectedArea.label}")
                    },
                    enabled = !busy,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("读取当前区")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("最近回读结果", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = lastReadValue ?: "暂无",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("操作日志", fontWeight = FontWeight.SemiBold)
                TextButton(
                    onClick = { logs.clear() },
                    enabled = logs.isNotEmpty()
                ) {
                    Text("清空")
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                Text(
                    text = "暂无日志",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(logs) { log ->
                Text(
                    text = "[${log.time}] ${log.message}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = if (log.isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}