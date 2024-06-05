package org.groover.bar.app.beheer.items.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.Cents

@Composable
fun BeheerItemsItemScreen(
    navigate: (String) -> Unit,
    itemRepository: ItemRepository,
    itemId: Int
) {
    // Get item
    val item = itemRepository.lookupById(itemId)

    if (item == null) {
        BeheerItemsItemError(
            navigate = navigate,
            itemId = itemId
        )
        return
    }

    // Finishes editing the item
    val finishEdit = { newName: String, newPrice: Cents, newBtwPercentage: Int, newColor: Int ->
        // Change the item
        itemRepository.changeItem(itemId, newName, newPrice, newBtwPercentage, newColor)

        // Navigate back
        navigate("beheer/items")
    }

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
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "beheer/items",
            height = 60.dp,
        )
        Spacer(modifier = Modifier.size(20.dp))

        TitleText("Kan item met ID $itemId niet vinden!")
    }
}


@Composable
private fun BeheerItemsItemContent(
    navigate: (String) -> Unit,
    item: Item,
    finishEdit: (String, Cents, Int, Int) -> Unit
) {
    // Remember name, price and BTW percentage
    var newName: String by remember { mutableStateOf(item.name) }
    var newPriceStr: String by remember { mutableStateOf(item.priceStringNoEuro) }
    var newBtwPercentageStr: String by remember { mutableStateOf(item.btwPercentage.toString()) }
    var newColorFloat: Float by remember { mutableStateOf(item.color.toFloat() / 360f) }

    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "beheer/items",
            height = 60.dp,
        )
        Spacer(modifier = Modifier.size(20.dp))

        TitleText("Item bewerken")
        Spacer(Modifier.size(20.dp))

        // Name field
        TextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text("Naam") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // New price field
        TextField(
            value = newPriceStr,
            onValueChange = { newPriceStr = it.replace('.',',') },
            placeholder = { Text("Naam") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // New BTW percentage field
        TextField(
            value = newBtwPercentageStr,
            onValueChange = { newBtwPercentageStr = it },
            placeholder = { Text("Naam") }
        )

        Slider(
            value = newColorFloat,
            onValueChange = { newColorFloat = it }
        )
        Spacer(modifier = Modifier.size(20.dp))

        val color = Color.hsv(newColorFloat * 360f, 1f, 1f)
        Box(
            modifier = Modifier.height(30.dp).background(color = color)
        )
        Spacer(modifier = Modifier.size(30.dp))

        // Save button
        Button(onClick = {
            // Convert new price and BTW percentage, finish
            finishEdit(
                newName,
                Cents.fromString(newPriceStr),
                newBtwPercentageStr.toInt(),
                (newColorFloat * 360f).toInt(),
            )
        }) {
            Text("Opslaan")
        }
    }
}