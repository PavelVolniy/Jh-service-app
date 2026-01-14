package com.example.jhserviceapp.presentation.addreport

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jhserviceapp.App
import com.example.jhserviceapp.domain.entity.article.ArticleCount
import com.example.jhserviceapp.domain.entity.article.ArticleCountCrossRef
import com.example.jhserviceapp.domain.entity.article.ArticleDTO
import com.example.jhserviceapp.domain.entity.article.ArticleWithCount
import com.example.jhserviceapp.domain.entity.report.ReportArticleCrossRef
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount
import com.example.jhserviceapp.domain.usecase.AddArtCountCrossRefUseCase
import com.example.jhserviceapp.domain.usecase.AddArticleCountsUseCase
import com.example.jhserviceapp.domain.usecase.AddArticlesUseCase
import com.example.jhserviceapp.domain.usecase.AddReportArticleCrossRefUseCase
import com.example.jhserviceapp.domain.usecase.AddReportUseCase
import com.example.jhserviceapp.domain.usecase.DeleteArticleWithCrossRef
import com.example.jhserviceapp.domain.usecase.DeleteReportArticleCrossRefUseCase
import com.example.jhserviceapp.domain.usecase.GetDefaultArticlesUseCase
import com.example.jhserviceapp.domain.usecase.GetLastReportIdUseCase
import com.example.jhserviceapp.domain.usecase.GetReportByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddReportWithArticleViewModel @Inject constructor(
    private val getDefaultArticlesUseCase: GetDefaultArticlesUseCase,
    private val getReportByIdUseCase: GetReportByIdUseCase,
    private val sharedPreferences: SharedPreferences,
    private val addReportsUseCase: AddReportUseCase,
    private val getLastReportId: GetLastReportIdUseCase,
    private val addReportArticleCrossRefUseCase: AddReportArticleCrossRefUseCase,
    private val addArtCountCrossRefUseCase: AddArtCountCrossRefUseCase,
    private val addArticleCountsUseCase: AddArticleCountsUseCase,
    private val addArticlesUseCase: AddArticlesUseCase,
    private val delArticleCountCrossRef: DeleteArticleWithCrossRef,
    private val delReportArticleCrossRefUseCase: DeleteReportArticleCrossRefUseCase
) : ViewModel() {

    init {
        editableReportArticles()
    }

    private var _articles = MutableStateFlow<List<ArticleWithCount>>(emptyList())
    val articles get() = _articles.asStateFlow()
    private var _editableReport = MutableStateFlow<ReportWithArticleAndCount?>(null)
    val editableReport get() = _editableReport.asStateFlow()
    private var editableReportId: Long? = null
    private var oldReport: ReportWithArticleAndCount? = null

    fun addNewArticleRow() {
        _articles.value += ArticleWithCount(
            articleDTO = ArticleDTO(0, ""),
            articleCount = ArticleCount(0, 0, false)
        )
    }

    fun addReport(
        reportArticleWithCount: ReportWithArticleAndCount,
    ) {
        viewModelScope.launch {
            if (oldReport != null) {
                delArticleCountCrossRef.deleteArticleWithCrossRef(oldReport!!.report.id!!)
                delReportArticleCrossRefUseCase.deleteCrossRef(oldReport!!.report.id!!)
                Log.e("addRep VM", oldReport.toString())
            }

            addReportsUseCase.addReport(reportArticleWithCount.report)
            val listArt =
                reportArticleWithCount.articles
                    .filter { it.articleDTO.articleName.isNotEmpty() }
                    .map { it.articleDTO }
            val listArtCount =
                reportArticleWithCount.articles.map {
                    ArticleCount(
                        it.articleCount.articleCountId,
                        it.articleCount.engineerNumber,
                        it.articleCount.isPaid
                    )
                }
            addArticlesUseCase.addArticles(listArt)
            addArticleCountsUseCase.addArtCounts(listArtCount)
            val listCrossRefArtCount = mutableListOf<ArticleCountCrossRef>()
            val listCrossRefReportArticle = mutableListOf<ReportArticleCrossRef>()
            val reportId: Long = if (editableReportId != null) {
                editableReportId!!
            } else {
                getLastReportId.getLastReportId()
            }

            reportArticleWithCount.articles.forEach {

                listCrossRefArtCount.add(
                    ArticleCountCrossRef(
                        articleId = it.articleDTO.articleId,
                        articleCountId = it.articleCount.articleCountId,
                        byReportId = reportId
                    )
                )
                listCrossRefReportArticle.add(
                    ReportArticleCrossRef(
                        reportId = reportId,
                        articleId = it.articleDTO.articleId
                    )
                )
            }
            addArtCountCrossRefUseCase.addCrossRef(listCrossRefArtCount)
            addReportArticleCrossRefUseCase.addCrossRef(listCrossRefReportArticle)
            sharedPreferences.edit().putString(App.EDITABLE_REPORT, null).apply()
        }
    }


    fun removeArticleRow(article: ArticleWithCount) {
        _articles.value -= article
    }

    fun addDefaultArticlesToArticleList() {
        viewModelScope.launch {
            val defaultArticles = getDefaultArticlesUseCase.getDefaultArticles()
            defaultArticles.forEach { defaultArticle ->
                _articles.value += ArticleWithCount(defaultArticle, ArticleCount(1, 0, false))
            }
        }
    }

    private fun editableReportArticles() {
        viewModelScope.launch {
            val idString = sharedPreferences.getString(App.EDITABLE_REPORT, null)
            if (!idString.isNullOrEmpty()) {
                Log.e("editableReportArticles VM", editableReportId.toString())
                editableReportId = idString.toLong()
                oldReport = getReportByIdUseCase.getReportById(editableReportId!!)
                _editableReport.value = oldReport
                _articles.value = oldReport?.articles ?: emptyList()
            } else {
                editableReportId = null
                oldReport = null
            }
        }
    }

}