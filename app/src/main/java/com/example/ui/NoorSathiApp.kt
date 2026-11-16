package com.example.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.LocationPickerSheet
import com.example.ui.components.rememberReduceMotion
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrayerRemindersScreen
import com.example.ui.screens.PrayerTimesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.NoorSathiTheme

object NoorSathiRoutes {
    const val HOME = "home"
    const val TASBIH = "tasbih"
    const val PRAYER_TIMES = "prayer_times"
    const val SETTINGS = "settings"
    const val REMINDERS = "reminders"
}

private data class BottomNavItem(
    val route: String,
    val labelBn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(NoorSathiRoutes.HOME, "হোম", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(NoorSathiRoutes.TASBIH, "তসবিহ", Icons.Filled.TouchApp, Icons.Outlined.TouchApp),
    BottomNavItem(NoorSathiRoutes.PRAYER_TIMES, "নামাজ", Icons.Filled.Schedule, Icons.Outlined.Schedule),
    BottomNavItem(NoorSathiRoutes.SETTINGS, "সেটিংস", Icons.Filled.Settings, Icons.Outlined.Settings),
)

@Composable
fun NoorSathiApp(viewModel: NoorSathiViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val completionPulse by viewModel.tasbihCompletionPulse.collectAsStateWithLifecycle()

    NoorSathiTheme(themePreference = state.prefs.themePreference) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: NoorSathiRoutes.HOME
        val snackbarHostState = remember { SnackbarHostState() }
        val reduceMotion = rememberReduceMotion()
        var showDistrictSheet by remember { mutableStateOf(false) }

        LaunchedEffect(state.statusMessageBn) {
            val msg = state.statusMessageBn ?: return@LaunchedEffect
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }

        val navigateTopLevel: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        val showBottomNav = currentRoute != NoorSathiRoutes.REMINDERS

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 700.dp

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (showBottomNav && !useNavigationRail) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp,
                        ) {
                            bottomNavItems.forEach { item ->
                                val selected = currentRoute == item.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(item.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.labelBn,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.labelBn,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                    modifier = Modifier.testTag("nav_item_${item.route}"),
                                )
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    if (showBottomNav && useNavigationRail) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            bottomNavItems.forEach { item ->
                                val selected = currentRoute == item.route
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(item.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.labelBn,
                                        )
                                    },
                                    label = { Text(item.labelBn) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    ),
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        val animDuration = if (reduceMotion) 0 else 240
                        NavHost(
                            navController = navController,
                            startDestination = NoorSathiRoutes.HOME,
                            enterTransition = {
                                fadeIn(tween(animDuration, easing = EaseOutCubic)) +
                                    slideIntoContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                        animationSpec = tween(animDuration, easing = EaseOutCubic),
                                        initialOffset = { it / 14 },
                                    )
                            },
                            exitTransition = {
                                fadeOut(tween(animDuration / 2))
                            },
                            popEnterTransition = {
                                fadeIn(tween(animDuration, easing = EaseOutCubic))
                            },
                            popExitTransition = {
                                fadeOut(tween(animDuration / 2))
                            },
                        ) {
                            composable(NoorSathiRoutes.HOME) {
                                HomeScreen(
                                    state = state,
                                    onOpenDistrictPicker = { showDistrictSheet = true },
                                    onNavigateToTasbih = { navigateTopLevel(NoorSathiRoutes.TASBIH) },
                                    onNavigateToPrayerTimes = { navigateTopLevel(NoorSathiRoutes.PRAYER_TIMES) },
                                    onNavigateToReminders = { navController.navigate(NoorSathiRoutes.REMINDERS) },
                                )
                            }

                            composable(NoorSathiRoutes.TASBIH) {
                                TasbihScreen(
                                    state = state,
                                    completionPulse = completionPulse,
                                    onIncrement = viewModel::incrementTasbih,
                                    onUndo = viewModel::undoTasbih,
                                    onReset = viewModel::resetTasbih,
                                    onSetTarget = viewModel::setTasbihTarget,
                                    onToggleSound = viewModel::setTasbihSound,
                                    onToggleVibration = viewModel::setTasbihVibration,
                                )
                            }

                            composable(NoorSathiRoutes.PRAYER_TIMES) {
                                PrayerTimesScreen(
                                    state = state,
                                    onOpenDistrictPicker = { showDistrictSheet = true },
                                    onAutoDetectLocation = viewModel::detectCurrentLocation,
                                    onSelectCalculationMethod = viewModel::setCalculationMethod,
                                    onSelectAsrMethod = viewModel::setAsrMethod,
                                    onToggleNotifications = viewModel::setNotificationsEnabled,
                                    onNavigateToReminders = { navController.navigate(NoorSathiRoutes.REMINDERS) },
                                )
                            }

                            composable(NoorSathiRoutes.SETTINGS) {
                                SettingsScreen(
                                    state = state,
                                    onOpenDistrictPicker = { showDistrictSheet = true },
                                    onSetLocationMode = viewModel::setLocationMode,
                                    onAutoDetectLocation = viewModel::detectCurrentLocation,
                                    onSetTheme = viewModel::setThemePreference,
                                    onSetCalculationMethod = viewModel::setCalculationMethod,
                                    onSetAsrMethod = viewModel::setAsrMethod,
                                    onToggleAzanSound = viewModel::setAzanSound,
                                    onToggleNotifications = viewModel::setNotificationsEnabled,
                                    onToggleTasbihSound = viewModel::setTasbihSound,
                                    onToggleTasbihVibration = viewModel::setTasbihVibration,
                                    onNavigateToReminders = { navController.navigate(NoorSathiRoutes.REMINDERS) },
                                )
                            }

                            composable(NoorSathiRoutes.REMINDERS) {
                                PrayerRemindersScreen(
                                    state = state,
                                    onBack = { navController.popBackStack() },
                                    onToggleMaster = viewModel::setReminderMaster,
                                    onToggleAzanSound = viewModel::setAzanSound,
                                    onToggleNotifications = viewModel::setNotificationsEnabled,
                                    onSelectPreOffset = viewModel::setPrePrayerOffset,
                                    onToggleWaqt = viewModel::setWaqtReminder,
                                    onSendTestNotification = viewModel::sendTestNotification,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDistrictSheet) {
            LocationPickerSheet(
                selectedDistrictId = state.prefs.selectedDistrictId,
                isDetectingLocation = state.isDetectingLocation,
                onSelectDistrict = viewModel::selectDistrict,
                onAutoDetectLocation = viewModel::detectCurrentLocation,
                onDismiss = { showDistrictSheet = false },
            )
        }
    }
}
