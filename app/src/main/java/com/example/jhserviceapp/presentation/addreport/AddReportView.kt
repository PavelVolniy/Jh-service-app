package com.example.jhserviceapp.presentation.addreport

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import com.example.jhserviceapp.domain.entity.article.ArticleWithCount
import com.example.jhserviceapp.domain.entity.report.ReportDTO
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount
import com.example.jhserviceapp.presentation.scan.QrScanViewContainer
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
//    val description = rememberTextFieldState()
    var internalComments by remember {
        mutableStateOf(
            reportWithArticle?.report?.internalComments ?: ""
        )
    }
    var articleList by remember { mutableStateOf<List<ArticleWithCount>>(emptyList()) }
    var isShowScan by remember { mutableStateOf(false) }
    val focus = remember { FocusRequester() }
    Surface(color = colorResource(R.color.jhGrayLight)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.default_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LoaderNumberRow(lifterNumber, onValueChanged = { lifterNumber = it }, focus = focus)
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
            )
            DescriptionBox(
                label = stringResource(R.string.internal_comments_hint),
                text = internalComments,
                changeText = { internalComments = it },
            )
            ArticleBox(articleList)
            ButtonsRow(
                onClickCancel = onClickCancel, onClickSave = {
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
//            TextButton(
//                shape = RoundedCornerShape(10.dp),
//                border = BorderStroke(1.5.dp, color = colorResource(R.color.jhGrayDark)),
//                onClick = { isShowScan = !isShowScan }) {
//                Text(text = "scan")
//            }
            if (isShowScan) {
                val state = rememberModalBottomSheetState(true)
                ModalBottomSheet(
                    sheetState = state,
                    onDismissRequest = { isShowScan = false }
                ) {
                    QrScanViewContainer()
                }
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
        )
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
                    contentDescription = null
                )
            }
        }
        if (isShowDatePicker) {
            DatePickerDialog(onDismissRequest = { isShowDatePicker = false },
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
            leadingIcon = { Text(text = "H") },
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
    lifterNumber: String, onValueChanged: (text: String) -> Unit,
    focus: FocusRequester
) {
    var isFnType by remember { mutableStateOf(false) }
    var isStringType by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf(TextFieldValue(lifterNumber)) }
    LaunchedEffect(Unit) {
        focus.requestFocus()
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focus),
        value = textState.copy(selection = TextRange(textState.text.length)),
        onValueChange = {
            onValueChanged(it.text)
            isError = isFnType && (it.text.length < 6 || it.text.length > 8)
            textState = it
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
                    text = if (lifterNumber.length < 6) stringResource(R.string.min_length_8_symbols)
                    else if (lifterNumber.length > 8) stringResource(R.string.length_should_be_8_symbols)
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
            Row {
                IconButton(onClick = {}) {
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
                                .size(31.dp)
                                .clickable {
                                    if (!isFnType) {
                                        isFnType = true
                                        onValueChanged("FN$lifterNumber")
                                        textState = TextFieldValue(
                                            text = "FN$lifterNumber",
                                            selection = TextRange(textState.selection.end)
                                        )
                                    } else {
                                        isFnType = false
                                        onValueChanged(lifterNumber.removePrefix("FN"))
                                        textState = TextFieldValue(
                                            text = lifterNumber.removePrefix("FN"),
                                            selection = TextRange(textState.selection.end)
                                        )
                                    }
                                },
                            painter = painterResource(R.drawable.ic_fn_button),
                            contentDescription = null
                        )
                    }
                }
                IconButton(onClick = {}) {
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
                                .padding(3.dp)
                                .clickable { isStringType = !isStringType },
                            painter = painterResource(R.drawable.ic_list_button),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    )
}