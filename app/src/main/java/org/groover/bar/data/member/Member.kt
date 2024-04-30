package org.groover.bar.data.member

import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV

data class Member(
    override val id: Int,
    val voornaam: String,
    val tussenvoegsel: String,
    val achternaam: String
): BarData() {

    val fullName: String = listOf(voornaam, tussenvoegsel, achternaam)
        .filter { it.isNotBlank() }
        .joinToString(" ")

    override fun toString(): String = "$id: $fullName"

    companion object {
        // (Serializes the user)
        fun serialize(member: Member): String {
            // Convert id to String
            val idStr = member.id.toString()

            // Return serialization
            return CSV.serialize(
                idStr,
                member.voornaam,
                member.tussenvoegsel,
                member.achternaam
            )
        }

        // (Deserializes the user)
        fun deserialize(str: String): Member {
            // Extract from split string, convert id to Int
            val (idStr, voornaam, tussenvoegsel, achternaam) = CSV.deserialize(str)
            val id = idStr.toInt()

            // Return user
            return Member(id, voornaam, tussenvoegsel, achternaam)
        }
    }
}