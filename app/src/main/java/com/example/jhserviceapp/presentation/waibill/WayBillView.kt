package com.example.jhserviceapp.presentation.waibill

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import com.example.jhserviceapp.presentation.scan.CameraView
import com.example.jhserviceapp.presentation.util.AnalyserType
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun PreviewWayBillView() {
    WayBillView()
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WayBillView(onclickBack: () -> Unit = {}) {
    val list = remember { mutableListOf<String>() }
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = { onclickBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = colorResource(R.color.black)
                    )
                }
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.waybill_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.black)
                )
            }
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { scope.launch { state.show() } }) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = colorResource(R.color.jhGrayLight)
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 10.dp
                            ),
                            text = stringResource(R.string.scan_barcode_button),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorResource(R.color.jhTextColorBlack)
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val scrollState = rememberScrollState()
                FlowRow(
                    modifier = Modifier.verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    list.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colorResource(R.color.jhGrayMedium)
                        ) {
                            Text(
                                modifier = Modifier.padding(10.dp),
                                text = item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorResource(R.color.black)
                            )
                        }
                    }
                }

                if (state.isVisible) {
                    ModalBottomSheet(
                        sheetState = state,
                        onDismissRequest = { scope.launch { state.hide() } },
                        containerColor = colorResource(R.color.transparent)
                    ) {
                        CameraView(
                            result = { listString ->
                                listString.forEach {
                                    list.add(it.replace('\"', ' '))
                                }
                                scope.launch { state.hide() }
                            },
                            analyserType = AnalyserType.BAR_CDD,
                            torch = false
                        )
                    }
                }
            }
        })
}