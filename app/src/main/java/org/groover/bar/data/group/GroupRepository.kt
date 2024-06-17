package org.groover.bar.data.group

import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository

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

    fun addGroup(newGroupName: String) {
        val newGroup = Group(
            id = 2000000 + generateId(),
            name = newGroupName,
            memberIds = emptyList()
        )

        data += newGroup

        save()
    }

    fun removeGroup(groupId: Int) {
        val newData = data
            .filter { it.id != groupId }

        data.clear()
        data += newData
        save()
    }

    fun changeGroup(groupId: Int, newName: String, newMemberIds: List<Int>) {
        // Create new group
        val newGroup = Group(groupId, newName, newMemberIds)

        replaceById(groupId, newGroup)
    }
}