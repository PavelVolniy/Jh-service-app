package com.example.jhserviceapp.presentation.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
        val matchResult = pattern.find(data) ?: return null

        val dateString = matchResult.groupValues.getOrNull(1) ?: return null

        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"))
        } catch (e: DateTimeParseException) {
            Log.e("getLocalDateError", e.message ?: "Unknown parse error", e)
            null
        }
    }

}