package com.example.domain

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.tan

object PrayerTimeCalculator {

    val dhakaTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Dhaka")
    private const val TIMEZONE_OFFSET_HOURS = 6.0

    fun currentDhakaCalendar(nowMillis: Long = System.currentTimeMillis()): Calendar {
        return Calendar.getInstance(dhakaTimeZone).apply {
            timeInMillis = nowMillis
        }
    }

    fun formatDateIso(year: Int, month1To12: Int, day: Int): String {
        val m = month1To12.toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')
        return "$year-$m-$d"
    }

    fun dateIsoFromMillis(millis: Long): String {
        val cal = currentDhakaCalendar(millis)
        return formatDateIso(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
        )
    }

    fun addDaysToIso(dateIso: String, daysToAdd: Int): String {
        val parts = dateIso.split("-")
        val cal = Calendar.getInstance(dhakaTimeZone).apply {
            clear()
            set(Calendar.YEAR, parts[0].toInt())
            set(Calendar.MONTH, parts[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, parts[2].toInt())
            set(Calendar.HOUR_OF_DAY, 12)
            add(Calendar.DAY_OF_MONTH, daysToAdd)
        }
        return formatDateIso(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
        )
    }

    fun calculateForDate(
        dateIso: String,
        districtId: String,
        latitude: Double,
        longitude: Double,
        calculationMethod: CalculationMethod,
        asrMethod: AsrMethod,
    ): DailyPrayerTimes {
        val parts = dateIso.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)

        // Initial approximations at 5h, 6h, 12h, 15h, 18h, 19h local solar time
        val fajrHour = computeSunAngleTime(
            jd = jd + 5.0 / 24.0,
            angle = calculationMethod.fajrAngle,
            latitude = latitude,
            longitude = longitude,
            isMorning = true,
        )
        val sunriseHour = computeSunAngleTime(
            jd = jd + 6.0 / 24.0,
            angle = 0.833,
            latitude = latitude,
            longitude = longitude,
            isMorning = true,
        )
        val dhuhrHour = computeMidDay(jd + 12.0 / 24.0, longitude) +
            (calculationMethod.dhuhrOffsetMinutes / 60.0)
        val asrHour = computeAsrTime(
            jd = jd + 15.0 / 24.0,
            factor = asrMethod.shadowFactor.toDouble(),
            latitude = latitude,
            longitude = longitude,
        )
        val maghribHour = computeSunAngleTime(
            jd = jd + 18.0 / 24.0,
            angle = 0.833,
            latitude = latitude,
            longitude = longitude,
            isMorning = false,
        ) + (calculationMethod.maghribOffsetMinutes / 60.0)

        val ishaHour = if (calculationMethod.ishaIntervalMinutes != null) {
            maghribHour + (calculationMethod.ishaIntervalMinutes / 60.0)
        } else {
            computeSunAngleTime(
                jd = jd + 19.0 / 24.0,
                angle = calculationMethod.ishaAngle,
                latitude = latitude,
                longitude = longitude,
                isMorning = false,
            )
        }

        val dayStartMillis = startOfDayMillis(year, month, day)
        return DailyPrayerTimes(
            dateIso = dateIso,
            districtId = districtId,
            fajrMillis = hoursToEpochMillis(dayStartMillis, fajrHour),
            sunriseMillis = hoursToEpochMillis(dayStartMillis, sunriseHour),
            dhuhrMillis = hoursToEpochMillis(dayStartMillis, dhuhrHour),
            asrMillis = hoursToEpochMillis(dayStartMillis, asrHour),
            maghribMillis = hoursToEpochMillis(dayStartMillis, maghribHour),
            ishaMillis = hoursToEpochMillis(dayStartMillis, ishaHour),
        )
    }

    fun calculateNext30Days(
        startDateIso: String,
        districtId: String,
        latitude: Double,
        longitude: Double,
        calculationMethod: CalculationMethod,
        asrMethod: AsrMethod,
        daysCount: Int = 31,
    ): List<DailyPrayerTimes> {
        return (0 until daysCount).map { offset ->
            val dateIso = addDaysToIso(startDateIso, offset)
            calculateForDate(
                dateIso = dateIso,
                districtId = districtId,
                latitude = latitude,
                longitude = longitude,
                calculationMethod = calculationMethod,
                asrMethod = asrMethod,
            )
        }
    }

    fun findNextPrayer(
        today: DailyPrayerTimes?,
        tomorrow: DailyPrayerTimes?,
        nowMillis: Long,
    ): NextPrayerInfo? {
        if (today == null) return null
        val upcomingToday = today.allWaqtTimes().firstOrNull { (_, time) -> time > nowMillis }
        if (upcomingToday != null) {
            return NextPrayerInfo(
                waqt = upcomingToday.first,
                triggerTimeMillis = upcomingToday.second,
                remainingMillis = (upcomingToday.second - nowMillis).coerceAtLeast(0L),
                isTomorrow = false,
            )
        }
        val fajrTomorrow = tomorrow?.fajrMillis ?: (today.fajrMillis + 24L * 60L * 60L * 1000L)
        return NextPrayerInfo(
            waqt = PrayerWaqt.FAJR,
            triggerTimeMillis = fajrTomorrow,
            remainingMillis = (fajrTomorrow - nowMillis).coerceAtLeast(0L),
            isTomorrow = true,
        )
    }

    private fun startOfDayMillis(year: Int, month1To12: Int, day: Int): Long {
        val cal = Calendar.getInstance(dhakaTimeZone).apply {
            clear()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month1To12 - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun hoursToEpochMillis(dayStartMillis: Long, fractionalHours: Double): Long {
        val normalizedMinutes = (fractionalHours * 60.0).roundToLong()
        return dayStartMillis + normalizedMinutes * 60_000L
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private data class SunPosition(val declinationDeg: Double, val equationOfTimeHours: Double)

    private fun sunPosition(jd: Double): SunPosition {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * dsin(g) + 0.020 * dsin(2 * g))
        val e = 23.439 - 0.00000036 * d

        val raRad = atan2(dcos(e) * dsin(l), dcos(l))
        val raHours = fixHour(Math.toDegrees(raRad) / 15.0)
        val declDeg = Math.toDegrees(asin(dsin(e) * dsin(l)))
        val eqTimeHours = q / 15.0 - raHours
        return SunPosition(declDeg, eqTimeHours)
    }

    private fun computeMidDay(jd: Double, longitude: Double): Double {
        val eqTime = sunPosition(jd).equationOfTimeHours
        val noon = 12.0 + TIMEZONE_OFFSET_HOURS - (longitude / 15.0) - eqTime
        return fixHour(noon)
    }

    private fun computeSunAngleTime(
        jd: Double,
        angle: Double,
        latitude: Double,
        longitude: Double,
        isMorning: Boolean,
    ): Double {
        val decl = sunPosition(jd).declinationDeg
        val noon = computeMidDay(jd, longitude)
        val numerator = -dsin(angle) - dsin(decl) * dsin(latitude)
        val denominator = dcos(decl) * dcos(latitude)
        val cosH = (numerator / denominator).coerceIn(-1.0, 1.0)
        val hourAngleHours = Math.toDegrees(acos(cosH)) / 15.0
        return if (isMorning) noon - hourAngleHours else noon + hourAngleHours
    }

    private fun computeAsrTime(
        jd: Double,
        factor: Double,
        latitude: Double,
        longitude: Double,
    ): Double {
        val decl = sunPosition(jd).declinationDeg
        val angleRad = -atan(1.0 / (factor + tan(Math.toRadians(abs(latitude - decl)))))
        val angleDeg = Math.toDegrees(angleRad)
        // angleDeg is negative (above horizon), so -dsin(angleDeg) = +sin(elevation)
        return computeSunAngleTime(
            jd = jd,
            angle = angleDeg,
            latitude = latitude,
            longitude = longitude,
            isMorning = false,
        )
    }

    private fun dsin(deg: Double): Double = sin(Math.toRadians(deg))
    private fun dcos(deg: Double): Double = cos(Math.toRadians(deg))

    private fun fixAngle(a: Double): Double {
        val res = a - 360.0 * floor(a / 360.0)
        return if (res < 0) res + 360.0 else res
    }

    private fun fixHour(a: Double): Double {
        val res = a - 24.0 * floor(a / 24.0)
        return if (res < 0) res + 24.0 else res
    }
}
