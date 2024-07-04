package org.groover.bar.data.order

import org.groover.bar.data.customer.Group
import org.groover.bar.data.item.Item
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.sum
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
    init {
        open()
    }

    // (Places an order)
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

        // Prepend
        addToStart(newOrder)
    }

    // (Gets the total order price for a customer)
    fun getTotalByCustomer(customerId: Int, groups: List<Group>, items: List<Item>): Cents {
        // Get the total for the customer
        val total = data
            .filter { it.customerId == customerId}
            .map { it.getTotalPrice(items) }
            .sum()

        // Get the total for all groups that they are in
        //  (NOTE: Members only)
        val groupTotal = groups
            .filter { it.memberIds.contains(customerId) }
            .map { group ->
                data
                    .filter { it.customerId == group.id }
                    .map { it.getTotalPrice(items) }
                    .sum() / group.memberIds.size
            }
            .sum()

        return total + groupTotal
    }
}