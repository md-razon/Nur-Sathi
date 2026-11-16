package com.example.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class PrayerScheduleDao(
    private val helper: NoorSathiSqliteHelper,
) {
    private val changeVersion = MutableStateFlow(0L)

    fun observeScheduleFrom(fromDateIso: String): Flow<List<PrayerScheduleEntity>> {
        return changeVersion
            .mapLatest { queryScheduleFrom(fromDateIso) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getScheduleFrom(fromDateIso: String): List<PrayerScheduleEntity> =
        withContext(Dispatchers.IO) { queryScheduleFrom(fromDateIso) }

    suspend fun countMatchingDays(
        fromDateIso: String,
        districtId: String,
        calcId: String,
        asrId: String,
    ): Int = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        db.rawQuery(
            """
            SELECT COUNT(*) FROM ${NoorSathiSqliteHelper.TABLE_PRAYER_SCHEDULE}
            WHERE dateIso >= ? AND districtId = ? AND calculationMethodId = ? AND asrMethodId = ?
            """.trimIndent(),
            arrayOf(fromDateIso, districtId, calcId, asrId),
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    suspend fun insertAll(items: List<PrayerScheduleEntity>) {
        withContext(Dispatchers.IO) {
            val db = helper.writableDatabase
            db.beginTransaction()
            try {
                for (item in items) {
                    val values = ContentValues().apply {
                        put("dateIso", item.dateIso)
                        put("districtId", item.districtId)
                        put("calculationMethodId", item.calculationMethodId)
                        put("asrMethodId", item.asrMethodId)
                        put("fajrMillis", item.fajrMillis)
                        put("sunriseMillis", item.sunriseMillis)
                        put("dhuhrMillis", item.dhuhrMillis)
                        put("asrMillis", item.asrMillis)
                        put("maghribMillis", item.maghribMillis)
                        put("ishaMillis", item.ishaMillis)
                    }
                    db.insertWithOnConflict(
                        NoorSathiSqliteHelper.TABLE_PRAYER_SCHEDULE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE,
                    )
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            changeVersion.value++
        }
    }

    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            helper.writableDatabase.delete(NoorSathiSqliteHelper.TABLE_PRAYER_SCHEDULE, null, null)
            changeVersion.value++
        }
    }

    private fun queryScheduleFrom(fromDateIso: String): List<PrayerScheduleEntity> {
        val db = helper.readableDatabase
        val list = mutableListOf<PrayerScheduleEntity>()
        db.rawQuery(
            """
            SELECT dateIso, districtId, calculationMethodId, asrMethodId,
                   fajrMillis, sunriseMillis, dhuhrMillis, asrMillis, maghribMillis, ishaMillis
            FROM ${NoorSathiSqliteHelper.TABLE_PRAYER_SCHEDULE}
            WHERE dateIso >= ?
            ORDER BY dateIso ASC
            """.trimIndent(),
            arrayOf(fromDateIso),
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    PrayerScheduleEntity(
                        dateIso = cursor.getString(0),
                        districtId = cursor.getString(1),
                        calculationMethodId = cursor.getString(2),
                        asrMethodId = cursor.getString(3),
                        fajrMillis = cursor.getLong(4),
                        sunriseMillis = cursor.getLong(5),
                        dhuhrMillis = cursor.getLong(6),
                        asrMillis = cursor.getLong(7),
                        maghribMillis = cursor.getLong(8),
                        ishaMillis = cursor.getLong(9),
                    ),
                )
            }
        }
        return list
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TasbihHistoryDao(
    private val helper: NoorSathiSqliteHelper,
) {
    private val changeVersion = MutableStateFlow(0L)

    fun observeRecentHistory(): Flow<List<TasbihHistoryEntity>> {
        return changeVersion
            .mapLatest { queryRecentHistory() }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getByDate(dateIso: String): TasbihHistoryEntity? =
        withContext(Dispatchers.IO) { queryByDate(dateIso) }

    fun observeByDate(dateIso: String): Flow<TasbihHistoryEntity?> {
        return changeVersion
            .mapLatest { queryByDate(dateIso) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun upsert(entry: TasbihHistoryEntity) {
        withContext(Dispatchers.IO) {
            val values = ContentValues().apply {
                put("dateIso", entry.dateIso)
                put("totalCount", entry.totalCount)
                put("completedRounds", entry.completedRounds)
                put("lastUpdatedMillis", entry.lastUpdatedMillis)
            }
            helper.writableDatabase.insertWithOnConflict(
                NoorSathiSqliteHelper.TABLE_TASBIH_HISTORY,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE,
            )
            changeVersion.value++
        }
    }

    private fun queryRecentHistory(): List<TasbihHistoryEntity> {
        val db = helper.readableDatabase
        val list = mutableListOf<TasbihHistoryEntity>()
        db.rawQuery(
            """
            SELECT dateIso, totalCount, completedRounds, lastUpdatedMillis
            FROM ${NoorSathiSqliteHelper.TABLE_TASBIH_HISTORY}
            ORDER BY dateIso DESC
            LIMIT 60
            """.trimIndent(),
            null,
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    TasbihHistoryEntity(
                        dateIso = cursor.getString(0),
                        totalCount = cursor.getInt(1),
                        completedRounds = cursor.getInt(2),
                        lastUpdatedMillis = cursor.getLong(3),
                    ),
                )
            }
        }
        return list
    }

    private fun queryByDate(dateIso: String): TasbihHistoryEntity? {
        val db = helper.readableDatabase
        db.rawQuery(
            """
            SELECT dateIso, totalCount, completedRounds, lastUpdatedMillis
            FROM ${NoorSathiSqliteHelper.TABLE_TASBIH_HISTORY}
            WHERE dateIso = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(dateIso),
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                TasbihHistoryEntity(
                    dateIso = cursor.getString(0),
                    totalCount = cursor.getInt(1),
                    completedRounds = cursor.getInt(2),
                    lastUpdatedMillis = cursor.getLong(3),
                )
            } else {
                null
            }
        }
    }
}

class NoorSathiSqliteHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_PRAYER_SCHEDULE (
                dateIso TEXT NOT NULL PRIMARY KEY,
                districtId TEXT NOT NULL,
                calculationMethodId TEXT NOT NULL,
                asrMethodId TEXT NOT NULL,
                fajrMillis INTEGER NOT NULL,
                sunriseMillis INTEGER NOT NULL,
                dhuhrMillis INTEGER NOT NULL,
                asrMillis INTEGER NOT NULL,
                maghribMillis INTEGER NOT NULL,
                ishaMillis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_TASBIH_HISTORY (
                dateIso TEXT NOT NULL PRIMARY KEY,
                totalCount INTEGER NOT NULL,
                completedRounds INTEGER NOT NULL,
                lastUpdatedMillis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRAYER_SCHEDULE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASBIH_HISTORY")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "noor_sathi.db"
        const val DATABASE_VERSION = 1
        const val TABLE_PRAYER_SCHEDULE = "prayer_schedule"
        const val TABLE_TASBIH_HISTORY = "tasbih_history"
    }
}

class NoorSathiDatabase private constructor(context: Context) {
    private val sqliteHelper = NoorSathiSqliteHelper(context)
    private val prayerScheduleDaoInstance = PrayerScheduleDao(sqliteHelper)
    private val tasbihHistoryDaoInstance = TasbihHistoryDao(sqliteHelper)

    fun prayerScheduleDao(): PrayerScheduleDao = prayerScheduleDaoInstance
    fun tasbihHistoryDao(): TasbihHistoryDao = tasbihHistoryDaoInstance

    companion object {
        @Volatile
        private var INSTANCE: NoorSathiDatabase? = null

        fun getInstance(context: Context): NoorSathiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NoorSathiDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
