package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.BengaliFormatter
import com.example.ui.NoorSathiUiState
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.IslamicGeometricPattern
import com.example.ui.components.TasbihBeadRing
import com.example.ui.components.rememberReduceMotion
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSoft
import com.example.ui.theme.LocalNoorSathiColors
import kotlinx.coroutines.delay

@Composable
fun TasbihScreen(
    state: NoorSathiUiState,
    completionPulse: Long,
    onIncrement: () -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onSetTarget: (Int) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extraColors = LocalNoorSathiColors.current
    val reduceMotion = rememberReduceMotion()
    val prefs = state.prefs
    val count = prefs.tasbihCount
    val target = prefs.tasbihTarget

    var showCustomTargetDialog by remember { mutableStateOf(false) }
    var showHistoryExpanded by remember { mutableStateOf(true) }
    var showCompletionBanner by remember { mutableStateOf(false) }

    LaunchedEffect(completionPulse) {
        if (completionPulse > 0L) {
            showCompletionBanner = true
            delay(2_200L)
            showCompletionBanner = false
        }
    }

    val tapInteraction = remember { MutableInteractionSource() }
    val isPressed by tapInteraction.collectIsPressedAsState()
    val tapScale by animateFloatAsState(
        targetValue = if (isPressed && !reduceMotion) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "tasbih_press_scale",
    )

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
            // Top Emerald Tasbih Zone
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(EmeraldDeep, EmeraldPrimary, EmeraldMedium),
                            ),
                        ),
                ) {
                    IslamicGeometricPattern(
                        color = GoldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.matchParentSize(),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "স্মার্ট তসবিহ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = extraColors.onHero,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "সম্পূর্ণ অফলাইনে জিকির গণনা করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight,
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Central Tappable Tasbih Ring
                        TasbihBeadRing(
                            currentCount = count,
                            targetCount = target,
                            reduceMotion = reduceMotion,
                            modifier = Modifier
                                .size(256.dp)
                                .scale(tapScale)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = tapInteraction,
                                    indication = androidx.compose.material3.ripple(
                                        bounded = true,
                                        color = GoldLight,
                                    ),
                                    onClick = onIncrement,
                                )
                                .testTag("tasbih_tap_circle"),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedCounterText(
                                    text = BengaliFormatter.toBengaliNumber(count),
                                    style = MaterialTheme.typography.displayLarge,
                                    color = extraColors.onHero,
                                    reduceMotion = reduceMotion,
                                    modifier = Modifier.testTag("tasbih_count_display"),
                                )
                                Text(
                                    text = "/ ${BengaliFormatter.toBengaliNumber(target)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = GoldLight,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "গণনা করতে স্পর্শ করুন",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.onHero.copy(alpha = 0.78f),
                                )
                            }
                        }

                        AnimatedVisibility(visible = showCompletionBanner) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = GoldLight,
                                modifier = Modifier.padding(top = 12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldDeep,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "মাশাআল্লাহ! ${BengaliFormatter.toBengaliNumber(target)} বার পূর্ণ হয়েছে",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = EmeraldDeep,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Target Preset Chips: 33, 99, Custom
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TargetChip(
                                label = "৩৩",
                                selected = target == 33,
                                onClick = { onSetTarget(33) },
                                modifier = Modifier.testTag("tasbih_target_33"),
                            )
                            TargetChip(
                                label = "৯৯",
                                selected = target == 99,
                                onClick = { onSetTarget(99) },
                                modifier = Modifier.testTag("tasbih_target_99"),
                            )
                            TargetChip(
                                label = if (target != 33 && target != 99) {
                                    "লক্ষ্য: ${BengaliFormatter.toBengaliNumber(target)}"
                                } else {
                                    "নিজে সেট করুন"
                                },
                                selected = target != 33 && target != 99,
                                onClick = { showCustomTargetDialog = true },
                                modifier = Modifier.testTag("tasbih_target_custom"),
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Reset & Undo Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedButton(
                                onClick = onUndo,
                                enabled = count > 0,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = GoldLight,
                                    disabledContentColor = GoldLight.copy(alpha = 0.4f),
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("tasbih_undo_btn"),
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("পেছনে")
                            }

                            Button(
                                onClick = onReset,
                                enabled = count > 0,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = EmeraldDeep,
                                    disabledContainerColor = GoldPrimary.copy(alpha = 0.35f),
                                    disabledContentColor = EmeraldDeep.copy(alpha = 0.5f),
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("tasbih_reset_btn"),
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("রিসেট")
                            }
                        }
                    }
                }
            }

            // Sound & Vibration Toggles
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TasbihToggleCard(
                        title = "সাউন্ড",
                        checked = prefs.tasbihSoundEnabled,
                        icon = if (prefs.tasbihSoundEnabled) {
                            Icons.AutoMirrored.Filled.VolumeUp
                        } else {
                            Icons.AutoMirrored.Filled.VolumeOff
                        },
                        onCheckedChange = onToggleSound,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tasbih_sound_toggle"),
                    )
                    TasbihToggleCard(
                        title = "কম্পন",
                        checked = prefs.tasbihVibrationEnabled,
                        icon = Icons.Default.Vibration,
                        onCheckedChange = onToggleVibration,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tasbih_vibration_toggle"),
                    )
                }
            }

            // Today's Count & Daily Counting History
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, extraColors.cardBorder),
                    shadowElevation = 2.dp,
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
                                .clickable { showHistoryExpanded = !showHistoryExpanded }
                                .testTag("tasbih_history_header"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "আজকের মোট গণনা",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "${BengaliFormatter.toBengaliNumber(state.todayTasbihTotal)} বার",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = "দৈনিক ইতিহাস",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (showHistoryExpanded) "ইতিহাস লুকান" else "দৈনিক ইতিহাস",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }

                        if (showHistoryExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = extraColors.cardBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            if (state.tasbihHistory.isEmpty()) {
                                Text(
                                    text = "এখনও কোনো দৈনিক ইতিহাস সংরক্ষিত হয়নি। তসবিহ গণনা শুরু করলেই প্রতিটি দিনের হিসাব এখানে দেখা যাবে।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    state.tasbihHistory.take(15).forEach { entry ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${BengaliFormatter.formatDayOfWeekFromIso(entry.dateIso)}, ${
                                                        BengaliFormatter.formatShortDateFromIso(entry.dateIso)
                                                    }",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                )
                                                Text(
                                                    text = "পূর্ণ চক্র: ${BengaliFormatter.toBengaliNumber(entry.completedRounds)} বার",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                            ) {
                                                Text(
                                                    text = "${BengaliFormatter.toBengaliNumber(entry.totalCount)} বার",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCustomTargetDialog) {
        CustomTargetDialog(
            currentTarget = target,
            onConfirm = {
                onSetTarget(it)
                showCustomTargetDialog = false
            },
            onDismiss = { showCustomTargetDialog = false },
        )
    }
}

@Composable
private fun TargetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
            )
        },
        shape = RoundedCornerShape(14.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = EmeraldDeep.copy(alpha = 0.65f),
            labelColor = GoldLight,
            selectedContainerColor = GoldSoft,
            selectedLabelColor = EmeraldDeep,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = GoldPrimary.copy(alpha = 0.4f),
            selectedBorderColor = GoldLight,
        ),
        modifier = modifier.height(48.dp),
    )
}

@Composable
private fun TasbihToggleCard(
    title: String,
    checked: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extraColors = LocalNoorSathiColors.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, extraColors.cardBorder),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

@Composable
private fun CustomTargetDialog(
    currentTarget: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var inputText by remember { mutableStateOf(currentTarget.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "নিজের মতো লক্ষ্য নির্ধারণ",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column {
                Text(
                    text = "১ থেকে ১,০০,০০০ এর মধ্যে আপনার পছন্দের তসবিহ লক্ষ্য লিখুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { raw ->
                        inputText = raw.filter { it.isDigit() }
                        isError = false
                    },
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("গণনার লক্ষ্য") },
                    supportingText = if (isError) {
                        { Text("অনুগ্রহ করে ১ বা তার বেশি সংখ্যা দিন") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_target_input"),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = inputText.toIntOrNull()
                    if (value == null || value < 1) {
                        isError = true
                    } else {
                        onConfirm(value.coerceAtMost(100_000))
                    }
                },
                modifier = Modifier.testTag("custom_target_confirm"),
            ) {
                Text("সংরক্ষণ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        },
    )
}
