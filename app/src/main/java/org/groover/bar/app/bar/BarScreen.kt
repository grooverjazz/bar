package org.groover.bar.app.bar

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BarScreen(
    navigate: (String) -> Unit,
) {
    BarContent(navigate)
}


@Composable
private fun BarContent(
    navigate: (String) -> Unit = {},
) {
    VerticalGrid {
        // Title
        Spacer(Modifier.size(100.dp))
        TitleText("Bar")
        Spacer(Modifier.size(80.dp))

        // Turven button
        NavigateButton("Turven",
            navigate = navigate,
            route = "bar/turven"
        )
        Spacer(Modifier.size(30.dp))

        // Geschiedenis button
        NavigateButton("Geschiedenis",
            navigate = navigate,
            route = "bar/geschiedenis"
        )
    }
}