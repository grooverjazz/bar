package org.groover.bar.app.beheer.items.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.app.util.BarButton
import org.groover.bar.app.util.BarDropdownMenu
import org.groover.bar.app.util.BarTextField
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout
import org.groover.bar.app.util.BarTextFieldType
import org.groover.bar.data.util.BTWPercentage
import org.groover.bar.data.util.Cents
import org.groover.bar.data.util.Cents.Companion.toCents

@Composable
fun BeheerItemsItemScreen(
    navigate: (route: String) -> Unit,
    itemRepository: ItemRepository,
    itemId: Int,
) {
    // Get item
    val item = itemRepository.find(itemId)!!

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeheerItemsItemContent(
    item: Item,
    finishEdit: (String, Cents, BTWPercentage, Float) -> Unit,
) {
    // Remember name, price and BTW percentage
    var newName: String by remember { mutableStateOf(item.name) }
    var newPriceStr: String by remember { mutableStateOf(item.price.toString()) }
    var newBTWPercentage: BTWPercentage by remember { mutableStateOf(item.btwPercentage) }
    var newColorFloat: Float by remember { mutableFloatStateOf(item.hue) }

    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(20.dp))
        BarTitle("Item bewerken")
        Spacer(Modifier.size(20.dp))

        // Name field
        BarTextField(
            text = "Naam",
            value = newName,
            onValueChange = { newName = it },
        )
        Spacer(Modifier.size(20.dp))

        // New price field
        BarTextField(
            text = "Prijs",
            value = newPriceStr,
            onValueChange = { newPriceStr = it.replace('.',',') },
            type = BarTextFieldType.Decimal,
        )
        Spacer(Modifier.size(20.dp))

        // New BTW percentage field
        BarDropdownMenu(
            label = "BTW-percentage:",
            currentValue = newBTWPercentage,
            onValueChange = { newBTWPercentage = it },
            values = BTWPercentage.entries,
            valueToString = BTWPercentage::toString
        )
        Spacer(Modifier.size(20.dp))

        // New tint field
        val newColor = Item.getColor(newColorFloat)
        Text("Tint:",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
        )
        Spacer(Modifier.height(5.dp))
        Slider(
            value = newColorFloat,
            onValueChange = { newColorFloat = it },
            colors = SliderDefaults.colors().copy(activeTrackColor = newColor),
            // Custom 'thumb'
            thumb = { Box(Modifier.size(50.dp).border(3.dp, Color.Black).background(newColor)) },
        )
        Spacer(Modifier.size(20.dp))

        // Save button
        BarButton("Opslaan",
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
