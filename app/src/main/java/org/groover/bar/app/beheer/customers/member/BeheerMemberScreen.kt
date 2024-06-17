package org.groover.bar.app.beheer.customers.member

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
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
import org.groover.bar.util.data.DateUtils
import java.time.Instant
import java.util.Date

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

    val finishEdit = { newRoepnaam: String, newVoornaam: String, newTussenvoegsel: String, newAchternaam: String, newVerjaardag: Date ->
        // Change the member
        memberRepository.changeMember(memberId, newRoepnaam, newVoornaam, newTussenvoegsel, newAchternaam, newVerjaardag)

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
        TitleText("Lid met ID $memberId niet gevonden!")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeheerMemberContent(
    navigate: (String) -> Unit,
    currentMember: Member,
    finishEdit: (String, String, String, String, Date) -> Unit,
) {
    // Remember parameters
    var newRoepnaam: String by remember { mutableStateOf(currentMember.roepnaam) }
    var newVoornaam: String by remember { mutableStateOf(currentMember.voornaam) }
    var newTussenvoegsel: String by remember { mutableStateOf(currentMember.tussenvoegsel) }
    var newAchternaam: String by remember { mutableStateOf(currentMember.achternaam) }
    val newVerjaardagState = rememberDatePickerState(initialSelectedDateMillis = DateUtils.dateToMillis(currentMember.verjaardag))

    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        Spacer(Modifier.size(20.dp))
        TitleText("Lid bewerken")
        Spacer(Modifier.size(20.dp))

        // Roepnaam field
        TextField(
            value = newRoepnaam,
            onValueChange = { newRoepnaam = it },
            placeholder = { Text("Roepnaam") }
        )
        Spacer(modifier = Modifier.size(20.dp))

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
        Spacer(modifier = Modifier.size(20.dp))

        // Verjaardag field
        Text("Verjaardag")
        DatePicker(
            state = newVerjaardagState,
            modifier = Modifier.padding(horizontal = 150.dp, vertical = 0.dp),
            title = { }
        )

        Icon(Icons.Rounded.Warning, null, tint=Color.Red)

        Text(
            text = "Pas op: deze gegevens worden overgeschreven bij import vanuit de ledenadmin!",
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.size(30.dp))

        // Save button
        Button(onClick = {
            // Finish editing
            val newVerjaardag = DateUtils.millisToDate(newVerjaardagState.selectedDateMillis!!)
            finishEdit(newRoepnaam, newVoornaam, newTussenvoegsel, newAchternaam, newVerjaardag)
        }) {
            Text("Opslaan")
        }
    }
}