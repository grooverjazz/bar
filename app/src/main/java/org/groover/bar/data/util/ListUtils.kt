package org.groover.bar.data.util

/**
 * Removes the first element of a list that satisfies the predicate.
 */
fun <T> List<T>.removeFirst(predicate: (T) -> Boolean): List<T> {
    var found = false
    return this.filter {
        if (!found && predicate(it)) {
            found = true
            false
        } else true
    }
}