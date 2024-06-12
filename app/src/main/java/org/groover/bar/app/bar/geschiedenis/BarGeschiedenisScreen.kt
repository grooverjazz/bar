package org.groover.bar.app.bar.geschiedenis

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.OrderList
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BarGeschiedenisScreen(
    navigate: (String) -> Unit,
    orderRepository: OrderRepository,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
    itemRepository: ItemRepository,
) {
    val getCustomerName = { id: Int ->
        (memberRepository.lookupById(id)?.fullName ?:
        groupRepository.lookupById(id)?.name) ?:
        "Naam van ID $id niet gevonden!"
    }

    val orderOnClick = { order: Order ->
        val orderStr = Order.serialize(order)

        navigate("bar/geschiedenis/edit/${orderStr}")
    }

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
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "bar",
        )

        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Geschiedenis")
        Spacer(modifier = Modifier.size(20.dp))

        OrderList(
            orders = orders,
            items = items,
            getCustomerName = getCustomerName,
            onClick = orderOnClick,
        )
    }
}