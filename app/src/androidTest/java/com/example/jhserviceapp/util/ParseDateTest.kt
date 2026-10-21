package com.example.jhserviceapp.util


import com.example.jhserviceapp.presentation.util.ParseDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RunWith(JUnit4::class)
class ParseDateTest {

    private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    @Test
    fun getLocalDate() {
        val data = "t=20231005"
        val result = ParseDate.getLocalDate(data) ?: 0
        assertEquals(LocalDate.of(2023, 10, 5), result)
    }

    @Test
    fun getLocalDateInvalidFormatReturnsNull() {
        val data = "t=20231305" // месяц 13 — невалиден
        val result = ParseDate.getLocalDate(data)
        assertNull(result)
    }

    @Test
    fun getLocalDateNotFormat() {
        val data = "no date here"
        assertNull(ParseDate.getLocalDate(data))
    }

    @Test
    fun getLocalDateNotFullFormat() {
        val data = "t=1234567" // 7 цифр вместо 8
        assertNull(ParseDate.getLocalDate(data))
    }

    @Test
    fun getDateFromReceiptString() {
        val data = "t=20240101"
        val result = ParseDate.getDateFromReceiptString(data)
        assertEquals("2024-01-01", result)
    }

    @Test
    fun getDateMillisFromReceiptStringCorrectFirstDayEpoch() {
        val data = "t=19700101" // первый день эпохи
        val result = ParseDate.getDateMillisFromReceiptString(data)
        assertEquals(0, result)
    }

    @Test
    fun getDateMillisFromReceiptStringDateAfterSecondDayEpoch() {
        val data = "t=19700102" // второй день эпохи
        val result = ParseDate.getDateMillisFromReceiptString(data)
        assertEquals(1, result)
    }

    @Test
    fun getDateMillisFromReceiptStringNoPatternReturn0() {
        val data = "no t=..."
        val result = ParseDate.getDateMillisFromReceiptString(data)
        assertEquals(0, result)
    }

    @Test
    fun getLocalDateMinimalDate() {
        val data = "t=00010101"
        val result = ParseDate.getLocalDate(data)
        assertEquals(LocalDate.of(1, 1, 1), result) // 0001-01-01
    }

}
