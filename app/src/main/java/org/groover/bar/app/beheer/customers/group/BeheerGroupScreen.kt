package org.groover.bar.app.beheer.customers.group

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.BigButton
import org.groover.bar.util.app.LazyBigList
import org.groover.bar.util.app.CustomerList
import org.groover.bar.util.app.CustomerListState
import org.groover.bar.util.app.LabeledTextField
import org.groover.bar.util.app.PopupDialog
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid


@Composable
fun BeheerGroupScreen(
    navigate: (String) -> Unit,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
    groupId: Int,
) {
    val currentGroup = groupRepository.find(groupId)

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
        memberRepository.find(it) ?: throw Exception("Kan lid met ID $it niet vinden!")
    }
    var newMembers = remember { mutableStateListOf(*initialMembers.toTypedArray()) }

    val addMember = { member: Member ->
        if (!newMembers.contains(member))
            newMembers += member
    }

    val removeMember = { member: Member ->
        newMembers -= member
    }

    val context = LocalContext.current
    val remove = {
        // Remove the member
        groupRepository.remove(groupId)

        // Navigate back
        navigate("beheer/customers")

        // Show toast
        Toast
            .makeText(context, "Groep verwijderd!", Toast.LENGTH_SHORT)
            .show()
    }

    BeheerGroupContent(
        navigate = navigate,
        members = memberRepository.data,
        currentGroup = currentGroup,
        finishEdit = finishEdit,
        newMembers = newMembers,
        addMember = addMember,
        removeMember = removeMember,
        remove = remove,
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
    remove: () -> Unit,
) {
    // Remember name
    var newName: String by remember { mutableStateOf(currentGroup.name) }

    var delete: Boolean by remember { mutableStateOf(false) }

    if (delete) {
        PopupDialog(
            confirmText = "Verwijderen",
            dismissText = "Annuleren",
            onConfirm = remove,
            onDismiss = { delete = false },
            dialogTitle = "Item verwijderen",
            dialogText = "Weet je zeker dat je dit lid (${currentGroup}) wilt verwijderen?",
            icon = Icons.Rounded.Delete,
        )
    }

    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        Spacer(Modifier.size(20.dp))
        Row {
            TitleText("Groep bewerken",
                modifier = Modifier.weight(.88f))

            Button(modifier = Modifier
                .weight(.12f)
                .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = { delete = true }
            ) {
                Icon(Icons.Rounded.Delete, null, Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.size(40.dp))

        // Name field
        LabeledTextField(
            text = "Naam",
            value = newName,
            onValueChange = { newName = it },
        )
        Spacer(modifier = Modifier.size(40.dp))

        // Members to add
        Text(
            text = "Leden om toe te voegen:",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomerList(
            members = members.filter { !newMembers.contains(it) },
            groups = emptyList(),
            listState = CustomerListState.MEMBERS,
            memberOnClick = addMember,
            memberOnMove = null,
            memberOnDelete = null,
            groupOnClick = { },
            groupOnMove = null,
            groupOnDelete = null,
            showAddNewButton = false,
            addTempMember = { },
            addGroup = { },
            height = 250.dp,
        )
        Spacer(modifier = Modifier.size(40.dp))

        // Members to remove
        Text(
            text = "Leden in groep (tik om te verwijderen):",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyBigList(height = 250.dp) {
            items(newMembers) { member ->
                BigButton(
                    color = if (member.isExtra) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    text = member.toString(),
                    onClick = { removeMember(member) }
                )
            }
        }
        Spacer(modifier = Modifier.size(40.dp))

        Button(
            modifier = Modifier.height(60.dp),
            onClick = { finishEdit(newName, newMembers.map { it.id }) },
        ) {
            Text("Opslaan",
                fontSize = 30.sp
            )
        }
    }
}