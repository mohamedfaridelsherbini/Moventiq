package com.mohamedfaridelsherbini.moventiq.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing

@Composable
fun MoventiqPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    @DrawableRes iconRes: Int? = null,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = spacing.xxl)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(spacing.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.textOnPrimary,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(spacing.iconButton),
                    tint = colors.textOnPrimary,
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
