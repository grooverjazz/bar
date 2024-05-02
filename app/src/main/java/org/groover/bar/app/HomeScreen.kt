package org.groover.bar.app

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun HomeScreen(
    navigate: (String) -> Unit,
) {
    HomeContent(
        navigate = navigate
    )
}


@Composable
private fun HomeContent(
   navigate: (String) -> Unit
) {
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        Spacer(Modifier.size(100.dp))

        // Title
        TitleText("Bartablet!")

        Spacer(Modifier.size(80.dp))

        // Bar button
        NavigateButton(
            navigate = navigate,
            text = "Bar",
            route = "bar",
        )

        Spacer(Modifier.size(30.dp))

        // Bar Beheer button
        NavigateButton(
            navigate = navigate,
            text = "Beheer",
            route = "beheer",
        )
    }
}