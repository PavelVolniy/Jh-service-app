package com.example.jhserviceapp.presentation.scan

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R

@Preview(apiLevel = 34)
@Composable
fun PreviewQrCodeAim() {
    QrCodeAim(state = QrScanState.Scanning)
}

@Composable
fun QrCodeAim(state: QrScanState = QrScanState.Wait) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Box {
        if (state is QrScanState.Scanning) {
            Image(
                painter = painterResource(R.drawable.qr_scan_line_aim),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = 0.dp, y = animationValue.dp)
                    .align(Alignment.TopCenter)
                    .padding(10.dp)
            )
        }

        Image(
            painter = when (state) {
                is QrScanState.Scanning -> painterResource(R.drawable.qr_aim_active)
                is QrScanState.Error -> painterResource(R.drawable.qr_aim_error)
                else -> painterResource(R.drawable.qr_aim)
            },
            contentDescription = null,
        )
    }

}