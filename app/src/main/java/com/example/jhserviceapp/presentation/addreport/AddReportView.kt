package com.example.jhserviceapp.presentation.addreport

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import com.example.jhserviceapp.domain.entity.article.ArticleWithCount
import com.example.jhserviceapp.domain.entity.report.ReportDTO
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount
import com.example.jhserviceapp.presentation.scan.QrScanState
import com.example.jhserviceapp.presentation.scan.QrScanView
import com.example.jhserviceapp.presentation.util.AnalyserType
import com.example.jhserviceapp.presentation.util.NumberUtil
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Preview(apiLevel = 34)
@Composable
private fun PreviewAddReportView() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AddReportView(null, {}, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportView(
    reportWithArticle: ReportWithArticleAndCount? = null,
    onClickSave: (reportWithArticle: ReportWithArticleAndCount) -> Unit,
    onClickCancel: () -> Unit
) {
    val context = LocalContext.current
    var lifterNumber by remember {
        mutableStateOf(
            reportWithArticle?.report?.numberLoader ?: ""
        )
    }
    var date by remember {
        mutableLongStateOf(
            reportWithArticle?.report?.date ?: System.currentTimeMillis()
        )
    }
    var hours by remember { mutableStateOf(reportWithArticle?.report?.hours?.toString() ?: "") }
    var description by remember { mutableStateOf(reportWithArticle?.report?.description ?: "") }
    var internalComments by remember {
        mutableStateOf(
            reportWithArticle?.report?.internalComments ?: ""
        )
    }
    var articleList by remember { mutableStateOf<List<ArticleWithCount>>(emptyList()) }
    val focus = remember { FocusRequester() }
    var typeScanner by remember { mutableStateOf(AnalyserType.TEXT) }
    val scannerDialogState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var viewScanState by remember { mutableStateOf<QrScanState>(QrScanState.Scanning) }
    var scannerList by remember { mutableStateOf<Set<String>>(emptySet()) }

    Surface(color = colorResource(R.color.jhGrayLight)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.default_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LoaderNumberRow(
                TextFieldValue(
                    text = lifterNumber,
                    selection = if (lifterNumber.isNotEmpty()) TextRange(lifterNumber.length) else TextRange.Zero
                ),
                onValueChanged = { lifterNumber = it },
                focus = focus,
                showCameraDialog = {
                    scope.launch {
                        typeScanner = AnalyserType.LIFT_TRAC
                        scannerDialogState.show()
                    }
                }
            )
            DateHoursRow(
                hours = hours,
                date = date,
                selectedDate = { date = it },
                changeHours = { hours = it },
                focus = focus
            )
            DescriptionBox(
                label = stringResource(R.string.description_text),
                text = description,
                changeText = { description = it },
                onClickScannerText = {
                    scope.launch {
                        typeScanner = AnalyserType.TEXT
                        scannerDialogState.show()
                    }
                }
            )
            DescriptionBox(
                label = stringResource(R.string.internal_comments_hint),
                text = internalComments,
                changeText = { internalComments = it },
                onClickScannerBarCode = {
                    scope.launch {
                        typeScanner = AnalyserType.BAR_CDD
                        scannerDialogState.show()
                    }
                },
                onClickScannerText = {
                    Toast.makeText(context, "В разработке", Toast.LENGTH_SHORT).show()
                },
                barcodeFlag = true
            )
            ArticleBox(articleList)
            ButtonsRow(
                onClickCancel = onClickCancel,
                onClickSave = {
                    onClickSave(
                        ReportWithArticleAndCount(
                            ReportDTO(
                                id = reportWithArticle?.report?.id,
                                numberLoader = lifterNumber,
                                hours = hours.toInt(),
                                date = date,
                                userName = "",
                                userNumber = "",
                                description = description,
                                placeOfOperations = "", typeOfOperations = "",
                                internalComments = internalComments
                            ),
                            articles = articleList
                        )
                    )
                },
                enabled = lifterNumber.isNotEmpty()
                        && lifterNumber.length > 5
                        && hours.isNotEmpty()
                        && description.isNotEmpty()
            )
        }

        if (scannerDialogState.isVisible) {
            ModalBottomSheet(
                containerColor = colorResource(R.color.transparent),
                sheetState = scannerDialogState,
                onDismissRequest = { scope.launch { scannerDialogState.hide() } }
            ) {
                QrScanView(
                    scanState = viewScanState,
                    isDetectedData = { rows ->
                        scope.launch {
                            when (typeScanner) {
                                AnalyserType.BAR_CDD -> {
                                    internalComments += "${rows.first()}\n"
                                    scannerDialogState.hide()
                                }

                                AnalyserType.TEXT -> {
                                    if (rows.isNotEmpty()) {
                                        scannerList = rows.filter { it.length > 4 }.toSet()
                                        description += rows.toString().trim('[', ']')
                                    }
                                    scannerDialogState.hide()
                                }

                                AnalyserType.BAR_COD_DATA -> {
                                    internalComments += "${rows.first()}\n"
                                    scannerDialogState.hide()
                                }

                                AnalyserType.LIFT_TRAC -> {
                                    for (item in rows) {
                                        if (NumberUtil.checkNumber(item)) {
                                            lifterNumber = item
                                            break
                                        }
                                    }
                                    if (lifterNumber.isNotEmpty()) scannerDialogState.hide()
                                }
                            }
                        }
                    },
                    analyserType = typeScanner
                )
            }

        }

    }
}

@Composable
private fun ArticleBox(list: List<ArticleWithCount>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(list) { ArticleRowView(it) }
    }
}

@Composable
fun ButtonsRow(onClickSave: () -> Unit, onClickCancel: () -> Unit, enabled: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        TextButton(
            modifier = Modifier.width(100.dp),
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(1.5.dp, colorResource(R.color.jhGrayMedium)),
            onClick = onClickCancel
        ) {
            Text(
                text = stringResource(R.string.cancel_button),
                color = colorResource(R.color.jhGrayDark)
            )
        }
        TextButton(
            enabled = enabled,
            modifier = Modifier.width(100.dp),
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(
                1.5.dp, if (enabled) colorResource(R.color.jhYellow)
                else colorResource(R.color.jhGrayMedium)
            ),
            onClick = onClickSave
        ) {
            Text(
                text = stringResource(R.string.save_button),
                color = colorResource(R.color.jhGrayDark)
            )
        }
    }
}

@Composable
private fun DescriptionBox(
    label: String,
    text: String,
    changeText: (description: String) -> Unit = {},
    onClickScannerText: () -> Unit = {},
    onClickScannerBarCode: () -> Unit = {},
    barcodeFlag: Boolean = false
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

@Composable
private fun DateHoursRow(
    date: Long,
    hours: String,
    selectedDate: (date: Long) -> Unit = {},
    changeHours: (hours: String) -> Unit = {},
    focus: FocusRequester
) {
    LaunchedEffect(Unit) {
        focus.freeFocus()
    }

    val datePickerState = rememberDatePickerState()
    var isShowDatePicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier.height(58.dp),
            border = BorderStroke(width = 1.3.dp, color = colorResource(R.color.jhGrayMedium)),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = colorResource(R.color.jhGrayLight),
                contentColor = colorResource(R.color.jhTextColorBlack)
            ),
            onClick = { isShowDatePicker = !isShowDatePicker }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Text(text = dateToStringFormat(date))
                }
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    tint = colorResource(R.color.jhGrayDark),
                    contentDescription = null
                )
            }
        }
        if (isShowDatePicker) {
            DatePickerDialog(
                onDismissRequest = { isShowDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDate(datePickerState.selectedDateMillis ?: 0)
                        isShowDatePicker = false
                    }) {
                        Text("ok")
                    }
                }) {
                DatePicker(datePickerState)
            }
        }
        OutlinedTextField(
            modifier = Modifier.width(150.dp),
            value = hours,
            maxLines = 1,
            onValueChange = { if (hours.length < 6) changeHours(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    tint = colorResource(R.color.jhGrayDark),
                    contentDescription = null
                )
            },
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun dateToStringFormat(dateInMillis: Long) = Instant
    .ofEpochMilli(dateInMillis)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()
    .format(DateTimeFormatter.ofPattern("yyyy:MM:dd"))
    .toString()

@Composable
private fun LoaderNumberRow(
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