package org.groover.bar.util.data

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

class CSV {
    companion object {
        fun serialize(strs: List<String>): String = strs
            .joinToString(";") { "\"$it\"" }
        fun serialize(vararg strs: String): String = serialize(strs.asList())

        fun deserialize(str: String): List<String> = str
            .split(";")
            .map {it.trim('"')}

        // TODO: ???
        @SuppressLint("SimpleDateFormat")
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        // Date methods
        fun serializeDate(date: Date): String = formatter.format(date)
        fun deserializeDate(str: String): Date = formatter.parse(str) ?: Date()
    }
}