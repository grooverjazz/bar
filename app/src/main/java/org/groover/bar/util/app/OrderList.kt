package org.groover.bar.util.app

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
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
import org.groover.bar.data.order.Order
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.SearchHandler
import java.util.Locale

@Composable
fun OrderList(
    orders: List<Order>,
    customerGetName: (customerId: Int) -> String,
    orderGetTotal: (order: Order) -> Cents,
    onClick: (order: Order) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val formattedSearchText = searchText.lowercase(Locale.ROOT)
    val filteredOrders = SearchHandler
        .search(formattedSearchText, orders) { customerGetName(it.customerId) }

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
        modifier = Modifier
            .focusRequester(keyboardFocus)
            .height(80.dp),
        textStyle = TextStyle.Default.copy(fontSize = 28.sp),
    )

    // UI
    LazyBigList {
        items(filteredOrders) { order ->
            // Get printable name of customer
            val customerName = customerGetName(order.customerId)

            // Get total price
            val totalPrice = orderGetTotal(order)

            Button({ onClick(order) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RectangleShape,
            ) {
                Text("$customerName (${totalPrice.toStringWithEuro()})",
                    fontSize = 25.sp
                )
            }
        }
    }
}