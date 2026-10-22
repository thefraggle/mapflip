package de.goork.mapflip.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.goork.mapflip.ui.Strings
import de.goork.mapflip.ui.theme.Green500
import de.goork.mapflip.ui.theme.Orange500
import de.goork.mapflip.ui.theme.Red500
import de.goork.mapflip.util.DomainStatusInfo

@Composable
fun StatusAndControlCard(
    s: Strings.AppStrings,
    isPaused: Boolean,
    linksActive: Boolean?,
    domainStatus: DomainStatusInfo?,
    onStatusClick: () -> Unit,
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
            // Status Header Row (tap to open setup guide)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onStatusClick() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusDotColor = when {
                    isPaused -> MaterialTheme.colorScheme.onSurfaceVariant
                    domainStatus != null -> when {
                        domainStatus.isFullyEnabled -> Green500
                        domainStatus.isPartiallyEnabled -> Orange500
                        else -> Red500
                    }
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
                        domainStatus != null -> when {
                            domainStatus.isFullyEnabled -> s.formatStatusAllActive(domainStatus.totalHosts)
                            domainStatus.isPartiallyEnabled -> s.formatStatusPartial(domainStatus.enabledHosts, domainStatus.totalHosts)
                            else -> s.setupStatusNone
                        }
                        linksActive == true -> s.statusActive
                        else -> s.statusInactive
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = s.menuSetupGuide,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
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
