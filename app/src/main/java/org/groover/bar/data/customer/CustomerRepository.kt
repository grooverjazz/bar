package org.groover.bar.data.customer

import org.groover.bar.util.data.DateUtils
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository
import java.util.Date
import kotlin.math.max

class CustomerRepository(
    fileOpener: FileOpener,
) : Repository<Customer>(
    fileOpener,
    "",
    { throw Exception("Unused serialize") },
    { throw Exception("Unused deserialize") },
    listOf("id", "name", "birthday")
) {
    val members: List<Member> get() = data.filterIsInstance<Member>()
    val groups: List<Group> get() = data.filterIsInstance<Group>()

    // TODO: move() override

    init {
        open()
    }

    // (OVERRIDE: Loads/reloads the data of the repository)
    override fun open() {
        // Read from file, add extra members
        mutableData.clear()

        // Add extra members
        mutableData += readFile("extraMembers.csv", Member.Companion::deserialize)
            .map { it.copy(isExtra = true) }

        // Add regular members
        mutableData += readFile("members.csv", Member.Companion::deserialize)

        // Add groups
        mutableData += readFile("groups.csv", Group.Companion::deserialize)

        // Add Hospitality if not present
        if (find(0) == null)
            addToStart(Member(0, "Hospitality", DateUtils.Y2K, true))
    }

    // (OVERRIDE: Saves the data of the repository)
    override fun save() {
        // Partition extra and regular members
        val (extraMembers, regularMembers) = members.partition { it.isExtra }

        // Save separately
        saveFile("groups.csv", groups, Group.Companion::serialize)
        saveFile("members.csv", regularMembers, Member.Companion::serialize)
        saveFile("extraMembers.csv", extraMembers, Member.Companion::serialize)
    }

    // (Finds the corresponding member)
    fun findMember(id: Int): Member? = find(id) as? Member

    // (Adds an extra member)
    fun addExtraMember(tempName: String) {
        // Create extra member
        val extraMember = Member(
            id = generateId(),
            name = tempName,
            birthday = DateUtils.Y2K,
            isExtra = true,
        )

        // Prepend
        addToStart(extraMember)
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
        replace(memberId, newMember)
    }

    // (Finds the corresponding member)
    fun findGroup(id: Int): Group? = find(id) as? Group

    // (Adds a new group)
    fun addGroup(newGroupName: String) {
        val newGroup = Group(
            // Create new group
            id = max(generateId(), 1000000),
            name = newGroupName,
            memberIds = emptyList()
        )

        // Prepend
        addToEnd(newGroup)
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
        replace(groupId, newGroup)
    }
}