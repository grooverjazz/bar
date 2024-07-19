package org.groover.bar.app.util

import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun BarNavigateButton(
    text: String,
    navigate: (route: String) -> Unit,
    route: String,
    color: Color = MaterialTheme.colorScheme.secondary,
) {
    val context = LocalContext.current
    val emptyRoute = route.isEmpty()

    BarButton(text = if (emptyRoute) "$text (TODO!)" else text,
        onClick = {
            // Empty route gives warning
            if (emptyRoute) {
                Toast.makeText(context, "TODO Route!", Toast.LENGTH_SHORT).show()
            }
            else {
                navigate(route)
            }
        },
        rounded = true,
        color = color,
    )
}