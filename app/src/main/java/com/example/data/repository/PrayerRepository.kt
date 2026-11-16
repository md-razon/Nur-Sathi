package com.example.data.repository

import com.example.data.local.PrayerScheduleDao
import com.example.data.local.PrayerScheduleEntity
import com.example.data.preferences.UserPreferencesState
import com.example.domain.DailyPrayerTimes
import com.example.domain.PrayerTimeCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrayerRepository(private val dao: PrayerScheduleDao) {

    fun observeNext30Days(fromDateIso: String = PrayerTimeCalculator.dateIsoFromMillis(System.currentTimeMillis())): Flow<List<DailyPrayerTimes>> {
        return dao.observeScheduleFrom(fromDateIso).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun ensureScheduleCached(prefs: UserPreferencesState): List<DailyPrayerTimes> {
        val todayIso = PrayerTimeCalculator.dateIsoFromMillis(System.currentTimeMillis())
        val cachedCount = dao.countMatchingDays(
            fromDateIso = todayIso,
            districtId = prefs.selectedDistrictId,
            calcId = prefs.calculationMethod.id,
            asrId = prefs.asrMethod.id,
        )
        if (cachedCount >= 30 && prefs.locationMode.id == "manual") {
            return dao.getScheduleFrom(todayIso).map { it.toDomain() }
        }

        val generated = PrayerTimeCalculator.calculateNext30Days(
            startDateIso = todayIso,
            districtId = prefs.selectedDistrictId,
            latitude = prefs.effectiveLatitude,
            longitude = prefs.effectiveLongitude,
            calculationMethod = prefs.calculationMethod,
            asrMethod = prefs.asrMethod,
            daysCount = 31,
        )

        val entities = generated.map { day ->
            PrayerScheduleEntity(
                dateIso = day.dateIso,
                districtId = day.districtId,
                calculationMethodId = prefs.calculationMethod.id,
                asrMethodId = prefs.asrMethod.id,
                fajrMillis = day.fajrMillis,
                sunriseMillis = day.sunriseMillis,
                dhuhrMillis = day.dhuhrMillis,
                asrMillis = day.asrMillis,
                maghribMillis = day.maghribMillis,
                ishaMillis = day.ishaMillis,
            )
        }
        dao.clearAll()
        dao.insertAll(entities)
        return generated
    }

    suspend fun getCachedSchedule(): List<DailyPrayerTimes> {
        val todayIso = PrayerTimeCalculator.dateIsoFromMillis(System.currentTimeMillis())
        return dao.getScheduleFrom(todayIso).map { it.toDomain() }
    }
}
