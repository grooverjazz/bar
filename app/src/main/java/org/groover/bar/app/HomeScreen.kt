package org.groover.bar.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.groover.bar.R
import org.groover.bar.app.util.BarNavigateButton
import org.groover.bar.app.util.BarLayout

/**
 * The main home screen.
 */
@Composable
fun HomeScreen(
    navigate: (route: String) -> Unit,
    sessionName: String,
    hasErrors: Boolean,
) {
    HomeContent(
        navigate = navigate,
        sessionName = sessionName,
        hasErrors = hasErrors,
    )
}

@Composable
private fun HomeContent(
   navigate: (route: String) -> Unit,
   sessionName: String,
   hasErrors: Boolean,
) {
    BarLayout {
        // Title
        Spacer(Modifier.size(100.dp))
        Image(
            painter = painterResource(id = R.drawable.bartablet),
            contentDescription = "",
        )
        Spacer(Modifier.size(120.dp))

        // Bar button
        BarNavigateButton("Bar",
            navigate = navigate,
            route = "bar",
        )
        Spacer(Modifier.size(30.dp))

        // Bar Beheer button
        BarNavigateButton(
            if (!hasErrors) "Beheer" else "⚠\uFE0F Beheer ⚠\uFE0F",
            navigate = navigate,
            route = "beheer/login",
            color = with(MaterialTheme.colorScheme) { if (!hasErrors) secondary else error },
        )
        Spacer(Modifier.size(50.dp))

        // Session text
        Text("Huidige sessie: $sessionName",
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(220.dp))

        // Version number
        val context = LocalContext.current
        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        Text("Versie $versionName",
            textAlign = TextAlign.Center,
        )
    }
}