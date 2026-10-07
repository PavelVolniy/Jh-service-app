package com.example.jhserviceapp.domain.usecase

import com.example.jhserviceapp.data.ReportDao
import javax.inject.Inject

class GetReportByCompanyUseCase @Inject constructor(
    private val reportDao: ReportDao
) {
    suspend operator fun invoke(request: String) = reportDao.getReportByCompany("%$request%")
}