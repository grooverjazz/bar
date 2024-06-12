package org.groover.bar.app.beheer.customers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.CustomerList
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

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

    BeheerCustomersContent(
        navigate = navigate,
        members = memberRepository.data,
        groups = groupRepository.data,
        memberOnClick = memberOnClick,
        groupOnClick = groupOnClick,
        addTempMember = memberRepository::addTempMember,
        addGroup = groupRepository::addGroup
    )
}

@Composable
private fun BeheerCustomersContent(
    navigate: (String) -> Unit,
    members: List<Member>,
    groups: List<Group>,
    memberOnClick: (Member) -> Unit,
    groupOnClick: (Group) -> Unit,
    addTempMember: (String) -> Unit,
    addGroup: (String) -> Unit
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "beheer",
        )

        // Title
        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Leden en Groepen")
        Spacer(modifier = Modifier.size(20.dp))

        // Customer list
        CustomerList(
            members = members,
            groups = groups,
            memberOnClick = memberOnClick,
            groupOnClick = groupOnClick,
            showAddNewButton = true,
            addTempMember = addTempMember,
            addGroup = addGroup,
        )
    }
}