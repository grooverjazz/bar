package org.groover.bar.util.data

import android.content.Context
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
        val lines = if (readFile.exists())
            readFile.readLines().drop(1)
        else emptyList()

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
        if (!writeFile.exists()) {
            writeFile.createNewFile()
        }

        // Get title row string
        val titleRowStr = CSV.serialize(titleRow)

        // Get data string
        val dataStr = data
            .joinToString("\n", transform = serialize)

        // Write content to file
        val data = titleRowStr + "\n" + dataStr
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

    // (Replaces the element)
    fun replaceById(id: Int, new: T) {
        assert(new.id == id) { "Incorrect ID" }

        // Get all items before and after the specified item
        val before = data.takeWhile { it.id != id }
        val after = data.takeLastWhile { it.id != id }

        val newData = before + listOf(new) + after

        // Reassign data
        data.clear()
        data += newData

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