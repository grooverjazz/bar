package org.groover.bar.data.util
import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date

/**
 * Class that handles date utility functions.
 */
@SuppressLint("SimpleDateFormat")
class DateUtils {
    companion object {
        // (Converts milliseconds from epoch to and from Dates)
        fun millisToDate(millis: Long): Date = Date.from(Instant.ofEpochMilli(millis))
        fun dateToMillis(date: Date): Long = date.time + 10_000_000 // Don't ask

        // (Checks if a birth date is more than 18 years ago)
        fun isOlderThan18(birthDate: Date): Boolean {
            // Convert Date to LocalDate
            val birthLocalDate: LocalDate = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            // Get current date
            val currentDate: LocalDate = LocalDate.now()

            // Calculate the period between birth date and current date
            val age: Period = Period.between(birthLocalDate, currentDate)

            // Check if the age is greater than or equal to 18
            return age.years >= 18
        }

        // Date methods
        private val dateFormat = SimpleDateFormat("dd MM yyyy")
        val Y2K: Date = dateFormat.parse("01 01 2000")!!

        // ((De-)serializes a Date)
        fun serializeDate(date: Date): String = dateFormat.format(date)
        fun deserializeDate(str: String): Date = removeTime(if (str == "") Date() else dateFormat.parse(str) ?: Date())

        // (Removes the time from a Date)
        private fun removeTime(date: Date): Date {
            // Convert Date to LocalDate
            val localDate: LocalDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            // Convert LocalDate back to Date with time set to midnight
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
    }
}