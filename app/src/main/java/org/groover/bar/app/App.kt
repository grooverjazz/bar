package org.groover.bar.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.groover.bar.app.bar.BarScreen
import org.groover.bar.app.bar.geschiedenis.BarGeschiedenisScreen
import org.groover.bar.app.bar.turven.BarTurvenScreen
import org.groover.bar.app.bar.turven.customer.BarTurvenCustomerScreen
import org.groover.bar.app.beheer.BeheerLoginScreen
import org.groover.bar.app.beheer.BeheerScreen
import org.groover.bar.app.beheer.customers.BeheerCustomersScreen
import org.groover.bar.app.beheer.customers.group.BeheerGroupScreen
import org.groover.bar.app.beheer.customers.member.BeheerMemberScreen
import org.groover.bar.app.beheer.error.BeheerErrorScreen
import org.groover.bar.app.beheer.items.BeheerItemsScreen
import org.groover.bar.app.beheer.items.item.BeheerItemsItemScreen
import org.groover.bar.app.beheer.password.BeheerPasswordScreen
import org.groover.bar.app.beheer.session.BeheerSessionScreen
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.customer.GroupRepository
import org.groover.bar.data.customer.MemberRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.export.ExportHandler
import org.groover.bar.export.OptionsHandler
import org.groover.bar.error.ErrorHandler
import org.groover.bar.data.util.FileOpener

@Composable
fun App() {
    // Get context
    val context = LocalContext.current

    // Initialize options
    val optionsHandler = tryOrCrash("OptionsHandler") {
        OptionsHandler(context)
    } ?: return

    // Initialize file opener
    val fileOpener = tryOrCrash("FileOpener") {
        FileOpener(context, optionsHandler.sessionName)
    } ?: return

    // Initialize repositories
    val itemRepository = tryOrCrash("ItemRepository") {
        ItemRepository(fileOpener)
    } ?: return
    val orderRepository = tryOrCrash("OrderRepository") {
        OrderRepository(fileOpener)
    } ?: return
    val customerRepository = tryOrCrash("CustomerRepository") {
        CustomerRepository(
            members = MemberRepository(fileOpener),
            groups = GroupRepository(fileOpener),
        )
    } ?: return

    // (Reloads the repositories)
    val reload: (String, Boolean) -> Unit = { newSessionName, copyGlobalData ->
        // Change session name
        optionsHandler.changeSession(newSessionName)
        fileOpener.relativePath = newSessionName

        // Don't open if copying over data from current session
        if (copyGlobalData) {
            customerRepository.save()
            itemRepository.save()
        }

        // Restart app
        (context as Activity).recreate()
    }

    // Initialize error handler
    val errorHandler = ErrorHandler(
        customerRepository = customerRepository,
        orderRepository = orderRepository,
        itemRepository = itemRepository,
    )

    // Initialize export handler
    val exportHandler = ExportHandler(
        fileOpener = fileOpener,
        optionsHandler = optionsHandler,
        customerRepository = customerRepository,
        itemRepository = itemRepository,
        orderRepository = orderRepository,
    )

    // Initialize navigation
    val navController = rememberNavController()
    val navigate: (String) -> Unit = { route -> navController.navigate(route) }
    @Suppress("LocalVariableName")
    val BackBehavior: @Composable (String) -> Unit = { route -> BackHandler { navigate(route) } }

    // UI
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        // Home
        composable("home") {
            BackHandler { (context as Activity).finish() }

            val hasErrors by remember { mutableStateOf(errorHandler.hasErrors) }

            HomeScreen(
                navigate = navigate,
                sessionName = optionsHandler.sessionName,
                hasErrors,
            )
        }

        // Bar
        composable("bar") {
            BackBehavior("home")

            BarScreen(
                navigate = navigate,
            )
        }

        // Bar: turven
        composable("bar/turven") {
            BackBehavior("bar")

            BarTurvenScreen(
                navigate = navigate,
                customerRepository = customerRepository,
            )
        }

        // Bar: turven: member
        composable("bar/turven/customer/{customerId}") { backStackEntry ->
            BackBehavior("bar/turven")

            // Extract the member ID from the route
            val customerIdStr = backStackEntry.arguments?.getString("customerId")!!
            val customerId = customerIdStr.toInt()

            BarTurvenCustomerScreen(
                navigate = navigate,
                customerRepository = customerRepository,
                itemRepository = itemRepository,
                orderRepository = orderRepository,
                customerId = customerId,
                previousOrder = null,
            )
        }

        // Bar: geschiedenis
        composable("bar/geschiedenis") {
            BackBehavior("bar")

            BarGeschiedenisScreen(
                navigate = navigate,
                orderRepository = orderRepository,
                customerRepository = customerRepository,
                itemRepository = itemRepository,
            )
        }

        // Bar: geschiedenis: aanpassing
        composable("bar/geschiedenis/edit/{orderId}") { backStackEntry ->
            BackBehavior("bar/geschiedenis")

            // Extract the previous order from the route
            val orderIdStr = backStackEntry.arguments?.getString("orderId")!!
            val orderId = orderIdStr.toInt()
            val previousOrder = orderRepository.find(orderId) ?: return@composable

            // Get the customer ID from the order
            val customerId = previousOrder.customerId

            BarTurvenCustomerScreen(
                navigate = navigate,
                customerRepository = customerRepository,
                itemRepository = itemRepository,
                orderRepository = orderRepository,
                customerId = customerId,
                previousOrder = previousOrder,
            )
        }

        // Beheer
        composable("beheer") {
            BackBehavior("home")

            val hasErrors by remember { mutableStateOf(errorHandler.hasErrors) }

            BeheerScreen(
                navigate = navigate,
                exportHandler = exportHandler,
                exportName = "Export ${optionsHandler.sessionName}",
                hasErrors = hasErrors,
            )
        }

        // Beheer
        composable("beheer/login") {
            BackBehavior("home")

            BeheerLoginScreen(
                navigate = navigate,
                correctPassword = optionsHandler.beheerPassword,
            )
        }

        // Beheer: items
        composable("beheer/items") {
            BackBehavior("beheer")

            BeheerItemsScreen(
                navigate = navigate,
                itemRepository = itemRepository,
            )
        }

        // Beheer: items: item
        composable("beheer/items/item/{itemId}") { backStackEntry ->
            BackBehavior("beheer/items")

            // Extract the item ID from the route
            val itemIdStr = backStackEntry.arguments?.getString("itemId")!!
            val itemId = itemIdStr.toInt()

            BeheerItemsItemScreen(
                navigate = navigate,
                itemRepository = itemRepository,
                itemId = itemId,
            )
        }

        // Beheer: customers
        composable("beheer/customers") {
            BackBehavior("beheer")

            BeheerCustomersScreen(
                navigate = navigate,
                customerRepository = customerRepository,
            )
        }

        // Beheer: customers: member
        composable("beheer/customers/member/{memberId}") { backStackEntry ->
            BackBehavior("beheer/customers")

            // Extract the member ID from the route
            val memberIdStr = backStackEntry.arguments?.getString("memberId")!!
            val memberId = memberIdStr.toInt()

            BeheerMemberScreen(
                navigate = navigate,
                customerRepository = customerRepository,
                memberId = memberId,
            )
        }

        // Beheer: customers: group
        composable("beheer/customers/group/{groupId}") { backStackEntry ->
            BackBehavior("beheer/customers")

            // Extract the member ID from the route
            val groupIdStr = backStackEntry.arguments?.getString("groupId")!!
            val groupId = groupIdStr.toInt()

            BeheerGroupScreen(
                navigate = navigate,
                customerRepository = customerRepository,
                groupId = groupId,
            )
        }

        // Beheer: session
        composable("beheer/session") {
            BackBehavior("beheer")

            BeheerSessionScreen(
                navigate = navigate,
                oldSessionName = optionsHandler.sessionName,
                allSessions = optionsHandler.getAllSessions(),
                finish = { newSessionName, copyGlobalData ->
                    reload(newSessionName, copyGlobalData)
                },
            )
        }

        // Beheer: session
        composable("beheer/password") {
            BackBehavior("beheer")

            BeheerPasswordScreen(
                navigate = navigate,
                finish = { newPassword ->
                    optionsHandler.beheerPassword = newPassword
                    optionsHandler.save()
                },
            )
        }

        // Beheer: errors
        composable("beheer/error") {
            BackBehavior("beheer")

            BeheerErrorScreen(
                navigate = navigate,
                errorHandler = errorHandler,
            )
        }
    }
}