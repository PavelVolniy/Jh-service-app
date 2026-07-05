package com.example.jhserviceapp.presentation.scan.analysers

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.atomic.AtomicBoolean

class TextAnalyser(
    private val onTextRecognized: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Создаем TextRecognizer один раз и переиспользуем
    private val textRecognizer = run {
        try {
            TextRecognition.getClient(
                TextRecognizerOptions.Builder().build()
            )
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
        if (isProcessing.get() || textRecognizer == null) {
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
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                try {
                    // Извлекаем весь распознанный текст
                    val recognizedText = extractText(visionText)

                    // Вызываем callback только если текст не пустой
                    if (recognizedText.isNotBlank()) {
                        onTextRecognized(recognizedText)
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

    private fun extractText(visionText: Text) =
        buildString { append(visionText.text) }.trim()
}