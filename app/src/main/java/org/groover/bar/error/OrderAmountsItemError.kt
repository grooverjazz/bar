package org.groover.bar.error

import androidx.compose.runtime.Composable
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.removeFirst

// Order.amounts contains a non-existent Item ID as a key.
data class OrderAmountsItemError(
    val orderRepository: OrderRepository,
    val itemRepository: ItemRepository,
    val orderId: Int,
    val itemId: Int
) : Error() {
    override fun toString(): String {
        return "Bestelling '$orderId' bevat itemnummer '$itemId' die niet bestaat."
    }

    @Composable
    override fun Panel(callback: () -> Unit) = Panel(callback,
        "Haal item $itemId uit bestelling" to ::solveByRemove,
        "Vervang $itemId overal met tijdelijk item" to ::solveByTempItem,
    )

    // (Removes itemId from order)
    private fun solveByRemove() {
        // Get order
        val order = orderRepository.find(orderId)!!

        // Filter itemId from amounts
        val newAmounts = order.getAmountsItems()
            .removeFirst { it == itemId }
            .associateWith { order.getAmount(it) }
            .toMutableMap()

        // Replace with new order
        val newOrder = order.copy(amounts = newAmounts)
        orderRepository.replace(order.id, newOrder)
    }

    // (Replaces itemId with temporary item in all occurrences)
    private fun solveByTempItem() {
        val tempItem = itemRepository.addItem(
            "Het spook van item $itemId",
            Cents(0),
            0,
            0f,
            errorHandlingOverrideId = itemId
        )

        // Check all orders for itemId
        for (order in orderRepository.data) {
            val amountsItems = order.getAmountsItems()

            if (!amountsItems.contains(itemId)) continue

            // Filter itemId from amounts
            val newAmounts = amountsItems
                .removeFirst { it == itemId }
                .associateWith { order.getAmount(it) }
                .toMutableMap()

            newAmounts[tempItem.id] = order.getAmount(itemId)

            // Replace with new order
            val newOrder = order.copy(amounts = newAmounts)
            orderRepository.replace(order.id, newOrder)
        }
    }
}