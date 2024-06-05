package org.groover.bar.app.beheer.customers.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.CustomerList
import org.groover.bar.util.app.CustomerListState
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid


@Composable
fun BeheerGroupScreen(
    navigate: (String) -> Unit,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
    groupId: Int,
) {
    val currentGroup = groupRepository.lookupById(groupId)

    if (currentGroup == null) {
        BeheerGroupError(
            navigate = navigate,
            groupId = groupId
        )
        return
    }

    val finishEdit = { newName: String, newMemberIds: List<Int> ->
        // Change the group
        groupRepository.changeGroup(groupId, newName, newMemberIds)

        // Navigate back
        navigate("beheer/customers")
    }

    val initialMembers = currentGroup.memberIds.map {
        memberRepository.lookupById(it) ?: throw Exception("Kan lid met ID $it niet vinden!")
    }
    var newMembers = remember { mutableStateListOf(*initialMembers.toTypedArray()) }

    val addMember = { member: Member ->
        if (!newMembers.contains(member))
            newMembers += member
    }

    val removeMember = { member: Member ->
        newMembers -= member
    }

    BeheerGroupContent(
        navigate = navigate,
        members = memberRepository.data,
        currentGroup = currentGroup,
        finishEdit = finishEdit,
        newMembers = newMembers,
        addMember = addMember,
        removeMember = removeMember,
    )
}

@Composable
fun BeheerGroupError(
    navigate: (String) -> Unit,
    groupId: Int,
) {
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "beheer/customers",
            height = 60.dp,
        )
        Spacer(Modifier.size(20.dp))

        // Name field
        TitleText("Groep met ID $groupId niet gevonden!")
    }
}

@Composable
fun BeheerGroupContent(
    navigate: (String) -> Unit,
    members: List<Member>,
    currentGroup: Group,
    finishEdit: (String, List<Int>) -> Unit,
    newMembers: SnapshotStateList<Member>,
    addMember: (Member) -> Unit,
    removeMember: (Member) -> Unit,
) {
    // Remember name, memberIds
    var newName: String by remember { mutableStateOf(currentGroup.name) }

    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "beheer/customers",
            height = 60.dp,
        )
        Spacer(Modifier.size(20.dp))

        // Name field
        TextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text("Naam") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Members to remove
        LazyColumn(
            modifier = Modifier.padding(10.dp).background(Color.LightGray).height(200.dp)
        ) {
            newMembers.forEach {  member ->
                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        onClick = { removeMember(member) }
                    ) { Text("$member") }
                }
            }
        }

        Spacer(modifier = Modifier.size(30.dp))

        // Save button
        Button(onClick = {
            // Finish editing
            finishEdit(newName, newMembers.map { it.id })
        }) {
            Text("Opslaan")
        }

        // Members to add
        CustomerList(
            members = members,
            groups = emptyList(),
            listState = CustomerListState.MEMBERS,
            memberOnClick = addMember,
            groupOnClick = { },
            showAddNewButton = false,
            addTempMember = { },
            addGroup = { },
        )
    }
}