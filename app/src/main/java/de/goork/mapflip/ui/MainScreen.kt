package de.goork.mapflip.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.goork.mapflip.AppConstants
import de.goork.mapflip.analytics.Analytics
import de.goork.mapflip.data.PreferencesRepository
import de.goork.mapflip.navigation.NavigationIntentBuilder
import de.goork.mapflip.parser.UniversalMapParser
import de.goork.mapflip.ui.theme.Green500
import de.goork.mapflip.ui.theme.Red500

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
    var detectedClipboardUrl by remember { mutableStateOf<String?>(null) }
    var dismissedClipboardUrl by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val currentStatus = de.goork.mapflip.util.DomainVerificationHelper.checkLinksEnabled(context)
                if (linksActive == false && currentStatus == true) {
                    Analytics.trackEvent("links_activated")
                }
                linksActive = currentStatus

                // Detect map links in clipboard on app open / resume
                val clipText = getClipboardTextSafely(context)
                detectedClipboardUrl = de.goork.mapflip.parser.UniversalMapParser.extractMapUrl(clipText)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(detectedClipboardUrl) {
        if (detectedClipboardUrl != null && detectedClipboardUrl != dismissedClipboardUrl) {
            Analytics.trackEvent("clipboard_banner_shown", mapOf(
                "source_service" to de.goork.mapflip.parser.UniversalMapParser.detectSourceService(detectedClipboardUrl)
            ))
        }
    }

    val isRtl = activeLangCode == "ar"

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
                        // Settings & About Button (opens full settings sheet including language and theme)
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
                                        val parsed = de.goork.mapflip.parser.UniversalMapParser.parse(url)
                                        val targetIntent = de.goork.mapflip.navigation.NavigationIntentBuilder.buildIntent(
                                            parsed, userPreferences.targetApp, context
                                        ).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        Analytics.trackEvent("clipboard_banner_clicked", mapOf(
                                            "source_service" to de.goork.mapflip.parser.UniversalMapParser.detectSourceService(url),
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

                    // Primary Settings CTA Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Analytics.trackEvent("open_system_settings_clicked")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                context.startActivity(Intent(
                                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                                    Uri.parse("package:${context.packageName}")
                                ))
                            } else {
                                context.startActivity(Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${context.packageName}")
                                ))
                            }
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
                            Icons.Rounded.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            s.btnSettings,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Collapsible Setup Instructions Card (Accordion)
                    val guideExpandDesc = if (isSetupGuideExpanded) s.actionCollapse else s.actionExpand
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val willExpand = !isSetupGuideExpanded
                                        isSetupGuideExpanded = willExpand
                                        Analytics.trackEvent("setup_guide_toggled", mapOf("expanded" to willExpand))
                                    }
                                    .semantics {
                                        role = Role.Button
                                        stateDescription = guideExpandDesc
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = s.quickGuideTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = if (isSetupGuideExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                    contentDescription = guideExpandDesc,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(
                                visible = isSetupGuideExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(modifier = Modifier.padding(top = 16.dp)) {
                                    StepItem(number = "1", title = s.step1)
                                    Spacer(Modifier.height(12.dp))
                                    StepItem(number = "2", title = s.step2)
                                    Spacer(Modifier.height(12.dp))
                                    StepItem(number = "3", title = s.step3)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Collapsible Link Tester Card (Accordion)
                    val testerExpandDesc = if (isLinkTesterExpanded) s.actionCollapse else s.actionExpand
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val willExpand = !isLinkTesterExpanded
                                        isLinkTesterExpanded = willExpand
                                        Analytics.trackEvent("tester_toggled", mapOf("expanded" to willExpand))
                                        if (willExpand && testInputUrl.isBlank()) {
                                            val clipText = getClipboardTextSafely(context)
                                            val mapUrl = UniversalMapParser.extractMapUrl(clipText)
                                            if (mapUrl != null) {
                                                testInputUrl = mapUrl
                                            }
                                        }
                                    }
                                    .semantics {
                                        role = Role.Button
                                        stateDescription = testerExpandDesc
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = s.testLinkTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = if (isLinkTesterExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                    contentDescription = testerExpandDesc,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(
                                visible = isLinkTesterExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(modifier = Modifier.padding(top = 16.dp)) {
                                    OutlinedTextField(
                                        value = testInputUrl,
                                        onValueChange = { testInputUrl = it },
                                        placeholder = {
                                            Text(
                                                s.testLinkHint,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Search,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium,
                                        trailingIcon = {
                                            if (testInputUrl.isNotEmpty()) {
                                                IconButton(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        testInputUrl = ""
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Close,
                                                        contentDescription = s.btnClearInput,
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            } else {
                                                TextButton(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        val clipText = getClipboardTextSafely(context)
                                                        if (!clipText.isNullOrBlank()) {
                                                            testInputUrl = clipText.trim()
                                                        }
                                                    },
                                                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                                                ) {
                                                    Text(
                                                        text = s.btnPasteClipboard,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        }
                                    )

                                    AnimatedVisibility(
                                        visible = convertedTargetUri.isNotBlank(),
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Column(Modifier.padding(top = 12.dp)) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                        val clip = ClipData.newPlainText("Converted Map URL", convertedTargetUri)
                                                        clipboard?.setPrimaryClip(clip)
                                                        Toast.makeText(context, s.linkCopiedToast, Toast.LENGTH_SHORT).show()
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = convertedTargetUri,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 12.sp,
                                                            textDirection = TextDirection.Ltr
                                                        ),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Icon(
                                                        imageVector = Icons.Outlined.ContentCopy,
                                                        contentDescription = s.btnCopyLink,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                            Spacer(Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        Analytics.trackEvent("tester_copy_clicked")
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                        val clip = ClipData.newPlainText("Converted Map URL", convertedTargetUri)
                                                        clipboard?.setPrimaryClip(clip)
                                                        Toast.makeText(context, s.linkCopiedToast, Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Outlined.ContentCopy,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(
                                                        text = s.btnCopyLink,
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                                        maxLines = 1
                                                    )
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        Analytics.trackEvent("tester_share_clicked")
                                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                            type = "text/plain"
                                                            putExtra(Intent.EXTRA_TEXT, convertedTargetUri)
                                                        }
                                                        context.startActivity(Intent.createChooser(shareIntent, s.btnShareLink))
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Outlined.Share,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(
                                                        text = s.btnShareLink,
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(8.dp))

                                            FilledTonalButton(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    Analytics.trackEvent("link_tester_used", mapOf(
                                                        "target_app" to userPreferences.targetApp.name.lowercase(),
                                                        "source_service" to UniversalMapParser.detectSourceService(testInputUrl)
                                                    ))
                                                    try {
                                                        val loc = UniversalMapParser.parse(testInputUrl)
                                                        val intent = NavigationIntentBuilder.buildIntent(loc, userPreferences.targetApp, context).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (_: android.content.ActivityNotFoundException) {
                                                        val appName = when (userPreferences.targetApp) {
                                                            de.goork.mapflip.navigation.TargetNavigationApp.SYSTEM_PICKER -> s.sectionTargetApp
                                                            else -> userPreferences.targetApp.displayName
                                                        }
                                                        Toast.makeText(context, s.appNotInstalledToast(appName), Toast.LENGTH_SHORT).show()
                                                    } catch (_: Exception) {
                                                        try {
                                                            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse(convertedTargetUri)).apply {
                                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            }
                                                            context.startActivity(fallback)
                                                        } catch (_: Exception) {
                                                            Toast.makeText(context, s.targetAppOpenError, Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(
                                                    Icons.AutoMirrored.Outlined.OpenInNew,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = s.testButtonLabel(userPreferences.targetApp),
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

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
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}

@Composable
private fun StatusAndControlCard(
    s: Strings.AppStrings,
    isPaused: Boolean,
    linksActive: Boolean?,
    onPauseToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Status Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusDotColor = when {
                    isPaused -> MaterialTheme.colorScheme.onSurfaceVariant
                    linksActive == true -> Green500
                    else -> Red500
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusDotColor)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = when {
                        isPaused -> s.statusPaused
                        linksActive == true -> s.statusActive
                        else -> s.statusInactive
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(14.dp))

            // Pause toggle control row
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.pauseTitle,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = s.pauseDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isPaused,
                        onCheckedChange = onPauseToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StepItem(number: String, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun AppFooter(s: Strings.AppStrings) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${s.headline} v${de.goork.mapflip.BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = s.copyright,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .clickable {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_PRIVACY_POLICY)))
                    } catch (_: Exception) {}
                }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = s.privacyPolicyTitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun ClipboardBanner(
    s: Strings.AppStrings,
    url: String,
    targetApp: de.goork.mapflip.navigation.TargetNavigationApp,
    onOpen: () -> Unit,
    onDismiss: () -> Unit
) {
    val serviceName = when (de.goork.mapflip.parser.UniversalMapParser.detectSourceService(url)) {
        "apple" -> "Apple Maps"
        "bing" -> "Bing Maps"
        "osm" -> "OpenStreetMap"
        "yandex" -> "Yandex Maps"
        "here" -> "HERE WeGo"
        "waze" -> "Waze"
        else -> s.testLinkTitle
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentPaste,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = s.clipboardDetectedTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = serviceName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = s.btnClose,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onOpen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = s.openInButtonLabel(targetApp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

private fun getClipboardTextSafely(context: Context): String? {
    return try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.takeIf { it.hasPrimaryClip() }?.primaryClip
        if (clip != null && clip.itemCount > 0) clip.getItemAt(0)?.text?.toString() else null
    } catch (_: Throwable) {
        null
    }
}

