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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.app.ItemList
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BarTurvenCustomerScreen(
    navController: NavController,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
    itemRepository: ItemRepository,
    orderRepository: OrderRepository,
    customerId: Int,
    previousOrder: Order?,
) {
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        val backRoute = if (previousOrder == null) "bar/turven" else "bar/geschiedenis"
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = backRoute,
            height = 60.dp,
        )
        // Look up current member
        val currentCustomer = (memberRepository.lookupById(customerId)
            ?: groupRepository.lookupById(customerId))!!

        val name = when (currentCustomer) {
            is Member -> currentCustomer.fullName
            is Group -> currentCustomer.name
            else -> currentCustomer.toString()
        }

        // State of current order
        val items = itemRepository.data

        // Initialize (use zeroes if no amounts specified)
        val zeroes = List(items.size) { 0 }
        val initialAmounts = previousOrder?.amounts ?: zeroes
        val currentOrder = remember { mutableStateListOf(*initialAmounts.toTypedArray()) }

        // Member name
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = name,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Items
        ItemList(itemRepository, currentOrder)

        val context = LocalContext.current

        // Submit button
        Spacer(modifier = Modifier.size(20.dp))
        Button(
            modifier = Modifier.height(60.dp),
            onClick = {
                orderRepository.placeOrder(currentOrder, customerId)

                if (previousOrder == null) {
                    navController.navigate("bar/turven")

                    // Show toast
                    Toast.makeText(context, "Bestelling geplaatst!", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    orderRepository.removeOrder(previousOrder.id)

                    navController.navigate("bar/geschiedenis")

                    // Show toast
                    Toast.makeText(context, "Bestelling aangepast!", Toast.LENGTH_SHORT)
                        .show()
                }
            },
        ) {
            Text("Bestelling afronden",
                fontSize = 30.sp
            )
        }
    }
}