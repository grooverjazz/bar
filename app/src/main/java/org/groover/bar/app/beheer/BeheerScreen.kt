package org.groover.bar.app.beheer

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.groover.bar.export.BTWHandler
import org.groover.bar.export.ExportHandler
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

private enum class BeheerState {
    INLOG, SCREEN
}


@Composable
fun BeheerScreen(
    navigate: (String) -> Unit,
    correctPassword: String,
    exportHandler: ExportHandler,
    btwHandler: BTWHandler
) {
    val context = LocalContext.current

    var state: BeheerState by remember { mutableStateOf(BeheerState.INLOG) }

    val logIn: (String) -> Unit = { currentPassword ->
        if (currentPassword == correctPassword) {
            state = BeheerState.SCREEN
        }
        else {
            Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Give screen
    when (state) {
        BeheerState.INLOG -> BeheerInlog(
            navigate = navigate,
            logIn = logIn
        )
        BeheerState.SCREEN -> BeheerContent(
            navigate = navigate,
            export = exportHandler::export,
            exportBtw = btwHandler::export,
        )
    }
}


@Composable
private fun BeheerInlog(
    navigate: (String) -> Unit,
    logIn: (String) -> Unit,
) {
    // Remember current password
    var currentPassword: String by remember { mutableStateOf("") }

    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "home",
            height = 60.dp,
        )

        Spacer(Modifier.size(100.dp))

        // Title
        TitleText("Beheer: Inlog")

        Spacer(Modifier.size(80.dp))

        // Password field
        TextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            placeholder = { Text("Wachtwoord") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Login button
        Button(onClick = {
            // Change the session
            logIn(currentPassword)
        }) {
            Text("Inloggen")
        }
    }
}


@Composable
private fun BeheerContent(
    navigate: (String) -> Unit,
    export: () -> Unit,
    exportBtw: () -> Unit,
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
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
            navigate = navigate,
            text = "Leden en Groepen",
            route = "beheer/customers",
        )

        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton(
            navigate = navigate,
            text = "Items",
            route = "beheer/items",
        )

        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton(
            navigate = navigate,
            text = "Sessie",
            route = "beheer/session",
        )

        Spacer(Modifier.size(30.dp))

        // Password button
        NavigateButton(
            navigate = navigate,
            text = "Wachtwoord veranderen",
            route = "beheer/password",
        )

        Spacer(Modifier.size(50.dp))

        Button(
            onClick = export
        ) {
            Text("Export incasso")
        }

        Button(
            onClick = exportBtw
        ) {
            Text("Export BTW")
        }
    }
}
