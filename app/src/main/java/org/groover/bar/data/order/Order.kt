package org.groover.bar.data.order

import org.groover.bar.data.util.BarData
import org.groover.bar.data.util.CSVHandler
import java.util.Date

/**
 * An order that can be made by a customer.
 */
data class Order(
    override val id: Int,
    val customerId: Int, // (this can be a user ID or a group ID)
    val timestamp: Date,
    private val mapAmounts: Map<Int, Int>, // (maps every item to an amount)
): BarData() {
    // Safe map that defaults to 0
    data class Amounts(val map: Map<Int, Int>) {
        operator fun get(key: Int) = map.getOrDefault(key, 0)
        val itemIds = map.keys
    }
    val amounts = Amounts(mapAmounts)

    companion object {
        // (Serializes the order)
        fun serialize(order: Order): String {
            // Serialize amounts
            val amountsStrs = order.amounts.map
                .map { (itemId, amount) -> "${itemId}:${amount}" }

            // Return serialization
            return CSVHandler.serialize(
                listOf(
                    order.id.toString(),
                    order.customerId.toString(),
                    CSVHandler.serializeTimestamp(order.timestamp)
                ) + amountsStrs
            )
        }

        // (Deserializes the order)
        fun deserialize(str: String): Order {
            try {
                // Get properties
                val props = CSVHandler.deserialize(str)
                val (idStr, customerIdStr, timestampStr) = props
                val amountsStrs = props.drop(3)

                // Deserialize properties
                val id = idStr.toInt()
                val customerId = customerIdStr.toInt()
                val timestamp = CSVHandler.deserializeTimestamp(timestampStr)
                val amounts = amountsStrs.associate { amountsStr ->
                    amountsStr.split(':').let { (itemId, amount) ->
                        itemId.toInt() to amount.toInt()
                    }
                }.toMutableMap()

                // Return order
                return Order(id, customerId, timestamp, amounts)
            } catch (e: Exception) {
                throw IllegalStateException("Kan bestelling '$str' niet deserialiseren\n" +
                        "(normaal in de vorm 'id;customerId;timestamp;...amounts')")
            }
        }
    }
}