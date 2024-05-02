package org.groover.bar.util.app

import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NavigateButton(
    navigate: (String) -> Unit,
    text: String,
    fontSize: TextUnit = 30.sp,
    height: Dp = 100.dp,
    route: String,
    color: Color = MaterialTheme.colorScheme.secondary,
) {
    val context = LocalContext.current
    val emptyRoute = route.isEmpty()

    Button(
        modifier = Modifier.height(height),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        onClick = {
            // Empty route gives warning
            if (emptyRoute) {
                Toast.makeText(context, "TODO Route!", Toast.LENGTH_SHORT).show()
            }
            else {
                navigate(route)
            }
        },
    ) {
        Text(if (emptyRoute) "$text (TODO!)" else text,
            fontSize = fontSize,
        )
    }
}