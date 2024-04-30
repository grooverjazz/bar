package org.groover.bar.app.beheer.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerItemsScreen(
    navController: NavController,
    itemRepository: ItemRepository,
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        val items = itemRepository.data

        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "beheer",
            height = 60.dp,
        )

        // Title
        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Items")
        Spacer(modifier = Modifier.size(20.dp))


        // Add item button
        Button(onClick = {
            itemRepository.addItem(
                "Tijdelijk item ${itemRepository.data.size}",
                0.0f,
                0,
            )
        }) {
            Text("Add item")
        }

        Spacer(modifier = Modifier.size(20.dp))

        // Items edit list
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.padding(5.dp)
        ) {
            items.forEach { item ->
                item {
                    ItemEditItem(
                        title = item.name,
                        onTitleClick = { navController.navigate("beheer/items/item/${item.id}") },
                        onUpClick = { itemRepository.moveItem(item.id, true) },
                        onDownClick = { itemRepository.moveItem(item.id, false) },
                        onDeleteClick = { itemRepository.removeById(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemEditItem(
    title: String,
    onTitleClick: () -> Unit,
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        Button(modifier = Modifier.fillMaxWidth(),
            onClick = onTitleClick
        ) {
            Text(title)
        }

        Row {
            Button(modifier = Modifier.weight(1f),
                onClick = onUpClick
            ) {
                Icon(Icons.Rounded.KeyboardArrowUp, null)
            }
            Button(modifier = Modifier.weight(1f),
                onClick = onDownClick
            ) {
                Icon(Icons.Rounded.KeyboardArrowDown, null)
            }
            Button(modifier = Modifier.weight(1f),
                onClick = onDeleteClick
            ) {
                Icon(Icons.Rounded.Delete, null)
            }
        }
    }
}