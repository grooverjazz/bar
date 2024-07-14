package org.groover.bar.app.beheer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerScreen(
    navigate: (route: String) -> Unit,
    export: () -> Unit,
    hasErrors: Boolean,
) {
    // Content
    BeheerContent(
        navigate = navigate,
        export = export,
        hasErrors = hasErrors,
    )
}

@Composable
private fun BeheerContent(
    navigate: (route: String) -> Unit,
    export: () -> Unit,
    hasErrors: Boolean,
) {
    // UI
    VerticalGrid {
        // Title
        Spacer(Modifier.size(80.dp))
        TitleText("Beheer")
        Spacer(Modifier.size(80.dp))

        // Error button
        if (hasErrors) {
            NavigateButton(
                "⚠\uFE0F Errors ⚠\uFE0F",
                navigate = navigate,
                route = "beheer/error",
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.size(40.dp))
        }

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

        // Session button
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
