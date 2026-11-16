package com.example.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.NoorSathiApplication
import com.example.domain.PrayerWaqt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val waqtId = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_WAQT_ID) ?: PrayerWaqt.FAJR.id
        val preOffset = intent.getIntExtra(PrayerAlarmScheduler.EXTRA_PRE_OFFSET, 0)
        val waqt = PrayerWaqt.fromId(waqtId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as NoorSathiApplication
                val prefs = app.preferencesRepository.getCurrent()
                if (prefs.reminderSettings.notificationsEnabled &&
                    prefs.reminderSettings.isWaqtEnabled(waqt)
                ) {
                    NotificationHelper.showPrayerNotification(
                        context = context,
                        waqt = waqt,
                        azanSoundEnabled = prefs.reminderSettings.azanSoundEnabled,
                        preOffsetMinutes = preOffset,
                        districtNameBn = prefs.activeDistrict.nameBn,
                    )
                }
                val schedule = app.prayerRepository.ensureScheduleCached(prefs)
                PrayerAlarmScheduler.rescheduleUpcomingAlarms(context, schedule, prefs)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val app = context.applicationContext as NoorSathiApplication
                    val prefs = app.preferencesRepository.getCurrent()
                    val schedule = app.prayerRepository.ensureScheduleCached(prefs)
                    PrayerAlarmScheduler.rescheduleUpcomingAlarms(context, schedule, prefs)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
