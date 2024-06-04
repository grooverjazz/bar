package org.groover.bar.util.data

class Cents(
    val amount: Int
) {
    val stringWithoutEuro = "${amount / 100},${"%02d".format(amount % 100)}"
    val stringWithEuro = "€" + stringWithoutEuro

    fun toInt() = amount

    operator fun plus(other: Cents) = Cents(amount + other.amount)
    operator fun div(other: Int) = Cents(amount / other)
    operator fun rem(other: Int) = Cents(amount % other)

    companion object {
        fun split(cents: Cents, n: Int): List<Cents> {
            val baseAmount = cents / n
            val centsToSplit = (cents % n).toInt()

            return (
                    List(centsToSplit) { baseAmount + Cents(1) } +
                            List(n - centsToSplit) { baseAmount }
                    ).shuffled()
        }

        fun fromString(str: String): Cents {
            val formattedStr = "0" + str.replace(",-",",00") + ",00"

            val (eurosStr, centsStr) = formattedStr.split(',')
            return Cents(100 * eurosStr.toInt() + centsStr.take(2).toInt())
        }
    }
}