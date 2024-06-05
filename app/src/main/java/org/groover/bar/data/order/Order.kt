package org.groover.bar.data.order

import org.groover.bar.data.item.Item
import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents
import java.util.Date

data class Order(
    override val id: Int,
    val customerId: Int, // (this can be a user ID or a group ID)
    val timestamp: Date,
    private val amounts: MutableMap<Int, Int>, // (maps every item to an amount)
): BarData() {
    // Get the total price of the order
    fun getTotalPrice(items: List<Item>): Cents =
        Cents(items.sumOf { item -> item.price.amount * getAmount(item.id) })

    fun getTotalPriceString(items: List<Item>): String = getTotalPrice(items).stringWithEuro

    fun getAmount(itemId: Int): Int = amounts.getOrElse(itemId) {
        amounts[itemId] = 0
        return 0
    }

    companion object {
        // (Serializes the order)
        fun serialize(order: Order): String {
            val amountsStrs = order.amounts
                .map { (itemId, amount) -> "${itemId}:${amount}" }

            // Return serialization
            return CSV.serialize(
                listOf(
                    order.id.toString(),
                    order.customerId.toString(),
                    CSV.serializeDate(order.timestamp)
                ) + amountsStrs
            )
        }

        // (Deserializes the order)
        fun deserialize(str: String): Order {
            // Extract from split string
            val data = CSV.deserialize(str)

            // Get id, customer ID and timestamp
            val (idStr, customerIdStr, timestampStr) = data
            val id = idStr.toInt()
            val customerId = customerIdStr.toInt()
            val timestamp = CSV.deserializeDate(timestampStr)

            // Get amounts
            val amountsStr = data.drop(3)
            val amounts = amountsStr.associate { s ->
                val (a, b) = s.split(":")

                a.toInt() to b.toInt()
            }.toMutableMap()

            // Return order
            return Order(id, customerId, timestamp, amounts)
        }
    }
}