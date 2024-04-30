package org.groover.bar.app.beheer.member

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerMemberScreen(
    navController: NavController,
    memberRepository: MemberRepository,
    memberId: Int,
) {
    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        // Terug button
        NavigateButton(
            navController = navController,
            text = "Terug",
            route = "beheer",
            height = 60.dp,
        )

        TitleText("Lid bewerken")

        val currentMember = memberRepository.lookupById(memberId)!!

        Spacer(Modifier.size(100.dp))

        // Title
        TitleText(currentMember.toString())
    }
}