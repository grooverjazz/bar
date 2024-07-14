package org.groover.bar.app.beheer.error

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.util.app.BigList
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.error.ErrorHandler

@Composable
fun BeheerErrorScreen(
    navigate: (route: String) -> Unit,
    errorHandler: ErrorHandler,
) {
    // UI
    VerticalGrid {
        // Title
        Spacer(Modifier.size(20.dp))
        TitleText("Errors")
        Spacer(Modifier.size(20.dp))

        // All inconsistencies
        BigList(spacing = 0.dp) {
            // Group.memberIds contains a non-existent Member ID or a Group ID.
            var groupMemberIdsInconsistencies by remember { mutableStateOf(errorHandler.groupMemberIdErrors()) }
            groupMemberIdsInconsistencies.forEach { it.Panel {
                groupMemberIdsInconsistencies = errorHandler.groupMemberIdErrors()
            }}

            // Order.amounts contains a non-existent Item ID as a key.
            var orderAmountsItemInconsistencies by remember { mutableStateOf(errorHandler.orderAmountsItemErrors()) }
            orderAmountsItemInconsistencies.forEach { it.Panel {
                orderAmountsItemInconsistencies = errorHandler.orderAmountsItemErrors()
            }}

            // Order.customerId is a non-existent Customer ID.
            var orderCustomerInconsistencies by remember { mutableStateOf(errorHandler.orderCustomerErrors()) }
            orderCustomerInconsistencies.forEach { it.Panel {
                orderCustomerInconsistencies = errorHandler.orderCustomerErrors()
            }}

            // There are 2 or more customers in the same group.
            var duplicateInGroupErrors by remember { mutableStateOf(errorHandler.duplicateInGroupErrors()) }
            duplicateInGroupErrors.forEach { it.Panel{
                duplicateInGroupErrors = errorHandler.duplicateInGroupErrors()
            }}

            // There are 2 or more {customers, items, orders} with the same ID.
            var duplicateCustomerInconsistencies by remember { mutableStateOf(errorHandler.duplicateCustomerErrors()) }
            duplicateCustomerInconsistencies.forEach { it.Panel {
                duplicateCustomerInconsistencies = errorHandler.duplicateCustomerErrors()
            }}
            var duplicateOrderInconsistencies by remember { mutableStateOf(errorHandler.duplicateOrderErrors()) }
            duplicateOrderInconsistencies.forEach { it.Panel {
                duplicateOrderInconsistencies = errorHandler.duplicateOrderErrors()
            }}
            var duplicateItemInconsistencies by remember { mutableStateOf(errorHandler.duplicateItemErrors()) }
            duplicateItemInconsistencies.forEach { it.Panel {
                duplicateItemInconsistencies = errorHandler.duplicateItemErrors()
            }}
        }
    }
}