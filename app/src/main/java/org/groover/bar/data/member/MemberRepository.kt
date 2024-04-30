package org.groover.bar.data.member

import android.content.Context
import org.groover.bar.util.data.Repository

class MemberRepository(
    context: Context
) : Repository<Member>(
    context,
    "members.txt",
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
}