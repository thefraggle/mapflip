package de.goork.mapflip.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import de.goork.mapflip.ui.theme.Green500
import de.goork.mapflip.ui.theme.Red500
import java.util.Locale

private const val PREFS_NAME = "mapflip"
private const val PREFS_KEY_LANG = "lang"
private const val PREFS_KEY_PAUSED = "is_paused"
private const val URL_FAMWAKE = "https://play.google.com/store/apps/details?id=de.familienwecker.famwake"
private const val URL_NOTTHOFF = "https://notthoff.org"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val systemLang = Locale.getDefault().language
    var lang by remember {
        mutableStateOf(prefs.getString(PREFS_KEY_LANG, if (systemLang == "de") "de" else "en") ?: "en")
    }
    var isPaused by remember { mutableStateOf(prefs.getBoolean(PREFS_KEY_PAUSED, false)) }
    val s = if (lang == "de") Strings.DE else Strings.EN

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

    val surfaceColor = MaterialTheme.colorScheme.surface

    Scaffold(
        containerColor = surfaceColor,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                surfaceColor
                            )
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                // Language toggle – top right
                FilledTonalButton(
                    onClick = {
                        lang = if (lang == "de") "en" else "de"
                        prefs.edit().putString(PREFS_KEY_LANG, lang).apply()
                    },
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        s.langToggle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Animation
            MapFlipAnimation(modifier = Modifier.padding(top = 0.dp))

            Spacer(Modifier.height(20.dp))

            // Title block
            Text(
                text = s.headline,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = s.subtitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = s.tagline,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(32.dp))

            // Setup card – premium styling
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = s.setupTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(20.dp))

                    SetupStep(
                        number = 1,
                        text = s.step1,
                        isLast = false
                    )
                    SetupStep(
                        number = 2,
                        text = s.step2,
                        isLast = false
                    )
                    SetupStep(
                        number = 3,
                        text = s.step3,
                        isLast = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Pause switch card – prominent, clean toggle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.pauseTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
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
                        onCheckedChange = { checked ->
                            isPaused = checked
                            prefs.edit().putBoolean(PREFS_KEY_PAUSED, checked).apply()
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Settings button – prominent but elegant
            Button(
                onClick = {
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
                    .padding(horizontal = 20.dp)
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
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // Feedback button for testers
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:daniel.notthoff@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "[MapFlip v1.0.3 Feedback]")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    s.btnFeedback,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // Status indicator – refined
            when {
                isPaused -> StatusBadge(text = s.statusPaused, active = false, isPaused = true)
                linksActive == true -> StatusBadge(text = s.statusActive, active = true, isPaused = false)
                linksActive == false -> StatusBadge(text = s.statusInactive, active = false, isPaused = false)
                else -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = s.statusHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 40.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            Spacer(Modifier.height(28.dp))

            // FamWake promo – subtle, premium
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = s.famwakePromo,
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = s.famwakeTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = s.famwakeDesc,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(URL_FAMWAKE)
                            ))
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                    ) {
                        Text(
                            s.famwakeButton,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Copyright footer
            Text(
                text = s.copyright,
                style = MaterialTheme.typography.bodySmall.copy(
                    letterSpacing = 0.3.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URL_NOTTHOFF)))
                }
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SetupStep(number: Int, text: String, isLast: Boolean) {
    Row(
        modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Number badge – subtle, refined
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(32.dp)
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
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
private fun StatusBadge(text: String, active: Boolean, isPaused: Boolean = false) {
    val targetBg = when {
        isPaused -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        active -> Green500.copy(alpha = 0.08f)
        else -> Red500.copy(alpha = 0.08f)
    }
    val bgColor by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(300),
        label = "statusBg"
    )
    val contentColor = when {
        isPaused -> MaterialTheme.colorScheme.tertiary
        active -> Green500
        else -> Red500
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (active) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Green500.copy(alpha = pulseAlpha))
                )
            } else {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

private fun checkLinksEnabled(context: Context): Boolean? {
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
