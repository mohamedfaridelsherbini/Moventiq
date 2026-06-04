package com.mohamedfaridelsherbini.moventiq.ui.permissions.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing

@Composable
fun PermissionHeroIcon(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    iconTint: Color? = null,
    containerColor: Color? = null,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Box(
        modifier = modifier
            .size(spacing.xxl * 2)
            .clip(CircleShape)
            .background(containerColor ?: colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconTint ?: colors.primary,
            modifier = Modifier.size(spacing.iconHero),
        )
    }
}
