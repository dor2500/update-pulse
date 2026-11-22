package com.dor2500.updatepulse

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dor2500.updatepulse.data.local.TrackedServiceEntity
import com.dor2500.updatepulse.ui.MainViewModel
import com.dor2500.updatepulse.ui.components.AddEditCustomSheet
import com.dor2500.updatepulse.ui.components.AppUpdateDialog
import com.dor2500.updatepulse.ui.screens.CatalogScreen
import com.dor2500.updatepulse.ui.screens.FeedScreen
import com.dor2500.updatepulse.ui.screens.SettingsScreen
import com.dor2500.updatepulse.ui.theme.UpdatePulseTheme

private enum class NavTab(val labelRes: Int) {
    FEED(R.string.nav_feed),
    CATALOG(R.string.nav_catalog),
    SETTINGS(R.string.nav_settings)
}

class MainActivity : AppCompatActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val viewModel: MainViewModel = viewModel()
            val prefs by viewModel.preferences.collectAsStateWithLifecycle()

            UpdatePulseTheme(
                themeMode = prefs.themeMode,
                dynamicColor = prefs.dynamicColor
            ) {
                UpdatePulseRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun UpdatePulseRoot(viewModel: MainViewModel) {
    val services by viewModel.services.collectAsStateWithLifecycle()
    val feedEvents by viewModel.feedEvents.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val availableAppUpdate by viewModel.availableAppUpdate.collectAsStateWithLifecycle()
    val isCheckingAppUpdate by viewModel.isCheckingAppUpdate.collectAsStateWithLifecycle()
    val isScanningApps by viewModel.isScanningApps.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavTab.FEED) }
    var showCustomSheet by remember { mutableStateOf(false) }
    var editingCustomService by remember { mutableStateOf<TrackedServiceEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (currentTab == NavTab.CATALOG) {
                FloatingActionButton(
                    onClick = {
                        editingCustomService = null
                        showCustomSheet = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.custom_add_fab)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavTab.entries.forEach { tab ->
                    val icon = when (tab) {
                        NavTab.FEED -> Icons.Rounded.DynamicFeed
                        NavTab.CATALOG -> Icons.Rounded.Apps
                        NavTab.SETTINGS -> Icons.Rounded.Settings
                    }
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = {
                            if (tab == NavTab.FEED && unreadCount > 0) {
                                BadgedBox(badge = { Badge { Text("$unreadCount") } }) {
                                    Icon(icon, contentDescription = null)
                                }
                            } else {
                                Icon(icon, contentDescription = null)
                            }
                        },
                        label = { Text(stringResource(tab.labelRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                NavTab.FEED -> FeedScreen(
                    events = feedEvents,
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::checkNow,
                    onMarkRead = viewModel::markEventRead,
                    onMarkAllRead = viewModel::markAllRead,
                    onClearFeed = viewModel::clearFeed
                )
                NavTab.CATALOG -> CatalogScreen(
                    services = services,
                    isScanningApps = isScanningApps,
                    onScanInstalledApps = { viewModel.scanInstalledApps(silentIfNoNew = false) },
                    onToggleSubscription = viewModel::toggleSubscription,
                    onToggleNotifications = viewModel::toggleServiceNotifications,
                    onCheckService = viewModel::checkSingleService
                )
                NavTab.SETTINGS -> SettingsScreen(
                    preferences = prefs,
                    isCheckingAppUpdate = isCheckingAppUpdate,
                    isScanningApps = isScanningApps,
                    onThemeModeChange = viewModel::setThemeMode,
                    onDynamicColorChange = viewModel::setDynamicColor,
                    onLanguageChange = viewModel::setAppLanguage,
                    onPollIntervalChange = viewModel::setPollInterval,
                    onWifiOnlyChange = viewModel::setWifiOnly,
                    onAutoScanAppsChange = viewModel::setAutoScanApps,
                    onScanInstalledApps = { viewModel.scanInstalledApps(silentIfNoNew = false) },
                    onGithubTokenSave = viewModel::setGithubToken,
                    onSendTestNotification = viewModel::sendTestNotification,
                    onClearFeed = viewModel::clearFeed,
                    onAutoCheckAppUpdateChange = viewModel::setAutoCheckAppUpdate,
                    onCheckAppUpdate = { viewModel.checkForAppUpdate(silentIfUpToDate = false) },
                    onExportBackup = viewModel::exportBackup,
                    onImportBackup = viewModel::importBackup
                )
            }
        }
    }

    if (showCustomSheet) {
        AddEditCustomSheet(
            existing = editingCustomService,
            onTestSource = viewModel::testSource,
            onSave = viewModel::saveCustomService,
            onDismiss = { showCustomSheet = false }
        )
    }

    availableAppUpdate?.let { release ->
        AppUpdateDialog(
            release = release,
            downloadState = downloadState,
            onDownloadAndInstall = { viewModel.downloadAndInstallAppUpdate(release) },
            onDismiss = viewModel::dismissAppUpdateDialog
        )
    }
}
