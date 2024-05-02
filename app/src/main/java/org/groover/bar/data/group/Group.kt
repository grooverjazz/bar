package org.groover.bar.data.group

import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV

data class Group (
    override val id: Int,
    val name: String,
    val memberIds: List<Int>
): BarData() {
    override fun toString(): String = "$name (${memberIds.size} leden)"

    companion object {
        // (Serializes the group)
        fun serialize(group: Group): String {
            // Return serialization
            return CSV.serialize(
                listOf(group.id.toString(), group.name)
                        + group.memberIds.map(Int::toString)
            )
        }

        // (Deserializes the group)
        fun deserialize(str: String): Group {
            // Extract from split string
            val data = CSV.deserialize(str)

            // Get name and ID, turn ID into string
            val (idStr, name) = data
            val id = idStr.toInt()

            // Get member IDs
            val memberIds = data.drop(2).map(String::toInt)

            // Return group
            return Group(id, name, memberIds)
        }
    }
}