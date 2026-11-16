package com.example

import android.app.Application
import com.example.ads.AdPlacementManager
import com.example.ads.NoOpAdPlacementManager
import com.example.data.local.NoorSathiDatabase
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.PrayerRepository
import com.example.data.repository.TasbihRepository
import com.example.system.NotificationHelper

class NoorSathiApplication : Application() {

    lateinit var database: NoorSathiDatabase
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var prayerRepository: PrayerRepository
        private set

    lateinit var tasbihRepository: TasbihRepository
        private set

    val adPlacementManager: AdPlacementManager = NoOpAdPlacementManager()

    override fun onCreate() {
        super.onCreate()
        database = NoorSathiDatabase.getInstance(this)
        preferencesRepository = UserPreferencesRepository(this)
        prayerRepository = PrayerRepository(database.prayerScheduleDao())
        tasbihRepository = TasbihRepository(database.tasbihHistoryDao())
        NotificationHelper.ensureChannels(this)
        adPlacementManager.initialize()
    }
}
