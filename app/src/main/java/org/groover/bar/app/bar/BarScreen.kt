package org.groover.bar.app.bar

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
fun BarScreen(navController: NavController) {
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "home",
            height = 60.dp,
        )

        Spacer(modifier = Modifier.size(80.dp))

        // Title
        TitleText("Bar")

        Spacer(Modifier.size(80.dp))

        // Turven button
        NavigateButton(
            navController = navController,
            text = "Turven",
            route = "bar/turven"
        )

        Spacer(Modifier.size(30.dp))

        // Overzicht bestellingen button
        NavigateButton(
            navController = navController,
            text = "Geschiedenis",
            route = "bar/geschiedenis"
        )
    }
}