package org.groover.bar.data.group

import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository
import kotlin.math.max

class GroupRepository(
    fileOpener: FileOpener,
) : Repository<Group>(
    fileOpener,
    "groups.csv",
    Group.Companion::serialize,
    Group.Companion::deserialize,
    listOf("ID", "Name", "Member IDs...")
) {
    init {
        open()
    }

    // (Adds a new group)
    fun addGroup(newGroupName: String) {
        val newGroup = Group(
            // Create new group
            id = max(generateId(), 1000000),
            name = newGroupName,
            memberIds = emptyList()
        )

        // Prepend
        prepend(newGroup)
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