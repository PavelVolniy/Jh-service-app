package com.example.jhserviceapp.presentation.scan.analysers

import android.graphics.ImageFormat
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.atomic.AtomicBoolean

class TextAnalyser(
    private val onTextRecognized: (List<String>) -> Unit
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
        val width = mediaImage.width
        val height = mediaImage.height
        val roiRect = Rect(width / 3f, height / 3f, width - (width / 3f), height - (height / 3f))


        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        if (!isSharpInRoi(imageProxy, roiRect, threshold = 1700f)) { // threshold четкость кадров 500-1200 размыто, 4000+ очень резко
            // Кадр размыт — просто закрываем и ждём следующий
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
//                    val recognizedText = extractText(visionText)
                    val recognizedText = filterByRegion(visionText, roiRect)

                    // Вызываем callback только если текст не пустой
                    if (recognizedText.isNotBlank()) {
                        onTextRecognized(
                            recognizedText.split("\\s+".toRegex()).filter { it.isNotEmpty() })
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

    private fun isSharpInRoi(
        imageProxy: ImageProxy,
        roiRect: Rect,
        threshold: Float
    ): Boolean {
        val image = imageProxy.image ?: return false
        if (image.format != ImageFormat.YUV_420_888) return false

        val yPlane = image.planes[0]
        val yBuffer = yPlane.buffer
        val yStride = yPlane.rowStride
        val pixelStride = yPlane.pixelStride

        // ROI в координатах Y-плоскости (они совпадают с пикселями)
        val left = maxOf(0, roiRect.left.toInt())
        val top = maxOf(0, roiRect.top.toInt())
        val right = minOf(image.width, roiRect.right.toInt())
        val bottom = minOf(image.height, roiRect.bottom.toInt())

        if (left >= right || top >= bottom) return false

        var sum = 0L
        var sumSq = 0L
        var count = 0

        for (y in top until bottom) {
            val rowOffset = y * yStride
            for (x in left until right) {
                // YUV_420_888: Y-канал — каждый байт это яркость 0..255
                val index = rowOffset + x * pixelStride
                val brightness = yBuffer.get(index).toInt() and 0xFF

                sum += brightness.toLong()
                sumSq += (brightness.toLong() * brightness)
                count++
            }
        }

        if (count == 0) return false

        val mean = sum.toDouble() / count
        val variance = (sumSq.toDouble() / count) - (mean * mean)

        return variance > threshold
    }

    private fun filterByRegion(
        visionText: Text,
        roiRect: Rect
    ): String {
        val result = StringBuilder()
        for (block in visionText.textBlocks) {
            val box = block.boundingBox ?: continue
            if (!box.intersects(
                    roiRect.left.toInt(),
                    roiRect.top.toInt(),
                    roiRect.right.toInt(),
                    roiRect.bottom.toInt()
                )
            ) continue

            for (line in block.lines) {
                val lineBox = line.boundingBox ?: continue
                if (!lineBox.intersects(
                        roiRect.left.toInt(),
                        roiRect.top.toInt(),
                        roiRect.right.toInt(),
                        roiRect.bottom.toInt()
                    )
                ) continue

                val words = line.elements.filter {
                    it.boundingBox?.intersects(
                        roiRect.left.toInt(),
                        roiRect.top.toInt(),
                        roiRect.right.toInt(),
                        roiRect.bottom.toInt()
                    ) == true
                }
                if (words.isNotEmpty()) {
                    result.appendLine(words.joinToString(" ") { it.text })
                }
            }
        }
        return result.toString().trim()
    }

    private fun extractText(visionText: Text) =
        buildString { append(visionText.text) }.trim()
}