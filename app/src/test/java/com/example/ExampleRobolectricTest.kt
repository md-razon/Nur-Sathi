package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
    fun `application container initializes room database and repositories`() {
        val app = ApplicationProvider.getApplicationContext<NoorSathiApplication>()
        assertNotNull(app.database)
        assertNotNull(app.preferencesRepository)
        assertNotNull(app.prayerRepository)
        assertNotNull(app.tasbihRepository)
        assertEquals(false, app.adPlacementManager.isAdsEnabled)
    }
}
