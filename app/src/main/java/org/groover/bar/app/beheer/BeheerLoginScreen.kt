package org.groover.bar.app.beheer

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import org.groover.bar.app.util.BarButton
import org.groover.bar.app.util.BarTextField
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout

@Composable
fun BeheerLoginScreen(
    navigate: (route: String) -> Unit,
    correctPassword: String
) {
    val context = LocalContext.current

    // (Handles login)
    val logIn: (String) -> Unit = { currentPassword ->
        if (currentPassword == correctPassword) {
            navigate("beheer")
        }
        else {
            Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Content
    BeheerLoginContent(
        logIn = logIn,
    )
}


@Composable
private fun BeheerLoginContent(
    logIn: (String) -> Unit,
) {
    // Keyboard focus for search box
    val keyboardFocus = remember { FocusRequester() }
    LaunchedEffect(keyboardFocus) {
        awaitFrame()
        keyboardFocus.freeFocus()
        keyboardFocus.requestFocus()
    }

    // Remember current password
    var currentPassword: String by remember { mutableStateOf("") }

    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(100.dp))
        BarTitle("Beheer")
        Spacer(Modifier.size(80.dp))

        // Password field
        BarTextField(Modifier.focusRequester(keyboardFocus),
            text = "Wachtwoord",
            value = currentPassword,
            onValueChange = { currentPassword = it.trim() },
            isPassword = true,
        )
        Spacer(Modifier.size(40.dp))

        // Login button
        BarButton("Inloggen",
            onClick = { logIn(currentPassword) },
            rounded = true,
        )
    }
}