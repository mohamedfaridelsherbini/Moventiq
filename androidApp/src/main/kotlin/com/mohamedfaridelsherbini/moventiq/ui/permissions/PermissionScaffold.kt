package com.mohamedfaridelsherbini.moventiq.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqColors
import com.mohamedfaridelsherbini.moventiq.ui.theme.moventiqSpacing

@Composable
internal fun PermissionScaffold(
    screenTag: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    bottomContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = moventiqColors()
    val spacing = moventiqSpacing()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(screenTag),
        containerColor = colors.surface,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    start = spacing.lg,
                    end = spacing.lg,
                    top = spacing.lg,
                    bottom = spacing.lg + spacing.xs,
                ),
            verticalArrangement = if (centered) Arrangement.Center else Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = !centered)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                content()
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.sm + spacing.xs),
            ) {
                bottomContent()
            }
        }
    }
}
