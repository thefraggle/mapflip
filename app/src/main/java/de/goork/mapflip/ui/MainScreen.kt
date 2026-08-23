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
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Collapsible Setup Instructions Card (Accordion)
                    val guideExpandDesc = if (isSetupGuideExpanded) "Einklappen" else "Ausklappen"
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
                                        isSetupGuideExpanded = !isSetupGuideExpanded
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
                    val testerExpandDesc = if (isLinkTesterExpanded) "Einklappen" else "Ausklappen"
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
                                        isLinkTesterExpanded = !isLinkTesterExpanded
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
                                                        val loc = UniversalMapParser.parse(testInputUrl)
                                                        val intent = NavigationIntentBuilder.buildIntent(loc, userPreferences.targetApp).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (_: Exception) {
                                                        try {
                                                            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse(convertedTargetUri)).apply {
                                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            }
                                                            context.startActivity(fallback)
                                                        } catch (_: Exception) {
                                                            Toast.makeText(context, "Target app could not be opened", Toast.LENGTH_SHORT).show()
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                if (isPaused) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = s.statusPaused.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
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

private fun checkLinksEnabled(context: Context): Boolean? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return try {
            val domainVerificationManager = context.getSystemService(
                android.content.pm.verify.domain.DomainVerificationManager::class.java
            )
            val userState = domainVerificationManager?.getDomainVerificationUserState(context.packageName)
            userState?.isLinkHandlingAllowed
        } catch (_: Exception) {
            null
        }
    }
    return null
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
