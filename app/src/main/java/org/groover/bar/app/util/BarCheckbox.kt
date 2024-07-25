package org.groover.bar.app.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * A simple labeled checkbox.
 * From: https://stackoverflow.com/a/76227672
 */
@Composable
fun BarCheckbox(
    label: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit
) {
    // Checkbox with text on right side
    Row(Modifier
        .fillMaxWidth()
        .height(40.dp)
        .clickable(
            role = Role.Checkbox,
            onClick = { onStateChange(!state) }
        )
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}