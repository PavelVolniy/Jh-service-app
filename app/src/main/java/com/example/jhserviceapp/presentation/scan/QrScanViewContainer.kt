package com.example.jhserviceapp.presentation.scan

import androidx.compose.runtime.Composable

@Composable
fun QrScanViewContainer(
//    qrScanViewModel: QrScanViewModel,
    onClickManuallyButton: () -> Unit = {},
    onSuccessScan: @Composable () -> Unit,
    onClickGalleryButton: () -> Unit = {},
    onClickDocumentButton: () -> Unit = {},
) {
//    val state = qrScanViewModel.scanState.collectAsState()
//    if (state.value is QrScanState.Success) onSuccessScan()

    QrScanView(
        scanState = QrScanState.Wait,
        onClickManuallyButton = onClickManuallyButton,
        onClickRestartButton = {
//            qrScanViewModel.restartState()
        },
        isDetectedQrCode = { row ->
//            qrScanViewModel.checkQrCode(row)
        },
        onClickGalleryButton = onClickGalleryButton,
        onClickDocumentButton = onClickDocumentButton,
    )
}

