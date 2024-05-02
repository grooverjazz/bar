package org.groover.bar.util.app

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository

@Composable
fun OrderList(
    orders: List<Order>,
    items: List<Item>,
    getCustomerName: (Int) -> String,
    onClick: (Order) -> Unit,
) {
    // UI
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.padding(10.dp)
    ) {
        orders.forEach { order ->
            item {
                // Get printable name of customer
                val customerName = getCustomerName(order.customerId)

                val totalPrice = order.getTotalPriceString(items)

                Button(
                    shape = RectangleShape,
                    onClick = { onClick(order) }
                ) { Text("$customerName ($totalPrice)") }
            }
        }
    }
}