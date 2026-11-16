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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.example.domain.AsrMethod
import com.example.domain.CalculationMethod
import com.example.domain.LocationMode
import com.example.domain.ThemePreference
import com.example.system.LocationHelper
import com.example.ui.NoorSathiUiState
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSoft
import com.example.ui.theme.LocalNoorSathiColors

@Composable
fun SettingsScreen(
    state: NoorSathiUiState,
    onOpenDistrictPicker: () -> Unit,
    onSetLocationMode: (LocationMode) -> Unit,
    onAutoDetectLocation: () -> Unit,
    onSetTheme: (ThemePreference) -> Unit,
    onSetCalculationMethod: (CalculationMethod) -> Unit,
    onSetAsrMethod: (AsrMethod) -> Unit,
    onToggleAzanSound: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleTasbihSound: (Boolean) -> Unit,
    onToggleTasbihVibration: (Boolean) -> Unit,
    onNavigateToReminders: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val extraColors = LocalNoorSathiColors.current
    val prefs = state.prefs

    var expandCalcMethod by remember { mutableStateOf(false) }
    var expandAsrMethod by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            onAutoDetectLocation()
        } else {
            onSetLocationMode(LocationMode.MANUAL)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
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
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "সেটিংস",
                            style = MaterialTheme.typography.headlineMedium,
                            color = extraColors.onHero,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "আপনার পছন্দ অনুযায়ী নূর সাথী সাজিয়ে নিন",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight,
                        )
                    }
                }
            }

            // 1. Current District & Auto/Manual Location Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "বর্তমান অবস্থান",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = "${prefs.activeDistrict.nameBn}, বাংলাদেশ",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldSoft,
                                modifier = Modifier
                                    .clickable(onClick = onOpenDistrictPicker)
                                    .testTag("settings_change_district_btn"),
                            ) {
                                Text(
                                    text = "জেলা নির্বাচন",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = EmeraldDeep,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = extraColors.cardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "লোকেশন ধরন",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilterChip(
                                selected = prefs.locationMode == LocationMode.MANUAL,
                                onClick = { onSetLocationMode(LocationMode.MANUAL) },
                                label = { Text("ম্যানুয়াল জেলা") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                            FilterChip(
                                selected = prefs.locationMode == LocationMode.AUTO,
                                onClick = {
                                    if (LocationHelper.hasLocationPermission(context)) {
                                        onAutoDetectLocation()
                                    } else {
                                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                    }
                                },
                                label = { Text("স্বয়ংক্রিয় অবস্থান") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // 2. Theme Selection (Light / Dark / System)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "থিম নির্বাচন",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ThemePreference.entries.forEach { option ->
                                FilterChip(
                                    selected = prefs.themePreference == option,
                                    onClick = { onSetTheme(option) },
                                    label = {
                                        Text(
                                            text = when (option) {
                                                ThemePreference.LIGHT -> "লাইট"
                                                ThemePreference.DARK -> "ডার্ক"
                                                ThemePreference.SYSTEM -> "সিস্টেম"
                                            },
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("theme_chip_${option.id}"),
                                )
                            }
                        }
                    }
                }
            }

            // 3. Calculation Method (Expandable)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateContentSize(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandCalcMethod = !expandCalcMethod }
                                .testTag("settings_calc_method_header"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "নামাজের সময় গণনার পদ্ধতি",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = prefs.calculationMethod.nameBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (expandCalcMethod) {
                                    Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.KeyboardArrowDown
                                },
                                contentDescription = null,
                            )
                        }

                        if (expandCalcMethod) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = extraColors.cardBorder)
                            Spacer(modifier = Modifier.height(6.dp))
                            CalculationMethod.entries.forEach { method ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSetCalculationMethod(method) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = prefs.calculationMethod == method,
                                        onClick = { onSetCalculationMethod(method) },
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
                        }
                    }
                }
            }

            // 4. Asr Calculation Method (Expandable)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateContentSize(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandAsrMethod = !expandAsrMethod }
                                .testTag("settings_asr_method_header"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "আসরের হিসাব পদ্ধতি",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = prefs.asrMethod.nameBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (expandAsrMethod) {
                                    Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.KeyboardArrowDown
                                },
                                contentDescription = null,
                            )
                        }

                        if (expandAsrMethod) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = extraColors.cardBorder)
                            Spacer(modifier = Modifier.height(6.dp))
                            AsrMethod.entries.forEach { method ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSetAsrMethod(method) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = prefs.asrMethod == method,
                                        onClick = { onSetAsrMethod(method) },
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
                        }
                    }
                }
            }

            // 5. Azan, Notifications, and Tasbih Sound & Vibration
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SettingsSwitchRow(
                            title = "নোটিফিকেশন",
                            subtitle = "প্রতি ওয়াক্তে নামাজের রিমাইন্ডার",
                            icon = Icons.Default.NotificationsActive,
                            checked = prefs.reminderSettings.notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                        )
                        HorizontalDivider(color = extraColors.cardBorder)
                        SettingsSwitchRow(
                            title = "আজানের শব্দ",
                            subtitle = "রিমাইন্ডার নোটিফিকেশনে মৃদু আজানের ধ্বনি",
                            icon = Icons.AutoMirrored.Filled.VolumeUp,
                            checked = prefs.reminderSettings.azanSoundEnabled,
                            onCheckedChange = onToggleAzanSound,
                        )
                        HorizontalDivider(color = extraColors.cardBorder)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onNavigateToReminders)
                                .padding(vertical = 8.dp)
                                .testTag("settings_reminders_row"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "বিস্তারিত রিমাইন্ডার সেটিংস",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        HorizontalDivider(color = extraColors.cardBorder)
                        SettingsSwitchRow(
                            title = "তসবিহ সাউন্ড",
                            subtitle = "গণনার সময় হালকা শব্দ",
                            icon = Icons.AutoMirrored.Filled.VolumeUp,
                            checked = prefs.tasbihSoundEnabled,
                            onCheckedChange = onToggleTasbihSound,
                        )
                        HorizontalDivider(color = extraColors.cardBorder)
                        SettingsSwitchRow(
                            title = "তসবিহ কম্পন",
                            subtitle = "প্রতি স্পর্শে মৃদু ভাইব্রেশন",
                            icon = Icons.Default.Vibration,
                            checked = prefs.tasbihVibrationEnabled,
                            onCheckedChange = onToggleTasbihVibration,
                        )
                    }
                }
            }

            // 6. Privacy Policy & About App
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        SettingsActionRow(
                            title = "গোপনীয়তা নীতি",
                            subtitle = "তথ্য সুরক্ষা ও অনুমতি সংক্রান্ত নীতিমালা",
                            icon = Icons.Default.Policy,
                            onClick = { showPrivacyDialog = true },
                            modifier = Modifier.testTag("settings_privacy_btn"),
                        )
                        HorizontalDivider(color = extraColors.cardBorder)
                        SettingsActionRow(
                            title = "অ্যাপ পরিচিতি",
                            subtitle = "নূর সাথী সংস্করণ ১.০",
                            icon = Icons.Default.Info,
                            onClick = { showAboutDialog = true },
                            modifier = Modifier.testTag("settings_about_btn"),
                        )
                    }
                }
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("গোপনীয়তা নীতি") },
            text = {
                Text(
                    text = "নূর সাথী একটি সম্পূর্ণ গোপনীয়তা-বান্ধব ও অফলাইন-সক্ষম অ্যাপ্লিকেশন।\n\n" +
                        "১. ব্যক্তিগত তথ্য: আমরা কোনো নাম, ইমেইল, ফোন নম্বর বা অ্যাকাউন্টের তথ্য সংগ্রহ করি না।\n" +
                        "২. অবস্থান (Location): আপনি স্বয়ংক্রিয় অবস্থান চালু করলে কেবল আপনার নিকটতম বাংলাদেশের জেলা ও সঠিক নামাজের সময় হিসাবের জন্য ডিভাইসের অবস্থান ব্যবহৃত হয়; এটি কোনো সার্ভারে পাঠানো হয় না।\n" +
                        "৩. অফলাইন সংরক্ষণ: আপনার নির্বাচিত জেলা, তসবিহ গণনার ইতিহাস এবং রিমাইন্ডার সেটিংস কেবল আপনার ডিভাইসেই সংরক্ষিত থাকে।\n" +
                        "৪. নোটিফিকেশন: সময়মতো নামাজের সতর্কতা পাঠানোর জন্যই কেবল নোটিফিকেশন অনুমতি চাওয়া হয়।",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("বুঝেছি")
                }
            },
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("নূর সাথী সম্পর্কে") },
            text = {
                Text(
                    text = "নূর সাথী (সংস্করণ ১.০)\n\n" +
                        "বাংলাদেশের মুসলিম ভাই-বোনদের জন্য নির্মিত একটি আধুনিক ও নির্ভরযোগ্য ইবাদত সহায়ক অ্যাপ।\n\n" +
                        "• বাংলাদেশের ৬৪টি জেলার সঠিক সূর্য-ভিত্তিক নামাজের সময়সূচি\n" +
                        "• আগামী ৩০ দিনের অফলাইন সময়সূচি ও স্বয়ংক্রিয় রিমাইন্ডার\n" +
                        "• দৈনিক ইতিহাসসহ সম্পূর্ণ অফলাইন স্মার্ট তসবিহ\n" +
                        "• বাংলাদেশ সময় (UTC+6) এবং বাংলা, ইংরেজি ও হিজরি তারিখ",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("বন্ধ করুন")
                }
            },
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
