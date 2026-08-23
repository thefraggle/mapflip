package de.goork.mapflip.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.goork.mapflip.AppConstants
import de.goork.mapflip.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    s: Strings.AppStrings,
    currentLangCode: String,
    currentThemePref: String,
    onLanguageSelected: (String) -> Unit,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val focusRequester = remember { FocusRequester() }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Escape) {
                        when {
                            showLanguagePicker -> showLanguagePicker = false
                            showThemePicker -> showThemePicker = false
                            else -> onDismiss()
                        }
                        true
                    } else false
                }
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 4.dp)
        ) {
            when {
                showLanguagePicker -> {
                    // Language picker subview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = s.selectLanguageTitle,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = { showLanguagePicker = false }) {
                            Text(s.btnClose)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                    ) {
                        items(Strings.SUPPORTED_LANGUAGES) { item ->
                            val isSelected = item.code == currentLangCode
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onLanguageSelected(item.code)
                                    showLanguagePicker = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                else Color.Transparent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.flag, fontSize = 20.sp)
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        text = if (item.code == "auto") s.systemLanguageAuto else item.nativeName,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                showThemePicker -> {
                    // Theme picker subview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = s.sectionTheme,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = { showThemePicker = false }) {
                            Text(s.btnClose)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    val themeOptions = listOf(
                        Triple("system", s.themeSystem, Icons.Outlined.Palette),
                        Triple("light", s.themeLight, Icons.Outlined.LightMode),
                        Triple("dark", s.themeDark, Icons.Outlined.DarkMode)
                    )
                    themeOptions.forEach { (mode, title, icon) ->
                        val isSelected = currentThemePref == mode
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onThemeSelected(mode)
                                showThemePicker = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            else Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
                else -> {
                    // Main Settings / About Overview
                    Text(
                        text = s.menuSettingsAbout,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))

                    // 1. Language Option
                    val currentLangItem = Strings.SUPPORTED_LANGUAGES.find { it.code == currentLangCode }
                        ?: Strings.SUPPORTED_LANGUAGES.first()

                    SettingsItem(
                        icon = Icons.Outlined.Language,
                        title = s.sectionLanguage,
                        subtitle = "${currentLangItem.flag} ${if (currentLangItem.code == "auto") s.systemLanguageAuto else currentLangItem.nativeName}",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showLanguagePicker = true
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    // 2. Theme Option (System / Light / Dark)
                    val themeLabel = when (currentThemePref) {
                        "light" -> s.themeLight
                        "dark" -> s.themeDark
                        else -> s.themeSystem
                    }
                    SettingsItem(
                        icon = Icons.Outlined.Palette,
                        title = s.sectionTheme,
                        subtitle = themeLabel,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showThemePicker = true
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    // 3. Support & Feedback
                    SettingsItem(
                        icon = Icons.Outlined.Email,
                        title = s.btnFeedback,
                        subtitle = AppConstants.FEEDBACK_EMAIL,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${AppConstants.FEEDBACK_EMAIL}")
                                putExtra(Intent.EXTRA_SUBJECT, "[MapFlip Feedback]")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )

                    // Flavor Specific: FOSS -> Spende / Ko-fi
                    if (BuildConfig.FLAVOR == "foss") {
                        Spacer(Modifier.height(10.dp))
                        SettingsItem(
                            icon = Icons.Outlined.Favorite,
                            title = s.btnDonate,
                            subtitle = s.donateSubtitle,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_KOFI)))
                                } catch (_: Exception) {}
                            }
                        )
                    }

                    // Flavor Specific: Play -> Rate App & FamWake Promo
                    if (BuildConfig.FLAVOR == "play") {
                        Spacer(Modifier.height(10.dp))
                        SettingsItem(
                            icon = Icons.Outlined.Star,
                            title = s.btnRateApp,
                            subtitle = "Google Play Store",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val rateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                }
                                try {
                                    context.startActivity(rateIntent)
                                } catch (_: Exception) {
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                                    } catch (_: Exception) {}
                                }
                            }
                        )

                        Spacer(Modifier.height(10.dp))
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_FAMWAKE)))
                                } catch (_: Exception) {}
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VolunteerActivism,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = s.famwakeTitle,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = s.famwakeDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
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
fun AppFooter(
    s: Strings.AppStrings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MapFlip v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = s.copyright,
            style = MaterialTheme.typography.bodySmall.copy(textDirection = TextDirection.Ltr),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            modifier = Modifier.clickable {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_NOTTHOFF)))
                } catch (_: Exception) {}
            }
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = s.privacyPolicyTitle,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.URL_PRIVACY_POLICY)))
                } catch (_: Exception) {}
            }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
