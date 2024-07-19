package org.groover.bar.data.customer

import androidx.compose.ui.util.fastMap
import org.groover.bar.data.util.DateUtils
import org.groover.bar.data.util.FileOpener
import org.groover.bar.data.util.Repository

class MemberRepository(
    fileOpener: FileOpener,
) : Repository<Member>(
    fileOpener,
    "members.csv",
    Member.Companion::serialize,
    Member.Companion::deserialize,
    listOf("id", "name", "birthday"),
) {
    init {
        open()
    }

    // (OVERRIDE: Loads/reloads the data of the repository)
    override fun open() {
        // Read from file, add extra members
        mutableData.clear()

        // Add extra members
        mutableData += readFile("extraMembers.csv", deserialize)
            .fastMap { it.copy(isExtra = true) }

        // Add regular members
        mutableData += readFile("members.csv", deserialize)

        // Add Hospitality if not present
        if (find(0) == null)
            addToStart(Member(0, "Hospitality", DateUtils.Y2K, true))
    }

    // (OVERRIDE: Saves the data of the repository)
    override fun save() {
        // Partition extra and regular members
        val (extraMembers, regularMembers) = data.partition { it.isExtra }

        // Save separately
        saveFile("members.csv", regularMembers, Member.Companion::serialize)
        saveFile("extraMembers.csv", extraMembers, Member.Companion::serialize)
    }
}
