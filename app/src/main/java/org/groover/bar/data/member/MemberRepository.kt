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
    listOf("ID", "Voornaam", "Tussenvoegsel", "Achternaam")
) {
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
        )

        // Add the new member
        data += newMember
        save()
    }

    fun changeMember(memberId: Int, newRoepnaam: String, newVoornaam: String, newTussenvoegsel: String, newAchternaam: String, newVerjaardag: Date) {
        // Create new member
        val newMember = Member(memberId, newRoepnaam, newVoornaam, newTussenvoegsel, newAchternaam, newVerjaardag)

        replaceById(memberId, newMember)
    }
}