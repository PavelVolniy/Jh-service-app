package com.example.jhserviceapp.presentation.scan

sealed class QrScanState {
    data object Scanning : QrScanState()
    data object Error : QrScanState()
    data object Wait : QrScanState()
    data object Success: QrScanState()
}