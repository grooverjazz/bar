package org.groover.bar.util.data
import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date

class DateUtils {
    @SuppressLint("SimpleDateFormat")
    companion object {


        fun millisToDate(millis: Long): Date = Date.from(Instant.ofEpochMilli(millis))
        fun dateToMillis(date: Date): Long = date.time + 10_000_000 // Don't ask

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

        fun equals(date1: Date, date2: Date): Boolean {
            // Convert Dates to LocalDates
            val date1Local: LocalDate = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val date2Local: LocalDate = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            // Calculate period
            val period: Period = Period.between(date1Local, date2Local)

            Log.i("asdf", period.days.toString())

            // Check if the period is 0 days
            return period.days == 0
        }

        // Date methods
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        val Y2K: Date = dateFormat.parse("01/01/2000")!!

        fun serializeDate(date: Date): String = dateFormat.format(date)

        fun deserializeDate(str: String): Date = removeTime(if (str == "") Date() else dateFormat.parse(str) ?: Date())

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