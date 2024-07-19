package org.groover.bar.error

import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.data.util.BarData


class ErrorHandler(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val itemRepository: ItemRepository,
) {
    val hasErrors =
        groupMemberIdErrors().isNotEmpty() ||
        orderAmountsItemErrors().isNotEmpty() ||
        orderCustomerErrors().isNotEmpty() ||
        duplicateInGroupErrors().isNotEmpty() ||
        duplicateCustomerErrors().isNotEmpty() ||
        duplicateOrderErrors().isNotEmpty() ||
        duplicateItemErrors().isNotEmpty()

    // Group.memberIds contains a non-existent Member ID or a Group ID.
    fun groupMemberIdErrors(): List<GroupMemberIdError> {
        val groups = customerRepository.groups.data
        val res = emptyList<GroupMemberIdError>().toMutableList()

        for (group in groups) {
            for (memberId in group.memberIds) {
                if (customerRepository.members.find(memberId) == null) {
                    res += GroupMemberIdError(
                        customerRepository,
                        group.id,
                        memberId
                    )
                }
            }
        }

        return res
    }

    // Order.amounts contains a non-existent Item ID as a key.
    fun orderAmountsItemErrors(): List<OrderAmountsItemError> {
        val orders = orderRepository.data
        val res = emptyList<OrderAmountsItemError>().toMutableList()

        for (order in orders) {
            for (itemId in order.amounts.itemIds) {
                if (itemRepository.find(itemId) == null) {
                    res += OrderAmountsItemError(
                        orderRepository,
                        itemRepository,
                        order.id,
                        itemId
                    )
                }
            }
        }

        return res
    }

    // Order.customerId is a non-existent Customer ID.
    fun orderCustomerErrors(): List<OrderCustomerError> {
        val orders = orderRepository.data
        val res = emptyList<OrderCustomerError>().toMutableList()

        for (order in orders) {
            val customerId = order.customerId
            if (customerRepository.find(customerId) == null) {
                res += OrderCustomerError(
                    orderRepository,
                    customerRepository,
                    order.id,
                    customerId
                )
            }
        }

        return res
    }

    // There are 2 or more customers in the same group.
    fun duplicateInGroupErrors(): List<DuplicateInGroupError> {
        val groups = customerRepository.groups.data
        val res = emptyList<DuplicateInGroupError>().toMutableList()

        for (group in groups) {
            val memberIds = group.memberIds

            val found = emptySet<Int>().toMutableSet()
            for (id in memberIds) {
                if (found.contains(id)) {
                    res += DuplicateInGroupError(
                        customerRepository,
                        group.id,
                        id
                    )
                }

                found += id
            }
        }

        return res
    }

    // There are 2 or more {customers, items, orders} with the same ID.
    fun duplicateCustomerErrors() = duplicateErrors(customerRepository.data) { index1: Int, index2: Int ->
        DuplicateCustomerError(customerRepository, index1, index2)
    }
    fun duplicateOrderErrors() = duplicateErrors(orderRepository.data) { index1: Int, index2: Int ->
        DuplicateOrderError(orderRepository, index1, index2)
    }
    fun duplicateItemErrors() = duplicateErrors(itemRepository.data) { index1: Int, index2: Int ->
        DuplicateItemError(itemRepository, index1, index2)
    }

    private fun duplicateErrors(
        elements: List<BarData>,
        construct: (index1: Int, index2: Int) -> DuplicateError
    ): List<DuplicateError> {
        val res = emptyList<DuplicateError>().toMutableList()
        val tempMap = emptyMap<Int, Int>().toMutableMap()

        elements.forEachIndexed { index, element ->
            val id = element.id

            // If the map already contains the ID,
            //   create an error between the stored index and the current index
            tempMap[id]?.let { res += construct(it, index) }

            tempMap[id] = index
        }

        return res
    }
}