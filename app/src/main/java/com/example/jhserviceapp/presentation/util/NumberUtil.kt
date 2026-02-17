package com.example.jhserviceapp.presentation.util

object NumberUtil {
    fun checkNumber(str: String): Boolean {
        val fnPattern = "FN\\d{6}".toRegex()
        val decimalPattern = "\\d{8}".toRegex()
        val chinaPattern = "\\d{2}\\w{2}\\d{5}".toRegex()
        val mbPattern = "F\\d{7}".toRegex()
        if (str.matches(fnPattern)) return true
        if (str.matches(decimalPattern)) return true
        if (str.matches(chinaPattern)) return true
        if (str.matches(mbPattern)) return true
        return false
    }
}