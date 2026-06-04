package com.mohamedfaridelsherbini.moventiq.ui.permissions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.mohamedfaridelsherbini.moventiq.R
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqRounded
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqStroke

@Composable
fun PermissionTrustRow(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()
    val rounded = moventiqRounded()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(spacing.iconContainerTrust)
                .clip(RoundedCornerShape(rounded.sm))
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_onboarding_checkmark),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(spacing.iconInline),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun PermissionBulletRow(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()
    val rounded = moventiqRounded()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(spacing.xl)
                .clip(RoundedCornerShape(rounded.md))
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_onboarding_sparkles),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(spacing.iconInline),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun PermissionStepRow(
    stepNumber: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()
    val rounded = moventiqRounded()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.smPlus),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(spacing.iconStepBadge)
                .clip(RoundedCornerShape(rounded.full))
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stepNumber.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.primary,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun PermissionCard(
    modifier: Modifier = Modifier,
    largeCorners: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()
    val stroke = moventiqStroke()
    val cornerRadius = if (largeCorners) spacing.xl else spacing.lg

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .border(stroke.hairline, colors.border, RoundedCornerShape(cornerRadius))
            .background(colors.surfaceElevated)
            .padding(spacing.cardPaddingInset),
    ) {
        content()
    }
}

@Composable
fun PermissionDeniedStepsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    PermissionCard(modifier = modifier, largeCorners = true, content = content)
}
