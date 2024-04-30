package org.groover.bar.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.groover.bar.app.bar.BarScreen
import org.groover.bar.app.bar.geschiedenis.BarGeschiedenisScreen
import org.groover.bar.app.bar.turven.BarTurvenScreen
import org.groover.bar.app.bar.turven.customer.BarTurvenCustomerScreen
import org.groover.bar.app.beheer.BeheerScreen
import org.groover.bar.app.beheer.member.BeheerMemberScreen
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository

@Composable
fun App() {
    val context = LocalContext.current

    val memberRepository = MemberRepository(context)
    val groupRepository = GroupRepository(context)
    val itemRepository = ItemRepository(context)
    val orderRepository = OrderRepository(context)

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Home",
    ) {
        // Home
        composable("home") {
            HomeScreen(
                navController = navController,
            )
        }

        // Bar
        composable("bar") {
            BarScreen(
                navController = navController,
            )
        }

        // Bar: turven
        composable("bar/turven") {
            BarTurvenScreen(
                navController = navController,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
            )
        }

        // Bar: turven: member
        composable("bar/turven/customer/{customerId}") { backStackEntry ->
            // Extract the member ID from the route
            val customerIdStr = backStackEntry.arguments?.getString("customerId")!!
            val customerId = customerIdStr.toInt()

            BarTurvenCustomerScreen(
                navController = navController,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
                itemRepository = itemRepository,
                orderRepository = orderRepository,
                customerId = customerId,
                previousOrder = null,
            )
        }

        // Bar: geschiedenis
        composable("bar/geschiedenis") {
            BarGeschiedenisScreen(
                navController = navController,
                orderRepository = orderRepository,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
                itemRepository = itemRepository,
            )
        }

        // Bar: geschiedenis: aanpassing
        composable("bar/geschiedenis/edit/{previousOrder}") { backStackEntry ->
            // Extract the previous order from the route
            val previousOrderStr = backStackEntry.arguments?.getString("previousOrder")!!
            val previousOrder = Order.deserialize(previousOrderStr)

            // Get the customer ID from the order
            val customerId = previousOrder.customerId

            BarTurvenCustomerScreen(
                navController = navController,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
                itemRepository = itemRepository,
                orderRepository = orderRepository,
                customerId = customerId,
                previousOrder = previousOrder,
            )
        }


        // Beheer
        composable("beheer") {
            BeheerScreen(
                navController = navController,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
            )
        }

        // Beheer: member
        composable("beheer/member/{memberId}") { backStackEntry ->
            // Extract the member ID from the route
            val memberIdStr = backStackEntry.arguments?.getString("memberId")!!
            val memberId = memberIdStr.toInt()

            BeheerMemberScreen(
                navController = navController,
                memberRepository = memberRepository,
                memberId = memberId,
            )
        }
    }
}