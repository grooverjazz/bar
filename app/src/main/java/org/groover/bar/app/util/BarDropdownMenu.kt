package org.groover.bar.app.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A simple labeled dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BarDropdownMenu(
    label: String,
    currentValue: T,
    onValueChange: (T) -> Unit,
    values: Iterable<T>,
    valueToString: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Label
        Text(label,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
        )
        Spacer(Modifier.height(5.dp))

        // Dropdown menu
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            TextField(valueToString(currentValue),
                readOnly = true,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                values.forEach { value ->
                    DropdownMenuItem(onClick = { onValueChange(value); expanded = false }, text = {
                        Text(valueToString(value))
                    })
                }
            }
        }
    }
}
