package com.example.jhserviceapp.presentation.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R

@Preview
@Composable
private fun PreviewDeleteDialogView() {
    DeleteDialogView()
}

@Composable
fun DeleteDialogView(onClickDelete: () -> Unit = {}, onClickCancel: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = colorResource(R.color.transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.do_you_want_delete_text),
                color = colorResource(id = R.color.jhTextColorWhite)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = onClickCancel) {
                    Text(
                        text = stringResource(id = R.string.cancel_dialog_button),
                        color = colorResource(id = R.color.jhTextColorWhite)
                    )
                }
                TextButton(onClick = onClickDelete) {
                    Text(
                        text = stringResource(id = R.string.delete_dialog_button),
                        color = colorResource(id = R.color.jhTextColorWhite)
                    )
                }
            }
        }
    }
}