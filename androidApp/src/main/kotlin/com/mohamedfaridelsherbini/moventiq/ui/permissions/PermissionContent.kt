package com.mohamedfaridelsherbini.moventiq.ui.permissions

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.components.MoventiqPrimaryButton
import com.mohamedfaridelsherbini.moventiq.ui.permissions.components.PermissionBulletRow
import com.mohamedfaridelsherbini.moventiq.ui.permissions.components.PermissionCard
import com.mohamedfaridelsherbini.moventiq.ui.permissions.components.PermissionHeroIcon
import com.mohamedfaridelsherbini.moventiq.ui.permissions.components.PermissionTrustRow
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PermissionFlowHost(
    modifier: Modifier = Modifier,
    viewModel: PermissionFlowViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(PermissionEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (state.step) {
        PermissionFlowStep.Location -> LocationPermissionContent(
            onEvent = viewModel::onEvent,
            modifier = modifier,
        )
        PermissionFlowStep.Notification -> NotificationPermissionContent(
            onEvent = viewModel::onEvent,
            modifier = modifier,
        )
        PermissionFlowStep.Denied -> PermissionDeniedContent(
            onEvent = viewModel::onEvent,
            modifier = modifier,
        )
        PermissionFlowStep.None -> Unit
    }
}

@Composable
fun LocationPermissionContent(
    onEvent: (PermissionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    val backgroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onEvent(
            PermissionEvent.LocationResults(
                fineGranted = true,
                backgroundGranted = granted,
            ),
        )
    }

    val fineLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val fineGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!fineGranted) {
            onEvent(PermissionEvent.LocationResults(fineGranted = false, backgroundGranted = false))
            return@rememberLauncherForActivityResult
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            onEvent(PermissionEvent.LocationResults(fineGranted = true, backgroundGranted = true))
        }
    }

    PermissionScaffold(
        screenTag = PermissionTestTags.LOCATION_SCREEN,
        modifier = modifier,
        bottomContent = {
            MoventiqPrimaryButton(
                text = stringResource(R.string.permission_location_allow),
                onClick = {
                    onEvent(PermissionEvent.LocationAllow)
                    fineLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ),
                    )
                },
                testTag = PermissionTestTags.LOCATION_ALLOW,
            )
            TextButton(
                onClick = { onEvent(PermissionEvent.LocationLater) },
                modifier = Modifier.testTag(PermissionTestTags.LOCATION_LATER),
            ) {
                Text(
                    text = stringResource(R.string.permission_location_later),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textMuted,
                )
            }
        },
    ) {
        PermissionHeroIcon(iconRes = R.drawable.ic_onboarding_map_pin)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs),
        ) {
            Text(
                text = stringResource(R.string.permission_location_headline),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.permission_location_body),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        PermissionCard {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                PermissionTrustRow(stringResource(R.string.permission_location_trust_1))
                PermissionTrustRow(stringResource(R.string.permission_location_trust_2))
                PermissionTrustRow(stringResource(R.string.permission_location_trust_3))
            }
        }
    }
}

@Composable
fun NotificationPermissionContent(
    onEvent: (PermissionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onEvent(PermissionEvent.NotificationResult(granted))
    }

    PermissionScaffold(
        screenTag = PermissionTestTags.NOTIFICATION_SCREEN,
        modifier = modifier,
        bottomContent = {
            MoventiqPrimaryButton(
                text = stringResource(R.string.permission_notification_allow),
                onClick = {
                    onEvent(PermissionEvent.NotificationAllow)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onEvent(PermissionEvent.NotificationResult(granted = true))
                    }
                },
                testTag = PermissionTestTags.NOTIFICATION_ALLOW,
            )
            TextButton(
                onClick = { onEvent(PermissionEvent.NotificationSkip) },
                modifier = Modifier.testTag(PermissionTestTags.NOTIFICATION_SKIP),
            ) {
                Text(
                    text = stringResource(R.string.permission_notification_skip),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textMuted,
                )
            }
        },
    ) {
        PermissionHeroIcon(iconRes = R.drawable.ic_permission_bell)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs),
        ) {
            Text(
                text = stringResource(R.string.permission_notification_headline),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.permission_notification_body),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs)) {
            PermissionBulletRow(stringResource(R.string.permission_notification_bullet_1))
            PermissionBulletRow(stringResource(R.string.permission_notification_bullet_2))
            PermissionBulletRow(stringResource(R.string.permission_notification_bullet_3))
        }
    }
}
