package org.groover.bar.app.beheer.password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerPasswordScreen(
    navigate: (String) -> Unit,
    finish: (String) -> Unit,
) {
    BeheerPasswordContent(
        navigate = navigate,
        finish = finish,
    )
}

@Composable
private fun BeheerPasswordContent(
    navigate: (String) -> Unit,
    finish: (String) -> Unit,
) {
    // Remember new password
    var newPassword: String by remember { mutableStateOf("") }

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
        TitleText("Wachtwoord aanpassen")

        Spacer(Modifier.size(80.dp))

        // Password field
        TextField(
            value = newPassword,
            onValueChange = { newPassword = it.trim() },
            placeholder = { Text("Nieuw wachtwoord") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Login button
        Button(onClick = {
            // Set the password
            finish(newPassword)
            navigate("beheer")
        }) {
            Text("Wachtwoord veranderen")
        }
    }
}

