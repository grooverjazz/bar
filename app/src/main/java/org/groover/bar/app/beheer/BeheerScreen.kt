package org.groover.bar.app.beheer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "home",
        )

        Spacer(Modifier.size(100.dp))

        // Title
        TitleText("Beheer")

        Spacer(Modifier.size(80.dp))

        // Leden en Groepen button
        NavigateButton(
            navigate = navigate,
            text = "Leden en Groepen",
            route = "beheer/customers",
        )

        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton(
            navigate = navigate,
            text = "Items",
            route = "beheer/items",
        )

        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton(
            navigate = navigate,
            text = "Sessie",
            route = "beheer/session",
        )

        Spacer(Modifier.size(30.dp))

        // Password button
        NavigateButton(
            navigate = navigate,
            text = "Wachtwoord veranderen",
            route = "beheer/password",
        )

        Spacer(Modifier.size(50.dp))

        Button(
            onClick = export
        ) {
            Text("Hele zooi afrekenen")
        }
    }
}
