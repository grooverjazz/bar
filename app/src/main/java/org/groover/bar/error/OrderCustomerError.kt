package org.groover.bar.error

import androidx.compose.runtime.Composable
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.order.OrderRepository

/**
 * Error where Order.customerId refers to a non-existent customer.
 */
data class OrderCustomerError(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val orderId: Int,
    private val customerId: Int
) : BarError() {
    // (Turns the error into a String)
    override fun toString(): String {
        return "Bestelling '$orderId' bevat klant '$customerId' die niet bestaat."
    }

    // (OVERRIDE: Makes a panel for the error screen)
    @Composable
    override fun Panel(callback: () -> Unit) = Panel(callback,
        "Verwijder bestelling" to ::solveByRemove,
        "Vervang $customerId overal met tijdelijk lid" to ::solveByExtraMember,
    )

    // (Removes the order)
    private fun solveByRemove() {
        orderRepository.remove(orderId)
    }

    // (Creates an extra member)
    private fun solveByExtraMember() {
        val extraMember = customerRepository.addExtraMember(
            "Het spook van klant $customerId",
            errorHandlingOverrideId = customerId
        )

        for (order in orderRepository.data) {
            if (order.customerId == customerId) {
                val newOrder = order.copy(customerId = extraMember.id)
                orderRepository.replace(order.id, newOrder)
            }
        }
    }
}