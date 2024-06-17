package org.groover.bar.app.bar.turven.customer

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.app.ItemList
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.DateUtils

@Composable
fun BarTurvenCustomerScreen(
    navigate: (String) -> Unit,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
    itemRepository: ItemRepository,
    orderRepository: OrderRepository,
    customerId: Int,
    previousOrder: Order?,
) {
    val context = LocalContext.current

    // Look up current customer's name
    val currentCustomer = (memberRepository.lookupById(customerId)
        ?: groupRepository.lookupById(customerId))
        ?: throw Exception("Kan gebruiker niet vinden!")

    val customerName = when (currentCustomer) {
        is Member -> currentCustomer.fullName
        is Group -> currentCustomer.name
        else -> throw Exception()
    }

    val items = itemRepository.data

    val customerTotal = orderRepository.getTotalByCustomer(customerId, items)

    val minderjarigMessage = when (currentCustomer) {
        is Member -> if (!DateUtils.isOlderThan18(currentCustomer.verjaardag)) "Dit lid is minderjarig!" else ""
        is Group -> if (currentCustomer.memberIds.any {
                !DateUtils.isOlderThan18(memberRepository.lookupById(it)!!.verjaardag)
            }) "Deze groep bevat minderjarigen!" else ""
        else -> ""
    }

    val placeOrder = { currentOrder: List<Int> ->
        orderRepository.placeOrder(currentOrder, customerId, items)

        if (previousOrder == null) {
            navigate("bar/turven")

            // Show toast
            Toast
                .makeText(context, "Bestelling geplaatst!", Toast.LENGTH_SHORT)
                .show()
        } else {
            orderRepository.removeOrder(previousOrder.id)

            navigate("bar/geschiedenis")

            // Show toast
            Toast
                .makeText(context, "Bestelling aangepast!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    val deleteOrder = {
        orderRepository.removeOrder(previousOrder!!.id)

        navigate("bar/geschiedenis")

        // Show toast
        Toast
            .makeText(context, "Bestelling verwijderd!", Toast.LENGTH_SHORT)
            .show()
    }

    val finishOrder = { currentOrder: List<Int> ->
        // Check if the order needs to be deleted
        val delete = currentOrder.sum() == 0 && previousOrder != null

        if (delete)
            deleteOrder()
        else
            placeOrder(currentOrder)
    }

    val getOrderCost: (List<Int>) -> Cents = { amounts ->
        orderRepository.getOrderCost(amounts, items)
    }

    BarTurvenCustomerContent(
        navigate = navigate,
        items = items,
        previousOrder = previousOrder,
        customerName = customerName,
        customerTotal = customerTotal,
        minderjarigMessage = minderjarigMessage,
        getOrderCost = getOrderCost,
        finishOrder = finishOrder,
    )
}

@Composable
private fun BarTurvenCustomerContent(
    navigate: (String) -> Unit,
    items: List<Item>,
    previousOrder: Order?,
    customerName: String,
    customerTotal: Cents,
    minderjarigMessage: String,
    getOrderCost: (List<Int>) -> Cents,
    finishOrder: (List<Int>) -> Unit,
) {
    // Initialize (use zeroes if no amounts specified)
    val initialAmounts = items.map { item -> previousOrder?.getAmount(item.id) ?: 0 }
    val currentOrder = remember { mutableStateListOf(*initialAmounts.toTypedArray()) }

    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Member name
        Spacer(modifier = Modifier.size(20.dp))
        TitleText(customerName)

        // Member total
        Text(
            text = "Voorlopige rekening: ${customerTotal.stringWithEuro}",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.size(30.dp))

        if (minderjarigMessage != "") {
            Text(
                text = minderjarigMessage,
                color = Color.Red,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.size(30.dp))
        }

        // Items
        ItemList(items, currentOrder)

        Spacer(modifier = Modifier.size(20.dp))

        // Submit button
        val orderCost = getOrderCost(currentOrder)
        val hasItems = orderCost.amount != 0
        val newOrder = previousOrder == null

        Button(
            modifier = Modifier.height(60.dp),
            onClick = { finishOrder(currentOrder) },
            enabled = hasItems || !newOrder,
        ) {
            val text = when {
                hasItems -> "Bestelling afronden (${orderCost.stringWithEuro})"
                !hasItems && !newOrder -> "Bestelling verwijderen"
                else -> "..."
            }

            Text(text,
                fontSize = 30.sp
            )
        }
    }
}