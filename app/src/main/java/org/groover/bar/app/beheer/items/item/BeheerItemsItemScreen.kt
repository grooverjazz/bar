package org.groover.bar.app.beheer.items.item

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerItemsItemScreen(
    navController: NavController,
    itemRepository: ItemRepository,
    itemId: Int
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Get item
        val item = itemRepository.lookupById(itemId)!!

        // Remember name, price and BTW percentage
        var newName: String by remember { mutableStateOf(item.name) }
        var newPriceStr: String by remember { mutableStateOf(item.price.toString()) }
        var newBtwPercentageStr: String by remember { mutableStateOf(item.btwPercentage.toString()) }

        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "beheer/items",
            height = 60.dp,
        )
        Spacer(modifier = Modifier.size(20.dp))

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
            onValueChange = { newPriceStr = it },
            placeholder = { Text("Naam") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // New BTW percentage field
        TextField(
            value = newBtwPercentageStr,
            onValueChange = { newBtwPercentageStr = it },
            placeholder = { Text("Naam") }
        )

        Spacer(modifier = Modifier.size(50.dp))

        // Save button
        Button(onClick = {
            // Convert new price and BTW percentage
            val newPrice = newPriceStr.toFloat()
            val newBtwPercentage = newBtwPercentageStr.toInt()

            // Change the item
            itemRepository.changeItem(itemId, newName, newPrice, newBtwPercentage)

            // Navigate back
            navController.navigate("beheer/items")
        }) {
            Text("Opslaan")
        }
    }
}