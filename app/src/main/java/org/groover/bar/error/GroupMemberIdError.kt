package org.groover.bar.error

import androidx.compose.runtime.Composable
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.util.removeFirst

/**
 * Error where Group.memberIds contains a non-existent Member ID or a Group ID.
 */
data class GroupMemberIdError(
    private val customerRepository: CustomerRepository,
    private val groupId: Int,
    private val memberId: Int,
) : BarError(){
    // (Turns the error into a String)
    override fun toString(): String {
        val group = customerRepository.groups.find(groupId)!!
        return "Groep '$group' bevat lidnummer $memberId die niet bestaat of een groeps-ID is."
    }

    // (OVERRIDE: Makes a panel for the error screen)
    @Composable
    override fun Panel(callback: () -> Unit) = Panel(callback,
        "Verwijder '$memberId' uit groep" to ::solveByDelete,
        "Maak tijdelijk lid aan" to ::solveByExtraMember
    )

    // (Deletes the member from the group)
    private fun solveByDelete() {
        // Make copy of group
        val group = customerRepository.groups.find(groupId)!!
        val newGroup = group.copy(memberIds = group.memberIds.removeFirst { it == memberId })

        // Replace
        customerRepository.groups.replace(group.id, newGroup)
    }

    // (Creates an extra member)
    private fun solveByExtraMember() {
        // Create extra member
        val extraMember = customerRepository.addExtraMember(
            "Het spook van lid $memberId",
            errorHandlingOverrideId = memberId
        )

        // Make copy of group
        val group = customerRepository.groups.find(groupId)!!
        val newGroup = group.copy(memberIds = group.memberIds.removeFirst { it == memberId } + extraMember.id)

        // Replace
        customerRepository.groups.replace(group.id, newGroup)
    }
}