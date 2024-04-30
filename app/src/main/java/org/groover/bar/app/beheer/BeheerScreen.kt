package org.groover.bar.app.beheer

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
fun BeheerScreen(
    navController: NavController
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "home",
            height = 60.dp,
        )

        Spacer(Modifier.size(100.dp))

        // Title
        TitleText("Beheer")

        Spacer(Modifier.size(80.dp))

        // Leden en Groepen button
        NavigateButton(
            navController = navController,
            text = "Leden en Groepen",
            route = "beheer/customers",
        )

        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton(
            navController = navController,
            text = "Items",
            route = "beheer/items",
        )
    }
}
