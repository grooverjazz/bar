package org.groover.bar.data.customer

import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.DateUtils

data class Group (
    override val id: Int,
    override val name: String,
    val memberIds: List<Int>
): Customer() {
    // (Converts the Group to a String)
    override fun toString(): String = "$name (${memberIds.size} leden)"

    // (OVERRIDE: Gets a warning message of the customer)
    override fun getWarningMessage(findMember: (id: Int) -> Member?): String {
        // Check if any of its members is extra
        if (memberIds.any { findMember(it)!!.isExtra })
            return "Deze groep bevat tijdelijke leden!"
        // Check if any of its members is a minor
        if (memberIds.any { !DateUtils.isOlderThan18(findMember(it)!!.birthday) })
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
                    group.name
                ) + group.memberIds.map(Int::toString)
            )
        }

        // (Deserializes the group)
        fun deserialize(str: String): Group {
            // Get properties
            val props = CSV.deserialize(str)
            val (idStr, name) = props
            val memberIdStrs = props.drop(2)

            // Deserialize properties
            val id = idStr.toInt()
            val memberIds = memberIdStrs
                .map(String::toInt)

            // Return group
            return Group(id, name, memberIds)
        }
    }
}