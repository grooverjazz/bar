package org.groover.bar.util.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import java.io.File

abstract class Repository<T: BarData>(
    context: Context,
    val fileName: String,
    val serialize: (T) -> String,
    deserialize: (String) -> T,
    val titleRow: List<String>
) {
    val data: SnapshotStateList<T>

    // Get the current directory
    // (Android/data/org.groover.bartablet2/files)
    private val dir = context.getExternalFilesDir("")

    // (Initialization)
    init {
        // Open a file for reading
        val readFile = File(dir, fileName)

        // Read all lines if file exists, drop title row
        val lines = if (!readFile.exists()) {
            readFile.createNewFile()
            emptyList()
        } else readFile.readLines().drop(1)

        // Turn the values into T's
        val values = lines
            .filter { it != "" }
            .map(String::trim)
            .map(deserialize)

        // Create snapshot state list
        data = values.toMutableStateList()
    }

    // (Saves the data of the repository)
    fun save() {
        // Open a file for writing
        val writeFile = File(dir, fileName)

        // Write title row to file
//        writeFile.writeText(CSV.serialize(titleRow) + "\n")

        // Write content to file
        val data = data.joinToString("\n", transform = serialize)
        writeFile.writeText(data)
    }

    // (Finds the corresponding element)
    fun lookupById(id: Int): T? = data.firstOrNull { it.id == id }

    // (Removes the corresponding element)
    fun removeById(id: Int) {
        // Look up the element, remove it if it exists
        val element = lookupById(id)
        if (element != null)
            data -= element

        // Save
        save()
    }

    // (Finds the first available ID)
    fun generateId(): Int {
        // Get the item with the highest ID, add 1
        val maxId = (data.maxByOrNull { it.id }?.id) ?: -1
        return maxId + 1
    }
}