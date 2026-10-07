package com.example.jhserviceapp.presentation.scan

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jhserviceapp.R
import com.example.jhserviceapp.presentation.util.AnalyserType
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun PreviewQrScanView() {
    QrScanView()
}

@Composable
fun QrScanView(
    scanState: QrScanState = QrScanState.Wait,
    isDetectedData: (rows: List<String>) -> Unit = {},
    analyserType: AnalyserType = AnalyserType.TEXT
) {
    var torch by remember { mutableStateOf(false) }
    var isVisibleHelpInfo by remember { mutableStateOf(true) }
    var qrScanStateText by remember { mutableStateOf("") }
    var qrScanHelpInfo by remember { mutableStateOf("") }

    var selectImage by remember { mutableStateOf<Uri?>(null) }

    when (scanState) {
        QrScanState.Wait -> {
            isVisibleHelpInfo = true
//            qrScanStateText = stringResource(R.string.qr_code_with_description)
//            qrScanHelpInfo = stringResource(R.string.qr_code_help_info)
        }

        QrScanState.Scanning -> {
//            qrScanStateText = stringResource(R.string.scanning_process_qr_scan_text)
            isVisibleHelpInfo = false
        }

        QrScanState.Error -> {
            isVisibleHelpInfo = true
//            qrScanStateText = stringResource(R.string.error_title_qr_code)
//            qrScanHelpInfo = stringResource(R.string.error_description_qr_code)
            selectImage = null
        }

        QrScanState.Success -> {
            //TODO не обязательная часть кода
            isVisibleHelpInfo = true
//            qrScanStateText = stringResource(R.string.qr_code_with_description)
//            qrScanHelpInfo = stringResource(R.string.qr_code_help_info)
        }
    }

    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Box {
            if (selectImage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    AsyncImage(
                        model = selectImage,
                        contentDescription = null
                    )
                }
                BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build()
                ).process(InputImage.fromFilePath(LocalContext.current, selectImage!!))
                    .addOnSuccessListener { barcodes ->
                        val list = mutableListOf<String>()
                        barcodes.forEach { list.add(it.rawValue ?: "") }
                        if (list.isNotEmpty()) isDetectedData(list)
                    }
            } else {
                CameraView(
                    result = { listRow ->
                        listRow.let { if (it.isNotEmpty()) isDetectedData(it) }
                    },
                    torch = torch,
                    analyserType = analyserType
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Scan",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(Modifier.height(30.dp))
                    Text(
                        text = qrScanStateText,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        modifier = Modifier.alpha(if (isVisibleHelpInfo) 1f else 0f),
                        text = qrScanHelpInfo,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(Modifier.height(100.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(205.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "",
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                    )
                    QrCodeAim(state = scanState)
                    Text(
                        text = "",
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(40.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                torch = !torch
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_flash_home_page),
                                    contentDescription = null,
                                    tint = Color.Green
                                )
                            }
                        }
                    }

                }
            }


        }
    }
}