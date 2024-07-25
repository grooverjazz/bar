package org.groover.bar.app.bar.turven

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.data.customer.Customer
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.customer.Group
import org.groover.bar.data.customer.Member
import org.groover.bar.data.customer.composable.CustomerList
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout

/**
 * The screen where a customer can be selected for placing an order.
 */
@Composable
fun BarTurvenScreen(
    navigate: (route: String) -> Unit,
    customerRepository: CustomerRepository,
) {
    // (Navigates to the turven screen)
    val customerOnClick = { customer: Customer ->
        navigate("bar/turven/customer/${customer.id}")
    }

    // Content
    BarTurvenContent(
        members = customerRepository.members.data,
        groups = customerRepository.groups.data,
        customerOnClick = customerOnClick,
    )
}

@Composable
private fun BarTurvenContent(
    members: List<Member>,
    groups: List<Group>,
    customerOnClick: (customer: Customer) -> Unit,
) {
    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(20.dp))
        BarTitle("Turven")
        Spacer(Modifier.size(20.dp))

        // Customer list
        CustomerList(
            members = members,
            groups = groups,
            customerOnClick = customerOnClick,
            customerOnMove = null,
            customerOnRemove = null,
            addExtraMember = null,
            addGroup = null,
        )
    }
}