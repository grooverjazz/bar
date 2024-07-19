package org.groover.bar.data.item

import androidx.compose.ui.util.fastMap
import org.groover.bar.data.util.BTWPercentage
import org.groover.bar.data.util.Cents
import org.groover.bar.data.util.Cents.Companion.sum
import org.groover.bar.data.util.FileOpener
import org.groover.bar.data.util.Repository

class ItemRepository(
    fileOpener: FileOpener,
) : Repository<Item>(
    fileOpener,
    "items.csv",
    Item.Companion::serialize,
    Item.Companion::deserialize,
    listOf("ID", "Name", "Visible", "Price", "BTW Percentage", "Hue"),
) {
    init {
        open()
    }

    // (Adds an item)
    fun addItem(
        name: String,
        price: Cents,
        btwPercentage: BTWPercentage,
        hue: Float,
        errorHandlingOverrideId: Int? = null // ONLY USE FOR ERROR HANDLING
    ): Item {
        // Create new item
        val newItem = Item(
            errorHandlingOverrideId ?: generateId(),
            name,
            true,
            price,
            btwPercentage,
            hue,
        )

        // Prepend
        addToStart(newItem)

        // Return item
        return newItem
    }

    // (Changes an item)
    fun changeItem(
        itemId: Int,
        newName: String,
        newVisible: Boolean,
        newPrice: Cents,
        newBtwPercentage: BTWPercentage,
        newHue: Float,
    ) {
        // Create new item
        val newItem = Item(
            itemId,
            newName,
            newVisible,
            newPrice,
            newBtwPercentage,
            newHue,
        )

        // Replace
        replace(itemId, newItem)
    }

    // (Toggles visibility for the given item)
    fun toggleVisible(id: Int) {
        val item = find(id) ?: throw Exception("Error bij wisselen zichtbaarheid")
        replace(id, item.copy(visible = !item.visible))
    }

    // (Gets the total cost of the specified amounts)
    fun costProduct(amounts: List<Int>): Cents {
        return (data zip amounts)
            .fastMap { (item, amount) -> item.price * amount }
            .sum()
    }
}