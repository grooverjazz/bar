package org.groover.bar.data.item

import android.content.Context
import org.groover.bar.util.data.Repository

class ItemRepository(
    context: Context
) : Repository<Item>(
    context,
    "items.csv",
    Item.Companion::serialize,
    Item.Companion::deserialize,
    listOf("ID", "Name", "Price", "BTW Percentage"),
) {
    fun addItem(name: String, price: Int, btwPercentage: Int) {
        // Create new item
        val newItem = Item(
            generateId(),
            name,
            price,
            btwPercentage
        )

        // Add to data
        data += newItem

        // Save
        save()
    }



    fun changeItem(itemId: Int, newName: String, newPrice: Int, newBtwPercentage: Int) {
        // Create new item
        val newItem = Item(
            itemId,
            newName,
            newPrice,
            newBtwPercentage
        )

        // Replace the old one
        replaceById(itemId, newItem)
    }

    fun moveItem(itemId: Int, moveUp: Boolean) {
        val item = lookupById(itemId)!!

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