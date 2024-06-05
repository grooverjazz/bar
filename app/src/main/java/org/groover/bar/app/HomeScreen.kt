package org.groover.bar.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.groover.bar.R
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun HomeScreen(
    navigate: (String) -> Unit,
    sessionName: String,
) {
    HomeContent(
        navigate = navigate,
        sessionName = sessionName
    )
}


@Composable
private fun HomeContent(
   navigate: (String) -> Unit,
   sessionName: String,
) {
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        Spacer(Modifier.size(100.dp))

        // Title
        Image(
            painter = painterResource(id = R.drawable.bartablet),
            contentDescription = ""
        )

        Spacer(Modifier.size(120.dp))

        // Bar button
        NavigateButton(
            navigate = navigate,
            text = "Bar",
            route = "bar",
        )

        Spacer(Modifier.size(30.dp))

        // Bar Beheer button
        NavigateButton(
            navigate = navigate,
            text = "Beheer",
            route = "beheer/login",
        )

        Spacer(Modifier.size(50.dp))

        Text(
            "Huidige sessie: $sessionName",
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.size(220.dp))

        val context = LocalContext.current
        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        Text(
            "Versie $versionName",
            textAlign = TextAlign.Center
        )
    }
}