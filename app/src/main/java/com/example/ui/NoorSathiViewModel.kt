package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NoorSathiApplication
import com.example.data.local.TasbihHistoryEntity
import com.example.data.preferences.UserPreferencesState
import com.example.domain.AsrMethod
import com.example.domain.CalculationMethod
import com.example.domain.DailyPrayerTimes
import com.example.domain.District
import com.example.domain.LocationMode
import com.example.domain.NextPrayerInfo
import com.example.domain.PrayerTimeCalculator
import com.example.domain.PrayerWaqt
import com.example.domain.ThemePreference
import com.example.system.LocationDetectResult
import com.example.system.LocationHelper
import com.example.system.NotificationHelper
import com.example.system.PrayerAlarmScheduler
import com.example.system.TasbihFeedbackHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoorSathiUiState(
    val prefs: UserPreferencesState = UserPreferencesState(),
    val schedule30Days: List<DailyPrayerTimes> = emptyList(),
    val todayPrayerTimes: DailyPrayerTimes? = null,
    val nextPrayer: NextPrayerInfo? = null,
    val nowMillis: Long = System.currentTimeMillis(),
    val todayTasbihTotal: Int = 0,
    val tasbihHistory: List<TasbihHistoryEntity> = emptyList(),
    val isDetectingLocation: Boolean = false,
    val statusMessageBn: String? = null,
    val isLoadingSchedule: Boolean = true,
)

class NoorSathiViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NoorSathiApplication
    private val prefsRepo = app.preferencesRepository
    private val prayerRepo = app.prayerRepository
    private val tasbihRepo = app.tasbihRepository
    private val feedbackHelper = TasbihFeedbackHelper(application)

    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    private val _isDetectingLocation = MutableStateFlow(false)
    private val _statusMessageBn = MutableStateFlow<String?>(null)
    private val _isLoadingSchedule = MutableStateFlow(true)
    private val _tasbihCompletionPulse = MutableStateFlow(0L)
    val tasbihCompletionPulse: StateFlow<Long> = _tasbihCompletionPulse.asStateFlow()

    val uiState: StateFlow<NoorSathiUiState> = combine(
        prefsRepo.preferencesFlow,
        prayerRepo.observeNext30Days(),
        _nowMillis,
        tasbihRepo.observeTodayCount(),
        tasbihRepo.observeHistory(),
    ) { prefs, schedule, nowMillis, todayTasbih, history ->
        val todayIso = PrayerTimeCalculator.dateIsoFromMillis(nowMillis)
        val tomorrowIso = PrayerTimeCalculator.addDaysToIso(todayIso, 1)
        val todayTimes = schedule.firstOrNull { it.dateIso == todayIso }
            ?: PrayerTimeCalculator.calculateForDate(
                dateIso = todayIso,
                districtId = prefs.selectedDistrictId,
                latitude = prefs.effectiveLatitude,
                longitude = prefs.effectiveLongitude,
                calculationMethod = prefs.calculationMethod,
                asrMethod = prefs.asrMethod,
            )
        val tomorrowTimes = schedule.firstOrNull { it.dateIso == tomorrowIso }
            ?: PrayerTimeCalculator.calculateForDate(
                dateIso = tomorrowIso,
                districtId = prefs.selectedDistrictId,
                latitude = prefs.effectiveLatitude,
                longitude = prefs.effectiveLongitude,
                calculationMethod = prefs.calculationMethod,
                asrMethod = prefs.asrMethod,
            )
        val nextPrayer = PrayerTimeCalculator.findNextPrayer(todayTimes, tomorrowTimes, nowMillis)

        NoorSathiUiState(
            prefs = prefs,
            schedule30Days = schedule,
            todayPrayerTimes = todayTimes,
            nextPrayer = nextPrayer,
            nowMillis = nowMillis,
            todayTasbihTotal = todayTasbih,
            tasbihHistory = history,
            isDetectingLocation = _isDetectingLocation.value,
            statusMessageBn = _statusMessageBn.value,
            isLoadingSchedule = _isLoadingSchedule.value && schedule.isEmpty(),
        )
    }.combine(_isDetectingLocation) { state, detecting ->
        state.copy(isDetectingLocation = detecting)
    }.combine(_statusMessageBn) { state, message ->
        state.copy(statusMessageBn = message)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoorSathiUiState(),
    )

    init {
        startClockTicker()
        observeScheduleAndAlarmSync()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (true) {
                _nowMillis.value = System.currentTimeMillis()
                delay(1_000L)
            }
        }
    }

    private fun observeScheduleAndAlarmSync() {
        prefsRepo.preferencesFlow
            .distinctUntilChanged { old, new ->
                old.selectedDistrictId == new.selectedDistrictId &&
                    old.locationMode == new.locationMode &&
                    old.customLatitude == new.customLatitude &&
                    old.customLongitude == new.customLongitude &&
                    old.calculationMethod == new.calculationMethod &&
                    old.asrMethod == new.asrMethod &&
                    old.reminderSettings == new.reminderSettings
            }
            .onEach { prefs ->
                _isLoadingSchedule.value = true
                val schedule = prayerRepo.ensureScheduleCached(prefs)
                PrayerAlarmScheduler.rescheduleUpcomingAlarms(app, schedule, prefs)
                _isLoadingSchedule.value = false
            }
            .launchIn(viewModelScope)
    }

    fun clearStatusMessage() {
        _statusMessageBn.value = null
    }

    fun selectDistrict(district: District) {
        viewModelScope.launch {
            prefsRepo.selectManualDistrict(district.id)
            _statusMessageBn.value = "${district.nameBn} জেলার নামাজের সময়সূচি নির্ধারিত হয়েছে।"
        }
    }

    fun setLocationMode(mode: LocationMode) {
        viewModelScope.launch {
            prefsRepo.setLocationMode(mode)
        }
    }

    fun detectCurrentLocation() {
        if (_isDetectingLocation.value) return
        viewModelScope.launch {
            _isDetectingLocation.value = true
            when (val result = LocationHelper.detectBangladeshDistrict(app)) {
                is LocationDetectResult.Success -> {
                    prefsRepo.setAutoLocation(
                        districtId = result.district.id,
                        latitude = result.latitude,
                        longitude = result.longitude,
                    )
                    _statusMessageBn.value = if (result.isWithinBangladesh) {
                        "স্বয়ংক্রিয় অবস্থান: ${result.district.nameBn}, বাংলাদেশ"
                    } else {
                        "বাংলাদেশের নিকটতম জেলা ${result.district.nameBn} নির্বাচন করা হয়েছে।"
                    }
                }
                is LocationDetectResult.Error -> {
                    _statusMessageBn.value = result.messageBn
                }
            }
            _isDetectingLocation.value = false
        }
    }

    fun setThemePreference(theme: ThemePreference) {
        viewModelScope.launch { prefsRepo.setThemePreference(theme) }
    }

    fun setCalculationMethod(method: CalculationMethod) {
        viewModelScope.launch {
            prefsRepo.setCalculationMethod(method)
            _statusMessageBn.value = "গণনা পদ্ধতি: ${method.nameBn}"
        }
    }

    fun setAsrMethod(method: AsrMethod) {
        viewModelScope.launch {
            prefsRepo.setAsrMethod(method)
            _statusMessageBn.value = "আসরের হিসাব: ${method.nameBn}"
        }
    }

    // --- Smart Tasbih Actions ---

    fun incrementTasbih() {
        val currentPrefs = uiState.value.prefs
        val current = currentPrefs.tasbihCount
        val target = currentPrefs.tasbihTarget
        val next = current + 1
        val completedRound = next > 0 && next % target == 0

        feedbackHelper.playFeedback(
            soundEnabled = currentPrefs.tasbihSoundEnabled,
            vibrationEnabled = currentPrefs.tasbihVibrationEnabled,
            isTargetCompleted = completedRound,
        )
        if (completedRound) {
            _tasbihCompletionPulse.value = System.currentTimeMillis()
        }

        viewModelScope.launch {
            prefsRepo.updateTasbihCount(newCount = next, previousCount = current)
            tasbihRepo.recordIncrement(completedRound = completedRound)
        }
    }

    fun undoTasbih() {
        val currentPrefs = uiState.value.prefs
        val current = currentPrefs.tasbihCount
        if (current <= 0) return
        val target = currentPrefs.tasbihTarget
        val undidCompletedRound = current % target == 0
        val newCount = (current - 1).coerceAtLeast(0)

        viewModelScope.launch {
            prefsRepo.updateTasbihCount(newCount = newCount, previousCount = current)
            tasbihRepo.recordUndo(undidCompletedRound = undidCompletedRound)
        }
    }

    fun resetTasbih() {
        val current = uiState.value.prefs.tasbihCount
        if (current == 0) return
        viewModelScope.launch {
            prefsRepo.updateTasbihCount(newCount = 0, previousCount = current)
        }
    }

    fun setTasbihTarget(target: Int) {
        viewModelScope.launch {
            prefsRepo.setTasbihTarget(target)
        }
    }

    fun setTasbihSound(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setTasbihSound(enabled) }
    }

    fun setTasbihVibration(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setTasbihVibration(enabled) }
    }

    // --- Reminder Actions ---

    fun setReminderMaster(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setReminderMaster(enabled) }
    }

    fun setAzanSound(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setAzanSound(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setNotificationsEnabled(enabled) }
    }

    fun setPrePrayerOffset(minutes: Int) {
        viewModelScope.launch { prefsRepo.setPrePrayerOffset(minutes) }
    }

    fun setWaqtReminder(waqt: PrayerWaqt, enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setWaqtReminder(waqt, enabled) }
    }

    fun sendTestNotification() {
        val prefs = uiState.value.prefs
        if (!NotificationHelper.hasNotificationPermission(app)) {
            _statusMessageBn.value = "নোটিফিকেশন পাঠাতে অনুগ্রহ করে অনুমতি প্রদান করুন।"
            return
        }
        NotificationHelper.showTestNotification(
            context = app,
            azanSoundEnabled = prefs.reminderSettings.azanSoundEnabled,
            districtNameBn = prefs.activeDistrict.nameBn,
        )
        _statusMessageBn.value = "পরীক্ষামূলক নোটিফিকেশন পাঠানো হয়েছে।"
    }

    override fun onCleared() {
        super.onCleared()
        feedbackHelper.release()
    }
}
