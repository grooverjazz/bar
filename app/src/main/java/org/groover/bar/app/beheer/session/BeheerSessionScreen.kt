package org.groover.bar.app.beheer.session

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFilter
import org.groover.bar.app.util.BarButton
import org.groover.bar.app.util.BarCheckbox
import org.groover.bar.app.util.BarListLazy
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout
import java.text.SimpleDateFormat
import java.util.Date

/**
 * The screen where the current session can be changed.
 */
@SuppressLint("SimpleDateFormat")
@Composable
fun BeheerSessionScreen(
    navigate: (route: String) -> Unit,
    oldSessionName: String,
    allSessions: List<String>,
    finish: (newSessionName: String, copyGlobalData: Boolean) -> Unit,
) {
    // Define date strings
    val exampleStrStart = SimpleDateFormat("yyyy-MM-dd").format(Date())
    val exampleStrEnd = SimpleDateFormat("d MMMM yyyy").format(Date())
    val exampleStr = "$exampleStrStart Pandavond $exampleStrEnd"

    // (Finishes setting the session)
    val onFinish = { newSessionName: String, copyGlobalData: Boolean ->
        finish(newSessionName, copyGlobalData)

        navigate("home")
    }

    // Content
    BeheerSessionContent(
        oldSessionName = oldSessionName,
        allSessions = allSessions,
        exampleStr = exampleStr,
        finish = onFinish,
    )
}

@Composable
private fun BeheerSessionContent(
    oldSessionName: String,
    allSessions: List<String>,
    exampleStr: String,
    finish: (String, Boolean) -> Unit,
) {
    // Remember session name
    var newSessionName: String by remember { mutableStateOf(oldSessionName) }
    var copyGlobalData: Boolean by remember { mutableStateOf(true) }

    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(20.dp))
        BarTitle("Sessies")
        Spacer(Modifier.size(20.dp))

        // New session name field
        TextField(modifier = Modifier.fillMaxWidth(),
            value = newSessionName,
            onValueChange = { newSessionName = it.trim() },
            placeholder = { Text("Naam") },
        )

        // Example name button
        BarButton("Wat dacht je van \"$exampleStr\"?",
            onClick = { newSessionName = exampleStr },
            color = MaterialTheme.colorScheme.tertiary,
            rounded = true,
            fontSize = 24.sp,
        )
        Spacer(Modifier.size(40.dp))

        // List of all sessions
        Text("Alle sessies:",
            textAlign = TextAlign.Center,
        )

        BarListLazy(height = 600.dp) {
            items(allSessions) { session ->
                val isCurrentSession = session == oldSessionName
                BarButton(if (isCurrentSession) "$session (huidige sessie)" else session,
                    onClick = {
                        newSessionName = session
                        copyGlobalData = false
                    },
                    enabled = !isCurrentSession,
                )
            }
        }
        Spacer(Modifier.size(30.dp))

        // Copy global data checkbox
        BarCheckbox("Kopieer leden, groepen en items van huidige sessie",
            state = copyGlobalData,
            onStateChange = { copyGlobalData = it },
        )
        Spacer(Modifier.size(10.dp))

        // Save button
        val buttonText = when {
            newSessionName == oldSessionName -> "(Open een sessie of maak er een aan)"
            allSessions.contains(newSessionName) -> "Sessie '$newSessionName' openen"
            else -> "Sessie '$newSessionName' aanmaken"
        }
        BarButton(buttonText,
            onClick = { finish(newSessionName, copyGlobalData) },
            rounded = true,
            enabled = (newSessionName != oldSessionName),
            fontSize = 24.sp,
        )
    }
}