package org.groover.bar.util.data

fun <T> List<T>.removeFirst(predicate: (T) -> Boolean): List<T> {
    var found = false
    return this.filter {
        if (!found && predicate(it)) {
            found = true
            false
        } else {
            true
        }
    }
}