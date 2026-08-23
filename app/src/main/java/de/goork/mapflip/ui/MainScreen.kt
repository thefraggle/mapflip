package de.goork.mapflip.ui

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
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.goork.mapflip.AppConstants
import de.goork.mapflip.AppleMapsParser
import de.goork.mapflip.data.PreferencesRepository
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
    val convertedTargetUri = remember(testInputUrl) {
        if (testInputUrl.isNotBlank()) AppleMapsParser.convert(testInputUrl) else ""
    }

    LaunchedEffect(showPauseDialogDefault) {
        if (showPauseDialogDefault) {
            showPauseSheet = true
        }
    }

    var linksActive by remember { mutableStateOf<Boolean?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                linksActive = checkLinksEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                val isWideScreen = maxWidth >= 600.dp
                val contentModifier = if (isWideScreen) {
                    Modifier
                        .width(560.dp)
                        .verticalScroll(rememberScrollState())
                } else {
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                }

                Column(
                    modifier = contentModifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animation
                    MapFlipAnimation()

                    Spacer(Modifier.height(12.dp))

                    // Subtitle & Tagline
                    Text(
                        text = s.subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = s.tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(24.dp))

                    // Main Status & Control Card
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
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Primary Settings CTA Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                                letterSpacing = 0.3.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Setup Accordion / Quick Guide
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
                                .padding(18.dp)
                        ) {
                            val isGuideVisible = isSetupGuideExpanded || linksActive == false || linksActive == null
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .semantics {
                                        role = Role.Button
                                        stateDescription = if (isGuideVisible) "Expanded" else "Collapsed"
                                    }
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        isSetupGuideExpanded = !isGuideVisible
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = s.setupTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = if (isGuideVisible) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                    contentDescription = if (isGuideVisible) s.quickGuideTitle else s.setupTitle,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Expandable Setup Steps (default open when inactive/first start or explicitly expanded)
                            AnimatedVisibility(
                                visible = isGuideVisible,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(Modifier.padding(top = 16.dp)) {
                                    SetupStep(number = 1, text = s.step1, isLast = false)
                                    SetupStep(number = 2, text = s.step2, isLast = false)
                                    SetupStep(number = 3, text = s.step3, isLast = true)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Link Tester Tool Card (collapsible accordion)
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
                        Column(Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .semantics {
                                        role = Role.Button
                                        stateDescription = if (isLinkTesterExpanded) "Expanded" else "Collapsed"
                                    }
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        isLinkTesterExpanded = !isLinkTesterExpanded
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = s.testLinkTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = if (isLinkTesterExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                    contentDescription = s.testLinkTitle,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(
                                visible = isLinkTesterExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(Modifier.padding(top = 14.dp)) {
                                    OutlinedTextField(
                                        value = testInputUrl,
                                        onValueChange = { testInputUrl = it },
                                        placeholder = {
                                            Text(
                                                text = s.testLinkHint,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        trailingIcon = {
                                            TextButton(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                    val clipText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                                    if (!clipText.isNullOrBlank()) {
                                                        testInputUrl = clipText.trim()
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = s.btnPasteClipboard,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
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
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = convertedTargetUri,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 12.sp,
                                                        textDirection = TextDirection.Ltr
                                                    ),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                            }
                                            Spacer(Modifier.height(10.dp))
                                            FilledTonalButton(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    try {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(convertedTargetUri)).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (_: Exception) {
                                                        Toast.makeText(context, "Google Maps could not be opened", Toast.LENGTH_SHORT).show()
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
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = s.btnTestLink,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
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
                                text = s.privacyNote,
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
                onLanguageSelected = { newLang ->
                    repository.setLanguage(newLang)
                },
                onThemeSelected = { newTheme ->
                    repository.setTheme(newTheme)
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
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            // Live Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusIndicatorDot(active = linksActive == true && !isPaused, isPaused = isPaused)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val statusText = when {
                        isPaused -> s.statusPaused
                        linksActive == true -> s.statusActive
                        linksActive == false -> s.statusInactive
                        else -> s.statusHint
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))

            // Pause Quick Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.pauseTitle,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = s.pauseDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Switch(
                    checked = isPaused,
                    onCheckedChange = onPauseToggle
                )
            }
        }
    }
}

@Composable
private fun StatusIndicatorDot(active: Boolean, isPaused: Boolean) {
    val targetColor = when {
        isPaused -> MaterialTheme.colorScheme.tertiary
        active -> Green500
        else -> Red500
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(24.dp)
    ) {
        if (active) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        alpha = pulseAlpha
                    }
                    .clip(CircleShape)
                    .background(targetColor)
            )
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(targetColor)
        )
    }
}

@Composable
private fun SetupStep(number: Int, text: String, isLast: Boolean) {
    Row(
        modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            modifier = Modifier.size(30.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun checkLinksEnabled(context: Context): Boolean? {
    val prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
    if (prefs.getBoolean("mock_links_active", false)) {
        return true
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return try {
            val manager = context.getSystemService(
                android.content.pm.verify.domain.DomainVerificationManager::class.java
            )
            val userState = manager.getDomainVerificationUserState(context.packageName)
            val hostMap = userState?.hostToStateMap ?: return false
            hostMap.any { (host, state) ->
                host == "maps.apple.com" &&
                        state == android.content.pm.verify.domain.DomainVerificationUserState.DOMAIN_STATE_SELECTED
            }
        } catch (_: Exception) {
            null
        }
    }
    return null
}
