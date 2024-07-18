package org.groover.bar.app.beheer.items.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.LabeledTextField
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.BTWPercentage
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.toCents
import kotlin.math.exp

@Composable
fun BeheerItemsItemScreen(
    navigate: (route: String) -> Unit,
    itemRepository: ItemRepository,
    itemId: Int,
) {
    // Get item
    val item = itemRepository.find(itemId)

    // Error
    if (item == null) {
        BeheerItemsItemError(itemId = itemId)
        return
    }

    // (Finishes editing the item)
    val finishEdit = { newName: String, newPrice: Cents, newBtwPercentage: BTWPercentage, newHue: Float ->
        // Change the item
        itemRepository.changeItem(itemId, newName, item.visible, newPrice, newBtwPercentage, newHue)

        // Navigate back
        navigate("beheer/items")
    }

    // Content
    BeheerItemsItemContent(
        item = item,
        finishEdit = finishEdit,
    )
}

@Composable
private fun BeheerItemsItemError(
    itemId: Int,
) {
    // UI
    VerticalGrid {
        // Title
        TitleText("Kan item met ID $itemId niet vinden!")
    }
}


@Composable
private fun BeheerItemsItemContent(
    item: Item,
    finishEdit: (String, Cents, BTWPercentage, Float) -> Unit,
) {
    // Remember name, price and BTW percentage
    var newName: String by remember { mutableStateOf(item.name) }
    var newPriceStr: String by remember { mutableStateOf(item.price.toString()) }
    var newBTWPercentage: BTWPercentage by remember { mutableStateOf(item.btwPercentage) }
    var newColorFloat: Float by remember { mutableStateOf(item.hue) }

    // UI
    VerticalGrid {
        // Title
        Spacer(Modifier.size(20.dp))
        TitleText("Item bewerken")
        Spacer(Modifier.size(20.dp))

        // Name field
        LabeledTextField(
            text = "Naam",
            value = newName,
            onValueChange = { newName = it },
        )
        Spacer(Modifier.size(20.dp))

        // New price field
        LabeledTextField(
            text = "Prijs",
            value = newPriceStr,
            onValueChange = { newPriceStr = it.replace('.',',') },
        )
        Spacer(Modifier.size(20.dp))

        // New BTW percentage field
        BTWDropdown(newBTWPercentage) { newBTWPercentage = it }
        Spacer(Modifier.size(20.dp))

        // New color field
        Text("Tint:",
            fontSize = 20.sp,
        )
        Spacer(Modifier.height(5.dp))
        Slider(
            value = newColorFloat,
            onValueChange = { newColorFloat = it },
        )
        Spacer(Modifier.size(20.dp))

        // Color box
        Box(
            Modifier
                .height(30.dp)
                .background(color = Item.getColor(newColorFloat)),
        )
        Spacer(Modifier.size(30.dp))

        // Save button
        BigButton("Opslaan",
            onClick = {
                // Convert new price and BTW percentage, finish
                finishEdit(
                    newName,
                    newPriceStr.toCents(),
                    newBTWPercentage,
                    newColorFloat,
                )
            },
            rounded = true,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BTWDropdown(currentPercentage: BTWPercentage, changePercentage: (BTWPercentage) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Label
        Text(
            "BTW-percentage:",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
        )
        Spacer(Modifier.height(5.dp))

        // Dropdown menu
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            TextField(
                readOnly = true,
                value = currentPercentage.toString(),
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                BTWPercentage.entries.forEach { percentage ->
                    DropdownMenuItem(onClick = { changePercentage(percentage); expanded = false }, text = {
                        Text(percentage.toString())
                    })
                }
            }
        }
    }
}