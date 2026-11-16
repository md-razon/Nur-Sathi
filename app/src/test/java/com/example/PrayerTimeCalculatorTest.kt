package com.example

import com.example.domain.AsrMethod
import com.example.domain.BangladeshDistricts
import com.example.domain.BengaliFormatter
import com.example.domain.CalculationMethod
import com.example.domain.PrayerTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class PrayerTimeCalculatorTest {

    @Test
    fun `all 64 districts of Bangladesh are present with unique ids`() {
        val districts = BangladeshDistricts.allDistricts
        assertEquals(64, districts.size)
        assertEquals(64, districts.map { it.id }.toSet().size)
        assertEquals("ঢাকা", BangladeshDistricts.defaultDistrict.nameBn)
    }

    @Test
    fun `nearest district finder locates Dhaka and Chattogram accurately`() {
        val (dhaka, _) = BangladeshDistricts.findNearest(23.81, 90.41)
        assertEquals("dhaka", dhaka.id)

        val (ctg, _) = BangladeshDistricts.findNearest(22.36, 91.78)
        assertEquals("chattogram", ctg.id)
    }

    @Test
    fun `prayer times are strictly chronological for Dhaka across seasons`() {
        val dates = listOf("2026-01-15", "2026-06-21", "2026-11-16")
        val dhaka = BangladeshDistricts.findById("dhaka")

        for (dateIso in dates) {
            val times = PrayerTimeCalculator.calculateForDate(
                dateIso = dateIso,
                districtId = dhaka.id,
                latitude = dhaka.latitude,
                longitude = dhaka.longitude,
                calculationMethod = CalculationMethod.ISLAMIC_FOUNDATION_BD,
                asrMethod = AsrMethod.HANAFI,
            )
            assertTrue("Fajr < Sunrise on $dateIso", times.fajrMillis < times.sunriseMillis)
            assertTrue("Sunrise < Dhuhr on $dateIso", times.sunriseMillis < times.dhuhrMillis)
            assertTrue("Dhuhr < Asr on $dateIso", times.dhuhrMillis < times.asrMillis)
            assertTrue("Asr < Maghrib on $dateIso", times.asrMillis < times.maghribMillis)
            assertTrue("Maghrib < Isha on $dateIso", times.maghribMillis < times.ishaMillis)
        }
    }

    @Test
    fun `Hanafi Asr is strictly later than Shafi Asr`() {
        val dhaka = BangladeshDistricts.findById("dhaka")
        val hanafi = PrayerTimeCalculator.calculateForDate(
            dateIso = "2026-11-16",
            districtId = dhaka.id,
            latitude = dhaka.latitude,
            longitude = dhaka.longitude,
            calculationMethod = CalculationMethod.ISLAMIC_FOUNDATION_BD,
            asrMethod = AsrMethod.HANAFI,
        )
        val shafi = PrayerTimeCalculator.calculateForDate(
            dateIso = "2026-11-16",
            districtId = dhaka.id,
            latitude = dhaka.latitude,
            longitude = dhaka.longitude,
            calculationMethod = CalculationMethod.ISLAMIC_FOUNDATION_BD,
            asrMethod = AsrMethod.SHAFI,
        )
        assertTrue(hanafi.asrMillis > shafi.asrMillis)
    }

    @Test
    fun `30-day offline schedule generates 31 consecutive days`() {
        val dhaka = BangladeshDistricts.defaultDistrict
        val list = PrayerTimeCalculator.calculateNext30Days(
            startDateIso = "2026-11-16",
            districtId = dhaka.id,
            latitude = dhaka.latitude,
            longitude = dhaka.longitude,
            calculationMethod = CalculationMethod.ISLAMIC_FOUNDATION_BD,
            asrMethod = AsrMethod.HANAFI,
        )
        assertEquals(31, list.size)
        assertEquals("2026-11-16", list.first().dateIso)
        assertEquals("2026-12-16", list.last().dateIso)
    }

    @Test
    fun `Bengali digit, countdown, and Bangla calendar formatting work accurately`() {
        assertEquals("৩৩", BengaliFormatter.toBengaliNumber(33))
        assertEquals("০০ : ৪৫ : ১২", BengaliFormatter.formatCountdown((45 * 60 + 12) * 1000L))

        val cal = Calendar.getInstance(PrayerTimeCalculator.dhakaTimeZone).apply {
            clear()
            set(2026, Calendar.APRIL, 14, 12, 0, 0)
        }
        assertEquals("১ বৈশাখ ১৪৩৩", BengaliFormatter.formatBanglaDate(cal.timeInMillis))
    }
}
