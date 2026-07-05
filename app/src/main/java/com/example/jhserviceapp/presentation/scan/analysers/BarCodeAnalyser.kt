package com.example.jhserviceapp.presentation.scan.analysers

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class BarCodeAnalyser(
    private val onBarcodeDetected: (barcodes: String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val oprions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()

    // Создаем TextRecognizer один раз и переиспользуем
    private val barcodeScanner = run {
        try {
            BarcodeScanning.getClient(oprions)
        } catch (e: Exception) {
            Log.e("BarCodeAnalyser", "Failed to initialize TextRecognizer: ${e.message}", e)
            null
        }
    }

    // Флаг для предотвращения параллельной обработки кадров (thread-safe)
    private val isProcessing = AtomicBoolean(false)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // Пропускаем кадры, если предыдущий еще обрабатывается
        if (isProcessing.get() || barcodeScanner == null) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        // Создаем InputImage из MediaImage с учетом поворота
        val image = try {
            InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
        } catch (e: Exception) {
            Log.e("BarCodeAnalyser", "Failed to create InputImage: ${e.message}", e)
            imageProxy.close()
            return
        }

        // Помечаем, что начали обработку (thread-safe)
        if (!isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        // Обрабатываем изображение
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                try {
                    // Извлекаем весь распознанный текст
                    val list = mutableListOf<String>()
                    for (barcode in barcodes) {
                        list.add(barcode.rawValue ?: "")
                    }
                    val recognizedText = list.toString().trim('[', ']')

                    // Вызываем callback только если текст не пустой
                    if (recognizedText.isNotBlank() && recognizedText.length > 1) {
                        onBarcodeDetected(recognizedText)
                    }
                } catch (e: Exception) {
                    Log.e("BarCodeAnalyser", "Error processing recognized text: ${e.message}", e)
                } finally {
                    isProcessing.set(false)
                    imageProxy.close()
                }
            }
            .addOnFailureListener { e ->
                try {
                    Log.e("BarCodeAnalyser", "Error recognizing text: ${e.message}", e)
                } finally {
                    isProcessing.set(false)
                    imageProxy.close()
                }
            }
            // Добавляем обработчик для случаев, когда задача отменена
            .addOnCompleteListener {
                // Убеждаемся, что флаг сброшен даже при отмене
                isProcessing.set(false)
            }
    }
}