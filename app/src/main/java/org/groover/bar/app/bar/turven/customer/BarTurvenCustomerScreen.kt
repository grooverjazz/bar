package org.groover.bar.app.bar.turven.customer

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.app.ItemList
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.Cents

@Composable
fun BarTurvenCustomerScreen(
    navigate: (String) -> Unit,
    customerRepository: CustomerRepository,
    itemRepository: ItemRepository,
    orderRepository: OrderRepository,
    customerId: Int,
    previousOrder: Order?,
) {
    val context = LocalContext.current

    // Look up current customer's name
    val currentCustomer = customerRepository.find(customerId)
        ?: throw Exception("Kan gebruiker niet vinden!")

    // Get customer total
    val items = itemRepository.data
    val customerTotal = orderRepository.getTotalByCustomer(customerId, customerRepository.groups.data, items)

    // Get warning message
    val warningMessage = currentCustomer.getWarningMessage(customerRepository.members::find)

    // (Gets the cost of an order)
    val getOrderCost = { amounts: List<Int> ->
        itemRepository.costProduct(amounts)
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
        navigate = navigate,
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
    navigate: (String) -> Unit,
    items: List<Item>,
    previousOrder: Order?,
    isHospitality: Boolean,
    customerName: String,
    customerTotal: Cents,
    warningMessage: String,
    getOrderCost: (List<Int>) -> Cents,
    finishOrder: (List<Int>) -> Unit,
) {
    // Initialize initial amounts (or zeroes if not specified)
    val currentOrder = remember {
        items.map { item -> previousOrder?.getAmount(item.id) ?: 0 }
            .toMutableStateList()
    }

    VerticalGrid {
        // Customer name
        Spacer(Modifier.size(20.dp))
        val hospitalityGradient = listOf(
            Color.Magenta,
            Color.Red,
            Color(0xFFFFD800),
        )
        TitleText(customerName,
            modifier = Modifier.basicMarquee(velocity = 80.dp),
            style = if (isHospitality) TextStyle(
                brush = Brush.linearGradient(hospitalityGradient),
                shadow = Shadow(blurRadius = 8f)
            ) else TextStyle()
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
        val hasItems = orderCost.amount != 0
        val newOrder = previousOrder == null

        // Finish order button
        Button(
            modifier = Modifier.height(60.dp),
            onClick = { finishOrder(currentOrder) },
            enabled = hasItems || !newOrder,
        ) {
            val text = when {
                hasItems -> "Bestelling afronden (${orderCost.toStringWithEuro()})"
                !hasItems && !newOrder -> "Bestelling verwijderen"
                else -> "..."
            }

            Text(text,
                fontSize = 30.sp
            )
        }
    }
}