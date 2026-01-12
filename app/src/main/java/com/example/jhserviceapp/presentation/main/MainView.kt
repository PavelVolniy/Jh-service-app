package com.example.jhserviceapp.presentation.main

import android.icu.util.Calendar
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import com.example.jhserviceapp.domain.entity.report.ReportDTO
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount


@Preview(apiLevel = 34)
@Composable
private fun PreviewMainView() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        MainView(
            listReport = listOf(
                ReportWithArticleAndCount(
                    ReportDTO(
                        numberLoader = "131321321",
                        date = Calendar.getInstance().timeInMillis,
                        hours = 1200,
                        description = "some text some textsome textsome textsome textsome textsome textsome text",
                        userName = "",
                        userNumber = "",
                        placeOfOperations = "",
                        typeOfOperations = "",
                        internalComments = ""
                    ), articles = emptyList()
                ),
                ReportWithArticleAndCount(
                    ReportDTO(
                        numberLoader = "131321321",
                        date = Calendar.getInstance().timeInMillis,
                        hours = 1200,
                        description = "some text some textsome textsome textsome textsome textsome textsome text",
                        userName = "",
                        userNumber = "",
                        placeOfOperations = "",
                        typeOfOperations = "",
                        internalComments = ""
                    ), articles = emptyList()
                )
            )
        )
    }
}

@Composable
fun MainView(
    onFilteredTextChanged: (text: String) -> Unit = {},
    onClickAdd: () -> Unit = {},
    listReport: List<ReportWithArticleAndCount> = emptyList()
) {
    var filterText by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.jhYellow)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.default_padding)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_filter),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    value = filterText,
                    onValueChange = {
                        filterText = it
                        onFilteredTextChanged(it)
                    },
                    trailingIcon = {
                        if (filterText.isNotEmpty())
                            Icon(
                                modifier = Modifier.clickable {
                                    filterText = ""
                                },
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                            )
                    }
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(listReport) { item ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            SwipeableItem(onSwipeToDelete = {}) {
                                ReportItemRowView(report = item.report)
                            }
                        }
                    }
                }
            }
            IconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onClickAdd
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colorResource(R.color.jhYellow),
                    border = BorderStroke(2.dp, color = colorResource(R.color.jhTextColorBlack))
                ) {
                    Icon(
                        modifier = Modifier.padding(5.dp),
                        painter = painterResource(R.drawable.ic_plus_square),
                        contentDescription = null
                    )
                }
            }
        }
    }
}