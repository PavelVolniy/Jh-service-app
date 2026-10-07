package com.example.jhserviceapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount
import com.example.jhserviceapp.domain.usecase.DeleteReportArticleCrossRefUseCase
import com.example.jhserviceapp.domain.usecase.DeleteReportUseCase
import com.example.jhserviceapp.domain.usecase.GetAllReportsUseCase
import com.example.jhserviceapp.domain.usecase.GetLast20ReportsUseCase
import com.example.jhserviceapp.domain.usecase.GetReportByCompanyUseCase
import com.example.jhserviceapp.domain.usecase.GetReportByDescriptionUseCase
import com.example.jhserviceapp.domain.usecase.GetReportByInternalCommentsUseCase
import com.example.jhserviceapp.domain.usecase.GetReportBySerialNumberUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReportViewModel @Inject constructor(
    private val getLast20ReportsUseCase: GetLast20ReportsUseCase,
    private val deleteReportUseCase: DeleteReportUseCase,
    private val deleteReportArticleCrossRefUseCase: DeleteReportArticleCrossRefUseCase,
    private val getReportByDescriptionUseCase: GetReportByDescriptionUseCase,
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val getReportBySerialNumberUseCase: GetReportBySerialNumberUseCase,
    private val getReportByInternalCommentsUseCase: GetReportByInternalCommentsUseCase,
    private val getReportByCompanyUseCase: GetReportByCompanyUseCase
) : ViewModel() {
    private val _listReport = MutableStateFlow<List<ReportWithArticleAndCount>>(emptyList())
    val listReports get() = _listReport.asStateFlow()

    init {
        updateData()
    }

    fun deleteReport(report: ReportWithArticleAndCount) {
        viewModelScope.launch {
            _listReport.value -= report
            report.report.id?.let { deleteReportArticleCrossRefUseCase.deleteCrossRef(reportId = it) }
            deleteReportUseCase.deleteReport(report.report)
            updateData()
        }
    }

    fun filterListByRequest(request: String) {
        viewModelScope.launch {
            if (request.isNotEmpty()) {
                if (request == "*") {
                    _listReport.value = getAllReportsUseCase()
                } else {
                    val list = mutableListOf<ReportWithArticleAndCount>()
                    list.addAll(getReportBySerialNumberUseCase(request))
                    list.addAll(getReportByDescriptionUseCase(request))
                    list.addAll(getReportByInternalCommentsUseCase(request))
                    list.addAll(getReportByCompanyUseCase(request))
                    _listReport.value = list
                }
            } else _listReport.value = getLast20ReportsUseCase()
        }
    }

    fun updateData() {
        viewModelScope.launch {
            _listReport.value = emptyList()
            _listReport.value = getLast20ReportsUseCase()
        }
    }

}