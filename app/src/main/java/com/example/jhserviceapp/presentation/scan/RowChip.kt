package com.example.jhserviceapp.presentation.scan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R

@Preview(apiLevel = 34)
@Composable
private fun PreviewRowChip() {
    RowChip("Some row")
}

@Composable
fun RowChip(row: String, onClockRow: () -> Unit = {}) {
    Surface(
        modifier = Modifier.clickable { onClockRow() },
        shape = RoundedCornerShape(10.dp),
        color = colorResource(R.color.jhGrayMedium)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = row,
            color = colorResource(R.color.jhGrayDark)
        )
    }
}