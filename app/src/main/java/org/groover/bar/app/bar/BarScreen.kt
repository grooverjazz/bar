package org.groover.bar.app.bar

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.app.util.BarNavigateButton
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout

@Composable
fun BarScreen(
    navigate: (route: String) -> Unit,
) {
    // Content
    BarContent(navigate)
}


@Composable
private fun BarContent(
    navigate: (route: String) -> Unit = {},
) {
    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(100.dp))
        BarTitle("Bar")
        Spacer(Modifier.size(80.dp))

        // Turven button
        BarNavigateButton("Turven",
            navigate = navigate,
            route = "bar/turven",
        )
        Spacer(Modifier.size(30.dp))

        // Geschiedenis button
        BarNavigateButton("Geschiedenis",
            navigate = navigate,
            route = "bar/geschiedenis",
        )
    }
}