package org.groover.bar.app.beheer.customers.member

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.NavigateButton
import org.groover.bar.util.app.TitleText
import org.groover.bar.util.app.VerticalGrid

@Composable
fun BeheerMemberScreen(
    navigate: (String) -> Unit,
    memberRepository: MemberRepository,
    memberId: Int,
) {
    val currentMember = memberRepository.lookupById(memberId)!!

    BeheerMemberContent(
        navigate = navigate,
        currentMember = currentMember,
    )
}


@Composable
private fun BeheerMemberContent(
    navigate: (String) -> Unit,
    currentMember: Member,
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

        TitleText("Lid bewerken")

        Spacer(Modifier.size(100.dp))

        // Title
        TitleText(currentMember.toString())
    }
}