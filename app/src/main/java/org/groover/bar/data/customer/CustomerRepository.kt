package org.groover.bar.data.customer

import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMaxOfOrNull
import org.groover.bar.util.data.DateUtils
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
    fun generateMemberId() = members.generateId()

    // (Finds the corresponding customer)
    fun find(id: Int): Customer? = data.fastFirstOrNull { element -> element.id == id }

    // (Adds an extra member)
    fun addExtraMember(
        newExtraName: String,
        errorHandlingOverrideId: Int? = null // ONLY USE FOR ERROR HANDLING
    ): Customer {
        // Create extra member
        val extraMember = Member(
            id = errorHandlingOverrideId ?: generateMemberId(),
            name = newExtraName,
            birthday = DateUtils.Y2K,
            isExtra = true,
        )

        // Prepend
        members.addToStart(extraMember)

        return extraMember
    }

    // (Changes a member)
    fun changeMember(
        memberId: Int,
        newName: String,
        newBirthday: Date,
        isExtra: Boolean,
    ) {
        // Create new member
        val newMember = Member(memberId, newName, newBirthday, isExtra)

        // Replace
        members.replace(memberId, newMember)
    }

    // (Generates a group ID)
    fun generateGroupId(): Int = max(
        data.fastMaxOfOrNull { it.id }?.plus(1) ?: 0,
        1000000
    )

    // (Adds a new group)
    fun addGroup(newGroupName: String) {
        val newGroup = Group(
            // Create new group
            id = generateGroupId(),
            name = newGroupName,
            memberIds = emptyList(),
        )

        // Prepend
        groups.addToStart(newGroup)
    }

    // (Changes a group)
    fun changeGroup(
        groupId: Int,
        newName: String,
        newMemberIds: List<Int>,
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