package org.groover.bar.app.beheer

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.LabeledTextField
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import kotlin.math.log

@Composable
fun BeheerLoginScreen(
    navigate: (String) -> Unit,
    correctPassword: String
) {
    val context = LocalContext.current

    val logIn: (String) -> Unit = { currentPassword ->
        if (currentPassword == correctPassword) {
            navigate("beheer")
        }
        else {
            Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    BeheerLoginContent(
        navigate = navigate,
        logIn = logIn,
    )
}


@Composable
private fun BeheerLoginContent(
    navigate: (String) -> Unit,
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

    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Title
        Spacer(Modifier.size(100.dp))
        TitleText("Beheer")

        Spacer(Modifier.size(80.dp))

        // Password field
        LabeledTextField(
            modifier = Modifier.focusRequester(keyboardFocus),
            text = "Wachtwoord",
            value = currentPassword,
            onValueChange = { currentPassword = it.trim() },
            isPassword = true,
        )
        Spacer(modifier = Modifier.size(40.dp))

        // Login button
        BigButton(text = "Inloggen",
            onClick = { logIn(currentPassword) },
            rounded = true,
        )
    }
}