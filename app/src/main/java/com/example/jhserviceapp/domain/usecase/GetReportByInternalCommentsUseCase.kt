package com.example.jhserviceapp.domain.usecase

import com.example.jhserviceapp.data.ReportDao
import javax.inject.Inject

class GetReportByInternalCommentsUseCase @Inject constructor(
    private val reportDao: ReportDao
) {
    suspend operator fun invoke(request: String) = reportDao.getReportByInternalComments("%$request%")
}