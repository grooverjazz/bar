package org.groover.bar.data.item

import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.sum
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository

class ItemRepository(
    fileOpener: FileOpener,
) : Repository<Item>(
    fileOpener,
    "items.csv",
    Item.Companion::serialize,
    Item.Companion::deserialize,
    listOf("ID", "Name", "Price", "BTW Percentage", "Tint"),
) {
    init {
        open()
    }

    // (Adds an item)
    fun addItem(name: String, price: Cents, btwPercentage: Int, hue: Float) {
        // Create new item
        val newItem = Item(
            generateId(),
            name,
            price,
            btwPercentage,
            hue,
        )

        // Prepend
        prepend(newItem)
    }

    // (Changes an item)
    fun changeItem(
        itemId: Int,
        newName: String,
        newPrice: Cents,
        newBtwPercentage: Int,
        newHue: Float
    ) {
        // Create new item
        val newItem = Item(
            itemId,
            newName,
            newPrice,
            newBtwPercentage,
            newHue,
        )

        // Replace
        replace(itemId, newItem)
    }

    // (Gets the total cost of the specified amounts)
    fun costProduct(amounts: List<Int>): Cents {
        return (data zip amounts)
            .map { (item, amount) -> item.price * amount }
            .sum()
    }
}