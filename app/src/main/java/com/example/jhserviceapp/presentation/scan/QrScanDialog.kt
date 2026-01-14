package com.example.jhserviceapp.presentation.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R


@Preview(apiLevel = 34)
@Composable
fun PreviewQrScanDialog() {
    QrScanDialog()
}

@Composable
fun QrScanDialog(
    onClickGalleryButton: () -> Unit = {},
    onClickDocumentButton: () -> Unit = {},
    onClickCancelButton: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colorResource(R.color.transparent),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClickGalleryButton,
                shape = RectangleShape,
                colors = ButtonColors(
                    contentColor = colorResource(R.color.jhGrayLight),
                    containerColor = colorResource(R.color.jhGrayLight),
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Scan")
            }
//            Button(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = onClickDocumentButton,
//                shape = RectangleShape,
//                colors = ButtonColors(
//                    contentColor = ColorScannerQrCodeSecondary,
//                    containerColor = ColorBackGroundCamera,
//                    disabledContentColor = Color.Gray,
//                    disabledContainerColor = Color.Gray
//                )
//            ) {
//                Text(text = stringResource(R.string.qr_scan_document_button))
//            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClickCancelButton,
                shape = RectangleShape,
                colors = ButtonColors(
                    contentColor = colorResource(R.color.jhGrayLight),
                    containerColor = colorResource(R.color.jhGrayLight),
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "stringResource(R.string.qr_scan_cancel_button)")
            }
        }
    }
}