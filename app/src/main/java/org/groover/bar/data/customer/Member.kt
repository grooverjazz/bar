package org.groover.bar.data.customer

import org.groover.bar.data.util.CSV
import org.groover.bar.data.util.DateUtils
import java.util.Date

data class Member(
    override val id: Int,
    override val name: String,
    val birthday: Date,
    val isExtra: Boolean = false,
): Customer() {
    // (Converts the Member to a String)
    override fun toString(): String = if (isExtra) "(($name))" else name

    // (OVERRIDE: Gets a warning message of the customer)
    override fun getWarningMessage(findMember: (id: Int) -> Member?): String {
        // Check if it is Hospitality
        if (id == 0)
            return ""
        // Check if it is extra
        if (isExtra)
            return "Dit lid is tijdelijk!"
        // Check if it is a minor
        if (!DateUtils.isOlderThan18(birthday))
            return "Dit lid is minderjarig!"
        // No warning
        return ""
    }


    companion object {
        // (Serializes the user)
        fun serialize(member: Member): String {
            // Serialize ID
            val idStr = member.id.toString()

            // Return serialization
            return CSV.serialize(
                idStr,
                member.name,
                DateUtils.serializeDate(member.birthday)
            )
        }

        // (Deserializes the user)
        fun deserialize(str: String): Member {
            try {
                // Get properties
                //  (destructuring a list works up until 5 items...)
                val props = CSV.deserialize(str)
                val (idStr, name, birthdayStr) = props

                // Deserialize properties
                val id = idStr.toInt()
                val birthday = DateUtils.deserializeDate(birthdayStr)

                // Return user
                return Member(id, name, birthday)
            } catch (e: Exception) {
                throw IllegalStateException("Kan lid '$str' niet deserialiseren\n" +
                        "(normaal in de vorm 'id;name;birthday')")
            }
        }
    }
}

