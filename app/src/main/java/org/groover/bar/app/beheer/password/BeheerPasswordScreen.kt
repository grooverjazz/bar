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
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.LabeledTextField
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
        // Title
        Spacer(Modifier.size(100.dp))
        TitleText("Wachtwoord")

        Spacer(Modifier.size(80.dp))

        // Password field
        LabeledTextField(
            text = "Nieuw wachtwoord",
            value = newPassword,
            onValueChange = { newPassword = it.trim() },
        )
        Spacer(modifier = Modifier.size(50.dp))

        // Login button
        BigButton(text = "Wachtwoord veranderen",
            onClick = {
                // Set the password
                finish(newPassword)
                navigate("beheer")
            },
            rounded = true,
        )
    }
}

