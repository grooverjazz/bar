package org.groover.bar.util.data

class Cents(
    val amount: Int
) {
    override fun toString(): String = "${amount / 100},${"%02d".format(amount % 100)}"
    fun toStringWithEuro(): String = "€$this"

    fun toInt() = amount

    operator fun plus(other: Cents) = Cents(amount + other.amount)
    operator fun div(other: Int) = Cents(amount / other)
    operator fun rem(other: Int) = Cents(amount % other)

    operator fun times(other: Int) = Cents(other * amount)

    companion object {
        fun Iterable<Cents>.sum(): Cents {
            var res = 0
            for (cent in this) res += cent.amount
            return Cents(res)
        }

        fun String.toCents(): Cents {
            // Format string
            val formattedStr = "0" + this.replace(',','.').replace(".-",".00") + ".00"

            // Split along point
            val (eurosStr, centsStr) = formattedStr.split('.')
            val correctCentsStr = if (centsStr.length == 1) centsStr + "0" else centsStr

            // Construct Cents
            return Cents(eurosStr.toInt() * 100 + correctCentsStr.take(2).toInt())
        }

        fun Cents.toDouble(): Double = amount.toDouble() / 100

        fun split(cents: Cents, n: Int): List<Cents> {
            val baseAmount = cents / n
            val centsToSplit = (cents % n).toInt()

            return (
                List(centsToSplit) { baseAmount + Cents(1) } +
                List(n - centsToSplit) { baseAmount }
            ).shuffled()
        }
    }
}