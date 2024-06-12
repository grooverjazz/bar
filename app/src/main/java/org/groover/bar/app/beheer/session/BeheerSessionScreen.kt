package org.groover.bar.app.beheer.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.groover.bar.util.app.ClickableCheckbox
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun BeheerSessionScreen(
    navigate: (String) -> Unit,
    oldSessionName: String,
    allSessions: List<String>,
    finish: (String, Boolean) -> Unit,
) {
    val exampleStrStart = SimpleDateFormat("yyyy-MM-dd").format(Date())
    val exampleStrEnd = SimpleDateFormat("d MMMM yyyy").format(Date())
    val exampleStr = "$exampleStrStart Pandavond $exampleStrEnd"

    BeheerSessionContent(
        navigate = navigate,
        oldSessionName = oldSessionName,
        allSessions = allSessions,
        exampleStr = exampleStr,
        finish = finish,
    )
}

@Composable
private fun BeheerSessionContent(
    navigate: (String) -> Unit,
    oldSessionName: String,
    allSessions: List<String>,
    exampleStr: String,
    finish: (String, Boolean) -> Unit,
) {
    // Remember session name
    var newSessionName: String by remember { mutableStateOf(oldSessionName) }
    var copyGlobalData: Boolean by remember { mutableStateOf(true) }

    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "beheer",
        )

        // Title
        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Sessies")
        Spacer(modifier = Modifier.size(20.dp))

        // New session name field
        TextField(
            value = newSessionName,
            onValueChange = { newSessionName = it },
            placeholder = { Text("Naam") }
        )

        // Example name button
        val colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        Button(
            colors = colors,
            shape = RectangleShape,
            onClick = {
                newSessionName = exampleStr
            }
        ) { Text("Wat dacht je van \"$exampleStr\"?") }
        Spacer(modifier = Modifier.size(40.dp))

        // List of all sessions
        Text("Alle sessies:", textAlign = TextAlign.Center)

        LazyColumn(
            modifier = Modifier.padding(10.dp).background(Color.LightGray).height(200.dp)
        ) {
            allSessions.forEach { session ->
                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        onClick = {
                            newSessionName = session
                            copyGlobalData = false
                        }
                    ) { Text(session) }
                }
            }
        }
        Spacer(modifier = Modifier.size(30.dp))

        // Copy global data checkbox
        ClickableCheckbox(
            label = "Kopieer leden, groepen en items van huidige sessie",
            state = copyGlobalData,
            onStateChange = { copyGlobalData = it }
        )
        Spacer(modifier = Modifier.size(10.dp))

        // Save button
        Button(onClick = {
            // Change the session
            finish(newSessionName.trim(), copyGlobalData)
        }) {
            Text(if (allSessions.contains(newSessionName.trim())) "Sessie openen" else "Sessie aanmaken")
        }
    }
}