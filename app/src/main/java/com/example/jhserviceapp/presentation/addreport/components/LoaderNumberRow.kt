package com.example.jhserviceapp.presentation.addreport.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R

@Composable
fun LoaderNumberRow(
    lifterNumber: TextFieldValue,
    onValueChanged: (text: String) -> Unit,
    focus: FocusRequester,
    showCameraDialog: () -> Unit = {}
) {
    var isFnType by remember { mutableStateOf(false) }
    var isStringType by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        focus.requestFocus()
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focus),
        value = lifterNumber,
        onValueChange = {
            onValueChanged(it.text)
            isError = isFnType && (it.text.length < 6 || it.text.length > 8)
        },
        shape = RoundedCornerShape(5.dp),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (!isStringType) KeyboardType.Number
            else KeyboardType.Text
        ),
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    text = if (lifterNumber.text.length < 6) stringResource(R.string.min_length_8_symbols)
                    else if (lifterNumber.text.length > 8) stringResource(R.string.length_should_be_8_symbols)
                    else ""
                )
            }
        },
        leadingIcon = {
            Text(
                text = stringResource(R.string.number_text),
                color = colorResource(R.color.jhTextColorBlack),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    shape = RoundedCornerShape(10.dp),
                    onClick = { showCameraDialog() }) {
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
                            modifier = Modifier
                                .padding(5.dp)
                                .align(Alignment.Center),
                            imageVector = Icons.Default.TextFields,
                            contentDescription = "qrIcon",
                            tint = colorResource(R.color.jhGrayDark)
                        )
                    }
                }

                IconButton(onClick = {
                    if (!isFnType) {
                        isFnType = true
                        onValueChanged("FN${lifterNumber.text}")
                    } else {
                        isFnType = false
                        onValueChanged(lifterNumber.text.removePrefix("FN"))
                    }
                }) {
                    Surface(
                        color = colorResource(R.color.jhGrayLight),
                        shape = RoundedCornerShape(5.dp),
                        border = BorderStroke(
                            2.dp,
                            color = colorResource(R.color.jhGrayDark)
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(31.dp),
                            painter = painterResource(R.drawable.ic_fn_button),
                            contentDescription = null
                        )
                    }
                }
                IconButton(onClick = { isStringType = !isStringType }) {
                    Surface(
                        color = colorResource(R.color.jhGrayLight),
                        shape = RoundedCornerShape(5.dp),
                        border = BorderStroke(
                            2.dp,
                            color = colorResource(R.color.jhGrayDark)
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(3.dp),
                            painter = painterResource(R.drawable.ic_list_button),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    )
}