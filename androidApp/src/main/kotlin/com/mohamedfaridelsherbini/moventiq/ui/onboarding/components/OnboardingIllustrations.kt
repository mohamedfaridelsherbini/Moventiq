package com.mohamedfaridelsherbini.moventiq.ui.onboarding.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mohamedfaridelsherbini.moventiq.ui.onboarding.OnboardingPage
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing

private val IllustrationHeight = 250.dp
private val IllustrationMaxWidth = 310.dp

@Composable
fun OnboardingIllustration(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IllustrationHeight)
            .width(IllustrationMaxWidth)
            .clip(RoundedCornerShape(spacing.xl))
            .background(colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        when (page) {
            OnboardingPage.LinkTasks -> LinkTasksIllustration(colors)
            OnboardingPage.DetectArrival -> DetectArrivalIllustration(colors)
            OnboardingPage.AutoSurface -> AutoSurfaceIllustration(colors)
        }
    }
}

@Composable
private fun LinkTasksIllustration(colors: com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqColors) {
    val spacing = moventiqSpacing()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.sm + 6.dp),
    ) {
        TaskCardSkeleton(
            checked = true,
            modifier = Modifier.shadow(6.dp, RoundedCornerShape(spacing.md)),
        )
        ConnectorDots(color = colors.primary)
        LocationPin(size = 56.dp, colors = colors)
    }
}

@Composable
private fun DetectArrivalIllustration(colors: com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqColors) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        GeofenceRing(size = 200.dp, alpha = 0.14f, color = colors.primary)
        GeofenceRing(size = 150.dp, alpha = 0.25f, color = colors.primary)
        GeofenceRing(size = 100.dp, alpha = 0.46f, color = colors.primary)
        LocationPin(size = 64.dp, colors = colors)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 52.dp, y = (-28).dp)
                .size(14.dp)
                .clip(CircleShape)
                .background(colors.accent)
                .border(3.dp, colors.textOnPrimary, CircleShape),
        )
    }
}

@Composable
private fun AutoSurfaceIllustration(colors: com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqColors) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        TaskListCard(
            modifier = Modifier
                .width(220.dp)
                .offset(y = 8.dp)
                .shadow(8.dp, RoundedCornerShape(18.dp)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-26).dp, y = 46.dp)
                .size(48.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(colors.primary)
                .border(3.dp, colors.textOnPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            SparkleIcon(tint = colors.textOnPrimary)
        }
    }
}

@Composable
private fun TaskCardSkeleton(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.md))
            .background(colors.surfaceElevated)
            .padding(spacing.sm + 6.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm + 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (checked) colors.primary else colors.skeleton),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                CheckMark(tint = colors.textOnPrimary, size = 15.dp)
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            SkeletonLine(width = 132.dp, color = colors.skeleton)
            SkeletonLine(width = 84.dp, color = colors.skeletonMuted)
        }
    }
}

@Composable
private fun TaskListCard(modifier: Modifier = Modifier) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surfaceElevated)
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm + 4.dp),
    ) {
        TaskRowSkeleton(checked = true, lineWidth = 120.dp)
        TaskRowSkeleton(checked = false, lineWidth = 96.dp)
        TaskRowSkeleton(checked = false, lineWidth = 76.dp)
    }
}

@Composable
private fun TaskRowSkeleton(
    checked: Boolean,
    lineWidth: androidx.compose.ui.unit.Dp,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (checked) colors.primary else colors.skeleton),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                CheckMark(tint = colors.textOnPrimary, size = 13.dp)
            }
        }
        SkeletonLine(width = lineWidth, color = if (checked) colors.skeleton else colors.skeletonMuted)
    }
}

@Composable
private fun SkeletonLine(
    width: androidx.compose.ui.unit.Dp,
    color: Color,
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(9.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun ConnectorDots(color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 1f - index * 0.22f)),
            )
        }
    }
}

@Composable
private fun LocationPin(
    size: androidx.compose.ui.unit.Dp,
    colors: com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqColors,
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        MapPinIcon(
            tint = colors.textOnPrimary,
            size = size * 0.46f,
        )
    }
}

@Composable
private fun GeofenceRing(
    size: androidx.compose.ui.unit.Dp,
    alpha: Float,
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, color.copy(alpha = alpha), CircleShape),
    )
}

@Composable
private fun CheckMark(
    tint: Color,
    size: androidx.compose.ui.unit.Dp,
) {
    Canvas(modifier = Modifier.size(size)) {
        val stroke = size.toPx() * 0.12f
        drawPath(
            path = Path().apply {
                moveTo(size.toPx() * 0.18f, size.toPx() * 0.52f)
                lineTo(size.toPx() * 0.42f, size.toPx() * 0.76f)
                lineTo(size.toPx() * 0.82f, size.toPx() * 0.28f)
            },
            color = tint,
            style = Stroke(width = stroke),
        )
    }
}

@Composable
private fun MapPinIcon(
    tint: Color,
    size: androidx.compose.ui.unit.Dp,
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w / 2f, h * 0.92f)
            cubicTo(w * 0.12f, h * 0.58f, w * 0.12f, h * 0.28f, w / 2f, h * 0.18f)
            cubicTo(w * 0.88f, h * 0.28f, w * 0.88f, h * 0.58f, w / 2f, h * 0.92f)
            close()
        }
        drawPath(path, tint)
        drawCircle(
            color = tint,
            radius = w * 0.14f,
            center = Offset(w / 2f, h * 0.38f),
        )
    }
}

@Composable
private fun SparkleIcon(tint: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val arm = size.minDimension * 0.34f
        drawLine(tint, center - Offset(arm, 0f), center + Offset(arm, 0f), strokeWidth = 2.5f)
        drawLine(tint, center - Offset(0f, arm), center + Offset(0f, arm), strokeWidth = 2.5f)
        drawLine(
            tint,
            center - Offset(arm * 0.7f, arm * 0.7f),
            center + Offset(arm * 0.7f, arm * 0.7f),
            strokeWidth = 2.5f,
        )
        drawLine(
            tint,
            center - Offset(arm * 0.7f, -arm * 0.7f),
            center + Offset(arm * 0.7f, -arm * 0.7f),
            strokeWidth = 2.5f,
        )
    }
}
