package com.example.system

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.BengaliFormatter
import com.example.domain.PrayerWaqt

object NotificationHelper {

    private const val CHANNEL_AZAN_ID = "noor_sathi_prayer_azan_v1"
    private const val CHANNEL_SILENT_ID = "noor_sathi_prayer_silent_v1"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val azanSoundUri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.azan_chime}",
        )
        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val azanChannel = NotificationChannel(
            CHANNEL_AZAN_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notification_channel_desc)
            setSound(azanSoundUri, audioAttrs)
            enableVibration(true)
        }

        val silentChannel = NotificationChannel(
            CHANNEL_SILENT_ID,
            "নামাজের নীরব রিমাইন্ডার",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "আজানের শব্দ ছাড়া নামাজের সময় রিমাইন্ডার"
            setSound(null, null)
            enableVibration(true)
        }

        manager.createNotificationChannel(azanChannel)
        manager.createNotificationChannel(silentChannel)
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showPrayerNotification(
        context: Context,
        waqt: PrayerWaqt,
        azanSoundEnabled: Boolean,
        preOffsetMinutes: Int,
        districtNameBn: String,
    ) {
        if (!hasNotificationPermission(context)) return
        ensureChannels(context)

        val channelId = if (azanSoundEnabled) CHANNEL_AZAN_ID else CHANNEL_SILENT_ID
        val title = if (preOffsetMinutes > 0) {
            "${BengaliFormatter.toBengaliNumber(preOffsetMinutes)} মিনিট পর ${waqt.nameBn} নামাজ"
        } else {
            waqt.reminderTitleBn
        }
        val body = if (preOffsetMinutes > 0) {
            "$districtNameBn — প্রস্তুতি নিন, ${waqt.nameBn} নামাজের সময় শীঘ্রই শুরু হবে।"
        } else {
            "$districtNameBn — ${waqt.reminderBodyBn}"
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            waqt.ordinal + 100,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(1000 + waqt.ordinal, notification)
    }

    fun showTestNotification(
        context: Context,
        azanSoundEnabled: Boolean,
        districtNameBn: String,
    ) {
        if (!hasNotificationPermission(context)) return
        ensureChannels(context)

        val channelId = if (azanSoundEnabled) CHANNEL_AZAN_ID else CHANNEL_SILENT_ID
        val title = "নূর সাথী — পরীক্ষামূলক রিমাইন্ডার"
        val body = "$districtNameBn জেলার নামাজের নোটিফিকেশন ও রিমাইন্ডার সঠিকভাবে কাজ করছে।"

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(9999, notification)
    }
}
