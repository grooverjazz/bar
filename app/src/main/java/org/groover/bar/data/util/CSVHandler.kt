package org.groover.bar.data.util

import android.annotation.SuppressLint
import androidx.compose.ui.util.fastMap
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Class that is responsible for CSV (de-)serialization.
 *   (not up to official CSV specification, but works well enough)
 */
@SuppressLint("SimpleDateFormat")
class CSVHandler {
    companion object {
        // (Serializes a list of Strings into CSV)
        fun serialize(values: List<String>): String = values
            .joinToString(";") { "\"$it\"" }

        // (Serializes a varargs of Strings into CSV)
        fun serialize(vararg values: String): String = serialize(values.asList())

        // (Deserializes a CSV String)
        fun deserialize(str: String): List<String> = str
            .split(";")
            .fastMap {it.trim('"')}

        // ((De-)serializes a timestamp)
        private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        fun serializeTimestamp(timestamp: Date): String = timestampFormat.format(timestamp)
        fun deserializeTimestamp(str: String): Date = timestampFormat.parse(str) ?: Date()
    }
}