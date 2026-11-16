package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.AsrMethod
import com.example.domain.BengaliFormatter
import com.example.domain.CalculationMethod
import com.example.domain.LocationMode
import com.example.domain.PrayerWaqt
import com.example.system.LocationHelper
import com.example.ui.NoorSathiUiState
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.MosqueSilhouette
import com.example.ui.components.rememberReduceMotion
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSoft
import com.example.ui.theme.LocalNoorSathiColors

@Composable
fun PrayerTimesScreen(
    state: NoorSathiUiState,
    onOpenDistrictPicker: () -> Unit,
    onAutoDetectLocation: () -> Unit,
    onSelectCalculationMethod: (CalculationMethod) -> Unit,
    onSelectAsrMethod: (AsrMethod) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onNavigateToReminders: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val extraColors = LocalNoorSathiColors.current
    val reduceMotion = rememberReduceMotion()
    val todayTimes = state.todayPrayerTimes
    val nextPrayer = state.nextPrayer
    val activeDistrict = state.prefs.activeDistrict

    var showMonthlySchedule by remember { mutableStateOf(false) }
    var showMethodsPanel by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) onAutoDetectLocation() else onOpenDistrictPicker()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // 1. Emerald Header + Location & Next Prayer Countdown Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(extraColors.heroStart, extraColors.heroEnd),
                            ),
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                    ) {
                        Text(
                            text = "নামাজের সময়",
                            style = MaterialTheme.typography.headlineMedium,
                            color = extraColors.onHero,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // District + Auto-detect Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "${activeDistrict.nameBn}, বাংলাদেশ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = extraColors.onHero,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = if (state.prefs.locationMode == LocationMode.AUTO) {
                                            "স্বয়ংক্রিয় অবস্থান"
                                        } else {
                                            "${activeDistrict.divisionBn} বিভাগ"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GoldLight,
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = EmeraldDeep.copy(alpha = 0.7f),
                                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f)),
                                    modifier = Modifier
                                        .clickable(enabled = !state.isDetectingLocation) {
                                            if (LocationHelper.hasLocationPermission(context)) {
                                                onAutoDetectLocation()
                                            } else {
                                                locationPermissionLauncher.launch(
                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                )
                                            }
                                        }
                                        .testTag("prayer_auto_location_btn"),
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (state.isDetectingLocation) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = GoldLight,
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.MyLocation,
                                                contentDescription = "স্বয়ংক্রিয় অবস্থান",
                                                tint = GoldLight,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldSoft,
                                    modifier = Modifier
                                        .clickable(onClick = onOpenDistrictPicker)
                                        .testTag("prayer_change_district_btn"),
                                ) {
                                    Text(
                                        text = "অবস্থান পরিবর্তন",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = EmeraldDeep,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Next Prayer Countdown Banner
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EmeraldDeep.copy(alpha = 0.78f),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box {
                                MosqueSilhouette(
                                    color = GoldPrimary.copy(alpha = 0.2f),
                                    highlightColor = GoldLight.copy(alpha = 0.35f),
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(92.dp)
                                        .align(Alignment.BottomEnd),
                                )
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "পরবর্তী নামাজ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = GoldLight,
                                    )
                                    Text(
                                        text = nextPrayer?.waqt?.nameBn ?: "ফজর",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = extraColors.onHero,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AnimatedCounterText(
                                        text = BengaliFormatter.formatCountdown(nextPrayer?.remainingMillis ?: 0L),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = GoldLight,
                                        reduceMotion = reduceMotion,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Five Daily Prayers Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "পাঁচ ওয়াক্ত নামাজের সময়",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (todayTimes != null) {
                                Text(
                                    text = "সূর্যোদয়: ${BengaliFormatter.formatTime12Hour(todayTimes.sunriseMillis)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val waqtList = todayTimes?.allWaqtTimes() ?: PrayerWaqt.entries.map { it to 0L }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            waqtList.forEach { (waqt, timeMillis) ->
                                val isActiveNext = nextPrayer != null && !nextPrayer.isTomorrow && nextPrayer.waqt == waqt
                                val waqtEnabled = state.prefs.reminderSettings.isWaqtEnabled(waqt)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isActiveNext) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                    },
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isActiveNext) extraColors.goldAccent else extraColors.cardBorder,
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("prayer_row_${waqt.id}"),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isActiveNext) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            MaterialTheme.colorScheme.surface
                                                        },
                                                    ),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Icon(
                                                    imageVector = iconForWaqt(waqt),
                                                    contentDescription = waqt.nameBn,
                                                    tint = if (isActiveNext) {
                                                        GoldLight
                                                    } else {
                                                        MaterialTheme.colorScheme.primary
                                                    },
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = waqt.nameBn,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                )
                                                if (isActiveNext) {
                                                    Text(
                                                        text = "পরবর্তী ওয়াক্ত",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                    )
                                                }
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (timeMillis > 0L) {
                                                    BengaliFormatter.formatTime12Hour(timeMillis, true)
                                                } else {
                                                    "--:--"
                                                },
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = if (waqtEnabled && state.prefs.reminderSettings.notificationsEnabled) {
                                                    Icons.Default.NotificationsActive
                                                } else {
                                                    Icons.Default.Notifications
                                                },
                                                contentDescription = null,
                                                tint = if (waqtEnabled && state.prefs.reminderSettings.notificationsEnabled) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                                },
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Notification toggle & full Reminder settings shortcut
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "নামাজের জন্য নোটিফিকেশন",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Switch(
                                checked = state.prefs.reminderSettings.notificationsEnabled,
                                onCheckedChange = onToggleNotifications,
                                modifier = Modifier.testTag("prayer_notif_switch"),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onNavigateToReminders,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("prayer_open_reminders_btn"),
                        ) {
                            Text("আজান ও রিমাইন্ডার বিস্তারিত সেটিংস")
                        }
                    }
                }
            }

            // 3. Calculation & Asr Method Selector (Expandable)
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 14.dp)
                        .animateContentSize(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMethodsPanel = !showMethodsPanel }
                                .testTag("prayer_methods_toggle"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "হিসাব ও আসর পদ্ধতি",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "${state.prefs.calculationMethod.nameBn} • আসর: ${state.prefs.asrMethod.nameBn}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Icon(
                                imageVector = if (showMethodsPanel) {
                                    Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.KeyboardArrowDown
                                },
                                contentDescription = null,
                            )
                        }

                        if (showMethodsPanel) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = extraColors.cardBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "নামাজের সময় গণনার পদ্ধতি",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            CalculationMethod.entries.forEach { method ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectCalculationMethod(method) }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = state.prefs.calculationMethod == method,
                                        onClick = { onSelectCalculationMethod(method) },
                                    )
                                    Column {
                                        Text(
                                            text = method.nameBn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = method.descriptionBn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "আসরের হিসাব পদ্ধতি",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            AsrMethod.entries.forEach { asr ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectAsrMethod(asr) }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = state.prefs.asrMethod == asr,
                                        onClick = { onSelectAsrMethod(asr) },
                                    )
                                    Column {
                                        Text(
                                            text = asr.nameBn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = asr.descriptionBn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. 30-Day Monthly Offline Schedule Button & Expandable Table
            item {
                Button(
                    onClick = { showMonthlySchedule = !showMonthlySchedule },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = EmeraldDeep,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 14.dp)
                        .height(50.dp)
                        .testTag("prayer_monthly_schedule_btn"),
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showMonthlySchedule) {
                            "মাসিক সময়সূচি বন্ধ করুন"
                        } else {
                            "আগামী ৩০ দিনের মাসিক সময়সূচি দেখুন"
                        },
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (showMonthlySchedule) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "তারিখ",
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldLight,
                                modifier = Modifier.weight(1.4f),
                            )
                            listOf("ফজর", "যোহর", "আসর", "মাগরিব", "এশা").forEach { label ->
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.onHero,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                items(state.schedule30Days.take(30), key = { it.dateIso }) { day ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(0.5.dp, extraColors.cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 3.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = BengaliFormatter.formatShortDateFromIso(day.dateIso),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1.4f),
                            )
                            listOf(
                                day.fajrMillis,
                                day.dhuhrMillis,
                                day.asrMillis,
                                day.maghribMillis,
                                day.ishaMillis,
                            ).forEach { millis ->
                                Text(
                                    text = BengaliFormatter.formatTime12Hour(millis),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun iconForWaqt(waqt: PrayerWaqt): ImageVector = when (waqt) {
    PrayerWaqt.FAJR -> Icons.Default.WbTwilight
    PrayerWaqt.DHUHR -> Icons.Default.WbSunny
    PrayerWaqt.ASR -> Icons.Default.Brightness5
    PrayerWaqt.MAGHRIB -> Icons.Default.Brightness6
    PrayerWaqt.ISHA -> Icons.Default.NightsStay
}
