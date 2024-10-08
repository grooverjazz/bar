package org.groover.bar.data.item

import androidx.compose.ui.graphics.Color
import org.groover.bar.data.util.BTWPercentage
import org.groover.bar.data.util.BTWPercentage.Companion.toBTWPercentage
import org.groover.bar.data.util.BarData
import org.groover.bar.data.util.CSVHandler
import org.groover.bar.data.util.Cents
import org.groover.bar.data.util.Cents.Companion.toCents

/**
 * An item that can be ordered.
 */
data class Item(
    override val id: Int,
    val name: String,
    val visible: Boolean,
    val price: Cents, // incl. BTW!
    val btwPercentage: BTWPercentage,
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
            return CSVHandler.serialize(
                item.id.toString(),
                item.name,
                item.visible.toString(),
                item.price.toString(),
                item.btwPercentage.serialize(),
                item.hue.toString(),
            )
        }

        // (Deserializes the item)
        fun deserialize(str: String): Item {
            try {
                // Get properties
                val props = CSVHandler.deserialize(str)
                val (idStr, name, visibleStr, priceStr, btwPercentageStr) = props
                val hueStr = props[5]

                // Deserialize properties
                val id = idStr.toInt()
                val visible = visibleStr.equals("true", ignoreCase = true)
                val price = priceStr.toCents()
                val btwPercentage = btwPercentageStr.toBTWPercentage()
                val hue = hueStr.replace(',', '.').toFloat()

                // Return item
                return Item(id, name, visible, price, btwPercentage, hue)
            } catch (e: Exception) {
                throw IllegalStateException("Kan item '$str' niet deserialiseren\n" +
                        "(normaal in de vorm 'id;name;visible;price;btwPercentage;hue')")
            }
        }
    }
}