package org.groover.bar.util.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import org.groover.bar.data.order.Order

@Composable
fun OrderList(
    orders: List<Order>,
    items: List<Item>,
    getCustomerName: (Int) -> String,
    onClick: (Order) -> Unit,
) {
    // UI
    LazyColumn(
        modifier = Modifier
            .padding(10.dp)
            .background(Color.LightGray)
            .height(900.dp)
    ) {
        orders.forEach { order ->
            item {
                // Get printable name of customer
                val customerName = getCustomerName(order.customerId)

                val totalPrice = order.getTotalPriceString(items)

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RectangleShape,
                    onClick = { onClick(order) }
                ) {
                    Text("$customerName ($totalPrice)",
                        fontSize = 25.sp
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))
            }
        }
    }
}