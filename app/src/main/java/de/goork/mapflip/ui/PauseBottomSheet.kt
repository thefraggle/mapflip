package de.goork.mapflip.ui

import android.content.Context
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TimerOff
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.unit.dp
import de.goork.mapflip.AppConstants
import de.goork.mapflip.PauseHelper

private data class PauseOption(
    val label: String,
    val durationMs: Long,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PauseBottomSheet(
    s: Strings.AppStrings,
    onDismiss: () -> Unit,
    onPauseConfigured: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val prefs = remember { context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE) }
    val focusRequester = remember { FocusRequester() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        val options = listOf(
            PauseOption(s.pause1Hour, 1 * 60 * 60 * 1000L, Icons.Outlined.HourglassBottom),
            PauseOption(s.pause8Hours, 8 * 60 * 60 * 1000L, Icons.Outlined.Schedule),
            PauseOption(s.pauseUntilTomorrow, -1L, Icons.Outlined.WbSunny),
            PauseOption(s.pauseIndefinitely, 0L, Icons.Outlined.TimerOff)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Escape) {
                        onDismiss()
                        true
                    } else false
                }
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 8.dp)
        ) {
            Text(
                text = s.dialogPauseTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = s.pauseDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            options.forEach { option ->
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val untilTimestamp = when (option.durationMs) {
                            0L -> 0L
                            -1L -> PauseHelper.getTomorrowMorningTimestamp()
                            else -> System.currentTimeMillis() + option.durationMs
                        }
                        prefs.edit()
                            .putBoolean(AppConstants.PREFS_KEY_PAUSED, true)
                            .putLong(PauseHelper.PREFS_KEY_PAUSED_UNTIL, untilTimestamp)
                            .apply()
                        onPauseConfigured()
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
