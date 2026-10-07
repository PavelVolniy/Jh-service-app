package com.example.jhserviceapp.presentation.addreport.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DateHoursRow(
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
