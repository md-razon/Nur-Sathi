package com.example.domain

import java.util.Calendar
import kotlin.math.floor

object BengaliFormatter {

    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    private val gregorianMonthsBn = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর",
    )

    private val weekdaysBn = mapOf(
        Calendar.SUNDAY to "রবিবার",
        Calendar.MONDAY to "সোমবার",
        Calendar.TUESDAY to "মঙ্গলবার",
        Calendar.WEDNESDAY to "বুধবার",
        Calendar.THURSDAY to "বৃহস্পতিবার",
        Calendar.FRIDAY to "শুক্রবার",
        Calendar.SATURDAY to "শনিবার",
    )

    private val banglaMonths = listOf(
        "বৈশাখ", "জ্যৈষ্ঠ", "আষাঢ়", "শ্রাবণ", "ভাদ্র", "আশ্বিন",
        "কার্তিক", "অগ্রহায়ণ", "পৌষ", "মাঘ", "ফাল্গুন", "চৈত্র",
    )

    private val hijriMonthsBn = listOf(
        "মহররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শাবান",
        "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ",
    )

    fun toBengaliDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toBengaliNumber(value: Int): String = toBengaliDigits(value.toString())

    fun toBengaliNumber(value: Long): String = toBengaliDigits(value.toString())

    fun formatTime12Hour(epochMillis: Long, includePeriod: Boolean = false): String {
        val cal = PrayerTimeCalculator.currentDhakaCalendar(epochMillis)
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val minStr = minute.toString().padStart(2, '0')
        val base = toBengaliDigits("$hour12:$minStr")
        if (!includePeriod) return base

        val period = when (hour24) {
            in 4..5 -> "ভোর"
            in 6..11 -> "সকাল"
            in 12..14 -> "দুপুর"
            in 15..17 -> "বিকাল"
            in 18..19 -> "সন্ধ্যা"
            else -> "রাত"
        }
        return "$base $period"
    }

    fun formatCountdown(remainingMillis: Long): String {
        val totalSeconds = (remainingMillis.coerceAtLeast(0L)) / 1000L
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L
        val raw = String.format(java.util.Locale.US, "%02d : %02d : %02d", hours, minutes, seconds)
        return toBengaliDigits(raw)
    }

    fun formatGregorianDateBn(nowMillis: Long = System.currentTimeMillis()): String {
        val cal = PrayerTimeCalculator.currentDhakaCalendar(nowMillis)
        val dayName = weekdaysBn[cal.get(Calendar.DAY_OF_WEEK)] ?: ""
        val day = toBengaliNumber(cal.get(Calendar.DAY_OF_MONTH))
        val month = gregorianMonthsBn[cal.get(Calendar.MONTH)]
        val year = toBengaliNumber(cal.get(Calendar.YEAR))
        return "$dayName, $day $month $year"
    }

    fun formatShortDateFromIso(dateIso: String): String {
        val parts = dateIso.split("-")
        if (parts.size != 3) return toBengaliDigits(dateIso)
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        val monthName = gregorianMonthsBn.getOrElse(month - 1) { "" }
        return "${toBengaliNumber(day)} $monthName"
    }

    fun formatDayOfWeekFromIso(dateIso: String): String {
        val parts = dateIso.split("-")
        if (parts.size != 3) return ""
        val cal = Calendar.getInstance(PrayerTimeCalculator.dhakaTimeZone).apply {
            clear()
            set(Calendar.YEAR, parts[0].toInt())
            set(Calendar.MONTH, parts[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, parts[2].toInt())
            set(Calendar.HOUR_OF_DAY, 12)
        }
        return weekdaysBn[cal.get(Calendar.DAY_OF_WEEK)] ?: ""
    }

    /**
     * Bangladesh Revised Bangla Academy Calendar (effective 2019):
     * - Boishakh (14 April) to Ashwin (first 6 months): 31 days each (Boishakh..Bhadro 31, Ashwin 31? Wait:
     *   Revised 2019: Boishakh, Jaishtha, Ashar, Srabon, Bhadro, Ashwin = 31 days; Kartik, Agrahayan, Poush, Magh, Chaitra = 30 days;
     *   Falgun = 29 days (30 in Gregorian leap year when Feb has 29 days).
     */
    fun formatBanglaDate(nowMillis: Long = System.currentTimeMillis()): String {
        val cal = PrayerTimeCalculator.currentDhakaCalendar(nowMillis)
        val gYear = cal.get(Calendar.YEAR)
        val gMonth = cal.get(Calendar.MONTH) + 1
        val gDay = cal.get(Calendar.DAY_OF_MONTH)

        val boishakhStart = Calendar.getInstance(PrayerTimeCalculator.dhakaTimeZone).apply {
            clear()
            set(gYear, Calendar.APRIL, 14, 0, 0, 0)
        }
        val todayCal = Calendar.getInstance(PrayerTimeCalculator.dhakaTimeZone).apply {
            clear()
            set(gYear, gMonth - 1, gDay, 0, 0, 0)
        }

        val banglaYear: Int
        val refBoishakh: Calendar
        if (todayCal.timeInMillis >= boishakhStart.timeInMillis) {
            banglaYear = gYear - 593
            refBoishakh = boishakhStart
        } else {
            banglaYear = gYear - 594
            refBoishakh = Calendar.getInstance(PrayerTimeCalculator.dhakaTimeZone).apply {
                clear()
                set(gYear - 1, Calendar.APRIL, 14, 0, 0, 0)
            }
        }

        val diffDays = ((todayCal.timeInMillis - refBoishakh.timeInMillis) / 86_400_000L).toInt()
        val targetGregorianYearForFalgun = if (todayCal.timeInMillis >= boishakhStart.timeInMillis) gYear + 1 else gYear
        val isLeap = isGregorianLeapYear(targetGregorianYearForFalgun)
        val monthLengths = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, if (isLeap) 30 else 29, 30)

        var remaining = diffDays
        var monthIdx = 0
        while (monthIdx < 11 && remaining >= monthLengths[monthIdx]) {
            remaining -= monthLengths[monthIdx]
            monthIdx++
        }
        val banglaDay = remaining + 1
        return "${toBengaliNumber(banglaDay)} ${banglaMonths[monthIdx]} ${toBengaliNumber(banglaYear)}"
    }

    /**
     * Offline tabular Hijri date approximation (Kuwaiti algorithm) formatted in Bengali.
     */
    fun formatHijriDateBn(nowMillis: Long = System.currentTimeMillis()): String {
        val cal = PrayerTimeCalculator.currentDhakaCalendar(nowMillis)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)

        var m = month
        var y = year
        if (m < 3) {
            y -= 1
            m += 12
        }
        var a = floor(y / 100.0)
        var b = 2 - a + floor(a / 4.0)
        if (y < 1583) b = 0.0
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524

        val z = jd - 1948440.0 + 10632.0
        val n = floor((z - 1.0) / 10631.0)
        val z2 = z - 10631.0 * n + 354.0
        val j = (floor((10985.0 - z2) / 5316.0) * floor((50.0 * z2) / 17719.0)) +
            (floor(z2 / 5670.0) * floor((43.0 * z2) / 15238.0))
        val z3 = z2 - (floor((30.0 - j) / 15.0) * floor((17719.0 * j) / 50.0)) -
            (floor(j / 16.0) * floor((15238.0 * j) / 43.0)) + 29.0

        val hMonth = floor((24.0 * z3) / 709.0).toInt()
        val hDay = (z3 - floor((709.0 * hMonth) / 24.0)).toInt()
        val hYear = (30 * n + j - 30).toInt()

        val monthName = hijriMonthsBn.getOrElse((hMonth - 1).coerceIn(0, 11)) { "" }
        return "${toBengaliNumber(hDay)} $monthName ${toBengaliNumber(hYear)} হিজরি"
    }

    private fun isGregorianLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
