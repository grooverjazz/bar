package org.groover.bar.data.util

/**
 * Enum describing all possible BTW percentages.
 */
enum class BTWPercentage(val value: Int) {
    Geen(0),
    Laag(9),
    Hoog(21);

    // (Converts the percentage into a string)
    override fun toString(): String = "$name ($value%)"
    fun serialize() = value.toString()

    // (Converts the percentage into a Double)
    fun toDouble() = value.toDouble()

    companion object {
        // (Converts a String to a percentage)
        fun String.toBTWPercentage(): BTWPercentage {
            val value = toInt()
            return entries.find { it.value == value }!!
        }
    }
}
