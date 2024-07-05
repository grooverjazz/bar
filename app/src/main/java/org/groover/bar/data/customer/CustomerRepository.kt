package org.groover.bar.data.customer

import org.groover.bar.util.data.DateUtils
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository
import java.util.Date
import kotlin.math.max

class CustomerRepository(
    val members: MemberRepository,
    val groups: GroupRepository,
) {
    val data: List<Customer> get() = members.data + groups.data

    // (Loads/reloads the data of the repository)
    fun open() {
        members.open()
        groups.open()
    }

    // (Saves the data of the repository)
    fun save() {
        members.save()
        groups.save()
    }


    // (Generates a member ID)
    private fun generateMemberId() = members.generateId()

    // (Finds the corresponding customer)
    fun find(id: Int): Customer? = data.firstOrNull { element -> element.id == id }

    // (Adds an extra member)
    fun addExtraMember(tempName: String) {
        // Create extra member
        val extraMember = Member(
            id = generateMemberId(),
            name = tempName,
            birthday = DateUtils.Y2K,
            isExtra = true,
        )

        // Prepend
        members.addToStart(extraMember)
    }

    // (Changes a member)
    fun changeMember(
        memberId: Int,
        newName: String,
        newBirthday: Date,
        isExtra: Boolean
    ) {
        // Create new member
        val newMember = Member(memberId, newName, newBirthday, isExtra)

        // Replace
        members.replace(memberId, newMember)
    }

    // (Generates a group ID)
    private fun generateGroupId(): Int = max(
        data.maxByOrNull { it.id }?.id?.plus(1) ?: 0,
        1000000
    )

    // (Adds a new group)
    fun addGroup(newGroupName: String) {
        val newGroup = Group(
            // Create new group
            id = generateGroupId(),
            name = newGroupName,
            memberIds = emptyList()
        )

        // Prepend
        groups.addToStart(newGroup)
    }

    // (Changes a group)
    fun changeGroup(
        groupId: Int,
        newName: String,
        newMemberIds: List<Int>
    ) {
        // Create new group
        val newGroup = Group(
            groupId,
            newName,
            newMemberIds,
        )

        // Replace
        groups.replace(groupId, newGroup)
    }

    // (Removes a customer)
    fun remove(id: Int) {
        when (find(id)) {
            is Member -> members.remove(id)
            is Group -> groups.remove(id)
            else -> throw Exception("Error bij verwijderen gebruiker")
        }
    }

    // (Moves a customer up or down)
    fun move(id: Int, moveUp: Boolean) {
        when (find(id)) {
            is Member -> members.move(id, moveUp)
            is Group -> groups.move(id, moveUp)
            else -> throw Exception("Error bij verplaatsen gebruiker")
        }
    }
}