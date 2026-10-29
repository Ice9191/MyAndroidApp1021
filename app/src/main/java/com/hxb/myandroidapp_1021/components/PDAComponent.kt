package com.hxb.myandroidapp_1021.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 一条扫码记录
data class ScanRecord(
    val barcode: String,
    val timeText: String
)

class UTouchScanReceiver(
    val onScanResult: (barcode: String) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("UTouchScanReceiver onReceive: $intent")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDAPage() {
    var currentBarcode by remember { mutableStateOf<String?>(null) }
    var currentTime by remember { mutableStateOf("") }
    val history = remember { mutableListOf<ScanRecord>() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDA扫码", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        history.clear()
                        currentBarcode = null
                        currentTime = ""
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "清空")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            // 当前扫码结果卡片
            /*CurrentResultCard(
                barcode = currentBarcode,
                timeText = currentTime,
                onCopy = {}
            )*/
            Spacer(Modifier.height(16.dp))
            // 历史列表标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("扫码记录", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    "${history.size}条",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            // 历史列表（空状态）
            if (history.isEmpty()) {
                EmptyHint(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(history) { index, record ->
                        HistoryItem(
                            index = history.size - index,
                            record = record,
                            onClick = {}
                        )
                    }
                }
            }
            // 底部手动触发按钮
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("手动触发扫描")
            }
        }
    }
}

@Composable
fun HistoryItem(index: Int, record: ScanRecord, onClick: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun EmptyHint(modifier: Modifier) {
    TODO("Not yet implemented")
}