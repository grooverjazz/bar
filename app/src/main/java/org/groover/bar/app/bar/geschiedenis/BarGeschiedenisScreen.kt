package org.groover.bar.app.bar.geschiedenis

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.data.order.composable.OrderList
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout
import org.groover.bar.data.util.Cents

/**
 * The screen that shows the history of all orders in the session.
 */
@Composable
fun BarGeschiedenisScreen(
    navigate: (route: String) -> Unit,
    customerRepository: CustomerRepository,
    orderRepository: OrderRepository,
    itemRepository: ItemRepository,
) {
    // (Retrieves the name of a customer)
    val getCustomerName = { id: Int ->
        customerRepository.find(id)!!.name
    }

    // (Gets the total price of an order)
    val items = itemRepository.data
    val orderGetTotal = { order: Order ->
        orderRepository.getOrderTotal(order, items)
    }

    // (Navigates to editing the order)
    val orderOnClick = { order: Order ->
        navigate("bar/geschiedenis/edit/${order.id}")
    }

    // Content
    BarGeschiedenisContent(
        orders = orderRepository.data,
        getCustomerName = getCustomerName,
        orderGetTotal = orderGetTotal,
        orderOnClick = orderOnClick,
    )
}

@Composable
private fun BarGeschiedenisContent(
    orders: List<Order>,
    getCustomerName: (customerId: Int) -> String,
    orderGetTotal: (order: Order) -> Cents,
    orderOnClick: (order: Order) -> Unit,
) {
    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(20.dp))
        BarTitle("Geschiedenis")
        Spacer(Modifier.size(20.dp))

        // Orders
        OrderList(
            orders = orders,
            customerGetName = getCustomerName,
            orderGetTotal = orderGetTotal,
            onClick = orderOnClick,
        )
    }
}