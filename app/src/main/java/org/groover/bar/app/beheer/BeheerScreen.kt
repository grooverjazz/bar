package org.groover.bar.app.beheer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.export.BTWHandler
import org.groover.bar.export.ExportHandler
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import kotlin.reflect.KFunction0

@Composable
fun BeheerScreen(
    navigate: (String) -> Unit,
    exportHandler: ExportHandler,
    btwHandler: BTWHandler
) {
    BeheerContent(
        navigate = navigate,
        export = exportHandler::export,
        exportBtw = btwHandler::export,
    )
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
