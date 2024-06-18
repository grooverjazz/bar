package org.groover.bar.app.beheer.customers.member

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.app.beheer.items.PopupDialog
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.app.LabeledTextField
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
        memberRepository.changeMember(memberId, newRoepnaam, newVoornaam, newTussenvoegsel, newAchternaam, newVerjaardag, currentMember.isExtra)

        // Navigate back
        navigate("beheer/customers")
    }

    val context = LocalContext.current
    val remove = {
        // Remove the member
        memberRepository.removeById(memberId)

        // Navigate back
        navigate("beheer/customers")

        // Show toast
        Toast
            .makeText(context, "Lid verwijderd!", Toast.LENGTH_SHORT)
            .show()
    }

    BeheerMemberContent(
        navigate = navigate,
        currentMember = currentMember,
        finishEdit = finishEdit,
        remove = remove,
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
    remove: () -> Unit,
) {
    // Remember parameters
    var newRoepnaam: String by remember { mutableStateOf(currentMember.roepnaam) }
    var newVoornaam: String by remember { mutableStateOf(currentMember.voornaam) }
    var newTussenvoegsel: String by remember { mutableStateOf(currentMember.tussenvoegsel) }
    var newAchternaam: String by remember { mutableStateOf(currentMember.achternaam) }
    val newVerjaardagState = rememberDatePickerState(initialSelectedDateMillis = DateUtils.dateToMillis(currentMember.verjaardag))

    var delete: Boolean by remember { mutableStateOf(false) }

    if (delete) {
        PopupDialog(
            confirmText = "Verwijderen",
            dismissText = "Annuleren",
            onConfirm = remove,
            onDismiss = { delete = false },
            dialogTitle = "Item verwijderen",
            dialogText = "Weet je zeker dat je dit lid (${currentMember}) wilt verwijderen?",
            icon = Icons.Rounded.Delete,
        )
    }

    VerticalGrid(
        modifier = Modifier.padding(10.dp)
    ) {
        Spacer(Modifier.size(20.dp))
        Row {
            TitleText("Lid",
                modifier = Modifier.weight(.85f))

            Button(modifier = Modifier
                .weight(.15f)
                .height(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = { delete = true }
            ) {
                Icon(Icons.Rounded.Delete, null, Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.size(20.dp))

        // Roepnaam field
        LabeledTextField(
            text = "Roepnaam",
            value = newRoepnaam,
            onValueChange = { newRoepnaam = it },
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Voornaam field
        LabeledTextField(
            text = "Voornaam",
            value = newVoornaam,
            onValueChange = { newVoornaam = it },
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Tussenvoegsel field
        LabeledTextField(
            text = "Tussenvoegsel",
            value = newTussenvoegsel,
            onValueChange = { newTussenvoegsel = it },
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Achternaam field
        LabeledTextField(
            text = "Achternaam",
            value = newAchternaam,
            onValueChange = { newAchternaam = it },
        )
        Spacer(modifier = Modifier.size(20.dp))

        // Verjaardag field
        Text(
            text = "Verjaardag",
            fontSize = 20.sp,
        )
        DatePicker(
            state = newVerjaardagState,
            modifier = Modifier
                .padding(horizontal = 150.dp)
                .offset(y = (-50).dp),
            title = { }
        )

//        Icon(Icons.Rounded.Warning, null, tint=Color.Red)
//
//        Text(
//            text = "Pas op: deze gegevens worden overgeschreven bij import vanuit de ledenadmin!",
//            textAlign = TextAlign.Center,
//        )
//
//        Spacer(modifier = Modifier.size(30.dp))

        // Save button
        Button(
            modifier = Modifier.height(60.dp),
            onClick = {
                // Finish editing
                val newVerjaardag = DateUtils.millisToDate(newVerjaardagState.selectedDateMillis!!)
                finishEdit(newRoepnaam, newVoornaam, newTussenvoegsel, newAchternaam, newVerjaardag)
            }
        ) {
            Text("Opslaan")
        }
    }


}