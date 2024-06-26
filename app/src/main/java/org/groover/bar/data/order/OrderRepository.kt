package org.groover.bar.data.order

import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
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

    fun getTotalByCustomer(customerId: Int, groups: List<Group>, items: List<Item>): Cents {
        val userTotal = data
            .filter { it.customerId == customerId}
            .map { it.getTotalPrice(items) }
            .sum()

        val groupTotal = groups
            .filter { it.memberIds.contains(customerId) }
            .map { group ->
                data
                    .filter { it.customerId == group.id }
                    .map { it.getTotalPrice(items) }
                    .sum() / group.memberIds.size
            }
            .sum()

        return userTotal + groupTotal
    }

    fun getOrderCost(amounts: List<Int>, items: List<Item>): Cents {
        return (items zip amounts)
            .map { (item, amount) -> item.price * amount }
            .sum()
    }
}