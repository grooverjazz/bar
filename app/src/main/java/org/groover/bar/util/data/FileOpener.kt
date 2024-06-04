package org.groover.bar.util.data

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import java.io.File

class FileOpener(
    val context: Context,
    relativePath: String,
) {
    // Get the current directory
    // (Android/data/org.groover.bartablet2/files)
    private val dir = context.getExternalFilesDir("")

    fun read(fileName: String): List<String> {
        // Open a file for reading
        val readFile = File(dir, fileName)

        // Read all lines if file exists, drop title row
        val lines = if (readFile.exists())
            readFile.readLines().drop(1)
        else emptyList()

        return lines
    }

    fun write(fileName: String, data: List<String>) {
        // Open a file for writing
        val writeFile = File(dir, fileName)
        if (!writeFile.exists()) {
            writeFile.createNewFile()
        }

        val dataStr = data.joinToString("\n");
        writeFile.writeText(dataStr)
    }
}