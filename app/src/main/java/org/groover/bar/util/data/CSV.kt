package org.groover.bar.util.data

import android.annotation.SuppressLint
import androidx.compose.ui.util.fastMap
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
class CSV {
    companion object {
        fun serialize(values: List<String>): String = values
            .joinToString(";") { "\"$it\"" }

        fun serialize(vararg values: String): String = serialize(values.asList())

        fun deserialize(str: String): List<String> = str
            .split(";")
            .fastMap {it.trim('"')}


        // Timestamp methods
        private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        fun serializeTimestamp(timestamp: Date): String = timestampFormat.format(timestamp)

        fun deserializeTimestamp(str: String): Date = timestampFormat.parse(str) ?: Date()
    }
}