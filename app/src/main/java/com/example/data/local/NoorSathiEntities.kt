package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.DailyPrayerTimes

@Entity(tableName = "prayer_schedule")
data class PrayerScheduleEntity(
    @PrimaryKey val dateIso: String,
    val districtId: String,
    val calculationMethodId: String,
    val asrMethodId: String,
    val fajrMillis: Long,
    val sunriseMillis: Long,
    val dhuhrMillis: Long,
    val asrMillis: Long,
    val maghribMillis: Long,
    val ishaMillis: Long,
) {
    fun toDomain(): DailyPrayerTimes = DailyPrayerTimes(
        dateIso = dateIso,
        districtId = districtId,
        fajrMillis = fajrMillis,
        sunriseMillis = sunriseMillis,
        dhuhrMillis = dhuhrMillis,
        asrMillis = asrMillis,
        maghribMillis = maghribMillis,
        ishaMillis = ishaMillis,
    )
}

@Entity(tableName = "tasbih_history")
data class TasbihHistoryEntity(
    @PrimaryKey val dateIso: String,
    val totalCount: Int,
    val completedRounds: Int,
    val lastUpdatedMillis: Long,
)
