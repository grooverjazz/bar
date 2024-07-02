package org.groover.bar.data.item

import androidx.compose.ui.graphics.Color
import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.toCents

data class Item(
    override val id: Int,
    val name: String,
    val price: Cents, // incl. BTW!
    val btwPercentage: Int,
    val hue: Float,
): BarData() {
    // Color, defined by hue property
    val color: Color = getColor(hue)

    // (Converts the Group to a String)
    override fun toString(): String = "$name (${price.toStringWithEuro()} incl. $btwPercentage% BTW)"

    companion object {
        // (Turns the hue into a color)
        fun getColor(hue: Float) = Color.hsv(hue * 360f, 0.4f, 1f)

        // (Serializes the item)
        fun serialize(item: Item): String {
            // Return serialization
            return CSV.serialize(
                item.id.toString(),
                item.name,
                item.price.toString(),
                item.btwPercentage.toString(),
                item.hue.toString(),
            )
        }

        // (Deserializes the item)
        fun deserialize(str: String): Item {
            // Get properties
            val props = CSV.deserialize(str)
            val (idStr, name, priceStr, btwPercentageStr, hueStr) = props

            // Deserialize properties
            val id = idStr.toInt()
            val price = priceStr.toCents()
            val btwPercentage = btwPercentageStr.toInt()
            val hue = hueStr.replace(',','.').toFloat()

            // Return item
            return Item(id, name, price, btwPercentage, hue)
        }
    }
}