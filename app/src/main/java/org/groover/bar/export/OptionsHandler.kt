package org.groover.bar.export

import android.content.Context
import org.groover.bar.data.util.FileOpener

class OptionsHandler(
    context: Context
) {
    private val fileOpener = FileOpener(context, "")
    val fileName = "options.csv"

    var sessionName = ""
    var beheerPassword = ""

    init {
        open()
    }

    private fun open() {
        // Load data
        val dataDict = fileOpener.readToMap(fileName)

        // Deserialize
        sessionName = dataDict.getOrDefault("sessionName", "__default")
        beheerPassword = dataDict.getOrDefault("beheerPassword", "")
    }

    fun changeSession(newSessionName: String) {
        // Change session name
        sessionName = newSessionName

        save()
    }

    fun save() {
        // Serialize
        val dataStr = mapOf(
            "sessionName" to sessionName,
            "beheerPassword" to beheerPassword,
        )

        // Save data
        fileOpener.writeFromMap(fileName, dataStr)
    }

    fun getAllSessions(): List<String> = fileOpener.listDirs().sortedDescending()
}