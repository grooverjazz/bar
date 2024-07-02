package org.groover.bar.data.group

import org.groover.bar.util.data.BarData
import org.groover.bar.util.data.CSV

data class Group (
    override val id: Int,
    val name: String,
    val memberIds: List<Int>
): BarData() {
    // (Converts the Group to a String)
    override fun toString(): String = "$name (${memberIds.size} leden)"


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