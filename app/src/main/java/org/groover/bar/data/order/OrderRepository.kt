package org.groover.bar.data.order

import android.content.Context
import org.groover.bar.util.data.Repository
import java.util.Date

class OrderRepository(
    context: Context
) : Repository<Order>(
    context,
    "orders.csv",
    Order.Companion::serialize,
    Order.Companion::deserialize,
    listOf("ID", "Customer ID", "Timestamp", "Amounts...")
) {
    fun placeOrder(amounts: List<Int>, customerId: Int) {
        // Create the order
        val newOrder = Order(
            generateId(),
            customerId,
            Date(),
            amounts,
        )

        // Add the order
        data += newOrder

        // Save
        save()
    }

    fun removeOrder(orderId: Int) = removeById(orderId)
}