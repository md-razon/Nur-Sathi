package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PrayerScheduleEntity
import com.example.data.local.TasbihHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name is Noor Sathi in Bengali`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("নূর সাথী", appName)
    }

    @Test
    fun `application container initializes sqlite database and repositories`() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<NoorSathiApplication>()
        assertNotNull(app.database)
        assertNotNull(app.preferencesRepository)
        assertNotNull(app.prayerRepository)
        assertNotNull(app.tasbihRepository)
        assertEquals(false, app.adPlacementManager.isAdsEnabled)

        val prayerDao = app.database.prayerScheduleDao()
        prayerDao.clearAll()
        prayerDao.insertAll(
            listOf(
                PrayerScheduleEntity(
                    dateIso = "2026-11-16",
                    districtId = "dhaka",
                    calculationMethodId = "ifb",
                    asrMethodId = "hanafi",
                    fajrMillis = 1000L,
                    sunriseMillis = 2000L,
                    dhuhrMillis = 3000L,
                    asrMillis = 4000L,
                    maghribMillis = 5000L,
                    ishaMillis = 6000L,
                ),
            ),
        )
        assertEquals(1, prayerDao.countMatchingDays("2026-11-16", "dhaka", "ifb", "hanafi"))
        assertEquals(1, prayerDao.observeScheduleFrom("2026-11-16").first().size)

        val tasbihDao = app.database.tasbihHistoryDao()
        tasbihDao.upsert(
            TasbihHistoryEntity(
                dateIso = "2026-11-16",
                totalCount = 33,
                completedRounds = 1,
                lastUpdatedMillis = 9999L,
            ),
        )
        assertEquals(33, tasbihDao.getByDate("2026-11-16")?.totalCount)
        assertEquals(1, tasbihDao.observeRecentHistory().first().size)
    }
}
