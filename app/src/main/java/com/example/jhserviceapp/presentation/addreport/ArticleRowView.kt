package com.example.jhserviceapp.presentation.addreport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jhserviceapp.R
import com.example.jhserviceapp.domain.entity.article.ArticleCount
import com.example.jhserviceapp.domain.entity.article.ArticleDTO
import com.example.jhserviceapp.domain.entity.article.ArticleWithCount

@Preview(apiLevel = 34)
@Composable
private fun PreviewArticleRowView() {
    ArticleRowView(
        ArticleWithCount(
            ArticleDTO(213215L, "name"),
            ArticleCount(3, 5303, false)
        )
    )
}

@Composable
fun ArticleRowView(article: ArticleWithCount, deleteArticle: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(width = 1.dp, color = colorResource(R.color.jhGrayDark)),
        color = colorResource(R.color.jhGrayLight)
    ) {
        var isPaid by remember { mutableStateOf(article.articleCount.isPaid) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = article.articleDTO.articleId.toString(),
                color = colorResource(R.color.jhTextColorBlack)
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                text = article.articleDTO.articleName,
                color = colorResource(R.color.jhTextColorBlack)
            )
            VerticalDivider()
            Text(
                text = stringResource(R.string.article_count_title_add_form),
                color = colorResource(R.color.jhTextColorBlack)
            )
            Surface(
                color = colorResource(R.color.jhGrayLight),
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(1.dp, colorResource(R.color.jhGrayDark))
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = article.articleCount.articleCountId.toString(),
                    color = colorResource(R.color.jhTextColorBlack)
                )
            }
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(5.dp)
//            ) {
//                Text(text = stringResource(R.string.customer_s_paid_checkbox))
//                Surface(
//                    modifier = Modifier.clickable { isPaid = !isPaid },
//                    color = colorResource(R.color.jhGrayLight),
//                    shape = RoundedCornerShape(2.dp),
//                    border = BorderStroke(
//                        1.dp,
//                        colorResource(
//                            if (isPaid) R.color.jhYellow
//                            else R.color.jhGrayDark
//                        )
//                    )
//                ) {
//                    Icon(
//                        modifier = Modifier.alpha(if (!isPaid) 0F else 1F),
//                        imageVector = Icons.Default.Check,
//                        contentDescription = null,
//                        tint = colorResource(R.color.jhGrayDark)
//                    )
//                }
//            }
            IconButton(
                onClick = { deleteArticle() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete_basket),
                    contentDescription = null,
                    tint = colorResource(R.color.jhGrayDark)
                )
            }
        }
    }
}