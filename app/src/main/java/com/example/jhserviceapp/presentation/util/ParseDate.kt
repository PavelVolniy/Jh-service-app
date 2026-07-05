package com.example.jhserviceapp.presentation.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ParseDate {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateFromReceiptString(data: String): String {
        return getLocalDate(data).toString()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateMillisFromReceiptString(data: String): Long {
        return getLocalDate(data)?.toEpochDay() ?: 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDate(data: String): LocalDate? {
        val pattern = "t=(\\d{8})".toRegex()
        val matcher = pattern.find(data)
        return LocalDate.parse(
            matcher?.groupValues[1],
            DateTimeFormatter.ofPattern("yyyyMMdd")
        )
    }
}