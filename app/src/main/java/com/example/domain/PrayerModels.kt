package com.example.domain

enum class PrayerWaqt(
    val id: String,
    val nameBn: String,
    val reminderTitleBn: String,
    val reminderBodyBn: String,
) {
    FAJR(
        id = "fajr",
        nameBn = "ফজর",
        reminderTitleBn = "ফজরের নামাজের সময় হয়েছে",
        reminderBodyBn = "আসসালাতু খাইরুম মিনান নাউম — ফজরের নামাজ আদায় করুন।",
    ),
    DHUHR(
        id = "dhuhr",
        nameBn = "যোহর",
        reminderTitleBn = "যোহরের নামাজের সময় হয়েছে",
        reminderBodyBn = "পবিত্রতা অর্জন করে যোহরের নামাজে মনোনিবেশ করুন।",
    ),
    ASR(
        id = "asr",
        nameBn = "আসর",
        reminderTitleBn = "আসরের নামাজের সময় হয়েছে",
        reminderBodyBn = "ব্যস্ততা থামিয়ে আসরের নামাজ আদায় করুন।",
    ),
    MAGHRIB(
        id = "maghrib",
        nameBn = "মাগরিব",
        reminderTitleBn = "মাগরিবের নামাজের সময় হয়েছে",
        reminderBodyBn = "আল্লাহর স্মরণে মাগরিবের নামাজে এগিয়ে আসুন।",
    ),
    ISHA(
        id = "isha",
        nameBn = "এশা",
        reminderTitleBn = "এশার নামাজের সময় হয়েছে",
        reminderBodyBn = "দিনের সমাপ্তিতে এশার নামাজ আদায় করে প্রশান্তি লাভ করুন।",
    );

    companion object {
        fun fromId(id: String): PrayerWaqt = entries.firstOrNull { it.id == id } ?: FAJR
    }
}

enum class CalculationMethod(
    val id: String,
    val nameBn: String,
    val descriptionBn: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMinutes: Int? = null,
    val dhuhrOffsetMinutes: Int = 1,
    val maghribOffsetMinutes: Int = 1,
) {
    ISLAMIC_FOUNDATION_BD(
        id = "ifb",
        nameBn = "ইসলামিক ফাউন্ডেশন বাংলাদেশ",
        descriptionBn = "১৮° ফজর ও ১৮° এশা — বাংলাদেশের জন্য আদর্শ",
        fajrAngle = 18.0,
        ishaAngle = 18.0,
        dhuhrOffsetMinutes = 1,
        maghribOffsetMinutes = 2,
    ),
    MUSLIM_WORLD_LEAGUE(
        id = "mwl",
        nameBn = "মুসলিম ওয়ার্ল্ড লীগ",
        descriptionBn = "১৮° ফজর ও ১৭° এশা — আন্তর্জাতিক মানদণ্ড",
        fajrAngle = 18.0,
        ishaAngle = 17.0,
        dhuhrOffsetMinutes = 1,
        maghribOffsetMinutes = 1,
    ),
    EGYPTIAN(
        id = "egypt",
        nameBn = "মিশরীয় সাধারণ কর্তৃপক্ষ",
        descriptionBn = "১৯.৫° ফজর ও ১৭.৫° এশা",
        fajrAngle = 19.5,
        ishaAngle = 17.5,
        dhuhrOffsetMinutes = 1,
        maghribOffsetMinutes = 1,
    ),
    UMM_AL_QURA(
        id = "uaq",
        nameBn = "উম্মুল কুরা (মক্কা)",
        descriptionBn = "১৮.৫° ফজর ও মাগরিবের ৯০ মিনিট পর এশা",
        fajrAngle = 18.5,
        ishaAngle = 18.0,
        ishaIntervalMinutes = 90,
        dhuhrOffsetMinutes = 1,
        maghribOffsetMinutes = 1,
    );

    companion object {
        fun fromId(id: String): CalculationMethod =
            entries.firstOrNull { it.id == id } ?: ISLAMIC_FOUNDATION_BD
    }
}

enum class AsrMethod(
    val id: String,
    val nameBn: String,
    val descriptionBn: String,
    val shadowFactor: Int,
) {
    HANAFI(
        id = "hanafi",
        nameBn = "হানাফি",
        descriptionBn = "বস্তুর ছায়া দ্বিগুণ হলে আসর (বাংলাদেশে প্রচলিত)",
        shadowFactor = 2,
    ),
    SHAFI(
        id = "shafi",
        nameBn = "শাফিঈ ও অন্যান্য",
        descriptionBn = "বস্তুর ছায়া একগুণ হলে আসর",
        shadowFactor = 1,
    );

    companion object {
        fun fromId(id: String): AsrMethod = entries.firstOrNull { it.id == id } ?: HANAFI
    }
}

enum class ThemePreference(val id: String, val nameBn: String) {
    LIGHT("light", "লাইট থিম"),
    DARK("dark", "ডার্ক থিম"),
    SYSTEM("system", "সিস্টেম অনুযায়ী");

    companion object {
        fun fromId(id: String): ThemePreference = entries.firstOrNull { it.id == id } ?: LIGHT
    }
}

enum class LocationMode(val id: String, val nameBn: String) {
    MANUAL("manual", "ম্যানুয়াল জেলা"),
    AUTO("auto", "স্বয়ংক্রিয় অবস্থান");

    companion object {
        fun fromId(id: String): LocationMode = entries.firstOrNull { it.id == id } ?: MANUAL
    }
}

data class District(
    val id: String,
    val nameBn: String,
    val divisionBn: String,
    val latitude: Double,
    val longitude: Double,
)

data class DailyPrayerTimes(
    val dateIso: String,
    val districtId: String,
    val fajrMillis: Long,
    val sunriseMillis: Long,
    val dhuhrMillis: Long,
    val asrMillis: Long,
    val maghribMillis: Long,
    val ishaMillis: Long,
) {
    fun timeFor(waqt: PrayerWaqt): Long = when (waqt) {
        PrayerWaqt.FAJR -> fajrMillis
        PrayerWaqt.DHUHR -> dhuhrMillis
        PrayerWaqt.ASR -> asrMillis
        PrayerWaqt.MAGHRIB -> maghribMillis
        PrayerWaqt.ISHA -> ishaMillis
    }

    fun allWaqtTimes(): List<Pair<PrayerWaqt, Long>> = listOf(
        PrayerWaqt.FAJR to fajrMillis,
        PrayerWaqt.DHUHR to dhuhrMillis,
        PrayerWaqt.ASR to asrMillis,
        PrayerWaqt.MAGHRIB to maghribMillis,
        PrayerWaqt.ISHA to ishaMillis,
    )
}

data class NextPrayerInfo(
    val waqt: PrayerWaqt,
    val triggerTimeMillis: Long,
    val remainingMillis: Long,
    val isTomorrow: Boolean,
)

data class ReminderSettings(
    val masterEnabled: Boolean = true,
    val azanSoundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val prePrayerOffsetMinutes: Int = 0,
    val fajrEnabled: Boolean = true,
    val dhuhrEnabled: Boolean = true,
    val asrEnabled: Boolean = true,
    val maghribEnabled: Boolean = true,
    val ishaEnabled: Boolean = true,
) {
    fun isWaqtEnabled(waqt: PrayerWaqt): Boolean {
        if (!masterEnabled) return false
        return when (waqt) {
            PrayerWaqt.FAJR -> fajrEnabled
            PrayerWaqt.DHUHR -> dhuhrEnabled
            PrayerWaqt.ASR -> asrEnabled
            PrayerWaqt.MAGHRIB -> maghribEnabled
            PrayerWaqt.ISHA -> ishaEnabled
        }
    }

    fun enabledCount(): Int {
        if (!masterEnabled) return 0
        return PrayerWaqt.entries.count { isWaqtEnabled(it) }
    }
}
