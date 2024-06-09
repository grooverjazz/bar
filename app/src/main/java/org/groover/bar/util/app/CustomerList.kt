package org.groover.bar.util.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.toLowerCase
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
) {
    var state by remember { mutableStateOf(listState ?: CustomerListState.MEMBERS) }
    var searchText by remember { mutableStateOf("") }

    // Leden / Groepen button
    if (listState == null) {
        Row {
            Button(
                modifier = Modifier.weight(1f).height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == CustomerListState.MEMBERS)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                ),
                onClick = { state = CustomerListState.MEMBERS }
            ) { Text(text = "Leden", fontSize = 25.sp) }

            Button(
                modifier = Modifier.weight(1f).height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == CustomerListState.GROUPS)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                ),
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
    val formattedSearchText = searchText.lowercase(Locale.ROOT)

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
    val filteredMembers = SearchHandler.search(searchText, members) { it.fullName }
        .take(if (searchText == "") 1000 else 20)

    // UI
    BigList {
        filteredMembers.forEach { member ->
            item {
                BigButton(
                    text = member.toString(),
                    onClick = { onClick(member) }
                )
            }
        }

        if (showAddNewButton && searchText != "") {
            item {
                BigButton(
                    text = "Nieuw tijdelijk lid '$searchText' toevoegen",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { addTempMember(searchText) },
                    hasSpacer = false,
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
    addGroup: (String) -> Unit
) {
    val filteredGroups = groups
        .filter { it.toString().contains(searchText) }
        .take(20)

    // UI
//    BigButtonList(
//        entries = filteredGroups,
//        onClick = onClick,
//    )

    BigList {
        filteredGroups.forEach { group ->
            item {
                BigButton(
                    text = group.toString(),
                    onClick = { onClick(group) },
                )
            }
        }

        if (showAddNewButton && searchText != "") {
            item {
                Spacer(Modifier.size(80.dp))

                BigButton(
                    text = "Nieuwe lege groep '$searchText' toevoegen",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { addGroup(searchText) },
                    hasSpacer = false,
                )
            }
        }
    }
}

@Composable
private fun BigList(
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp)
            .background(color = Color.LightGray)
            .height(700.dp),
        content = content
    )
}

@Composable
private fun BigButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = { },
    hasSpacer: Boolean = true
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RectangleShape,
        onClick = onClick
    ) {
        Text(
            text = text,
            fontSize = 25.sp
        )
    }

    if (hasSpacer)
        Spacer(modifier = Modifier.size(20.dp))
}