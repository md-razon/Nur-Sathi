package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.AsrMethod
import com.example.domain.BangladeshDistricts
import com.example.domain.CalculationMethod
import com.example.domain.LocationMode
import com.example.domain.PrayerWaqt
import com.example.domain.ReminderSettings
import com.example.domain.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "noor_sathi_prefs")

data class UserPreferencesState(
    val selectedDistrictId: String = BangladeshDistricts.defaultDistrict.id,
    val locationMode: LocationMode = LocationMode.MANUAL,
    val customLatitude: Double? = null,
    val customLongitude: Double? = null,
    val themePreference: ThemePreference = ThemePreference.LIGHT,
    val calculationMethod: CalculationMethod = CalculationMethod.ISLAMIC_FOUNDATION_BD,
    val asrMethod: AsrMethod = AsrMethod.HANAFI,
    val tasbihCount: Int = 0,
    val tasbihPreviousCount: Int = 0,
    val tasbihTarget: Int = 33,
    val tasbihSoundEnabled: Boolean = true,
    val tasbihVibrationEnabled: Boolean = true,
    val reminderSettings: ReminderSettings = ReminderSettings(),
) {
    val activeDistrict get() = BangladeshDistricts.findById(selectedDistrictId)
    val effectiveLatitude: Double
        get() = if (locationMode == LocationMode.AUTO && customLatitude != null) {
            customLatitude
        } else {
            activeDistrict.latitude
        }
    val effectiveLongitude: Double
        get() = if (locationMode == LocationMode.AUTO && customLongitude != null) {
            customLongitude
        } else {
            activeDistrict.longitude
        }
}

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val SELECTED_DISTRICT_ID = stringPreferencesKey("selected_district_id")
        val LOCATION_MODE = stringPreferencesKey("location_mode")
        val CUSTOM_LAT = doublePreferencesKey("custom_lat")
        val CUSTOM_LON = doublePreferencesKey("custom_lon")
        val THEME_PREF = stringPreferencesKey("theme_pref")
        val CALC_METHOD = stringPreferencesKey("calc_method")
        val ASR_METHOD = stringPreferencesKey("asr_method")

        val TASBIH_COUNT = intPreferencesKey("tasbih_count")
        val TASBIH_PREV_COUNT = intPreferencesKey("tasbih_prev_count")
        val TASBIH_TARGET = intPreferencesKey("tasbih_target")
        val TASBIH_SOUND = booleanPreferencesKey("tasbih_sound")
        val TASBIH_VIBRATION = booleanPreferencesKey("tasbih_vibration")

        val REMINDER_MASTER = booleanPreferencesKey("reminder_master")
        val REMINDER_AZAN_SOUND = booleanPreferencesKey("reminder_azan_sound")
        val REMINDER_NOTIFICATIONS = booleanPreferencesKey("reminder_notifications")
        val REMINDER_PRE_OFFSET = intPreferencesKey("reminder_pre_offset")
        val REMINDER_FAJR = booleanPreferencesKey("reminder_fajr")
        val REMINDER_DHUHR = booleanPreferencesKey("reminder_dhuhr")
        val REMINDER_ASR = booleanPreferencesKey("reminder_asr")
        val REMINDER_MAGHRIB = booleanPreferencesKey("reminder_maghrib")
        val REMINDER_ISHA = booleanPreferencesKey("reminder_isha")
    }

    val preferencesFlow: Flow<UserPreferencesState> = context.dataStore.data.map { prefs ->
        UserPreferencesState(
            selectedDistrictId = prefs[Keys.SELECTED_DISTRICT_ID] ?: BangladeshDistricts.defaultDistrict.id,
            locationMode = LocationMode.fromId(prefs[Keys.LOCATION_MODE] ?: LocationMode.MANUAL.id),
            customLatitude = prefs[Keys.CUSTOM_LAT],
            customLongitude = prefs[Keys.CUSTOM_LON],
            themePreference = ThemePreference.fromId(prefs[Keys.THEME_PREF] ?: ThemePreference.LIGHT.id),
            calculationMethod = CalculationMethod.fromId(prefs[Keys.CALC_METHOD] ?: CalculationMethod.ISLAMIC_FOUNDATION_BD.id),
            asrMethod = AsrMethod.fromId(prefs[Keys.ASR_METHOD] ?: AsrMethod.HANAFI.id),
            tasbihCount = prefs[Keys.TASBIH_COUNT] ?: 0,
            tasbihPreviousCount = prefs[Keys.TASBIH_PREV_COUNT] ?: 0,
            tasbihTarget = (prefs[Keys.TASBIH_TARGET] ?: 33).coerceAtLeast(1),
            tasbihSoundEnabled = prefs[Keys.TASBIH_SOUND] ?: true,
            tasbihVibrationEnabled = prefs[Keys.TASBIH_VIBRATION] ?: true,
            reminderSettings = ReminderSettings(
                masterEnabled = prefs[Keys.REMINDER_MASTER] ?: true,
                azanSoundEnabled = prefs[Keys.REMINDER_AZAN_SOUND] ?: true,
                notificationsEnabled = prefs[Keys.REMINDER_NOTIFICATIONS] ?: true,
                prePrayerOffsetMinutes = prefs[Keys.REMINDER_PRE_OFFSET] ?: 0,
                fajrEnabled = prefs[Keys.REMINDER_FAJR] ?: true,
                dhuhrEnabled = prefs[Keys.REMINDER_DHUHR] ?: true,
                asrEnabled = prefs[Keys.REMINDER_ASR] ?: true,
                maghribEnabled = prefs[Keys.REMINDER_MAGHRIB] ?: true,
                ishaEnabled = prefs[Keys.REMINDER_ISHA] ?: true,
            ),
        )
    }

    suspend fun getCurrent(): UserPreferencesState = preferencesFlow.first()

    suspend fun selectManualDistrict(districtId: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_DISTRICT_ID] = districtId
            prefs[Keys.LOCATION_MODE] = LocationMode.MANUAL.id
        }
    }

    suspend fun setAutoLocation(districtId: String, latitude: Double, longitude: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_DISTRICT_ID] = districtId
            prefs[Keys.LOCATION_MODE] = LocationMode.AUTO.id
            prefs[Keys.CUSTOM_LAT] = latitude
            prefs[Keys.CUSTOM_LON] = longitude
        }
    }

    suspend fun setLocationMode(mode: LocationMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LOCATION_MODE] = mode.id
        }
    }

    suspend fun setThemePreference(theme: ThemePreference) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_PREF] = theme.id
        }
    }

    suspend fun setCalculationMethod(method: CalculationMethod) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CALC_METHOD] = method.id
        }
    }

    suspend fun setAsrMethod(method: AsrMethod) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ASR_METHOD] = method.id
        }
    }

    suspend fun updateTasbihCount(newCount: Int, previousCount: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TASBIH_COUNT] = newCount.coerceAtLeast(0)
            prefs[Keys.TASBIH_PREV_COUNT] = previousCount.coerceAtLeast(0)
        }
    }

    suspend fun setTasbihTarget(target: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TASBIH_TARGET] = target.coerceIn(1, 100_000)
        }
    }

    suspend fun setTasbihSound(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.TASBIH_SOUND] = enabled }
    }

    suspend fun setTasbihVibration(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.TASBIH_VIBRATION] = enabled }
    }

    suspend fun setReminderMaster(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.REMINDER_MASTER] = enabled }
    }

    suspend fun setAzanSound(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.REMINDER_AZAN_SOUND] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.REMINDER_NOTIFICATIONS] = enabled }
    }

    suspend fun setPrePrayerOffset(minutes: Int) {
        context.dataStore.edit { prefs -> prefs[Keys.REMINDER_PRE_OFFSET] = minutes }
    }

    suspend fun setWaqtReminder(waqt: PrayerWaqt, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            when (waqt) {
                PrayerWaqt.FAJR -> prefs[Keys.REMINDER_FAJR] = enabled
                PrayerWaqt.DHUHR -> prefs[Keys.REMINDER_DHUHR] = enabled
                PrayerWaqt.ASR -> prefs[Keys.REMINDER_ASR] = enabled
                PrayerWaqt.MAGHRIB -> prefs[Keys.REMINDER_MAGHRIB] = enabled
                PrayerWaqt.ISHA -> prefs[Keys.REMINDER_ISHA] = enabled
            }
        }
    }
}
