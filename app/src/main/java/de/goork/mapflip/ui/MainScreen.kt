package de.goork.mapflip.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.navigation.NavigationIntentBuilder
import de.goork.mapflip.parser.UniversalMapParser
import de.goork.mapflip.ui.components.*
import de.goork.mapflip.util.ClipboardUtil
import de.goork.mapflip.util.DomainStatusInfo
import de.goork.mapflip.util.DomainVerificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repository: PreferencesRepository,
    showPauseDialogDefault: Boolean = false,
    onPauseDialogDismissed: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val userPreferences by repository.preferences.collectAsStateWithLifecycle()

    val activeLangCode = Strings.resolveLanguage(userPreferences.language)
    val s = Strings.getStrings(activeLangCode)

    var showPauseSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var isSetupGuideExpanded by remember { mutableStateOf(false) }
    var isLinkTesterExpanded by remember { mutableStateOf(false) }

    var testInputUrl by remember { mutableStateOf("") }
    val convertedTargetUri = remember(testInputUrl, userPreferences.targetApp) {
        if (testInputUrl.isNotBlank()) {
            val loc = UniversalMapParser.parse(testInputUrl)
            NavigationIntentBuilder.buildUriString(loc, userPreferences.targetApp)
        } else ""
    }

    LaunchedEffect(showPauseDialogDefault) {
        if (showPauseDialogDefault) {
            showPauseSheet = true
        }
    }

    var linksActive by remember { mutableStateOf<Boolean?>(null) }
    var domainStatus by remember { mutableStateOf<DomainStatusInfo?>(null) }
    var showSetupGuideSheet by remember { mutableStateOf(false) }
    var detectedClipboardUrl by remember { mutableStateOf<String?>(null) }
    var dismissedClipboardUrl by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val currentDomainStatus = DomainVerificationHelper.getDomainStatus(context)
                domainStatus = currentDomainStatus
                val currentStatus = currentDomainStatus?.let { it.enabledHosts > 0 }
                    ?: DomainVerificationHelper.checkLinksEnabled(context)
                if (linksActive == false && currentStatus == true) {
                    Analytics.trackEvent("links_activated")
                }
                linksActive = currentStatus

                // Detect map links in clipboard on app open / resume
                val clipText = ClipboardUtil.getClipboardTextSafely(context)
                detectedClipboardUrl = UniversalMapParser.extractMapUrl(clipText)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(detectedClipboardUrl) {
        if (detectedClipboardUrl != null && detectedClipboardUrl != dismissedClipboardUrl) {
            Analytics.trackEvent("clipboard_banner_shown", mapOf(
                "source_service" to UniversalMapParser.detectSourceService(detectedClipboardUrl)
            ))
        }
    }

    val isRtl = activeLangCode in listOf("ar", "he", "fa", "ur")

    CompositionLocalProvider(LocalLayoutDirection provides (if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = s.headline,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showSettingsSheet = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = s.menuSettingsAbout,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(Modifier.height(8.dp))

                    // Subtitle / Claim
                    Text(
                        text = s.subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    // Tagline
                    Text(
                        text = s.tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(20.dp))

                    // Clipboard Action Banner (1-Tap Map Redirect when link found in clipboard)
                    val showClipboardBanner = detectedClipboardUrl != null && detectedClipboardUrl != dismissedClipboardUrl
                    AnimatedVisibility(
                        visible = showClipboardBanner,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            ClipboardBanner(
                                s = s,
                                url = detectedClipboardUrl ?: "",
                                targetApp = userPreferences.targetApp,
                                onOpen = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val url = detectedClipboardUrl
                                    if (url != null) {
                                        val parsed = UniversalMapParser.parse(url)
                                        val targetIntent = NavigationIntentBuilder.buildIntent(
                                            parsed, userPreferences.targetApp, context
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        Analytics.trackEvent("clipboard_banner_clicked", mapOf(
                                            "source_service" to UniversalMapParser.detectSourceService(url),
                                            "target_app" to userPreferences.targetApp.name.lowercase()
                                        ))
                                        try {
                                            context.startActivity(targetIntent)
                                        } catch (_: Exception) {
                                            Toast.makeText(
                                                context,
                                                s.targetAppOpenError,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onDismiss = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    dismissedClipboardUrl = detectedClipboardUrl
                                    Analytics.trackEvent("clipboard_banner_dismissed")
                                }
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    // Status & Control Card (Active / Paused Status + Pause Switch)
                    StatusAndControlCard(
                        s = s,
                        isPaused = userPreferences.isPaused,
                        linksActive = linksActive,
                        domainStatus = domainStatus,
                        onStatusClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            Analytics.trackEvent("setup_guide_opened", mapOf("source" to "status_card"))
                            showSetupGuideSheet = true
                        },
                        onPauseToggle = { checked ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (checked) {
                                showPauseSheet = true
                            } else {
                                repository.unpause()
                                Analytics.trackEvent("pause_toggled", mapOf("action" to "unpause"))
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Primary Settings / Setup Guide CTA Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Analytics.trackEvent("setup_guide_opened", mapOf("source" to "main_cta"))
                            showSetupGuideSheet = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = if (linksActive == true && domainStatus?.isFullyEnabled == true) s.menuSetupGuide else s.btnSettings,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Collapsible Setup Instructions Card (Accordion)
                    SetupGuideCard(
                        s = s,
                        isExpanded = isSetupGuideExpanded,
                        onToggleExpand = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val willExpand = !isSetupGuideExpanded
                            isSetupGuideExpanded = willExpand
                            Analytics.trackEvent("setup_guide_toggled", mapOf("expanded" to willExpand))
                        },
                        onOpenFullGuide = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            Analytics.trackEvent("setup_guide_opened", mapOf("source" to "accordion"))
                            showSetupGuideSheet = true
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Collapsible Link Tester Card (Accordion)
                    LinkTesterCard(
                        s = s,
                        isExpanded = isLinkTesterExpanded,
                        testInputUrl = testInputUrl,
                        convertedTargetUri = convertedTargetUri,
                        targetApp = userPreferences.targetApp,
                        context = context,
                        haptic = haptic,
                        onToggleExpand = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val willExpand = !isLinkTesterExpanded
                            isLinkTesterExpanded = willExpand
                            Analytics.trackEvent("tester_toggled", mapOf("expanded" to willExpand))
                            if (willExpand && testInputUrl.isBlank()) {
                                val clipText = ClipboardUtil.getClipboardTextSafely(context)
                                val mapUrl = UniversalMapParser.extractMapUrl(clipText)
                                if (mapUrl != null) {
                                    testInputUrl = mapUrl
                                }
                            }
                        },
                        onInputChange = { testInputUrl = it }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Privacy Note Card (100% datenschutzfreundlich)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(14.dp))
                            Text(
                                text = s.effectivePrivacyNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Standardized App Footer (Version, Copyright, Privacy Policy)
                    AppFooter(s = s)

                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        // Pause Duration BottomSheet
        if (showPauseSheet) {
            PauseBottomSheet(
                s = s,
                repository = repository,
                onDismiss = {
                    showPauseSheet = false
                    onPauseDialogDismissed()
                },
                onPauseConfigured = {
                    showPauseSheet = false
                    onPauseDialogDismissed()
                }
            )
        }

        // Settings & About BottomSheet
        if (showSettingsSheet) {
            SettingsSheet(
                s = s,
                currentLangCode = userPreferences.language,
                currentThemePref = userPreferences.theme,
                currentTargetApp = userPreferences.targetApp,
                onLanguageSelected = { newLang ->
                    repository.setLanguage(newLang)
                },
                onThemeSelected = { newTheme ->
                    repository.setTheme(newTheme)
                },
                onTargetAppSelected = { newApp ->
                    repository.setTargetApp(newApp)
                },
                onOpenSetupGuide = {
                    showSettingsSheet = false
                    showSetupGuideSheet = true
                },
                onDismiss = { showSettingsSheet = false }
            )
        }

        // Setup Guide BottomSheet (Android 12+)
        if (showSetupGuideSheet) {
            SetupGuideBottomSheet(
                onDismissRequest = {
                    Analytics.trackEvent("setup_guide_dismissed")
                    showSetupGuideSheet = false
                },
                onOpenSystemSettings = {
                    Analytics.trackEvent("setup_guide_settings_clicked")
                    showSetupGuideSheet = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        try {
                            context.startActivity(Intent(
                                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                                Uri.parse("package:${context.packageName}")
                            ))
                        } catch (_: Exception) {
                            context.startActivity(Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}")
                            ))
                        }
                    } else {
                        context.startActivity(Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        ))
                    }
                },
                domainStatus = domainStatus,
                s = s
            )
        }
    }
}
