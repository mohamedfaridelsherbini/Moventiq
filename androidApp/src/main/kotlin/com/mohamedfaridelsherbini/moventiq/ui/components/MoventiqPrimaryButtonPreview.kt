package com.mohamedfaridelsherbini.moventiq.ui.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedfaridelsherbini.moventiq.ui.theme.MoventiqTheme

@Preview(name = "MoventiqPrimaryButton — Light", showBackground = true)
@Composable
private fun MoventiqPrimaryButtonLightPreview() {
    MoventiqTheme(darkTheme = false) {
        MoventiqPrimaryButton(
            text = "Continue",
            onClick = {},
        )
    }
}

@Preview(
    name = "MoventiqPrimaryButton — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun MoventiqPrimaryButtonDarkPreview() {
    MoventiqTheme(darkTheme = true) {
        MoventiqPrimaryButton(
            text = "Get started",
            onClick = {},
        )
    }
}
