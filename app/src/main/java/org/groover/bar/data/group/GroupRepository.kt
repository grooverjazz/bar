package org.groover.bar.data.group

import android.content.Context
import org.groover.bar.util.data.Repository

class GroupRepository(
    context: Context
) : Repository<Group>(context,
    "groups.csv",
    Group.Companion::serialize,
    Group.Companion::deserialize,
    listOf("ID", "Name", "Member IDs...")
) {
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
}