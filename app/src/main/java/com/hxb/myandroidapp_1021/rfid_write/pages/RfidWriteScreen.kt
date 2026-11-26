package com.hxb.myandroidapp_1021.rfid_write.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.rfid_write.models.WriteRequest
import com.hxb.myandroidapp_1021.rfid_write.models.WriteUiState
import kotlin.math.roundToInt

/**
 * RFID 标签写入测试页（只做界面，读写功能未接）
 * 状态与事件全部由外部传入，接入 ViewModel 后直接替换调用方即可。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RfidWriteScreen(
    state: WriteUiState = WriteUiState(),
    onScan: () -> Unit = {},
    onWrite: () -> Unit = {},
    onEpcChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onPowerChange: (Int) -> Unit = {},
    onMessageShown: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // 一次性提示：显示后通知外部清空 message
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message.text)
        onMessageShown()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("RFID 标签写入") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DeviceCard(state)
            TargetCard(state, onScan)
            ParameterCard(state, onEpcChange, onPasswordChange, onPowerChange)
            Button(
                onClick = onWrite,
                enabled = state.canWrite,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isWriting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (state.isWriting) "写入中…" else "写入标签")
            }
        }
    }
}

@Composable
private fun DeviceCard(state: WriteUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("UHF 设备", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = if (state.deviceReady) "已就绪" else "未就绪",
                    color = if (state.deviceReady) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Text(
                text = "当前功率 ${state.powerDbm} dBm",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TargetCard(state: WriteUiState, onScan: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("目标标签", style = MaterialTheme.typography.titleSmall)
            Button(onClick = onScan, enabled = state.canScan) {
                if (state.isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (state.isScanning) "扫描中…" else "扫描标签")
            }
            val tag = state.targetTag
            if (tag == null) {
                Text(
                    text = "尚未锁定标签，按住扫描键或点击上方按钮",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text("EPC：${tag.epc}", fontSize = 13.sp)
                Text(
                    text = "RSSI ${tag.rssi} dBm · 读取 ${tag.readCount} 次 · ${tag.epcByteLength} 字节",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ParameterCard(
    state: WriteUiState,
    onEpcChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPowerChange: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("写入参数", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = state.newEpcInput,
                onValueChange = onEpcChange,
                label = { Text("新 EPC（十六进制）") },
                placeholder = { Text("E28011700000020A1B2C3D4E") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.accessPassword,
                onValueChange = onPasswordChange,
                label = { Text("访问密码（8 位十六进制）") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("发射功率", fontSize = 13.sp)
                Text("${state.powerDbm} dBm", fontSize = 13.sp)
            }
            Slider(
                value = state.powerDbm.toFloat(),
                onValueChange = { onPowerChange(it.roundToInt()) },
                valueRange = WriteRequest.MIN_POWER.toFloat()..WriteRequest.MAX_POWER.toFloat(),
                steps = WriteRequest.MAX_POWER - WriteRequest.MIN_POWER - 1
            )
            Text(
                text = "EPC 载荷 ${WriteRequest.MIN_EPC_BYTES}~${WriteRequest.MAX_EPC_BYTES} 字节",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
