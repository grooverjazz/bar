package org.groover.bar.util.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

abstract class Repository<Element: BarData>(
    private val fileOpener: FileOpener,
    protected val fileName: String,
    private val serialize: (Element) -> String,
    private val deserialize: (String) -> Element,
    private val titleRow: List<String>
) {
    // Mutable, internal, list of elements
    //  (only use when you know what you're doing!)
    protected val mutableData: SnapshotStateList<Element> = emptyList<Element>().toMutableStateList()

    // Immutable, external, list of elements
    //  (points to 'mutableData', guarantees that the list isn't written to)
    val data: List<Element> get() = mutableData

    // (Loads/reloads the data of the repository)
    open fun open() {
        // Read from file
        mutableData.clear()
        mutableData += openFile(fileName)
    }

    // (Opens and deserializes a file)
    protected fun openFile(f: String): List<Element> {
        // Read data
        val dataStr = fileOpener.read(f, dropFirst = true)

        // Deserialize data
        return dataStr
            .map(String::trim)
            .filterNot { it.isBlank() }
            .map(deserialize)
    }

    // (Saves the data of the repository)
    open fun save() = saveFile(data, fileName)

    // (Saves a list of elements to file)
    protected fun saveFile(list: List<Element>, f: String) {
        // Serialize data
        val titleRowStr = CSV.serialize(titleRow)
        val dataStr = listOf(titleRowStr) + list.map(serialize)

        // Save data
        fileOpener.write(f, dataStr)
    }

    // (Finds the corresponding element)
    fun find(id: Int): Element? = data.firstOrNull { element -> element.id == id }

    // (Finds the index with specified ID)
    fun findIndex(id: Int): Int = data.indexOfFirst { element -> element.id == id }

    // (Removes the corresponding element)
    fun remove(id: Int) {
        // Remove the element
        mutableData.removeIf { element -> element.id == id }

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
    fun prepend(element: Element) {
        // Prepend
        mutableData.add(0, element)

        // Save
        save()
    }

    // (Moves an element up or down)
    fun move(itemId: Int, moveUp: Boolean) {
        val index = findIndex(itemId)
        if (index == -1)
            throw Exception("ID-Error tijdens verplaatsen van item")

        // Check if movement is necessary
        if ((moveUp && index == 0) || (!moveUp && index == data.lastIndex))
            return

        // Remove and re-add the item
        val element = mutableData.removeAt(index)
        mutableData.add(index + (if (moveUp) -1 else 1), element)

        // Save
        save()
    }

    // (Finds the first available ID)
    fun generateId(): Int = data.maxByOrNull { it.id }?.id?.plus(1) ?: 0
}