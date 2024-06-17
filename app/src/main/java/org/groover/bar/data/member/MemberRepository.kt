package org.groover.bar.data.member

import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository
import java.util.Date

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

    override fun open() {
        // Read from file, add extra members
        data.clear()
        data += openFile("extraMembers.csv")
            .map { it.copy(isExtra = true) }
        data += openFile(fileName)
    }

    override fun save() {
        val (extraMembers, members) = data.partition { it.isExtra }

        saveFile(members, fileName)
        saveFile(extraMembers, "extraMembers.csv")
    }

    // (Adds a temporary new member to the repository)
    fun addTempMember(tempName: String) {
        // Construct temporary member
        val newMember = Member(
            id = 1000000 + data.size,
            roepnaam = tempName,
            voornaam = "",
            tussenvoegsel = "",
            achternaam = "",
            verjaardag = Date(),
            isExtra = true,
        )

        // Add the new member
        data += newMember
        save()
    }

    fun changeMember(memberId: Int, newRoepnaam: String, newVoornaam: String, newTussenvoegsel: String, newAchternaam: String, newVerjaardag: Date, isExtra: Boolean) {
        // Create new member
        val newMember = Member(memberId, newRoepnaam, newVoornaam, newTussenvoegsel, newAchternaam, newVerjaardag, isExtra)

        replaceById(memberId, newMember, inPlace = false)

        val (extraMembers, members) = data.partition { it.isExtra }
        data.clear()
        data += extraMembers
        data += members
    }
}