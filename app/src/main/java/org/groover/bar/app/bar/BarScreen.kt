package org.groover.bar.app.bar

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.app.util.BarNavigateButton
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout

/**
 * The main 'Bar' screen.
 */
@Composable
fun BarScreen(
    navigate: (route: String) -> Unit,
    sessionName: String
) {
    // Content
    BarContent(navigate, sessionName)
}

@Composable
private fun BarContent(
    navigate: (route: String) -> Unit = {},
    sessionName: String = "Session not found"
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
        Spacer(Modifier.size(50.dp))

        // Session text
        Text("Huidige sessie: $sessionName",
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}