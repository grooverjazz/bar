package org.groover.bar.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


abstract class BarError {
    @Composable
    internal fun Panel(callback: () -> Unit, vararg buttons: Pair<String, () -> Unit>) {
        val desc = toString()

        return Box(
            Modifier.fillMaxWidth().padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray)
        ) {
            Column(Modifier.fillMaxWidth().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(desc,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.size(10.dp))

                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    buttons.forEach { (name, func) ->
                        Button({ func(); callback() },
                            modifier = Modifier.weight(1f)
                        ) { Text(name) }
                    }
                }
            }
        }
    }

    @Composable
    abstract fun Panel(callback: () -> Unit)
}