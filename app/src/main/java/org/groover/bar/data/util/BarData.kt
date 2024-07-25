package org.groover.bar.data.util

/**
 * The abstract class that covers Customer (so also Member and Group), Item and Order.
 * Allows for shared functionality in Repository.
 */
abstract class BarData {
    abstract val id: Int
}