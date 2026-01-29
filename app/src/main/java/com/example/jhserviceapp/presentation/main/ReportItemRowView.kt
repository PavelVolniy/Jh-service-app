package com.example.jhserviceapp.presentation.main

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import com.example.jhserviceapp.domain.entity.Report
import com.example.jhserviceapp.domain.entity.report.ReportDTO
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Preview(apiLevel = 34)
@Composable
private fun PreviewReportItemRowView() {
    ReportItemRowView(
        report = ReportDTO(
            numberLoader = "131321321",
            date = Calendar.getInstance().timeInMillis,
            hours = 1200,
            description = "some text some textsome textsome textsome textsome textsome textsome text",
            userName = "",
            userNumber = "",
            placeOfOperations = "",
            typeOfOperations = "",
            internalComments = "some internal comments"
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportItemRowView(
    report: Report,
    onClickShare: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val date = Instant
        .ofEpochMilli(report.date)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy:MM:dd"))
        .toString()
    val colorTitle = colorResource(R.color.jhTextColorBlack)
    var onClickText by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, color = colorResource(R.color.black))
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onLongClick = { onLongClick() },
                    interactionSource = null,
                    indication = null,
                    enabled = true,
                    onClickLabel = null,
                    role = null,
                    onLongClickLabel = null,
                    onDoubleClick = null,
                    hapticFeedbackEnabled = true,
                    onClick = {},
                )
                .background(colorResource(R.color.jhGrayLight))
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "№ ${report.numberLoader}",
                    color = colorTitle,
                )
                Text(
                    text = "H ${report.hours}",
                    color = colorTitle
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = date,
                    color = colorTitle
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .clickable { onClickText = !onClickText }
                        .fillMaxWidth()
                        .background(colorResource(R.color.jhGrayDark))
                        .padding(vertical = 20.dp, horizontal = 10.dp),
                    text = report.description,
                    color = colorResource(R.color.white),
                    overflow = if (!onClickText) TextOverflow.Ellipsis else TextOverflow.Visible,
                    maxLines = if (!onClickText) 1 else Int.MAX_VALUE
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clickable { onClickShare() },
                    painter = painterResource(R.drawable.ic_share_button),
                    contentDescription = null,
                    tint = colorResource(R.color.jhYellow)
                )
            }
            if (onClickText) {
                HorizontalDivider()
                Text(
                    modifier = Modifier
                        .clickable { onClickText = !onClickText }
                        .fillMaxWidth()
                        .background(colorResource(R.color.jhGrayDark))
                        .padding(vertical = 20.dp, horizontal = 10.dp),
                    text = report.internalComments,
                    color = colorResource(R.color.white),
                    overflow = if (!onClickText) TextOverflow.Ellipsis else TextOverflow.Visible,
                    maxLines = if (!onClickText) 1 else Int.MAX_VALUE
                )
            }
        }
    }
}