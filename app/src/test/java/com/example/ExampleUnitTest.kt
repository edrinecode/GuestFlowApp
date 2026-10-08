package com.example

import com.example.data.model.BirthdayUtils
import com.example.data.model.SpaGymClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class ExampleUnitTest {

    @Test
    fun testFirstNameExtraction() {
        val client1 = SpaGymClient(id = "1", name = "Jordan Lee", phone = "+256700000000")
        assertEquals("Jordan", client1.firstName)

        val client2 = SpaGymClient(id = "2", name = "sarah connor", phone = "0700000000")
        assertEquals("Sarah", client2.firstName)

        val client3 = SpaGymClient(id = "3", name = "Alex", phone = "0700000000")
        assertEquals("Alex", client3.firstName)
    }

    @Test
    fun testBirthdayEvaluationInKampala() {
        val kampalaZone = ZoneId.of("Africa/Kampala")
        val todayInKampala = LocalDate.now(kampalaZone)

        val todayDay = todayInKampala.dayOfMonth.toString()
        val todayMonthName = todayInKampala.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val todayMonthNumber = todayInKampala.monthValue.toString()

        // Exact match with month name
        assertTrue(BirthdayUtils.isTodayInKampala(todayDay, todayMonthName))

        // Exact match with numeric month
        assertTrue(BirthdayUtils.isTodayInKampala(todayDay, todayMonthNumber))

        // Different day should return false
        val differentDay = if (todayInKampala.dayOfMonth == 1) "2" else "1"
        assertFalse(BirthdayUtils.isTodayInKampala(differentDay, todayMonthName))

        // Blank or null values should return false
        assertFalse(BirthdayUtils.isTodayInKampala(null, null))
        assertFalse(BirthdayUtils.isTodayInKampala("", ""))
    }
}
