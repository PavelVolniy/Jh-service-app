package com.example.jhserviceapp.presentation.scan

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    result: (row: String) -> Unit,
    torch: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Состояние для отслеживания инициализации и управления камерой
    var isInitialized by remember { mutableStateOf(false) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    // Executor для анализа изображений - создается один раз и очищается при dispose
    val cameraExecutor: ExecutorService = remember {
        Executors.newSingleThreadExecutor()
    }

    // Флаг для предотвращения множественных вызовов result для одного QR кода
    var lastScannedCode by remember { mutableStateOf<String?>(null) }

    // Очистка ресурсов при выходе из композиции
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProvider?.unbindAll()
                cameraExecutor.shutdown()
            } catch (e: Exception) {
                Log.e("CameraView", "Error during cleanup: ${e.localizedMessage}", e)
            }
        }
    }

    // Реактивное обновление фонарика при изменении параметра torch
    LaunchedEffect(camera, torch) {
        try {
            camera?.cameraControl?.enableTorch(torch)
        } catch (e: Exception) {
            Log.e("CameraView", "Error setting torch: ${e.localizedMessage}", e)
        }
    }

    AndroidView(
        factory = { androidViewContext ->
            PreviewView(androidViewContext).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { previewView ->
            // Инициализация выполняется только один раз при первом создании View
            if (!isInitialized) {
                isInitialized = true

                initializeCamera(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    previewView = previewView,
                    cameraExecutor = cameraExecutor,
                    onCameraInitialized = { provider, cam ->
                        cameraProvider = provider
                        camera = cam
                    },
                    onResult = { barcodeValue ->
                        // Предотвращаем множественные вызовы для одного QR кода
                        if (lastScannedCode != barcodeValue) {
                            lastScannedCode = barcodeValue
                            result(barcodeValue)
                        }
                    },
                    onError = { error ->
                        Log.e(
                            "CameraView",
                            "Camera initialization error: ${error.localizedMessage}",
                            error
                        )
                    }
                )
            }
        }
    )
}

private fun initializeCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    cameraExecutor: ExecutorService,
    onCameraInitialized: (ProcessCameraProvider, Camera) -> Unit,
    onResult: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    val cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        try {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Настройка Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            // Настройка анализатора изображений для QR/баркодов
            // Примечание: BarCodeAnalyser должен быть реализован с использованием
            // ML Kit или другой библиотеки для распознавания баркодов
            val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                onResult(barcodes)
            }

            val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                }

            // Отвязываем все предыдущие use cases перед привязкой новых
            cameraProvider.unbindAll()

            // Привязываем use cases к lifecycle
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )

            onCameraInitialized(cameraProvider, camera)

        } catch (e: Exception) {
            onError(e)
        }
    }, ContextCompat.getMainExecutor(context))
}
