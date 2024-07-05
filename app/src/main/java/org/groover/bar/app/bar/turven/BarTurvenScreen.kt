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
import org.groover.bar.util.app.CustomerList
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BarTurvenScreen(
    navigate: (String) -> Unit,
    customerRepository: CustomerRepository,
) {
    val customerOnClick = { customer: Customer ->
        navigate("bar/turven/customer/${customer.id}")
    }

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
    VerticalGrid {
        Spacer(Modifier.size(20.dp))
        TitleText("Turven")
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