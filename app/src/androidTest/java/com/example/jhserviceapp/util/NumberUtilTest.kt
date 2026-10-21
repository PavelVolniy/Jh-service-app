package com.example.jhserviceapp.util

import com.example.jhserviceapp.presentation.util.NumberUtil
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberUtilTest {

    @Test
    fun checkNumber_fnPattern_valid() {
        // FN + ровно 6 цифр
        assertTrue(NumberUtil.checkNumber("FN123456"))
        assertTrue(NumberUtil.checkNumber("FN000000"))
        assertTrue(NumberUtil.checkNumber("FN999999"))
    }

    @Test
    fun checkNumber_fnPattern_invalid() {
        assertFalse(NumberUtil.checkNumber("FN12345"))   // 5 цифр
        assertFalse(NumberUtil.checkNumber("FN1234567")) // 7 цифр
        assertFalse(NumberUtil.checkNumber("F123456"))   // нет N
        assertFalse(NumberUtil.checkNumber("FNabcdef"))  // буквы вместо цифр
    }


    @Test
    fun checkNumber_mbPattern_valid() {
        // F + ровно 7 цифр
        assertTrue(NumberUtil.checkNumber("F1234567"))
        assertTrue(NumberUtil.checkNumber("F0000000"))
        assertTrue(NumberUtil.checkNumber("F9999999"))
    }

    @Test
    fun checkNumber_mbPattern_invalid() {
        assertFalse(NumberUtil.checkNumber("F123456"))   // 6 цифр
        assertFalse(NumberUtil.checkNumber("F12345678")) // 8 цифр
        assertFalse(NumberUtil.checkNumber("f1234567"))  // строчная f (\d только цифры, но F — заглавная буква)
        assertFalse(NumberUtil.checkNumber("G1234567"))  // другая буква
    }

    @Test
    fun checkNumber_edgeCases() {
        // Пустая строка — не подходит ни под один паттерн
        assertFalse(NumberUtil.checkNumber(""))

        // Пробелы, спецсимволы
        assertFalse(NumberUtil.checkNumber(" FN123456"))
        assertFalse(NumberUtil.checkNumber("FN123456 "))
        assertFalse(NumberUtil.checkNumber("FN-123456"))
    }
}
