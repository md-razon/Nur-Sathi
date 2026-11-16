package com.example.ads

/**
 * AdMob-ready architectural abstraction.
 *
 * Noor Sathi launches completely ad-free. If advertisements are enabled in a future release,
 * they must strictly respect the following product and ethical boundaries:
 * - NEVER display ads on the Smart Tasbih screen or interrupt counting.
 * - NEVER display ads over prayer reminder screens or notifications.
 * - NEVER display intrusive interstitial/full-screen ads during worship activities.
 */
enum class AppSurface {
    HOME,
    TASBIH,
    PRAYER_TIMES,
    PRAYER_REMINDERS,
    SETTINGS,
}

interface AdPlacementManager {
    val isAdsEnabled: Boolean

    fun shouldShowBanner(surface: AppSurface): Boolean {
        if (!isAdsEnabled) return false
        return when (surface) {
            AppSurface.TASBIH, AppSurface.PRAYER_REMINDERS -> false
            AppSurface.HOME, AppSurface.PRAYER_TIMES, AppSurface.SETTINGS -> true
        }
    }

    fun initialize()
}

class NoOpAdPlacementManager : AdPlacementManager {
    override val isAdsEnabled: Boolean = false
    override fun initialize() {
        // Ad-free build: no ad SDK initialized.
    }
}
