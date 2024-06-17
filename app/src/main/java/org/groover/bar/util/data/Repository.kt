package org.groover.bar.util.data

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

//    open val asdf: SnapshotStateList<T> get() = data

    // (Loads/Reloads the data of the repository)
    open fun open() {
        // Read from file
        data.clear()
        data += openFile(fileName)
    }

    protected fun openFile(f: String): List<T> {
        // Read data
        val dataStr = fileOpener.read(f, dropFirst = true)

        // Deserialize data
        return dataStr
            .map(String::trim)
            .filter { it != "" }
            .map(deserialize)
    }

    // (Saves the data of the repository)
    open fun save() {
        saveFile(data, fileName)
    }

    protected fun saveFile(list: List<T>, f: String) {
        // Serialize data
        val titleRowStr = CSV.serialize(titleRow)
        val dataStr = listOf(titleRowStr) + list.map(serialize)

        // Save data
        fileOpener.write(f, dataStr)
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
    fun replaceById(id: Int, new: T, inPlace: Boolean = true) {
        assert(new.id == id) { "Incorrect ID" }

        if (!inPlace) {
            // Remove and re-add
            data.remove(lookupById(id))
            data += new

            // Save
            save()

            return
        }

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