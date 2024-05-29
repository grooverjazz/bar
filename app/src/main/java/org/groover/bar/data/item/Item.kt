package org.groover.bar.data.item

import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents
import java.util.Locale

data class Item(
    override val id: Int,
    val name: String,
    val price: Cents, // incl. BTW, in cents!
    val btwPercentage: Int
): BarData() {
    // String formatting helper properties
    val priceStringNoEuro = price.stringWithoutEuro
    val priceString = price.stringWithEuro

    override fun toString(): String = "$name ($priceString incl. $btwPercentage% BTW)"

    companion object {
        // (Serializes the item)
        fun serialize(item: Item): String {
            // Return serialization
            return CSV.serialize(
                item.id.toString(),
                item.name,
                item.price.amount.toString(),
                item.btwPercentage.toString()
            )
        }

        // (Deserializes the item)
        fun deserialize(str: String): Item {
            // Extract from split string
            val (idStr, name, priceStr, btwPercentageStr) = CSV.deserialize(str)

            // Turn id, price and BTW-percentage into floats
            val id = idStr.toInt()
            val price = Cents(priceStr.toInt())
            val btwPercentage = btwPercentageStr.toInt()

            return Item(id, name, price, btwPercentage)
        }
    }
}