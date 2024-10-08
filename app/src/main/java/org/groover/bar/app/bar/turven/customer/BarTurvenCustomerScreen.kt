package org.groover.bar.app.bar.turven.customer

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastMap
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.app.util.BarButton
import org.groover.bar.data.item.composable.ItemList
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout
import org.groover.bar.data.util.Cents

/**
 * The screen where an order can be made/changed for the specified customer.
 * If 'prevousOrder' is specified, this is the order that will be changed.
 */
@Composable
fun BarTurvenCustomerScreen(
    navigate: (route: String) -> Unit,
    customerRepository: CustomerRepository,
    itemRepository: ItemRepository,
    orderRepository: OrderRepository,
    customerId: Int,
    previousOrder: Order?,
) {
    val context = LocalContext.current

    // Look up current customer's name
    val currentCustomer = customerRepository.find(customerId)!!

    // Get customer total
    val items = itemRepository.data
    val customerTotal = orderRepository.getTotalByCustomer(customerId, customerRepository.groups.data, items)

    // Get warning message
    val warningMessage = currentCustomer.getWarningMessage { customerRepository.members.find(it)!! }

    // (Gets the cost of an order)
    val getOrderCost = { currentOrder: List<Int> ->
        itemRepository.costProduct(currentOrder)
    }

    // (Places an order)
    val placeOrder = { currentOrder: List<Int> ->
        orderRepository.placeOrder(currentOrder, customerId, items)

        if (previousOrder == null) {
            navigate("bar/turven")

            // Show toast
            Toast.makeText(context, "Bestelling geplaatst!", Toast.LENGTH_SHORT)
                .show()
        } else {
            orderRepository.remove(previousOrder.id)

            navigate("bar/geschiedenis")

            // Show toast
            Toast.makeText(context, "Bestelling aangepast!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // (Removes an order)
    val removeOrder = {
        orderRepository.remove(previousOrder!!.id)

        navigate("bar/geschiedenis")

        // Show toast
        Toast.makeText(context, "Bestelling verwijderd!", Toast.LENGTH_SHORT)
            .show()
    }

    // (Finishes an order)
    val finishOrder = { currentOrder: List<Int> ->
        // Check if the order needs to be removed
        val remove = currentOrder.sum() == 0 && previousOrder != null

        if (remove)
            removeOrder()
        else
            placeOrder(currentOrder)
    }

    // Content
    BarTurvenCustomerContent(
        items = items,
        previousOrder = previousOrder,
        isHospitality = currentCustomer.id == 0,
        customerName = currentCustomer.name,
        customerTotal = customerTotal,
        warningMessage = warningMessage,
        getOrderCost = getOrderCost,
        finishOrder = finishOrder,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BarTurvenCustomerContent(
    items: List<Item>,
    previousOrder: Order?,
    isHospitality: Boolean,
    customerName: String,
    customerTotal: Cents,
    warningMessage: String,
    getOrderCost: (currentOrder: List<Int>) -> Cents,
    finishOrder: (currentOrder: List<Int>) -> Unit,
) {
    // Initialize initial amounts (or zeroes if not specified)
    val currentOrder = remember {
        items.fastMap { item -> previousOrder?.amounts?.get(item.id) ?: 0 }
            .toMutableStateList()
    }

    // UI
    BarLayout {
        // Customer name
        Spacer(Modifier.size(20.dp))
        val hospitalityGradient = listOf(
            Color.Magenta,
            Color.Red,
            Color(0xFFFFD800),
        )
        BarTitle(customerName,
            modifier = Modifier.basicMarquee(velocity = 80.dp),
            style = if (isHospitality) TextStyle(
                brush = Brush.linearGradient(hospitalityGradient),
                shadow = Shadow(blurRadius = 8f)
            ) else TextStyle(),
        )

        // Member total
        Text("Voorlopige rekening: ${customerTotal.toStringWithEuro()}",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(10.dp))

        // Warning message
        if (warningMessage != "") {
            Text(warningMessage,
                color = Color.Red,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(10.dp))
        }

        // Items
        ItemList(items, currentOrder)
        Spacer(Modifier.size(10.dp))

        // Submit button
        val orderCost = getOrderCost(currentOrder)
        val hasItems = currentOrder.sum() != 0
        val newOrder = previousOrder == null

        // Finish order button
        BarButton(
            when {
                hasItems -> "Bestelling afronden (${orderCost.toStringWithEuro()})"
                !hasItems && !newOrder -> "Bestelling verwijderen"
                else -> "..."
            },
            onClick = { finishOrder(currentOrder) },
            enabled = hasItems || !newOrder,
            rounded = true,
        )
    }
}