package org.groover.bar.data.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

/**
 * Class responsible for opening a file in a current session folder.
 */
class FileOpener(
    private val context: Context,
    var relativePath: String,
) {
    // Get the current directory
    // (Android/data/org.groover.bar/files)
    private val dir get() =
        context.getExternalFilesDir("")?.path + "/" + relativePath +
        (if (relativePath == "") "" else "/")

    // (Lists all session directories)
    fun getSessionDirs(): List<String> {
        return File(dir)
            .walk()
            .filter(File::isDirectory)
            .map(File::getName)
            .filter { it != "files" }
            .sortedDescending()
            .toList()
    }

    // (Reads the contents of a file)
    fun read(fileName: String, dropFirst: Boolean = false): List<String> {
        // Open a file for reading
        val readFile = File(dir, fileName)

        // Read all lines if file exists, drop title row
        val lines = if (readFile.exists())
            readFile.readLines().drop(if (dropFirst) 1 else 0)
        else emptyList()

        return lines
    }

    // (Reads the contents of a file to a Map)
    fun readToMap(fileName: String, dropFirst: Boolean = false): Map<String, String> {
        val lines = read(fileName, dropFirst)
        return lines.associate { line ->
            val (key, value) = CSVHandler.deserialize(line)
            return@associate (key to value)
        }
    }

    // (Writes the data to a file)
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

    // (Writes a Map to a file)
    fun writeFromMap(fileName: String, data: Map<String, String>) {
        val lines = data.map { (key, value) -> CSVHandler.serialize(listOf(key, value)) }
        write(fileName, lines)
    }

    // (Writes an Excel workbook to a file)
    fun write(fileName: String, data: XSSFWorkbook) {
        FileOutputStream(File(dir, fileName)).use { fileOut ->
            data.write(fileOut)
        }

        File(dir, fileName)
    }

    // (Opens the specified file in an external program)
    //   (for now only works with Excel files)
    fun openExternal(fileName: String) {
        val file = File(dir, fileName)
        val fileProviderStr = "${context.packageName}.fileprovider"
        val fileUri: Uri = FileProvider.getUriForFile(context, fileProviderStr, file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/vnd.ms-excel")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}