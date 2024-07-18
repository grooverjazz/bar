package org.groover.bar.app.beheer

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.groover.bar.export.ExportHandler
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.ProgressDialog
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerScreen(
    navigate: (route: String) -> Unit,
    exportHandler: ExportHandler,
    exportName: String,
    hasErrors: Boolean,
) {
    val context = LocalContext.current

    // Export progress bar state
    var isExporting by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    // (Creates an export, while showing the progress bar)
    val exportWithProgress: () -> Unit = {
        // Launch into separate thread
        coroutineScope.launch {
            isExporting = true

            withContext(Dispatchers.IO) { exportHandler.export(exportName, { progress = it }) }

            // Show toast
            Toast.makeText(context, "Export klaar!", Toast.LENGTH_SHORT)
                .show()

            isExporting = false
        }
    }

    // Show progress dialog when exporting
    if (isExporting)
        ProgressDialog("Bezig met export...", progress)

    // Content
    BeheerContent(
        navigate = navigate,
        export = exportWithProgress,
        hasErrors = hasErrors,
    )
}

@Composable
private fun BeheerContent(
    navigate: (route: String) -> Unit,
    export: () -> Unit,
    hasErrors: Boolean,
) {
    // UI
    VerticalGrid {
        // Title
        Spacer(Modifier.size(80.dp))
        TitleText("Beheer")
        Spacer(Modifier.size(80.dp))

        // Error button
        if (hasErrors) {
            NavigateButton(
                "⚠\uFE0F Errors ⚠\uFE0F",
                navigate = navigate,
                route = "beheer/error",
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.size(40.dp))
        }

        // Leden en Groepen button
        NavigateButton("Leden en Groepen",
            navigate = navigate,
            route = "beheer/customers",
        )
        Spacer(Modifier.size(30.dp))

        // Items button
        NavigateButton("Items",
            navigate = navigate,
            route = "beheer/items",
        )
        Spacer(Modifier.size(30.dp))

        // Session button
        NavigateButton("Sessie",
            navigate = navigate,
            route = "beheer/session",
        )
        Spacer(Modifier.size(30.dp))

        // Password button
        NavigateButton("Wachtwoord veranderen",
            navigate = navigate,
            route = "beheer/password",
        )
        Spacer(Modifier.size(50.dp))

        BigButton("Hele zooi afrekenen",
            onClick = export,
            rounded = true,
        )
    }
}
