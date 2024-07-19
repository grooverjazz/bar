package org.groover.bar.data.util

import android.content.Context
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class FileOpener(
    private val context: Context,
    var relativePath: String,
) {
    // Get the current directory
    // (Android/data/org.groover.bar/files)
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

        val dataStr = data.joinToString("\n")
        writeFile.writeText(dataStr)
    }

    fun write(fileName: String, data: XSSFWorkbook) {
        FileOutputStream(File(dir, fileName)).use { fileOut ->
            data.write(fileOut)
        }
    }
}