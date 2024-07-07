package org.groover.bar.app.beheer.customers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.data.customer.Customer
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.customer.Group
import org.groover.bar.data.customer.Member
import org.groover.bar.util.app.CustomerList
import org.groover.bar.util.app.PopupDialog
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerCustomersScreen(
    navigate: (route: String) -> Unit,
    customerRepository: CustomerRepository,
) {
    // (Selects a customer for editing)
    val customerOnClick = { customer: Customer ->
        when (customer) {
            is Member -> navigate("beheer/customers/member/${customer.id}")
            else -> navigate("beheer/customers/group/${customer.id}")
        }
    }

    // Remove popup variables
    var customerRemoveState: Customer? by remember { mutableStateOf(null) }
    val customerOnRemove = { id: Int ->
        customerRemoveState = customerRepository.find(id)
    }

    // Remove popup
    if (customerRemoveState != null) {
        // Get description of customer
        val desc: String = when (customerRemoveState) {
            is Member -> "dit lid (${customerRemoveState!!.name})"
            is Group -> "deze groep (${customerRemoveState!!.name})"
            else -> throw Exception("Fout bij verwijderen gebruiker")
        }

        // Dialog
        PopupDialog(
            confirmText = "Verwijderen",
            dismissText = "Annuleren",
            onConfirm = { // (Removes the customer)
                customerRepository.remove(customerRemoveState!!.id)
                customerRemoveState = null
            },
            onDismiss = { customerRemoveState = null }, // (Dismisses the popup)
            dialogTitle = "Lid/groep verwijderen",
            dialogText = "Weet je zeker dat je $desc wilt verwijderen?",
            icon = Icons.Rounded.Delete,
        )
    }

    // Content
    BeheerCustomersContent(
        members = customerRepository.members.data,
        groups = customerRepository.groups.data,
        customerOnClick = customerOnClick,
        customerOnMove = customerRepository::move,
        customerOnRemove = customerOnRemove,
        addTempMember = customerRepository::addExtraMember,
        addGroup = customerRepository::addGroup,
    )
}

@Composable
private fun BeheerCustomersContent(
    members: List<Member>,
    groups: List<Group>,
    customerOnClick: (Customer) -> Unit,
    customerOnMove: (id: Int, moveUp: Boolean) -> Unit,
    customerOnRemove: (id: Int) -> Unit,
    addTempMember: (newExtraName: String) -> Unit,
    addGroup: (newGroupName: String) -> Unit,
) {
    //UI
    VerticalGrid {
        // Title
        Spacer(Modifier.size(20.dp))
        TitleText("Leden en Groepen")
        Spacer(Modifier.size(20.dp))

        // Customer list
        CustomerList(
            members = members,
            groups = groups,
            customerOnClick = customerOnClick,
            customerOnMove = customerOnMove,
            customerOnRemove = customerOnRemove,
            addExtraMember = addTempMember,
            addGroup = addGroup,
        )
    }
}