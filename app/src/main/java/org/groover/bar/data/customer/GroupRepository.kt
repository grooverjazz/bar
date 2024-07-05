package org.groover.bar.data.customer

import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository

class GroupRepository(
    fileOpener: FileOpener,
) : Repository<Group>(
    fileOpener,
    "groups.csv",
    Group.Companion::serialize,
    Group.Companion::deserialize,
    listOf("id", "name", "memberIds..."),
) {
    init {
        open()
    }
}
