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
import org.groover.bar.app.beheer.customers.BeheerCustomersScreen
import org.groover.bar.app.beheer.customers.group.BeheerGroupScreen
import org.groover.bar.app.beheer.customers.member.BeheerMemberScreen
import org.groover.bar.app.beheer.items.BeheerItemsScreen
import org.groover.bar.app.beheer.items.item.BeheerItemsItemScreen
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.export.BTWHandler
import org.groover.bar.export.ExportHandler

@Composable
fun App() {
    val context = LocalContext.current

    val memberRepository = MemberRepository(context)
    val groupRepository = GroupRepository(context)
    val itemRepository = ItemRepository(context)
    val orderRepository = OrderRepository(context)

    val exportHandler = ExportHandler(
        context = context,
        memberRepository = memberRepository,
        groupRepository = groupRepository,
        itemRepository = itemRepository,
        orderRepository = orderRepository,
    )

    val btwHandler = BTWHandler(
        context = context,
        orderRepository = orderRepository,
        itemRepository = itemRepository,
    )

    val navController = rememberNavController()
    val navigate: (String) -> Unit = { navController.navigate(it) }

    NavHost(
        navController = navController,
        startDestination = "Home",
    ) {
        // Home
        composable("home") {
            HomeScreen(
                navigate = navigate,
            )
        }

        // Bar
        composable("bar") {
            BarScreen(
                navigate = navigate,
            )
        }

        // Bar: turven
        composable("bar/turven") {
            BarTurvenScreen(
                navigate = navigate,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
            )
        }

        // Bar: turven: member
        composable("bar/turven/customer/{customerId}") { backStackEntry ->
            // Extract the member ID from the route
            val customerIdStr = backStackEntry.arguments?.getString("customerId") ?: throw Exception("Kan klant niet vinden in route!")
            val customerId = customerIdStr.toInt()

            BarTurvenCustomerScreen(
                navigate = navigate,
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
                navigate = navigate,
                orderRepository = orderRepository,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
                itemRepository = itemRepository,
            )
        }

        // Bar: geschiedenis: aanpassing
        composable("bar/geschiedenis/edit/{previousOrder}") { backStackEntry ->
            // Extract the previous order from the route
            val previousOrderStr = backStackEntry.arguments?.getString("previousOrder") ?: throw Exception("Kan vorige order niet vinden in route!")
            val previousOrder = Order.deserialize(previousOrderStr)

            // Get the customer ID from the order
            val customerId = previousOrder.customerId

            BarTurvenCustomerScreen(
                navigate = navigate,
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
                navigate = navigate,
                exportHandler = exportHandler,
                btwHandler = btwHandler,
            )
        }

        // Beheer: items
        composable("beheer/items") {
            BeheerItemsScreen(
                navigate = navigate,
                itemRepository = itemRepository,
            )
        }

        // Beheer: items: item
        composable("beheer/items/item/{itemId}") { backStackEntry ->
            // Extract the item ID from the route
            val itemIdStr = backStackEntry.arguments?.getString("itemId") ?: throw Exception("Kan item niet vinden in route!")
            val itemId = itemIdStr.toInt()

            BeheerItemsItemScreen(
                navigate = navigate,
                itemRepository = itemRepository,
                itemId = itemId,
            )
        }

        // Beheer: customers
        composable("beheer/customers") {
            BeheerCustomersScreen(
                navigate = navigate,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
            )
        }

        // Beheer: customers: member
        composable("beheer/customers/member/{memberId}") { backStackEntry ->
            // Extract the member ID from the route
            val memberIdStr = backStackEntry.arguments?.getString("memberId") ?: throw Exception("Kan lid niet vinden in route!")
            val memberId = memberIdStr.toInt()

            BeheerMemberScreen(
                navigate = navigate,
                memberRepository = memberRepository,
                memberId = memberId,
            )
        }

        // Beheer: customers: group
        composable("beheer/customers/group/{groupId}") { backStackEntry ->
            // Extract the member ID from the route
            val groupIdStr = backStackEntry.arguments?.getString("groupId") ?: throw Exception("Kan groep niet vinden in route!")
            val groupId = groupIdStr.toInt()

            BeheerGroupScreen(
                navigate = navigate,
                memberRepository = memberRepository,
                groupRepository = groupRepository,
                groupId = groupId,
            )
        }
    }
}