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
    val isExtra: Boolean = false,
): BarData() {
    // Roepnaam if present, else voornaam
    val roepVoornaam = if (roepnaam == "") voornaam else roepnaam

    // Full name
    val fullName: String = listOf(roepVoornaam, tussenvoegsel, achternaam)
        .filter { it.isNotBlank() }
        .joinToString(" ")

    // (Converts the Member to a String)
    override fun toString(): String = if (isExtra) "(($fullName))" else fullName


    companion object {
        // (Serializes the user)
        fun serialize(member: Member): String {
            // Serialize ID
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
            // Get properties
            //  (destructuring a list works up until 5 items...)
            val props = CSV.deserialize(str)
            val (idStr, roepnaam, voornaam, tussenvoegsel, achternaam) = props
            val verjaardagStr = props[5]

            // Deserialize properties
            val id = idStr.toInt()
            val verjaardag = DateUtils.deserializeDate(verjaardagStr)

            // Return user
            return Member(id, roepnaam, voornaam, tussenvoegsel, achternaam, verjaardag)
        }
    }
}

