package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.BengaliFormatter
import com.example.domain.LocationMode
import com.example.domain.PrayerWaqt
import com.example.ui.NoorSathiUiState
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.CrescentStarEmblem
import com.example.ui.components.GentleEntrance
import com.example.ui.components.IslamicGeometricPattern
import com.example.ui.components.MosqueSilhouette
import com.example.ui.components.rememberReduceMotion
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LocalNoorSathiColors

@Composable
fun HomeScreen(
    state: NoorSathiUiState,
    onOpenDistrictPicker: () -> Unit,
    onNavigateToTasbih: () -> Unit,
    onNavigateToPrayerTimes: () -> Unit,
    onNavigateToReminders: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extraColors = LocalNoorSathiColors.current
    val reduceMotion = rememberReduceMotion()
    val todayTimes = state.todayPrayerTimes
    val nextPrayer = state.nextPrayer
    val activeDistrict = state.prefs.activeDistrict
    val reminders = state.prefs.reminderSettings
    val activeReminderCount = reminders.enabledCount()

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
            // 1. Emerald Hero Header with Islamic Geometry, Crescent & Mosque Silhouette
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(extraColors.heroStart, extraColors.heroEnd),
                            ),
                        ),
                ) {
                    IslamicGeometricPattern(
                        color = GoldPrimary.copy(alpha = 0.14f),
                        modifier = Modifier.matchParentSize(),
                    )

                    MosqueSilhouette(
                        color = GoldPrimary.copy(alpha = 0.22f),
                        highlightColor = GoldLight.copy(alpha = 0.48f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(82.dp)
                            .align(Alignment.BottomCenter),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CrescentStarEmblem(modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "নূর সাথী",
                            style = MaterialTheme.typography.headlineLarge,
                            color = extraColors.onHero,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "আপনার প্রতিদিনের নামাজ ও ইবাদতের সাথী",
                            style = MaterialTheme.typography.bodySmall,
                            color = extraColors.onHeroMuted,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Greeting, Bangladesh Date & District Card
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EmeraldDeep.copy(alpha = 0.72f),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onOpenDistrictPicker)
                                .testTag("home_district_card"),
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "আসসালামু আলাইকুম",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = GoldLight,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = BengaliFormatter.formatGregorianDateBn(state.nowMillis),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = extraColors.onHero,
                                )
                                Text(
                                    text = "${BengaliFormatter.formatBanglaDate(state.nowMillis)} • ${
                                        BengaliFormatter.formatHijriDateBn(state.nowMillis)
                                    }",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = extraColors.onHeroMuted.copy(alpha = 0.92f),
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = EmeraldPrimary.copy(alpha = 0.9f),
                                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "নির্বাচিত জেলা",
                                            tint = GoldLight,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val modeSuffix = if (state.prefs.locationMode == LocationMode.AUTO) {
                                            " (স্বয়ংক্রিয়)"
                                        } else {
                                            ""
                                        }
                                        Text(
                                            text = "${activeDistrict.nameBn}, বাংলাদেশ$modeSuffix",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = extraColors.onHero,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Next Prayer Countdown Hero Card
            item {
                GentleEntrance(delayMillis = 40, reduceMotion = reduceMotion) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                            .clickable(onClick = onNavigateToPrayerTimes)
                            .testTag("home_next_prayer_card"),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(EmeraldDeep, EmeraldMedium),
                                    ),
                                ),
                        ) {
                            MosqueSilhouette(
                                color = GoldPrimary.copy(alpha = 0.18f),
                                highlightColor = GoldLight.copy(alpha = 0.32f),
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(110.dp)
                                    .align(Alignment.BottomEnd),
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "পরবর্তী নামাজ",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = GoldLight,
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = nextPrayer?.waqt?.nameBn ?: "ফজর",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = extraColors.onHero,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTimeFilled,
                                            contentDescription = null,
                                            tint = GoldLight,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = nextPrayer?.let {
                                                val dayLabel = if (it.isTomorrow) "আগামীকাল " else ""
                                                "$dayLabel${BengaliFormatter.formatTime12Hour(it.triggerTimeMillis, true)}"
                                            } ?: "--:--",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = extraColors.onHero.copy(alpha = 0.92f),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "বাকি সময়",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldLight.copy(alpha = 0.85f),
                                    )
                                    AnimatedCounterText(
                                        text = BengaliFormatter.formatCountdown(nextPrayer?.remainingMillis ?: 0L),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = extraColors.onHero,
                                        reduceMotion = reduceMotion,
                                        modifier = Modifier.testTag("home_countdown_text"),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Five Daily Prayers Strip
            item {
                GentleEntrance(delayMillis = 90, reduceMotion = reduceMotion) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 18.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "নামাজের সময়সূচি",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(onClick = onNavigateToPrayerTimes)
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("home_see_all_prayers"),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "সকল দেখুন",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            val list = todayTimes?.allWaqtTimes() ?: PrayerWaqt.entries.map { it to 0L }
                            list.forEach { (waqt, timeMillis) ->
                                val isNext = nextPrayer != null && !nextPrayer.isTomorrow && nextPrayer.waqt == waqt
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isNext) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    },
                                    border = BorderStroke(
                                        width = if (isNext) 1.5.dp else 1.dp,
                                        color = if (isNext) extraColors.goldAccent else extraColors.cardBorder,
                                    ),
                                    shadowElevation = if (isNext) 3.dp else 1.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable(onClick = onNavigateToPrayerTimes)
                                        .testTag("home_waqt_${waqt.id}"),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(
                                            text = waqt.nameBn,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isNext) {
                                                GoldLight
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (timeMillis > 0L) {
                                                BengaliFormatter.formatTime12Hour(timeMillis)
                                            } else {
                                                "--:--"
                                            },
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isNext) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            },
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Quick Access Cards (Smart Tasbih, Prayer Schedule, Reminder Status)
            item {
                GentleEntrance(delayMillis = 140, reduceMotion = reduceMotion) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "দ্রুত প্রবেশ",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            QuickActionTile(
                                title = "স্মার্ট তসবিহ",
                                subtitle = "আজ: ${BengaliFormatter.toBengaliNumber(state.todayTasbihTotal)} বার",
                                icon = Icons.Default.TouchApp,
                                onClick = onNavigateToTasbih,
                                reduceMotion = reduceMotion,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_quick_tasbih"),
                            )
                            QuickActionTile(
                                title = "নামাজের সময়",
                                subtitle = "${activeDistrict.nameBn} • ৩০ দিন",
                                icon = Icons.Default.Schedule,
                                onClick = onNavigateToPrayerTimes,
                                reduceMotion = reduceMotion,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_quick_prayers"),
                            )
                        }

                        // Reminder Status Card
                        val reminderActive = reminders.masterEnabled && reminders.notificationsEnabled && activeReminderCount > 0
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, extraColors.cardBorder),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onNavigateToReminders)
                                .testTag("home_reminder_status_card"),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (reminderActive) {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                } else {
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                },
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = if (reminderActive) {
                                                Icons.Default.NotificationsActive
                                            } else {
                                                Icons.Default.NotificationsOff
                                            },
                                            contentDescription = "রিমাইন্ডারের অবস্থা",
                                            tint = if (reminderActive) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "নামাজের রিমাইন্ডার",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        val statusDesc = if (reminderActive) {
                                            "সক্রিয় (${BengaliFormatter.toBengaliNumber(activeReminderCount)} ওয়াক্ত)" +
                                                if (reminders.azanSoundEnabled) " • আজান চালু" else " • নীরব"
                                        } else {
                                            "রিমাইন্ডার বন্ধ আছে — চালু করতে স্পর্শ করুন"
                                        }
                                        Text(
                                            text = statusDesc,
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
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reduceMotion) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "quick_tile_scale",
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = EmeraldPrimary,
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
        shadowElevation = 2.dp,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = GoldLight),
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldDeep.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = GoldLight,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LocalNoorSathiColors.current.onHero,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = GoldLight,
            )
        }
    }
}
