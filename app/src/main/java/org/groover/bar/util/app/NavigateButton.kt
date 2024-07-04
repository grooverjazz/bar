package org.groover.bar.util.app

import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun NavigateButton(
    text: String,
    navigate: (String) -> Unit,
    route: String,
) {
    val context = LocalContext.current
    val emptyRoute = route.isEmpty()

    BigButton(text = if (emptyRoute) "$text (TODO!)" else text,
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
        color = MaterialTheme.colorScheme.secondary,
    )
}