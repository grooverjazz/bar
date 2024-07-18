package org.groover.bar.util.data

enum class BTWPercentage(val value: Int) {
    Geen(0),
    Laag(9),
    Hoog(21);

    override fun toString(): String = "$name ($value%)"
    fun serialize() = value.toString()
    fun toDouble() = value.toDouble()

    companion object {
        fun String.toBTWPercentage(): BTWPercentage {
            val value = toInt()
            return entries.find { it.value == value }!!
        }
    }
}
