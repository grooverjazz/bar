package org.groover.bar.data.member

import org.groover.bar.util.data.DateUtils
import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository
import java.util.Date
import kotlin.math.min

class MemberRepository(
    fileOpener: FileOpener,
) : Repository<Member>(
    fileOpener,
    "members.csv",
    Member.Companion::serialize,
    Member.Companion::deserialize,
    listOf("ID", "Roepnaam", "Voornaam", "Tussenvoegsel", "Achternaam", "Verjaardag")
) {
    init {
        open()
    }

    // (OVERRIDE: Loads/reloads the data of the repository)
    override fun open() {
        // Read from file, add extra members
        mutableData.clear()

        // Add extra members
        mutableData += openFile("extraMembers.csv")
            .map { it.copy(isExtra = true) }

        // Add regular members
        mutableData += openFile(fileName)

        // Add Hospitality if not present
        if (find(0) == null)
            prepend(Member(0, "Hospitality", "", "", "", DateUtils.Y2K, true))
    }

    // (OVERRIDE: Saves the data of the repository)
    override fun save() {
        // Partition extra members
        val (extraMembers, members) = data.partition { it.isExtra }

        // Save separately
        saveFile(members, fileName)
        saveFile(extraMembers, "extraMembers.csv")
    }

    // (Adds an extra member)
    fun addExtraMember(tempName: String) {
        // Create extra member
        val extraMember = Member(
            id = generateExtraId(),
            roepnaam = tempName,
            voornaam = "",
            tussenvoegsel = "",
            achternaam = "",
            verjaardag = DateUtils.Y2K,
            isExtra = true,
        )

        // Prepend
        prepend(extraMember)
    }

    // (Changes a member)
    fun changeMember(
        memberId: Int,
        newRoepnaam: String,
        newVoornaam: String,
        newTussenvoegsel: String,
        newAchternaam: String,
        newVerjaardag: Date,
        isExtra: Boolean
    ) {
        // Create new member
        val newMember = Member(
            memberId,
            newRoepnaam,
            newVoornaam,
            newTussenvoegsel,
            newAchternaam,
            newVerjaardag,
            isExtra
        )

        // Replace
        replace(memberId, newMember)
    }

    fun generateExtraId(): Int = min(super.generateId(), 1000000)
}