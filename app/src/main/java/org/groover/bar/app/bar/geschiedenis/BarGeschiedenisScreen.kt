package org.groover.bar.app.bar.geschiedenis

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.app.OrderList
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BarGeschiedenisScreen(
    navigate: (String) -> Unit,
    customerRepository: CustomerRepository,
    orderRepository: OrderRepository,
    itemRepository: ItemRepository,
) {
    // (Retrieves the name of a customer)
    val getCustomerName = { id: Int ->
        customerRepository.find(id)?.name ?:
        "Naam van ID $id niet gevonden!"
    }

    // (Navigates to editing the order)
    val orderOnClick = { order: Order ->
        val orderStr = Order.serialize(order)
        navigate("bar/geschiedenis/edit/${orderStr}")
    }

    // Content
    BarGeschiedenisContent(
        navigate = navigate,
        orders = orderRepository.data,
        items = itemRepository.data,
        getCustomerName = getCustomerName,
        orderOnClick = orderOnClick
    )
}


@Composable
private fun BarGeschiedenisContent(
    navigate: (String) -> Unit,
    orders: List<Order>,
    items: List<Item>,
    getCustomerName: (Int) -> String,
    orderOnClick: (Order) -> Unit,
) {
    VerticalGrid {
        // Title
        Spacer(Modifier.size(20.dp))
        TitleText("Geschiedenis")
        Spacer(Modifier.size(20.dp))

        // Orders
        OrderList(
            orders = orders,
            items = items,
            getCustomerName = getCustomerName,
            onClick = orderOnClick,
        )
    }
}