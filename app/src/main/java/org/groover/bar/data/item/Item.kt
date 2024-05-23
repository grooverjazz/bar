package org.groover.bar.data.item

import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import java.util.Locale

data class Item(
    override val id: Int,
    val name: String,
    val price: Int, // incl. BTW, in cents!
    val btwPercentage: Int
): BarData() {
    // String formatting helper properties
    val priceStringNoEuro: String = "${price / 100}.${"%02d".format(price % 100)}"
    val priceString: String = "€" + priceStringNoEuro

    override fun toString(): String = "$name ($priceString incl. $btwPercentage% BTW)"

    companion object {
        // (Serializes the item)
        fun serialize(item: Item): String {
            // Return serialization
            return CSV.serialize(
                item.id.toString(),
                item.name,
                item.price.toString(),
                item.btwPercentage.toString()
            )
        }

        // (Deserializes the item)
        fun deserialize(str: String): Item {
            // Extract from split string
            val (idStr, name, priceStr, btwPercentageStr) = CSV.deserialize(str)

            // Turn id, price and BTW-percentage into floats
            val id = idStr.toInt()
            val price = priceStr.toInt()
            val btwPercentage = btwPercentageStr.toInt()

            return Item(id, name, price, btwPercentage)
        }
    }
}