package com.example.jhserviceapp.presentation.scan

import androidx.compose.runtime.Composable
import com.example.jhserviceapp.presentation.util.AnalyserType

@Composable
fun QrScanViewContainer(
    onSuccessResult: (result: String) -> Unit = {},
    analyserType: AnalyserType
) {
    QrScanView(
        scanState = QrScanState.Wait,
        isDetectedQrCode = { row ->
            onSuccessResult(row)
        },
        analyserType = analyserType
        )
}

