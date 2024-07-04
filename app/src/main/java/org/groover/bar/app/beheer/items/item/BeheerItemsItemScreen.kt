package org.groover.bar.app.beheer.items.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.LabeledTextField
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.toCents

@Composable
fun BeheerItemsItemScreen(
    navigate: (String) -> Unit,
    itemRepository: ItemRepository,
    itemId: Int
) {
    // Get item
    val item = itemRepository.find(itemId)

    // Error
    if (item == null) {
        BeheerItemsItemError(
            navigate = navigate,
            itemId = itemId
        )
        return
    }

    // (Finishes editing the item)
    val finishEdit = { newName: String, newPrice: Cents, newBtwPercentage: Int, newHue: Float ->
        // Change the item
        itemRepository.changeItem(itemId, newName, item.visible, newPrice, newBtwPercentage, newHue)

        // Navigate back
        navigate("beheer/items")
    }

    // Content
    BeheerItemsItemContent(
        navigate = navigate,
        item = item,
        finishEdit = finishEdit,
    )
}

@Composable
private fun BeheerItemsItemError(
    navigate: (String) -> Unit,
    itemId: Int
) {
    VerticalGrid {
        // Title
        TitleText("Kan item met ID $itemId niet vinden!")
    }
}


@Composable
private fun BeheerItemsItemContent(
    navigate: (String) -> Unit,
    item: Item,
    finishEdit: (String, Cents, Int, Float) -> Unit
) {
    // Remember name, price and BTW percentage
    var newName: String by remember { mutableStateOf(item.name) }
    var newPriceStr: String by remember { mutableStateOf(item.price.toString()) }
    var newBtwPercentageStr: String by remember { mutableStateOf(item.btwPercentage.toString()) }
    var newColorFloat: Float by remember { mutableStateOf(item.hue) }

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
        LabeledTextField(
            text = "BTW-percentage",
            value = newBtwPercentageStr,
            onValueChange = { newBtwPercentageStr = it },
        )
        Spacer(Modifier.size(20.dp))

        // New color field
        Text(
            text = "Tint:",
            fontSize = 20.sp,
        )
        Spacer(Modifier.height(5.dp))
        Slider(
            value = newColorFloat,
            onValueChange = { newColorFloat = it }
        )
        Spacer(Modifier.size(20.dp))

        // Color box
        Box(Modifier
            .height(30.dp)
            .background(color = Item.getColor(newColorFloat))
        )
        Spacer(Modifier.size(30.dp))

        // Save button
        BigButton("Opslaan",
            onClick = {
                // Convert new price and BTW percentage, finish
                finishEdit(
                    newName,
                    newPriceStr.toCents(),
                    newBtwPercentageStr.toInt(),
                    newColorFloat,
                )
            },
            rounded = true,
        )
    }
}