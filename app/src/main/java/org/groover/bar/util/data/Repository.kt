package org.groover.bar.util.data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

abstract class Repository<T: BarData>(
    private val fileOpener: FileOpener,
    val fileName: String,
    private val serialize: (T) -> String,
    private val deserialize: (String) -> T,
    val titleRow: List<String>
) {
    val data: SnapshotStateList<T> = emptyList<T>().toMutableStateList()

    init {
        open()
    }

    // (Loads/Reloads the data of the repository)
    fun open() {
        // Read data
        val dataStr = fileOpener.read(fileName, dropFirst = true)

        // Deserialize data
        data.clear()
        data += dataStr
            .map(String::trim)
            .filter { it != "" }
            .map(deserialize)
            .toMutableStateList()
    }

    // (Saves the data of the repository)
    fun save() {
        // Serialize data
        val titleRowStr = CSV.serialize(titleRow)
        val dataStr = listOf(titleRowStr) + data.map(serialize)

        // Save data
        fileOpener.write(fileName, dataStr)
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