package org.groover.bar.app.beheer.customers.member

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    val currentMember = memberRepository.lookupById(memberId)

    if (currentMember == null) {
        BeheerMemberError(
            navigate = navigate,
            memberId = memberId
        )
        return
    }

    val finishEdit = { newVoornaam: String, newTussenvoegsel: String, newAchternaam: String ->
        // Change the member
        memberRepository.changeMember(memberId, newVoornaam, newTussenvoegsel, newAchternaam)

        // Navigate back
        navigate("beheer/customers")
    }

    BeheerMemberContent(
        navigate = navigate,
        currentMember = currentMember,
        finishEdit = finishEdit,
    )
}


@Composable
private fun BeheerMemberError(
    navigate: (String) -> Unit,
    memberId: Int,
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

        TitleText("Lid met ID $memberId niet gevonden!")
    }
}


@Composable
private fun BeheerMemberContent(
    navigate: (String) -> Unit,
    currentMember: Member,
    finishEdit: (String, String, String) -> Unit,
) {
    // Remember voornaam, tussenvoegsel, achternaam
    var newVoornaam: String by remember { mutableStateOf(currentMember.voornaam) }
    var newTussenvoegsel: String by remember { mutableStateOf(currentMember.tussenvoegsel) }
    var newAchternaam: String by remember { mutableStateOf(currentMember.achternaam) }

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

        TitleText("Lid bewerken")
        Spacer(Modifier.size(20.dp))

        // Voornaam field
        TextField(
            value = newVoornaam,
            onValueChange = { newVoornaam = it },
            placeholder = { Text("Voornaam") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Tussenvoegsel field
        TextField(
            value = newTussenvoegsel,
            onValueChange = { newTussenvoegsel = it },
            placeholder = { Text("Tussenvoegsel") }
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Achternaam field
        TextField(
            value = newAchternaam,
            onValueChange = { newAchternaam = it },
            placeholder = { Text("Achternaam") }
        )
        
        if (newVoornaam != currentMember.voornaam || newTussenvoegsel != currentMember.tussenvoegsel || newAchternaam != currentMember.achternaam) {
            Spacer(modifier = Modifier.size(20.dp))

            Icon(Icons.Rounded.Warning, null, tint=Color.Red)

            Text(
                text = "Pas op: deze gegevens worden overgeschreven bij import vanuit de ledenadmin!",
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.size(30.dp))

        // Save button
        Button(onClick = {
            // Finish editing
            finishEdit(newVoornaam, newTussenvoegsel, newAchternaam)
        }) {
            Text("Opslaan")
        }
    }
}