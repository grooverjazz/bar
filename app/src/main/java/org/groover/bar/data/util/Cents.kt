package org.groover.bar.data.util

/**
 * Class describing currency values (a wrapper for Int with extra functionality).
 */
class Cents(
    val amount: Int
) {
    // (Converts the Cents into a String; also available with Euro sign)
    override fun toString(): String = "${amount / 100},${"%02d".format(amount % 100)}"
    fun toStringWithEuro(): String = "€$this"

    // (Turns a Cents into a double)
    fun toDouble(): Double = amount.toDouble() / 100

    // Operator functions
    operator fun plus(other: Cents) = Cents(amount + other.amount)
    operator fun div(other: Int) = Cents(amount / other)
    operator fun rem(other: Int) = Cents(amount % other)
    operator fun times(other: Int) = Cents(other * amount)

    companion object {
        // (Returns the sum of a list of Cents)
        fun Iterable<Cents>.sum(): Cents {
            var res = 0
            for (cent in this) res += cent.amount
            return Cents(res)
        }

        // (Turns a String into a Cents)
        fun String.toCents(): Cents {
            // Format string
            val formattedStr = "0" + this.replace(',','.').replace(".-",".00") + ".00"

            // Split along point
            val (eurosStr, centsStr) = formattedStr.split('.')
            val correctCentsStr = if (centsStr.length == 1) centsStr + "0" else centsStr

            // Construct Cents
            return Cents(eurosStr.toInt() * 100 + correctCentsStr.take(2).toInt())
        }
    }
}