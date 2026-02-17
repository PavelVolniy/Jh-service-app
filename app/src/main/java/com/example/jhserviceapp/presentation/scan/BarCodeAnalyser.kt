package com.example.jhserviceapp.presentation.scan

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.atomic.AtomicBoolean

class BarCodeAnalyser(
    private val onTextRecognized: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Создаем TextRecognizer один раз и переиспользуем
    private val textRecognizer = run {
        try {
//             Используйте эту версию для английского и базовых языков
            com.google.mlkit.vision.text.TextRecognition.getClient(
                com.google.mlkit.vision.text.latin.TextRecognizerOptions.Builder().build()
            )

            // Или используйте эту версию для поддержки русского и других языков:
//            com.google.mlkit.vision.text.TextRecognition.getClient(
//                com.google.mlkit.vision.text.latin.TextRecognizerOptions.Builder()
//                    .build()
//            )
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
            com.google.mlkit.vision.common.InputImage.fromMediaImage(
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

    /**
     * Извлекает текст из результата распознавания ML Kit
     *
     * Можно настроить различные стратегии извлечения:
     * - Весь текст одной строкой
     * - Только блоки текста
     * - Только строки
     * - С сохранением структуры (абзацы, строки)
     */
    private fun extractText(visionText: com.google.mlkit.vision.text.Text): String {
        return buildString {
            // Вариант 1: Весь текст одной строкой (по умолчанию)
            append(visionText.text)

            // Вариант 2: Текст с сохранением структуры (раскомментируйте, если нужно):
            /*
            visionText.textBlocks.forEach { block ->
                block.lines.forEach { line ->
                    line.elements.forEach { element ->
                        append(element.text)
                        append(" ")
                    }
                    append("\n")
                }
                append("\n")
            }
            */

            // Вариант 3: Только строки без элементов (раскомментируйте, если нужно):
//            visionText.textBlocks.forEach { block ->
//                block.lines.forEach { line ->
//                    append(line.text)
//                    append("\n")
//                }
//            }
        }.trim()
    }
}
