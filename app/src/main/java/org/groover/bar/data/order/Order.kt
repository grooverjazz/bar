package org.groover.bar.data.order

import org.groover.bar.data.item.Item
import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import java.util.Date
import java.util.Locale

data class Order(
    override val id: Int,
    val customerId: Int, // (this can be a user ID or a group ID)
    val timestamp: Date,
    val amounts: List<Int>,
): BarData() {
    // Get the total price of the order
    fun getTotalPrice(items: List<Item>): Float = amounts
        .zip(items)
        .map { (amount, item) -> amount * item.priceWithBtw }
        .sum()

    private val locale = Locale("nl")

    fun getTotalPriceString(items: List<Item>): String = "€%.2f".format(locale, getTotalPrice(items))

    companion object {
        // (Serializes the order)
        fun serialize(order: Order): String {
            // Return serialization
            return CSV.serialize(
                listOf(
                    order.id.toString(),
                    order.customerId.toString(),
                    CSV.serializeDate(order.timestamp)
                ) + order.amounts.map(Int::toString)
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
            val amounts = amountsStr.map(String::toInt)

            // Return order
            return Order(id, customerId, timestamp, amounts)
        }
    }
}