package org.groover.bar.app.bar.turven

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
fun BarTurvenScreen(
    navigate: (String) -> Unit,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
) {
    val memberOnClick = { member: Member ->
        navigate("bar/turven/customer/${member.id}")
    }

    val groupOnClick = { group: Group ->
        navigate("bar/turven/customer/${group.id}") // TODO: ander scherm
    }

    BarTurvenContent(
        navigate = navigate,
        members = memberRepository.data,
        groups = groupRepository.data,
        memberOnClick = memberOnClick,
        groupOnClick = groupOnClick,
    )
}


@Composable
private fun BarTurvenContent(
    navigate: (String) -> Unit,
    members: List<Member>,
    groups: List<Group>,
    memberOnClick: (Member) -> Unit,
    groupOnClick: (Group) -> Unit,
) {
    // UI
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navigate = navigate,
            text = "Terug",
            route = "bar",
        )

        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Turven")
        Spacer(modifier = Modifier.size(20.dp))

        // Customer list
        CustomerList(
            members = members,
            groups = groups,
            memberOnClick = memberOnClick,
            groupOnClick = groupOnClick,
            showAddNewButton = false,
            addTempMember = { /*impossible*/ },
            addGroup = { /*impossible*/ },
        )
    }
}