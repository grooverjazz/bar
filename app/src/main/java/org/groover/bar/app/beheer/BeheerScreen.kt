package org.groover.bar.app.beheer

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.groover.bar.export.ExportHandler
import org.groover.bar.app.util.BarButton
import org.groover.bar.app.util.BarCheckbox
import org.groover.bar.app.util.BarNavigateButton
import org.groover.bar.app.util.ProgressDialog
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout

@Composable
fun BeheerScreen(
    navigate: (route: String) -> Unit,
    exportHandler: ExportHandler,
    exportName: String,
    hasErrors: Boolean,
) {
    val context = LocalContext.current

    // Export progress bar state
    var openExternal by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    // (Creates an export, while showing the progress bar)
    val exportWithProgress: () -> Unit = {
        // Launch into separate thread
        coroutineScope.launch {
            isExporting = true

            withContext(Dispatchers.IO) {
                exportHandler.export(exportName, openExternal) { progress = it }
            }

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
        openExternal = openExternal,
        setOpenExternal = { openExternal = it }
    )
}

@Composable
private fun BeheerContent(
    navigate: (route: String) -> Unit,
    export: () -> Unit,
    hasErrors: Boolean,
    openExternal: Boolean,
    setOpenExternal: (Boolean) -> Unit,
) {
    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(80.dp))
        BarTitle("Beheer")
        Spacer(Modifier.size(80.dp))

        // Error button
        if (hasErrors) {
            BarNavigateButton(
                "⚠\uFE0F Errors ⚠\uFE0F",
                navigate = navigate,
                route = "beheer/error",
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.size(40.dp))
        }

        // Leden en Groepen button
        BarNavigateButton("Leden en Groepen",
            navigate = navigate,
            route = "beheer/customers",
        )
        Spacer(Modifier.size(30.dp))

        // Items button
        BarNavigateButton("Items",
            navigate = navigate,
            route = "beheer/items",
        )
        Spacer(Modifier.size(30.dp))

        // Session button
        BarNavigateButton("Sessie",
            navigate = navigate,
            route = "beheer/session",
        )
        Spacer(Modifier.size(30.dp))

        // Password button
        BarNavigateButton("Wachtwoord veranderen",
            navigate = navigate,
            route = "beheer/password",
        )
        Spacer(Modifier.size(50.dp))

        BarButton("Hele zooi afrekenen",
            onClick = export,
            rounded = true,
        )
        BarCheckbox("Export-bestand openen na export", openExternal, setOpenExternal)
    }
}
