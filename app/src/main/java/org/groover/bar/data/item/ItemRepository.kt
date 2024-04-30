package org.groover.bar.data.item

import android.content.Context
import org.groover.bar.util.data.Repository

class ItemRepository(
    context: Context
) : Repository<Item>(
    context,
    "items.txt",
    Item.Companion::serialize,
    Item.Companion::deserialize,
    listOf("ID", "Name", "Price", "BTW Percentage"),
) {
}