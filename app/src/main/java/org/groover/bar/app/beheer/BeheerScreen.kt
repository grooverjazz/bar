package org.groover.bar.app.beheer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerScreen(
    navigate: (String) -> Unit,
    export: () -> Unit,
) {
    BeheerContent(
        navigate = navigate,
        export = export,
    )
}

@Composable
private fun BeheerContent(
    navigate: (String) -> Unit,
    export: () -> Unit,
) {
    VerticalGrid {
        // Title
        Spacer(Modifier.size(80.dp))
        TitleText("Beheer")
        Spacer(Modifier.size(80.dp))

        // Leden en Groepen button
        NavigateButton("Leden en Groepen",
            navigate = navigate,
            route = "beheer/customers",
        )
        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton("Items",
            navigate = navigate,
            route = "beheer/items",
        )
        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton("Sessie",
            navigate = navigate,
            route = "beheer/session",
        )
        Spacer(Modifier.size(30.dp))

        // Password button
        NavigateButton("Wachtwoord veranderen",
            navigate = navigate,
            route = "beheer/password",
        )
        Spacer(Modifier.size(50.dp))

        BigButton("Hele zooi afrekenen",
            onClick = export,
            rounded = true,
        )
    }
}
