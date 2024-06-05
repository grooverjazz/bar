package org.groover.bar.data.item

import androidx.compose.ui.graphics.Color
import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents

data class Item(
    override val id: Int,
    val name: String,
    val price: Cents, // incl. BTW, in cents!
    val btwPercentage: Int,
    val color: Int,
): BarData() {
    val colorC: Color = Color.hsv(color.toFloat(), 0.4f, 1f)

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
                item.btwPercentage.toString(),
                item.color.toString(),
            )
        }

        // (Deserializes the item)
        fun deserialize(str: String): Item {
            // Extract from split string
            val (idStr, name, priceStr, btwPercentageStr, colorStr) = CSV.deserialize(str)

            // Turn id, price and BTW-percentage into floats
            val id = idStr.toInt()
            val price = Cents(priceStr.toInt())
            val btwPercentage = btwPercentageStr.toInt()
            val color = colorStr.toInt()

            return Item(id, name, price, btwPercentage, color)
        }
    }
}