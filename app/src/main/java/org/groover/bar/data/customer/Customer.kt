package org.groover.bar.data.customer

import org.groover.bar.data.util.BarData

abstract class Customer : BarData() {
    // ABSTRACT: The name of the customer
    abstract val name: String

    // (ABSTRACT: Gets a warning message of the customer)
    abstract fun getWarningMessage(findMember: (id: Int) -> Member): String
}