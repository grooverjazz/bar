package org.groover.bar.error

import androidx.compose.runtime.Composable
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.customer.Group
import org.groover.bar.data.customer.Member
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository

abstract class DuplicateError(
    private val elementName: String,
    internal val id: Int,
) : BarError() {
    internal abstract val index1: Int
    internal abstract val index2: Int

    override fun toString(): String =
        "Er zijn 2 $elementName met hetzelfde ID (${id} bij index $index1 en $index2)"

    @Composable
    override fun Panel(callback: () -> Unit) = Panel(callback,
        "Kopieer de eerste" to { solve(index1) },
        "Kopieer de tweede" to { solve(index2) },
    )

    // (ABSTRACT: Changes the ID of the first occurrence)
    abstract fun solve(index: Int)
}

// (Duplicate member error)
data class DuplicateCustomerError(
    private val customerRepository: CustomerRepository,
    override val index1: Int,
    override val index2: Int,
): DuplicateError("leden", customerRepository.data[index1].id) {
    // (OVERRIDE: Changes the ID of the first occurrence)
    override fun solve(index: Int) {
        val oldCustomer = customerRepository.data[index]
        customerRepository.remove(id)
        when (oldCustomer) {
            is Member -> customerRepository.members.addToStart(oldCustomer.copy(id = customerRepository.generateMemberId()))
            is Group -> customerRepository.groups.addToStart(oldCustomer.copy(id = customerRepository.generateGroupId()))
        }
    }
}

// (Duplicate item error)
data class DuplicateItemError(
    private val itemRepository: ItemRepository,
    override val index1: Int,
    override val index2: Int,
): DuplicateError("items", itemRepository.data[index1].id) {
    // (OVERRIDE: Changes the ID of the first occurrence)
    override fun solve(index: Int) {
        val oldItem = itemRepository.data[index]
        itemRepository.remove(oldItem.id)
        itemRepository.addToStart(oldItem.copy(id = itemRepository.generateId()))
    }
}

// (Duplicate order error)
data class DuplicateOrderError(
    private val orderRepository: OrderRepository,
    override val index1: Int,
    override val index2: Int,
): DuplicateError("bestellingen", orderRepository.data[index1].id) {
    // (OVERRIDE: Changes the ID of the first occurrence)
    override fun solve(index: Int) {
        val oldOrder = orderRepository.data[index]
        orderRepository.remove(oldOrder.id)
        orderRepository.addToStart(oldOrder.copy(id = orderRepository.generateId()))
    }
}