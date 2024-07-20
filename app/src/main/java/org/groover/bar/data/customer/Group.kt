package org.groover.bar.data.customer

import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastMap
import org.groover.bar.data.util.CSV
import org.groover.bar.data.util.DateUtils

data class Group (
    override val id: Int,
    override val name: String,
    val memberIds: List<Int>,
): Customer() {
    // (Converts the Group to a String)
    override fun toString(): String = "$name (${memberIds.size} leden)"

    // (OVERRIDE: Gets a warning message of the customer)
    override fun getWarningMessage(findMember: (id: Int) -> Member?): String {
        // Check if any of its members is extra
        if (memberIds.fastAny { findMember(it)!!.isExtra })
            return "Deze groep bevat tijdelijke leden!"
        // Check if any of its members is a minor
        if (memberIds.fastAny { !DateUtils.isOlderThan18(findMember(it)!!.birthday) })
            return "Deze groep bevat minderjarigen!"
        // No warning
        return ""
    }


    companion object {
        // (Serializes the group)
        fun serialize(group: Group): String {
            // Return serialization
            return CSV.serialize(
                listOf(
                    group.id.toString(),
                    group.name,
                ) + group.memberIds.fastMap(Int::toString)
            )
        }

        // (Deserializes the group)
        fun deserialize(str: String): Group {
            try {
                // Get properties
                val props = CSV.deserialize(str)
                val (idStr, name) = props
                val memberIdStrs = props.drop(2)

                // Deserialize properties
                val id = idStr.toInt()
                val memberIds = memberIdStrs
                    .fastMap(String::toInt)

                // Return group
                return Group(id, name, memberIds)
            } catch (e: Exception) {
                throw IllegalStateException("Kan groep '$str' niet deserialiseren\n" +
                        "(normaal in de vorm 'id;name;...memberIds')")
            }
        }
    }
}