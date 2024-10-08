package org.groover.bar.data.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxOfOrNull

/**
 * Class that is responsible for loading, storing, accessing, editing and saving all bar data.
 */
abstract class Repository<Element: BarData>(
    private val fileOpener: FileOpener,
    private val fileName: String,
    protected val serialize: (Element) -> String,
    protected val deserialize: (String) -> Element,
    private val titleRow: List<String>,
) {
    // Mutable, internal, list of elements
    //  (only use when you know what you're doing!)
    protected val mutableData: SnapshotStateList<Element> = arrayListOf<Element>().toMutableStateList()

    // Immutable, external, list of elements
    //  (points to 'mutableData', guarantees that the list isn't written to)
    val data: List<Element> get() = mutableData

    // (Loads/reloads the data of the repository)
    open fun open() {
        // Read from file
        mutableData.clear()
        mutableData += readFile(fileName, deserialize)
    }

    // (Reads a file into a list)
    protected fun <T> readFile(
        fileName: String,
        deserialize: (String) -> T,
    ): List<T> {
        // Read data
        val dataStr = fileOpener.read(fileName, dropFirst = true)

        // Deserialize data
        return dataStr
            .fastMap(String::trim)
            .fastFilter { it.isNotBlank() }
            .fastMap(deserialize)
    }

    // (Saves the data of the repository)
    open fun save() = saveFile(fileName, data, serialize)

    // (Writes a list into a file)
    protected fun <T> saveFile(
        fileName: String,
        data: List<T>,
        serialize: (T) -> String,
    ) {
        // Serialize data
        val titleRowStr = CSVHandler.serialize(titleRow)
        val dataStr = listOf(titleRowStr) + data.fastMap(serialize)

        // Write data
        fileOpener.write(fileName, dataStr)
    }

    // (Finds the corresponding element)
    fun find(id: Int): Element? = data.fastFirstOrNull { element -> element.id == id }

    // (Finds the index with specified ID)
    //  (crucial implementation detail: finds the first index)
    fun findIndex(id: Int): Int = data.indexOfFirst { element -> element.id == id }

    // (Removes the corresponding element)
    fun remove(id: Int) {
        // Remove the element
        mutableData.removeAt(findIndex(id))

        // Save
        save()
    }

    // (Replaces the element)
    fun replace(id: Int, new: Element) {
        // Assert indices are the same
        if (new.id != id)
            throw Exception("ID-error bij vervangen element")

        // Get index
        val index = findIndex(id)
        if (index == -1)
            throw Exception("Index-error bij vervangen element")

        // Reassign
        mutableData[index] = new

        // Save
        save()
    }

    // (Prepends an element)
    fun addToStart(element: Element) {
        // Prepend
        mutableData.add(0, element)

        // Save
        save()
    }

    // (Moves an element up or down)
    fun move(id: Int, moveUp: Boolean) {
        val index = findIndex(id)
        if (index == -1)
            throw Exception("ID-Error tijdens verplaatsen van item")

        // Swap elements if necessary
        val newIndex = index + (if (moveUp) -1 else 1)
        val tempElement = mutableData.getOrElse(newIndex) { return }
        mutableData[newIndex] = mutableData[index]
        mutableData[index] = tempElement

        // Save
        save()
    }

    // (Finds the first available ID)
    fun generateId(): Int = data.fastMaxOfOrNull { it.id }?.plus(1) ?: 0
}