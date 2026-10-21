package com.example.jhserviceapp.presentation.util

object NumberUtil {
    private val fnPattern = "FN\\d{6}".toRegex()
    private val mbPattern = "F\\d{7}".toRegex()

    fun checkNumber(str: String): Boolean {
        if (str.matches(fnPattern)) return true
        if (str.matches(mbPattern)) return true
        return false
    }
}
