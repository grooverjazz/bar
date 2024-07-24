package org.groover.bar.app.beheer.customers.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.customer.Member
import org.groover.bar.app.util.BarButton
import org.groover.bar.app.util.BarTextField
import org.groover.bar.app.util.BarTitle
import org.groover.bar.app.util.BarLayout
import org.groover.bar.data.util.DateUtils
import java.util.Date

@Composable
fun BeheerMemberScreen(
    navigate: (route: String) -> Unit,
    customerRepository: CustomerRepository,
    memberId: Int,
) {
    // Get current member
    val currentMember = customerRepository.members.find(memberId)!!

    // (Finishes editing a member)
    val finishEdit = { newName: String, newBirthday: Date ->
        // Change the member
        customerRepository.changeMember(memberId, newName, newBirthday, currentMember.isExtra)

        // Navigate back
        navigate("beheer/customers")
    }

    // Content
    BeheerMemberContent(
        currentMember = currentMember,
        finishEdit = finishEdit,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeheerMemberContent(
    currentMember: Member,
    finishEdit: (newName: String, newBirthday: Date) -> Unit,
) {
    // Remember parameters
    var newName: String by remember { mutableStateOf(currentMember.name) }
    var newBirthdayStr: String by remember { mutableStateOf(DateUtils.serializeDate(currentMember.birthday)) }

    // Birthday calendar popup
    var birthdayPopupShow: Boolean by remember { mutableStateOf(false) }
    val birthdayPopupState: DatePickerState = rememberDatePickerState(initialSelectedDateMillis = DateUtils.dateToMillis(currentMember.birthday))

    // UI
    BarLayout {
        // Title
        Spacer(Modifier.size(20.dp))
        BarTitle("Lid bewerken")
        Spacer(Modifier.size(20.dp))

        // Name field
        BarTextField(
            text = "Roepnaam",
            value = newName,
            onValueChange = { newName = it },
        )
        Spacer(Modifier.size(20.dp))

        // Birthday field
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            // Text field
            BarTextField(Modifier.weight(0.8f),
                text = "Verjaardag",
                value = newBirthdayStr,
                onValueChange = { newBirthdayStr = it },
            )

            // Birthday calendar popup button
            Button({
                    birthdayPopupShow = true

                    val millis = DateUtils.dateToMillis(DateUtils.deserializeDate(newBirthdayStr.trim()))
                    birthdayPopupState.displayedMonthMillis = millis
                    birthdayPopupState.selectedDateMillis = millis
                },
                modifier = Modifier
                    .weight(0.2f)
                    .height(56.dp)
                    .align(Alignment.Bottom),
            ) { Icon(Icons.Rounded.DateRange, null) }
        }
        Spacer(Modifier.size(20.dp))

        // Birthday calendar popup
        if (birthdayPopupShow) {
            BirthdayPopup(
                pickerState = birthdayPopupState,
                dismiss = { birthdayPopupShow = false },
                confirm = {
                    birthdayPopupShow = false
                    val newBirthday = DateUtils.millisToDate(birthdayPopupState.selectedDateMillis!!)
                    newBirthdayStr = DateUtils.serializeDate(newBirthday)
                },
            )
        }

        // Save button
        BarButton("Opslaan",
            onClick = {
                // Finish editing
                val newVerjaardagDate = DateUtils.deserializeDate(newBirthdayStr.trim())
                finishEdit(newName, newVerjaardagDate)
            },
            rounded = true,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayPopup(
    pickerState: DatePickerState,
    dismiss: () -> Unit,
    confirm: () -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            Button(onClick = confirm) { Text("OK") }
        },
        dismissButton = {
            Button(onClick = dismiss) { Text("Cancel") }
        },
    ) { DatePicker(pickerState) }
}