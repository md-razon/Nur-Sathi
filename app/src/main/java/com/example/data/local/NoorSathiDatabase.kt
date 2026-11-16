package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerScheduleDao {
    @Query("SELECT * FROM prayer_schedule WHERE dateIso >= :fromDateIso ORDER BY dateIso ASC")
    fun observeScheduleFrom(fromDateIso: String): Flow<List<PrayerScheduleEntity>>

    @Query("SELECT * FROM prayer_schedule WHERE dateIso >= :fromDateIso ORDER BY dateIso ASC")
    suspend fun getScheduleFrom(fromDateIso: String): List<PrayerScheduleEntity>

    @Query("SELECT COUNT(*) FROM prayer_schedule WHERE dateIso >= :fromDateIso AND districtId = :districtId AND calculationMethodId = :calcId AND asrMethodId = :asrId")
    suspend fun countMatchingDays(
        fromDateIso: String,
        districtId: String,
        calcId: String,
        asrId: String,
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PrayerScheduleEntity>)

    @Query("DELETE FROM prayer_schedule")
    suspend fun clearAll()
}

@Dao
interface TasbihHistoryDao {
    @Query("SELECT * FROM tasbih_history ORDER BY dateIso DESC LIMIT 60")
    fun observeRecentHistory(): Flow<List<TasbihHistoryEntity>>

    @Query("SELECT * FROM tasbih_history WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getByDate(dateIso: String): TasbihHistoryEntity?

    @Query("SELECT * FROM tasbih_history WHERE dateIso = :dateIso LIMIT 1")
    fun observeByDate(dateIso: String): Flow<TasbihHistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: TasbihHistoryEntity)
}

@Database(
    entities = [PrayerScheduleEntity::class, TasbihHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NoorSathiDatabase : RoomDatabase() {
    abstract fun prayerScheduleDao(): PrayerScheduleDao
    abstract fun tasbihHistoryDao(): TasbihHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: NoorSathiDatabase? = null

        fun getInstance(context: Context): NoorSathiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoorSathiDatabase::class.java,
                    "noor_sathi.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}
