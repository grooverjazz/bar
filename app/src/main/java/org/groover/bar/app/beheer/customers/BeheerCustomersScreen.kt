package org.groover.bar.app.beheer.customers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.CustomerList
import org.groover.bar.util.app.PopupDialog
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid
import org.groover.bar.util.data.BarData

@Composable
fun BeheerCustomersScreen(
    navigate: (String) -> Unit,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
) {
    val memberOnClick = { member: Member ->
        navigate("beheer/customers/member/${member.id}")
    }

    val groupOnClick = { group: Group ->
        navigate("beheer/customers/group/${group.id}")
    }

    var customerRemoveState: BarData? by remember { mutableStateOf(null) }

    if (customerRemoveState != null) {
        val remove: (id: Int) -> Unit
        val desc: String

        when (customerRemoveState) {
            is Member -> {
                remove = memberRepository::remove
                desc = "dit lid (${(customerRemoveState as Member).fullName})"
            }
            is Group -> {
                remove = groupRepository::remove
                desc = "deze groep (${(customerRemoveState as Group).name})"
            }
            else -> throw Exception("Fout bij verwijderen gebruiker")
        }

        PopupDialog(
            confirmText = "Verwijderen",
            dismissText = "Annuleren",
            onConfirm = {
                remove(customerRemoveState!!.id)
                customerRemoveState = null
            },
            onDismiss = { customerRemoveState = null },
            dialogTitle = "Lid/groep verwijderen",
            dialogText = "Weet je zeker dat je $desc wilt verwijderen?",
            icon = Icons.Rounded.Delete,
        )
    }

    val memberOnDelete = { id: Int -> customerRemoveState = memberRepository.find(id) }
    val groupOnDelete = { id: Int -> customerRemoveState = groupRepository.find(id) }

    BeheerCustomersContent(
        navigate = navigate,
        members = memberRepository.data,
        groups = groupRepository.data,
        memberOnClick = memberOnClick,
        memberOnMove = memberRepository::move,
        memberOnDelete = memberOnDelete,
        groupOnClick = groupOnClick,
        groupOnMove = groupRepository::move,
        groupOnDelete = groupOnDelete,
        addTempMember = memberRepository::addExtraMember,
        addGroup = groupRepository::addGroup
    )
}

@Composable
private fun BeheerCustomersContent(
    navigate: (String) -> Unit,
    members: List<Member>,
    groups: List<Group>,
    memberOnClick: (Member) -> Unit,
    memberOnMove: (id: Int, moveUp: Boolean) -> Unit,
    memberOnDelete: (id: Int) -> Unit,
    groupOnClick: (Group) -> Unit,
    groupOnMove: (id: Int, moveUp: Boolean) -> Unit,
    groupOnDelete: (id: Int) -> Unit,
    addTempMember: (String) -> Unit,
    addGroup: (String) -> Unit
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Leden en Groepen")
        Spacer(modifier = Modifier.size(20.dp))

        // Customer list
        CustomerList(
            members = members,
            groups = groups,
            memberOnClick = memberOnClick,
            memberOnMove = memberOnMove,
            memberOnDelete = memberOnDelete,
            groupOnClick = groupOnClick,
            groupOnMove = groupOnMove,
            groupOnDelete = groupOnDelete,
            showAddNewButton = true,
            addTempMember = addTempMember,
            addGroup = addGroup,
        )
    }
}