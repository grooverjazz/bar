package org.groover.bar.data.customer.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.android.awaitFrame
import org.groover.bar.data.customer.Customer
import org.groover.bar.data.customer.Group
import org.groover.bar.data.customer.Member
import org.groover.bar.app.util.BarButton
import org.groover.bar.app.util.BarListMoveable
import org.groover.bar.app.util.BarMultiButtons
import org.groover.bar.data.util.SearchHandler
import java.util.Locale

/**
 * The state that a CustomerList may be in.
 */
enum class CustomerListState {
    /** Shows all members.*/
    MEMBERS,
    /** Shows all groups.*/
    GROUPS
}

/**
 * A list of all customers (members, groups).
 */
@Composable
fun CustomerList(
    members: List<Member>,
    groups: List<Group>,
    height: Dp = 800.dp,
    listState: CustomerListState? = null,
    customerOnClick: (customer: Customer) -> Unit,
    customerOnMove: ((id: Int, moveUp: Boolean) -> Unit)?,
    customerOnRemove: ((id: Int) -> Unit)?,
    addExtraMember: ((name: String) -> Unit)?,
    addGroup: ((name: String) -> Unit)?,
) {
    var state by remember { mutableStateOf(listState ?: CustomerListState.MEMBERS) }
    var searchText by remember { mutableStateOf("") }

    // Leden / Groepen button
    if (listState == null) {
        BarMultiButtons(
            currentValue = state,
            onValueChange = { state = it },
            options = listOf("Leden", "Groepen"),
            values = listOf(CustomerListState.MEMBERS, CustomerListState.GROUPS),
        )
    }

    // Keyboard focus for search box
    val keyboardFocus = remember { FocusRequester() }
    LaunchedEffect(keyboardFocus) {
        awaitFrame()
        keyboardFocus.requestFocus()
    }

    // Search box
    TextField(searchText,
        modifier = Modifier.fillMaxWidth()
            .focusRequester(keyboardFocus)
            .height(80.dp),
        onValueChange = { searchText = it },
        textStyle = TextStyle.Default.copy(fontSize = 28.sp),
    )

    // Format search text
    val formattedSearchText = searchText
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        .trim()

    // Define colors
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Show customer list
    val customers: List<Customer>
    val addCustomer: ((String) -> Unit)?
    val addCustomerText: String
    val getColor: (Customer) -> Color

    when (state) {
        CustomerListState.MEMBERS -> {
            customers = members
            addCustomer = addExtraMember
            addCustomerText = "Nieuw 'extra' lid '$searchText' toevoegen"
            getColor = { if ((it as Member).isExtra) secondaryColor else primaryColor }
        }
        CustomerListState.GROUPS -> {
            customers = groups
            addCustomer = addGroup
            addCustomerText = "Nieuwe lege groep '$searchText' toevoegen"
            getColor = { secondaryColor }
        }
    }

    CustomerList(
        customers = customers,
        height = height,
        searchText = formattedSearchText,
        onClick = customerOnClick,
        onMove = customerOnMove,
        onRemove = customerOnRemove,
        addCustomer = addCustomer,
        addCustomerText = addCustomerText,
        getColor = getColor,
    )
}

@Composable
private fun CustomerList(
    customers: List<Customer>,
    height: Dp,
    searchText: String,
    onClick: (customer: Customer) -> Unit,
    onMove: ((id: Int, moveUp: Boolean) -> Unit)?,
    onRemove: ((id: Int) -> Unit)?,
    addCustomer: ((name: String) -> Unit)?,
    addCustomerText: String?,
    getColor: (Customer) -> Color,
) {
    // Search through customers
    val filteredCustomers = SearchHandler.search(searchText, customers) { it.toString() }

    // UI
    BarListMoveable(
        height = height,
        lazy = true,
        elements = filteredCustomers,
        getName = { it.toString() },
        getVisible = { true },
        getColor = getColor,
        fontColor = Color.White,
        onClick = onClick,
        onMove = onMove,
        onToggleVisible = null,
        onRemove = onRemove,
    ) {
        if (searchText != "" && addCustomer != null) {
            BarButton(addCustomerText!!,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { addCustomer(searchText) },
            )
        }
    }
}
