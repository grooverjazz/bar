package org.groover.bar.util.app

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.android.awaitFrame
import org.groover.bar.data.item.Item
import org.groover.bar.data.order.Order
import org.groover.bar.util.data.SearchHandler
import java.util.Locale

@Composable
fun OrderList(
    orders: List<Order>,
    items: List<Item>,
    getCustomerName: (Int) -> String,
    onClick: (Order) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val formattedSearchText = searchText.lowercase(Locale.ROOT)
    val filteredOrders = SearchHandler
        .search(formattedSearchText, orders) { getCustomerName(it.customerId) }

    // Keyboard focus for search box
    val keyboardFocus = remember { FocusRequester() }
    LaunchedEffect(keyboardFocus) {
        awaitFrame()
        keyboardFocus.requestFocus()
    }

    // Search box
    TextField(
        value = searchText,
        onValueChange = { newSearchText: String ->
            searchText = newSearchText
        },
        modifier = Modifier.focusRequester(keyboardFocus).height(80.dp),
        textStyle = TextStyle.Default.copy(fontSize = 28.sp)
    )

    // UI
    LazyBigList {
        filteredOrders.forEach { order ->
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
            }
        }
    }
}