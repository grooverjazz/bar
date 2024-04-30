package org.groover.bar.data.item

import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import java.util.Locale

data class Item(
    override val id: Int,
    val name: String,
    val price: Float, // TODO: beter datatype
    val btwPercentage: Int
): BarData() {
    val priceWithBtw: Float = price * (1f + (btwPercentage / 100.0f))

    // String formatting helper properties
    private val locale = Locale("nl")

    val priceStringNoEuro: String = "%.2f".format(locale, price)
    val priceString: String = "€" + priceStringNoEuro

    val priceWithBtwString: String = "€%.2f".format(locale, priceWithBtw)

    override fun toString(): String = "$name ($priceString, $btwPercentage% BTW)"

    companion object {
        // (Serializes the item)
        fun serialize(item: Item): String {
            // Return serialization
            return CSV.serialize(
                item.id.toString(),
                item.name,
                item.priceStringNoEuro,
                item.btwPercentage.toString()
            )
        }

        // (Deserializes the item)
        fun deserialize(str: String): Item {
            // Extract from split string
            val (idStr, name, priceStr, btwPercentageStr) = CSV.deserialize(str)

            // Turn id, price and BTW-percentage into floats
            val id = idStr.toInt()
            val price = priceStr.toFloat()
            val btwPercentage = btwPercentageStr.toInt()

            return Item(id, name, price, btwPercentage)
        }
    }
}