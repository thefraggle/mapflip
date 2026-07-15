package de.goork.mapflip.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import de.goork.mapflip.ui.theme.Green500
import de.goork.mapflip.ui.theme.Red500
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("mapflip", Context.MODE_PRIVATE) }
    val systemLang = Locale.getDefault().language
    var lang by remember {
        mutableStateOf(prefs.getString("lang", if (systemLang == "de") "de" else "en") ?: "en")
    }
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = {
                    FilterChip(
                        selected = false,
                        onClick = {
                            lang = if (lang == "de") "en" else "de"
                            prefs.edit().putString("lang", lang).apply()
                        },
                        label = { Text(s.langToggle) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            MapFlipAnimation()

            Spacer(Modifier.height(16.dp))

            Text(
                text = s.headline,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = s.subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = s.tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(28.dp))

            // Setup card
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text(text = s.setupTitle, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    SetupStep(1, s.step1)
                    Spacer(Modifier.height(12.dp))
                    SetupStep(2, s.step2)
                    Spacer(Modifier.height(12.dp))
                    SetupStep(3, s.step3)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Settings button
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
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(s.btnSettings, style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(12.dp))

            // Status indicator
            when (linksActive) {
                true -> StatusChip(text = s.statusActive, active = true)
                false -> StatusChip(text = s.statusInactive, active = false)
                null -> Text(
                    text = s.statusHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(32.dp))

            // FamWake promo
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = s.famwakePromo,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(text = "FamWake \u2013 Familienwecker", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = s.famwakeDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = {
                        context.startActivity(Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=de.familienwecker.famwake")
                        ))
                    }) {
                        Text(s.famwakeButton)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Copyright footer
            Text(
                text = s.copyright,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://notthoff.org")))
                }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SetupStep(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun StatusChip(text: String, active: Boolean) {
    Surface(
        color = if (active) Green500.copy(alpha = 0.12f) else Red500.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = if (active) "\u2705" else "\u274c", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = if (active) Green500 else Red500)
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
