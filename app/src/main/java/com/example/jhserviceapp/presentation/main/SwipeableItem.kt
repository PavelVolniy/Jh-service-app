package com.example.jhserviceapp.presentation.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableItem(
    onSwipeToDelete: () -> Unit,
    swipeThreshold: androidx.compose.ui.unit.Dp = 100.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { swipeThreshold.toPx() }
    val coroutineScope = rememberCoroutineScope()

    val animatableOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    var currentOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(animatableOffset.value.roundToInt(), 0) }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        // Разрешаем только свайп влево (отрицательные значения)
                        val newOffset = (animatableOffset.value + delta).coerceAtMost(0f)
                        animatableOffset.snapTo(newOffset)
                        currentOffset = newOffset
                    }
                },
                onDragStopped = {
                    coroutineScope.launch {
                        // Если свайпнули достаточно далеко - удаляем
                        if (currentOffset <= -swipeThresholdPx) {
                            animatableOffset.animateTo(
                                targetValue = -1000f,
                                animationSpec = tween(300)
                            )
                            onSwipeToDelete()
                            animatableOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            )
                        } else {
                            // Возвращаем на место
                            animatableOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            )
                            currentOffset = 0f
                        }
                    }
                }
            )
    ) {
        content()
    }
}