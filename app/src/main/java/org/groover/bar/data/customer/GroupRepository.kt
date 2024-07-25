package org.groover.bar.data.customer

import org.groover.bar.data.util.FileOpener
import org.groover.bar.data.util.Repository

/**
 * The repository that contains all groups.
 */
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
