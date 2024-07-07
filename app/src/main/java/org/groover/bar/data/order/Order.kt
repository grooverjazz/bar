package org.groover.bar.data.order

import androidx.compose.ui.util.fastMap
import org.groover.bar.data.item.Item
import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.sum
import java.util.Date

data class Order(
    override val id: Int,
    val customerId: Int, // (this can be a user ID or a group ID)
    val timestamp: Date,
    private val amounts: MutableMap<Int, Int>, // (maps every item to an amount)
): BarData() {
    // Gets the amount ordered of the specified item
    //  (If the item does not exist, initialize it to 0)
    fun getAmount(itemId: Int): Int = amounts.getOrElse(itemId) { // TODO: setting amounts[itemId] needed?
        amounts[itemId] = 0
        return 0
    }

    // (Gets the total price of the order)
    fun getTotalPrice(items: List<Item>): Cents = items
        .fastMap { item -> item.price * getAmount(item.id) }
        .sum()


    companion object {
        // (Serializes the order)
        fun serialize(order: Order): String {
            // Serialize amounts
            val amountsStrs = order.amounts
                .map { (itemId, amount) -> "${itemId}:${amount}" }

            // Return serialization
            return CSV.serialize(
                listOf(
                    order.id.toString(),
                    order.customerId.toString(),
                    CSV.serializeTimestamp(order.timestamp)
                ) + amountsStrs
            )
        }

        // (Deserializes the order)
        fun deserialize(str: String): Order {
            // Get properties
            val props = CSV.deserialize(str)
            val (idStr, customerIdStr, timestampStr) = props
            val amountsStrs = props.drop(3)

            // Deserialize properties
            val id = idStr.toInt()
            val customerId = customerIdStr.toInt()
            val timestamp = CSV.deserializeTimestamp(timestampStr)
            val amounts = amountsStrs.associate { amountsStr ->
                amountsStr.split(':').let { (itemId, amount) ->
                    itemId.toInt() to amount.toInt()
                }
            }.toMutableMap()

            // Return order
            return Order(id, customerId, timestamp, amounts)
        }
    }
}