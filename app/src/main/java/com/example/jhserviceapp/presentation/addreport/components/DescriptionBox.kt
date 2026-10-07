package com.example.jhserviceapp.presentation.addreport.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R

@Composable
fun DescriptionBox(
    label: String,
    text: String,
    changeText: (description: String) -> Unit = {},
    onClickScannerText: () -> Unit = {},
    onClickScannerBarCode: () -> Unit = {},
    barcodeFlag: Boolean = false,
    scanTextFlag: Boolean = true
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = { changeText(it) },
        maxLines = 20,
        label = {
            Text(text = label)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrectEnabled = true,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences
        ),
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (scanTextFlag) {
                    IconButton(onClick = { onClickScannerText() }) {
                        Box {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(R.drawable.qr_aim),
                                contentDescription = "textScanner",
                                tint = colorResource(R.color.black)
                            )
                            Icon(
                                modifier = Modifier.align(Alignment.Center),
                                imageVector = Icons.Default.TextFields,
                                contentDescription = "textScanner",
                                tint = colorResource(R.color.jhGrayDark)
                            )

                        }
                    }
                }
                if (barcodeFlag) {
                    IconButton(onClick = { onClickScannerBarCode() }) {
                        Box {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(R.drawable.qr_aim),
                                contentDescription = "textScanner",
                                tint = colorResource(R.color.black)
                            )
                            Icon(
                                modifier = Modifier.align(Alignment.Center),
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "textScanner",
                                tint = colorResource(R.color.jhGrayDark)
                            )

                        }
                    }
                }
            }

        }
    )
}
