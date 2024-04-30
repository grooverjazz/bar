package org.groover.bar.app.bar.geschiedenis

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.groover.bar.data.group.GroupRepository
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
    navController: NavController,
    orderRepository: OrderRepository,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
    itemRepository: ItemRepository,
) {
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "bar",
            height = 60.dp,
        )

        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Geschiedenis")
        Spacer(modifier = Modifier.size(20.dp))

        OrderList(
            orderRepository = orderRepository,
            memberRepository = memberRepository,
            groupRepository = groupRepository,
            itemRepository = itemRepository,
            onClick = { order ->
                val orderStr = Order.serialize(order)

                navController.navigate("bar/geschiedenis/edit/${orderStr}")
            }
        )
    }
}