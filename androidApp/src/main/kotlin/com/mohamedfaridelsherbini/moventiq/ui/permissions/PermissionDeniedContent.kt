package com.mohamedfaridelsherbini.moventiq.ui.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.components.MoventiqPrimaryButton
import com.mohamedfaridelsherbini.moventiq.ui.permissions.components.PermissionDeniedStepsCard
import com.mohamedfaridelsherbini.moventiq.ui.permissions.components.PermissionHeroIcon
import com.mohamedfaridelsherbini.moventiq.ui.permissions.components.PermissionStepRow
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing

@Composable
fun PermissionDeniedContent(
    onEvent: (PermissionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()
    val context = LocalContext.current

    PermissionScaffold(
        screenTag = PermissionTestTags.DENIED_SCREEN,
        modifier = modifier,
        centered = true,
        bottomContent = {
            MoventiqPrimaryButton(
                text = stringResource(R.string.permission_denied_open_settings),
                onClick = {
                    onEvent(PermissionEvent.DeniedOpenSettings)
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        },
                    )
                },
                testTag = PermissionTestTags.DENIED_OPEN_SETTINGS,
                iconRes = R.drawable.ic_permission_external_link,
            )
            TextButton(
                onClick = { onEvent(PermissionEvent.DeniedLimitedFeatures) },
                modifier = Modifier.testTag(PermissionTestTags.DENIED_LIMITED),
            ) {
                Text(
                    text = stringResource(R.string.permission_denied_limited),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textMuted,
                )
            }
        },
    ) {
        PermissionHeroIcon(
            iconRes = R.drawable.ic_permission_shield_off,
            iconTint = colors.error,
            containerColor = colors.errorContainer,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs),
        ) {
            Text(
                text = stringResource(R.string.permission_denied_headline),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.permission_denied_body),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        PermissionDeniedStepsCard {
            PermissionStepRow(1, stringResource(R.string.permission_denied_step_1))
            PermissionStepRow(2, stringResource(R.string.permission_denied_step_2))
            PermissionStepRow(3, stringResource(R.string.permission_denied_step_3))
        }
    }
}
