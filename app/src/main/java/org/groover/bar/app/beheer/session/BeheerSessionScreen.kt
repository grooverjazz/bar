package org.groover.bar.app.beheer.session

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
    val exampleStrEnd = SimpleDateFormat("d MMM yyyy").format(Date())
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
            height = 60.dp,
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
        Spacer(modifier = Modifier.size(20.dp))

        // Example name button
        Button(
            shape = RectangleShape,
            onClick = {
                newSessionName = exampleStr
            }
        ) { Text(exampleStr) }
        Spacer(modifier = Modifier.size(20.dp))

        // List of all sessions
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.padding(10.dp)
        ) {
            allSessions.forEach { session ->
                item {
                    Button(
                        shape = RectangleShape,
                        onClick = {
                            newSessionName = session
                            copyGlobalData = false
                        }
                    ) { Text(session) }
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))

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