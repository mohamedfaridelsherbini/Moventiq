package com.mohamedfaridelsherbini.moventiq.ui.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingTestTags
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing

@Composable
fun OnboardingProgressDots(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Row(
        modifier = modifier.testTag(OnboardingTestTags.PROGRESS),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        repeat(pageCount) { index ->
            val active = index == currentPage
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (active) 24.dp else 8.dp)
                    .clip(if (active) RoundedCornerShape(999.dp) else CircleShape)
                    .background(if (active) colors.primary else colors.progressInactive),
            )
        }
    }
}
