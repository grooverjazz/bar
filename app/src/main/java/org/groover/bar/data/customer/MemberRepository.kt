package org.groover.bar.data.customer

import androidx.compose.ui.util.fastMap
import org.groover.bar.data.item.Item
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.sum
import org.groover.bar.util.data.DateUtils
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository

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
