package com.example.jhserviceapp.presentation.addreport.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.domain.entity.article.ArticleWithCount
import com.example.jhserviceapp.presentation.addreport.ArticleRowView

@Composable
fun ArticleBox(list: List<ArticleWithCount>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(list) { ArticleRowView(it) }
    }
}
