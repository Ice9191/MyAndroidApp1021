package com.hxb.myandroidapp_1021.pages

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors

/**
 * AI总结：https://chat.deepseek.com/share/3dcpl80drmt4f551dr
 */

// Logcat 过滤标签
private const val TAG = "CameraXPractice"

@Composable
fun CameraWithFrameLog() {
    val context = LocalContext.current

    // 记录当前是否已获得相机权限
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 权限请求启动器，权限弹窗
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        Log.d(TAG, "相机权限结果: granted=$granted")
    }

    // 首次进入页面时请求相机权限
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // 有权限才创建相机预览
    if (hasPermission) {
        CameraPreviewContent()
    } else {
        Log.d(TAG, "等待相机权限")
    }
}

@Composable
private fun CameraPreviewContent() {
    val context = LocalContext.current
    // 拿到当前 Composable 所在 Activity/Fragment 的生命周期
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    // ImageAnalysis 的分析器在独立线程执行，避免阻塞主线程
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )

    LaunchedEffect(Unit) {
        // 获取 CameraX 的入口，内部管理相机设备
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                // 注册图像分析器
                analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                    logFrameInfo(imageProxy)
                }
            }
        try {
            // 解绑
            cameraProvider.unbindAll()
            // 绑定到 lifecycleOwner 的生命周期
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
            Log.d(TAG, "相机绑定成功")
        } catch (e: Exception) {
            Log.e(TAG, "相机绑定失败", e)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            analysisExecutor.shutdown()
            Log.d(TAG, "分析线程池已关闭")
        }
    }
}

/**
 * 每收到一帧就打印宽、搞、旋转角度
 */
private fun logFrameInfo(imageProxy: ImageProxy) {
    imageProxy.use { imageProxy ->
        val width = imageProxy.width
        val height = imageProxy.height
        // rotationDegrees 表示这一帧相对设备自然方向需要旋转多少度
        val rotation = imageProxy.imageInfo.rotationDegrees
        Log.d(TAG, "帧信息: width=$width, height=$height, rotationDegrees=$rotation")
    }
}