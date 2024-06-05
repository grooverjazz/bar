package org.groover.bar.app.beheer.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.Cents

@Composable
fun BeheerItemsScreen(
    navigate: (String) -> Unit,
    itemRepository: ItemRepository,
) {
    val itemMoveUp = { id: Int -> itemRepository.moveItem(id, true) }
    val itemMoveDown = { id: Int -> itemRepository.moveItem(id, false) }

    BeheerItemsContent(
        navigate = navigate,
        items = itemRepository.data,
        addItem = itemRepository::addItem,
        itemMoveUp = itemMoveUp,
        itemMoveDown = itemMoveDown,
        itemRemove = itemRepository::removeById
    )

}

@Composable
private fun BeheerItemsContent(
    navigate: (String) -> Unit,
    items: List<Item>,
    addItem: (String, Cents, Int, Int) -> Unit,
    itemMoveUp: (Int) -> Unit,
    itemMoveDown: (Int) -> Unit,
    itemRemove: (Int) -> Unit,
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
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
            addItem(
                "Tijdelijk item",
                Cents(0),
                0,
                0,
            )
        }) {
            Text("Add item")
        }

        Spacer(modifier = Modifier.size(20.dp))

        // Items edit list
        LazyColumn(
            modifier = Modifier.padding(10.dp).background(Color.LightGray).height(800.dp)
        ) {
            items.forEach { item ->
                item {
                    ItemEditItem(
                        title = item.name,
                        color = item.colorC,
                        onTitleClick = { navigate("beheer/items/item/${item.id}") },
                        onUpClick = { itemMoveUp(item.id) },
                        onDownClick = { itemMoveDown(item.id) },
                        onDeleteClick = { itemRemove(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemEditItem(
    title: String,
    color: Color,
    onTitleClick: () -> Unit,
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(vertical = 5.dp).fillMaxWidth()
    ) {
        Button(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
            onClick = onTitleClick,
            colors = ButtonDefaults.buttonColors(containerColor = color),
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                color = Color.Black,
            )
        }

        val colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)

        Row {
            Button(modifier = Modifier.weight(1f),
                colors = colors,
                onClick = onUpClick
            ) {
                Icon(Icons.Rounded.KeyboardArrowUp, null)
            }
            Button(modifier = Modifier.weight(1f),
                colors = colors,
                onClick = onDownClick
            ) {
                Icon(Icons.Rounded.KeyboardArrowDown, null)
            }
            Button(modifier = Modifier.weight(1f),
                colors = colors,
                onClick = onDeleteClick
            ) {
                Icon(Icons.Rounded.Delete, null)
            }
        }
    }
}