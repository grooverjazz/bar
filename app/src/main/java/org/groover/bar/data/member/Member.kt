package org.groover.bar.data.member

import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.DateUtils
import java.util.Date

data class Member(
    override val id: Int,
    val roepnaam: String,
    val voornaam: String,
    val tussenvoegsel: String,
    val achternaam: String,
    val verjaardag: Date,
): BarData() {
    private val roepVoornaam = if (roepnaam == "") voornaam else roepnaam

    val fullName: String = listOf(roepVoornaam, tussenvoegsel, achternaam)
        .filter { it.isNotBlank() }
        .joinToString(" ")

    override fun toString(): String = fullName

    companion object {
        // (Serializes the user)
        fun serialize(member: Member): String {
            // Convert id to String
            val idStr = member.id.toString()

            // Return serialization
            return CSV.serialize(
                idStr,
                member.roepnaam,
                member.voornaam,
                member.tussenvoegsel,
                member.achternaam,
                DateUtils.serializeDate(member.verjaardag)
            )
        }

        // (Deserializes the user)
        fun deserialize(str: String): Member {
            // Extract from split string, convert id to Int
            val params = CSV.deserialize(str)
            val (idStr, roepnaam, voornaam, tussenvoegsel, achternaam) = params
            val verjaardagStr = params[5]

            val id = idStr.toInt()
            val verjaardag = DateUtils.deserializeDate(verjaardagStr)

            // Return user
            return Member(id, roepnaam, voornaam, tussenvoegsel, achternaam, verjaardag)
        }
    }
}

