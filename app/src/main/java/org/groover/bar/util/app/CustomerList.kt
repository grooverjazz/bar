package org.groover.bar.util.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import org.groover.bar.data.group.Group
import org.groover.bar.data.member.Member

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
) {
    var state by remember { mutableStateOf(listState ?: CustomerListState.MEMBERS) }
    var searchText by remember { mutableStateOf("") }

    // Leden / Groepen button
    if (listState == null) {
        Row {
            Button(
                modifier = Modifier.weight(1f),
                enabled = (state != CustomerListState.MEMBERS),
                onClick = { state = CustomerListState.MEMBERS }
            ) { Text("Leden") }

            Button(
                modifier = Modifier.weight(1f),
                enabled = (state != CustomerListState.GROUPS),
                onClick = { state = CustomerListState.GROUPS }
            ) { Text("Groepen") }
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
        placeholder = { Text("Zoek op naam") },
        modifier = Modifier.focusRequester(keyboardFocus)
    )

    // Show current list
    val formattedSearchText = searchText.trim().replaceFirstChar(Char::uppercaseChar)

    when (state) {
        CustomerListState.MEMBERS -> MemberList(
            members = members,
            searchText = formattedSearchText,
            onClick = memberOnClick,
            showAddNewButton = showAddNewButton,
            addTempMember = addTempMember,
        )

        CustomerListState.GROUPS -> GroupsList(
            groups = groups,
            searchText = formattedSearchText,
            onClick = groupOnClick,
            showAddNewButton = showAddNewButton,
            addGroup = addGroup
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
) {
    val filteredMembers = members
        .filter { it.toString().contains(searchText) }
        .take(if (searchText == "") 1000 else 20)

    // UI
    LazyColumn(
        modifier = Modifier.padding(10.dp).background(color = Color.LightGray).height(600.dp)
    ) {
        filteredMembers.forEach { member ->
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    onClick = { onClick(member) }
                ) { Text(member.toString()) }
            }
        }

        if (showAddNewButton && searchText != "") {
            item {
                Button(
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    onClick = { addTempMember(searchText) },
                ) { Text("Nieuw tijdelijk lid '$searchText' toevoegen") }
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
    addGroup: (String) -> Unit
) {
    val filteredGroups = groups
        .filter { it.toString().contains(searchText) }
        .take(20)

    // UI
    LazyColumn(
        modifier = Modifier.padding(10.dp).background(color = Color.LightGray).height(600.dp)
    ) {
        filteredGroups.forEach { group ->
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    onClick = { onClick(group) }
                ) { Text(group.toString()) }
            }
        }

        if (showAddNewButton && searchText != "") {
            item {
                Spacer(Modifier.size(80.dp))

                Button(
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    onClick = { addGroup(searchText) },
                ) { Text("Nieuwe lege groep '$searchText' toevoegen") }
            }
        }
    }
}