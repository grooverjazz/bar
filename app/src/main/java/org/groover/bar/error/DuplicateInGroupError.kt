package org.groover.bar.error

import androidx.compose.runtime.Composable
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.util.data.removeFirst

// There are 2 or more customers in the same group.
class DuplicateInGroupError(
    val customerRepository: CustomerRepository,
    val groupId: Int,
    val memberId: Int,
) : Error() {
    override fun toString(): String {
        val group = customerRepository.groups.find(groupId)
        val member = customerRepository.members.find(memberId)
        return "Het lid '$member' zit meerdere keren in groep '$group'"
    }

    @Composable
    override fun Panel(callback: () -> Unit) = Panel(callback,
        "Haal er eentje weg" to ::solve
    )

    // (Removes the first instance of the member ID)
    private fun solve() {
        val group = customerRepository.groups.find(groupId)!!
        customerRepository.groups.replace(groupId, group.copy(memberIds = group.memberIds.removeFirst { it == memberId }))
    }
}