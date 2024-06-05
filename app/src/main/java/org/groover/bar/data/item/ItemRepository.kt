package org.groover.bar.data.item

import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository

class ItemRepository(
    fileOpener: FileOpener,
) : Repository<Item>(
    fileOpener,
    "items.csv",
    Item.Companion::serialize,
    Item.Companion::deserialize,
    listOf("ID", "Name", "Price", "BTW Percentage"),
) {
    fun addItem(name: String, price: Cents, btwPercentage: Int, color: Int) {
        // Create new item
        val newItem = Item(
            generateId(),
            name,
            price,
            btwPercentage,
            color
        )

        // Add to data
        data += newItem

        // Save
        save()
    }

    fun changeItem(itemId: Int, newName: String, newPrice: Cents, newBtwPercentage: Int, newColor: Int) {
        // Create new item
        val newItem = Item(
            itemId,
            newName,
            newPrice,
            newBtwPercentage,
            newColor,
        )

        // Replace the old one
        replaceById(itemId, newItem)
    }

    fun moveItem(itemId: Int, moveUp: Boolean) {
        val item = lookupById(itemId) ?: throw Exception("Error tijdens verplaatsen van item")

        // Check if movement is necessary
        if ((moveUp && data.first() == item) || (!moveUp && data.last() == item))
            return

        // Get all items before and after the specified item
        val before = data.takeWhile { it.id != itemId }
        val after = data.takeLastWhile { it.id != itemId }

        // Shuffle them
        val newData = if (moveUp)
            before.dropLast(1) + listOf(item, before.last()) + after
        else
            before + listOf(after.first(), item) + after.drop(1)

        // Reassign data
        data.clear()
        data += newData

        // Save
        save()
    }
}