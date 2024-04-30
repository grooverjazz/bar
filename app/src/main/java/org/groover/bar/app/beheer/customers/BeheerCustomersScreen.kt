package org.groover.bar.app.beheer.customers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.TotalMemberList
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerCustomersScreen(
    navController: NavController,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
) {
    VerticalGrid(
        modifier = Modifier
            .padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "beheer",
            height = 60.dp,
        )

        // Title
        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Leden en Groepen")
        Spacer(modifier = Modifier.size(20.dp))

        // Member list
        TotalMemberList(
            memberRepository = memberRepository,
            groupRepository = groupRepository,
            memberOnClick = { member ->
                navController.navigate("beheer/customers/member/${member.id}")
            },
            groupOnClick = {
                groupRepository.removeGroup(it.id) // TODO
            },
            showAddNewButton = true,
        )
    }
}