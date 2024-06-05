package org.groover.bar.data.member

import org.groover.bar.util.data.FileOpener
import org.groover.bar.util.data.Repository

class MemberRepository(
    fileOpener: FileOpener,
) : Repository<Member>(
    fileOpener,
    "members.csv",
    Member.Companion::serialize,
    Member.Companion::deserialize,
    listOf("ID", "Voornaam", "Tussenvoegsel", "Achternaam")
) {
    // (Adds a temporary new member to the repository)
    fun addTempMember(tempName: String) {
        // Construct temporary member
        val newMember = Member(
            id = 1000000 + data.size,
            voornaam = tempName,
            tussenvoegsel = "",
            achternaam = ""
        )

        // Add the new member
        data += newMember
        save()
    }

    fun changeMember(memberId: Int, newVoornaam: String, newTussenvoegsel: String, newAchternaam: String) {
        // Create new member
        val newMember = Member(memberId, newVoornaam, newTussenvoegsel, newAchternaam)

        replaceById(memberId, newMember)
    }
}