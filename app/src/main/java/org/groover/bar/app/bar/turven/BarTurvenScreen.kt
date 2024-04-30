package org.groover.bar.app.bar.turven

import android.util.Log
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
fun BarTurvenScreen(
    navController: NavController,
    memberRepository: MemberRepository,
    groupRepository: GroupRepository,
) {
    // UI
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "bar",
            height = 60.dp,
        )

        Spacer(modifier = Modifier.size(20.dp))
        TitleText("Turven")
        Spacer(modifier = Modifier.size(20.dp))

        // Member list
        TotalMemberList(
            memberRepository = memberRepository,
            groupRepository = groupRepository,
            memberOnClick = { member ->
                navController.navigate("bar/turven/customer/${member.id}")
            },
            groupOnClick = { group ->
                navController.navigate("bar/turven/customer/${group.id}")
//                Log.i("TURVEN", "GROEP $group")
            },
        )
    }
}