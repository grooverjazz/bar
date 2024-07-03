package org.groover.bar.util.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.android.awaitFrame
import org.groover.bar.data.group.Group
import org.groover.bar.data.member.Member
import org.groover.bar.util.data.SearchHandler
import java.util.Locale

enum class CustomerListState {
    MEMBERS,
    GROUPS
}

@Composable
fun CustomerList(
    members: List<Member>,
    groups: List<Group>,
    listState: CustomerListState? = null,
    memberOnClick: (Member) -> Unit,
    groupOnClick: (Group) -> Unit,
    showAddNewButton: Boolean = false,
    addTempMember: (String) -> Unit,
    addGroup: (String) -> Unit,
    height: Dp = 800.dp,
) {
    var state by remember { mutableStateOf(listState ?: CustomerListState.MEMBERS) }
    var searchText by remember { mutableStateOf("") }

    // Leden / Groepen button
    if (listState == null) {
        Row {
            val modifier = Modifier.weight(1f).height(80.dp)
            val modifierShadow = modifier.innerShadow(
                shape = RectangleShape,
                blur = 12.dp,
                offsetX = 3.dp,
                offsetY = 3.dp,
            )

            Button(
                modifier = if (state == CustomerListState.MEMBERS) modifierShadow else modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == CustomerListState.MEMBERS)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                ),
                shape = RectangleShape,
                onClick = { state = CustomerListState.MEMBERS }
            ) { Text(text = "Leden", fontSize = 25.sp) }

            Button(
                modifier = if (state == CustomerListState.GROUPS) modifierShadow else modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == CustomerListState.GROUPS)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                ),
                shape = RectangleShape,
                onClick = { state = CustomerListState.GROUPS }
            ) { Text(text = "Groepen", fontSize = 25.sp) }
        }
    }

    // Keyboard focus for search box
    val keyboardFocus = remember { FocusRequester() }
    LaunchedEffect(keyboardFocus) {
        awaitFrame()
        keyboardFocus.requestFocus()
    }

    // Search box
    TextField(
        value = searchText,
        onValueChange = { newSearchText: String ->
            searchText = newSearchText
        },
        modifier = Modifier.focusRequester(keyboardFocus).height(80.dp),
        textStyle = TextStyle.Default.copy(fontSize = 28.sp)
    )

    // Show current list
    //  (with capitalized first letter)
    val formattedSearchText = searchText
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        .trim()

    when (state) {
        CustomerListState.MEMBERS -> MemberList(
            members = members,
            searchText = formattedSearchText,
            onClick = memberOnClick,
            showAddNewButton = showAddNewButton,
            addTempMember = addTempMember,
            height = height,
        )

        CustomerListState.GROUPS -> GroupsList(
            groups = groups,
            searchText = formattedSearchText,
            onClick = groupOnClick,
            showAddNewButton = showAddNewButton,
            addGroup = addGroup,
            height = height,
        )
    }
}

@Composable
private fun MemberList(
    members: List<Member>,
    searchText: String,
    onClick: (Member) -> Unit,
    showAddNewButton: Boolean,
    addTempMember: (String) -> Unit,
    height: Dp,
) {
    val filteredMembers = SearchHandler.search(searchText, members) { it.toString() }

    // UI
    LazyBigList(height = height) {
        if (showAddNewButton && searchText != "") {
            item {
                BigButton(
                    text = "Nieuw tijdelijk lid '$searchText' toevoegen",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { addTempMember(searchText) },
                )
            }
        }

        filteredMembers.forEach { member ->
            item {
                BigButton(
                    text = member.toString(),
                    color = if (member.isExtra) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    onClick = { onClick(member) }
                )
            }
        }
    }
}

@Composable
private fun GroupsList(
    groups: List<Group>,
    searchText: String,
    onClick: (Group) -> Unit,
    showAddNewButton: Boolean,
    addGroup: (String) -> Unit,
    height: Dp,
) {
    val filteredGroups = groups
        .filter { it.toString().contains(searchText) }

    // UI
    LazyBigList(height = height) {
        if (showAddNewButton && searchText != "") {
            item {
                BigButton(
                    text = "Nieuwe lege groep '$searchText' toevoegen",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { addGroup(searchText) },
                )
            }
        }

        filteredGroups.forEach { group ->
            item {
                BigButton(
                    text = group.toString(),
                    onClick = { onClick(group) },
                )
            }
        }
    }
}



