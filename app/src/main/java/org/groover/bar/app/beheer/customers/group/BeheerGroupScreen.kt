package org.groover.bar.app.beheer.customers.group

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMap
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.customer.Group
import org.groover.bar.data.customer.Member
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.CustomerList
import org.groover.bar.util.app.CustomerListState
import org.groover.bar.util.app.LabeledTextField
import org.groover.bar.util.app.LazyBigList
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid


@Composable
fun BeheerGroupScreen(
    navigate: (route: String) -> Unit,
    customerRepository: CustomerRepository,
    groupId: Int,
) {
    // Get current group
    val currentGroup = customerRepository.groups.find(groupId)

    // Error
    if (currentGroup == null) {
        BeheerGroupError(groupId = groupId)
        return
    }

    // Remember list of new members
    val initialMembers = currentGroup.memberIds.fastMap {
        customerRepository.members.find(it) ?: throw Exception("Kan lid met ID $it niet vinden!")
    }
    val newMembers = remember { mutableStateListOf(*initialMembers.toTypedArray()) }

    // (Adds a member to the group)
    val includeMember = { member: Member ->
        if (!newMembers.contains(member))
            newMembers.add(member)
    }

    // (Removes a member from the group)
    val excludeMember: (Member) -> Unit = { member: Member ->
        newMembers.remove(member)
    }

    // (Finish editing the group)
    val finishEdit = { newName: String, newMemberIds: List<Int> ->
        // Change the group
        customerRepository.changeGroup(groupId, newName, newMemberIds)

        // Navigate back
        navigate("beheer/customers")
    }

    // Content
    BeheerGroupContent(
        members = customerRepository.members.data,
        currentGroup = currentGroup,
        finishEdit = finishEdit,
        newMembers = newMembers,
        includeMember = includeMember,
        excludeMember = excludeMember,
    )
}

@Composable
fun BeheerGroupError(
    groupId: Int,
) {
    // UI
    VerticalGrid {
        // Title
        TitleText("Groep met ID $groupId niet gevonden!")
    }
}

@Composable
fun BeheerGroupContent(
    members: List<Member>,
    currentGroup: Group,
    finishEdit: (newName: String, newMemberIds: List<Int>) -> Unit,
    newMembers: List<Member>,
    includeMember: (member: Member) -> Unit,
    excludeMember: (member: Member) -> Unit,
) {
    // Remember name
    var newName: String by remember { mutableStateOf(currentGroup.name) }

    // UI
    VerticalGrid {
        // Title
        Spacer(Modifier.size(20.dp))
        TitleText("Groep bewerken")
        Spacer(Modifier.size(40.dp))

        // Name field
        LabeledTextField(
            text = "Naam",
            value = newName,
            onValueChange = { newName = it },
        )
        Spacer(Modifier.size(40.dp))

        // Members to add
        Text("Leden om toe te voegen:",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        CustomerList(
            members = members.fastFilter { !newMembers.contains(it) },
            groups = emptyList(),
            height = 250.dp,
            listState = CustomerListState.MEMBERS,
            customerOnClick = { includeMember(it as Member) },
            customerOnMove = null,
            customerOnRemove = null,
            addExtraMember = null,
            addGroup = null,
        )
        Spacer(Modifier.size(40.dp))

        // Members to remove
        Text("Leden in groep (tik om te verwijderen):",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        LazyBigList(height = 250.dp) {
            items(newMembers) { member ->
                BigButton(member.toString(),
                    color = if (member.isExtra) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    onClick = { excludeMember(member) },
                )
            }
        }
        Spacer(Modifier.size(40.dp))

        // Save button
        Button({ finishEdit(newName, newMembers.fastMap { it.id }) },
            modifier = Modifier.height(60.dp),
        ) {
            Text("Opslaan",
                fontSize = 30.sp,
            )
        }
    }
}