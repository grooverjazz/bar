package org.groover.bar.app.beheer.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.util.app.EditableBigList
import org.groover.bar.util.app.PopupDialog
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.Cents

@Composable
fun BeheerItemsScreen(
    navigate: (String) -> Unit,
    itemRepository: ItemRepository,
) {
    var itemRemoveState: Item? by remember { mutableStateOf(null) }

    if (itemRemoveState != null) {
        PopupDialog(
            confirmText = "Verwijderen",
            dismissText = "Annuleren",
            onConfirm = {
                itemRepository.remove(itemRemoveState!!.id)
                itemRemoveState = null
            },
            onDismiss = { itemRemoveState = null },
            dialogTitle = "Item verwijderen",
            dialogText = "Weet je zeker dat je dit item (${itemRemoveState!!.name}) wilt verwijderen?",
            icon = Icons.Rounded.Delete,
        )
    }

    BeheerItemsContent(
        navigate = navigate,
        items = itemRepository.data,
        addItem = itemRepository::addItem,
        itemMove = itemRepository::move,
        itemRemove = { itemRemoveState = itemRepository.find(it) },
        onToggleVisible = itemRepository::toggleVisible
    )
}

@Composable
private fun BeheerItemsContent(
    navigate: (String) -> Unit,
    items: List<Item>,
    addItem: (String, Cents, Int, Float) -> Unit,
    itemMove: (Int, Boolean) -> Unit,
    onToggleVisible: (Int) -> Unit,
    itemRemove: (Int) -> Unit,
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Title
        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Items")
        Spacer(modifier = Modifier.size(20.dp))

        // Add item button
        Button(
            modifier = Modifier.height(70.dp),
            onClick = {
                addItem("Item", Cents(0), 0, 0f)
            },
        ) {
            Text("Voeg item toe",
                fontSize = 30.sp,
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        // Items edit list
        EditableBigList(
            height = 900.dp,
            elements = items,
            getName = { it.name },
            getVisible = { it.visible },
            getColor = { it.color },
            fontColor = Color.Black,
            onClick = { navigate("beheer/items/item/${it.id}") },
            onMove = itemMove,
            onToggleVisible = onToggleVisible,
            onDelete = itemRemove
        )
    }
}
