package org.groover.bar.app.beheer.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.LazyBigList
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.Cents

@Composable
fun BeheerItemsScreen(
    navigate: (String) -> Unit,
    itemRepository: ItemRepository,
) {
    val itemMoveUp = { id: Int -> itemRepository.move(id, moveUp = true) }
    val itemMoveDown = { id: Int -> itemRepository.move(id, moveUp = false) }

    BeheerItemsContent(
        navigate = navigate,
        items = itemRepository.data,
        addItem = itemRepository::addItem,
        itemMoveUp = itemMoveUp,
        itemMoveDown = itemMoveDown,
        itemRemove = itemRepository::remove
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
        var itemRemoveState: Item? by remember { mutableStateOf(null) }

        if (itemRemoveState != null) {
            PopupDialog(
                confirmText = "Verwijderen",
                dismissText = "Annuleren",
                onConfirm = {
                    itemRemove(itemRemoveState!!.id)
                    itemRemoveState = null
                },
                onDismiss = { itemRemoveState = null },
                dialogTitle = "Item verwijderen",
                dialogText = "Weet je zeker dat je dit item (${itemRemoveState!!.name}) wilt verwijderen?",
                icon = Icons.Rounded.Delete,
            )
        }

        // Title
        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Items")
        Spacer(modifier = Modifier.size(20.dp))

        // Add item button
        Button(
            modifier = Modifier.height(70.dp),
            onClick = {
                addItem("Item", Cents(0), 0, 0)
            },
        ) {
            Text("Voeg item toe",
                fontSize = 30.sp,
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        // Items edit list
        LazyBigList(height = 900.dp) {
            items(items) { item ->
                ItemEditItem(
                    title = item.name,
                    color = item.colorC,
                    onTitleClick = { navigate("beheer/items/item/${item.id}") },
                    onUpClick = { itemMoveUp(item.id) },
                    onDownClick = { itemMoveDown(item.id) },
                    onDeleteClick = { itemRemoveState = item }
                )
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
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
            onClick = onTitleClick,
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = RectangleShape
        ) {
            Text(
                text = title,
                fontSize = 25.sp,
                color = Color.Black,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RectangleShape,
                onClick = onDeleteClick
            ) {
                Icon(Icons.Rounded.Delete, null)
            }

            val c = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            Button(modifier = Modifier.weight(2f).height(50.dp),
                colors = c,
                shape = RectangleShape,
                onClick = onUpClick
            ) {
                Icon(Icons.Rounded.KeyboardArrowUp, null)
            }
            Button(modifier = Modifier.weight(2f).height(50.dp),
                colors = c,
                shape = RectangleShape,
                onClick = onDownClick
            ) {
                Icon(Icons.Rounded.KeyboardArrowDown, null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupDialog(
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = { Icon(icon, null) },
        title = { Text(text = dialogTitle) },
        text = { Text(dialogText) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}