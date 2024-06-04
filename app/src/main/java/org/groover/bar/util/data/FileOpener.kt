package org.groover.bar.util.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import java.io.File

class FileOpener(
    val context: Context,
    var relativePath: String,
) {
    // Get the current directory
    // (Android/data/org.groover.bartablet2/files)
    private val dir get() =
        context.getExternalFilesDir("")?.path + "/" +
        relativePath +
        (if (relativePath == "") "" else "/")


    fun listDirs(): List<String> {
        return File(dir)
            .walk()
            .filter(File::isDirectory)
            .map(File::getName)
            .filter { it != "files" }
            .toList()
    }

    fun read(fileName: String, dropFirst: Boolean = false): List<String> {
        // Open a file for reading
        val readFile = File(dir, fileName)

        // Read all lines if file exists, drop title row
        val lines = if (readFile.exists())
            readFile.readLines().drop(if (dropFirst) 1 else 0)
        else emptyList()

        return lines
    }

    fun write(fileName: String, data: List<String>) {
        // Create directory if it doesn't exist
        val writeDir = File(dir)
        writeDir.mkdirs()

        // Open a file for writing
        val writeFile = File(dir, fileName)
        if (!writeFile.exists()) {
            writeFile.createNewFile()
        }

        val dataStr = data.joinToString("\n");
        writeFile.writeText(dataStr)
    }
}