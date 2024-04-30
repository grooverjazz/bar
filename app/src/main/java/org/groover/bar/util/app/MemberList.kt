package org.groover.bar.util.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository

enum class MemberListState {
    MEMBERS,
    GROUPS
}

@Composable
fun TotalMemberList(
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
    memberOnClick: (Member) -> Unit,
    groupOnClick: (Group) -> Unit,
    showAddNewButton: Boolean = false,
) {
    var state by remember { mutableStateOf(MemberListState.MEMBERS) }
    var searchText by remember { mutableStateOf("") }

    // Leden / Groepen button
    Row {
        Button(
            modifier = Modifier.weight(1f),
            enabled = (state != MemberListState.MEMBERS),
            onClick = { state = MemberListState.MEMBERS }
        ) { Text("Leden") }

        Button(
            modifier = Modifier.weight(1f),
            enabled = (state != MemberListState.GROUPS),
            onClick = { state = MemberListState.GROUPS }
        ) { Text("Groepen") }
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
        MemberListState.MEMBERS -> MemberList(memberRepository, formattedSearchText, memberOnClick, showAddNewButton)
        MemberListState.GROUPS -> GroupsList(groupRepository, formattedSearchText, groupOnClick, showAddNewButton)
    }
}

@Composable
fun MemberList(
    memberRepository: MemberRepository,
    searchText: String,
    onClick: (Member) -> Unit,
    showAddNewButton: Boolean
) {
    val members = memberRepository.data
    val filteredMembers = members
        .filter { it.toString().contains(searchText) }
        .take(20)

    // UI
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.padding(10.dp)
    ) {
        filteredMembers.forEach { member ->
            item {
                Button(
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
                    onClick = {
                        memberRepository.addTempMember(searchText)
                    },
                ) { Text("Nieuw tijdelijk lid '$searchText' toevoegen") }
            }
        }
    }
}

@Composable
fun GroupsList(
    groupRepository: GroupRepository,
    searchText: String,
    onClick: (Group) -> Unit,
    showAddNewButton: Boolean
) {
    val groups = groupRepository.data
    val filteredGroups = groups
        .filter { it.toString().contains(searchText) }
        .take(20)

    // UI
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.padding(10.dp)
    ) {
        filteredGroups.forEach { group ->
            item {
                Button(
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
                    onClick = {
                        groupRepository.addGroup(searchText)
                    },
                ) { Text("Nieuwe lege groep '$searchText' toevoegen") }
            }
        }
    }
}