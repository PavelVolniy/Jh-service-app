package com.example.jhserviceapp.presentation.addreport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R

@Preview
@Composable
private fun PreviewScannerData() {
    ScannerData(
        listOf(
            "test 1",
            "test 2",
            "test ",
            "test 4",
            "test 5",
        )
    )
}

@Composable
fun ScannerData(
    dataList: List<String>, onClickSelected: (list: List<String>) -> Unit = {},
    onClickCancel: () -> Unit = {}
) {
    val selectedList = remember { mutableStateListOf<String>() }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(dataList) {
                Surface(
                    color = if (selectedList.contains(it)) colorResource(R.color.jhYellow)
                    else colorResource(R.color.jhGrayLight),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        2.dp,
                        color = if (selectedList.contains(it)) colorResource(R.color.jhYellow)
                        else colorResource(R.color.jhTextColorBlack)
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                if (!selectedList.contains(it)) selectedList.add(it)
                                else selectedList.remove(it)
                            },
                        text = it,
                        color = colorResource(R.color.jhTextColorBlack)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(bottom = 20.dp, end = 20.dp)
                .align(Alignment.BottomEnd),
        ) {
            TextButton(

                onClick = { onClickCancel() }
            ) {
                Text(
                    text = "cancel",
                    color = colorResource(R.color.jhGrayLight)
                )
            }
            TextButton(
                onClick = { onClickSelected(selectedList) }
            ) {
                Text(
//                    text = stringResource(R.string.selected_button_text),
                    text = "select",
                    color = colorResource(R.color.jhGrayLight)
                )
            }
        }
    }
}