package org.groover.bar.app.beheer.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.app.util.BarButton
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.app.util.BarListMoveable
import org.groover.bar.app.util.PromptDialog
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout
import org.groover.bar.data.util.BTWPercentage
import org.groover.bar.data.util.Cents



@Composable
fun BeheerItemsScreen(
    navigate: (route: String) -> Unit,
    itemRepository: ItemRepository,
) {
    // Remove popup
    var itemRemoveState: Item? by remember { mutableStateOf(null) }
    if (itemRemoveState != null) {
        PromptDialog(
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

    // Content
    BeheerItemsContent(
        navigate = navigate,
        items = itemRepository.data,
        addNewItem = { itemRepository.addItem("Item", Cents(0), BTWPercentage.Geen, 0f) },
        itemMove = itemRepository::move,
        itemRemove = { itemRemoveState = itemRepository.find(it) },
        onToggleVisible = itemRepository::toggleVisible,
    )
}

@Composable
private fun BeheerItemsContent(
    navigate: (route: String) -> Unit,
    items: List<Item>,
    addNewItem: () -> Unit,
    itemMove: (Int, Boolean) -> Unit,
    onToggleVisible: (Int) -> Unit,
    itemRemove: (Int) -> Unit,
) {
    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(20.dp))
        BarTitle("Items")
        Spacer(Modifier.size(20.dp))

        // Add item button
        BarButton("Voeg item toe",
            onClick = addNewItem,
            rounded = true,
        )
        Spacer(Modifier.size(20.dp))

        // Items edit list
        BarListMoveable(
            height = 900.dp,
            elements = items,
            getName = { it.name },
            getVisible = { it.visible },
            getColor = { it.color },
            fontColor = Color.Black,
            onClick = { navigate("beheer/items/item/${it.id}") },
            onMove = itemMove,
            onToggleVisible = onToggleVisible,
            onRemove = itemRemove,
        )
    }
}
