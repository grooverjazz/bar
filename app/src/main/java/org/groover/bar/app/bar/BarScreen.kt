package org.groover.bar.app.bar

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Title
        Spacer(modifier = Modifier.size(100.dp))
        TitleText("Bar")

        Spacer(Modifier.size(80.dp))

        // Turven button
        NavigateButton(
            navigate = navigate,
            text = "Turven",
            route = "bar/turven"
        )

        Spacer(Modifier.size(30.dp))

        // Overzicht bestellingen button
        NavigateButton(
            navigate = navigate,
            text = "Geschiedenis",
            route = "bar/geschiedenis"
        )
    }
}