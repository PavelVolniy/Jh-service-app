package com.example.jhserviceapp.presentation.addreport

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import com.example.jhserviceapp.domain.entity.article.ArticleWithCount
import com.example.jhserviceapp.domain.entity.report.ReportDTO
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount
import com.example.jhserviceapp.presentation.addreport.components.ArticleBox
import com.example.jhserviceapp.presentation.addreport.components.DateHoursRow
import com.example.jhserviceapp.presentation.addreport.components.DescriptionBox
import com.example.jhserviceapp.presentation.addreport.components.LoaderNumberRow
import com.example.jhserviceapp.presentation.scan.QrScanState
import com.example.jhserviceapp.presentation.scan.QrScanView
import com.example.jhserviceapp.presentation.util.AnalyserType
import com.example.jhserviceapp.presentation.util.NumberUtil
import kotlinx.coroutines.launch

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
    var companyName by remember {
        mutableStateOf(
            reportWithArticle?.report?.placeOfOperations ?: ""
        )
    }
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
                label = stringResource(R.string.company_label_text),
                text = companyName,
                changeText = { companyName = it },
                scanTextFlag = false
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
                                placeOfOperations = companyName,
                                typeOfOperations = "",
                                internalComments = internalComments,
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
                                        description += scannerList.toString().trim('[', ']')
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
