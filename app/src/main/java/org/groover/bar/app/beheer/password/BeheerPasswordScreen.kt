package org.groover.bar.app.beheer.password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.LabeledTextField
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerPasswordScreen(
    navigate: (route: String) -> Unit,
    finish: (newPassword: String) -> Unit,
) {
    // (Finishes setting the password)
    val onFinish = { newPassword: String ->
        finish(newPassword)
        navigate("beheer")
    }

    // Content
    BeheerPasswordContent(
        onFinish = onFinish,
    )
}

@Composable
private fun BeheerPasswordContent(
    onFinish: (newPassword: String) -> Unit,
) {
    // Remember new password
    var newPassword: String by remember { mutableStateOf("") }

    // UI
    VerticalGrid {
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
        Spacer(Modifier.size(50.dp))

        // Change password button
        BigButton(
            "Wachtwoord veranderen",
            onClick = { onFinish(newPassword) },
            rounded = true,
        )
    }
}

