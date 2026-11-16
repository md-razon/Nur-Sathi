package com.example.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.preferences.UserPreferencesState
import com.example.domain.DailyPrayerTimes
import com.example.domain.PrayerWaqt

object PrayerAlarmScheduler {

    const val EXTRA_WAQT_ID = "extra_waqt_id"
    const val EXTRA_PRE_OFFSET = "extra_pre_offset"

    private const val REQUEST_CODE_BASE = 4100

    fun rescheduleUpcomingAlarms(
        context: Context,
        schedule: List<DailyPrayerTimes>,
        prefs: UserPreferencesState,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelAllAlarms(context, alarmManager)

        val reminders = prefs.reminderSettings
        if (!reminders.masterEnabled || !reminders.notificationsEnabled) return

        val now = System.currentTimeMillis()
        val offsetMillis = reminders.prePrayerOffsetMinutes * 60_000L

        // Schedule the next upcoming occurrence for each enabled waqt across cached 30 days
        for (waqt in PrayerWaqt.entries) {
            if (!reminders.isWaqtEnabled(waqt)) continue

            val nextTrigger = schedule
                .asSequence()
                .map { it.timeFor(waqt) - offsetMillis }
                .firstOrNull { it > now + 2_000L }
                ?: continue

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra(EXTRA_WAQT_ID, waqt.id)
                putExtra(EXTRA_PRE_OFFSET, reminders.prePrayerOffsetMinutes)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_BASE + waqt.ordinal,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTrigger,
                    pendingIntent,
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTrigger,
                    pendingIntent,
                )
            }
        }
    }

    private fun cancelAllAlarms(context: Context, alarmManager: AlarmManager) {
        for (waqt in PrayerWaqt.entries) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_BASE + waqt.ordinal,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }
}
