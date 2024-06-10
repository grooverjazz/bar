package org.groover.bar.data.order

import org.groover.bar.data.item.Item
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository
import java.util.Date

class OrderRepository(
    fileOpener: FileOpener,
) : Repository<Order>(
    fileOpener,
    "orders.csv",
    Order.Companion::serialize,
    Order.Companion::deserialize,
    listOf("ID", "Customer ID", "Timestamp", "Amounts...")
) {
    fun placeOrder(amounts: List<Int>, customerId: Int, items: List<Item>) {
        val amountsMap = (items zip amounts).associate { (item, amount) ->
            item.id to amount
        }.toMutableMap()

        // Create the order
        val newOrder = Order(
            generateId(),
            customerId,
            Date(),
            amountsMap,
        )

        // Add the order
        data += newOrder

        // Save
        save()
    }

    fun removeOrder(orderId: Int) = removeById(orderId)

    fun getTotalByCustomer(customerId: Int, items: List<Item>): Cents {
        return data
            .filter { it.customerId == customerId }
            .map { it.getTotalPrice(items) }
            .fold(Cents(0), Cents::plus)
    }
}